import org.neo4j.graphdb.*;

public class NodeWrappingEntityFactory implements EntityFactory {
    private final GraphDatabaseService service;
    private final EntityRegistry registry;

    public NodeWrappingEntityFactory(final GraphDatabaseService service,final EntityRegistry registry){
        this.service = service;
        this.registry = registry;
    }
    @Override
    public <T extends EngineNode> T create(Class<T> entityClass) {
        RelationshipType subrefNodeRelFor = registry.getSubrefNodeRelFor(entityClass);
        Transaction tx = service.beginTx();
        T t = null;
        try{
            Node refNode = service.getReferenceNode();
            Relationship relationship = refNode.getSingleRelationship(subrefNodeRelFor, Direction.OUTGOING);
            Node subrefNode = relationship.getEndNode();
            Node newNode = service.createNode();
            subrefNode.createRelationshipTo(newNode,subrefNodeRelFor);
            t = entityClass.newInstance();
            t.setNode(newNode);
            t.setService(service);
            tx.success();
        }catch (Exception e){
            tx.failure();
        }finally {
            tx.finish();
        }
        if(t == null){
            throw new RuntimeException("failed to create a new instance of " + entityClass);
        }else{
            return t;
        }
    }
}
