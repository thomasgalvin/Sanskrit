package sanskrit

import java.io.File
import java.util.*

interface ProjectLocation

data class ProjectLocationFile(val location: File): ProjectLocation

interface ProjectIO{
    fun load( location: ProjectLocation ): Project
    fun save( project: Project, location: ProjectLocation )
}

class Project(
        var uuid: String = uuid(),
        var title: String,
        val strings: Strings = Strings()
){
    val manuscript: Node = emptyNode( strings.manuscript )
    val research: Node = emptyNode( strings.research )
    val resources: Node = emptyNode( strings.resources )
    val trash: Node = emptyNode( strings.trash )
}

class Node(
        val uuid: String = uuid(),
        title: String,
        manuscript: String,
        description: String,
        summary: String,
        notes: String
){
    var title: String = title
        set(value){
            if( value != this.title ){
                field = value
                notifyListeners(titleChanged = true)
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


    private val children: MutableList<Node> = mutableListOf()

    operator fun get(index: Int) = children[index]

    fun add( child: Node ){
        children.add(child)
        notifyListeners(childrenChanged = true)
    }

    fun add( index: Int, child: Node ){
        children.remove(child)
        children.add(index, child)
        notifyListeners(childrenChanged = true)
    }

    fun remove( child: Node ){
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
    fun manuscriptChanged(node: Node)
    fun descriptionChanged(node: Node)
    fun summaryChanged(node: Node)
    fun notesChanged(node: Node)
    fun childrenChanged(node: Node)
}

fun emptyNode( name: String ): Node = Node( uuid = name, title = name, manuscript = "", description = "", summary = "", notes = "" )

fun uuid(): String = UUID.randomUUID().toString()