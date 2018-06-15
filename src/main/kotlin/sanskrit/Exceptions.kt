package sanskrit

class UnsupportedLocationTypeException( val location: ProjectLocation? = null, error: String = "Unsupported location type" ): RuntimeException(error)

class NodeNotFoundException( val uuid: UUID ): RuntimeException("Unable to locate node with UUID $uuid")

class DatabaseException :  RuntimeException {
    constructor(message: String, ex: Throwable?): super(message, ex)
    constructor(message: String): super(message)
    constructor(ex: Exception): super(ex)
}