package concurrent.locks;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer {

    /**
     * CLH node
     */
    abstract static class Node {
        volatile Node prev;
        volatile Node next;
        Thread waiter;
        volatile int status;

        final boolean casPrev(Node c, Node v) {
            return false;
        }
        final boolean casNext(Node c, Node v) {
            return false;
        }
        final int getAndUnsetStatus(int v) {
            return 0;
        }
        final void setPrevRelaxed(Node p) {

        }
        final void setStatusRelaxed(int s) {}
        final void clearStatus() {}
    }

    private transient volatile Node head;
    private transient volatile Node tail;
    private volatile int state;

    protected final int getState() {
        return state;
    }

    protected final void setState(int state) {
        this.state = state;
    }

    protected final boolean compareAndSetState(int expect, int update) {
        return false;
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
        if (!tryAcquire(arg))
            acquire(null, arg, false, false, false, 0L);
    }

    public final boolean release() { return false; }

    public final Thread getFirstQueuedThread() { return null; }

    public final boolean isQueued(Thread thread) { return false; }

    public final boolean hasQueuedPredecessors() { return false; }


}
