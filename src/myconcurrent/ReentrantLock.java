package myconcurrent;

public class ReentrantLock implements Lock {

    private final Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {

        abstract boolean initialTryLock();

        final void lock() {
            if (!initialTryLock())
                acquire(1);
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getExclusiveOwnerThread() != Thread.currentThread())
                throw new IllegalArgumentException();

            int c = getState() - arg;
            boolean free = (c == 0);
            if (free) {
                setExclusiveOwnerThread(null);
            }

            setState(c);
            return free;
        }
    }

    static class FairSync extends Sync {
        @Override
        boolean initialTryLock() {
            Thread currentThread = Thread.currentThread();
            if (!hasQueuedPredecessors(currentThread) && compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(currentThread);
                return true;
            } else if (getExclusiveOwnerThread() == currentThread) {
                setState(getState() + 1);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected boolean tryAcquire(int arg) {
            if (getState() == 0 &&
                    !hasQueuedPredecessors(Thread.currentThread()) &&
                    compareAndSetState(0, arg)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
    }

    static class NonFairSync extends Sync {
        @Override
        boolean initialTryLock() {
            Thread currentThread = Thread.currentThread();
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(currentThread);
                return true;
            } else if (getExclusiveOwnerThread() == currentThread) {
                setState(getState() + 1);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected boolean tryAcquire(int arg) {
            if (getState() == 0 &&
                    compareAndSetState(0, arg)) {
                System.out.println(Thread.currentThread() + " in tryAcquire: state is " + getState() + ", owner is " + getExclusiveOwnerThread());
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            System.out.println(Thread.currentThread() + " failed in tryAcquire: state is " + getState() + ", owner is " + getExclusiveOwnerThread());
            return false;
        }
    }

    public ReentrantLock() {
        this.sync = new NonFairSync();
    }

    public ReentrantLock(boolean fair) {
        this.sync = fair ? new FairSync() : new NonFairSync();
    }

    public void lock() {
        sync.lock();
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean unlock() {
        return sync.release(1);
    }
}
