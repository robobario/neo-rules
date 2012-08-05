import org.neo4j.graphdb.RelationshipType;

public class RuleDescription<T extends EngineNode,Y extends EngineNode> {
    private final Class<T> fromEntity;
    private final RelationshipType relationshipType;
    private final Class<Y> toEntity;

    public RuleDescription(Class<T> fromEntity, RelationshipType relationshipType, Class<Y> toEntity) {
        this.fromEntity = fromEntity;
        this.relationshipType = relationshipType;
        this.toEntity = toEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleDescription that = (RuleDescription) o;

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
