import myconcurrent.Lock;
import myconcurrent.ReentrantLock;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        TestReentrantLock test1 = new TestReentrantLock();
        test1.doTest();
    }
}

class TestReentrantLock {
    int a = 0;

    public void doTest() {
        ReentrantLock lock = new ReentrantLock();


        Thread t1 = new Thread(() -> adding(lock));
        Thread t2 = new Thread(() -> adding(lock));

        t1.start();
        t2.start();

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(a);
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