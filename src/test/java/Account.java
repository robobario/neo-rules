public class Account extends EngineNode {

    @Override
    public boolean equals(Object o) {
        if(o instanceof Account){
            final Account other = (Account) o;
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
