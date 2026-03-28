import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class MultipleConsumerMessageProducer {
    private val channel = Channel<Int>()
    
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
    
    suspend fun consumeMessages(consumerId: Int) = coroutineScope {
        launch {
            for (message in channel) {
                println("Consumer $consumerId consumed: $message")
                delay(100)
            }
            println("Consumer $consumerId finished")
        }
    }
    
    fun getChannel() = channel
}

class MultipleConsumerDataProducer {
    private val channel = Channel<String>()
    
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
    
    suspend fun consumeData(consumerId: Int) = coroutineScope {
        launch {
            for (data in channel) {
                println("Consumer $consumerId consumed: $data")
                delay(100)
            }
            println("Consumer $consumerId finished")
        }
    }
    
    fun getChannel() = channel
}

class MultipleConsumerEventProducer {
    private val channel = Channel<Event>()
    
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
    
    suspend fun consumeEvents(consumerId: Int) = coroutineScope {
        launch {
            for (event in channel) {
                println("Consumer $consumerId consumed: ${event.name}")
                delay(100)
            }
            println("Consumer $consumerId finished")
        }
    }
    
    fun getChannel() = channel
}

class MultipleConsumerTaskProducer {
    private val channel = Channel<Task>()
    
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
    
    suspend fun consumeTasks(consumerId: Int) = coroutineScope {
        launch {
            for (task in channel) {
                println("Consumer $consumerId consumed: ${task.description}")
                delay(100)
            }
            println("Consumer $consumerId finished")
        }
    }
    
    fun getChannel() = channel
}

class MultipleConsumerNotificationProducer {
    private val channel = Channel<Notification>()
    
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
    
    suspend fun consumeNotifications(consumerId: Int) = coroutineScope {
        launch {
            for (notification in channel) {
                println("Consumer $consumerId consumed: ${notification.message}")
                delay(100)
            }
            println("Consumer $consumerId finished")
        }
    }
    
    fun getChannel() = channel
}

suspend fun simulateMultipleConsumerMessageProducer(
    producer: MultipleConsumerMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(10)
        delay(100)
    }
    
    println("Multiple consumer message producer $producerId completed")
}

suspend fun simulateMultipleConsumerDataProducer(
    producer: MultipleConsumerDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(10)
        delay(100)
    }
    
    println("Multiple consumer data producer $producerId completed")
}

suspend fun simulateMultipleConsumerEventProducer(
    producer: MultipleConsumerEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(10)
        delay(100)
    }
    
    println("Multiple consumer event producer $producerId completed")
}

suspend fun simulateMultipleConsumerTaskProducer(
    producer: MultipleConsumerTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(10)
        delay(100)
    }
    
    println("Multiple consumer task producer $producerId completed")
}

suspend fun simulateMultipleConsumerNotificationProducer(
    producer: MultipleConsumerNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(10)
        delay(100)
    }
    
    println("Multiple consumer notification producer $producerId completed")
}

suspend fun monitorMultipleConsumerChannels(
    messageProducer: MultipleConsumerMessageProducer,
    dataProducer: MultipleConsumerDataProducer,
    eventProducer: MultipleConsumerEventProducer,
    taskProducer: MultipleConsumerTaskProducer,
    notificationProducer: MultipleConsumerNotificationProducer,
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
    println("Starting Multiple Consumers Simulation...")
    println()
    
    val messageProducer = MultipleConsumerMessageProducer()
    val dataProducer = MultipleConsumerDataProducer()
    val eventProducer = MultipleConsumerEventProducer()
    val taskProducer = MultipleConsumerTaskProducer()
    val notificationProducer = MultipleConsumerNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMultipleConsumerMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        messageProducer.consumeMessages(1)
    })
    
    jobs.add(launch {
        messageProducer.consumeMessages(2)
    })
    
    jobs.add(launch {
        simulateMultipleConsumerDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateMultipleConsumerEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateMultipleConsumerTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateMultipleConsumerNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorMultipleConsumerChannels(
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
    
    println("\n⚠️  Multiple Consumers Warning:")
    println("  The code has multiple consumers consuming from the same channel:")
    println("  - MultipleConsumerMessageProducer has multiple consumers")
    println("  - MultipleConsumerDataProducer has multiple consumers")
    println("  - MultipleConsumerEventProducer has multiple consumers")
    println("  - MultipleConsumerTaskProducer has multiple consumers")
    println("  - MultipleConsumerNotificationProducer has multiple consumers")
    println("  Multiple consumers may cause race conditions and message duplication.")
    println("  Fix: Use BroadcastChannel or ensure proper message distribution.")
}