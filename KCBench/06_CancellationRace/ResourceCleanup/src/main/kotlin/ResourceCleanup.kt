import kotlinx.coroutines.*
import kotlin.random.Random

class ResourceHolder {
    private var resources = mutableListOf<String>()
    private var cleanupJob: Job? = null
    
    suspend fun cleanupResource(resourceId: String) = coroutineScope {
        cleanupJob?.cancel()
        
        cleanupJob = launch {
            val tmp = resources.toList()
            delay(50)
            resources.remove(resourceId)
            println("Resource $resourceId cleaned up")
        }
    }
    
    suspend fun cleanupMultipleResources(resourceIds: List<String>) = coroutineScope {
        resourceIds.forEach { resourceId ->
            cleanupResource(resourceId)
            delay(100)
        }
    }
    
    fun getResources() = resources.toList()
}

class ConnectionPool {
    private var connections = mutableListOf<String>()
    private var cleanupJob: Job? = null
    
    suspend fun cleanupConnection(connectionId: String) = coroutineScope {
        cleanupJob?.cancel()
        
        cleanupJob = launch {
            val tmp = connections.toList()
            delay(50)
            connections.remove(connectionId)
            println("Connection $connectionId cleaned up")
        }
    }
    
    suspend fun cleanupMultipleConnections(connectionIds: List<String>) = coroutineScope {
        connectionIds.forEach { connectionId ->
            cleanupConnection(connectionId)
            delay(100)
        }
    }
    
    fun getConnections() = connections.toList()
}

class FileHandler {
    private var files = mutableListOf<String>()
    private var cleanupJob: Job? = null
    
    suspend fun cleanupFile(fileId: String) = coroutineScope {
        cleanupJob?.cancel()
        
        cleanupJob = launch {
            val tmp = files.toList()
            delay(50)
            files.remove(fileId)
            println("File $fileId cleaned up")
        }
    }
    
    suspend fun cleanupMultipleFiles(fileIds: List<String>) = coroutineScope {
        fileIds.forEach { fileId ->
            cleanupFile(fileId)
            delay(100)
        }
    }
    
    fun getFiles() = files.toList()
}

class MemoryManager {
    private var allocations = mutableListOf<String>()
    private var cleanupJob: Job? = null
    
    suspend fun cleanupAllocation(allocationId: String) = coroutineScope {
        cleanupJob?.cancel()
        
        cleanupJob = launch {
            val tmp = allocations.toList()
            delay(50)
            allocations.remove(allocationId)
            println("Allocation $allocationId cleaned up")
        }
    }
    
    suspend fun cleanupMultipleAllocations(allocationIds: List<String>) = coroutineScope {
        allocationIds.forEach { allocationId ->
            cleanupAllocation(allocationId)
            delay(100)
        }
    }
    
    fun getAllocations() = allocations.toList()
}

class NetworkConnection {
    private var connections = mutableListOf<String>()
    private var cleanupJob: Job? = null
    
    suspend fun cleanupNetworkConnection(connectionId: String) = coroutineScope {
        cleanupJob?.cancel()
        
        cleanupJob = launch {
            val tmp = connections.toList()
            delay(50)
            connections.remove(connectionId)
            println("Network connection $connectionId cleaned up")
        }
    }
    
    suspend fun cleanupMultipleNetworkConnections(connectionIds: List<String>) = coroutineScope {
        connectionIds.forEach { connectionId ->
            cleanupNetworkConnection(connectionId)
            delay(100)
        }
    }
    
    fun getNetworkConnections() = connections.toList()
}

suspend fun simulateResourceHolder(
    holder: ResourceHolder,
    holderId: Int
) {
    repeat(10) { attempt ->
        holder.cleanupResource("Resource$attempt")
        delay(Random.nextLong(50, 150))
    }
    
    println("Resource holder $holderId completed")
}

suspend fun simulateConnectionPool(
    pool: ConnectionPool,
    poolId: Int
) {
    val connectionIds = listOf(
        "Conn1", "Conn2", "Conn3", "Conn4", "Conn5"
    )
    
    pool.cleanupMultipleConnections(connectionIds)
    
    println("Connection pool $poolId completed")
}

suspend fun simulateFileHandler(
    handler: FileHandler,
    handlerId: Int
) {
    val fileIds = listOf(
        "File1", "File2", "File3", "File4", "File5"
    )
    
    handler.cleanupMultipleFiles(fileIds)
    
    println("File handler $handlerId completed")
}

suspend fun simulateMemoryManager(
    manager: MemoryManager,
    managerId: Int
) {
    val allocationIds = listOf(
        "Alloc1", "Alloc2", "Alloc3", "Alloc4", "Alloc5"
    )
    
    manager.cleanupMultipleAllocations(allocationIds)
    
    println("Memory manager $managerId completed")
}

suspend fun simulateNetworkConnection(
    connection: NetworkConnection,
    connectionId: Int
) {
    val networkConnectionIds = listOf(
        "Net1", "Net2", "Net3", "Net4", "Net5"
    )
    
    connection.cleanupMultipleNetworkConnections(networkConnectionIds)
    
    println("Network connection $connectionId completed")
}

suspend fun monitorCleanup(
    resourceHolder: ResourceHolder,
    connectionPool: ConnectionPool,
    fileHandler: FileHandler,
    memoryManager: MemoryManager,
    networkConnection: NetworkConnection,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Resources: ${resourceHolder.getResources()}")
        println("  Connections: ${connectionPool.getConnections()}")
        println("  Files: ${fileHandler.getFiles()}")
        println("  Allocations: ${memoryManager.getAllocations()}")
        println("  Network connections: ${networkConnection.getNetworkConnections()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Resource Cleanup Simulation...")
    println()
    
    val resourceHolder = ResourceHolder()
    val connectionPool = ConnectionPool()
    val fileHandler = FileHandler()
    val memoryManager = MemoryManager()
    val networkConnection = NetworkConnection()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateResourceHolder(resourceHolder, 1)
    })
    
    jobs.add(launch {
        simulateResourceHolder(resourceHolder, 2)
    })
    
    jobs.add(launch {
        simulateConnectionPool(connectionPool, 1)
    })
    
    jobs.add(launch {
        simulateFileHandler(fileHandler, 1)
    })
    
    jobs.add(launch {
        simulateMemoryManager(memoryManager, 1)
    })
    
    jobs.add(launch {
        simulateNetworkConnection(networkConnection, 1)
    })
    
    jobs.add(launch {
        monitorCleanup(
            resourceHolder,
            connectionPool,
            fileHandler,
            memoryManager,
            networkConnection,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Cleanup Status ===")
    println("Resources: ${resourceHolder.getResources()}")
    println("Connections: ${connectionPool.getConnections()}")
    println("Files: ${fileHandler.getFiles()}")
    println("Allocations: ${memoryManager.getAllocations()}")
    println("Network connections: ${networkConnection.getNetworkConnections()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels cleanup jobs and immediately starts new ones:")
    println("  - ResourceHolder.cleanupResource() cancels cleanupJob and starts new one")
    println("  - ConnectionPool.cleanupConnection() cancels cleanupJob and starts new one")
    println("  - FileHandler.cleanupFile() cancels cleanupJob and starts new one")
    println("  - MemoryManager.cleanupAllocation() cancels cleanupJob and starts new one")
    println("  - NetworkConnection.cleanupNetworkConnection() cancels cleanupJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}