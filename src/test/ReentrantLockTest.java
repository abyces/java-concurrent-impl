package test;

import myconcurrent.ReentrantLock;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;

class ReentrantLockTest {
    int a = 0;

    @Test
    public void testLock() {
        ReentrantLock lock = new ReentrantLock();

        Thread t1 = new Thread(() -> adding(lock));
        Thread t2 = new Thread(() -> adding(lock));
        Thread t3 = new Thread(() -> adding(lock));
        Thread t4 = new Thread(() -> adding(lock));

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assert a == 400: "a = " + a + ", Test failed.";
        System.out.println("a == " + a);
    }

    private void adding(ReentrantLock lock) {
        for (int i = 0; i < 100; i++) {
            lock.lock();
            System.out.println(Thread.currentThread() + " get the lock");
            a += 1;
            lock.unlock();
            System.out.println(Thread.currentThread() + " release the lock");
        }
    }
}