import kotlinx.coroutines.*
import kotlin.random.Random

data class Resource(
    val resourceId: String,
    val name: String,
    var status: ResourceStatus,
    var allocatedTo: String? = null
)

enum class ResourceStatus {
    AVAILABLE, ALLOCATED, MAINTENANCE
}

class ResourcePool {
    private val resources = mutableMapOf<String, Resource>()
    private var totalAllocations = 0
    private var failedAllocations = 0
    
    init {
        initializeResources()
    }
    
    private fun initializeResources() {
        val resourceNames = listOf(
            "Database", "Cache", "API", "Storage", "Network",
            "Compute", "Memory", "Disk", "GPU", "Bandwidth"
        )
        
        resourceNames.forEach { name ->
            resources[name] = Resource(
                resourceId = "RES_${Random.nextInt(1000, 9999)}",
                name = name,
                status = ResourceStatus.AVAILABLE
            )
        }
    }
    
    suspend fun allocateResource(
        resourceName: String,
        requester: String
    ): Boolean {
        val resource = resources[resourceName] ?: return false
        
        if (resource.status == ResourceStatus.AVAILABLE) {
            delay(Random.nextLong(1, 10))
            
            resource.status = ResourceStatus.ALLOCATED
            resource.allocatedTo = requester
            delay(Random.nextLong(1, 5))
            
            val currentAllocations = totalAllocations
            delay(Random.nextLong(1, 5))
            totalAllocations = currentAllocations + 1
            
            return true
        }
        
        val currentFailed = failedAllocations
        delay(Random.nextLong(1, 5))
        failedAllocations = currentFailed + 1
        
        return false
    }
    
    suspend fun releaseResource(resourceName: String): Boolean {
        val resource = resources[resourceName] ?: return false
        
        if (resource.status == ResourceStatus.ALLOCATED) {
            delay(Random.nextLong(1, 10))
            
            resource.status = ResourceStatus.AVAILABLE
            resource.allocatedTo = null
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun putInMaintenance(resourceName: String): Boolean {
        val resource = resources[resourceName] ?: return false
        
        if (resource.status == ResourceStatus.AVAILABLE) {
            delay(Random.nextLong(1, 10))
            
            resource.status = ResourceStatus.MAINTENANCE
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun releaseFromMaintenance(resourceName: String): Boolean {
        val resource = resources[resourceName] ?: return false
        
        if (resource.status == ResourceStatus.MAINTENANCE) {
            delay(Random.nextLong(1, 10))
            
            resource.status = ResourceStatus.AVAILABLE
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun allocateMultipleResources(
        resourceNames: List<String>,
        requester: String
    ): Int {
        var allocated = 0
        
        resourceNames.forEach { name ->
            if (allocateResource(name, requester)) {
                allocated++
            }
        }
        
        return allocated
    }
    
    fun getAvailableResources(): List<Resource> {
        return resources.values.filter { it.status == ResourceStatus.AVAILABLE }
    }
    
    fun getAllocatedResources(): List<Resource> {
        return resources.values.filter { it.status == ResourceStatus.ALLOCATED }
    }
    
    fun getAllResources() = resources.values.toList()
    
    fun getStatistics(): Pair<Int, Int> {
        return Pair(totalAllocations, failedAllocations)
    }
}

class ResourceUser(
    val resourcePool: ResourcePool,
    private val userId: String
) {
    suspend fun useResource(resourceName: String): Boolean {
        if (resourcePool.allocateResource(resourceName, userId)) {
            delay(Random.nextLong(50, 200))
            
            resourcePool.releaseResource(resourceName)
            return true
        }
        
        return false
    }
    
    suspend fun useMultipleResources(count: Int): Int {
        val availableResources = resourcePool.getAvailableResources()
        
        if (availableResources.size < count) {
            return 0
        }
        
        val selectedResources = availableResources.shuffled().take(count)
        var used = 0
        
        selectedResources.forEach { resource ->
            if (resourcePool.allocateResource(resource.name, userId)) {
                used++
            }
        }
        
        delay(Random.nextLong(100, 300))
        
        selectedResources.take(used).forEach { resource ->
            resourcePool.releaseResource(resource.name)
        }
        
        return used
    }
}

suspend fun simulateResourceUsage(
    user: ResourceUser,
    userId: Int
) {
    repeat(12) { attempt ->
        val resources = user.resourcePool.getAvailableResources()
        
        if (resources.isNotEmpty()) {
            val resource = resources.random()
            user.useResource(resource.name)
        }
        
        delay(Random.nextLong(20, 80))
    }
}

suspend fun simulateBulkAllocation(
    resourcePool: ResourcePool
) {
    repeat(10) { attempt ->
        val availableResources = resourcePool.getAvailableResources()
        
        if (availableResources.size >= 3) {
            val selectedResources = availableResources.shuffled().take(3)
            val resourceNames = selectedResources.map { it.name }
            
            resourcePool.allocateMultipleResources(
                resourceNames,
                "Bulk_${Random.nextInt(100, 999)}"
            )
        }
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateMaintenance(
    resourcePool: ResourcePool
) {
    repeat(8) { attempt ->
        val availableResources = resourcePool.getAvailableResources()
        
        if (availableResources.isNotEmpty()) {
            val resource = availableResources.random()
            
            if (Random.nextBoolean()) {
                resourcePool.putInMaintenance(resource.name)
            } else {
                resourcePool.releaseFromMaintenance(resource.name)
            }
        }
        
        delay(Random.nextLong(150, 400))
    }
}

fun main() = runBlocking {
    val resourcePool = ResourcePool()
    
    println("Starting Resource Pool Simulation...")
    println("Initial Available Resources: ${resourcePool.getAvailableResources().size}")
    println()
    
    val jobs = mutableListOf<Job>()
    
    val users = listOf(
        ResourceUser(resourcePool, "Alice"),
        ResourceUser(resourcePool, "Bob"),
        ResourceUser(resourcePool, "Charlie"),
        ResourceUser(resourcePool, "David"),
        ResourceUser(resourcePool, "Eve")
    )
    
    users.forEachIndexed { index, user ->
        jobs.add(launch {
            simulateResourceUsage(user, index)
        })
    }
    
    jobs.add(launch {
        simulateBulkAllocation(resourcePool)
    })
    
    jobs.add(launch {
        simulateMaintenance(resourcePool)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (totalAllocations, failedAllocations) = resourcePool.getStatistics()
    
    println("\n=== Resource Pool Statistics ===")
    println("Total Allocations: $totalAllocations")
    println("Failed Allocations: $failedAllocations")
    
    val available = resourcePool.getAvailableResources().size
    val allocated = resourcePool.getAllocatedResources().size
    val maintenance = resourcePool.getAllResources().count { it.status == ResourceStatus.MAINTENANCE }
    
    println("\nResource Status:")
    println("  Available: $available")
    println("  Allocated: $allocated")
    println("  Maintenance: $maintenance")
    
    val doubleAllocated = resourcePool.getAllocatedResources()
    
    if (doubleAllocated.size > 5) {
        println("\n⚠️  Many allocated resources: ${doubleAllocated.size}")
    }
    
    val allocationRate = if (totalAllocations + failedAllocations > 0) {
        (totalAllocations.toDouble() / (totalAllocations + failedAllocations) * 100)
    } else {
        0.0
    }
    
    println("\nAllocation Success Rate: ${"%.2f".format(allocationRate)}%")
}