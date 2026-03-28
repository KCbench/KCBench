import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class MultipleMessageProducer {
    private val channel = Channel<Int>()
    
    suspend fun produceMessages(producerId: Int, count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(producerId * 100 + index)
                println("Producer $producerId produced: ${producerId * 100 + index}")
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

class MultipleDataProducer {
    private val channel = Channel<String>()
    
    suspend fun produceData(producerId: Int, count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send("Producer${producerId}_Data$index")
                println("Producer $producerId produced: Producer${producerId}_Data$index")
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

class MultipleEventProducer {
    private val channel = Channel<Event>()
    
    data class Event(val producerId: Int, val id: Int, val name: String)
    
    suspend fun produceEvents(producerId: Int, count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Event(producerId, index, "Producer${producerId}_Event$index"))
                println("Producer $producerId produced: Producer${producerId}_Event$index")
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

class MultipleTaskProducer {
    private val channel = Channel<Task>()
    
    data class Task(val producerId: Int, val id: Int, val description: String)
    
    suspend fun produceTasks(producerId: Int, count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Task(producerId, index, "Producer${producerId}_Task$index"))
                println("Producer $producerId produced: Producer${producerId}_Task$index")
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

class MultipleNotificationProducer {
    private val channel = Channel<Notification>()
    
    data class Notification(val producerId: Int, val id: Int, val message: String)
    
    suspend fun produceNotifications(producerId: Int, count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel.send(Notification(producerId, index, "Producer${producerId}_Notification$index"))
                println("Producer $producerId produced: Producer${producerId}_Notification$index")
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

suspend fun simulateMultipleMessageProducer(
    producer: MultipleMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(producerId, 5)
        delay(100)
    }
    
    println("Message producer $producerId completed")
}

suspend fun simulateMultipleDataProducer(
    producer: MultipleDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(producerId, 5)
        delay(100)
    }
    
    println("Data producer $producerId completed")
}

suspend fun simulateMultipleEventProducer(
    producer: MultipleEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(producerId, 5)
        delay(100)
    }
    
    println("Event producer $producerId completed")
}

suspend fun simulateMultipleTaskProducer(
    producer: MultipleTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(producerId, 5)
        delay(100)
    }
    
    println("Task producer $producerId completed")
}

suspend fun simulateMultipleNotificationProducer(
    producer: MultipleNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(producerId, 5)
        delay(100)
    }
    
    println("Notification producer $producerId completed")
}

suspend fun monitorMultipleChannels(
    messageProducer: MultipleMessageProducer,
    dataProducer: MultipleDataProducer,
    eventProducer: MultipleEventProducer,
    taskProducer: MultipleTaskProducer,
    notificationProducer: MultipleNotificationProducer,
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
    println("Starting Multiple Producers Simulation...")
    println()
    
    val messageProducer = MultipleMessageProducer()
    val dataProducer = MultipleDataProducer()
    val eventProducer = MultipleEventProducer()
    val taskProducer = MultipleTaskProducer()
    val notificationProducer = MultipleNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMultipleMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateMultipleMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateMultipleDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateMultipleEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateMultipleTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateMultipleNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorMultipleChannels(
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
    
    println("\n⚠️  Multiple Producers Warning:")
    println("  The code has multiple producers sending to the same channel:")
    println("  - MultipleMessageProducer has multiple producers")
    println("  - MultipleDataProducer has multiple producers")
    println("  - MultipleEventProducer has multiple producers")
    println("  - MultipleTaskProducer has multiple producers")
    println("  - MultipleNotificationProducer has multiple producers")
    println("  Multiple producers may cause race conditions and message loss.")
    println("  Fix: Use mutex to synchronize producers or use separate channels.")
}