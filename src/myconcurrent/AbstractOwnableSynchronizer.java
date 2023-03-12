package myconcurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractOwnableSynchronizer {

    protected AbstractOwnableSynchronizer() {
        exclusiveOwnerThread = new AtomicReference<>(null);
    }

    private volatile AtomicReference<Thread> exclusiveOwnerThread;

    public Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread.get();
    }

    public void setExclusiveOwnerThread(Thread exclusiveOwnerThread) {
        this.exclusiveOwnerThread.set(exclusiveOwnerThread);
    }
}