package sanskrit

import java.util.*

class Node(
        val uuid: UUID = UUID(),
        title: String,
        subtitle: String,
        manuscript: String,
        description: String,
        summary: String,
        notes: String,
        children: List<UUID> = listOf()
){
    var title: String = title
        set(value){
            if( value != this.title ){
                field = value
                notifyListeners(titleChanged = true)
            }
        }

    var subtitle: String = title
        set(value){
            if( value != this.subtitle ){
                field = value
                notifyListeners(subtitleChanged = true)
            }
        }

    var manuscript: String = manuscript
        set(value){
            if( value != this.manuscript ){
                field = value
                notifyListeners(manuscriptChanged = true)
            }
        }

    var description: String = description
        set(value){
            if( value != this.description ){
                field = value
                notifyListeners(descriptionChanged = true)
            }
        }

    var summary: String = summary
        set(value){
            if( value != this.summary ){
                field = value
                notifyListeners(summaryChanged = true)
            }
        }

    var notes: String = notes
        set(value){
            if( value != this.notes ){
                field = value
                notifyListeners(notesChanged = true)
            }
        }

    var modified: Boolean = false


    private val children: MutableList<UUID> = children.toMutableList()

    operator fun get(index: Int) = children[index]

    fun add( child: UUID ){
        children.add(child)
        notifyListeners(childrenChanged = true)
    }

    fun add( index: Int, child: UUID ){
        children.remove(child)
        children.add(index, child)
        notifyListeners(childrenChanged = true)
    }

    fun remove( child: UUID ){
        if( children.contains(child) ){
            children.remove(child)
            notifyListeners(childrenChanged = true)
        }
    }

    fun remove( index: Int ){
        children.removeAt(index)
        notifyListeners(childrenChanged = true)
    }

    private val listeners: MutableList<NodeListener> = mutableListOf()
    fun addListener( listener: NodeListener ) = listeners.add(listener)
    fun removeListener( listener: NodeListener ) = listeners.remove(listener)

    fun notifyListeners(
            titleChanged: Boolean = false,
            subtitleChanged: Boolean = false,
            manuscriptChanged: Boolean = false,
            descriptionChanged: Boolean = false,
            summaryChanged: Boolean = false,
            notesChanged: Boolean = false,
            childrenChanged: Boolean = false
    ){
        modified = true

        val list = listeners.toMutableList()
        list.reverse()

        for( listener in list ){
            if( titleChanged ) listener.titleChanged(this)
            if( subtitleChanged ) listener.subtitleChanged(this)
            if( manuscriptChanged ) listener.manuscriptChanged(this)
            if( descriptionChanged ) listener.descriptionChanged(this)
            if( summaryChanged ) listener.summaryChanged(this)
            if( notesChanged ) listener.notesChanged(this)
            if( childrenChanged ) listener.childrenChanged(this)
        }
    }
}

interface NodeListener{
    fun titleChanged(node: Node)
    fun subtitleChanged(node: Node)
    fun manuscriptChanged(node: Node)
    fun descriptionChanged(node: Node)
    fun summaryChanged(node: Node)
    fun notesChanged(node: Node)
    fun childrenChanged(node: Node)
}

fun emptyNode( name: String ): Node = Node( uuid = UUID(name), title = name, subtitle = "", manuscript = "", description = "", summary = "", notes = "" )
