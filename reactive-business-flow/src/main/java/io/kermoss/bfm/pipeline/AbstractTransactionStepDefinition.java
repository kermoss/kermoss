package io.kermoss.bfm.pipeline;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.bfm.event.BaseTransactionEvent;

public abstract class AbstractTransactionStepDefinition<F extends BaseTransactionEvent> {

	private F in;
	private Optional<Supplier> process;
	private Stream<BaseTransactionCommand> send;
	private Stream<BaseTransactionEvent> blow;
	private ReceivedCommand receivedCommand;
	private ReceivedCommandGTX receivedCommandGTX;
	private CompensateWhen compensateWhen;
	private Optional<Consumer<String>> attach;

	public AbstractTransactionStepDefinition(F in, Optional<Supplier> process, Stream<BaseTransactionCommand> send,
			Stream<BaseTransactionEvent> blow, ReceivedCommand receivedCommand, Optional<Consumer<String>> attach,
			ReceivedCommandGTX receivedCommandGTX, CompensateWhen compensateWhen) {
		this.in = in;
		this.process = process;
		this.send = send;
		this.blow = blow;
		this.receivedCommand = receivedCommand;
		this.attach = attach;
		this.receivedCommandGTX = receivedCommandGTX;
		this.compensateWhen = compensateWhen;
	}

	public AbstractTransactionStepDefinition() {
	}

	public F getIn() {
		return in;
	}

	public Optional<Supplier> getProcess() {
		if (process == null)
			return Optional.empty();
		return process;
	}

	public Stream<BaseTransactionCommand> getSend() {
		if (send == null)
			return Stream.empty();
		return send;
	}

	public <P> ReceivedCommand<P> getReceivedCommand() {
		return receivedCommand;
	}

	public <E extends Class<Exception>> CompensateWhen<E> getCompensateWhen() {
		return compensateWhen;
	}

	public <P> ReceivedCommandGTX<P> getReceivedCommandGTX() {
		return receivedCommandGTX;
	}

	public Stream<BaseTransactionEvent> getBlow() {
		if (blow == null)
			return Stream.empty();
		return blow;
	}

	public Optional<Consumer<String>> getAttach() {
		if (attach == null)
			return Optional.empty();
		return attach;
	}

	public static class CompensateWhen<E extends Class<? extends Exception>> {
		private E[] exceptionClazz;
		private Stream<BaseTransactionEvent> blow;
		private Propagation propagation = Propagation.LOCAL;

		public CompensateWhen(E... exceptionClazz) {
			this.exceptionClazz = exceptionClazz;
		}

		public CompensateWhen(Propagation propagation, E... exceptionClazz) {
			this.propagation=propagation;
			this.exceptionClazz = exceptionClazz;
		}
		public CompensateWhen(Propagation propagation, Stream<BaseTransactionEvent> blow,E... exceptionClazz) {
			this.propagation=propagation;
			this.blow = blow;
			this.exceptionClazz = exceptionClazz;
					 
		}

		public void setBlow(Stream<BaseTransactionEvent> blow) {
			this.blow = blow;
		}

		public E[] getExceptions() {
			return exceptionClazz;
		}

		public Propagation getPropagation() {
			return propagation;
		}
		public Stream<BaseTransactionEvent> getBlow() {
			return blow;
		}
	}

	public static class ReceivedCommand<P> {
		@NotNull
		private Class<P> target;
		@NotNull
		private Consumer<P> consumer;

		public ReceivedCommand(Class<P> target, Consumer<P> consumer) {
			super();
			this.target = target;
			this.consumer = consumer;
		}

		public Class<P> getTarget() {
			return target;
		}

		public Consumer<P> getConsumer() {
			return consumer;
		}
	}

	public static class ReceivedCommandGTX<P> {
		@NotNull
		private Class<P> target;
		@NotNull
		private BiConsumer<String, P> consumer;

		public ReceivedCommandGTX(Class<P> target, BiConsumer<String, P> consumer) {
			super();
			this.target = target;
			this.consumer = consumer;
		}

		@NotNull
		public Class<P> getTarget() {
			return target;
		}

		@NotNull
		public BiConsumer<String, P> getConsumer() {
			return consumer;
		}
	}

}
