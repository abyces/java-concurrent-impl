package concurrent.locks;

import java.util.concurrent.TimeUnit;

public class ReentrantLock implements Lock {

    private final Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {

        /*
        Checks for reentrancy
         */
        abstract boolean initialTryLock();

        final void lock() {
            if (!initialTryLock())
                acquire(1);
        }

        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (getExclusiveOwnerThread() != Thread.currentThread())
                throw new IllegalArgumentException();

            // AQS::getState(), state == 0 才说明资源已经被释放，而这是一个可重入锁
            boolean free = (c == 0);
            if (free)
                setExclusiveOwnerThread(null);
            setState(c);
            return free;
        }

        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

    }

    static final class NonfairSync extends Sync {
        @Override
        boolean initialTryLock() {
            Thread currentThread = Thread.currentThread();
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(currentThread);
                return true;
            } else if (getExclusiveOwnerThread() == currentThread) {
                setState(getState() + 1); // 源码判断了溢出的情况
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected boolean tryAcquire(int acquires) {
            if (getState() == 0 && compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
    }

    static final class FairSync extends Sync {
        @Override
        boolean initialTryLock() {
            Thread currentThread = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() && compareAndSetState(0, 1)) {
                    // 如果资源没有被占用，同时队列前没有别的排队线程
                    setExclusiveOwnerThread(currentThread);
                    return true;
                }
            } else if (getExclusiveOwnerThread() == currentThread) {
                // 资源还是处于被占有状态，同时是自己占有
                setState(c + 1); // 源码判断了溢出的情况
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryAcquire(int acquires) {
            if (getState() == 0 && !hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                // acquire only if thread is first waiter or empty
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
    }

    public ReentrantLock() { sync = new NonfairSync(); }

    public ReentrantLock(boolean fair) { sync = fair ? new FairSync() : new NonfairSync(); }

    public void lock() {
        sync.lock();
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(int time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public boolean unlock() {
        return sync.release(1);
    }
}
