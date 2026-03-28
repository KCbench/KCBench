import kotlinx.coroutines.*
import kotlin.random.Random

data class ResourceConfig(
    val resourceId: String,
    val resourceType: String,
    val dependencies: List<String>,
    val initTime: Int
)

data class Resource(
    val resourceId: String,
    val resourceType: String,
    var allocated: Boolean = false,
    var initialized: Boolean = false,
    var ready: Boolean = false,
    val dependencies: List<String>
)

class ResourceInitializationManager {
    private val resources = mutableMapOf<String, Resource>()
    
    init {
        initializeResources()
    }
    
    private fun initializeResources() {
        val resourceConfigs = listOf(
            ResourceConfig("MemoryPool", "Memory", listOf(), 100),
            ResourceConfig("ThreadPool", "Thread", listOf("MemoryPool"), 120),
            ResourceConfig("DatabasePool", "Database", listOf("ThreadPool"), 150),
            ResourceConfig("CachePool", "Cache", listOf("MemoryPool"), 110),
            ResourceConfig("NetworkPool", "Network", listOf("ThreadPool"), 130),
            ResourceConfig("FilePool", "File", listOf("ThreadPool"), 140),
            ResourceConfig("QueuePool", "Queue", listOf("MemoryPool"), 90),
            ResourceConfig("LockPool", "Lock", listOf("MemoryPool"), 80),
            ResourceConfig("EventPool", "Event", listOf("QueuePool"), 100),
            ResourceConfig("TimerPool", "Timer", listOf("ThreadPool"), 85)
        )
        
        resourceConfigs.forEach { config ->
            resources[config.resourceId] = Resource(
                resourceId = config.resourceId,
                resourceType = config.resourceType,
                allocated = false,
                initialized = false,
                ready = false,
                dependencies = config.dependencies
            )
        }
    }
    
    suspend fun allocateResource(resourceId: String): Boolean {
        val resource = resources[resourceId] ?: return false
        
        if (resource.allocated) {
            return true
        }
        
        val dependenciesAllocated = resource.dependencies.all { depId ->
            val depResource = resources[depId]
            depResource != null && depResource.allocated
        }
        
        if (!dependenciesAllocated) {
            return false
        }
        
        delay(Random.nextLong(20, 80))
        
        resource.allocated = true
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun initializeResource(resourceId: String): Boolean {
        val resource = resources[resourceId] ?: return false
        
        if (!resource.allocated) {
            return false
        }
        
        if (resource.initialized) {
            return true
        }
        
        delay(Random.nextLong(30, 100))
        
        resource.initialized = true
        delay(Random.nextLong(10, 20))
        
        return true
    }
    
    suspend fun markResourceReady(resourceId: String): Boolean {
        val resource = resources[resourceId] ?: return false
        
        if (!resource.initialized) {
            return false
        }
        
        if (resource.ready) {
            return true
        }
        
        delay(Random.nextLong(15, 50))
        
        resource.ready = true
        delay(Random.nextLong(10, 20))
        
        return true
    }
    
    suspend fun initializeAllResources(): Int {
        var initialized = 0
        
        resources.keys.forEach { resourceId ->
            if (allocateResource(resourceId) &&
                initializeResource(resourceId) &&
                markResourceReady(resourceId)) {
                initialized++
            }
        }
        
        return initialized
    }
    
    suspend fun resetResource(resourceId: String): Boolean {
        val resource = resources[resourceId] ?: return false
        
        resource.allocated = false
        resource.initialized = false
        resource.ready = false
        
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun getResourceStatus(resourceId: String): Resource? {
        return resources[resourceId]
    }
    
    fun getAllResources() = resources.values.toList()
}

class ResourceInitializer(
    private val resourceManager: ResourceInitializationManager,
    private val initializerName: String
) {
    suspend fun initializeRandomResource(): Boolean {
        val resources = resourceManager.getAllResources()
        val unallocatedResources = resources.filter { !it.allocated }
        
        if (unallocatedResources.isEmpty()) {
            return false
        }
        
        val resource = unallocatedResources.random()
        
        return resourceManager.allocateResource(resource.resourceId) &&
               resourceManager.initializeResource(resource.resourceId) &&
               resourceManager.markResourceReady(resource.resourceId)
    }
    
    suspend fun initializeMultipleResources(count: Int): Int {
        var initialized = 0
        
        repeat(count) {
            if (initializeRandomResource()) {
                initialized++
            }
        }
        
        return initialized
    }
}

suspend fun simulateResourceInitialization(
    initializer: ResourceInitializer,
    initializerId: Int
) {
    repeat(12) { attempt ->
        initializer.initializeMultipleResources(2)
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateResourceReset(
    resourceManager: ResourceInitializationManager
) {
    repeat(8) { attempt ->
        val resources = resourceManager.getAllResources()
        val resource = resources.random()
        
        resourceManager.resetResource(resource.resourceId)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateDependencyInitialization(
    resourceManager: ResourceInitializationManager
) {
    repeat(15) { attempt ->
        val resources = resourceManager.getAllResources()
        val unallocatedResources = resources.filter { !it.allocated }
        
        if (unallocatedResources.isNotEmpty()) {
            val resource = unallocatedResources.random()
            
            if (resource.dependencies.isEmpty()) {
                resourceManager.allocateResource(resource.resourceId)
            }
        }
        
        delay(Random.nextLong(40, 120))
    }
}

suspend fun simulateResourceMonitoring(
    resourceManager: ResourceInitializationManager
) {
    repeat(20) { attempt ->
        val resources = resourceManager.getAllResources()
        
        println("Resource Status:")
        println("  Allocated: ${resources.count { it.allocated }}")
        println("  Initialized: ${resources.count { it.initialized }}")
        println("  Ready: ${resources.count { it.ready }}")
        
        val invalidAllocated = resources.filter { resource ->
            resource.allocated && !resource.dependencies.all { depId ->
                val depResource = resourceManager.getResourceStatus(depId)
                depResource != null && depResource.allocated
            }
        }
        
        if (invalidAllocated.isNotEmpty()) {
            println("  Invalid allocated resources: ${invalidAllocated.size}")
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun simulateFullResourceInitialization(
    resourceManager: ResourceInitializationManager
) {
    repeat(6) { attempt ->
        resourceManager.initializeAllResources()
        delay(Random.nextLong(200, 500))
    }
}

fun main() = runBlocking {
    val resourceManager = ResourceInitializationManager()
    
    println("Starting Resource Initialization Simulation...")
    println("Initial Resource Status:")
    resourceManager.getAllResources().forEach { resource ->
        println(
            "  ${resource.resourceId} (${resource.resourceType}): " +
            "Allocated=${resource.allocated}, " +
            "Deps=${resource.dependencies.joinToString()}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val initializers = listOf(
        ResourceInitializer(resourceManager, "Alice"),
        ResourceInitializer(resourceManager, "Bob"),
        ResourceInitializer(resourceManager, "Charlie"),
        ResourceInitializer(resourceManager, "David")
    )
    
    initializers.forEachIndexed { index, initializer ->
        jobs.add(launch {
            simulateResourceInitialization(initializer, index)
        })
    }
    
    jobs.add(launch {
        simulateResourceReset(resourceManager)
    })
    
    jobs.add(launch {
        simulateDependencyInitialization(resourceManager)
    })
    
    jobs.add(launch {
        simulateResourceMonitoring(resourceManager)
    })
    
    jobs.add(launch {
        simulateFullResourceInitialization(resourceManager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val resources = resourceManager.getAllResources()
    val allocatedResources = resources.filter { it.allocated }
    val initializedResources = resources.filter { it.initialized }
    val readyResources = resources.filter { it.ready }
    
    println("\n=== Final Resource Status ===")
    resources.forEach { resource ->
        println(
            "  ${resource.resourceId} (${resource.resourceType}): " +
            "Allocated=${resource.allocated}, " +
            "Initialized=${resource.initialized}, " +
            "Ready=${resource.ready}"
        )
    }
    
    val invalidAllocated = allocatedResources.filter { resource ->
        !resource.dependencies.all { depId ->
            val depResource = resourceManager.getResourceStatus(depId)
            depResource != null && depResource.allocated
        }
    }
    
    if (invalidAllocated.isNotEmpty()) {
        println("\n⚠️  Resources allocated before dependencies:")
        invalidAllocated.forEach { resource ->
            println("  ${resource.resourceId}: ${resource.dependencies}")
        }
    } else {
        println("\n✅ All resources allocated in correct order")
    }
    
    val totalResources = resources.size
    println("\nAllocated: ${allocatedResources.size}/$totalResources")
    println("Initialized: ${initializedResources.size}/$totalResources")
    println("Ready: ${readyResources.size}/$totalResources")
}