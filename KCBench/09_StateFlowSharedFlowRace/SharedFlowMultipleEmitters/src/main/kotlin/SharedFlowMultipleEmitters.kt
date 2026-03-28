import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SharedFlowEmitter {
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

class SharedFlowCollector {
    private var collectedCount = 0
    
    suspend fun collect(sharedFlow: SharedFlow<Int>, collectorId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { value ->
                collectedCount++
                println("Collector $collectorId received: $value (Count: $collectedCount)")
                delay(50)
            }
        }
    }
    
    fun getCollectedCount() = collectedCount
}

class SharedFlowBroadcaster {
    private val sharedFlow = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    suspend fun broadcast(message: String) {
        sharedFlow.emit(message)
    }
    
    suspend fun broadcastMultiple(messages: List<String>) = coroutineScope {
        messages.forEach { message ->
            broadcast(message)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowSubscriber {
    private var receivedCount = 0
    
    suspend fun subscribe(sharedFlow: SharedFlow<String>, subscriberId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { message ->
                receivedCount++
                println("Subscriber $subscriberId received: $message (Count: $receivedCount)")
                delay(50)
            }
        }
    }
    
    fun getReceivedCount() = receivedCount
}

class SharedFlowPublisher {
    private val sharedFlow = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    data class Event(val id: Int, val name: String)
    
    suspend fun publish(event: Event) {
        sharedFlow.emit(event)
    }
    
    suspend fun publishMultiple(events: List<Event>) = coroutineScope {
        events.forEach { event ->
            publish(event)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowListener {
    private var listenedCount = 0
    
    suspend fun listen(sharedFlow: SharedFlow<SharedFlowPublisher.Event>, listenerId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { event ->
                listenedCount++
                println("Listener $listenerId received: ${event.name} (Count: $listenedCount)")
                delay(50)
            }
        }
    }
    
    fun getListenedCount() = listenedCount
}

suspend fun simulateSharedFlowEmitter(
    emitter: SharedFlowEmitter,
    emitterId: Int
) {
    repeat(100) { attempt ->
        emitter.emitMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("SharedFlow emitter $emitterId completed")
}

suspend fun simulateSharedFlowBroadcaster(
    broadcaster: SharedFlowBroadcaster,
    broadcasterId: Int
) {
    repeat(100) { attempt ->
        broadcaster.broadcastMultiple(listOf(
            "Message1",
            "Message2",
            "Message3",
            "Message4",
            "Message5"
        ))
        delay(50)
    }
    
    println("SharedFlow broadcaster $broadcasterId completed")
}

suspend fun simulateSharedFlowPublisher(
    publisher: SharedFlowPublisher,
    publisherId: Int
) {
    repeat(100) { attempt ->
        publisher.publishMultiple(listOf(
            SharedFlowPublisher.Event(1, "Event1"),
            SharedFlowPublisher.Event(2, "Event2"),
            SharedFlowPublisher.Event(3, "Event3"),
            SharedFlowPublisher.Event(4, "Event4"),
            SharedFlowPublisher.Event(5, "Event5")
        ))
        delay(50)
    }
    
    println("SharedFlow publisher $publisherId completed")
}

suspend fun monitorSharedFlows(
    emitter: SharedFlowEmitter,
    broadcaster: SharedFlowBroadcaster,
    publisher: SharedFlowPublisher,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Emitter flow active: true")
        println("  Broadcaster flow active: true")
        println("  Publisher flow active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting SharedFlow Multiple Emitters Simulation...")
    println()
    
    val emitter = SharedFlowEmitter()
    val broadcaster = SharedFlowBroadcaster()
    val publisher = SharedFlowPublisher()
    
    val collector1 = SharedFlowCollector()
    val collector2 = SharedFlowCollector()
    val subscriber1 = SharedFlowSubscriber()
    val subscriber2 = SharedFlowSubscriber()
    val listener1 = SharedFlowListener()
    val listener2 = SharedFlowListener()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSharedFlowEmitter(emitter, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowEmitter(emitter, 2)
    })
    
    jobs.add(launch {
        simulateSharedFlowBroadcaster(broadcaster, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowPublisher(publisher, 1)
    })
    
    jobs.add(launch {
        collector1.collect(emitter.getFlow(), 1)
    })
    
    jobs.add(launch {
        collector2.collect(emitter.getFlow(), 2)
    })
    
    jobs.add(launch {
        subscriber1.subscribe(broadcaster.getFlow(), 1)
    })
    
    jobs.add(launch {
        subscriber2.subscribe(broadcaster.getFlow(), 2)
    })
    
    jobs.add(launch {
        listener1.listen(publisher.getFlow(), 1)
    })
    
    jobs.add(launch {
        listener2.listen(publisher.getFlow(), 2)
    })
    
    jobs.add(launch {
        monitorSharedFlows(
            emitter,
            broadcaster,
            publisher,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  SharedFlow Multiple Emitters Warning:")
    println("  The code has multiple emitters sending to the same SharedFlow:")
    println("  - SharedFlowEmitter has multiple emitters")
    println("  - SharedFlowBroadcaster has multiple emitters")
    println("  - SharedFlowPublisher has multiple emitters")
    println("  Multiple emitters may cause race conditions and event loss.")
    println("  Fix: Use Mutex to synchronize emitters or use separate SharedFlows.")
}