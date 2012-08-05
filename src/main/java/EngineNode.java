import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public abstract class EngineNode {
    private Node node;
    private GraphDatabaseService service;

    protected Node getUnderlyingNode(){
        return node;
    }

    protected void setProperty(String propertyName, Object value){
        Transaction tx = service.beginTx();
        try{
            node.setProperty(propertyName, value);
            tx.success();
        }catch (Exception e){
            tx.failure();
        }finally {
            tx.finish();
        }
    }

    public long getNodeId(){
        return node.getId();
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setService(GraphDatabaseService service) {
        this.service = service;
    }
}
