import kotlinx.coroutines.*
import kotlin.random.Random

class ResourceReleaser {
    private var resources = mutableListOf<String>()
    private var releaseJob: Job? = null
    
    suspend fun releaseResource(resourceId: String) = coroutineScope {
        releaseJob?.cancel()
        
        releaseJob = launch {
            val tmp = resources.toList()
            delay(50)
            resources.remove(resourceId)
            println("Resource $resourceId released")
        }
    }
    
    suspend fun releaseMultipleResources(resourceIds: List<String>) = coroutineScope {
        resourceIds.forEach { resourceId ->
            releaseResource(resourceId)
            delay(100)
        }
    }
    
    fun getResources() = resources.toList()
}

class ConnectionReleaser {
    private var connections = mutableListOf<String>()
    private var releaseJob: Job? = null
    
    suspend fun releaseConnection(connectionId: String) = coroutineScope {
        releaseJob?.cancel()
        
        releaseJob = launch {
            val tmp = connections.toList()
            delay(50)
            connections.remove(connectionId)
            println("Connection $connectionId released")
        }
    }
    
    suspend fun releaseMultipleConnections(connectionIds: List<String>) = coroutineScope {
        connectionIds.forEach { connectionId ->
            releaseConnection(connectionId)
            delay(100)
        }
    }
    
    fun getConnections() = connections.toList()
}

class FileReleaser {
    private var files = mutableListOf<String>()
    private var releaseJob: Job? = null
    
    suspend fun releaseFile(fileId: String) = coroutineScope {
        releaseJob?.cancel()
        
        releaseJob = launch {
            val tmp = files.toList()
            delay(50)
            files.remove(fileId)
            println("File $fileId released")
        }
    }
    
    suspend fun releaseMultipleFiles(fileIds: List<String>) = coroutineScope {
        fileIds.forEach { fileId ->
            releaseFile(fileId)
            delay(100)
        }
    }
    
    fun getFiles() = files.toList()
}

class MemoryReleaser {
    private var allocations = mutableListOf<String>()
    private var releaseJob: Job? = null
    
    suspend fun releaseMemory(allocationId: String) = coroutineScope {
        releaseJob?.cancel()
        
        releaseJob = launch {
            val tmp = allocations.toList()
            delay(50)
            allocations.remove(allocationId)
            println("Memory $allocationId released")
        }
    }
    
    suspend fun releaseMultipleMemory(allocationIds: List<String>) = coroutineScope {
        allocationIds.forEach { allocationId ->
            releaseMemory(allocationId)
            delay(100)
        }
    }
    
    fun getAllocations() = allocations.toList()
}

class HandleReleaser {
    private var handles = mutableListOf<String>()
    private var releaseJob: Job? = null
    
    suspend fun releaseHandle(handleId: String) = coroutineScope {
        releaseJob?.cancel()
        
        releaseJob = launch {
            val tmp = handles.toList()
            delay(50)
            handles.remove(handleId)
            println("Handle $handleId released")
        }
    }
    
    suspend fun releaseMultipleHandles(handleIds: List<String>) = coroutineScope {
        handleIds.forEach { handleId ->
            releaseHandle(handleId)
            delay(100)
        }
    }
    
    fun getHandles() = handles.toList()
}

suspend fun simulateResourceReleaser(
    releaser: ResourceReleaser,
    releaserId: Int
) {
    repeat(10) { attempt ->
        releaser.releaseResource("Resource$attempt")
        delay(Random.nextLong(50, 150))
    }
    
    println("Resource releaser $releaserId completed")
}

suspend fun simulateConnectionReleaser(
    releaser: ConnectionReleaser,
    releaserId: Int
) {
    val connectionIds = listOf(
        "Conn1", "Conn2", "Conn3", "Conn4", "Conn5"
    )
    
    releaser.releaseMultipleConnections(connectionIds)
    
    println("Connection releaser $releaserId completed")
}

suspend fun simulateFileReleaser(
    releaser: FileReleaser,
    releaserId: Int
) {
    val fileIds = listOf(
        "File1", "File2", "File3", "File4", "File5"
    )
    
    releaser.releaseMultipleFiles(fileIds)
    
    println("File releaser $releaserId completed")
}

suspend fun simulateMemoryReleaser(
    releaser: MemoryReleaser,
    releaserId: Int
) {
    val allocationIds = listOf(
        "Alloc1", "Alloc2", "Alloc3", "Alloc4", "Alloc5"
    )
    
    releaser.releaseMultipleMemory(allocationIds)
    
    println("Memory releaser $releaserId completed")
}

suspend fun simulateHandleReleaser(
    releaser: HandleReleaser,
    releaserId: Int
) {
    val handleIds = listOf(
        "Handle1", "Handle2", "Handle3", "Handle4", "Handle5"
    )
    
    releaser.releaseMultipleHandles(handleIds)
    
    println("Handle releaser $releaserId completed")
}

suspend fun monitorReleasers(
    resourceReleaser: ResourceReleaser,
    connectionReleaser: ConnectionReleaser,
    fileReleaser: FileReleaser,
    memoryReleaser: MemoryReleaser,
    handleReleaser: HandleReleaser,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Resources: ${resourceReleaser.getResources()}")
        println("  Connections: ${connectionReleaser.getConnections()}")
        println("  Files: ${fileReleaser.getFiles()}")
        println("  Allocations: ${memoryReleaser.getAllocations()}")
        println("  Handles: ${handleReleaser.getHandles()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Resource Release Simulation...")
    println()
    
    val resourceReleaser = ResourceReleaser()
    val connectionReleaser = ConnectionReleaser()
    val fileReleaser = FileReleaser()
    val memoryReleaser = MemoryReleaser()
    val handleReleaser = HandleReleaser()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateResourceReleaser(resourceReleaser, 1)
    })
    
    jobs.add(launch {
        simulateResourceReleaser(resourceReleaser, 2)
    })
    
    jobs.add(launch {
        simulateConnectionReleaser(connectionReleaser, 1)
    })
    
    jobs.add(launch {
        simulateFileReleaser(fileReleaser, 1)
    })
    
    jobs.add(launch {
        simulateMemoryReleaser(memoryReleaser, 1)
    })
    
    jobs.add(launch {
        simulateHandleReleaser(handleReleaser, 1)
    })
    
    jobs.add(launch {
        monitorReleasers(
            resourceReleaser,
            connectionReleaser,
            fileReleaser,
            memoryReleaser,
            handleReleaser,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Release Status ===")
    println("Resources: ${resourceReleaser.getResources()}")
    println("Connections: ${connectionReleaser.getConnections()}")
    println("Files: ${fileReleaser.getFiles()}")
    println("Allocations: ${memoryReleaser.getAllocations()}")
    println("Handles: ${handleReleaser.getHandles()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels release jobs and immediately starts new ones:")
    println("  - ResourceReleaser.releaseResource() cancels releaseJob and starts new one")
    println("  - ConnectionReleaser.releaseConnection() cancels releaseJob and starts new one")
    println("  - FileReleaser.releaseFile() cancels releaseJob and starts new one")
    println("  - MemoryReleaser.releaseMemory() cancels releaseJob and starts new one")
    println("  - HandleReleaser.releaseHandle() cancels releaseJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}