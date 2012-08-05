import org.neo4j.graphdb.RelationshipType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rules {
    public static <T extends EngineNode, Y extends EngineNode> Rule<T,Y> simpleRelation(Class<T> fromEntity, RelationshipType relationshipType, Class<Y> toEntity) {
        return new SimpleRelationRule<T,Y>(fromEntity,relationshipType,toEntity);
    }

    public static <T extends EngineNode, Y extends EngineNode> Rule virtualRelation(Class<T> fromEntity,Class<Y> toEntity,List<Rule> subrules) {
        return new VirtualChainRule<T,Y>(fromEntity,toEntity,subrules);
    }

    public static <T extends EngineNode, Y extends EngineNode> Rule<Y,T> reverse(Rule<T,Y> rule) {
        return new ReverseRule<Y,T>(rule);
    }

    private static class ReverseRule<T extends EngineNode,Y extends EngineNode> implements Rule<T,Y> {

        private Rule<Y, T> rule;

        public ReverseRule(Rule<Y,T> rule) {
            this.rule = rule;
        }

        @Override
        public List<RelationshipType> getTypesToTraverse(EntityRegistry registry) {
            List<RelationshipType> typesToTraverse = new ArrayList<RelationshipType>();
            typesToTraverse.addAll(rule.getTypesToTraverse(registry));
            Collections.reverse(typesToTraverse);
            return typesToTraverse;
        }

        @Override
        public Class<T> getFromClass() {
            return rule.getToClass();
        }

        @Override
        public Class<Y> getToClass() {
            return rule.getFromClass();
        }

    }

    private static class VirtualChainRule<T extends EngineNode,Y extends EngineNode> implements Rule<T,Y> {
        private final Class<T> fromEntity;
        private final Class<Y> toEntity;
        private List<Rule> subrules;

        public VirtualChainRule(Class<T> fromEntity, Class<Y> toEntity, List<Rule> subrules) {
            this.fromEntity = fromEntity;
            this.toEntity = toEntity;
            this.subrules = subrules;
        }

        @Override
        public List<RelationshipType> getTypesToTraverse(EntityRegistry registry) {
            ArrayList<RelationshipType> relationshipTypes = new ArrayList<RelationshipType>();
            for(Rule rule : subrules){
                relationshipTypes.addAll(rule.getTypesToTraverse(registry));
            }
            return relationshipTypes;
        }

        @Override
        public Class<T> getFromClass() {
            return fromEntity;
        }

        @Override
        public Class<Y> getToClass() {
            return toEntity;
        }

    }

    private static class SimpleRelationRule<T extends EngineNode,Y extends EngineNode> implements Rule<T,Y> {
        private final Class<T> fromEntity;
        private final RelationshipType relationshipType;
        private final Class<Y> toEntity;

        private SimpleRelationRule(Class<T> fromEntity, RelationshipType relationshipType, Class<Y> toEntity) {
            this.fromEntity = fromEntity;
            this.relationshipType = relationshipType;
            this.toEntity = toEntity;
        }

        @Override
        public List<RelationshipType> getTypesToTraverse(EntityRegistry registry) {
            ArrayList<RelationshipType> relationshipTypes = new ArrayList<RelationshipType>();
            relationshipTypes.add(registry.getSubrefNodeRelFor(toEntity));
            relationshipTypes.add(relationshipType);
            return relationshipTypes;
        }

        @Override
        public Class<T> getFromClass() {
            return fromEntity;
        }

        @Override
        public Class<Y> getToClass() {
            return toEntity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleRelationRule that = (SimpleRelationRule) o;

            if (fromEntity != null ? !fromEntity.equals(that.fromEntity) : that.fromEntity != null) return false;
            if (relationshipType != null ? !relationshipType.equals(that.relationshipType) : that.relationshipType != null)
                return false;
            if (toEntity != null ? !toEntity.equals(that.toEntity) : that.toEntity != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = fromEntity != null ? fromEntity.hashCode() : 0;
            result = 31 * result + (relationshipType != null ? relationshipType.hashCode() : 0);
            result = 31 * result + (toEntity != null ? toEntity.hashCode() : 0);
            return result;
        }
    }
}
