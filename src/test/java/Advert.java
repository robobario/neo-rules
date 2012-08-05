public class Advert extends EngineNode {

    @Override
    public boolean equals(Object o) {
        if(o instanceof Advert){
            final Advert other = (Advert) o;
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
