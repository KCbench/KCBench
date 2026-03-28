import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class NetworkConnection(
    val connectionId: String,
    val remoteAddress: String,
    var inUse: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class Socket(
    val socketId: String,
    val port: Int,
    var bound: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

class NetworkManager {
    private val connections = mutableMapOf<String, NetworkConnection>()
    private val sockets = mutableMapOf<String, Socket>()
    private val connectionPoolMutex = Mutex()
    private val socketPoolMutex = Mutex()
    
    init {
        initializeConnections()
        initializeSockets()
    }
    
    private fun initializeConnections() {
        val connectionConfigs = listOf(
            Pair("CONN001", "192.168.1.1:8080"),
            Pair("CONN002", "192.168.1.2:8080"),
            Pair("CONN003", "192.168.1.3:8080"),
            Pair("CONN004", "192.168.1.4:8080"),
            Pair("CONN005", "192.168.1.5:8080"),
            Pair("CONN006", "192.168.1.6:8080"),
            Pair("CONN007", "192.168.1.7:8080"),
            Pair("CONN008", "192.168.1.8:8080"),
            Pair("CONN009", "192.168.1.9:8080"),
            Pair("CONN010", "192.168.1.10:8080")
        )
        
        connectionConfigs.forEach { (connectionId, remoteAddress) ->
            connections[connectionId] = NetworkConnection(
                connectionId = connectionId,
                remoteAddress = remoteAddress,
                inUse = false,
                locked = false
            )
        }
    }
    
    private fun initializeSockets() {
        val socketConfigs = listOf(
            Pair("SOCK001", 8080),
            Pair("SOCK002", 8081),
            Pair("SOCK003", 8082),
            Pair("SOCK004", 8083),
            Pair("SOCK005", 8084),
            Pair("SOCK006", 8085),
            Pair("SOCK007", 8086),
            Pair("SOCK008", 8087),
            Pair("SOCK009", 8088),
            Pair("SOCK010", 8089)
        )
        
        socketConfigs.forEach { (socketId, port) ->
            sockets[socketId] = Socket(
                socketId = socketId,
                port = port,
                bound = false,
                locked = false
            )
        }
    }
    
    suspend fun acquireConnection(connectionId: String): Boolean {
        val connection = connections[connectionId] ?: return false
        
        if (connection.inUse) {
            return false
        }
        
        connectionPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (connection.inUse) {
                return false
            }
            
            connection.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                connection.inUse = true
                connection.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun releaseConnection(connectionId: String): Boolean {
        val connection = connections[connectionId] ?: return false
        
        if (!connection.inUse) {
            return false
        }
        
        connection.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            connectionPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                connection.inUse = false
                connection.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun bindSocket(socketId: String): Boolean {
        val socket = sockets[socketId] ?: return false
        
        if (socket.bound) {
            return false
        }
        
        socketPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (socket.bound) {
                return false
            }
            
            socket.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                socket.bound = true
                socket.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun unbindSocket(socketId: String): Boolean {
        val socket = sockets[socketId] ?: return false
        
        if (!socket.bound) {
            return false
        }
        
        socket.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            socketPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                socket.bound = false
                socket.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun sendDataWithSocket(
        connectionId: String,
        socketId: String
    ): Boolean {
        val connection = connections[connectionId] ?: return false
        val socket = sockets[socketId] ?: return false
        
        if (!connection.inUse || !socket.bound) {
            return false
        }
        
        connection.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            socket.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                delay(Random.nextLong(20, 50))
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferConnection(
        fromConnectionId: String,
        toConnectionId: String
    ): Boolean {
        val fromConnection = connections[fromConnectionId]
        val toConnection = connections[toConnectionId]
        
        if (fromConnection == null || toConnection == null) {
            return false
        }
        
        if (!fromConnection.inUse || toConnection.inUse) {
            return false
        }
        
        fromConnection.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toConnection.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromConnection.inUse = false
                fromConnection.locked = false
                toConnection.inUse = true
                toConnection.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapConnections(
        connectionId1: String,
        connectionId2: String
    ): Boolean {
        val connection1 = connections[connectionId1]
        val connection2 = connections[connectionId2]
        
        if (connection1 == null || connection2 == null) {
            return false
        }
        
        if (!connection1.inUse || !connection2.inUse) {
            return false
        }
        
        connection1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            connection2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempInUse = connection1.inUse
                val tempLocked = connection1.locked
                
                connection1.inUse = connection2.inUse
                connection1.locked = connection2.locked
                connection2.inUse = tempInUse
                connection2.locked = tempLocked
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getConnectionStatus(connectionId: String): NetworkConnection? {
        val connection = connections[connectionId] ?: return null
        
        return connection.mutex.withLock {
            delay(Random.nextLong(5, 15))
            connection.copy()
        }
    }
    
    suspend fun getSocketStatus(socketId: String): Socket? {
        val socket = sockets[socketId] ?: return null
        
        return socket.mutex.withLock {
            delay(Random.nextLong(5, 15))
            socket.copy()
        }
    }
    
    fun getAllConnections() = connections.values.toList()
    fun getAllSockets() = sockets.values.toList()
}

suspend fun simulateConnectionAcquisition(
    networkManager: NetworkManager,
    clientId: Int
) {
    val connections = networkManager.getAllConnections()
    
    repeat(10) { attempt ->
        val connection = connections.filter { !it.inUse }.randomOrNull()
        
        if (connection != null) {
            val success = networkManager.acquireConnection(connection.connectionId)
            if (success) {
                println("Client $clientId: Acquired ${connection.connectionId}")
            } else {
                println("Client $clientId: Failed to acquire ${connection.connectionId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateConnectionRelease(
    networkManager: NetworkManager,
    clientId: Int
) {
    val connections = networkManager.getAllConnections()
    
    repeat(10) { attempt ->
        val connection = connections.filter { it.inUse }.randomOrNull()
        
        if (connection != null) {
            val success = networkManager.releaseConnection(connection.connectionId)
            if (success) {
                println("Client $clientId: Released ${connection.connectionId}")
            } else {
                println("Client $clientId: Failed to release ${connection.connectionId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateSocketBinding(
    networkManager: NetworkManager,
    clientId: Int
) {
    val sockets = networkManager.getAllSockets()
    
    repeat(8) { attempt ->
        val socket = sockets.filter { !it.bound }.randomOrNull()
        
        if (socket != null) {
            val success = networkManager.bindSocket(socket.socketId)
            if (success) {
                println("Client $clientId: Bound ${socket.socketId}")
            } else {
                println("Client $clientId: Failed to bind ${socket.socketId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateSocketUnbinding(
    networkManager: NetworkManager,
    clientId: Int
) {
    val sockets = networkManager.getAllSockets()
    
    repeat(8) { attempt ->
        val socket = sockets.filter { it.bound }.randomOrNull()
        
        if (socket != null) {
            val success = networkManager.unbindSocket(socket.socketId)
            if (success) {
                println("Client $clientId: Unbound ${socket.socketId}")
            } else {
                println("Client $clientId: Failed to unbind ${socket.socketId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateDataSending(
    networkManager: NetworkManager,
    senderId: Int
) {
    val connections = networkManager.getAllConnections()
    val sockets = networkManager.getAllSockets()
    
    repeat(10) { attempt ->
        val connection = connections.filter { it.inUse }.randomOrNull()
        val socket = sockets.filter { it.bound }.randomOrNull()
        
        if (connection != null && socket != null) {
            val success = networkManager.sendDataWithSocket(
                connection.connectionId,
                socket.socketId
            )
            
            if (success) {
                println("Sender $senderId: Sent data via ${connection.connectionId} and ${socket.socketId}")
            } else {
                println("Sender $senderId: Failed to send data")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateConnectionTransfer(
    networkManager: NetworkManager,
    transferId: Int
) {
    val connections = networkManager.getAllConnections()
    
    repeat(6) { attempt ->
        val inUseConnections = connections.filter { it.inUse }
        val availableConnections = connections.filter { !it.inUse }
        
        if (inUseConnections.isNotEmpty() && availableConnections.isNotEmpty()) {
            val fromConnection = inUseConnections.random()
            val toConnection = availableConnections.random()
            
            val success = networkManager.transferConnection(
                fromConnection.connectionId,
                toConnection.connectionId
            )
            
            if (success) {
                println("Transfer $transferId: ${fromConnection.connectionId} -> ${toConnection.connectionId}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateConnectionSwap(
    networkManager: NetworkManager,
    swapId: Int
) {
    val connections = networkManager.getAllConnections()
    
    repeat(5) { attempt ->
        val inUseConnections = connections.filter { it.inUse }
        
        if (inUseConnections.size >= 2) {
            val connection1 = inUseConnections.random()
            val connection2 = inUseConnections.filter { it.connectionId != connection1.connectionId }.random()
            
            val success = networkManager.swapConnections(
                connection1.connectionId,
                connection2.connectionId
            )
            
            if (success) {
                println("Swap $swapId: ${connection1.connectionId} <-> ${connection2.connectionId}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorNetwork(
    networkManager: NetworkManager,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val connections = networkManager.getAllConnections()
        val sockets = networkManager.getAllSockets()
        
        val inUse = connections.count { it.inUse }
        val locked = connections.count { it.locked }
        val boundSockets = sockets.count { it.bound }
        val lockedSockets = sockets.count { it.locked }
        
        println("Monitor $monitorId: InUse=$inUse, Locked=$locked, " +
                "BoundSockets=$boundSockets, LockedSockets=$lockedSockets")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val networkManager = NetworkManager()
    
    println("Starting Network Connection Simulation...")
    println("Initial Connection Status:")
    networkManager.getAllConnections().forEach { connection ->
        println("  ${connection.connectionId} (${connection.remoteAddress}): " +
                "InUse=${connection.inUse}, Locked=${connection.locked}")
    }
    println()
    
    println("Initial Socket Status:")
    networkManager.getAllSockets().forEach { socket ->
        println("  ${socket.socketId} (Port ${socket.port}): Bound=${socket.bound}, Locked=${socket.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateConnectionAcquisition(networkManager, 1)
    })
    
    jobs.add(launch {
        simulateConnectionAcquisition(networkManager, 2)
    })
    
    jobs.add(launch {
        simulateConnectionRelease(networkManager, 1)
    })
    
    jobs.add(launch {
        simulateConnectionRelease(networkManager, 2)
    })
    
    jobs.add(launch {
        simulateSocketBinding(networkManager, 1)
    })
    
    jobs.add(launch {
        simulateSocketUnbinding(networkManager, 1)
    })
    
    jobs.add(launch {
        simulateDataSending(networkManager, 1)
    })
    
    jobs.add(launch {
        simulateConnectionTransfer(networkManager, 1)
    })
    
    jobs.add(launch {
        simulateConnectionSwap(networkManager, 1)
    })
    
    jobs.add(launch {
        monitorNetwork(networkManager, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val connections = networkManager.getAllConnections()
    val sockets = networkManager.getAllSockets()
    
    println("\n=== Final Connection Status ===")
    connections.forEach { connection ->
        println("  ${connection.connectionId} (${connection.remoteAddress}): " +
                "InUse=${connection.inUse}, Locked=${connection.locked}")
    }
    
    println("\n=== Final Socket Status ===")
    sockets.forEach { socket ->
        println("  ${socket.socketId} (Port ${socket.port}): Bound=${socket.bound}, Locked=${socket.locked}")
    }
    
    val inUse = connections.count { it.inUse }
    val locked = connections.count { it.locked }
    val boundSockets = sockets.count { it.bound }
    val lockedSockets = sockets.count { it.locked }
    
    println("\nInUse Connections: $inUse/${connections.size}")
    println("Locked Connections: $locked/${connections.size}")
    println("Bound Sockets: $boundSockets/${sockets.size}")
    println("Locked Sockets: $lockedSockets/${sockets.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - acquireConnection(): connectionPoolMutex -> connection.mutex")
    println("  - releaseConnection(): connection.mutex -> connectionPoolMutex")
    println("  - bindSocket(): socketPoolMutex -> socket.mutex")
    println("  - unbindSocket(): socket.mutex -> socketPoolMutex")
    println("  - sendDataWithSocket(): connection.mutex -> socket.mutex")
    println("  - transferConnection(): connection1.mutex -> connection2.mutex")
    println("  - swapConnections(): connection1.mutex -> connection2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}