package sanskrit

import org.junit.Test
import java.io.File
import java.security.SecureRandom

class SanskritDBtest{
    private val random = SecureRandom()
    private val maxConnections = 1

    fun randomDB() = File("target/" + UUID().value + ".dat")

    @Test fun testCreateDatabases(){
        SanskritDB.SQLite( maxConnections, randomDB() )
    }

    @Test fun testStoreNode(){
        val db = SanskritDB.SQLite( maxConnections, randomDB() )

        val nodes = listOf(
                createNode(),
                createNode(),
                createNode(),
                createNode(),
                createNode()
        )

        for( node in nodes ) db.storeNode(node)
    }

    private fun createNode(): Node{
        val node = Node(
                uuid = UUID(),
                title = UUID().value,
                subtitle = UUID().value,
                manuscript = UUID().value,
                description = UUID().value,
                summary = UUID().value,
                notes = UUID().value
        )

        for(i in 1..5) node.add( createContributor() )

        return node
    }

    private fun createContributor(): Contributor{
        val list = ContributorRole.values()
        val index = random.nextInt(list.size)
        return Contributor( UUID().value, UUID().value, list[index] )
    }
}