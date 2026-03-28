import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class Resource(
    val resourceId: String,
    val resourceType: String,
    var allocated: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

class ResourcePoolManager {
    private val resources = mutableMapOf<String, Resource>()
    private val poolMutex = Mutex()
    
    init {
        initializeResources()
    }
    
    private fun initializeResources() {
        val resourceConfigs = listOf(
            Triple("RES001", "Database", 100),
            Triple("RES002", "Cache", 80),
            Triple("RES003", "Network", 120),
            Triple("RES004", "File", 90),
            Triple("RES005", "Memory", 70),
            Triple("RES006", "Thread", 110),
            Triple("RES007", "Socket", 85),
            Triple("RES008", "Queue", 75),
            Triple("RES009", "Lock", 65),
            Triple("RES010", "Event", 95)
        )
        
        resourceConfigs.forEach { (resourceId, resourceType, allocTime) ->
            resources[resourceId] = Resource(
                resourceId = resourceId,
                resourceType = resourceType,
                allocated = false,
                locked = false
            )
        }
    }
    
    suspend fun allocateResource(resourceId: String): Boolean {
        val resource = resources[resourceId] ?: return false
        
        if (resource.allocated) {
            return false
        }
        
        poolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (resource.allocated) {
                return false
            }
            
            resource.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                resource.allocated = true
                resource.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun releaseResource(resourceId: String): Boolean {
        val resource = resources[resourceId] ?: return false
        
        if (!resource.allocated) {
            return false
        }
        
        resource.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            poolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                resource.allocated = false
                resource.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferResource(
        fromResourceId: String,
        toResourceId: String
    ): Boolean {
        val fromResource = resources[fromResourceId]
        val toResource = resources[toResourceId]
        
        if (fromResource == null || toResource == null) {
            return false
        }
        
        if (!fromResource.allocated || toResource.allocated) {
            return false
        }
        
        fromResource.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toResource.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromResource.allocated = false
                fromResource.locked = false
                toResource.allocated = true
                toResource.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapResources(
        resourceId1: String,
        resourceId2: String
    ): Boolean {
        val resource1 = resources[resourceId1]
        val resource2 = resources[resourceId2]
        
        if (resource1 == null || resource2 == null) {
            return false
        }
        
        if (!resource1.allocated || !resource2.allocated) {
            return false
        }
        
        resource1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            resource2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempAllocated = resource1.allocated
                val tempLocked = resource1.locked
                
                resource1.allocated = resource2.allocated
                resource1.locked = resource2.locked
                resource2.allocated = tempAllocated
                resource2.locked = tempLocked
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getResourceStatus(resourceId: String): Resource? {
        val resource = resources[resourceId] ?: return null
        
        return resource.mutex.withLock {
            delay(Random.nextLong(5, 15))
            resource.copy()
        }
    }
    
    fun getAllResources() = resources.values.toList()
}

suspend fun simulateResourceAllocation(
    poolManager: ResourcePoolManager,
    allocatorId: Int
) {
    val resources = poolManager.getAllResources()
    
    repeat(10) { attempt ->
        val resource = resources.filter { !it.allocated }.randomOrNull()
        
        if (resource != null) {
            val success = poolManager.allocateResource(resource.resourceId)
            if (success) {
                println("Allocator $allocatorId: Allocated ${resource.resourceId}")
            } else {
                println("Allocator $allocatorId: Failed to allocate ${resource.resourceId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateResourceRelease(
    poolManager: ResourcePoolManager,
    releaserId: Int
) {
    val resources = poolManager.getAllResources()
    
    repeat(10) { attempt ->
        val resource = resources.filter { it.allocated }.randomOrNull()
        
        if (resource != null) {
            val success = poolManager.releaseResource(resource.resourceId)
            if (success) {
                println("Releaser $releaserId: Released ${resource.resourceId}")
            } else {
                println("Releaser $releaserId: Failed to release ${resource.resourceId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateResourceTransfer(
    poolManager: ResourcePoolManager,
    transferId: Int
) {
    val resources = poolManager.getAllResources()
    
    repeat(8) { attempt ->
        val allocatedResources = resources.filter { it.allocated }
        val unallocatedResources = resources.filter { !it.allocated }
        
        if (allocatedResources.isNotEmpty() && unallocatedResources.isNotEmpty()) {
            val fromResource = allocatedResources.random()
            val toResource = unallocatedResources.random()
            
            val success = poolManager.transferResource(
                fromResource.resourceId,
                toResource.resourceId
            )
            
            if (success) {
                println("Transfer $transferId: ${fromResource.resourceId} -> ${toResource.resourceId}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateResourceSwap(
    poolManager: ResourcePoolManager,
    swapId: Int
) {
    val resources = poolManager.getAllResources()
    
    repeat(6) { attempt ->
        val allocatedResources = resources.filter { it.allocated }
        
        if (allocatedResources.size >= 2) {
            val resource1 = allocatedResources.random()
            val resource2 = allocatedResources.filter { it.resourceId != resource1.resourceId }.random()
            
            val success = poolManager.swapResources(
                resource1.resourceId,
                resource2.resourceId
            )
            
            if (success) {
                println("Swap $swapId: ${resource1.resourceId} <-> ${resource2.resourceId}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateBidirectionalTransfer(
    poolManager: ResourcePoolManager,
    transferId: Int
) {
    val resources = poolManager.getAllResources()
    
    repeat(5) { attempt ->
        val resource1 = resources.random()
        val resource2 = resources.filter { it.resourceId != resource1.resourceId }.random()
        
        val job1 = launch {
            poolManager.transferResource(resource1.resourceId, resource2.resourceId)
        }
        
        val job2 = launch {
            poolManager.transferResource(resource2.resourceId, resource1.resourceId)
        }
        
        job1.join()
        job2.join()
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorResourcePool(
    poolManager: ResourcePoolManager,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val resources = poolManager.getAllResources()
        val allocated = resources.count { it.allocated }
        val locked = resources.count { it.locked }
        
        println("Monitor $monitorId: Allocated=$allocated, Locked=$locked, Total=${resources.size}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val poolManager = ResourcePoolManager()
    
    println("Starting Resource Pool Simulation...")
    println("Initial Resource Status:")
    poolManager.getAllResources().forEach { resource ->
        println("  ${resource.resourceId} (${resource.resourceType}): " +
                "Allocated=${resource.allocated}, Locked=${resource.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateResourceAllocation(poolManager, 1)
    })
    
    jobs.add(launch {
        simulateResourceAllocation(poolManager, 2)
    })
    
    jobs.add(launch {
        simulateResourceRelease(poolManager, 1)
    })
    
    jobs.add(launch {
        simulateResourceRelease(poolManager, 2)
    })
    
    jobs.add(launch {
        simulateResourceTransfer(poolManager, 1)
    })
    
    jobs.add(launch {
        simulateResourceTransfer(poolManager, 2)
    })
    
    jobs.add(launch {
        simulateResourceSwap(poolManager, 1)
    })
    
    jobs.add(launch {
        simulateBidirectionalTransfer(poolManager, 1)
    })
    
    jobs.add(launch {
        monitorResourcePool(poolManager, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val resources = poolManager.getAllResources()
    
    println("\n=== Final Resource Status ===")
    resources.forEach { resource ->
        println("  ${resource.resourceId} (${resource.resourceType}): " +
                "Allocated=${resource.allocated}, Locked=${resource.locked}")
    }
    
    val allocated = resources.count { it.allocated }
    val locked = resources.count { it.locked }
    
    println("\nAllocated: $allocated/${resources.size}")
    println("Locked: $locked/${resources.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - allocateResource(): poolMutex -> resource.mutex")
    println("  - releaseResource(): resource.mutex -> poolMutex")
    println("  - transferResource(): resource1.mutex -> resource2.mutex")
    println("  - swapResources(): resource1.mutex -> resource2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}