import org.neo4j.graphdb.*;

public class EntityAssociator {
    private GraphDatabaseService service;
    private EntityRegistry registry;

    public EntityAssociator(GraphDatabaseService service, EntityRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    public <T extends EngineNode, Y extends EngineNode> void associate(T fromEntity, RelationshipType relationshipType, Y toEntity) {
            Transaction tx = service.beginTx();
            try{
                Node fromNode = service.getNodeById(fromEntity.getNodeId());
                RelationshipType subref = registry.getSubrefNodeRelFor(toEntity.getClass());
                Node subrefNode;
                if(!fromNode.hasRelationship(subref, Direction.OUTGOING)){
                    subrefNode = service.createNode();
                    fromNode.createRelationshipTo(subrefNode,subref);
                }else{
                    subrefNode = fromNode.getSingleRelationship(subref,Direction.OUTGOING).getEndNode();
                }
                Node toNode = service.getNodeById(toEntity.getNodeId());
                subrefNode.createRelationshipTo(toNode,relationshipType);
                tx.success();
            }catch (Exception e){
                tx.failure();
            }finally {
                tx.finish();
            }
    }
}
