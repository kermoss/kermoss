package io.kermoss.cmd.app;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.ArgumentMatchers.nullable;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.event.InboundCommandPrepared;
import io.kermoss.cmd.domain.event.InboundCommandStarted;
import io.kermoss.cmd.domain.event.OutboundCommandPrepared;
import io.kermoss.cmd.domain.event.OutboundCommandStarted;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.CommandUnmarchelingException;
import io.kermoss.cmd.exception.DecoderException;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.infra.KermossTxLogger;
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommandOrchestratorImplTest {

    @Mock
    private CommandRepository mockCommandRepository;
    @Mock
    private ApplicationEventPublisher mockPublisher;
    @Mock
    private BubbleCache mockBubbleCache;
    @Mock
    private ObjectMapper mockMapper;
    @Mock
    private KermossTxLogger txLogger;
    @Mock
    private Environment mockEnvironment;
    private CommandOrchestratorImpl commandOrchestratorImplUnderTest;
    private CommandMeta mockCommandMeta;

    final private String PAYLOAD = "This is a payload";

    @BeforeEach
    public void setUp() {
        //initMocks(this);
        commandOrchestratorImplUnderTest = new CommandOrchestratorImpl(mockCommandRepository, mockPublisher, mockBubbleCache, mockMapper, mockEnvironment, txLogger);
        mockCommandMeta = mock(CommandMeta.class);
        when(mockCommandRepository.exists(anyString())).thenReturn(false);

    }

    @Test
    public void testReceiveWhenCommandExistInDb() {
        // Setup
        final InboundCommand command = mock(InboundCommand.class);
        when(mockCommandRepository.save(any(AbstractCommand.class))).thenReturn(command);
        when(command.buildMeta()).thenReturn(mockCommandMeta);

        // Run the test
        commandOrchestratorImplUnderTest.receive(command);

        // Verify the results
        verify(mockCommandRepository).save(same(command));
    }

    @Test
    public void testReceiveWhenCommandNotExistInDb() {
        // Setup
        final InboundCommand command = mock(InboundCommand.class);
        when(command.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockCommandRepository.exists(anyString())).thenReturn(true);
        when(mockCommandRepository.findOne(anyString())).thenReturn(command);
        when(command.buildMeta()).thenReturn(mockCommandMeta);
        // Run the test
        commandOrchestratorImplUnderTest.receive(command);

        // Verify the results
        verify(mockCommandRepository).findOne(command.getId());

    }

    @Test
    public void testReceiveInboundCommand() {
        // Setup
        final InboundCommand command = mock(InboundCommand.class);
        when(command.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockCommandRepository.exists(anyString())).thenReturn(true);
        when(mockCommandRepository.findOne(anyString())).thenReturn(command);
        when(command.buildMeta()).thenReturn(mockCommandMeta);
        // Run the test
        commandOrchestratorImplUnderTest.receive(command);

        // Verify the results
        verify(mockPublisher).publishEvent(any(InboundCommandStarted.class));
    }

    @Test
    public void testReceiveOutboundCommand() {
        // Setup
        final OutboundCommand command = mock(OutboundCommand.class);
        when(command.getId()).thenReturn(UUID.randomUUID().toString());
        when(mockCommandRepository.exists(anyString())).thenReturn(true);
        when(mockCommandRepository.findOne(anyString())).thenReturn(command);
        when(command.buildMeta()).thenReturn(mockCommandMeta);
        when(mockEnvironment.getProperty(same("kermoss.service-name"), nullable(String.class))).thenReturn("source");

        // Run the test
        commandOrchestratorImplUnderTest.receive(command);

        // Verify the results
        verify(mockPublisher).publishEvent(any(OutboundCommandStarted.class));
        verify(command).setSource("source");

    }

    @Test
    public void testReceiveBaseTransactionCommand() throws JsonProcessingException {
        // Setup
        final BaseTransactionCommand command = mock(BaseTransactionCommand.class);
        when(command.getId()).thenReturn(UUID.randomUUID().toString());
        final AbstractCommand abstractCommand = mock(AbstractCommand.class);
        ArgumentCaptor<OutboundCommand> commandArgumentCaptor = ArgumentCaptor.forClass(OutboundCommand.class);
        BubbleMessage message = mock(BubbleMessage.class);
        when(mockBubbleCache.getBubble(anyString())).thenReturn(Optional.of(message));
        when(mockMapper.writeValueAsString(nullable(String.class))).thenReturn(PAYLOAD);
        when(abstractCommand.buildMeta()).thenReturn(mockCommandMeta);
        when(mockCommandRepository.save(any(AbstractCommand.class))).thenReturn(abstractCommand);
        when(mockEnvironment.getProperty(same("kermoss.service-name"), nullable(String.class))).thenReturn("source");

        // Run the test
        commandOrchestratorImplUnderTest.receive(command);

        // Verify the results
        verify(mockCommandRepository).save(commandArgumentCaptor.capture());
        OutboundCommand mappedCommand = commandArgumentCaptor.getValue();
        verify(mockPublisher).publishEvent(any(OutboundCommandStarted.class));
        assertEquals(mappedCommand.getPayload(), PAYLOAD);
        assertEquals(mappedCommand.getSource(), "source");
        verify(mockPublisher).publishEvent(any(OutboundCommandStarted.class));

    }

    @Test
    public void testPrepareBaseTransactionCommand() throws JsonProcessingException {
        // Setup
        final BaseTransactionCommand command = mock(BaseTransactionCommand.class);
        when(command.getId()).thenReturn(UUID.randomUUID().toString());
        final AbstractCommand abstractCommand = mock(AbstractCommand.class);
        ArgumentCaptor<OutboundCommand> commandArgumentCaptor = ArgumentCaptor.forClass(OutboundCommand.class);
        BubbleMessage message = mock(BubbleMessage.class);
        when(mockBubbleCache.getBubble(nullable(String.class))).thenReturn(Optional.of(message));
        when(mockMapper.writeValueAsString(nullable(String.class))).thenReturn(PAYLOAD);
        when(abstractCommand.buildMeta()).thenReturn(mockCommandMeta);
        when(mockCommandRepository.save(any(AbstractCommand.class))).thenReturn(abstractCommand);
        when(mockEnvironment.getProperty(same("kermoss.service-name"), nullable(String.class))).thenReturn("source");

        // Run the test
        commandOrchestratorImplUnderTest.prepare(command);

        // Verify the results
        verify(mockCommandRepository).save(commandArgumentCaptor.capture());
        OutboundCommand mappedCommand = commandArgumentCaptor.getValue();
        verify(mockPublisher).publishEvent(any(OutboundCommandPrepared.class));
        assertEquals(mappedCommand.getPayload(), PAYLOAD);
        assertEquals(mappedCommand.getSource(), "source");
        assertEquals(mappedCommand.getStatus(), OutboundCommand.Status.PREPARED);
    }

    @Test
    public void testPrepareInboundCommand() {
        // Setup
        final InboundCommand command = mock(InboundCommand.class);
        ArgumentCaptor<InboundCommand> commandArgumentCaptor = ArgumentCaptor.forClass(InboundCommand.class);
        BubbleMessage message = mock(BubbleMessage.class);
        when(mockBubbleCache.getBubble(anyString())).thenReturn(Optional.of(message));
        when(command.buildMeta()).thenReturn(mockCommandMeta);
        when(mockCommandRepository.save(any(AbstractCommand.class))).thenReturn(command);
        // Apply set chnegs and get actual value
        doCallRealMethod().when(command).setStatus(InboundCommand.Status.PREPARED);
        doCallRealMethod().when(command).getStatus();

        // Run the test
        commandOrchestratorImplUnderTest.prepare(command);

        assertEquals(command.getStatus(), InboundCommand.Status.PREPARED);
        verify(mockPublisher).publishEvent(any(InboundCommandPrepared.class));
    }

    @Test
    public void testIsInboundCommandExistWhenCommandInDB() {
        // Setup
        final String refId = "123";
        final InboundCommand command = mock(InboundCommand.class);
        when(mockCommandRepository.findByRefId(refId)).thenReturn(Optional.of(command));

        // Run the test
        final boolean result = commandOrchestratorImplUnderTest.isInboundCommandExist(refId);

        // Verify the results
        assertEquals(true, result);
    }

    @Test
    public void testIsInboundCommandExistWhenCommandNotInDB() {
        // Setup
        final String refId = "123";
        when(mockCommandRepository.findByRefId(refId)).thenReturn(Optional.empty());

        // Run the test
        final boolean result = commandOrchestratorImplUnderTest.isInboundCommandExist(refId);

        // Verify the results
        assertEquals(false, result);
    }

    @Test
    public void testIsInboundCommandWithStatusExistWhenCommandInDB() {
        // Setup
        final String refId = "123";
        final InboundCommand.Status status = InboundCommand.Status.STARTED;
        final InboundCommand command = mock(InboundCommand.class);
        when(mockCommandRepository.findByIdAndStatus(refId, status)).thenReturn(Optional.of(command));

        // Run the test
        final boolean result = commandOrchestratorImplUnderTest.isInboundCommandWithStatusExist(refId, status);

        // Verify the results
        assertEquals(true, result);
    }

    @Test
    public void testIsInboundCommandWithStatusExistWhenCommandNotInDB() {
        // Setup
        final String refId = "123";
        final InboundCommand.Status status = InboundCommand.Status.STARTED;
        when(mockCommandRepository.findByIdAndStatus(refId, status)).thenReturn(Optional.empty());

        // Run the test
        final boolean result = commandOrchestratorImplUnderTest.isInboundCommandWithStatusExist(refId, status);

        // Verify the results
        assertEquals(false, result);
    }

    @Test
    public void testRetreiveWhenCommandExistInDbAndBubbleMessage() throws IOException {
        // Setup
        final String eventId = "1234";
        final TestData someData = new TestData("test user", "20");
        final AbstractCommand command = mock(AbstractCommand.class);
        String payload = "{\"name\":\"test user\", \"age\":\"20\"}";
        BubbleMessage message = mock(BubbleMessage.class);

        when(command.getPayload()).thenReturn(payload);
        when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.of(message));
        when(mockCommandRepository.findOne(nullable(String.class))).thenReturn(command);
        when(mockMapper.readValue(payload, TestData.class)).thenReturn(someData);

        // Run the test
        final Optional<TestData> result = commandOrchestratorImplUnderTest.retreive(eventId, TestData.class);

        // Verify the results
        assertEquals( Optional.of(someData), result);
    }

    @Test
    public void testRetreiveWhenCommandNotExistInBubbleMessage() throws IOException {
        // Setup
        final String eventId = "1234";
        final TestData someData = new TestData("test user", "20");
        final AbstractCommand command = mock(AbstractCommand.class);
        String payload = "{\"name\":\"test user\", \"age\":\"20\"}";
        BubbleMessage message = mock(BubbleMessage.class);

        when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.empty());

        // Run the test
        final Optional<TestData> result = commandOrchestratorImplUnderTest.retreive(eventId, TestData.class);

        // Verify the results
        assertEquals( Optional.empty(), result);
    }

    @Test
    public void testRetreiveWhenCommandNotExistInDb() throws IOException {
        // Setup
        final String eventId = "1234";
        BubbleMessage message = mock(BubbleMessage.class);

        when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.of(message));
        when(mockCommandRepository.findOne(anyString())).thenReturn(null);

        // Run the test
       assertThrows(CommandUnmarchelingException.class, ()->commandOrchestratorImplUnderTest.retreive(eventId, TestData.class));
    }

    @Test
    public void testRetreiveWhenJAcksonFireException() throws IOException {
        // Setup
        final String eventId = "1234";
        final AbstractCommand command = mock(AbstractCommand.class);
        BubbleMessage message = mock(BubbleMessage.class);

        when(command.getPayload()).thenReturn("");
        when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.of(message));
        when(mockCommandRepository.findOne(anyString())).thenReturn(command);
        when(mockMapper.readValue("", TestData.class)).thenThrow(JsonProcessingException.class);

        // Run the test
        assertThrows(CommandUnmarchelingException.class, ()->commandOrchestratorImplUnderTest.retreive(eventId, TestData.class));
    }

    @Test
    public void testCommandMapper() throws JsonProcessingException {
        // Setup
        final BaseTransactionCommand baseTransactionCommand = mock(BaseTransactionCommand.class);
        final BubbleMessage bubbleMessage = mock(BubbleMessage.class);
        final String payload= "somepayload";

        when(mockBubbleCache.getBubble(baseTransactionCommand.getId())).thenReturn(Optional.of(bubbleMessage));
        when(mockMapper.writeValueAsString(baseTransactionCommand.getPayload())).thenReturn(payload);

        // Run the test
        final OutboundCommand result = commandOrchestratorImplUnderTest.commandMapper(baseTransactionCommand);

        // Verify the results
        assertEquals(payload, result.getPayload());
    }

    @Test
    public void testCommandMapperWhenException() throws JsonProcessingException {
        // Setup
        final BaseTransactionCommand baseTransactionCommand = mock(BaseTransactionCommand.class);
        final BubbleMessage bubbleMessage = mock(BubbleMessage.class);
        final JsonProcessingException exception = mock(JsonProcessingException.class);

        when(mockBubbleCache.getBubble(baseTransactionCommand.getId())).thenReturn(Optional.of(bubbleMessage));
        when(mockMapper.writeValueAsString(baseTransactionCommand.getPayload())).thenThrow(exception);

        // Run the test
      assertThrows(DecoderException.class,()-> commandOrchestratorImplUnderTest.commandMapper(baseTransactionCommand));
    }

    class TestData{
        String name;
        String age;

        public TestData(String name, String age) {
            this.name = name;
            this.age = age;
        }

        public TestData() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
}

