package io.kermoss.cmd.infra.translator;

import io.kermoss.cmd.domain.event.InboundCommandPrepared;
import io.kermoss.cmd.domain.event.InboundCommandStarted;

public interface LanguageTranslator {
     void onCommandStarted(final InboundCommandStarted commandStarted);
     void onCommandPrepared(final InboundCommandPrepared commandPrepared);
     void registerDecoder(final String header, final BaseDecoder baseDecoder);
}
