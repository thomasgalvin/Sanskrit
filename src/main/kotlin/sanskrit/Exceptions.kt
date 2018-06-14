package sanskrit

class UnsupportedLocationException( val location: ProjectLocation? = null, error: String = "Unsupported location type" ): RuntimeException(error)