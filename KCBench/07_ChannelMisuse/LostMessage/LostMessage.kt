import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class LostMessageProducer {
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
    
    suspend fun consumeMessages() = coroutineScope {
        launch {
            var count = 0
            for (message in channel) {
                if (count >= 3) {
                    println("Stopping after consuming 3 messages")
                    break
                }
                println("Consumed: $message")
                count++
                delay(50)
            }
            println("Consumer finished early")
        }
    }
    
    fun getChannel() = channel
}

class LostDataProducer {
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
    
    suspend fun consumeData() = coroutineScope {
        launch {
            var count = 0
            for (data in channel) {
                if (count >= 3) {
                    println("Stopping after consuming 3 data items")
                    break
                }
                println("Consumed: $data")
                count++
                delay(50)
            }
            println("Data consumer finished early")
        }
    }
    
    fun getChannel() = channel
}

class LostEventProducer {
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
    
    suspend fun consumeEvents() = coroutineScope {
        launch {
            var count = 0
            for (event in channel) {
                if (count >= 3) {
                    println("Stopping after consuming 3 events")
                    break
                }
                println("Consumed: ${event.name}")
                count++
                delay(50)
            }
            println("Event consumer finished early")
        }
    }
    
    fun getChannel() = channel
}

class LostTaskProducer {
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
    
    suspend fun consumeTasks() = coroutineScope {
        launch {
            var count = 0
            for (task in channel) {
                if (count >= 3) {
                    println("Stopping after consuming 3 tasks")
                    break
                }
                println("Consumed: ${task.description}")
                count++
                delay(50)
            }
            println("Task consumer finished early")
        }
    }
    
    fun getChannel() = channel
}

class LostNotificationProducer {
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
    
    suspend fun consumeNotifications() = coroutineScope {
        launch {
            var count = 0
            for (notification in channel) {
                if (count >= 3) {
                    println("Stopping after consuming 3 notifications")
                    break
                }
                println("Consumed: ${notification.message}")
                count++
                delay(50)
            }
            println("Notification consumer finished early")
        }
    }
    
    fun getChannel() = channel
}

suspend fun simulateLostMessageProducer(
    producer: LostMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages(10)
        delay(100)
    }
    
    println("Lost message producer $producerId completed")
}

suspend fun simulateLostDataProducer(
    producer: LostDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData(10)
        delay(100)
    }
    
    println("Lost data producer $producerId completed")
}

suspend fun simulateLostEventProducer(
    producer: LostEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents(10)
        delay(100)
    }
    
    println("Lost event producer $producerId completed")
}

suspend fun simulateLostTaskProducer(
    producer: LostTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks(10)
        delay(100)
    }
    
    println("Lost task producer $producerId completed")
}

suspend fun simulateLostNotificationProducer(
    producer: LostNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications(10)
        delay(100)
    }
    
    println("Lost notification producer $producerId completed")
}

suspend fun monitorLostChannels(
    messageProducer: LostMessageProducer,
    dataProducer: LostDataProducer,
    eventProducer: LostEventProducer,
    taskProducer: LostTaskProducer,
    notificationProducer: LostNotificationProducer,
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
    println("Starting Lost Message Simulation...")
    println()
    
    val messageProducer = LostMessageProducer()
    val dataProducer = LostDataProducer()
    val eventProducer = LostEventProducer()
    val taskProducer = LostTaskProducer()
    val notificationProducer = LostNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateLostMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateLostMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateLostDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateLostEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateLostTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateLostNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorLostChannels(
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
    
    println("\n⚠️  Lost Message Warning:")
    println("  The code consumers stop early, leaving messages in the channel:")
    println("  - LostMessageProducer consumer stops after 3 messages")
    println("  - LostDataProducer consumer stops after 3 data items")
    println("  - LostEventProducer consumer stops after 3 events")
    println("  - LostTaskProducer consumer stops after 3 tasks")
    println("  - LostNotificationProducer consumer stops after 3 notifications")
    println("  Messages are lost because consumers don't consume all messages.")
    println("  Fix: Ensure consumers consume all messages or use tryReceive().")
}