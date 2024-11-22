package application;

import annotation.Component;
import annotation.web.Get;
import annotation.web.RequestMapping;
import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;


@RequestMapping("/test")
@Scope(ScopeType.PER_REQUEST)
@CreationStrategy(CreationStrategyType.LAZY_ACQUISITION)
@Component //por enquanto vai ficar assim
public class test {

    @Get("/rota")
    //@Secured anotacao que forcaria requisicao ter token
    public String method(){
        return "test";
    }
    
    
}
