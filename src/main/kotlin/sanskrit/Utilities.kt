package sanskrit

import org.slf4j.LoggerFactory
import java.io.IOException

class Utilities{
    companion object {
        private val logger = LoggerFactory.getLogger(Utilities::class.java!!)

        fun loadFromClasspath( classpathEntry: String ): String {
            if( logger.isTraceEnabled )logger.trace("Loading text from classpath: $classpathEntry")
            val resource = Utilities::class.java.getResource(classpathEntry) ?: throw IOException( "Unable to load SQL: $classpathEntry" )

            val result = resource.readText()
            if( isBlank(result) ) throw IOException( "Loaded empty classpath entry: $classpathEntry" )
            if( logger.isTraceEnabled )logger.trace("Loaded classpath entry $classpathEntry:\n$result")

            return result
        }

        fun isBlank( string: String? ) = string == null || string.isBlank()
    }
}

