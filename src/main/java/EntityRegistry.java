import org.neo4j.graphdb.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRegistry {
    private Map<RelationshipType,Class<?>> relationshipTypeClassMap = new HashMap<RelationshipType,Class<?>>();
    private Map<Class<?>,RelationshipType> classRelationshipTypeMap = new HashMap<Class<?>,RelationshipType>();
    List<NewEntityTypeHandler> handlers = new ArrayList<NewEntityTypeHandler>();


    public boolean contains(RelationshipType subrefNodeType) {
        return relationshipTypeClassMap.containsKey(subrefNodeType);
    }

    public boolean contains(Class<?> clazz) {
        return classRelationshipTypeMap.containsKey(clazz);
    }

    public <T extends EngineNode> RelationshipType getSubrefNodeRelFor(Class<T> entityClass){
        if(contains(entityClass)){
            return classRelationshipTypeMap.get(entityClass);
        }else{
            throw new NoSuchClassRegisteredException("no subref node registered for entity class: " + entityClass.getSimpleName());
        }
    }

    public <T extends EngineNode> void register(Class<T> entityClass, RelationshipType subrefNodeType) {
        if(!contains(subrefNodeType)){
            relationshipTypeClassMap.put(subrefNodeType, entityClass);
            classRelationshipTypeMap.put(entityClass,subrefNodeType);
            notifyNewTypeHandlers(subrefNodeType);
        }else{
            throw new ExistingEntityException("Entity subrefType : " + subrefNodeType + " already exists");
        }
    }

    private void notifyNewTypeHandlers(RelationshipType subrefNodeType) {
        for(NewEntityTypeHandler handler : handlers){
            handler.handle(subrefNodeType);
        }
    }

    public void addNewEntityTypeHandler(NewEntityTypeHandler handler) {
        handlers.add(handler);
    }

    private class ExistingEntityException extends RuntimeException {
        ExistingEntityException(String message){
            super(message);
        }
    }

    private class NoSuchClassRegisteredException extends RuntimeException {
        NoSuchClassRegisteredException(String message){
            super(message);
        }
    }
}
