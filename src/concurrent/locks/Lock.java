package concurrent.locks;

import java.util.concurrent.TimeUnit;

public interface Lock {

    boolean tryLock();

    boolean tryLock(int time, TimeUnit unit) throws InterruptedException;

    boolean unlock();

}
