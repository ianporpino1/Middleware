package lifecycle;

import annotation.Component;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;

@Component
@CreationStrategy(CreationStrategyType.POOLING)
public class Dummy {
    public Dummy() {

    }
}
