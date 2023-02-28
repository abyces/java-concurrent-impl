package concurrent.locks;

public abstract class AbstractQueuedSynchronizer {

    /**
     * CLH node
     */
    abstract static class Node {
        volatile Node prev;
        volatile Node next;
        Thread waiter;
        volatile int status;

        

    }

}
