import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.BufferOverflow

class StateFlowMisuse {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter
    
    suspend fun increment() {
        _counter.value = _counter.value + 1
    }
    
    suspend fun decrement() {
        _counter.value = _counter.value - 1
    }
    
    suspend fun reset() {
        _counter.value = 0
    }
    
    suspend fun incrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            increment()
            delay(10)
        }
    }
    
    suspend fun decrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            decrement()
            delay(10)
        }
    }
    
    suspend fun resetMultiple(times: Int) = coroutineScope {
        repeat(times) {
            reset()
            delay(10)
        }
    }
    
    fun getValue() = counter.value
}

class SharedFlowMisuse {
    private val sharedFlow = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    data class Event(val id: Int, val name: String)
    
    suspend fun emit(event: Event) {
        sharedFlow.emit(event)
    }
    
    suspend fun emitMultiple(events: List<Event>) = coroutineScope {
        events.forEach { event ->
            emit(event)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowMisuseListener {
    private var receivedCount = 0
    
    suspend fun listen(sharedFlow: SharedFlow<SharedFlowMisuse.Event>, listenerId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { event ->
                receivedCount++
                println("Listener $listenerId received: ${event.name} (Count: $receivedCount)")
                delay(50)
            }
        }
    }
    
    fun getReceivedCount() = receivedCount
}

class StateFlowConcurrentMisuse {
    private val _concurrent = MutableStateFlow(0)
    val concurrent: StateFlow<Int> = _concurrent
    
    suspend fun update(value: Int) {
        _concurrent.value = _concurrent.value + value
    }
    
    suspend fun updateMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            update(value)
            delay(10)
        }
    }
    
    fun getValue() = concurrent.value
}

class SharedFlowConcurrentMisuse {
    private val sharedFlow = MutableSharedFlow<Message>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    
    data class Message(val id: Int, val content: String)
    
    suspend fun send(message: Message) {
        sharedFlow.emit(message)
    }
    
    suspend fun sendMultiple(messages: List<Message>) = coroutineScope {
        messages.forEach { message ->
            send(message)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowConcurrentMisuseReceiver {
    private var receivedCount = 0
    
    suspend fun receive(sharedFlow: SharedFlow<SharedFlowConcurrentMisuse.Message>, receiverId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { message ->
                receivedCount++
                println("Receiver $receiverId received: ${message.content} (Count: $receivedCount)")
                delay(50)
            }
        }
    }
    
    fun getReceivedCount() = receivedCount
}

suspend fun simulateStateFlowMisuse(
    misuse: StateFlowMisuse,
    misuseId: Int
) {
    repeat(100) { attempt ->
        misuse.incrementMultiple(10)
        misuse.decrementMultiple(10)
        misuse.resetMultiple(5)
        delay(50)
    }
    
    println("StateFlow misuse $misuseId completed")
}

suspend fun simulateSharedFlowMisuse(
    misuse: SharedFlowMisuse,
    misuseId: Int
) {
    repeat(100) { attempt ->
        misuse.emitMultiple(listOf(
            SharedFlowMisuse.Event(1, "Event1"),
            SharedFlowMisuse.Event(2, "Event2"),
            SharedFlowMisuse.Event(3, "Event3"),
            SharedFlowMisuse.Event(4, "Event4"),
            SharedFlowMisuse.Event(5, "Event5")
        ))
        delay(50)
    }
    
    println("SharedFlow misuse $misuseId completed")
}

suspend fun simulateStateFlowConcurrentMisuse(
    concurrentMisuse: StateFlowConcurrentMisuse,
    concurrentMisuseId: Int
) {
    repeat(100) { attempt ->
        concurrentMisuse.updateMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow concurrent misuse $concurrentMisuseId completed")
}

suspend fun simulateSharedFlowConcurrentMisuse(
    concurrentMisuse: SharedFlowConcurrentMisuse,
    concurrentMisuseId: Int
) {
    repeat(100) { attempt ->
        concurrentMisuse.sendMultiple(listOf(
            SharedFlowConcurrentMisuse.Message(1, "Message1"),
            SharedFlowConcurrentMisuse.Message(2, "Message2"),
            SharedFlowConcurrentMisuse.Message(3, "Message3"),
            SharedFlowConcurrentMisuse.Message(4, "Message4"),
            SharedFlowConcurrentMisuse.Message(5, "Message5")
        ))
        delay(50)
    }
    
    println("SharedFlow concurrent misuse $concurrentMisuseId completed")
}

suspend fun monitorStateFlowSharedFlowMisuse(
    misuse: StateFlowMisuse,
    sharedFlowMisuse: SharedFlowMisuse,
    concurrentMisuse: StateFlowConcurrentMisuse,
    sharedFlowConcurrentMisuse: SharedFlowConcurrentMisuse,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  StateFlow misuse counter: ${misuse.getValue()}")
        println("  SharedFlow misuse flow active: true")
        println("  StateFlow concurrent misuse value: ${concurrentMisuse.getValue()}")
        println("  SharedFlow concurrent misuse flow active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting StateFlow/SharedFlow Misuse Simulation...")
    println()
    
    val misuse = StateFlowMisuse()
    val sharedFlowMisuse = SharedFlowMisuse()
    val concurrentMisuse = StateFlowConcurrentMisuse()
    val sharedFlowConcurrentMisuse = SharedFlowConcurrentMisuse()
    
    val listener1 = SharedFlowMisuseListener()
    val listener2 = SharedFlowMisuseListener()
    val receiver1 = SharedFlowConcurrentMisuseReceiver()
    val receiver2 = SharedFlowConcurrentMisuseReceiver()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateStateFlowMisuse(misuse, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowMisuse(misuse, 2)
    })
    
    jobs.add(launch {
        simulateSharedFlowMisuse(sharedFlowMisuse, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowMisuse(sharedFlowMisuse, 2)
    })
    
    jobs.add(launch {
        simulateStateFlowConcurrentMisuse(concurrentMisuse, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowConcurrentMisuse(sharedFlowConcurrentMisuse, 1)
    })
    
    jobs.add(launch {
        listener1.listen(sharedFlowMisuse.getFlow(), 1)
    })
    
    jobs.add(launch {
        listener2.listen(sharedFlowMisuse.getFlow(), 2)
    })
    
    jobs.add(launch {
        receiver1.receive(sharedFlowConcurrentMisuse.getFlow(), 1)
    })
    
    jobs.add(launch {
        receiver2.receive(sharedFlowConcurrentMisuse.getFlow(), 2)
    })
    
    jobs.add(launch {
        monitorStateFlowSharedFlowMisuse(
            misuse,
            sharedFlowMisuse,
            concurrentMisuse,
            sharedFlowConcurrentMisuse,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  StateFlow/SharedFlow Misuse Warning:")
    println("  The code misuses StateFlow and SharedFlow:")
    println("  - StateFlowMisuse uses non-atomic updates")
    println("  - SharedFlowMisuse has multiple emitters")
    println("  - StateFlowConcurrentMisuse uses non-atomic updates")
    println("  - SharedFlowConcurrentMisuse has multiple emitters")
    println("  Misuse of StateFlow and SharedFlow can cause race conditions,")
    println("  lost updates, and event loss.")
    println("  Fix: Use update {} for atomic StateFlow updates and Mutex for SharedFlow emitters.")
}