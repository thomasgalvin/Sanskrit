package sanskrit

class UnsupportedLocationTypeException( val location: ProjectLocation? = null, error: String = "Unsupported location type" ): RuntimeException(error)
class NodeNotFoundException( val uuid: UUID ): RuntimeException("Unable to locate node with UUID $uuid")