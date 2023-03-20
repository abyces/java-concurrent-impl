package test;

import myconcurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class CountDownLatchTest {
    @Test
    public void testCountDownLatch() {
        CountDownLatch latch = new CountDownLatch(5);
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                System.out.println(Thread.currentThread() + ": working.");
                try {
                    sleep(new Random().nextInt(5000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println(Thread.currentThread() + ": finished");
                    latch.countDown();
                }
            });
        }

        Stream.of(threads).forEach(Thread::start);
        System.out.println("all started.");
        latch.await();
        System.out.println("all done.");
    }
}