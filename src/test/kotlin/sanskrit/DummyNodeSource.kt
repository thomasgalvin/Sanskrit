package sanskrit

class DummyNodeSource: NodeDB{
    private val map = mutableMapOf<UUID, Node>()

    fun add( node: Node ){ map[node.uuid] = node }
    override fun getNode(uuid: UUID): Node? = map[uuid]

}