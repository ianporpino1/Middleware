package lifecycle;

import exceptions.BadConstructorException;

import java.lang.reflect.Constructor;
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
    private final LifecycleManager lifecycleManager;

    // criado para testes
    public PoolingResource(Class<?> clazz, int size, LifecycleManager lifecycleManager) {
        this.clazz = clazz;
        this.pool = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
        this.maxPoolSize = size;
        this.poolNotEmpty = lock.newCondition();
        this.lifecycleManager = lifecycleManager;

        try {
            populatePool(maxPoolSize);
        } catch (BadConstructorException e) {
            throw new RuntimeException(e);
        }
    }

    public PoolingResource(Class<?> clazz, LifecycleManager lifecycleManager) {
        this.clazz = clazz;
        this.pool = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
        this.maxPoolSize = 10;
        this.poolNotEmpty = lock.newCondition();
        this.lifecycleManager = lifecycleManager;

        try {
            populatePool(maxPoolSize);
        } catch (BadConstructorException e) {
            throw new RuntimeException(e);
        }
    }

    private void populatePool(int poolSize) throws BadConstructorException {
        for (int i = 0; i < poolSize; i++) {
            Object servant = createServant();
            pool.add(servant);
        }
    }

    private Object createServant() throws BadConstructorException {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                parameters[i] = lifecycleManager.getRemoteObject(parameterTypes[i]);
            }

            return constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getServant() throws BadConstructorException {
        lock.lock();
        try {
            while(pool.isEmpty()) {
                poolNotEmpty.await();
            }
            return pool.poll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void releaseServant(Object servant) {
        lock.lock();
        try {
            pool.offer(servant);
            poolNotEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
}
