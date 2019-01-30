package io.kermoss.cmd.domain;

public class CommandMeta {
    private final String commandId;
    private final String subject;
    private final String GTX;
    private final String LTX;
    private final String PGTX;
    private final String FLTX;
    private final String traceId;

    public CommandMeta(String commandId, String subject, String GTX, String LTX, String PGTX, String FLTX, String traceId) {
        this.commandId = commandId;
        this.subject = subject;
        this.GTX = GTX;
        this.LTX = LTX;
        this.PGTX = PGTX;
        this.FLTX = FLTX;
        this.traceId = traceId;
    }

    public static CommandMetaBuilder builder() {
        return new CommandMetaBuilder();
    }

    public String getCommandId() {
        return this.commandId;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getGTX() {
        return this.GTX;
    }

    public String getLTX() {
        return this.LTX;
    }

    public String getPGTX() {
        return PGTX;
    }

    public String getFLTX() {
        return FLTX;
    }

    public String getTraceId() {
        return traceId;
    }

    public static class CommandMetaBuilder {
        private String commandId;
        private String subject;
        private String GTX;
        private String LTX;
        private String PGTX;
        private String FLTX;
        private String traceId;

        CommandMetaBuilder() {
        }

        public CommandMeta.CommandMetaBuilder commandId(String commandId) {
            this.commandId = commandId;
            return this;
        }

        public CommandMeta.CommandMetaBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public CommandMeta.CommandMetaBuilder GTX(String GTX) {
            this.GTX = GTX;
            return this;
        }

        public CommandMeta.CommandMetaBuilder LTX(String LTX) {
            this.LTX = LTX;
            return this;
        }

        public CommandMeta.CommandMetaBuilder PGTX(String PGTX) {
            this.PGTX = PGTX;
            return this;
        }

        public CommandMeta.CommandMetaBuilder FLTX(String FLTX) {
            this.FLTX = FLTX;
            return this;
        }

        public CommandMeta.CommandMetaBuilder trace(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public CommandMeta build() {
            return new CommandMeta(commandId, subject, GTX, LTX, PGTX, FLTX, traceId);
        }

        @Override
        public String toString() {
            return "CommandMetaBuilder{" +
                    "commandId='" + commandId + '\'' +
                    ", subject='" + subject + '\'' +
                    ", GTX='" + GTX + '\'' +
                    ", LTX='" + LTX + '\'' +
                    ", PGTX='" + PGTX + '\'' +
                    ", FLTX='" + FLTX + '\'' +
                    '}';
        }
    }
}
