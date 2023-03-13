package test;

import myconcurrent.Semaphore;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SemaphoreTest {
    @Test
    public void testAcquire() {
        Semaphore semaphore = new Semaphore(3);
        Thread[] threads = new Thread[6];
        for (int i = 0; i < 6; i++) {
            threads[i] = new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread() + " get the permit.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    System.out.println(Thread.currentThread() + " release the permit.");
                }
            });
        }

        Arrays.stream(threads).forEach(Thread::start);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTryAcquire() {
        Semaphore semaphore = new Semaphore(3);
        Thread[] threads = new Thread[6];
        for (int i = 0; i < 6; i++) {
            threads[i] = new Thread(() -> {
                try {
                    if (semaphore.tryAcquire(1)) {
                        System.out.println(Thread.currentThread() + " get the permit.");
                        Thread.sleep(1000);
                    } else {
                        System.out.println(Thread.currentThread() + " tried to get the permit.");
                        Thread.sleep(2000);
                        semaphore.acquire();
                        System.out.println(Thread.currentThread() + " get the permit.");
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    System.out.println(Thread.currentThread() + " release the permit.");
                }
            });
        }

        Arrays.stream(threads).forEach(Thread::start);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}