import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.Set;

import static org.junit.Assert.*;

public class TestNeo {

    private GraphDatabaseService graphDb;

    @Before
    public void setup(){
        graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
    }

    @Test
    public void testAdMatchSlotThroughCat(){
        RulesEngineDatabase database = new RulesEngineDatabase(graphDb);
        database.registerEntity(Slot.class,RelTypes.SLOT_REF);
        database.registerEntity(Advert.class, RelTypes.ADVERT_REF);
        database.registerEntity(Category.class, RelTypes.CATEGORY_REF);
        database.allowStructuralRelationship(Advert.class, RelTypes.IS, Category.class);
        database.allowStructuralRelationship(Slot.class, RelTypes.TARGETS, Category.class);
        Advert advert = database.create(Advert.class);
        Slot slot = database.create(Slot.class);
        Slot slot2 = database.create(Slot.class);
        Category category = database.create(Category.class);
        Category category2 = database.create(Category.class);
        database.associate(advert, RelTypes.IS,category);
        database.associate(advert, RelTypes.IS,category2);
        database.associate(slot, RelTypes.TARGETS,category);
        database.associate(slot2, RelTypes.TARGETS,category);

        database.addVirtualRelationship(VirtualRelationShipDescription.of(Advert.class,"a",RelTypes.MATCHES, Slot.class,"s").where("a",RelTypes.IS,Category.class,"c").where("s",RelTypes.TARGETS,"c").build());
        assertTrue(database.does(advert,RelTypes.MATCHES,slot));
        Set<Slot> slots = database.findAll(Slot.class,advert,Advert.class,RelTypes.MATCHES);
        assertEquals(2, slots.size());

    }

    @Test
    public void test(){
        RulesEngineDatabase database = new RulesEngineDatabase(graphDb);
        database.registerEntity(Account.class,RelTypes.ACCOUNT_REF);
        database.registerEntity(Filter.class, RelTypes.FILTER_REF);
        database.allowStructuralRelationship(Account.class, RelTypes.OWNS, Filter.class);
        VirtualRelationShipDescription.Builder<Account,Filter> where = accountHasFilter();
        database.addVirtualRelationship(where.build());

        Account account = database.create(Account.class);
        Filter filter = database.create(Filter.class);
        Filter filter2 = database.create(Filter.class);
        database.associate(account, RelTypes.OWNS, filter);

        assertTrue(database.does(account, RelTypes.OWNS, filter));
        assertFalse(database.does(account, RelTypes.OWNS, filter2));
        Set<Filter> all = database.findAll(Filter.class, account,Account.class, RelTypes.OWNS);
        assertTrue(all.contains(filter));
        assertEquals(1,all.size());

        assertTrue(database.does(account, RelTypes.HAS, filter));
        assertFalse(database.does(account, RelTypes.HAS, filter2));
        all = database.findAll(Filter.class, account,Account.class, RelTypes.HAS);
        assertTrue(all.contains(filter));
        assertEquals(1,all.size());

        assertNotNull(account.getUnderlyingNode());
    }

    private VirtualRelationShipDescription.Builder<Account,Filter> accountHasFilter() {
        VirtualRelationShipDescription.Builder<Account, Filter> builder = VirtualRelationShipDescription.of(Account.class, "a", RelTypes.HAS, Filter.class, "f");
        builder = builder.where("a", RelTypes.OWNS, "f");
        return builder;
    }

    @After
    public void destroyTestDatabase()
    {
        graphDb.shutdown();
    }

    private static enum RelTypes implements RelationshipType
    {
        ACCOUNT_REF, FILTER_REF,SLOT_REF,ADVERT_REF,CATEGORY_REF, OWNS, HAS, IS, TARGETS,MATCHES
    }
}
