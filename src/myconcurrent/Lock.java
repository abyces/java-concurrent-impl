package myconcurrent;

import java.util.concurrent.TimeUnit;

public interface Lock {

    boolean tryLock();

    boolean unlock();

}
