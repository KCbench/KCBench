import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class BufferedMessageProducer {
    private val channel = Channel<Int>(capacity = 2)
    
    suspend fun produceMessages(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(index)
                println("Produced: $index")
                delay(50)
            }
            channel.close()
            println("Channel closed")
        }
    }
    
    suspend fun consumeMessages() = coroutineScope {
        launch {
            for (message in channel) {
                println("Consumed: $message")
                delay(200)
            }
            println("Consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BufferedDataProducer {
    private val channel = Channel<String>(capacity = 2)
    
    suspend fun produceData(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send("Data$index")
                println("Produced: Data$index")
                delay(50)
            }
            channel.close()
            println("Data channel closed")
        }
    }
    
    suspend fun consumeData() = coroutineScope {
        launch {
            for (data in channel) {
                println("Consumed: $data")
                delay(200)
            }
            println("Data consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BufferedEventProducer {
    private val channel = Channel<Event>(capacity = 2)
    
    data class Event(val id: Int, val name: String)
    
    suspend fun produceEvents(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Event(index, "Event$index"))
                println("Produced: Event$index")
                delay(50)
            }
            channel.close()
            println("Event channel closed")
        }
    }
    
    suspend fun consumeEvents() = coroutineScope {
        launch {
            for (event in channel) {
                println("Consumed: ${event.name}")
                delay(200)
            }
            println("Event consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BufferedTaskProducer {
    private val channel = Channel<Task>(capacity = 2)
    
    data class Task(val id: Int, val description: String)
    
    suspend fun produceTasks(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Task(index, "Task$index"))
                println("Produced: Task$index")
                delay(50)
            }
            channel.close()
            println("Task channel closed")
        }
    }
    
    suspend fun consumeTasks() = coroutineScope {
        launch {
            for (task in channel) {
                println("Consumed: ${task.description}")
                delay(200)
            }
            println("Task consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BufferedNotificationProducer {
    private val channel = Channel<Notification>(capacity = 2)
    
    data class Notification(val id: Int, val message: String)
    
    suspend fun produceNotifications(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Notification(index, "Notification$index"))
                println("Produced: Notification$index")
                delay(50)
            }
            channel.close()
            println("Notification channel closed")
        }
    }
    
    suspend fun consumeNotifications() = coroutineScope {
        launch {
            for (notification in channel) {
                println("Consumed: ${notification.message}")
                delay(200)
            }
            println("Notification consumer finished")
        }
    }
    
    fun getChannel() = channel
}

suspend fun simulateBufferedMessageProducer(
    producer: BufferedMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(10)
        delay(100)
    }
    
    println("Buffered message producer $producerId completed")
}

suspend fun simulateBufferedDataProducer(
    producer: BufferedDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(10)
        delay(100)
    }
    
    println("Buffered data producer $producerId completed")
}

suspend fun simulateBufferedEventProducer(
    producer: BufferedEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(10)
        delay(100)
    }
    
    println("Buffered event producer $producerId completed")
}

suspend fun simulateBufferedTaskProducer(
    producer: BufferedTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(10)
        delay(100)
    }
    
    println("Buffered task producer $producerId completed")
}

suspend fun simulateBufferedNotificationProducer(
    producer: BufferedNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(10)
        delay(100)
    }
    
    println("Buffered notification producer $producerId completed")
}

suspend fun monitorBufferedChannels(
    messageProducer: BufferedMessageProducer,
    dataProducer: BufferedDataProducer,
    eventProducer: BufferedEventProducer,
    taskProducer: BufferedTaskProducer,
    notificationProducer: BufferedNotificationProducer,
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
    println("Starting Buffer Overflow Simulation...")
    println()
    
    val messageProducer = BufferedMessageProducer()
    val dataProducer = BufferedDataProducer()
    val eventProducer = BufferedEventProducer()
    val taskProducer = BufferedTaskProducer()
    val notificationProducer = BufferedNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateBufferedMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateBufferedMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateBufferedDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateBufferedEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateBufferedTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateBufferedNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorBufferedChannels(
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
    
    println("\n⚠️  Buffer Overflow Warning:")
    println("  The code uses small buffer capacity (2) with fast producers and slow consumers:")
    println("  - BufferedMessageProducer has capacity 2")
    println("  - BufferedDataProducer has capacity 2")
    println("  - BufferedEventProducer has capacity 2")
    println("  - BufferedTaskProducer has capacity 2")
    println("  - BufferedNotificationProducer has capacity 2")
    println("  Producers will block when buffer is full, causing performance issues.")
    println("  Fix: Increase buffer capacity or use unlimited buffer (Channel.UNLIMITED).")
}