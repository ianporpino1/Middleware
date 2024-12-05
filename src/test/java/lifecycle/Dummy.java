package lifecycle;

import annotation.Component;
import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;

@Component
@Scope(ScopeType.PER_REQUEST)
public class Dummy {
    public Dummy() {

    }
}
