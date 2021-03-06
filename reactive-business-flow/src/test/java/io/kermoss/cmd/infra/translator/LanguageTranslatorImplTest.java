package io.kermoss.cmd.infra.translator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.event.InboundCommandPrepared;
import io.kermoss.cmd.domain.event.InboundCommandStarted;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.CommandNotFoundException;
import io.kermoss.cmd.exception.DecoderNotFoundException;
import io.kermoss.domain.DecoderRegistry;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.infra.KermossTxLogger;

@MockitoSettings(strictness = Strictness.LENIENT)
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

    @BeforeEach
    public void setUp() {
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

  

    @Test
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
        assertThrows(DecoderNotFoundException.class,()-> languageTranslatorImplUnderTest.onCommandStarted(commandStarted));

    }
}
