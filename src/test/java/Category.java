public class Category extends EngineNode {

    @Override
    public boolean equals(Object o) {
        if(o instanceof Category){
            final Category other = (Category) o;
            return other.getNodeId() == this.getNodeId();
        } else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
