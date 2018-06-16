package sanskrit

import org.junit.Assert
import org.junit.Test

class NodeReferenceTest{
    val strings = Strings()

    private val expected = """
        Manuscript
          Chapter 1
            Section A
              Subsection I
              Subsection II
              Subsection III
            Section B
              Subsection IV
              Subsection V
              Subsection VI
            Section C
              Subsection VII
              Subsection VIII
              Subsection IX
          Chapter 2
            Section D
              Subsection X
              Subsection XI
              Subsection XII
            Section E
              Subsection XIII
              Subsection XIV
              Subsection XV
            Section F
              Subsection XVI
              Subsection XVII
              Subsection XVIII
          Chapter 3
            Section G
              Subsection XIX
              Subsection XX
              Subsection XXI
            Section H
              Subsection XXII
              Subsection XXIII
              Subsection XXIV
            Section I
              Subsection XXV
              Subsection XXVI
              Subsection XXVII
    """.trimIndent()

    @Test fun testToNodeReference(){
        val nodeSource = DummyNodeDB()

        val manuscript = createNode(uuid = strings.manuscript, title = strings.manuscript, parent = null, nodeSource = nodeSource)
            val ch1 = createNode(uuid = "Chapter 1", nodeSource = nodeSource, parent = manuscript)
                val secA = createNode(uuid = "Section A", nodeSource = nodeSource, parent = ch1)
                    createNode(uuid = "Subsection I", nodeSource = nodeSource, parent = secA)
                    createNode(uuid = "Subsection II", nodeSource = nodeSource, parent = secA)
                    createNode(uuid = "Subsection III", nodeSource = nodeSource, parent = secA)
                val secB = createNode(uuid = "Section B", nodeSource = nodeSource, parent = ch1)
                    createNode(uuid = "Subsection IV", nodeSource = nodeSource, parent = secB)
                    createNode(uuid = "Subsection V", nodeSource = nodeSource, parent = secB)
                    createNode(uuid = "Subsection VI", nodeSource = nodeSource, parent = secB)
                val secC = createNode(uuid = "Section C", nodeSource = nodeSource, parent = ch1)
                    createNode(uuid = "Subsection VII", nodeSource = nodeSource, parent = secC)
                    createNode(uuid = "Subsection VIII", nodeSource = nodeSource, parent = secC)
                    createNode(uuid = "Subsection IX", nodeSource = nodeSource, parent = secC)
            val ch2 = createNode(uuid = "Chapter 2", nodeSource = nodeSource, parent = manuscript)
                val secD = createNode(uuid = "Section D", nodeSource = nodeSource, parent = ch2)
                    createNode(uuid = "Subsection X", nodeSource = nodeSource, parent = secD)
                    createNode(uuid = "Subsection XI", nodeSource = nodeSource, parent = secD)
                    createNode(uuid = "Subsection XII", nodeSource = nodeSource, parent = secD)
                val secE = createNode(uuid = "Section E", nodeSource = nodeSource, parent = ch2)
                    createNode(uuid = "Subsection XIII", nodeSource = nodeSource, parent = secE)
                    createNode(uuid = "Subsection XIV", nodeSource = nodeSource, parent = secE)
                    createNode(uuid = "Subsection XV", nodeSource = nodeSource, parent = secE)
                val secF = createNode(uuid = "Section F", nodeSource = nodeSource, parent = ch2)
                    createNode(uuid = "Subsection XVI", nodeSource = nodeSource, parent = secF)
                    createNode(uuid = "Subsection XVII", nodeSource = nodeSource, parent = secF)
                    createNode(uuid = "Subsection XVIII", nodeSource = nodeSource, parent = secF)
            val ch3 = createNode(uuid = "Chapter 3", nodeSource = nodeSource, parent = manuscript)
                val secG = createNode(uuid = "Section G", nodeSource = nodeSource, parent = ch3)
                    createNode(uuid = "Subsection XIX", nodeSource = nodeSource, parent = secG)
                    createNode(uuid = "Subsection XX", nodeSource = nodeSource, parent = secG)
                    createNode(uuid = "Subsection XXI", nodeSource = nodeSource, parent = secG)
                val secH = createNode(uuid = "Section H", nodeSource = nodeSource, parent = ch3)
                    createNode(uuid = "Subsection XXII", nodeSource = nodeSource, parent = secH)
                    createNode(uuid = "Subsection XXIII", nodeSource = nodeSource, parent = secH)
                    createNode(uuid = "Subsection XXIV", nodeSource = nodeSource, parent = secH)
                val secI = createNode(uuid = "Section I", nodeSource = nodeSource, parent = ch3)
                    createNode(uuid = "Subsection XXV", nodeSource = nodeSource, parent = secI)
                    createNode(uuid = "Subsection XXVI", nodeSource = nodeSource, parent = secI)
                    createNode(uuid = "Subsection XXVII", nodeSource = nodeSource, parent = secI)
        
        val reference = toNodeReference(manuscript, nodeSource)
        val string = reference.toString(nodeSource)
        Assert.assertEquals("Unexpected string representation of node references", expected, string)
    }

    private fun createNode(uuid: String = UUID().value, title: String = uuid, nodeSource: DummyNodeDB, parent: Node? ): Node{
        val node = Node( UUID(uuid), title, UUID().value, UUID().value, UUID().value, UUID().value, UUID().value )
        nodeSource.storeNode(node)
        if( parent != null ) parent.addChild(node.uuid)
        return node
    }
}