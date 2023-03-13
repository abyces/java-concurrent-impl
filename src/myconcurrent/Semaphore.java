package myconcurrent;

public class Semaphore {

    private final Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {

        public Sync(int permits) {
            setState(permits);
        }

        final int getPermits() {
            return getState();
        }

        final int nonfairTryAcquireShared(int acquires) {
            while (true) {
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 || compareAndSetState(available, remaining))
                    // 资源不足的话会返回负数
                    // 或者CAS成功
                    return remaining;
            }
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            // 源码检查了overflow: curState + releases < curState
            while (true) {
                int currentState = getState();
                if (compareAndSetState(currentState, currentState + releases))
                    return true;
            }
        }

        final void reducePermits(int reductions) {
            // 源码检查了underflow: curState - reductions > curState
            while (true) {
                int currentState = getState();
                if (compareAndSetState(currentState, currentState - reductions))
                    return;
            }
        }

        final int drainPermits() {
            while (true) {
                int currentState = getState();
                if (currentState == 0 || compareAndSetState(currentState, 0))
                    return currentState;
            }
        }

    }

    static class FairSync extends Sync {

        public FairSync(int permits) {
            super(permits);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            while (true) {
                if (hasQueuedPredecessors(Thread.currentThread()))
                    return -1;
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 || compareAndSetState(available, remaining))
                    return remaining;
            }
        }
    }

    static class NonFairSync extends Sync {
        public NonFairSync(int permits) {
            super(permits);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return super.nonfairTryAcquireShared(acquires);
        }

    }

    public Semaphore(int permits) {
        this.sync = new NonFairSync(permits);
    }

    public Semaphore(int permits, boolean fair) {
        this.sync = fair ? new FairSync(permits) : new NonFairSync(permits);
    }

    public void acquire() {
        sync.acquireShared(1);
    }

    public void acquire(int acquires) {
        if (acquires < 0) throw new IllegalArgumentException();
        sync.acquireShared(acquires);
    }

    public boolean tryAcquire(int acquires) {
        if (acquires < 0) throw new IllegalArgumentException();
        return sync.tryAcquireShared(acquires) >= 0;
    }

    public void release() {
        sync.releaseShared(1);
    }

    public void release(int releases) {
        if (releases < 0) throw new IllegalArgumentException();
        sync.releaseShared(releases);
    }

    public boolean tryRelease(int releases) {
        if (releases < 0) throw new IllegalArgumentException();
        return sync.tryRelease(releases);
    }

    public int availablePermits() {
        return sync.getPermits();
    }

    /**
     * shrink number of available permits
     * @param reductions
     */
    public void reducePermits(int reductions) {
        if (reductions < 0) throw new IllegalArgumentException();
        sync.reducePermits(reductions);
    }

    /**
     * acquires all permits that are immediately available
     */
    public void drainPermits() {
        sync.drainPermits();
    }

}
