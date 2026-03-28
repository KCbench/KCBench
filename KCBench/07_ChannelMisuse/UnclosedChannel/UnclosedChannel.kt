import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class MessageProducer {
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
                delay(50)
            }
            println("Consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class DataProducer {
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
                delay(50)
            }
            println("Data consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class EventProducer {
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
                delay(50)
            }
            println("Event consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class TaskProducer {
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
                delay(50)
            }
            println("Task consumer finished")
        }
    }
    
    fun getChannel() = channel
}

class NotificationProducer {
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
                delay(50)
            }
            println("Notification consumer finished")
        }
    }
    
    fun getChannel() = channel
}

suspend fun simulateMessageProducer(
    producer: MessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(5)
        delay(100)
    }
    
    println("Message producer $producerId completed")
}

suspend fun simulateDataProducer(
    producer: DataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(5)
        delay(100)
    }
    
    println("Data producer $producerId completed")
}

suspend fun simulateEventProducer(
    producer: EventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(5)
        delay(100)
    }
    
    println("Event producer $producerId completed")
}

suspend fun simulateTaskProducer(
    producer: TaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(5)
        delay(100)
    }
    
    println("Task producer $producerId completed")
}

suspend fun simulateNotificationProducer(
    producer: NotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(5)
        delay(100)
    }
    
    println("Notification producer $producerId completed")
}

suspend fun monitorChannels(
    messageProducer: MessageProducer,
    dataProducer: DataProducer,
    eventProducer: EventProducer,
    taskProducer: TaskProducer,
    notificationProducer: NotificationProducer,
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
    println("Starting Unclosed Channel Simulation...")
    println()
    
    val messageProducer = MessageProducer()
    val dataProducer = DataProducer()
    val eventProducer = EventProducer()
    val taskProducer = TaskProducer()
    val notificationProducer = NotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorChannels(
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
    
    println("\n⚠️  Unclosed Channel Warning:")
    println("  The code creates channels but never closes them:")
    println("  - MessageProducer.channel is never closed")
    println("  - DataProducer.channel is never closed")
    println("  - EventProducer.channel is never closed")
    println("  - TaskProducer.channel is never closed")
    println("  - NotificationProducer.channel is never closed")
    println("  Consumers will wait indefinitely for messages that will never arrive.")
    println("  Fix: Call channel.close() after all messages are sent.")
}