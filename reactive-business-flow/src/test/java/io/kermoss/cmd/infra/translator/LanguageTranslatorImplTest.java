package io.kermoss.cmd.infra.translator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.event.InboundCommandPrepared;
import io.kermoss.cmd.domain.event.InboundCommandStarted;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.CommandNotFoundException;
import io.kermoss.cmd.exception.DecoderNotFoundException;
import io.kermoss.cmd.infra.translator.BaseDecoder;
import io.kermoss.cmd.infra.translator.LanguageTranslatorImpl;
import io.kermoss.domain.DecoderRegistry;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.infra.KermossTxLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class LanguageTranslatorImplTest {

    @Mock
    private CommandRepository mockCommandRepository;
    @Mock
    private ApplicationEventPublisher mockPublisher;
    @Mock
    private BubbleCache mockBubbleCache;
    @Mock
    private DecoderRegistry mockDecoderRegistry;
    @Mock
    private KermossTxLogger txLogger;

    private LanguageTranslatorImpl languageTranslatorImplUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        languageTranslatorImplUnderTest = new LanguageTranslatorImpl(mockCommandRepository, mockPublisher, mockBubbleCache, mockDecoderRegistry, txLogger);
    }

    @Test
    public void testRegisterDecoderWhenDecoderExist() {
        // Setup
        final String subject = "subject";
        final BaseDecoder baseDecoder = mock(BaseDecoder.class);
        when(mockDecoderRegistry.containsKey(subject)).thenReturn(true);

        // Run the test
        languageTranslatorImplUnderTest.registerDecoder(subject, baseDecoder);

        // Verify the results
        verify(mockDecoderRegistry, never()).put(anyString(), any(BaseDecoder.class));

    }

    @Test
    public void testRegisterDecoderWhenDecoderNotExist() {
        // Setup
        final String subject = "subject";
        final BaseDecoder baseDecoder = mock(BaseDecoder.class);
        when(mockDecoderRegistry.containsKey(subject)).thenReturn(false);

        // Run the test
        languageTranslatorImplUnderTest.registerDecoder(subject, baseDecoder);

        // Verify the results
        verify(mockDecoderRegistry).put(anyString(), any(BaseDecoder.class));

    }

    @Test(expected = DecoderNotFoundException.class)
    public void testOnCommandStartedWhenDecoderNotRegistred() {
        // Setup
        final InboundCommandStarted commandStarted = mock(InboundCommandStarted.class);
        final String subject = "subject";
        final CommandMeta meta = mock(CommandMeta.class);
        when(mockDecoderRegistry.containsKey(subject)).thenReturn(false);
        when(commandStarted.getMeta()).thenReturn(meta);

        // Run the test
        languageTranslatorImplUnderTest.onCommandStarted(commandStarted);

        // Verify the results
        verify(mockBubbleCache, never()).addBubble(anyString(), any(BubbleMessage.class));
        verify(mockPublisher, never()).publishEvent(any(BaseTransactionEvent.class));
    }

    @Test(expected = CommandNotFoundException.class)
    public void testOnCommandStartedWhenDecodedRegistredAndCommandNotExistOnDb() {
        // Setup
        final InboundCommandStarted commandStarted = mock(InboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final BaseDecoder decoder = mock(BaseDecoder.class);
        final BaseTransactionEvent event = mock(BaseTransactionEvent.class);
        when(mockDecoderRegistry.containsKey(anyString())).thenReturn(true);
        when(commandStarted.getMeta()).thenReturn(meta);
        when(mockDecoderRegistry.get(anyString())).thenReturn(decoder);
        when(decoder.decode(meta)).thenReturn(event);
        when(mockCommandRepository.findInboundommandOpt(anyString())).thenReturn(Optional.empty());

        // Run the test
        languageTranslatorImplUnderTest.onCommandStarted(commandStarted);

        // Verify the results
        verify(mockBubbleCache, atLeastOnce()).addBubble(anyString(), any(BubbleMessage.class));
        verify(mockPublisher).publishEvent(BaseTransactionEvent.class);
    }

    @Test
    public void testOnCommandStartedWhenDecodedRegistredAndCommandExistInDb() {
        // Setup
        final InboundCommandStarted commandStarted = mock(InboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final BaseDecoder decoder = mock(BaseDecoder.class);
        final BaseTransactionEvent event = mock(BaseTransactionEvent.class);
        final InboundCommand command = mock(InboundCommand.class);
        when(mockDecoderRegistry.containsKey(anyString())).thenReturn(true);
        when(commandStarted.getMeta()).thenReturn(meta);
        when(mockDecoderRegistry.get(anyString())).thenReturn(decoder);
        when(decoder.decode(meta)).thenReturn(event);
        when(mockCommandRepository.findInboundommandOpt(anyString())).thenReturn(Optional.of(command));

        // Run the test
        languageTranslatorImplUnderTest.onCommandStarted(commandStarted);

        // Verify the results
        verify(mockBubbleCache, atLeastOnce()).addBubble(anyString(), any(BubbleMessage.class));
        verify(mockPublisher).publishEvent(event);
        verify(command).changeStatusToCompleted();
    }

    @Test
    public void testOnCommandPreparedDecodedRegistred() {
        // Setup
        final InboundCommandPrepared commandPrepared = mock(InboundCommandPrepared.class);
        final String subject = "subject";
        final CommandMeta meta = mock(CommandMeta.class);
        final BaseDecoder decoder = mock(BaseDecoder.class);
        final BaseTransactionEvent event = mock(BaseTransactionEvent.class);

        when(mockDecoderRegistry.containsKey(anyString())).thenReturn(true);
        when(commandPrepared.getMeta()).thenReturn(meta);
        when(mockDecoderRegistry.get(anyString())).thenReturn(decoder);
        when(decoder.decode(meta)).thenReturn(event);

        // Run the test
        languageTranslatorImplUnderTest.onCommandPrepared(commandPrepared);

        // Verify the results
        verify(mockBubbleCache, atLeastOnce()).addBubble(anyString(), any(BubbleMessage.class));
        verify(mockPublisher).publishEvent(any(BaseTransactionEvent.class));
    }

    @Test(expected = DecoderNotFoundException.class)
    public void testOnCommandPreparedDecodedNotRegistred() {
        // Setup
        final InboundCommandPrepared commandPrepared = mock(InboundCommandPrepared.class);
        final CommandMeta meta = mock(CommandMeta.class);

        when(mockDecoderRegistry.containsKey(anyString())).thenReturn(false);
        when(commandPrepared.getMeta()).thenReturn(meta);

        // Run the test
        languageTranslatorImplUnderTest.onCommandPrepared(commandPrepared);

        // Verify the results
        verify(mockBubbleCache, never()).addBubble(anyString(), any(BubbleMessage.class));
        verify(mockPublisher, never()).publishEvent(any());
    }

    @Test
    public void testAddBubbleMessage() {
        // Setup
        final CommandMeta meta = mock(CommandMeta.class);
        final Stream<BaseTransactionEvent> events = Stream.of(mock(BaseTransactionEvent.class));

        // Run the test
        languageTranslatorImplUnderTest.addBubbleMessage(meta, events);

        // Verify the results
        verify(mockBubbleCache, atLeastOnce()).addBubble(anyString(), any(BubbleMessage.class));
    }
}
