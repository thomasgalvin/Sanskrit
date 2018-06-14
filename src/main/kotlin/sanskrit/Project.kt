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
        var uuid: UUID = UUID(),
        var title: String,
        val strings: Strings = Strings()
){
    val manuscript: Node = emptyNode( strings.manuscript )
    val research: Node = emptyNode( strings.research )
    val resources: Node = emptyNode( strings.resources )
    val trash: Node = emptyNode( strings.trash )
}


