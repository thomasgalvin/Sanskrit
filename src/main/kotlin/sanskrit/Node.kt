package sanskrit

class Node(
        val uuid: UUID = UUID(),
        title: String,
        subtitle: String,
        manuscript: String,
        description: String,
        summary: String,
        notes: String,
        children: List<UUID> = listOf(),
        contributors: List<Contributor> = listOf()
){
    var modified: Boolean = false

    var title: String = title
        set(value){
            if( value != this.title ){
                field = value
                notifyListeners(titleChanged = true)
            }
        }

    var subtitle: String = subtitle
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

    private val _children: MutableList<UUID> = children.toMutableList()
    val children: List<UUID> get() = _children.toList()

    fun addChild(child: UUID ){
        _children.add(child)
        notifyListeners(childrenChanged = true)
    }

    fun addChild(index: Int, child: UUID ){
        _children.remove(child)
        _children.add(index, child)
        notifyListeners(childrenChanged = true)
    }

    fun removeChild(child: UUID ){
        if( _children.contains(child) ){
            _children.remove(child)
            notifyListeners(childrenChanged = true)
        }
    }

    fun removeChild(index: Int ){
        _children.removeAt(index)
        notifyListeners(childrenChanged = true)
    }

    private val _contributors: MutableList<Contributor> = contributors.toMutableList()
    val contributors: List<Contributor> get() = _contributors.toList()

    fun addContributor(contributor: Contributor ){
        _contributors.add(contributor)
        notifyListeners(contributorsChanged = true)
    }

    fun removeContributor(contributor: Contributor ){
        _contributors.remove(contributor)
        notifyListeners(contributorsChanged = true)
    }

    fun clearContributors(){
        _contributors.clear()
        notifyListeners(contributorsChanged = true)
    }


    private val listeners: MutableList<NodeListener> = mutableListOf()
    fun addListener( listener: NodeListener ) = listeners.add(listener)
    fun removeListener( listener: NodeListener ) = listeners.remove(listener)

    private fun notifyListeners(
            titleChanged: Boolean = false,
            subtitleChanged: Boolean = false,
            manuscriptChanged: Boolean = false,
            descriptionChanged: Boolean = false,
            summaryChanged: Boolean = false,
            notesChanged: Boolean = false,
            childrenChanged: Boolean = false,
            contributorsChanged: Boolean = false
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
            if( contributorsChanged ) listener.contributorsChanged(this)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (uuid != other.uuid) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (manuscript != other.manuscript) return false
        if (description != other.description) return false
        if (summary != other.summary) return false
        if (notes != other.notes) return false
        if (_children != other._children) return false
        if (_contributors != other._contributors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + subtitle.hashCode()
        result = 31 * result + manuscript.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + summary.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + _children.hashCode()
        result = 31 * result + _contributors.hashCode()
        return result
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
    fun contributorsChanged(node: Node)
}

