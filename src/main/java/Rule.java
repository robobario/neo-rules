import org.neo4j.graphdb.RelationshipType;

import java.util.List;

public interface Rule<T extends EngineNode, Y extends EngineNode> {

    List<RelationshipType> getTypesToTraverse(EntityRegistry registry);

    Class<T> getFromClass();

    Class<Y> getToClass();


}
