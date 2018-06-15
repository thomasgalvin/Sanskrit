package sanskrit

import java.io.File

interface ProjectLocation
data class ProjectLocationFile(val location: File): ProjectLocation

interface NodeDB{
    fun getNode( uuid: UUID ): Node?
}

data class NodeReference( val uuid: UUID, val children: List<NodeReference> ){
    fun toString(nodeDB: NodeDB): String {
        val builder = StringBuilder()
        toString(builder, 0, nodeDB)
        return builder.toString().trim()
    }

    private fun toString(builder: StringBuilder, indentLevel: Int, nodeDB: NodeDB ){
        val node = nodeDB.getNode(uuid)
        if( node != null ) builder.append( "${getIndent(indentLevel)}${node.title}\n" )
        for(child in children) child.toString(builder, indentLevel + 1, nodeDB)
    }

    private fun getIndent(indentLevel: Int): String{
        val builder = StringBuilder( indentLevel * 2 )
        for( i in 0 until indentLevel ) builder.append("  ")
        return builder.toString()
    }
}

fun toNodeReference(node: Node, nodeDB: NodeDB ): NodeReference{
    val childReferences = mutableListOf<NodeReference>()

    for( childUUID in node.children ){
        val child = nodeDB.getNode(childUUID)
        if( child != null ){
            val childReference = toNodeReference(child, nodeDB)
            childReferences.add(childReference)
        }
    }

    return NodeReference(node.uuid, childReferences)
}
