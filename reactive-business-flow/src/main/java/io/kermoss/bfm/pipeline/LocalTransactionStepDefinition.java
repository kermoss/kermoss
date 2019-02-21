package io.kermoss.bfm.pipeline;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.trx.app.visitors.localtx.StepLocalTxVisitor;

public  class LocalTransactionStepDefinition<T extends BaseLocalTransactionEvent> extends AbstractTransactionStepDefinition<BaseLocalTransactionEvent> {

    @NotNull
    private WorkerMeta meta;

    public LocalTransactionStepDefinition(BaseLocalTransactionEvent in, Optional<Supplier> process, 
    		Stream<BaseTransactionCommand> send, Stream<BaseTransactionEvent> blow, 
    		ReceivedCommand receivedCommand, Optional<Consumer<String>> attach, 
    		ReceivedCommandGTX receivedCommandGTX, 
    		@NotNull WorkerMeta meta,CompensateWhen compensateWhen) {
        super(in, process, send, blow, receivedCommand, attach, receivedCommandGTX,compensateWhen);
        this.meta = meta;
    }

    public LocalTransactionStepDefinition() {
    }

    public static <T extends BaseLocalTransactionEvent> LocalTransactionPipelineBuilder<T> builder() {
        return new LocalTransactionPipelineBuilder<T>();
    }

    public void  accept(StepLocalTxVisitor visitor){
		visitor.visit(this);
	}

    @NotNull
    public WorkerMeta getMeta() {
        return this.meta;
    }

    public static class LocalTransactionPipelineBuilder<T extends BaseLocalTransactionEvent> {
        private BaseLocalTransactionEvent in;
        private Optional<Supplier> process;
        private Stream<BaseTransactionCommand> send;
        private Stream<BaseTransactionEvent> blow;
        private WorkerMeta meta;
        private ReceivedCommand receivedCommand;
        private Optional<Consumer<String>> attach;
        private ReceivedCommandGTX receivedCommandGTX;
        private CompensateWhen compensateWhen;


        LocalTransactionPipelineBuilder() {
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> in(BaseLocalTransactionEvent in) {
            this.in = in;
            return this;
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> process(Optional<Supplier> process) {
            this.process = process;
            return this;
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> send(Stream<BaseTransactionCommand> send) {
            this.send = send;
            return this;
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> blow(Stream<BaseTransactionEvent> blow) {
            this.blow = blow;
            return this;
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> meta(WorkerMeta meta) {
            this.meta = meta;
            return this;
        }

        public <P> LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> receive(Class<P> target, Consumer<P> consumer) {
            this.receivedCommand = new AbstractTransactionStepDefinition.ReceivedCommand<P>( target, consumer);
            return this;
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> attach(Consumer<String> attach){
            this.attach = Optional.ofNullable(attach);
            return this;
        }

        public <P> LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> receive(Class<P> target, BiConsumer<String, P> biConsumer){
            this.receivedCommandGTX = new AbstractTransactionStepDefinition.ReceivedCommandGTX<P>( target, biConsumer);
            return this;
        }
        
        @SafeVarargs
		public final <E extends Class<? extends Exception>> LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> compensateWhen(E... exceptionClazz){
            this.compensateWhen = new AbstractTransactionStepDefinition.CompensateWhen<E>(exceptionClazz);
            return this;
        }
        @SafeVarargs
		public final <E extends Class<? extends Exception>> LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> compensateWhen(Propagation propagation,E... exceptionClazz){
            this.compensateWhen = new AbstractTransactionStepDefinition.CompensateWhen<E>(propagation,exceptionClazz);
            return this;
        }
        
        @SafeVarargs
		public final <E extends Class<? extends Exception>> LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> compensateWhen(Propagation propagation,Stream<BaseTransactionEvent> blow,E... exceptionClazz){
            this.compensateWhen = new AbstractTransactionStepDefinition.CompensateWhen<E>(propagation,blow,exceptionClazz);
            return this;
        }
        
        public LocalTransactionStepDefinition<T> build() {
            return new LocalTransactionStepDefinition<T>(in, process, send, blow, receivedCommand, attach, receivedCommandGTX, meta,compensateWhen);
        }

		@Override
		public String toString() {
			return "LocalTransactionPipelineBuilder [in=" + in + ", process=" + process + ", send=" + send + ", blow="
					+ blow + ", meta=" + meta + ", receivedCommand=" + receivedCommand + ", attach=" + attach
					+ ", receivedCommandGTX=" + receivedCommandGTX + ", compensateWhen=" + compensateWhen + "]";
		}

        
    }
}
