package sanskrit

import java.io.File
import java.util.*

interface ProjectLocation
data class ProjectLocationFile(val location: File): ProjectLocation

interface NodeSource{
    fun getNode( uuid: UUID ): Node?
}

data class NodeReference( val uuid: UUID, val children: List<NodeReference> ){
    fun toString(nodeSource: NodeSource): String {
        val builder = StringBuilder()
        toString(builder, 0, nodeSource)
        return builder.toString().trim()
    }

    private fun toString( builder: StringBuilder, indentLevel: Int, nodeSource: NodeSource ){
        val node = nodeSource.getNode(uuid)
        if( node != null ) builder.append( "${getIndent(indentLevel)}${node.title}\n" )
        for(child in children) child.toString(builder, indentLevel + 1, nodeSource)
    }

    private fun getIndent(indentLevel: Int): String{
        val builder = StringBuilder( indentLevel * 2 )
        for( i in 0 until indentLevel ) builder.append("  ")
        return builder.toString()
    }
}

class ProjectStructure(
        val manuscript: NodeReference,
        val research: NodeReference,
        val resources: NodeReference,
        val trash: NodeReference
)

fun toNodeReference( node: Node, source: NodeSource ): NodeReference{
    val childReferences = mutableListOf<NodeReference>()

    for( childUUID in node.children ){
        val child = source.getNode(childUUID)
        if( child != null ){
            val childReference = toNodeReference(child, source)
            childReferences.add(childReference)
        }
    }

    return NodeReference(node.uuid, childReferences)
}


//class SqliteProject( private val strings: Strings = Strings(),
//                     val location: ProjectLocation )
//{
//    init{
//        if( location !is ProjectLocationFile ) throw UnsupportedLocationException(location)
//    }
//
//    private val nodes: MutableMap<UUID, Node> = mutableMapOf()
//
//    val manuscript: Node = emptyNode( strings.manuscript )
//    val research: Node = emptyNode( strings.research )
//    val resources: Node = emptyNode( strings.resources )
//    val trash: Node = emptyNode( strings.trash )
//}
//
//fun emptyNode( name: String ): Node = Node( uuid = UUID(name), title = name, subtitle = "", manuscript = "", description = "", summary = "", notes = "" )