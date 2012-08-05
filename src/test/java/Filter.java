public class Filter extends EngineNode{
    @Override
    public boolean equals(Object o) {
        if(o instanceof Filter){
            final Filter other = (Filter) o;
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
