import kotlinx.coroutines.*
import kotlin.random.Random

class StateUpdater {
    private var state = mutableMapOf<String, String>()
    private var updateJob: Job? = null
    
    suspend fun updateState(key: String, value: String) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = state.toMap()
            delay(50)
            state[key] = value
            println("State updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleStates(updates: Map<String, String>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateState(key, value)
            delay(100)
        }
    }
    
    fun getState() = state.toMap()
}

class StatusUpdater {
    private var status = mutableMapOf<String, String>()
    private var updateJob: Job? = null
    
    suspend fun updateStatus(key: String, value: String) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = status.toMap()
            delay(50)
            status[key] = value
            println("Status updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleStatus(updates: Map<String, String>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateStatus(key, value)
            delay(100)
        }
    }
    
    fun getStatus() = status.toMap()
}

class FlagUpdater {
    private var flags = mutableMapOf<String, Boolean>()
    private var updateJob: Job? = null
    
    suspend fun updateFlag(key: String, value: Boolean) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = flags.toMap()
            delay(50)
            flags[key] = value
            println("Flag updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleFlags(updates: Map<String, Boolean>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateFlag(key, value)
            delay(100)
        }
    }
    
    fun getFlags() = flags.toMap()
}

class CounterUpdater {
    private var counters = mutableMapOf<String, Int>()
    private var updateJob: Job? = null
    
    suspend fun updateCounter(key: String, value: Int) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = counters.toMap()
            delay(50)
            counters[key] = value
            println("Counter updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleCounters(updates: Map<String, Int>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateCounter(key, value)
            delay(100)
        }
    }
    
    fun getCounters() = counters.toMap()
}

class PropertyUpdater {
    private var properties = mutableMapOf<String, String>()
    private var updateJob: Job? = null
    
    suspend fun updateProperty(key: String, value: String) = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = properties.toMap()
            delay(50)
            properties[key] = value
            println("Property updated: $key = $value")
        }
    }
    
    suspend fun updateMultipleProperties(updates: Map<String, String>) = coroutineScope {
        updates.forEach { (key, value) ->
            updateProperty(key, value)
            delay(100)
        }
    }
    
    fun getProperties() = properties.toMap()
}

suspend fun simulateStateUpdater(
    updater: StateUpdater,
    updaterId: Int
) {
    repeat(10) { attempt ->
        updater.updateState("State$attempt", "Value$attempt")
        delay(Random.nextLong(50, 150))
    }
    
    println("State updater $updaterId completed")
}

suspend fun simulateStatusUpdater(
    updater: StatusUpdater,
    updaterId: Int
) {
    val updates = mapOf(
        "Status1" to "Active",
        "Status2" to "Inactive",
        "Status3" to "Pending",
        "Status4" to "Completed",
        "Status5" to "Failed"
    )
    
    updater.updateMultipleStatus(updates)
    
    println("Status updater $updaterId completed")
}

suspend fun simulateFlagUpdater(
    updater: FlagUpdater,
    updaterId: Int
) {
    val updates = mapOf(
        "Flag1" to true,
        "Flag2" to false,
        "Flag3" to true,
        "Flag4" to false,
        "Flag5" to true
    )
    
    updater.updateMultipleFlags(updates)
    
    println("Flag updater $updaterId completed")
}

suspend fun simulateCounterUpdater(
    updater: CounterUpdater,
    updaterId: Int
) {
    val updates = mapOf(
        "Counter1" to 10,
        "Counter2" to 20,
        "Counter3" to 30,
        "Counter4" to 40,
        "Counter5" to 50
    )
    
    updater.updateMultipleCounters(updates)
    
    println("Counter updater $updaterId completed")
}

suspend fun simulatePropertyUpdater(
    updater: PropertyUpdater,
    updaterId: Int
) {
    val updates = mapOf(
        "Property1" to "Value1",
        "Property2" to "Value2",
        "Property3" to "Value3",
        "Property4" to "Value4",
        "Property5" to "Value5"
    )
    
    updater.updateMultipleProperties(updates)
    
    println("Property updater $updaterId completed")
}

suspend fun monitorUpdaters(
    stateUpdater: StateUpdater,
    statusUpdater: StatusUpdater,
    flagUpdater: FlagUpdater,
    counterUpdater: CounterUpdater,
    propertyUpdater: PropertyUpdater,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  State: ${stateUpdater.getState()}")
        println("  Status: ${statusUpdater.getStatus()}")
        println("  Flags: ${flagUpdater.getFlags()}")
        println("  Counters: ${counterUpdater.getCounters()}")
        println("  Properties: ${propertyUpdater.getProperties()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting State Update Simulation...")
    println()
    
    val stateUpdater = StateUpdater()
    val statusUpdater = StatusUpdater()
    val flagUpdater = FlagUpdater()
    val counterUpdater = CounterUpdater()
    val propertyUpdater = PropertyUpdater()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateStateUpdater(stateUpdater, 1)
    })
    
    jobs.add(launch {
        simulateStateUpdater(stateUpdater, 2)
    })
    
    jobs.add(launch {
        simulateStatusUpdater(statusUpdater, 1)
    })
    
    jobs.add(launch {
        simulateFlagUpdater(flagUpdater, 1)
    })
    
    jobs.add(launch {
        simulateCounterUpdater(counterUpdater, 1)
    })
    
    jobs.add(launch {
        simulatePropertyUpdater(propertyUpdater, 1)
    })
    
    jobs.add(launch {
        monitorUpdaters(
            stateUpdater,
            statusUpdater,
            flagUpdater,
            counterUpdater,
            propertyUpdater,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Updated States ===")
    println("State: ${stateUpdater.getState()}")
    println("Status: ${statusUpdater.getStatus()}")
    println("Flags: ${flagUpdater.getFlags()}")
    println("Counters: ${counterUpdater.getCounters()}")
    println("Properties: ${propertyUpdater.getProperties()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels update jobs and immediately starts new ones:")
    println("  - StateUpdater.updateState() cancels updateJob and starts new one")
    println("  - StatusUpdater.updateStatus() cancels updateJob and starts new one")
    println("  - FlagUpdater.updateFlag() cancels updateJob and starts new one")
    println("  - CounterUpdater.updateCounter() cancels updateJob and starts new one")
    println("  - PropertyUpdater.updateProperty() cancels updateJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}