import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SharedFlowBufferOverflow {
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    suspend fun emit(value: Int) {
        sharedFlow.emit(value)
    }
    
    suspend fun emitMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            emit(value)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowSlowCollector {
    private var collectedCount = 0
    
    suspend fun collect(sharedFlow: SharedFlow<Int>, collectorId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { value ->
                collectedCount++
                println("Collector $collectorId received: $value (Count: $collectedCount)")
                delay(100)
            }
        }
    }
    
    fun getCollectedCount() = collectedCount
}

class SharedFlowFastEmitter {
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    suspend fun emit(value: Int) {
        sharedFlow.emit(value)
    }
    
    suspend fun emitMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            emit(value)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowSlowEmitter {
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    suspend fun emit(value: Int) {
        sharedFlow.emit(value)
    }
    
    suspend fun emitMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            emit(value)
            delay(100)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowFastCollector {
    private var collectedCount = 0
    
    suspend fun collect(sharedFlow: SharedFlow<Int>, collectorId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { value ->
                collectedCount++
                println("Collector $collectorId received: $value (Count: $collectedCount)")
                delay(10)
            }
        }
    }
    
    fun getCollectedCount() = collectedCount
}

suspend fun simulateSharedFlowBufferOverflow(
    overflow: SharedFlowBufferOverflow,
    overflowId: Int
) {
    repeat(100) { attempt ->
        overflow.emitMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("SharedFlow buffer overflow $overflowId completed")
}

suspend fun simulateSharedFlowFastEmitter(
    fastEmitter: SharedFlowFastEmitter,
    fastEmitterId: Int
) {
    repeat(100) { attempt ->
        fastEmitter.emitMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("SharedFlow fast emitter $fastEmitterId completed")
}

suspend fun simulateSharedFlowSlowEmitter(
    slowEmitter: SharedFlowSlowEmitter,
    slowEmitterId: Int
) {
    repeat(100) { attempt ->
        slowEmitter.emitMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("SharedFlow slow emitter $slowEmitterId completed")
}

suspend fun monitorSharedFlowBufferOverflows(
    overflow: SharedFlowBufferOverflow,
    fastEmitter: SharedFlowFastEmitter,
    slowEmitter: SharedFlowSlowEmitter,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Buffer overflow flow active: true")
        println("  Fast emitter flow active: true")
        println("  Slow emitter flow active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting SharedFlow Buffer Overflow Simulation...")
    println()
    
    val overflow = SharedFlowBufferOverflow()
    val fastEmitter = SharedFlowFastEmitter()
    val slowEmitter = SharedFlowSlowEmitter()
    
    val slowCollector1 = SharedFlowSlowCollector()
    val slowCollector2 = SharedFlowSlowCollector()
    val fastCollector1 = SharedFlowFastCollector()
    val fastCollector2 = SharedFlowFastCollector()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSharedFlowBufferOverflow(overflow, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowBufferOverflow(overflow, 2)
    })
    
    jobs.add(launch {
        simulateSharedFlowFastEmitter(fastEmitter, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowSlowEmitter(slowEmitter, 1)
    })
    
    jobs.add(launch {
        slowCollector1.collect(overflow.getFlow(), 1)
    })
    
    jobs.add(launch {
        slowCollector2.collect(overflow.getFlow(), 2)
    })
    
    jobs.add(launch {
        fastCollector1.collect(fastEmitter.getFlow(), 1)
    })
    
    jobs.add(launch {
        fastCollector2.collect(fastEmitter.getFlow(), 2)
    })
    
    jobs.add(launch {
        monitorSharedFlowBufferOverflows(
            overflow,
            fastEmitter,
            slowEmitter,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  SharedFlow Buffer Overflow Warning:")
    println("  The code uses small buffer capacity with fast emitters and slow collectors:")
    println("  - SharedFlowBufferOverflow has capacity 2")
    println("  - SharedFlowFastEmitter has capacity 2")
    println("  - SharedFlowSlowEmitter has capacity 2")
    println("  Small buffer with fast emitters and slow collectors causes buffer overflow,")
    println("  leading to emitters being suspended and potential event loss.")
    println("  Fix: Increase buffer capacity or use BufferOverflow.DROP_OLDEST.")
}