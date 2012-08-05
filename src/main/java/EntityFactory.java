public interface EntityFactory {
    <T extends EngineNode> T create(Class<T> entityClass);
}
