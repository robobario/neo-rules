import org.neo4j.graphdb.*;

public class NewEntityPopulator implements NewEntityTypeHandler {
    private GraphDatabaseService service;

    public NewEntityPopulator(GraphDatabaseService service) {
        this.service = service;
    }

    @Override
    public void handle(RelationshipType type) {
        Transaction tx = service.beginTx();
        try{
            Node refNode = service.getReferenceNode();
            Relationship relationship = refNode.getSingleRelationship(type, Direction.OUTGOING);
            if(null == relationship){
                Node node = service.createNode();
                refNode.createRelationshipTo(node,type);
            }
            tx.success();
        }catch (Exception e){
            tx.failure();
        }finally {
            tx.finish();
        }
    }
}
