package sanskrit

import org.slf4j.LoggerFactory
import sanskrit.Utilities.Companion.loadFromClasspath
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class SanskritDB(
        private val connectionManager: ConnectionManager,
        private val sqlClasspath: String
){
    private val logger = LoggerFactory.getLogger(SanskritDB::class.java)
    private val concurrencyLock = Object()

    private val nodeExists = loadFromClasspath("$sqlClasspath/nodeExists.sql")
    private val storeNode = loadFromClasspath("$sqlClasspath/storeNode.sql")
    private val storeParentChild = loadFromClasspath("$sqlClasspath/storeParentChild.sql")
    private val storeContributor = loadFromClasspath("$sqlClasspath/storeContributor.sql")
    private val deleteChildren = loadFromClasspath("$sqlClasspath/deleteChildren.sql")
    private val deleteContributors = loadFromClasspath("$sqlClasspath/deleteContributors.sql")


    init{
        createTables()
    }

    private fun createTables(){
        logger.trace("Creating SanskritDB tables")
        connectionManager.execute( loadFromClasspath("$sqlClasspath/createTableNodes.sql") )
        connectionManager.execute( loadFromClasspath("$sqlClasspath/createTableChildren.sql") )
        connectionManager.execute( loadFromClasspath("$sqlClasspath/createTableContributors.sql") )
    }

    private fun conn(): Connection = connectionManager.connect()
    private fun release(conn: Connection) = connectionManager.release(conn)

    fun nodeExists(uuid: UUID): Boolean{
        synchronized(concurrencyLock) {
            val conn = conn()

            try {
                val statement = conn.prepareStatement(nodeExists)
                statement.setString(1, uuid.value)
                val resultSet = statement.executeQuery()
                if( resultSet.next() ) return resultSet.getBoolean(1)
                return false
            }
            finally{
                connectionManager.release(conn)
            }
        }
    }

    fun storeNode( node: Node ){
        if(logger.isTraceEnabled) logger.trace("Storing node: ${node.uuid}")
        val exists = nodeExists(node.uuid)

        synchronized(concurrencyLock) {
            val conn = conn()

            try {
                val nodeStatement = conn.prepareStatement(storeNode)
                nodeStatement.setString(1, node.uuid.value)
                nodeStatement.setString(2, node.title)
                nodeStatement.setString(3, node.subtitle)
                nodeStatement.setString(4, node.manuscript)
                nodeStatement.setString(5, node.description)
                nodeStatement.setString(6, node.summary)
                nodeStatement.setString(7, node.notes)
                nodeStatement.executeUpdate()

                if (exists) {
                    if(logger.isTraceEnabled) logger.trace("    Node ${node.uuid} exists, deleting child relationships")
                    val deleteChildrenStatement = conn.prepareStatement(deleteChildren)
                    deleteChildrenStatement.setString(1, node.uuid.value)
                    deleteChildrenStatement.executeUpdate()

                    if(logger.isTraceEnabled) logger.trace("    Node ${node.uuid} exists, deleting contributors")
                    val deleteContributorsStatement = conn.prepareStatement(deleteContributors)
                    deleteContributorsStatement.setString(1, node.uuid.value)
                    deleteContributorsStatement.executeUpdate()
                }

                for ((index, child) in node.children.withIndex()) {
                    if(logger.isTraceEnabled) logger.trace("    Creating parent/child relationship: ${node.uuid} : $child")
                    val storeChildStatement = conn.prepareStatement(storeParentChild)
                    storeChildStatement.setString(1, node.uuid.value)
                    storeChildStatement.setString(2, child.value)
                    storeChildStatement.setInt(3, index)
                    storeChildStatement.executeUpdate()
                }

                for ((index, contributor) in node.contributors.withIndex()) {
                    if(logger.isTraceEnabled) logger.trace("    Adding contributor ${contributor.sortByName}")
                    val storeContributorStatement = conn.prepareStatement(storeContributor)
                    storeContributorStatement.setString(1, node.uuid.value)
                    storeContributorStatement.setString(2, contributor.name)
                    storeContributorStatement.setString(3, contributor.sortByName)
                    storeContributorStatement.setInt(4, contributor.role.ordinal)
                    storeContributorStatement.setInt(5, index)
                    storeContributorStatement.executeUpdate()
                }

                conn.commit()
            }
            catch(t: Throwable){
                conn.rollback()
            }
            finally{
                release(conn)
            }

        }
    }

    companion object {
        fun SQLite(maxConnections: Int, databaseFile: File, timeout: Long = 60_000): SanskritDB
                = SanskritDB( ConnectionManager.SQLite(maxConnections, databaseFile, timeout), "/sanscrit/sql/sqlite" )
    }
}

class ConnectionManager(val maxConnections: Int,
                        val connectionURL: String,
                        driverName: String,
                        val timeout: Long = 60_000,
                        val user: String? = null,
                        val password: String? = null )
{
    companion object {
        fun SQLite( maxConnections: Int, databaseFile: File, timeout: Long = 60_000 ): ConnectionManager{
            val connectionURL = "jdbc:sqlite:" + databaseFile.absolutePath
            return ConnectionManager(maxConnections, connectionURL, "org.sqlite.JDBC", timeout)
        }

        fun PostgreSQL( maxConnections: Int, connectionURL: String, timeout: Long = 60_000, username: String? = null, password: String? = null ) = ConnectionManager(maxConnections, connectionURL, "org.postgresql.Driver", timeout, username, password)
    }

    private val logger = LoggerFactory.getLogger(ConnectionManager::class.java)
    private val lock = Object()
    private var connectionCount = 0

    init{
        try {
            if(logger.isTraceEnabled) logger.trace("Loading JDBC driver: $driverName")
            Class.forName(driverName)
        } catch (t: Throwable) {
            if(logger.isDebugEnabled)logger.debug(t.message, t)
            throw DatabaseException("Error creating JDBC driver: " + driverName, t)
        }

    }

    fun connect(): Connection {
        try {
            if(logger.isTraceEnabled) logger.trace("Connecting to database")
            incrementCount()
            waitForAvailableConnection()

            val result = DriverManager.getConnection(connectionURL, user, password)
            result.autoCommit = false
            return result
        } catch (t: Throwable) {
            decrementCount()

            logger.debug(t.message, t)
            throw DatabaseException("Error establishing database connection: $connectionURL", t)
        }

    }

    fun release(conn: Connection?) {
        try {
            if(logger.isTraceEnabled) logger.trace("Releasing connection")
            if (conn != null && !conn.isClosed) {
                conn.close()
            }
        } catch (t: Throwable) {
            logger.debug(t.message, t)
            throw DatabaseException("Error returning database connection", t)
        } finally {
            decrementCount()
        }
    }

    private fun incrementCount(){
        synchronized(lock) {
            connectionCount++

            if(logger.isTraceEnabled) logger.trace("Connection count: $connectionCount")
        }
    }

    private fun decrementCount(){
        synchronized(lock) {
            connectionCount--
            if( connectionCount < 0 ) connectionCount = 0

            if(logger.isTraceEnabled) logger.trace("Connection count: $connectionCount")
        }
    }

    private fun waitForAvailableConnection(){
        val sleepFor = 25.toLong()
        var waitTime = 0.toLong()

        while (connectionCount > maxConnections) {
            try {
                if (logger.isTraceEnabled) {
                    logger.trace("Waiting for available connection: max: $maxConnections current: $connectionCount")
                }

                Thread.sleep(sleepFor)
                waitTime += sleepFor
                if( waitTime > timeout ) throw DatabaseException("Timeout waiting for database connection: $connectionURL")
            } catch (t: Throwable) {
                logger.trace(t.message)
            }

        }
    }

    fun execute( sql: String ){
        if(logger.isDebugEnabled) logger.debug("Executing SQL:\n$sql")
        val conn = connect()

        try {
            if(logger.isTraceEnabled) logger.trace("    Creating statement")
            val statement = conn.prepareStatement(sql)

            if(logger.isTraceEnabled) logger.trace("    Executing update")
            statement.executeUpdate()

            if(logger.isTraceEnabled) logger.trace("    Closing statement")
            statement.close()

            if(logger.isTraceEnabled) logger.trace("    Committing changes")
            conn.commit()

            if(logger.isTraceEnabled) logger.trace("    Execution complete")
        }
        catch(t: Throwable){
            if(logger.isTraceEnabled) logger.trace("    Rolling back changes")
            conn.rollback()
        }
        finally{
            if( !conn.isClosed ) {
                if(logger.isTraceEnabled) logger.trace("    Closing statement")
                conn.close()
            }

            if(logger.isTraceEnabled) logger.trace("    Releasing connection")
            release(conn)
        }
    }
}
