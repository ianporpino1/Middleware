package application;

import annotation.Get;
import annotation.RequestMapping;
import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;
import broker.Middleware;

@RequestMapping("/test")
@Scope(ScopeType.PER_REQUEST)
@CreationStrategy(CreationStrategyType.LAZY_ACQUISITION)
public class test {

    @Get("/rota")
    public String method(){
        return "test";
    }
    
    
    
    

    public static void main(String[] args) {
        //registrar classe de dominio

        Middleware middleware = new Middleware();
        middleware.addComponent(test.class);
        middleware.addComponent(test.class);//outra classe
        
        middleware.start(8080, "tcp");
    }
}
