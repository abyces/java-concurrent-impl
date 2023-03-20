package myconcurrent;

public class CountDownLatch {

    private static final class Sync extends AbstractQueuedSynchronizer {
        public Sync(int count) {
            setState(count);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            while (true) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c - 1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }

        int getCount() {
            return getState();
        }
    }

    private final Sync sync;

    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("Count < 0!");
        sync = new Sync(count);
    }

    public void await() {
        sync.acquireShared(1);
    }

    public void countDown() {
        sync.releaseShared(1);
    }

    public int getCount() {
        return sync.getCount();
    }

}
