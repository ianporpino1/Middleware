package lifecycle;

import lifecycle.exceptions.BadConstructorException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemoteObjectTest {
    @Test
    public void testStaticLifecycleWithLazyAcquisition() throws BadConstructorException {
        RemoteObject remoteObject = new RemoteObject(
                TestClass.class,
                new StaticInstance(),
                new LazyAcquisitionResource(TestClass.class)
        );

        Object servant1 = remoteObject.getServant();
        Object servant2 = remoteObject.getServant();

        System.out.println(servant1);
        System.out.println(servant2);

        assertSame(servant1, servant2);
    }

    @Test
    public void testPerRequestLifecycleWithLazyAcquisition() throws BadConstructorException {
        RemoteObject remoteObject = new RemoteObject(
                TestClass.class,
                new PerRequestInstance(),
                new LazyAcquisitionResource(TestClass.class)
        );

        Object servant1 = remoteObject.getServant();
        Object servant2 = remoteObject.getServant();

        System.out.println(servant1);
        System.out.println(servant2);

        assertNotSame(servant1, servant2);
    }

    @Test
    public void testPoolingResourceWithPerRequestLifecycle() throws BadConstructorException {
        RemoteObject remoteObject = new RemoteObject(
                TestClass.class,
                new PerRequestInstance(),
                new PoolingResource(TestClass.class)
        );

        Object servant1 = remoteObject.getServant();
        System.out.println(servant1);
        remoteObject.releaseServant(servant1);
        Object servant2 = remoteObject.getServant();


        System.out.println(servant2);

        assertSame(servant1, servant2);
    }

    @Test
    public void testPoolingResourceBehavior() throws BadConstructorException, InterruptedException {
        PoolingResource resource = new PoolingResource(TestClass.class, 2);

        Thread t1 = new Thread(() -> {
            try {
                Object s1 = resource.getServant();
                Object s2 = resource.getServant();
                System.out.println("1 - " + s1);
                Thread.sleep(500);
                resource.releaseServant(s1);
            } catch (BadConstructorException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Object s3 = resource.getServant();
                System.out.println("3 - " + s3);
            } catch (BadConstructorException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }

    public static class TestClass {
        public TestClass() {}
    }



}