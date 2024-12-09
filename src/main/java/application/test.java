package application;

import annotation.Component;
import annotation.parameters.PathVariable;
import annotation.parameters.RequestBody;
import annotation.parameters.RequestParam;
import annotation.web.Get;
import annotation.web.RequestMapping;
import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;


@RequestMapping("/test")
@Scope(ScopeType.STATIC_INSTANCE)
@CreationStrategy(CreationStrategyType.LAZY_ACQUISITION)
@Component
public class test {

    private TestService service;

    public test(TestService service) {
        this.service = service;
    }

    @Get("/hello/{userId}")
    //@Secured anotacao que forcaria requisicao ter token
    public String hello(@RequestParam("name") String name, @PathVariable("userId") String userId
            ,@RequestBody User user) {
        return "Hello "  + user.name + " with password " + user.password + " with name " + name + " with id " + userId ;
    }

    @Get("/hi")
    public String hi() {
        return service.hi();
    }

}

