package concurrent.locks;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer {

    static final int WAITING = 1;

    /**
     * CLH node
     */
    abstract static class Node {
        AtomicReference<Node> prev;
        AtomicReference<Node> next;
        AtomicInteger status;
        Thread waiter;

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

    private static void signalNext(Node h) {
        Node s;
        if (h != null) {
            s = h.next.get();
            if (s != null && s.status.get() != 0) {
                s.getAndUnsetStatus(WAITING);
                LockSupport.unpark(s.waiter);
            }
        }
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
    final int acquire(Node node, int arg, boolean shared, boolean interruptible, boolean timed, long time) {
        Thread currentThread = Thread.currentThread();
        byte spins = 0, postSpins = 0;
        boolean first = false;
        Node pred = null;

        for (;;) {
            // Check if node now first
            //      if so, ensure head stable, else ensure valid predecessor
            if (!first && (pred = (node == null) ? null : node.prev.get()) != null && !(first = (head == pred))) {
                if (pred.status.get() < 0) {
                    cleanQueue();
                    continue;
                } else if (pred.prev == null) {
                    Thread.onSpinWait();
                    continue;
                }
            }
            // if node is first or not yet enqueued, try acquiring
            if (first || pred == null) {
                // 暂时省略了 tryAcquireShared
                boolean acquired = tryAcquire(arg);
                if (acquired) {
                    if (first) {
                        node.prev = null;
                        head = node;
                        pred.next = null;
                        node.waiter = null;
                    }
                    return 1;
                }
            }
            // else if node not yet created, create it
            if (node == null) {
                // allocate
                node = new ExclusiveNode();
            } else if (pred == null) {
                // try to enq
                node.waiter = currentThread;
                Node t = tail;
                node.setPrevRelaxed(t);
                if (t == null)
                    tryInitializeHead();
                else if (!casTail(t, node))
                    node.setPrevRelaxed(null);
                else
                    t.next.set(node);
            } else if (first && spins != 0) {
                --spins;
                Thread.onSpinWait();
            } else if (node.status.get() == 0) {
                node.status.set(WAITING);
            } else {
                long nanos;
                spins = postSpins = (byte)((postSpins << 1) | 1);
                if (!timed)
                    LockSupport.park(this);
                else if ((nanos = time - System.nanoTime()) > 0L)
                    LockSupport.parkNanos(this, nanos);
                else
                    break;
                node.clearStatus();
            }
            return cancelAcquire(node, false, interruptible);
        }
    }


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

    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            signalNext(head);
            return true;
        }
        return false;
    }

    public final Thread getFirstQueuedThread() { return null; }

    public final boolean isQueued(Thread thread) { return false; }

    public final boolean hasQueuedPredecessors() { return false; }


    // Unsafe
    private static final Unsafe U = Unsafe.getUnsafe();

}
