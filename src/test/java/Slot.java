public class Slot extends EngineNode {

    @Override
    public boolean equals(Object o) {
        if(o instanceof Slot){
            final Slot other = (Slot) o;
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
