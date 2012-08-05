import org.neo4j.graphdb.RelationshipType;

public interface NewEntityTypeHandler {
    void handle(RelationshipType relationshipType);
}
