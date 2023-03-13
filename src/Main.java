import myconcurrent.Lock;
import myconcurrent.ReentrantLock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);  // 信号量
        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                try {
                    // 获得，假设如果线程已经满了，等待被释放为止
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "抢到了车位！！！");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "离开车位！！！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 将信号量释放，并唤醒等待的线程
                    semaphore.release();
                }
            }, String.valueOf(i)).start();
        }
    }
}
