import org.neo4j.graphdb.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RulesEngine {
    private Map<RuleDescription, Rule> rules = new HashMap<RuleDescription, Rule>();

    public RulesEngine() {
    }

    public <T extends EngineNode, Y extends EngineNode> void allowStructuralRelationship(Class<T> fromEntity, RelationshipType relationshipType, Class<Y> toEntity) {
        Rule<T,Y> rule = Rules.simpleRelation(fromEntity, relationshipType, toEntity);
        rules.put(new RuleDescription<T, Y>(fromEntity, relationshipType, toEntity), rule);
        rules.put(new RuleDescription<Y, T>(toEntity, relationshipType, fromEntity),Rules.reverse(rule));
    }

    public <T extends EngineNode, Y extends EngineNode> boolean isAllowed(Class<T> fromEntity, RelationshipType relationshipType, Class<Y> toEntity) {
        return rules.containsKey(new RuleDescription<T, Y>(fromEntity, relationshipType, toEntity));
    }

    public <T extends EngineNode, Y extends EngineNode> Rule<T,Y> getRuleFor(Class<T> fromEntity, RelationshipType relationshipType, Class<Y> toEntity) {
        RuleDescription<T, Y> ruleDescription = new RuleDescription<T, Y>(fromEntity, relationshipType, toEntity);
        if(rules.containsKey(ruleDescription)){
            return (Rule<T,Y>)rules.get(ruleDescription);
        }else{
            throw new RuntimeException("no such rule");
        }
    }

    public <T extends EngineNode, Y extends EngineNode> void addVirtualRelationship(VirtualRelationShipDescription<T,Y> virtualDesc) {
        RuleDescription<T,Y> rule = new RuleDescription<T, Y>(virtualDesc.getFromClass(),virtualDesc.getFinalRelationshipType(),virtualDesc.getToClass());
        if(rules.containsKey(rule)){
            throw new RuntimeException("rule already exists");
        }else{
            List<Rule> subrules= new ArrayList<Rule>();
            for(RuleDescription description: virtualDesc.getRuleDescriptions()){
                if(rules.containsKey(description)){
                    subrules.add(rules.get(description));
                }else{
                    throw new RuntimeException("shit");
                }
            }
            rules.put(rule,Rules.virtualRelation(virtualDesc.getFromClass(),virtualDesc.getToClass(),subrules));
        }
    }
}
