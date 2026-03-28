import kotlinx.coroutines.*
import kotlin.random.Random

class ConcurrentUpdater {
    private var counter = 0
    private var updateJob: Job? = null
    
    suspend fun updateCounter() = coroutineScope {
        updateJob?.cancel()
        
        updateJob = launch {
            val tmp = counter
            delay(50)
            counter = tmp + 1
            println("Counter updated to $counter")
        }
    }
    
    suspend fun updateMultipleCounters(times: Int) = coroutineScope {
        repeat(times) {
            updateCounter()
            delay(100)
        }
    }
    
    fun getCounter() = counter
}

class ConcurrentIncrementer {
    private var incrementCount = 0
    private var incrementJob: Job? = null
    
    suspend fun incrementCounter() = coroutineScope {
        incrementJob?.cancel()
        
        incrementJob = launch {
            val tmp = incrementCount
            delay(50)
            incrementCount = tmp + 1
            println("Increment count updated to $incrementCount")
        }
    }
    
    suspend fun incrementMultipleCounters(times: Int) = coroutineScope {
        repeat(times) {
            incrementCounter()
            delay(100)
        }
    }
    
    fun getIncrementCount() = incrementCount
}

class ConcurrentModifier {
    private var modifyCount = 0
    private var modifyJob: Job? = null
    
    suspend fun modifyCounter() = coroutineScope {
        modifyJob?.cancel()
        
        modifyJob = launch {
            val tmp = modifyCount
            delay(50)
            modifyCount = tmp + 1
            println("Modify count updated to $modifyCount")
        }
    }
    
    suspend fun modifyMultipleCounters(times: Int) = coroutineScope {
        repeat(times) {
            modifyCounter()
            delay(100)
        }
    }
    
    fun getModifyCount() = modifyCount
}

class ConcurrentChanger {
    private var changeCount = 0
    private var changeJob: Job? = null
    
    suspend fun changeCounter() = coroutineScope {
        changeJob?.cancel()
        
        changeJob = launch {
            val tmp = changeCount
            delay(50)
            changeCount = tmp + 1
            println("Change count updated to $changeCount")
        }
    }
    
    suspend fun changeMultipleCounters(times: Int) = coroutineScope {
        repeat(times) {
            changeCounter()
            delay(100)
        }
    }
    
    fun getChangeCount() = changeCount
}

class ConcurrentAdjuster {
    private var adjustCount = 0
    private var adjustJob: Job? = null
    
    suspend fun adjustCounter() = coroutineScope {
        adjustJob?.cancel()
        
        adjustJob = launch {
            val tmp = adjustCount
            delay(50)
            adjustCount = tmp + 1
            println("Adjust count updated to $adjustCount")
        }
    }
    
    suspend fun adjustMultipleCounters(times: Int) = coroutineScope {
        repeat(times) {
            adjustCounter()
            delay(100)
        }
    }
    
    fun getAdjustCount() = adjustCount
}

suspend fun simulateConcurrentUpdater(
    updater: ConcurrentUpdater,
    updaterId: Int
) {
    repeat(10) { attempt ->
        updater.updateCounter()
        delay(Random.nextLong(50, 150))
    }
    
    println("Concurrent updater $updaterId completed")
}

suspend fun simulateConcurrentIncrementer(
    incrementer: ConcurrentIncrementer,
    incrementerId: Int
) {
    repeat(10) { attempt ->
        incrementer.incrementCounter()
        delay(Random.nextLong(50, 150))
    }
    
    println("Concurrent incrementer $incrementerId completed")
}

suspend fun simulateConcurrentModifier(
    modifier: ConcurrentModifier,
    modifierId: Int
) {
    repeat(10) { attempt ->
        modifier.modifyCounter()
        delay(Random.nextLong(50, 150))
    }
    
    println("Concurrent modifier $modifierId completed")
}

suspend fun simulateConcurrentChanger(
    changer: ConcurrentChanger,
    changerId: Int
) {
    repeat(10) { attempt ->
        changer.changeCounter()
        delay(Random.nextLong(50, 150))
    }
    
    println("Concurrent changer $changerId completed")
}

suspend fun simulateConcurrentAdjuster(
    adjuster: ConcurrentAdjuster,
    adjusterId: Int
) {
    repeat(10) { attempt ->
        adjuster.adjustCounter()
        delay(Random.nextLong(50, 150))
    }
    
    println("Concurrent adjuster $adjusterId completed")
}

suspend fun monitorConcurrentUpdaters(
    concurrentUpdater: ConcurrentUpdater,
    concurrentIncrementer: ConcurrentIncrementer,
    concurrentModifier: ConcurrentModifier,
    concurrentChanger: ConcurrentChanger,
    concurrentAdjuster: ConcurrentAdjuster,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Counter: ${concurrentUpdater.getCounter()}")
        println("  Increment count: ${concurrentIncrementer.getIncrementCount()}")
        println("  Modify count: ${concurrentModifier.getModifyCount()}")
        println("  Change count: ${concurrentChanger.getChangeCount()}")
        println("  Adjust count: ${concurrentAdjuster.getAdjustCount()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Concurrent Update Simulation...")
    println()
    
    val concurrentUpdater = ConcurrentUpdater()
    val concurrentIncrementer = ConcurrentIncrementer()
    val concurrentModifier = ConcurrentModifier()
    val concurrentChanger = ConcurrentChanger()
    val concurrentAdjuster = ConcurrentAdjuster()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateConcurrentUpdater(concurrentUpdater, 1)
    })
    
    jobs.add(launch {
        simulateConcurrentUpdater(concurrentUpdater, 2)
    })
    
    jobs.add(launch {
        simulateConcurrentIncrementer(concurrentIncrementer, 1)
    })
    
    jobs.add(launch {
        simulateConcurrentModifier(concurrentModifier, 1)
    })
    
    jobs.add(launch {
        simulateConcurrentChanger(concurrentChanger, 1)
    })
    
    jobs.add(launch {
        simulateConcurrentAdjuster(concurrentAdjuster, 1)
    })
    
    jobs.add(launch {
        monitorConcurrentUpdaters(
            concurrentUpdater,
            concurrentIncrementer,
            concurrentModifier,
            concurrentChanger,
            concurrentAdjuster,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Concurrent Update Counts ===")
    println("Counter: ${concurrentUpdater.getCounter()}")
    println("Increment count: ${concurrentIncrementer.getIncrementCount()}")
    println("Modify count: ${concurrentModifier.getModifyCount()}")
    println("Change count: ${concurrentChanger.getChangeCount()}")
    println("Adjust count: ${concurrentAdjuster.getAdjustCount()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels update jobs and immediately starts new ones:")
    println("  - ConcurrentUpdater.updateCounter() cancels updateJob and starts new one")
    println("  - ConcurrentIncrementer.incrementCounter() cancels incrementJob and starts new one")
    println("  - ConcurrentModifier.modifyCounter() cancels modifyJob and starts new one")
    println("  - ConcurrentChanger.changeCounter() cancels changeJob and starts new one")
    println("  - ConcurrentAdjuster.adjustCounter() cancels adjustJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}