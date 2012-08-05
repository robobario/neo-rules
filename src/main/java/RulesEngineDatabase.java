import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RulesEngineDatabase {
    private EntityRegistry registry = new EntityRegistry();
    private RulesEngine rulesEngine = new RulesEngine();
    private EntityAssociator associator;
    private EntityFactory factory;
    private RuleEvaluator evaluator;

    public RulesEngineDatabase(GraphDatabaseService service){
        NewEntityTypeHandler newEntityTypePopulator = new NewEntityPopulator(service);
        registry.addNewEntityTypeHandler(newEntityTypePopulator);
        factory = new NodeWrappingEntityFactory(service,registry);
        associator = new EntityAssociator(service,registry);
        evaluator = new RuleEvaluator(service,registry);
    }

    public <T extends EngineNode> void registerEntity(Class<T> entityClass, RelationshipType subrefNodeType){
        registry.register(entityClass, subrefNodeType);
    }

    public <T extends EngineNode,Y extends EngineNode> void allowStructuralRelationship(Class<T> fromEntity, RelationshipType relationshipType, Class<Y> toEntity){
        rulesEngine.allowStructuralRelationship(fromEntity, relationshipType, toEntity);
    }

    public <T extends EngineNode,Y extends EngineNode> void addVirtualRelationship(VirtualRelationShipDescription<T,Y> description){
        rulesEngine.addVirtualRelationship(description);
    }

    public <T extends EngineNode,Y extends EngineNode> void associate(T fromEntity, RelationshipType relationshipType, Y toEntity){
        if(rulesEngine.isAllowed(fromEntity.getClass(),relationshipType,toEntity.getClass())){
            associator.associate(fromEntity,relationshipType,toEntity);
        }else{
            throw new RuntimeException("no matching rule");
        }
    }

    public <T extends EngineNode> T create(Class<T> entityClass){
        return factory.create(entityClass);
    }

    public <T extends EngineNode,Y extends EngineNode> boolean does(T fromEntity, RelationshipType relationshipType, Y toEntity) {
        boolean does = false;
        if(rulesEngine.isAllowed(fromEntity.getClass(),relationshipType,toEntity.getClass())){
            Rule rule = getRuleFor(fromEntity.getClass(),relationshipType,toEntity.getClass());
            return evaluator.verify(rule, fromEntity, toEntity);
        }
        return does;
    }

    private <T extends EngineNode,Y extends EngineNode> Rule<T,Y> getRuleFor(Class<T> fromEntity, RelationshipType relationshipType, Class<Y >toEntity){
        return rulesEngine.getRuleFor(fromEntity,relationshipType,toEntity);
    }

    public <T extends EngineNode,Y extends EngineNode> Set<T> findAll(Class<T> toEntityClass, Y fromEntity, Class<Y> fromEntityClass, RelationshipType relationshipType) {
        Set<T> results = new HashSet<T>();
        if(rulesEngine.isAllowed(fromEntity.getClass(),relationshipType,toEntityClass)){
            Rule<Y,T> rule = getRuleFor(fromEntityClass, relationshipType, toEntityClass);
            Iterator<T> all = evaluator.findAll(rule, fromEntity);
            while(all.hasNext()){
                results.add(all.next());
            }
            return results;
        }
        return results;
    }
}
