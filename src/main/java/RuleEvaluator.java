import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

import java.util.Iterator;
import java.util.List;

public class RuleEvaluator {
    private GraphDatabaseService service;
    private EntityRegistry registry;

    public RuleEvaluator(GraphDatabaseService service,EntityRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    public <T extends EngineNode,Y extends EngineNode> Iterator<Y> findAll(final Rule<T,Y> rule, T fromEntity) {
        Transaction tx = service.beginTx();
        Iterator<Node> iterator = null;
        try{
            final Node fromNode = service.getNodeById(fromEntity.getNodeId());
            if(fromNode != null){
                TraversalDescription td = Traversal.description()
                        .breadthFirst();
                final List<RelationshipType> typesToTraverse = rule.getTypesToTraverse(registry);
                for(RelationshipType type: typesToTraverse){
                    td = td.relationships(type);
                }
                td = td.evaluator(Evaluators.excludeStartPosition()).evaluator(new Evaluator() {
                    @Override
                    public Evaluation evaluate(Path path) {
                        if(path.length() == 0){
                            return Evaluation.EXCLUDE_AND_CONTINUE;
                        }
                        else if(path.length() == typesToTraverse.size() && path.lastRelationship().getType().equals(typesToTraverse.get(path.length() - 1))){
                            return Evaluation.INCLUDE_AND_PRUNE;
                        }
                        else if(path.lastRelationship().getType().equals(typesToTraverse.get(path.length() - 1))){
                            return Evaluation.EXCLUDE_AND_CONTINUE;
                        }else{
                            return Evaluation.EXCLUDE_AND_PRUNE;
                        }
                    }
                });
                Traverser traverse = td.traverse(fromNode);
                iterator = traverse.nodes().iterator();
            }
            tx.success();
        }catch (Exception e){
            tx.failure();
        }finally {
            tx.finish();
        }
        final Iterator<Node> finalIterator = iterator;
        if(null != iterator){
            return new Iterator<Y>() {
                @Override
                public boolean hasNext() {
                    return finalIterator.hasNext();
                }

                @Override
                public Y next() {
                    Y instance;
                    Node next = finalIterator.next();
                    try {
                        instance = rule.getToClass().newInstance();
                        instance.setNode(next);
                        instance.setService(service);
                        return instance;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void remove() {
                    finalIterator.remove();
                }
            };
        }else{
            return null;
        }
    }

    public <T extends EngineNode, Y extends EngineNode> boolean verify(Rule rule, T fromEntity, final Y toEntity) {
        boolean found = false;
        Transaction tx = service.beginTx();
        try{
            final Node fromNode = service.getNodeById(fromEntity.getNodeId());
            if(fromNode != null){
                TraversalDescription td = Traversal.description()
                        .breadthFirst();
                final List<RelationshipType> typesToTraverse = rule.getTypesToTraverse(registry);
                for(RelationshipType type: typesToTraverse){
                   td = td.relationships(type);
                }
                td = td.evaluator(new Evaluator() {
                    @Override
                    public Evaluation evaluate(Path path) {
                        if(path.length() == 0){
                            return Evaluation.EXCLUDE_AND_CONTINUE;
                        }
                        else if(path.length() == typesToTraverse.size() && path.endNode().equals(toEntity.getUnderlyingNode()) && path.lastRelationship().getType().equals(typesToTraverse.get(path.length() - 1))){
                            return Evaluation.INCLUDE_AND_PRUNE;
                        }
                        else if(path.lastRelationship().getType().equals(typesToTraverse.get(path.length() - 1))){
                            return Evaluation.EXCLUDE_AND_CONTINUE;
                        }else{
                            return Evaluation.EXCLUDE_AND_PRUNE;
                        }
                    }
                });
                Traverser traverse = td.traverse(fromNode);
                Iterator<Node> iterator = traverse.nodes().iterator();
                Node next = iterator.next();
                found = null != next && !iterator.hasNext();
            }
            tx.success();
        }catch (Exception e){
            tx.failure();
        }finally {
            tx.finish();
        }
        return found;
    }
}
