package concurrent.locks;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer {

    /**
     * CLH node
     */
    abstract static class Node {
        AtomicReference<Node> prev;
        AtomicReference<Node> next;
        AtomicInteger status;
        Thread waiter;

//        volatile Node prev;
//        volatile Node next;
//        volatile int status;

        final boolean casPrev(Node c, Node v) {
            return prev.compareAndSet(c, v);
        }
        final boolean casNext(Node c, Node v) {
            return next.compareAndSet(c, v);
        }
        final int getAndUnsetStatus(int v) {
            return status.getAndSet(0);
        }
        final void setPrevRelaxed(Node p) {

        }
        final void setStatusRelaxed(int s) {}
        final void clearStatus() {}
    }

    private transient volatile Node head;

    private transient volatile Node tail;

//    private volatile int state;
    private AtomicInteger state;

    protected final int getState() {
        return state.get();
    }

    protected final void setState(int state) {
        this.state.set(state);
    }

    protected final boolean compareAndSetState(int expect, int update) {
        return state.compareAndSet(expect, update);
    }

    final void enqueue(Node node) {

    }

    final boolean isEnqueued(Node node) {
        return false;
    }

    /**
     * Main acquire method invoked by all exported acquire methods.
     * @param node
     * @param arg
     * @param shared
     * @param interruptible
     * @param timed
     * @param time
     * @return
     */
    final int acquire(Node node, int arg, boolean shared, boolean interruptible, boolean timed, long time) { return 0; }


    // Main exported methods
    protected boolean tryAcquire(int arg) { throw new UnsupportedOperationException(); }
    protected boolean tryRelease(int arg) { throw new UnsupportedOperationException(); }

    public final void acquire(int arg) {
        /** jdk 17 */
        if (!tryAcquire(arg))
            acquire(null, arg, false, false, false, 0L);
        /** jdk 1.8
         *      if (!tryAcquire(arg) &&
         *          acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
         *          selfInterrupt();
         */
    }

    public final boolean release() { return false; }

    public final Thread getFirstQueuedThread() { return null; }

    public final boolean isQueued(Thread thread) { return false; }

    public final boolean hasQueuedPredecessors() { return false; }


    // Unsafe
    private static final Unsafe U = Unsafe.getUnsafe();

}
