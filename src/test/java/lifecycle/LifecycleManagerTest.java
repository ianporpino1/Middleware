package lifecycle;

import annotation.Component;
import annotation.scope.Scope;
import annotation.strategy.CreationStrategy;
import lifecycle.exceptions.BadConstructorException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleManagerTest {

    private final LifecycleManager lifecycleManager = new LifecycleManager();

    /*
    * Testa criação de objeto remoto default (static e lazy) usando apenas a anotação @Component
    * no próprio objeto. Verifica se objetos estáticos são o mesmo apesar da chamada
    * */
    @Test
    public void testDefaultRemoteObjectCreation() {
        try {
            Object s1 = lifecycleManager.getRemoteObject(Dummy.class);
            Object s2 = lifecycleManager.getRemoteObject(Dummy.class);

            lifecycleManager.listAllRemoteObjectsByClass(Dummy.class);

            assertSame(s1, s2);


        } catch (BadConstructorException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * Testa criação per request, objetos s1 e s2 não devem ser iguais, pois são criados em contexto
    * de uma requisição.
    * */
    @Test
    public void testPerRequestAndLazyObjectCreation() {
        try {
            Object s1 = lifecycleManager.getRemoteObject(Dummy.class);
            Object s2 = lifecycleManager.getRemoteObject(Dummy.class);

            lifecycleManager.listAllRemoteObjectsByClass(Dummy.class);

            assertNotSame(s1, s2);
        } catch (BadConstructorException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * Testa comportamento do pool de servants
    * */
    @Test
    public void testPoolingCreation() {
        try {
            // aqui vai retornar um servant dentro do pool
            Object s1 = lifecycleManager.getRemoteObject(Dummy.class);
            Object s2 = lifecycleManager.getRemoteObject(Dummy.class);

            assertNotSame(s1, s2);
        } catch (BadConstructorException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPerRequestPoolingCreation() {

    }
}

