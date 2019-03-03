package io.kermoss.bfm.pipeline;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.trx.app.visitors.globaltx.StepGlobalTxVisitor;

public class GlobalTransactionStepDefinition<T extends BaseGlobalTransactionEvent> extends AbstractTransactionStepDefinition<BaseGlobalTransactionEvent> {
    private WorkerMeta meta;
    private Stream<BaseTransactionCommand> prepare;


    public GlobalTransactionStepDefinition(BaseGlobalTransactionEvent in, Optional<Supplier> process, Stream<BaseTransactionCommand> send, Stream<BaseTransactionEvent> blow, ReceivedCommand receivedCommand, Optional<Consumer<String>> attach, ReceivedCommandGTX receivedCommandGTX, WorkerMeta meta, Stream<BaseTransactionCommand> prepare,CompensateWhen compensateWhen) {
        super(in,null, process, send, blow, receivedCommand, attach, receivedCommandGTX,compensateWhen);
        this.meta = meta;
        this.prepare = prepare;
    }

    public GlobalTransactionStepDefinition() {
    }

    public static <T extends BaseGlobalTransactionEvent> GlobalTransactionPipelineBuilder<T> builder() {
        return new GlobalTransactionPipelineBuilder<T>();
    }

    public void  accept(StepGlobalTxVisitor visitor){
        visitor.visit(this);
    }


    public WorkerMeta getMeta() {
        return this.meta;
    }

    public Stream<BaseTransactionCommand> getPrepare() {
        if(prepare == null)
            return Stream.empty();
        return prepare;
    }

    public static class GlobalTransactionPipelineBuilder<T extends BaseGlobalTransactionEvent> {
        
    	private T in;
        private Optional<Supplier> process;
        private Stream<BaseTransactionCommand> send;
        private Stream<BaseTransactionCommand> prepare;
        private Stream<BaseTransactionEvent> blow;
        private WorkerMeta meta;
        private ReceivedCommand receivedCommand;
        private Optional<Consumer<String>> attach;
        private ReceivedCommandGTX receivedCommandGTX;
        private CompensateWhen compensateWhen;


        GlobalTransactionPipelineBuilder() {
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> in(T in) {
            this.in = in;
            return this;
        }
        

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> process(Optional<Supplier> process) {
            this.process = process;
            return this;
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> send(Stream<BaseTransactionCommand> send) {
            this.send = send;
            return this;
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> prepare(Stream<BaseTransactionCommand> prepare) {
            this.prepare = prepare;
            return this;
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> blow(Stream<BaseTransactionEvent> blow) {
            this.blow = blow;
            return this;
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> meta(WorkerMeta meta) {
            this.meta = meta;
            return this;
        }

        public <P> GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> receive(Class<P> target, Consumer<P> consumer) {
            this.receivedCommand = new AbstractTransactionStepDefinition.ReceivedCommand<P>( target, consumer);
            return this;
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> attach(Consumer<String> attach){
            this.attach = Optional.ofNullable(attach);
            return this;
        }

        public <P> GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> receive(Class<P> target, BiConsumer<String, P> biConsumer){
            this.receivedCommandGTX = new AbstractTransactionStepDefinition.ReceivedCommandGTX<P>( target, biConsumer);
            return this;
        }
        
        public <E extends Class<Exception>> GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> compensateWhen(E... exceptions){
            this.compensateWhen = new AbstractTransactionStepDefinition.CompensateWhen<E>(exceptions);
            return this;
        }

        public GlobalTransactionStepDefinition<T> build() {
            return new GlobalTransactionStepDefinition<T>(in, process, send, blow, receivedCommand, attach, receivedCommandGTX, meta, prepare,compensateWhen);
        }

		@Override
		public String toString() {
			return "GlobalTransactionPipelineBuilder [in=" + in + ", process=" + process + ", send=" + send
					+ ", prepare=" + prepare + ", blow=" + blow + ", meta=" + meta + ", receivedCommand="
					+ receivedCommand + ", attach=" + attach + ", receivedCommandGTX=" + receivedCommandGTX
					+ ", compensateWhen=" + compensateWhen + "]";
		}
    }
}
