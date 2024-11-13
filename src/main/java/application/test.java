package application;
//classe anotada, teria os metodos de dominio (acho que nao da certo fazer assim, talvez criar uma classe de dominio separada e essa classe se comportar como um controller)

import annotation.Get;
import annotation.RequestMapping;
import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;

@RequestMapping("/test")
@Scope(ScopeType.PER_REQUEST)
@CreationStrategy(CreationStrategyType.LAZY_ACQUISITION)
public class test {

    @Get("/rota")
    public String method(){
        return "test";
    }

    public static void main(String[] args) {
        //registrar classe de dominio, instanciando-a

        Middleware middleware = new Middleware();
        middleware.start(8080, "tcp");

    }
}
