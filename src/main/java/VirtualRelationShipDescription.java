import org.neo4j.graphdb.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualRelationShipDescription<T extends EngineNode, Y extends EngineNode> {
    private final List<RuleDescription> ruleDescriptions = new ArrayList<RuleDescription>();
    private final Class<T> fromClass;
    private final RelationshipType finalRelationshipType;
    private final Class<Y> toClass;

    private VirtualRelationShipDescription(Class<T> fromClass, RelationshipType finalRelationshipType, Class<Y> toClass){
        this.fromClass = fromClass;
        this.finalRelationshipType = finalRelationshipType;
        this.toClass = toClass;
    }

    private void add(RuleDescription description){
        ruleDescriptions.add(description);
    }

    public List<RuleDescription> getRuleDescriptions(){
        return ruleDescriptions;
    }

    public static <T extends EngineNode, Y extends EngineNode> Builder<T,Y> of(Class<T> fromClass, String fromAlias, RelationshipType type, Class<Y> toClass, String toAlias){
        return new Builder<T,Y>(fromClass,fromAlias,type,toClass,toAlias);
    }

    public RelationshipType getFinalRelationshipType() {
        return finalRelationshipType;
    }

    public Class<T> getFromClass() {
        return fromClass;
    }

    public Class<Y> getToClass() {
        return toClass;
    }

    public static class Builder<T extends EngineNode, Y extends EngineNode>{
        private String currentAlias;
        private final String destinationAlias;
        private Map<String,Class<? extends EngineNode>> aliasToClazz = new HashMap<String, Class<? extends EngineNode>>();
        VirtualRelationShipDescription<T,Y> description;

        private Builder(Class<T> fromClass, String fromAlias, RelationshipType finalRelationshipType, Class<Y> toClass, String toAlias) {
            destinationAlias = toAlias;
            currentAlias = fromAlias;
            aliasToClazz.put(fromAlias,fromClass);
            aliasToClazz.put(toAlias,toClass);
            description = new VirtualRelationShipDescription<T,Y>(fromClass,finalRelationshipType,toClass);
        }

        public Builder<T,Y> where(String fromAlias, RelationshipType relationship, Class<? extends EngineNode> clazz,String toAlias){
            if(aliasToClazz.containsKey(toAlias) || currentAlias.equals(destinationAlias)){
                throw new RuntimeException("aw hell naw this is bulllll shizzz");
            }else{
                aliasToClazz.put(toAlias,clazz);
            }
            if (fromAlias.equals(currentAlias)){
                addRule(aliasToClazz.get(fromAlias),relationship,clazz);
            }else if(toAlias.equals(currentAlias)){
                addRule(clazz,relationship,aliasToClazz.get(fromAlias));
            }
            else{
                throw new RuntimeException("aw hell naw this is bulllll shizzz");
            }
            currentAlias = toAlias;
            return this;
        }

        private <T extends EngineNode, Y extends EngineNode> void addRule(Class<T> fromClass, RelationshipType relationship, Class<Y> toClazz) {
            description.add(new RuleDescription<T,Y>(fromClass,relationship,toClazz));
        }

        public Builder<T,Y> where(String fromAlias, RelationshipType relationship,String toAlias){
            if(currentAlias.equals(destinationAlias) || !aliasToClazz.containsKey(fromAlias) || !aliasToClazz.containsKey(toAlias)){
                throw new RuntimeException("aw hell naw this is bulllll shizzz");
            }
            if (fromAlias.equals(currentAlias)){
                addRule(aliasToClazz.get(fromAlias),relationship,aliasToClazz.get(toAlias));
            }else if(toAlias.equals(currentAlias)){
                addRule(aliasToClazz.get(toAlias),relationship,aliasToClazz.get(fromAlias));
            }
            else{
                throw new RuntimeException("aw hell naw this is bulllll shizzz");
            }
            currentAlias = toAlias;
            return this;
        }

        public VirtualRelationShipDescription<T,Y> build(){
            return description;
        }
    }

}
