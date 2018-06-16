package sanskrit

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.security.SecureRandom

class NodePatchTest{
    private val random = SecureRandom()
    private val maxConnections = 1

    fun randomDB() = File("target/" + UUID().value + ".dat")

    @Test fun testPatchStore(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchTitle(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.title = UUID().value
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchSubtitle(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.subtitle = UUID().value
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchManuscript(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.manuscript = UUID().value
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchDescription(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.description = UUID().value
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchSummary(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.summary = UUID().value
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchNotes(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.notes = UUID().value
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchAddChild(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.addChild( UUID() )
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchRemoveChild(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.removeChild(0)
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchClearChildren(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.clearChildren()
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchAddContributor(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.addContributor( createContributor() )
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testPatchRemoveContributor(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        val contributor = node.contributors[0]
        node.removeContributor(contributor)
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    @Test fun testClearContributors(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )
        val node = createNode()
        db.storeNode(node)

        node.clearContributors()
        db.patchNode(node)

        val loaded = db.retrieveNode(node.uuid)
        Assert.assertEquals("Loaded node did not match original", node, loaded)
    }

    private fun createNode( uuid: String = UUID().value ): Node {
        val node = Node( UUID(uuid), UUID().value, UUID().value, UUID().value, UUID().value, UUID().value, UUID().value )
        for( i in 1..5 ) node.addChild( UUID() )
        for( i in 1..5 ) node.addContributor( createContributor() )
        node.dirty.clean()
        return node
    }

    private fun createContributor(): Contributor = Contributor( UUID().value, UUID().value, randomRole() )
    private fun randomRole(): ContributorRole{
        val list = ContributorRole.values()
        val next = random.nextInt( list.size )
        return list[next]
    }
}