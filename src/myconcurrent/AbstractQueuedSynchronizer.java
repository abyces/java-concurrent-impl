package myconcurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer {

    private volatile AtomicInteger state;
    private volatile ArrayBlockingQueue<Thread> waiters;

    public AbstractQueuedSynchronizer() {
        state = new AtomicInteger(0);
        waiters = new ArrayBlockingQueue<>(10);
    }

    public final void acquire(int arg) {
        Thread current = Thread.currentThread();
        boolean isEnqueued = false, acquired = false;
        for (;;) {
            System.out.println(Thread.currentThread() + " try acquiring...");
            acquired = tryAcquire(arg);
            if (acquired) {
                break;
            } else {
                if (!isEnqueued) {
                    waiters.offer(current);
                    isEnqueued = true;
                }
                LockSupport.park(this);
                System.out.println(Thread.currentThread() + " parked...");
            }
        }
        waiters.remove(current);
    }

    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            System.out.println(Thread.currentThread() + " releasing lock, current waiters: " + waiters.toString());
            waiters.forEach(LockSupport::unpark);
            return true;
        }
        return false;
    }

    protected boolean tryAcquire(int arg) { throw new UnsupportedOperationException(); }
    protected boolean tryAcquireShared(int arg) { throw new UnsupportedOperationException(); }
    protected boolean tryRelease(int arg) { throw new UnsupportedOperationException(); }
    protected boolean tryReleaseShared(int arg) { throw new UnsupportedOperationException(); }

    protected final int getState() {
        return state.get();
    }
    protected final void setState(int state) {
        this.state.set(state);
    }
    protected final boolean compareAndSetState(int expect, int update) {
        return state.compareAndSet(expect, update);
    }

    public final boolean hasQueuedPredecessors(Thread thread) {
        if (!waiters.isEmpty() && waiters.peek() != thread) {
            return true;
        }
        return false;
    }

}
