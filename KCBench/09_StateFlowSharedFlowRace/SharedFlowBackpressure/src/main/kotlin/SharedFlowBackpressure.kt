import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SharedFlowBackpressure {
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 10,
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

class SharedFlowSlowProcessor {
    private var processedCount = 0
    
    suspend fun process(sharedFlow: SharedFlow<Int>, processorId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { value ->
                processedCount++
                println("Processor $processorId processing: $value (Count: $processedCount)")
                delay(200)
            }
        }
    }
    
    fun getProcessedCount() = processedCount
}

class SharedFlowFastProducer {
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    suspend fun produce(value: Int) {
        sharedFlow.emit(value)
    }
    
    suspend fun produceMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            produce(value)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowSlowConsumer {
    private var consumedCount = 0
    
    suspend fun consume(sharedFlow: SharedFlow<Int>, consumerId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { value ->
                consumedCount++
                println("Consumer $consumerId consuming: $value (Count: $consumedCount)")
                delay(200)
            }
        }
    }
    
    fun getConsumedCount() = consumedCount
}

class SharedFlowHighVolume {
    private val sharedFlow = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    suspend fun generate(value: Int) {
        sharedFlow.emit(value)
    }
    
    suspend fun generateMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            generate(value)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowLowCapacity {
    private var handledCount = 0
    
    suspend fun handle(sharedFlow: SharedFlow<Int>, handlerId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { value ->
                handledCount++
                println("Handler $handlerId handling: $value (Count: $handledCount)")
                delay(200)
            }
        }
    }
    
    fun getHandledCount() = handledCount
}

suspend fun simulateSharedFlowBackpressure(
    backpressure: SharedFlowBackpressure,
    backpressureId: Int
) {
    repeat(100) { attempt ->
        backpressure.emitMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("SharedFlow backpressure $backpressureId completed")
}

suspend fun simulateSharedFlowFastProducer(
    fastProducer: SharedFlowFastProducer,
    fastProducerId: Int
) {
    repeat(100) { attempt ->
        fastProducer.produceMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("SharedFlow fast producer $fastProducerId completed")
}

suspend fun simulateSharedFlowHighVolume(
    highVolume: SharedFlowHighVolume,
    highVolumeId: Int
) {
    repeat(100) { attempt ->
        highVolume.generateMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("SharedFlow high volume $highVolumeId completed")
}

suspend fun monitorSharedFlowBackpressures(
    backpressure: SharedFlowBackpressure,
    fastProducer: SharedFlowFastProducer,
    highVolume: SharedFlowHighVolume,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Backpressure flow active: true")
        println("  Fast producer flow active: true")
        println("  High volume flow active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting SharedFlow Backpressure Simulation...")
    println()
    
    val backpressure = SharedFlowBackpressure()
    val fastProducer = SharedFlowFastProducer()
    val highVolume = SharedFlowHighVolume()
    
    val slowProcessor1 = SharedFlowSlowProcessor()
    val slowProcessor2 = SharedFlowSlowProcessor()
    val slowConsumer1 = SharedFlowSlowConsumer()
    val slowConsumer2 = SharedFlowSlowConsumer()
    val lowCapacity1 = SharedFlowLowCapacity()
    val lowCapacity2 = SharedFlowLowCapacity()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSharedFlowBackpressure(backpressure, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowBackpressure(backpressure, 2)
    })
    
    jobs.add(launch {
        simulateSharedFlowFastProducer(fastProducer, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowHighVolume(highVolume, 1)
    })
    
    jobs.add(launch {
        slowProcessor1.process(backpressure.getFlow(), 1)
    })
    
    jobs.add(launch {
        slowProcessor2.process(backpressure.getFlow(), 2)
    })
    
    jobs.add(launch {
        slowConsumer1.consume(fastProducer.getFlow(), 1)
    })
    
    jobs.add(launch {
        slowConsumer2.consume(fastProducer.getFlow(), 2)
    })
    
    jobs.add(launch {
        lowCapacity1.handle(highVolume.getFlow(), 1)
    })
    
    jobs.add(launch {
        lowCapacity2.handle(highVolume.getFlow(), 2)
    })
    
    jobs.add(launch {
        monitorSharedFlowBackpressures(
            backpressure,
            fastProducer,
            highVolume,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  SharedFlow Backpressure Warning:")
    println("  The code has backpressure issues with fast producers and slow consumers:")
    println("  - SharedFlowBackpressure has fast emitters and slow processors")
    println("  - SharedFlowFastProducer has fast emitters and slow consumers")
    println("  - SharedFlowHighVolume has fast emitters and slow handlers")
    println("  Backpressure issues cause emitters to be suspended,")
    println("  leading to performance degradation and potential event loss.")
    println("  Fix: Increase buffer capacity or use BufferOverflow.DROP_OLDEST.")
}