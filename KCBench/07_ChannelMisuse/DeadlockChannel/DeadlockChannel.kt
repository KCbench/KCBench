import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class DeadlockMessageProducer {
    private val channel1 = Channel<Int>()
    private val channel2 = Channel<Int>()
    
    suspend fun produceMessages1(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel1.send(index)
                println("Produced to channel1: $index")
                delay(50)
            }
        }
    }
    
    suspend fun produceMessages2(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel2.send(index)
                println("Produced to channel2: $index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeMessages1() = coroutineScope {
        launch {
            for (message in channel1) {
                println("Consumed from channel1: $message")
                delay(100)
            }
            println("Consumer1 finished")
        }
    }
    
    suspend fun consumeMessages2() = coroutineScope {
        launch {
            for (message in channel2) {
                println("Consumed from channel2: $message")
                delay(100)
            }
            println("Consumer2 finished")
        }
    }
    
    fun getChannel1() = channel1
    fun getChannel2() = channel2
}

class DeadlockDataProducer {
    private val channel1 = Channel<String>()
    private val channel2 = Channel<String>()
    
    suspend fun produceData1(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel1.send("Data1_$index")
                println("Produced to channel1: Data1_$index")
                delay(50)
            }
        }
    }
    
    suspend fun produceData2(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel2.send("Data2_$index")
                println("Produced to channel2: Data2_$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeData1() = coroutineScope {
        launch {
            for (data in channel1) {
                println("Consumed from channel1: $data")
                delay(100)
            }
            println("Data consumer1 finished")
        }
    }
    
    suspend fun consumeData2() = coroutineScope {
        launch {
            for (data in channel2) {
                println("Consumed from channel2: $data")
                delay(100)
            }
            println("Data consumer2 finished")
        }
    }
    
    fun getChannel1() = channel1
    fun getChannel2() = channel2
}

class DeadlockEventProducer {
    private val channel1 = Channel<Event>()
    private val channel2 = Channel<Event>()
    
    data class Event(val id: Int, val name: String)
    
    suspend fun produceEvents1(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel1.send(Event(index, "Event1_$index"))
                println("Produced to channel1: Event1_$index")
                delay(50)
            }
        }
    }
    
    suspend fun produceEvents2(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel2.send(Event(index, "Event2_$index"))
                println("Produced to channel2: Event2_$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeEvents1() = coroutineScope {
        launch {
            for (event in channel1) {
                println("Consumed from channel1: ${event.name}")
                delay(100)
            }
            println("Event consumer1 finished")
        }
    }
    
    suspend fun consumeEvents2() = coroutineScope {
        launch {
            for (event in channel2) {
                println("Consumed from channel2: ${event.name}")
                delay(100)
            }
            println("Event consumer2 finished")
        }
    }
    
    fun getChannel1() = channel1
    fun getChannel2() = channel2
}

class DeadlockTaskProducer {
    private val channel1 = Channel<Task>()
    private val channel2 = Channel<Task>()
    
    data class Task(val id: Int, val description: String)
    
    suspend fun produceTasks1(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel1.send(Task(index, "Task1_$index"))
                println("Produced to channel1: Task1_$index")
                delay(50)
            }
        }
    }
    
    suspend fun produceTasks2(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel2.send(Task(index, "Task2_$index"))
                println("Produced to channel2: Task2_$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeTasks1() = coroutineScope {
        launch {
            for (task in channel1) {
                println("Consumed from channel1: ${task.description}")
                delay(100)
            }
            println("Task consumer1 finished")
        }
    }
    
    suspend fun consumeTasks2() = coroutineScope {
        launch {
            for (task in channel2) {
                println("Consumed from channel2: ${task.description}")
                delay(100)
            }
            println("Task consumer2 finished")
        }
    }
    
    fun getChannel1() = channel1
    fun getChannel2() = channel2
}

class DeadlockNotificationProducer {
    private val channel1 = Channel<Notification>()
    private val channel2 = Channel<Notification>()
    
    data class Notification(val id: Int, val message: String)
    
    suspend fun produceNotifications1(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel1.send(Notification(index, "Notification1_$index"))
                println("Produced to channel1: Notification1_$index")
                delay(50)
            }
        }
    }
    
    suspend fun produceNotifications2(count: Int) = coroutineScope {
        launch {
            repeat(count) { index ->
                channel2.send(Notification(index, "Notification2_$index"))
                println("Produced to channel2: Notification2_$index")
                delay(50)
            }
        }
    }
    
    suspend fun consumeNotifications1() = coroutineScope {
        launch {
            for (notification in channel1) {
                println("Consumed from channel1: ${notification.message}")
                delay(100)
            }
            println("Notification consumer1 finished")
        }
    }
    
    suspend fun consumeNotifications2() = coroutineScope {
        launch {
            for (notification in channel2) {
                println("Consumed from channel2: ${notification.message}")
                delay(100)
            }
            println("Notification consumer2 finished")
        }
    }
    
    fun getChannel1() = channel1
    fun getChannel2() = channel2
}

suspend fun simulateDeadlockMessageProducer(
    producer: DeadlockMessageProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceMessages1(5)
        producer.produceMessages2(5)
        delay(100)
    }
    
    println("Deadlock message producer $producerId completed")
}

suspend fun simulateDeadlockDataProducer(
    producer: DeadlockDataProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceData1(5)
        producer.produceData2(5)
        delay(100)
    }
    
    println("Deadlock data producer $producerId completed")
}

suspend fun simulateDeadlockEventProducer(
    producer: DeadlockEventProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceEvents1(5)
        producer.produceEvents2(5)
        delay(100)
    }
    
    println("Deadlock event producer $producerId completed")
}

suspend fun simulateDeadlockTaskProducer(
    producer: DeadlockTaskProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceTasks1(5)
        producer.produceTasks2(5)
        delay(100)
    }
    
    println("Deadlock task producer $producerId completed")
}

suspend fun simulateDeadlockNotificationProducer(
    producer: DeadlockNotificationProducer,
    producerId: Int
) {
    repeat(10) { attempt ->
        producer.produceNotifications1(5)
        producer.produceNotifications2(5)
        delay(100)
    }
    
    println("Deadlock notification producer $producerId completed")
}

suspend fun monitorDeadlockChannels(
    messageProducer: DeadlockMessageProducer,
    dataProducer: DeadlockDataProducer,
    eventProducer: DeadlockEventProducer,
    taskProducer: DeadlockTaskProducer,
    notificationProducer: DeadlockNotificationProducer,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Message channel1: ${messageProducer.getChannel1().tryReceive().getOrNull()}")
        println("  Message channel2: ${messageProducer.getChannel2().tryReceive().getOrNull()}")
        println("  Data channel1: ${dataProducer.getChannel1().tryReceive().getOrNull()}")
        println("  Data channel2: ${dataProducer.getChannel2().tryReceive().getOrNull()}")
        println("  Event channel1: ${eventProducer.getChannel1().tryReceive().getOrNull()}")
        println("  Event channel2: ${eventProducer.getChannel2().tryReceive().getOrNull()}")
        println("  Task channel1: ${taskProducer.getChannel1().tryReceive().getOrNull()}")
        println("  Task channel2: ${taskProducer.getChannel2().tryReceive().getOrNull()}")
        println("  Notification channel1: ${notificationProducer.getChannel1().tryReceive().getOrNull()}")
        println("  Notification channel2: ${notificationProducer.getChannel2().tryReceive().getOrNull()}")
        
        delay(200)
    }
}

fun main() = runBlocking {
    println("Starting Deadlock Channel Simulation...")
    println()
    
    val messageProducer = DeadlockMessageProducer()
    val dataProducer = DeadlockDataProducer()
    val eventProducer = DeadlockEventProducer()
    val taskProducer = DeadlockTaskProducer()
    val notificationProducer = DeadlockNotificationProducer()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateDeadlockMessageProducer(messageProducer, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockMessageProducer(messageProducer, 2)
    })
    
    jobs.add(launch {
        simulateDeadlockDataProducer(dataProducer, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockEventProducer(eventProducer, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockTaskProducer(taskProducer, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockNotificationProducer(notificationProducer, 1)
    })
    
    jobs.add(launch {
        monitorDeadlockChannels(
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
    
    println("\n⚠️  Deadlock Channel Warning:")
    println("  The code has multiple channels that may cause deadlock:")
    println("  - DeadlockMessageProducer has channel1 and channel2")
    println("  - DeadlockDataProducer has channel1 and channel2")
    println("  - DeadlockEventProducer has channel1 and channel2")
    println("  - DeadlockTaskProducer has channel1 and channel2")
    println("  - DeadlockNotificationProducer has channel1 and channel2")
    println("  Multiple channels may cause circular dependencies and deadlock.")
    println("  Fix: Use a single channel or ensure proper channel usage order.")
}