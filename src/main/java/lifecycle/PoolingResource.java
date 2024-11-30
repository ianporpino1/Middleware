package lifecycle;

import lifecycle.exceptions.BadConstructorException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PoolingResource implements ResourceStrategy {

    private final Class<?> clazz;
    private final Queue<Object> pool;
    private final Lock lock;
    private final int maxPoolSize;
    private final Condition poolNotEmpty;

    public PoolingResource(Class<?> clazz) {
        this.clazz = clazz;
        this.pool = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
        this.maxPoolSize = 10;
        this.poolNotEmpty = lock.newCondition();
    }

    @Override
    public Object createServant() throws BadConstructorException {
        Object servant = pool.poll();
        if (servant != null) {
            return servant;
        }

        lock.lock();
        try {
            while (pool.isEmpty() || pool.size() >= maxPoolSize) {
                poolNotEmpty.await();
            }
            servant = pool.poll();
            if (servant == null) {
                try {
                    servant = clazz.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return servant;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for a servant.", e);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void releaseServant(Object servant) {
        lock.lock();
        try {
            pool.offer(servant);
            poolNotEmpty.signal(); // Notifica threads aguardando por servants
        } finally {
            lock.unlock();
        }
    }
}
