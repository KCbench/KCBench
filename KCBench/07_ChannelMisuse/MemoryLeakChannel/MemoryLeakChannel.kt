import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class MemoryLeakMessageProducer {
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

class MemoryLeakDataProducer {
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

class MemoryLeakEventProducer {
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

class MemoryLeakTaskProducer {
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

class MemoryLeakNotificationProducer {
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

suspend fun simulateMemoryLeakMessageProducer(
    producer: MemoryLeakMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(10)
        delay(100)
    }
    
    println("Memory leak message producer $producerId completed")
}

suspend fun simulateMemoryLeakDataProducer(
    producer: MemoryLeakDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(10)
        delay(100)
    }
    
    println("Memory leak data producer $producerId completed")
}

suspend fun simulateMemoryLeakEventProducer(
    producer: MemoryLeakEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(10)
        delay(100)
    }
    
    println("Memory leak event producer $producerId completed")
}

suspend fun simulateMemoryLeakTaskProducer(
    producer: MemoryLeakTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(10)
        delay(100)
    }
    
    println("Memory leak task producer $producerId completed")
}

suspend fun simulateMemoryLeakNotificationProducer(
    producer: MemoryLeakNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(10)
        delay(100)
    }
    
    println("Memory leak notification producer $producerId completed")
}

suspend fun monitorMemoryLeakChannels(
    messageProducer: MemoryLeakMessageProducer,
    dataProducer: MemoryLeakDataProducer,
    eventProducer: MemoryLeakEventProducer,
    taskProducer: MemoryLeakTaskProducer,
    notificationProducer: MemoryLeakNotificationProducer,
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
    println("Starting Memory Leak Channel Simulation...")
    println()
    
    val messageProducer = MemoryLeakMessageProducer()
    val dataProducer = MemoryLeakDataProducer()
    val eventProducer = MemoryLeakEventProducer()
    val taskProducer = MemoryLeakTaskProducer()
    val notificationProducer = MemoryLeakNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMemoryLeakMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateMemoryLeakMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateMemoryLeakDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateMemoryLeakEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateMemoryLeakTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateMemoryLeakNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorMemoryLeakChannels(
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
    
    println("\n⚠️  Memory Leak Channel Warning:")
    println("  The code creates channels but never closes them or cancels consumers:")
    println("  - MemoryLeakMessageProducer.channel is never closed")
    println("  - MemoryLeakDataProducer.channel is never closed")
    println("  - MemoryLeakEventProducer.channel is never closed")
    println("  - MemoryLeakTaskProducer.channel is never closed")
    println("  - MemoryLeakNotificationProducer.channel is never closed")
    println("  Channels and their buffers will remain in memory, causing memory leaks.")
    println("  Fix: Call channel.close() and cancel consumer jobs when done.")
}