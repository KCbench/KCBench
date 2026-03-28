import kotlinx.coroutines.*
import kotlin.random.Random

class CounterManager {
    private var counter = 0
    private var refreshJob: Job? = null
    
    suspend fun refresh() = coroutineScope {
        refreshJob?.cancel()
        
        refreshJob = launch {
            val tmp = counter
            delay(50)
            counter = tmp + 1
            println("Counter updated to $counter")
        }
    }
    
    suspend fun refreshMultiple(times: Int) = coroutineScope {
        repeat(times) {
            refresh()
            delay(100)
        }
    }
    
    fun getCounter() = counter
}

class DataCounter {
    private var dataCount = 0
    private var updateJob: Job? = null
    
    suspend fun updateData() = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = dataCount
            delay(50)
            dataCount = tmp + 1
            println("Data count updated to $dataCount")
        }
    }
    
    suspend fun updateMultipleData(counts: List<Int>) = coroutineScope {
        counts.forEach { count ->
            updateData()
            delay(100)
        }
    }
    
    fun getDataCount() = dataCount
}

class RefreshCounter {
    private var refreshCount = 0
    private var refreshJob: Job? = null
    
    suspend fun refresh() = coroutineScope {
        refreshJob?.cancel()
        
        refreshJob = launch {
            val tmp = refreshCount
            delay(50)
            refreshCount = tmp + 1
            println("Refresh count updated to $refreshCount")
        }
    }
    
    suspend fun refreshMultiple(times: Int) = coroutineScope {
        repeat(times) {
            refresh()
            delay(100)
        }
    }
    
    fun getRefreshCount() = refreshCount
}

class IncrementCounter {
    private var incrementCount = 0
    private var incrementJob: Job? = null
    
    suspend fun increment() = coroutineScope {
        incrementJob?.cancel()
        
        incrementJob = launch {
            val tmp = incrementCount
            delay(50)
            incrementCount = tmp + 1
            println("Increment count updated to $incrementCount")
        }
    }
    
    suspend fun incrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            increment()
            delay(100)
        }
    }
    
    fun getIncrementCount() = incrementCount
}

class UpdateCounter {
    private var updateCount = 0
    private var updateJob: Job? = null
    
    suspend fun update() = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = updateCount
            delay(50)
            updateCount = tmp + 1
            println("Update count updated to $updateCount")
        }
    }
    
    suspend fun updateMultiple(times: Int) = coroutineScope {
        repeat(times) {
            update()
            delay(100)
        }
    }
    
    fun getUpdateCount() = updateCount
}

suspend fun simulateCounterRefresh(
    manager: CounterManager,
    refreshId: Int
) {
    repeat(10) { attempt ->
        manager.refresh()
        delay(Random.nextLong(50, 150))
    }
    
    println("Counter refresh $refreshId completed")
}

suspend fun simulateDataCounter(
    counter: DataCounter,
    counterId: Int
) {
    val counts = (1..10).toList()
    
    counter.updateMultipleData(counts)
    
    println("Data counter $counterId completed")
}

suspend fun simulateRefreshCounter(
    counter: RefreshCounter,
    counterId: Int
) {
    repeat(10) { attempt ->
        counter.refresh()
        delay(Random.nextLong(50, 150))
    }
    
    println("Refresh counter $counterId completed")
}

suspend fun simulateIncrementCounter(
    counter: IncrementCounter,
    counterId: Int
) {
    repeat(10) { attempt ->
        counter.increment()
        delay(Random.nextLong(50, 150))
    }
    
    println("Increment counter $counterId completed")
}

suspend fun simulateUpdateCounter(
    counter: UpdateCounter,
    counterId: Int
) {
    repeat(10) { attempt ->
        counter.update()
        delay(Random.nextLong(50, 150))
    }
    
    println("Update counter $counterId completed")
}

suspend fun monitorCounters(
    counterManager: CounterManager,
    dataCounter: DataCounter,
    refreshCounter: RefreshCounter,
    incrementCounter: IncrementCounter,
    updateCounter: UpdateCounter,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Counter: ${counterManager.getCounter()}")
        println("  Data count: ${dataCounter.getDataCount()}")
        println("  Refresh count: ${refreshCounter.getRefreshCount()}")
        println("  Increment count: ${incrementCounter.getIncrementCount()}")
        println("  Update count: ${updateCounter.getUpdateCount()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Counter Refresh Simulation...")
    println()
    
    val counterManager = CounterManager()
    val dataCounter = DataCounter()
    val refreshCounter = RefreshCounter()
    val incrementCounter = IncrementCounter()
    val updateCounter = UpdateCounter()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateCounterRefresh(counterManager, 1)
    })
    
    jobs.add(launch {
        simulateCounterRefresh(counterManager, 2)
    })
    
    jobs.add(launch {
        simulateDataCounter(dataCounter, 1)
    })
    
    jobs.add(launch {
        simulateRefreshCounter(refreshCounter, 1)
    })
    
    jobs.add(launch {
        simulateIncrementCounter(incrementCounter, 1)
    })
    
    jobs.add(launch {
        simulateUpdateCounter(updateCounter, 1)
    })
    
    jobs.add(launch {
        monitorCounters(
            counterManager,
            dataCounter,
            refreshCounter,
            incrementCounter,
            updateCounter,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Counter Values ===")
    println("Counter: ${counterManager.getCounter()}")
    println("Data count: ${dataCounter.getDataCount()}")
    println("Refresh count: ${refreshCounter.getRefreshCount()}")
    println("Increment count: ${incrementCounter.getIncrementCount()}")
    println("Update count: ${updateCounter.getUpdateCount()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels jobs and immediately starts new ones:")
    println("  - CounterManager.refresh() cancels refreshJob and starts new one")
    println("  - DataCounter.updateData() cancels updateJob and starts new one")
    println("  - RefreshCounter.refresh() cancels refreshJob and starts new one")
    println("  - IncrementCounter.increment() cancels incrementJob and starts new one")
    println("  - UpdateCounter.update() cancels updateJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}