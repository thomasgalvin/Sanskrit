package sanskrit

class DummyNodeSource: NodeSource{
    private val map = mutableMapOf<UUID, Node>()

    fun add( node: Node ){ map[node.uuid] = node }
    override fun getNode(uuid: UUID): Node? = map[uuid]

}