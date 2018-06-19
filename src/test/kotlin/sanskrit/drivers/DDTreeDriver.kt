package sanskrit.drivers

import sanskrit.ui.ApplicationWindow
import javax.swing.ScrollPaneConstants
import javax.swing.JScrollPane
import javax.swing.tree.DefaultTreeModel
import sanskrit.ui.DraggableTreeNode
import sanskrit.ui.DragAndDropTree


fun main( args: Array<String> ){
    val tree = DragAndDropTree()
    tree.isEditable = true

    val root = DraggableTreeNode("Project", false, true, true)
    val set1 = DraggableTreeNode("Draft", false, true)
    val set2 = DraggableTreeNode("Research", false, true)
    val set3 = DraggableTreeNode("Trash", false, true)
    set1.add(DraggableTreeNode("Chapter 01"))
    set1.add(DraggableTreeNode("Chapter 02"))
    set1.add(DraggableTreeNode("Chapter 03"))
    set2.add(DraggableTreeNode("Characters"))
    set2.add(DraggableTreeNode("Locations"))
    set2.add(DraggableTreeNode("Outline"))
    set3.add(DraggableTreeNode("Chapter One"))
    set3.add(DraggableTreeNode("Chapter Two"))
    root.add(set1)
    root.add(set2)
    root.add(set3)
    val mod = DefaultTreeModel(root)
    tree.model = mod

    // expand all
    for (i in 0 until tree.rowCount) {
        tree.expandRow(i)
    }

    // show tree
    val scroller = JScrollPane(tree,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)

    val window = ApplicationWindow("DnD JTree")
    window.contentPane.add(scroller)
    window.pack()
    window.isVisible = true
}