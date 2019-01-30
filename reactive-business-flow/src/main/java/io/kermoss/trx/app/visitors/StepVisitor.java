package io.kermoss.trx.app.visitors;

import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.trx.app.TransactionUtilities;

public abstract class StepVisitor {

    private BubbleCache bubbleCache;
    private ApplicationEventPublisher applicationEventPublisher;
    private VisitorProvision provision;
    private CommandOrchestrator commandOrchestrator;
    private TransactionUtilities utilities;


    public StepVisitor(BubbleCache bubbleCache, ApplicationEventPublisher applicationEventPublisher, VisitorProvision provision, CommandOrchestrator commandOrchestrator, TransactionUtilities utilities) {
        this.bubbleCache = bubbleCache;
        this.applicationEventPublisher = applicationEventPublisher;
        this.provision = provision;
        this.commandOrchestrator = commandOrchestrator;
        this.utilities = utilities;
    }

    public StepVisitor() {
    }

    public BubbleCache getBubbleCache() {
        return this.bubbleCache;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return this.applicationEventPublisher;
    }

    public VisitorProvision getProvision() {
        return this.provision;
    }

    public CommandOrchestrator getCommandOrchestrator() {
        return this.commandOrchestrator;
    }

    public TransactionUtilities getUtilities() {
        return utilities;
    }
}
