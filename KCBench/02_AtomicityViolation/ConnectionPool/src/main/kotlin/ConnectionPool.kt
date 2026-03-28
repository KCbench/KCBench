import kotlinx.coroutines.*
import kotlin.random.Random

data class Connection(
    val connectionId: String,
    val host: String,
    val port: Int,
    var status: ConnectionStatus,
    var inUse: Boolean = false
)

enum class ConnectionStatus {
    IDLE, ACTIVE, CLOSING, CLOSED
}

class ConnectionPool {
    private val connections = mutableMapOf<String, Connection>()
    private val hosts = listOf(
        "db1.example.com", "db2.example.com", "db3.example.com",
        "cache1.example.com", "cache2.example.com"
    )
    private var activeConnections = 0
    private var maxConnections = 20
    
    init {
        initializeConnections()
    }
    
    private fun initializeConnections() {
        hosts.forEach { host ->
            repeat(4) { index ->
                val connection = Connection(
                    connectionId = "CONN_${Random.nextInt(1000, 9999)}",
                    host = host,
                    port = Random.nextInt(3000, 9000),
                    status = ConnectionStatus.IDLE,
                    inUse = false
                )
                connections[connection.connectionId] = connection
            }
        }
    }
    
    suspend fun acquireConnection(
        host: String? = null
    ): Connection? {
        val availableConnections = connections.values.filter { 
            it.status == ConnectionStatus.IDLE && !it.inUse 
        }
        
        val targetConnections = if (host != null) {
            availableConnections.filter { it.host == host }
        } else {
            availableConnections
        }
        
        if (targetConnections.isNotEmpty()) {
            val connection = targetConnections.random()
            
            if (connection.status == ConnectionStatus.IDLE) {
                delay(Random.nextLong(1, 10))
                
                connection.status = ConnectionStatus.ACTIVE
                connection.inUse = true
                delay(Random.nextLong(1, 5))
                
                val currentActive = activeConnections
                delay(Random.nextLong(1, 5))
                activeConnections = currentActive + 1
                
                return connection
            }
        }
        
        return null
    }
    
    suspend fun releaseConnection(connectionId: String): Boolean {
        val connection = connections[connectionId] ?: return false
        
        if (connection.status == ConnectionStatus.ACTIVE) {
            delay(Random.nextLong(1, 10))
            
            connection.status = ConnectionStatus.IDLE
            connection.inUse = false
            delay(Random.nextLong(1, 5))
            
            val currentActive = activeConnections
            delay(Random.nextLong(1, 5))
            activeConnections = maxOf(0, currentActive - 1)
            
            return true
        }
        
        return false
    }
    
    suspend fun closeConnection(connectionId: String): Boolean {
        val connection = connections[connectionId] ?: return false
        
        if (connection.status != ConnectionStatus.CLOSED) {
            delay(Random.nextLong(1, 10))
            
            connection.status = ConnectionStatus.CLOSING
            delay(Random.nextLong(1, 5))
            
            connection.status = ConnectionStatus.CLOSED
            connection.inUse = false
            delay(Random.nextLong(1, 5))
            
            val currentActive = activeConnections
            delay(Random.nextLong(1, 5))
            activeConnections = maxOf(0, currentActive - 1)
            
            return true
        }
        
        return false
    }
    
    suspend fun acquireMultipleConnections(
        count: Int,
        host: String? = null
    ): List<Connection> {
        val acquiredConnections = mutableListOf<Connection>()
        
        repeat(count) {
            val connection = acquireConnection(host)
            if (connection != null) {
                acquiredConnections.add(connection)
            }
        }
        
        return acquiredConnections
    }
    
    fun getAvailableConnections(): List<Connection> {
        return connections.values.filter { 
            it.status == ConnectionStatus.IDLE && !it.inUse 
        }
    }
    
    fun getActiveConnections(): List<Connection> {
        return connections.values.filter { it.status == ConnectionStatus.ACTIVE }
    }
    
    fun getAllConnections() = connections.values.toList()
    
    fun getHosts() = hosts
    
    fun getStatistics(): Pair<Int, Int> {
        return Pair(activeConnections, maxConnections)
    }
}

class ConnectionUser(
    private val pool: ConnectionPool,
    private val userId: String
) {
    suspend fun useConnection(): Boolean {
        val connection = pool.acquireConnection()
        
        if (connection != null) {
            delay(Random.nextLong(50, 200))
            
            pool.releaseConnection(connection.connectionId)
            return true
        }
        
        return false
    }
    
    suspend fun useMultipleConnections(count: Int): Int {
        val connections = pool.acquireMultipleConnections(count)
        
        delay(Random.nextLong(100, 300))
        
        connections.forEach { connection ->
            pool.releaseConnection(connection.connectionId)
        }
        
        return connections.size
    }
}

suspend fun simulateConnectionUsage(
    user: ConnectionUser,
    userId: Int
) {
    repeat(15) { attempt ->
        val connectionsNeeded = Random.nextInt(1, 4)
        val used = user.useMultipleConnections(connectionsNeeded)
        
        if (used > 0) {
            println("User $userId used $used connections")
        }
        
        delay(Random.nextLong(20, 80))
    }
}

suspend fun simulateHostSpecificConnections(
    pool: ConnectionPool
) {
    val hostList = pool.getHosts()
    repeat(12) { attempt ->
        val host = hostList.random()
        val connection = pool.acquireConnection(host)
        
        if (connection != null) {
            delay(Random.nextLong(100, 300))
            
            pool.releaseConnection(connection.connectionId)
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateConnectionCleanup(
    pool: ConnectionPool
) {
    repeat(8) { attempt ->
        val activeConnections = pool.getActiveConnections()
        
        if (activeConnections.isNotEmpty()) {
            val connection = activeConnections.random()
            
            if (Random.nextBoolean()) {
                pool.releaseConnection(connection.connectionId)
            } else {
                pool.closeConnection(connection.connectionId)
            }
        }
        
        delay(Random.nextLong(200, 500))
    }
}

fun main() = runBlocking {
    val pool = ConnectionPool()
    
    println("Starting Connection Pool Simulation...")
    println("Initial Available Connections: ${pool.getAvailableConnections().size}")
    println()
    
    val jobs = mutableListOf<Job>()
    
    val users = listOf(
        ConnectionUser(pool, "Alice"),
        ConnectionUser(pool, "Bob"),
        ConnectionUser(pool, "Charlie"),
        ConnectionUser(pool, "David"),
        ConnectionUser(pool, "Eve")
    )
    
    users.forEachIndexed { index, user ->
        jobs.add(launch {
            simulateConnectionUsage(user, index)
        })
    }
    
    jobs.add(launch {
        simulateHostSpecificConnections(pool)
    })
    
    jobs.add(launch {
        simulateConnectionCleanup(pool)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (activeConnections, maxConnections) = pool.getStatistics()
    
    println("\n=== Connection Pool Statistics ===")
    println("Active Connections: $activeConnections")
    println("Max Connections: $maxConnections")
    
    val available = pool.getAvailableConnections().size
    val active = pool.getActiveConnections().size
    val closing = pool.getAllConnections().count { it.status == ConnectionStatus.CLOSING }
    val closed = pool.getAllConnections().count { it.status == ConnectionStatus.CLOSED }
    
    println("\nConnection Status:")
    println("  Available: $available")
    println("  Active: $active")
    println("  Closing: $closing")
    println("  Closed: $closed")
    
    val overUsed = pool.getActiveConnections()
    
    if (overUsed.size > 10) {
        println("\n⚠️  Many active connections: ${overUsed.size}")
    }
    
    val utilizationRate = if (maxConnections > 0) {
        (activeConnections.toDouble() / maxConnections * 100)
    } else {
        0.0
    }
    
    println("\nUtilization Rate: ${"%.2f".format(utilizationRate)}%")
}