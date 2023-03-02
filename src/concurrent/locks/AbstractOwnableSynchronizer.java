package concurrent.locks;

public abstract class AbstractOwnableSynchronizer {

    protected AbstractOwnableSynchronizer() {}

    private transient Thread exclusiveOwnerThread;

    public Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

    public void setExclusiveOwnerThread(Thread exclusiveOwnerThread) {
        this.exclusiveOwnerThread = exclusiveOwnerThread;
    }
}
