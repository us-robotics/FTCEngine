package FTCEngine.Core;

import java.util.ArrayList;

import FTCEngine.Core.Auto.AutoBehavior;

public abstract class AutoOpModeBase extends OpModeBase {

    boolean isQueueingActions;

    ArrayList<BehaviorAction<?>> actions = new ArrayList<BehaviorAction<?>>();

    @Override
    public void start() {
        super.start();

        isQueueingActions = true;
        queueActions();
        isQueueingActions = false;
    }

    protected abstract void queueActions();

    protected  <E,TBehavior extends AutoBehavior<E>> void execute(TBehavior behavior, E parameters) {
        checkQueueState();
        actions.add(new BehaviorAction<E>(behavior, parameters));
    }

    protected void buffer(AutoBehavior behavior,) {
        checkQueueState();
    }

    protected void execute() {
        checkQueueState();
    }

    private void checkQueueState() {
        if (!isQueueingActions) throw new IllegalStateException("Invalid time for queueing actions");
    }

    static class BehaviorAction<E> {
        public BehaviorAction(AutoBehavior behavior, E parameter) {
            this.behavior = behavior;
            this.parameter = parameter;
        }

        public final AutoBehavior behavior;
        public final E parameter;
    }
 }
