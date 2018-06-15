package sanskrit

class DummyNodeDB: NodeDB{
    private val map = mutableMapOf<UUID, Node>()

    override fun storeNode( node: Node ){ map[node.uuid] = node }
    override fun getNode(uuid: UUID): Node = map[uuid] ?: throw NodeNotFoundException(uuid)

}