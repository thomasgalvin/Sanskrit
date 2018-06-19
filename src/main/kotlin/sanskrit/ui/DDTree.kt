package sanskrit.ui

import org.slf4j.LoggerFactory
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import javax.swing.JTree
import javax.swing.tree.*
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.DefaultMutableTreeNode
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DragGestureEvent
import java.awt.dnd.DragSourceDragEvent
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DragSourceDropEvent
import java.awt.*
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DragSourceEvent
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException
import java.awt.datatransfer.Transferable

class DraggableTreeNode(userObject: Any,
                        val draggable: Boolean = true,
                        val allowsMoreChildren: Boolean = true,
                        val canBeReparented: Boolean = true )
    : DefaultMutableTreeNode(userObject)
{
    override fun add(newChild: MutableTreeNode?) {
        if( newChild is DraggableTreeNode) super.add(newChild)
        else throw IllegalArgumentException( "Node $newChild is not of type DraggableTreeNode." )
    }

    override fun insert(newChild: MutableTreeNode?, childIndex: Int) {
        if( newChild is DraggableTreeNode) super.insert(newChild, childIndex)
        else throw IllegalArgumentException( "Node $newChild is not of type DraggableTreeNode." )
    }
}

class DraggableTreeCellRenderer( val dragAndDropTree: DragAndDropTree,
                                 val dropIndicatorColor: Color = Color.BLACK ): DefaultTreeCellRenderer()
{
    private val normalInsets = super.getInsets()
    private var isTargetNode: Boolean = false

    override fun getTreeCellRendererComponent( tree: JTree?,
                                               value: Any?,
                                               sel: Boolean,
                                               expanded: Boolean,
                                               leaf: Boolean,
                                               row: Int,
                                               hasFocus: Boolean): Component
    {
        if( tree == dragAndDropTree ){
            isTargetNode = value == dragAndDropTree.getDropTargetNode()
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        }
        else{
            throw IllegalArgumentException("Expeced tree: $dragAndDropTree but found $tree")
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if( g == null ) return

        if( isTargetNode ){
            g.color = dropIndicatorColor
            if( dragAndDropTree.nodeWillBeInsertedAbove() )
            {
                g.drawLine( 0, 0, size.width, 0 )
            }
            else if( dragAndDropTree.nodeWillBeInsertedBelow() )
            {
                g.drawLine( 0, size.height - 1, size.width, size.height
                        - 1 )
            }
            else
            {
                g.drawRect( 0, 0, size.width - 1, size.height - 1 )
            }
        }
    }
}

class DragAndDropTree( val rootNode: DraggableTreeNode = DraggableTreeNode("Root") )
    : JTree(rootNode), DragSourceListener, DropTargetListener, DragGestureListener
{
    private val logger = LoggerFactory.getLogger(DragAndDropTree::class.java)
    private val PIXELS_ABOVE_AND_BELOW = 5
    private val supportedFlavors = arrayOf( DataFlavor( DataFlavor.javaJVMLocalObjectMimeType ) )
    private val dragSource = DragSource()
    private val dropTarget: DropTarget
    private var dropTargetNode: DraggableTreeNode? = null
    private var dropTargetPath: TreePath? = null
    private var nodeWillBeInsertedAbove: Boolean = false
    private var nodeWillBeInsertedBelow: Boolean = false
    private var draggedObjects: MutableList<Any> = mutableListOf()

    override fun getDropTarget(): DropTarget = dropTarget

    init{
        setCellRenderer( DraggableTreeCellRenderer(this) )
        setModel( DefaultTreeModel( rootNode ) )

        dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY_OR_MOVE, this )
        dropTarget = DropTarget(this, this)
    }

    fun getSelectedNodes(): List<DefaultMutableTreeNode>{
        val result = mutableListOf<DefaultMutableTreeNode>()

        val selectedPaths = selectionPaths
        if( selectedPaths != null ){
            for( treePath in selectedPaths ){
                val lastComponent = treePath.lastPathComponent
                if( lastComponent is DefaultMutableTreeNode ){
                    result.add(lastComponent)
                }
            }
        }

        return result
    }

    fun select( node: DefaultMutableTreeNode ){
        val model = model as DefaultTreeModel

        if( node.parent != null ) {
            val parentNodes = model.getPathToRoot(node.parent)
            val parentPath = TreePath(parentNodes)
            expandPath(parentPath)
        }

        val treeNodes = model.getPathToRoot(node)
        val treePath = TreePath(treeNodes)
        selectionPath = treePath
    }

    fun getPath(node: DefaultMutableTreeNode): Array<TreeNode>{
        val model = model as DefaultTreeModel
        return  model.getPathToRoot(node)
    }

    fun isExpanded(node: DefaultMutableTreeNode): Boolean{
        val model = model as DefaultTreeModel
        val nodes = model.getPathToRoot(node)
        if( nodes != null ){
            val path = TreePath(nodes)
            return isExpanded(path)
        }

        return false
    }

    fun setExpanded(node: DefaultMutableTreeNode, expanded: Boolean){
        val model = model as DefaultTreeModel
        val nodes = model.getPathToRoot(node)
        if( nodes != null ){
            val path = TreePath(nodes)

            if(expanded) expandPath(path)
            else collapsePath(path)
        }
    }

    // Sibling / Child convenience methods

    fun addChild(target: DefaultMutableTreeNode, newNode: DefaultMutableTreeNode) {
        val model = model as DefaultTreeModel

        val index = target.childCount
        model.insertNodeInto(newNode, target, index)
    }

    fun addSibling(target: DefaultMutableTreeNode, newNode: DefaultMutableTreeNode) {
        var parent: DefaultMutableTreeNode? = target.parent as DefaultMutableTreeNode
        val model = model as DefaultTreeModel

        if (parent == null) {
            parent = target
        }

        val index = parent.childCount
        model.insertNodeInto(newNode, parent, index)
    }

    fun addSiblingAfter(target: DefaultMutableTreeNode, newNode: DefaultMutableTreeNode) {
        var parent: DefaultMutableTreeNode? = target.parent as DefaultMutableTreeNode


        val model = model as DefaultTreeModel
        var index = 0

        if (parent != null) {
            index = parent.getIndex(target)
            index++
        } else {
            parent = target
        }

        model.insertNodeInto(newNode, parent, index)
    }

    // drag and drop

    /**
     * Can be overridden by children to control drag-and-drop behavior.
     */
    fun acceptDrop(dropTargetDropEvent: DropTargetDropEvent): Boolean {
        val dragPoint = dropTargetDropEvent.location
        dropTargetPath = getDropTargetPath(dragPoint)

        val dropTargetPath = this.dropTargetPath
        if( dropTargetPath == null ) return false

        val node = dropTargetPath.lastPathComponent as DraggableTreeNode
        return if (node != null) {
            node.allowsMoreChildren
        } else true
    }

    override fun dragGestureRecognized(dragGestureEvent: DragGestureEvent) {
        logger.debug("Drag gesture recognized: $dragGestureEvent")
        draggedObjects.clear()
        val selectedPaths = selectionPaths
        if (selectedPaths != null) {
            for (selectedPath in selectedPaths) {
                val path = selectedPath.path
                val selectedObject = path[path.size - 1]
                draggedObjects.add(selectedObject)
            }

            val transferable = LocalTransferable(draggedObjects)
            dragSource.startDrag(dragGestureEvent, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), transferable, this)
        }
    }

    override fun dragEnter(dsde: DragSourceDragEvent) {}

    override fun dragEnter(dropTargetDragEvent: DropTargetDragEvent) {
        dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE)
    }

    override fun dragOver(dsde: DragSourceDragEvent) {}

    override fun dragOver(dropTargetDragEvent: DropTargetDragEvent) {
        nodeWillBeInsertedAbove = false
        nodeWillBeInsertedBelow = false

        val dragPoint = dropTargetDragEvent.location
        dropTargetPath = getDropTargetPath(dragPoint)

        var dropTargetPath = this.dropTargetPath
        var dropTargetNode = this.dropTargetNode

        if (dropTargetPath != null) {
            dropTargetNode = dropTargetPath.lastPathComponent as DraggableTreeNode

            if (dropTargetNode.allowsMoreChildren && dropTargetNode.allowsMoreChildren) {
                val nodeBounds = getPathBounds(dropTargetPath)
                val dropPoint = dropTargetDragEvent.location

                if (nodeBounds != null && dropPoint != null) {
                    if (dropPoint.y - nodeBounds.y <= PIXELS_ABOVE_AND_BELOW) {
                        nodeWillBeInsertedAbove = true
                    } else if (nodeBounds.height + nodeBounds.y - dropPoint.y <= PIXELS_ABOVE_AND_BELOW) {
                        nodeWillBeInsertedBelow = true
                    }
                }
            } else {
                dropTargetNode = null
                dropTargetPath = null
            }
        }

        repaint()
    }

    override fun dragDropEnd(dsde: DragSourceDropEvent) {
        dropTargetNode = null
        draggedObjects.clear()
        repaint()
    }

    fun getDroppedNode(dropTargetDropEvent: DropTargetDropEvent): DraggableTreeNode? {
        val result = getDroppedNodes(dropTargetDropEvent)
        return if (!result.isEmpty()) {
            result.get(0)
        } else null

    }

    fun getDroppedNodes(dropTargetDropEvent: DropTargetDropEvent): List<DraggableTreeNode> {
        val result = mutableListOf<DraggableTreeNode>()

        val dragPoint = dropTargetDropEvent.location
        dropTargetPath = getDropTargetPath(dragPoint)

        var dropTargetPath = this.dropTargetPath

        if (dropTargetPath != null) {
            val dropTargetNode = dropTargetPath.lastPathComponent as DraggableTreeNode
            if (dropTargetNode != null) {
                try {
                    val model = model as DefaultTreeModel
                    val droppedObject = dropTargetDropEvent.transferable.getTransferData(supportedFlavors[0])

                    if (droppedObject is List<*>) {
                        for (target in droppedObject) {
                            if( target != null ) {
                                val droppedNode = createDraggableTreeNode(target)
                                result.add(droppedNode)
                            }
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

            }
        }

        return result
    }

    override fun drop( dropTargetDropEvent: DropTargetDropEvent ){
        if( !acceptDrop( dropTargetDropEvent ) )
        {
            dropTargetDropEvent.rejectDrop()
            return
        }

        val dragPoint = dropTargetDropEvent.location
        dropTargetPath = getDropTargetPath( dragPoint )
        if( dropTargetPath == null ){
            dropTargetDropEvent.rejectDrop()
            return
        }

        try{
            val model = model as DefaultTreeModel
            val droppedObject = dropTargetDropEvent.transferable.getTransferData(supportedFlavors[0])

            if( droppedObject is List<*> ){
                for( target in droppedObject ){
                    if( target == null ) continue

                    val droppedNode = createDraggableTreeNode(target)
                    if( droppedNode.isNodeDescendant( dropTargetNode ) ){
                        dropTargetDropEvent.rejectDrop()
                        return
                    }

                    val dropTargetNode = this.dropTargetNode
                    if( dropTargetNode == null ){
                        dropTargetDropEvent.rejectDrop()
                        return
                    }

                    if( nodeWillBeInsertedAbove || nodeWillBeInsertedBelow ){
                        val parent = dropTargetNode.parent as DraggableTreeNode
                        if( parent == null || !parent.allowsMoreChildren || !parent.allowsChildren ){
                            dropTargetDropEvent.rejectDrop()
                            return
                        }

                        if( droppedNode.canBeReparented || droppedNode.parent == parent ){
                            if( nodeWillBeInsertedAbove ) insertNodeAbove( droppedNode, dropTargetNode )
                            else if( nodeWillBeInsertedBelow ) insertNodeBelow( droppedNode, dropTargetNode )
                        }
                        else{
                            dropTargetDropEvent.rejectDrop()
                            return
                        }
                    }
                    else{
                        if( droppedNode.canBeReparented || droppedNode.parent == dropTargetNode ){
                            insertNodeAsNewChild( droppedNode, dropTargetNode )
                        }
                        else{
                            dropTargetDropEvent.rejectDrop()
                            return
                        }
                    }

                    dropTargetDropEvent.acceptDrop( DnDConstants.ACTION_MOVE )
                    dropTargetDropEvent.dropComplete( true )
                }
            }
        }
        catch(t: Throwable){
            logger.error("Error dropping node", t)
        }
    }

    fun insertNodeAbove(droppedNode: DraggableTreeNode, dropTargetNode: DraggableTreeNode) {
        logger.debug("nsertNodeAbove")

        val parent = dropTargetNode.parent as DraggableTreeNode
        val model = model as DefaultTreeModel

        val path = TreePath(model.getPathToRoot(parent))
        val isExpanded = isExpanded(path) || parent.isLeaf

        val dropPath = TreePath(model.getPathToRoot(dropTargetNode))
        logger.debug("  dropPath: $dropPath")
        if (dropPath != null) {
            model.removeNodeFromParent(droppedNode)
        }

        val index = parent.getIndex(dropTargetNode)
        model.insertNodeInto(droppedNode, parent, index)

        if (isExpanded) {
            expandPath(path)
        }

        logger.debug("")
    }

    fun insertNodeBelow(droppedNode: DraggableTreeNode, dropTargetNode: DraggableTreeNode) {
        logger.debug("base class: insertNodeBelow")

        val parent = dropTargetNode.parent as DraggableTreeNode
        val model = model as DefaultTreeModel

        val path = TreePath(model.getPathToRoot(parent))
        val isExpanded = isExpanded(path) || parent.isLeaf

        val dropPath = TreePath(model.getPathToRoot(dropTargetNode))
        logger.debug("  dropPath: $dropPath")
        if (dropPath != null) {
            model.removeNodeFromParent(droppedNode)
        }

        val index = parent.getIndex(dropTargetNode) + 1
        model.insertNodeInto(droppedNode, parent, index)

        if (isExpanded) {
            expandPath(path)
        }

        logger.debug("")
    }

    fun insertNodeAsNewChild(droppedNode: DraggableTreeNode, dropTargetNode: DraggableTreeNode) {
        logger.debug("base class: insertNodeAsNewChild")

        val model = model as DefaultTreeModel

        val path = TreePath(model.getPathToRoot(dropTargetNode))
        val isExpanded = isExpanded(path) || dropTargetNode.isLeaf

        val dropPath = TreePath(model.getPathToRoot(dropTargetNode))
        logger.debug("  dropPath: $dropPath")
        if (dropPath != null) {
            model.removeNodeFromParent(droppedNode)
        }

        model.insertNodeInto(droppedNode, dropTargetNode, dropTargetNode.childCount)

        if (isExpanded) {
            expandPath(path)
        }

        logger.debug("")
    }

    override fun dragExit(dse: DragSourceEvent) {}

    override fun dragExit(dte: DropTargetEvent) = repaint()

    override fun dropActionChanged(dsde: DragSourceDragEvent) {}

    override fun dropActionChanged(dtde: DropTargetDragEvent) {}

    fun getDropTargetPath(point: Point): TreePath? {
        var result = getPathForLocation(point.x, point.y)
        if (result == null) {
            dropTargetNode = null

            var y = point.y
            while (y >= 0 && result == null) {
                y--
                result = getPathForLocation(point.x, y)
            }
        }

        return result
    }

    private fun createDraggableTreeNode(target: Any): DraggableTreeNode {
        return target as? DraggableTreeNode ?: DraggableTreeNode(target)
    }

    fun nodeWillBeInsertedAbove(): Boolean {
        return nodeWillBeInsertedAbove
    }

    fun nodeWillBeInsertedBelow(): Boolean {
        return nodeWillBeInsertedBelow
    }

    fun getDropTargetNode(): DraggableTreeNode? {
        return dropTargetNode
    }
}

internal class LocalTransferable(private val payload: Any) : Transferable {
    private val supportedFlavors = arrayOf( DataFlavor( DataFlavor.javaJVMLocalObjectMimeType ) )

    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return supportedFlavors
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        for (supportedFlavor in supportedFlavors) {
            if (supportedFlavor.equals(flavor)) {
                return true
            }
        }
        return false
    }

    @Throws(UnsupportedFlavorException::class, IOException::class)
    override fun getTransferData(flavor: DataFlavor): Any {
        if (isDataFlavorSupported(flavor)) {
            return payload
        }

        throw UnsupportedFlavorException(flavor)
    }
}