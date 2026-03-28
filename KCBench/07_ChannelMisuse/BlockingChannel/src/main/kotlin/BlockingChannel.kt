import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class BlockingMessageProducer {
    private val channel = Channel<Int>()
    
    suspend fun produceMessages(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(index)
                println("Produced: $index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeMessages() = coroutineScope {
        launch {
            for (message in channel) {
                println("Consumed: $message")
                delay(500)
            }
            println("Consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BlockingDataProducer {
    private val channel = Channel<String>()
    
    suspend fun produceData(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send("Data$index")
                println("Produced: Data$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeData() = coroutineScope {
        launch {
            for (data in channel) {
                println("Consumed: $data")
                delay(500)
            }
            println("Data consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BlockingEventProducer {
    private val channel = Channel<Event>()
    
    data class Event(val id: Int, val name: String)
    
    suspend fun produceEvents(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Event(index, "Event$index"))
                println("Produced: Event$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeEvents() = coroutineScope {
        launch {
            for (event in channel) {
                println("Consumed: ${event.name}")
                delay(500)
            }
            println("Event consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BlockingTaskProducer {
    private val channel = Channel<Task>()
    
    data class Task(val id: Int, val description: String)
    
    suspend fun produceTasks(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Task(index, "Task$index"))
                println("Produced: Task$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeTasks() = coroutineScope {
        launch {
            for (task in channel) {
                println("Consumed: ${task.description}")
                delay(500)
            }
            println("Task consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class BlockingNotificationProducer {
    private val channel = Channel<Notification>()
    
    data class Notification(val id: Int, val message: String)
    
    suspend fun produceNotifications(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Notification(index, "Notification$index"))
                println("Produced: Notification$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeNotifications() = coroutineScope {
        launch {
            for (notification in channel) {
                println("Consumed: ${notification.message}")
                delay(500)
            }
            println("Notification consumer finished")
        }
    }
    
    fun getChannel() = channel
}

suspend fun simulateBlockingMessageProducer(
    producer: BlockingMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(10)
        delay(100)
    }
    
    println("Blocking message producer $producerId completed")
}

suspend fun simulateBlockingDataProducer(
    producer: BlockingDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(10)
        delay(100)
    }
    
    println("Blocking data producer $producerId completed")
}

suspend fun simulateBlockingEventProducer(
    producer: BlockingEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(10)
        delay(100)
    }
    
    println("Blocking event producer $producerId completed")
}

suspend fun simulateBlockingTaskProducer(
    producer: BlockingTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(10)
        delay(100)
    }
    
    println("Blocking task producer $producerId completed")
}

suspend fun simulateBlockingNotificationProducer(
    producer: BlockingNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(10)
        delay(100)
    }
    
    println("Blocking notification producer $producerId completed")
}

suspend fun monitorBlockingChannels(
    messageProducer: BlockingMessageProducer,
    dataProducer: BlockingDataProducer,
    eventProducer: BlockingEventProducer,
    taskProducer: BlockingTaskProducer,
    notificationProducer: BlockingNotificationProducer,
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
    println("Starting Blocking Channel Simulation...")
    println()
    
    val messageProducer = BlockingMessageProducer()
    val dataProducer = BlockingDataProducer()
    val eventProducer = BlockingEventProducer()
    val taskProducer = BlockingTaskProducer()
    val notificationProducer = BlockingNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateBlockingMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateBlockingMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateBlockingDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateBlockingEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateBlockingTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateBlockingNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorBlockingChannels(
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
    
    println("\n⚠️  Blocking Channel Warning:")
    println("  The code uses slow consumers with fast producers:")
    println("  - BlockingMessageProducer has slow consumer (500ms delay)")
    println("  - BlockingDataProducer has slow consumer (500ms delay)")
    println("  - BlockingEventProducer has slow consumer (500ms delay)")
    println("  - BlockingTaskProducer has slow consumer (500ms delay)")
    println("  - BlockingNotificationProducer has slow consumer (500ms delay)")
    println("  Producers will block when channel buffer is full, causing performance issues.")
    println("  Fix: Increase buffer capacity or use buffered channels.")
}