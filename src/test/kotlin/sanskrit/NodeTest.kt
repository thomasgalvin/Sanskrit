package sanskrit

import org.junit.Assert
import org.junit.Test

class NodeTest{
    @Test fun testNodeCreation(){
        val node = Node(
                title = "Title",
                manuscript = "Manuscript",
                description = "Description",
                summary = "Summary",
                notes = "Notes"
        )

        Assert.assertEquals("Unexpected title", "Title", node.title)
        Assert.assertEquals("Unexpected title", "Manuscript", node.manuscript)
        Assert.assertEquals("Unexpected title", "Description", node.description)
        Assert.assertEquals("Unexpected title", "Summary", node.summary)
        Assert.assertEquals("Unexpected title", "Notes", node.notes)
    }

    @Test fun testAddChildren(){
        val node = createNode("Parent")

        val children = mutableListOf(
                createNode("1"),
                createNode("2"),
                createNode("3"),
                createNode("4"),
                createNode("5")
        )
        for(child in children) node.add(child)

        assertOrder(children, node)

        val a = createNode("A")
        children.add(0, a)
        node.add(0, a)
        assertOrder(children, node)

        val b = createNode("B")
        children.add(3, b)
        node.add(3, b)
        assertOrder(children, node)

        val c = createNode("C")
        children.add(5, c)
        node.add(5, c)
        assertOrder(children, node)
    }

    private fun assertOrder( children: List<Node>, node: Node ){
        for( i in 0 until children.size ){
            val expected = children[i]
            val actual = node[i]

            Assert.assertEquals("Unexpected child node", expected, actual)
        }
    }

    @Test fun testNodeListenerTitle(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.title = uuid()
        Assert.assertTrue("Title should have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
    }

    @Test fun testNodeListenerManuscript(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.manuscript = uuid()
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertTrue("Manuscript should have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
    }

    @Test fun testNodeListenerDescription(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.description = uuid()
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertTrue("Description should have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
    }

    @Test fun testNodeListenerSummary(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.summary = uuid()
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertTrue("Summary should have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
    }

    @Test fun testNodeListenerNotes(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.notes = uuid()
        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertTrue("Notes should have been changed", listener.notes)
        Assert.assertFalse("Children should not have been changed", listener.children)
    }

    @Test fun testNodeListenerChildrenAdd(){
        val node = createNode()

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.add( createNode() )

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
    }

    @Test fun testNodeListenerChildrenAddAt(){
        val node = createNode()
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.add( 2, createNode() )

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
    }

    @Test fun testNodeListenerChildrenRemove(){
        val node = createNode()
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )

        val child = createNode()
        node.add( 2, child )

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.remove(child)

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
    }

    @Test fun testNodeListenerChildrenRemoveAt(){
        val node = createNode()
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )
        node.add( createNode() )

        val listener = DummyNodeListener()
        node.addListener(listener)

        node.remove(3)

        Assert.assertFalse("Title should not have been changed", listener.title)
        Assert.assertFalse("Manuscript should not have been changed", listener.manuscript)
        Assert.assertFalse("Description should not have been changed", listener.description)
        Assert.assertFalse("Summary should not have been changed", listener.summary)
        Assert.assertFalse("Notes should not have been changed", listener.notes)
        Assert.assertTrue("Children should have been changed", listener.children)
    }

    private fun createNode( uuid: String = uuid() ): Node = Node( uuid, uuid(), uuid(), uuid(), uuid(), uuid() )

    private class DummyNodeListener: NodeListener{
        var title: Boolean = false
        var manuscript: Boolean = false
        var description: Boolean = false
        var summary: Boolean = false
        var notes: Boolean = false
        var children: Boolean = false

        override fun titleChanged(node: Node) {
            title = true
        }

        override fun manuscriptChanged(node: Node) {
            manuscript = true
        }

        override fun descriptionChanged(node: Node) {
            description = true
        }

        override fun summaryChanged(node: Node) {
            summary = true
        }

        override fun notesChanged(node: Node) {
            notes = true
        }

        override fun childrenChanged(node: Node) {
            children = true
        }
    }
}