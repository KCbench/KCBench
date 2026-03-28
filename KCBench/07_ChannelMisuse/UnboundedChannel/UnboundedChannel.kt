import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class UnboundedMessageProducer {
    private val channel = Channel<Int>(capacity = Channel.UNLIMITED)
    
    suspend fun produceMessages(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(index)
                println("Produced: $index")
                delay(10)
            }
            channel.close()
            println("Channel closed")
        }
    }
    
    suspend fun consumeMessages() = coroutineScope {
        launch {
            for (message in channel) {
                println("Consumed: $message")
                delay(1000)
            }
            println("Consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class UnboundedDataProducer {
    private val channel = Channel<String>(capacity = Channel.UNLIMITED)
    
    suspend fun produceData(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send("Data$index")
                println("Produced: Data$index")
                delay(10)
            }
            channel.close()
            println("Data channel closed")
        }
    }
    
    suspend fun consumeData() = coroutineScope {
        launch {
            for (data in channel) {
                println("Consumed: $data")
                delay(1000)
            }
            println("Data consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class UnboundedEventProducer {
    private val channel = Channel<Event>(capacity = Channel.UNLIMITED)
    
    data class Event(val id: Int, val name: String)
    
    suspend fun produceEvents(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Event(index, "Event$index"))
                println("Produced: Event$index")
                delay(10)
            }
            channel.close()
            println("Event channel closed")
        }
    }
    
    suspend fun consumeEvents() = coroutineScope {
        launch {
            for (event in channel) {
                println("Consumed: ${event.name}")
                delay(1000)
            }
            println("Event consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class UnboundedTaskProducer {
    private val channel = Channel<Task>(capacity = Channel.UNLIMITED)
    
    data class Task(val id: Int, val description: String)
    
    suspend fun produceTasks(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Task(index, "Task$index"))
                println("Produced: Task$index")
                delay(10)
            }
            channel.close()
            println("Task channel closed")
        }
    }
    
    suspend fun consumeTasks() = coroutineScope {
        launch {
            for (task in channel) {
                println("Consumed: ${task.description}")
                delay(1000)
            }
            println("Task consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class UnboundedNotificationProducer {
    private val channel = Channel<Notification>(capacity = Channel.UNLIMITED)
    
    data class Notification(val id: Int, val message: String)
    
    suspend fun produceNotifications(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Notification(index, "Notification$index"))
                println("Produced: Notification$index")
                delay(10)
            }
            channel.close()
            println("Notification channel closed")
        }
    }
    
    suspend fun consumeNotifications() = coroutineScope {
        launch {
            for (notification in channel) {
                println("Consumed: ${notification.message}")
                delay(1000)
            }
            println("Notification consumer finished")
        }
    }
    
    fun getChannel() = channel
}

suspend fun simulateUnboundedMessageProducer(
    producer: UnboundedMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(1000)
        delay(100)
    }
    
    println("Unbounded message producer $producerId completed")
}

suspend fun simulateUnboundedDataProducer(
    producer: UnboundedDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(1000)
        delay(100)
    }
    
    println("Unbounded data producer $producerId completed")
}

suspend fun simulateUnboundedEventProducer(
    producer: UnboundedEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(1000)
        delay(100)
    }
    
    println("Unbounded event producer $producerId completed")
}

suspend fun simulateUnboundedTaskProducer(
    producer: UnboundedTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(1000)
        delay(100)
    }
    
    println("Unbounded task producer $producerId completed")
}

suspend fun simulateUnboundedNotificationProducer(
    producer: UnboundedNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(1000)
        delay(100)
    }
    
    println("Unbounded notification producer $producerId completed")
}

suspend fun monitorUnboundedChannels(
    messageProducer: UnboundedMessageProducer,
    dataProducer: UnboundedDataProducer,
    eventProducer: UnboundedEventProducer,
    taskProducer: UnboundedTaskProducer,
    notificationProducer: UnboundedNotificationProducer,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Message channel: ${messageProducer.getChannel().tryReceive().getOrNull()}")
        println("  Data channel: ${dataProducer.getChannel().tryReceive().getOrNull()}")
        println("  Event channel: ${eventProducer.getChannel().tryReceive().getOrNull()}")
        println("  Task channel: ${taskProducer.getChannel().tryReceive().getOrNull()}")
        println("  Notification channel: ${notificationProducer.getChannel().tryReceive().getOrNull()}")
        
        delay(200)
    }
}

fun main() = runBlocking {
    println("Starting Unbounded Channel Simulation...")
    println()
    
    val messageProducer = UnboundedMessageProducer()
    val dataProducer = UnboundedDataProducer()
    val eventProducer = UnboundedEventProducer()
    val taskProducer = UnboundedTaskProducer()
    val notificationProducer = UnboundedNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateUnboundedMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateUnboundedMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateUnboundedDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateUnboundedEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateUnboundedTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateUnboundedNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorUnboundedChannels(
            messageProducer,
            dataProducer,
            eventProducer,
            taskProducer,
            notificationProducer,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Unbounded Channel Warning:")
    println("  The code uses unbounded channels with fast producers and slow consumers:")
    println("  - UnboundedMessageProducer uses UNLIMITED capacity")
    println("  - UnboundedDataProducer uses UNLIMITED capacity")
    println("  - UnboundedEventProducer uses UNLIMITED capacity")
    println("  - UnboundedTaskProducer uses UNLIMITED capacity")
    println("  - UnboundedNotificationProducer uses UNLIMITED capacity")
    println("  Unbounded channels may cause memory issues if producers are too fast.")
    println("  Fix: Use bounded channels or limit production rate.")
}