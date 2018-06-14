package sanskrit

import org.junit.Assert
import org.junit.Test
import java.security.SecureRandom

class NodeTest{
    private val random = SecureRandom()

    @Test fun testNodeCreation(){
        val node = Node(
                title = "Title",
                subtitle = "Subtitle",
                manuscript = "Manuscript",
                description = "Description",
                summary = "Summary",
                notes = "Notes"
        )

        Assert.assertEquals("Unexpected title", "Title", node.title)
        Assert.assertEquals("Unexpected subtitle", "Subtitle", node.subtitle)
        Assert.assertEquals("Unexpected title", "Manuscript", node.manuscript)
        Assert.assertEquals("Unexpected title", "Description", node.description)
        Assert.assertEquals("Unexpected title", "Summary", node.summary)
        Assert.assertEquals("Unexpected title", "Notes", node.notes)
    }

    /// children

    @Test fun testAddChildren(){
        val node = createNode("Parent")

        val children = mutableListOf(
                UUID("1"),
                UUID("2"),
                UUID("3"),
                UUID("4"),
                UUID("5")
        )
        for(child in children) node.add(child)

        assertChildNodeOrder(children, node)

        val a = UUID("A")
        children.add(0, a)
        node.add(0, a)
        assertChildNodeOrder(children, node)

        val b = UUID("B")
        children.add(3, b)
        node.add(3, b)
        assertChildNodeOrder(children, node)

        val c = UUID("C")
        children.add(5, c)
        node.add(5, c)
        assertChildNodeOrder(children, node)
    }

    private fun assertChildNodeOrder(children: List<UUID>, node: Node ){
        for( i in 0 until children.size ){
            val expected = children[i]
            val actual = node.children[i]
            Assert.assertEquals("Unexpected child node", expected, actual)
        }
    }

    /// contributors

    @Test fun testAddContributors(){
        val node = createNode("Parent")

        val contributors = listOf(
                createContributor(),
                createContributor(),
                createContributor(),
                createContributor(),
                createContributor()
        )
        for( contributor in contributors ) node.add(contributor)

        assertContributorOrder( contributors, node )
    }

    @Test fun testRemoveContributors(){
        val node = createNode("Parent")

        val contributors = mutableListOf(
                createContributor(),
                createContributor(),
                createContributor(),
                createContributor(),
                createContributor()
        )
        for( contributor in contributors ) node.add(contributor)

        val a = contributors.removeAt(1)
        node.remove(a)

        val b = contributors.removeAt(3)
        node.remove(b)

        assertContributorOrder( contributors, node )
    }

    @Test fun testClearContributors(){
        val node = createNode("Parent")
        node.add( createContributor() )
        node.add( createContributor() )
        node.add( createContributor() )
        node.add( createContributor() )
        node.add( createContributor() )

        node.clearContributors()
        assertContributorOrder( listOf(), node )
    }

    private fun assertContributorOrder( contributors: List<Contributor>, node: Node ){
        for( i in 0 until contributors.size ){
            val expected = contributors[i]
            val actual = node.contributors[i]
            Assert.assertEquals("Unexpected contributors node", expected, actual)
        }
    }

    /// listeners

    @Test fun testNodeListenerTitle(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.title = UUID().value
        Assert.assertTrue("Title should have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerSubtitle(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.subtitle = UUID().value
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertTrue("Subtitle should have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerManuscript(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.manuscript = UUID().value
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertTrue("Manuscript should have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerDescription(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.description = UUID().value
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertTrue("Description should have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerSummary(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.summary = UUID().value
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertTrue("Summary should have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerNotes(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.notes = UUID().value
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertTrue("Notes should have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerChildrenAdd(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.add( UUID() )

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerChildrenAddAt(){
        val node = createNode()
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.add( 2, UUID() )

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerChildrenRemove(){
        val node = createNode()
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )

        val child = UUID()
        node.add( 2, child )

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.remove(child)

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerChildrenRemoveAt(){
        val node = createNode()
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )
        node.add( UUID() )

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.remove(3)

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
        Assert.assertFalse("Contributors should not have been changed", listener.contributors)
    }

    @Test fun testNodeListenerAddContributors(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.add( createContributor() )

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Subtitle should not have been changed", listener.subtitle)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
        Assert.assertTrue("Contributors should have been changed", listener.contributors)
    }

    private fun createNode( uuid: String = UUID().value ): Node = Node( UUID(uuid), UUID().value, UUID().value, UUID().value, UUID().value, UUID().value, UUID().value )

    private fun createContributor(): Contributor = Contributor( UUID().value, UUID().value, randomRole() )
    private fun randomRole(): ContributorRole{
        val list = ContributorRole.values()
        val next = random.nextInt( list.size )
        return list[next]
    }

    private class DummyNodeListener: NodeListener{
        var title: Boolean = false
        var subtitle: Boolean = false
        var manuscript: Boolean = false
        var description: Boolean = false
        var summary: Boolean = false
        var notes: Boolean = false
        var children: Boolean = false
        var contributors: Boolean = false

        override fun titleChanged(node: Node) { title = true }
        override fun subtitleChanged(node: Node) { subtitle = true }
        override fun manuscriptChanged(node: Node) { manuscript = true }
        override fun descriptionChanged(node: Node) { description = true }
        override fun summaryChanged(node: Node) { summary = true }
        override fun notesChanged(node: Node) { notes = true }
        override fun childrenChanged(node: Node) { children = true }
        override fun contributorsChanged(node: Node) { contributors = true }
    }
}