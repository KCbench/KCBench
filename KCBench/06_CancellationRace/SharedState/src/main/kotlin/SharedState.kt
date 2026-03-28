import kotlinx.coroutines.*
import kotlin.random.Random

class SharedStateManager {
    private var sharedState = mutableMapOf<String, String>()
    private var updateJob: Job? = null
    
    suspend fun updateSharedState(key: String, value: String) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = sharedState.toMap()
            delay(50)
            sharedState[key] = value
            println("Shared state updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleSharedStates(updates: Map<String, String>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateSharedState(key, value)
            delay(100)
        }
    }
    
    fun getSharedState() = sharedState.toMap()
}

class SharedDataHolder {
    private var sharedData = mutableMapOf<String, String>()
    private var updateJob: Job? = null
    
    suspend fun updateSharedData(key: String, value: String) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = sharedData.toMap()
            delay(50)
            sharedData[key] = value
            println("Shared data updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleSharedData(updates: Map<String, String>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateSharedData(key, value)
            delay(100)
        }
    }
    
    fun getSharedData() = sharedData.toMap()
}

class SharedValueManager {
    private var sharedValues = mutableMapOf<String, Int>()
    private var updateJob: Job? = null
    
    suspend fun updateSharedValue(key: String, value: Int) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = sharedValues.toMap()
            delay(50)
            sharedValues[key] = value
            println("Shared value updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleSharedValues(updates: Map<String, Int>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateSharedValue(key, value)
            delay(100)
        }
    }
    
    fun getSharedValues() = sharedValues.toMap()
}

class SharedResourceHolder {
    private var sharedResources = mutableMapOf<String, String>()
    private var updateJob: Job? = null
    
    suspend fun updateSharedResource(key: String, value: String) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = sharedResources.toMap()
            delay(50)
            sharedResources[key] = value
            println("Shared resource updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleSharedResources(updates: Map<String, String>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateSharedResource(key, value)
            delay(100)
        }
    }
    
    fun getSharedResources() = sharedResources.toMap()
}

class SharedMemoryManager {
    private var sharedMemory = mutableMapOf<String, String>()
    private var updateJob: Job? = null
    
    suspend fun updateSharedMemory(key: String, value: String) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = sharedMemory.toMap()
            delay(50)
            sharedMemory[key] = value
            println("Shared memory updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleSharedMemory(updates: Map<String, String>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateSharedMemory(key, value)
            delay(100)
        }
    }
    
    fun getSharedMemory() = sharedMemory.toMap()
}

suspend fun simulateSharedStateManager(
    manager: SharedStateManager,
    managerId: Int
) {
    repeat(10) { attempt ->
        manager.updateSharedState("State$attempt", "Value$attempt")
        delay(Random.nextLong(50, 150))
    }
    
    println("Shared state manager $managerId completed")
}

suspend fun simulateSharedDataHolder(
    holder: SharedDataHolder,
    holderId: Int
) {
    val updates = mapOf(
        "Data1" to "Value1",
        "Data2" to "Value2",
        "Data3" to "Value3",
        "Data4" to "Value4",
        "Data5" to "Value5"
    )
    
    holder.updateMultipleSharedData(updates)
    
    println("Shared data holder $holderId completed")
}

suspend fun simulateSharedValueManager(
    manager: SharedValueManager,
    managerId: Int
) {
    val updates = mapOf(
        "Value1" to 10,
        "Value2" to 20,
        "Value3" to 30,
        "Value4" to 40,
        "Value5" to 50
    )
    
    manager.updateMultipleSharedValues(updates)
    
    println("Shared value manager $managerId completed")
}

suspend fun simulateSharedResourceHolder(
    holder: SharedResourceHolder,
    holderId: Int
) {
    val updates = mapOf(
        "Resource1" to "Value1",
        "Resource2" to "Value2",
        "Resource3" to "Value3",
        "Resource4" to "Value4",
        "Resource5" to "Value5"
    )
    
    holder.updateMultipleSharedResources(updates)
    
    println("Shared resource holder $holderId completed")
}

suspend fun simulateSharedMemoryManager(
    manager: SharedMemoryManager,
    managerId: Int
) {
    val updates = mapOf(
        "Memory1" to "Value1",
        "Memory2" to "Value2",
        "Memory3" to "Value3",
        "Memory4" to "Value4",
        "Memory5" to "Value5"
    )
    
    manager.updateMultipleSharedMemory(updates)
    
    println("Shared memory manager $managerId completed")
}

suspend fun monitorSharedManagers(
    sharedStateManager: SharedStateManager,
    sharedDataHolder: SharedDataHolder,
    sharedValueManager: SharedValueManager,
    sharedResourceHolder: SharedResourceHolder,
    sharedMemoryManager: SharedMemoryManager,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Shared state: ${sharedStateManager.getSharedState()}")
        println("  Shared data: ${sharedDataHolder.getSharedData()}")
        println("  Shared values: ${sharedValueManager.getSharedValues()}")
        println("  Shared resources: ${sharedResourceHolder.getSharedResources()}")
        println("  Shared memory: ${sharedMemoryManager.getSharedMemory()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Shared State Simulation...")
    println()
    
    val sharedStateManager = SharedStateManager()
    val sharedDataHolder = SharedDataHolder()
    val sharedValueManager = SharedValueManager()
    val sharedResourceHolder = SharedResourceHolder()
    val sharedMemoryManager = SharedMemoryManager()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSharedStateManager(sharedStateManager, 1)
    })
    
    jobs.add(launch {
        simulateSharedStateManager(sharedStateManager, 2)
    })
    
    jobs.add(launch {
        simulateSharedDataHolder(sharedDataHolder, 1)
    })
    
    jobs.add(launch {
        simulateSharedValueManager(sharedValueManager, 1)
    })
    
    jobs.add(launch {
        simulateSharedResourceHolder(sharedResourceHolder, 1)
    })
    
    jobs.add(launch {
        simulateSharedMemoryManager(sharedMemoryManager, 1)
    })
    
    jobs.add(launch {
        monitorSharedManagers(
            sharedStateManager,
            sharedDataHolder,
            sharedValueManager,
            sharedResourceHolder,
            sharedMemoryManager,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Shared States ===")
    println("Shared state: ${sharedStateManager.getSharedState()}")
    println("Shared data: ${sharedDataHolder.getSharedData()}")
    println("Shared values: ${sharedValueManager.getSharedValues()}")
    println("Shared resources: ${sharedResourceHolder.getSharedResources()}")
    println("Shared memory: ${sharedMemoryManager.getSharedMemory()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels update jobs and immediately starts new ones:")
    println("  - SharedStateManager.updateSharedState() cancels updateJob and starts new one")
    println("  - SharedDataHolder.updateSharedData() cancels updateJob and starts new one")
    println("  - SharedValueManager.updateSharedValue() cancels updateJob and starts new one")
    println("  - SharedResourceHolder.updateSharedResource() cancels updateJob and starts new one")
    println("  - SharedMemoryManager.updateSharedMemory() cancels updateJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}