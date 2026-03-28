import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SharedFlowEventLoss {
    private val sharedFlow = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
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

class SharedFlowEventListener {
    private var receivedCount = 0
    
    suspend fun listen(sharedFlow: SharedFlow<SharedFlowEventLoss.Event>, listenerId: Int) = coroutineScope {
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

class SharedFlowMessageLoss {
    private val sharedFlow = MutableSharedFlow<Message>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
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

class SharedFlowMessageReceiver {
    private var receivedCount = 0
    
    suspend fun receive(sharedFlow: SharedFlow<SharedFlowMessageLoss.Message>, receiverId: Int) = coroutineScope {
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

class SharedFlowNotificationLoss {
    private val sharedFlow = MutableSharedFlow<Notification>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    data class Notification(val id: Int, val title: String)
    
    suspend fun notify(notification: Notification) {
        sharedFlow.emit(notification)
    }
    
    suspend fun notifyMultiple(notifications: List<Notification>) = coroutineScope {
        notifications.forEach { notification ->
            notify(notification)
            delay(10)
        }
    }
    
    fun getFlow() = sharedFlow.asSharedFlow()
}

class SharedFlowNotificationObserver {
    private var observedCount = 0
    
    suspend fun observe(sharedFlow: SharedFlow<SharedFlowNotificationLoss.Notification>, observerId: Int) = coroutineScope {
        launch {
            sharedFlow.collect { notification ->
                observedCount++
                println("Observer $observerId observed: ${notification.title} (Count: $observedCount)")
                delay(50)
            }
        }
    }
    
    fun getObservedCount() = observedCount
}

suspend fun simulateSharedFlowEventLoss(
    eventLoss: SharedFlowEventLoss,
    eventLossId: Int
) {
    repeat(100) { attempt ->
        eventLoss.emitMultiple(listOf(
            SharedFlowEventLoss.Event(1, "Event1"),
            SharedFlowEventLoss.Event(2, "Event2"),
            SharedFlowEventLoss.Event(3, "Event3"),
            SharedFlowEventLoss.Event(4, "Event4"),
            SharedFlowEventLoss.Event(5, "Event5")
        ))
        delay(50)
    }
    
    println("SharedFlow event loss $eventLossId completed")
}

suspend fun simulateSharedFlowMessageLoss(
    messageLoss: SharedFlowMessageLoss,
    messageLossId: Int
) {
    repeat(100) { attempt ->
        messageLoss.sendMultiple(listOf(
            SharedFlowMessageLoss.Message(1, "Message1"),
            SharedFlowMessageLoss.Message(2, "Message2"),
            SharedFlowMessageLoss.Message(3, "Message3"),
            SharedFlowMessageLoss.Message(4, "Message4"),
            SharedFlowMessageLoss.Message(5, "Message5")
        ))
        delay(50)
    }
    
    println("SharedFlow message loss $messageLossId completed")
}

suspend fun simulateSharedFlowNotificationLoss(
    notificationLoss: SharedFlowNotificationLoss,
    notificationLossId: Int
) {
    repeat(100) { attempt ->
        notificationLoss.notifyMultiple(listOf(
            SharedFlowNotificationLoss.Notification(1, "Notification1"),
            SharedFlowNotificationLoss.Notification(2, "Notification2"),
            SharedFlowNotificationLoss.Notification(3, "Notification3"),
            SharedFlowNotificationLoss.Notification(4, "Notification4"),
            SharedFlowNotificationLoss.Notification(5, "Notification5")
        ))
        delay(50)
    }
    
    println("SharedFlow notification loss $notificationLossId completed")
}

suspend fun monitorSharedFlowEventLosses(
    eventLoss: SharedFlowEventLoss,
    messageLoss: SharedFlowMessageLoss,
    notificationLoss: SharedFlowNotificationLoss,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Event loss flow active: true")
        println("  Message loss flow active: true")
        println("  Notification loss flow active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting SharedFlow Event Loss Simulation...")
    println()
    
    val eventLoss = SharedFlowEventLoss()
    val messageLoss = SharedFlowMessageLoss()
    val notificationLoss = SharedFlowNotificationLoss()
    
    val listener1 = SharedFlowEventListener()
    val listener2 = SharedFlowEventListener()
    val receiver1 = SharedFlowMessageReceiver()
    val receiver2 = SharedFlowMessageReceiver()
    val observer1 = SharedFlowNotificationObserver()
    val observer2 = SharedFlowNotificationObserver()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSharedFlowEventLoss(eventLoss, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowEventLoss(eventLoss, 2)
    })
    
    jobs.add(launch {
        simulateSharedFlowMessageLoss(messageLoss, 1)
    })
    
    jobs.add(launch {
        simulateSharedFlowNotificationLoss(notificationLoss, 1)
    })
    
    jobs.add(launch {
        listener1.listen(eventLoss.getFlow(), 1)
    })
    
    jobs.add(launch {
        listener2.listen(eventLoss.getFlow(), 2)
    })
    
    jobs.add(launch {
        receiver1.receive(messageLoss.getFlow(), 1)
    })
    
    jobs.add(launch {
        receiver2.receive(messageLoss.getFlow(), 2)
    })
    
    jobs.add(launch {
        observer1.observe(notificationLoss.getFlow(), 1)
    })
    
    jobs.add(launch {
        observer2.observe(notificationLoss.getFlow(), 2)
    })
    
    jobs.add(launch {
        monitorSharedFlowEventLosses(
            eventLoss,
            messageLoss,
            notificationLoss,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  SharedFlow Event Loss Warning:")
    println("  The code uses DROP_OLDEST buffer overflow strategy with fast emitters:")
    println("  - SharedFlowEventLoss uses DROP_OLDEST")
    println("  - SharedFlowMessageLoss uses DROP_OLDEST")
    println("  - SharedFlowNotificationLoss uses DROP_OLDEST")
    println("  DROP_OLDEST strategy with fast emitters causes event loss,")
    println("  leading to missed events and incorrect state.")
    println("  Fix: Increase buffer capacity or use SUSPEND strategy.")
}