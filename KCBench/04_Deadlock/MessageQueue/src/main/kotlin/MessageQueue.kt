import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class MessageQueue(
    val queueId: String,
    val queueName: String,
    var locked: Boolean = false,
    var processing: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class Message(
    val messageId: String,
    val content: String,
    var queued: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

class MessageQueueManager {
    private val queues = mutableMapOf<String, MessageQueue>()
    private val messages = mutableMapOf<String, Message>()
    private val queuePoolMutex = Mutex()
    private val messagePoolMutex = Mutex()
    
    init {
        initializeQueues()
        initializeMessages()
    }
    
    private fun initializeQueues() {
        val queueConfigs = listOf(
            Pair("QUEUE001", "OrderQueue"),
            Pair("QUEUE002", "NotificationQueue"),
            Pair("QUEUE003", "EmailQueue"),
            Pair("QUEUE004", "LogQueue"),
            Pair("QUEUE005", "EventQueue"),
            Pair("QUEUE006", "TaskQueue"),
            Pair("QUEUE007", "AlertQueue"),
            Pair("QUEUE008", "ReportQueue"),
            Pair("QUEUE009", "BackupQueue"),
            Pair("QUEUE010", "SyncQueue")
        )
        
        queueConfigs.forEach { (queueId, queueName) ->
            queues[queueId] = MessageQueue(
                queueId = queueId,
                queueName = queueName,
                locked = false,
                processing = false
            )
        }
    }
    
    private fun initializeMessages() {
        val messageConfigs = listOf(
            Pair("MSG001", "Order #1234"),
            Pair("MSG002", "Notification #5678"),
            Pair("MSG003", "Email #9012"),
            Pair("MSG004", "Log entry #3456"),
            Pair("MSG005", "Event #7890"),
            Pair("MSG006", "Task #1357"),
            Pair("MSG007", "Alert #2468"),
            Pair("MSG008", "Report #3690"),
            Pair("MSG009", "Backup #4812"),
            Pair("MSG010", "Sync #5934")
        )
        
        messageConfigs.forEach { (messageId, content) ->
            messages[messageId] = Message(
                messageId = messageId,
                content = content,
                queued = false,
                locked = false
            )
        }
    }
    
    suspend fun lockQueue(queueId: String): Boolean {
        val queue = queues[queueId] ?: return false
        
        if (queue.locked) {
            return false
        }
        
        queuePoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (queue.locked) {
                return false
            }
            
            queue.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                queue.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun unlockQueue(queueId: String): Boolean {
        val queue = queues[queueId] ?: return false
        
        if (!queue.locked) {
            return false
        }
        
        queue.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            queuePoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                queue.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun enqueueMessage(messageId: String): Boolean {
        val message = messages[messageId] ?: return false
        
        if (message.queued) {
            return false
        }
        
        messagePoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (message.queued) {
                return false
            }
            
            message.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                message.queued = true
                message.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun dequeueMessage(messageId: String): Boolean {
        val message = messages[messageId] ?: return false
        
        if (!message.queued) {
            return false
        }
        
        message.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            messagePoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                message.queued = false
                message.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun processMessageWithQueue(
        messageId: String,
        queueId: String
    ): Boolean {
        val message = messages[messageId] ?: return false
        val queue = queues[queueId] ?: return false
        
        if (!message.queued || !queue.locked) {
            return false
        }
        
        message.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            queue.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                queue.processing = true
                delay(Random.nextLong(20, 50))
                queue.processing = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferQueue(
        fromQueueId: String,
        toQueueId: String
    ): Boolean {
        val fromQueue = queues[fromQueueId]
        val toQueue = queues[toQueueId]
        
        if (fromQueue == null || toQueue == null) {
            return false
        }
        
        if (!fromQueue.locked || toQueue.locked) {
            return false
        }
        
        fromQueue.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toQueue.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromQueue.locked = false
                toQueue.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapQueues(
        queueId1: String,
        queueId2: String
    ): Boolean {
        val queue1 = queues[queueId1]
        val queue2 = queues[queueId2]
        
        if (queue1 == null || queue2 == null) {
            return false
        }
        
        if (!queue1.locked || !queue2.locked) {
            return false
        }
        
        queue1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            queue2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempLocked = queue1.locked
                val tempProcessing = queue1.processing
                
                queue1.locked = queue2.locked
                queue1.processing = queue2.processing
                queue2.locked = tempLocked
                queue2.processing = tempProcessing
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getQueueStatus(queueId: String): MessageQueue? {
        val queue = queues[queueId] ?: return null
        
        return queue.mutex.withLock {
            delay(Random.nextLong(5, 15))
            queue.copy()
        }
    }
    
    suspend fun getMessageStatus(messageId: String): Message? {
        val message = messages[messageId] ?: return null
        
        return message.mutex.withLock {
            delay(Random.nextLong(5, 15))
            message.copy()
        }
    }
    
    fun getAllQueues() = queues.values.toList()
    fun getAllMessages() = messages.values.toList()
}

suspend fun simulateQueueLocking(
    queueManager: MessageQueueManager,
    managerId: Int
) {
    val queues = queueManager.getAllQueues()
    
    repeat(10) { attempt ->
        val queue = queues.filter { !it.locked }.randomOrNull()
        
        if (queue != null) {
            val success = queueManager.lockQueue(queue.queueId)
            if (success) {
                println("Manager $managerId: Locked ${queue.queueName}")
            } else {
                println("Manager $managerId: Failed to lock ${queue.queueName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateQueueUnlocking(
    queueManager: MessageQueueManager,
    managerId: Int
) {
    val queues = queueManager.getAllQueues()
    
    repeat(10) { attempt ->
        val queue = queues.filter { it.locked }.randomOrNull()
        
        if (queue != null) {
            val success = queueManager.unlockQueue(queue.queueId)
            if (success) {
                println("Manager $managerId: Unlocked ${queue.queueName}")
            } else {
                println("Manager $managerId: Failed to unlock ${queue.queueName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateMessageEnqueueing(
    queueManager: MessageQueueManager,
    managerId: Int
) {
    val messages = queueManager.getAllMessages()
    
    repeat(8) { attempt ->
        val message = messages.filter { !it.queued }.randomOrNull()
        
        if (message != null) {
            val success = queueManager.enqueueMessage(message.messageId)
            if (success) {
                println("Manager $managerId: Enqueued ${message.content}")
            } else {
                println("Manager $managerId: Failed to enqueue ${message.content}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateMessageDequeuing(
    queueManager: MessageQueueManager,
    managerId: Int
) {
    val messages = queueManager.getAllMessages()
    
    repeat(8) { attempt ->
        val message = messages.filter { it.queued }.randomOrNull()
        
        if (message != null) {
            val success = queueManager.dequeueMessage(message.messageId)
            if (success) {
                println("Manager $managerId: Dequeued ${message.content}")
            } else {
                println("Manager $managerId: Failed to dequeue ${message.content}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateMessageProcessing(
    queueManager: MessageQueueManager,
    processorId: Int
) {
    val messages = queueManager.getAllMessages()
    val queues = queueManager.getAllQueues()
    
    repeat(10) { attempt ->
        val message = messages.filter { it.queued }.randomOrNull()
        val queue = queues.filter { it.locked }.randomOrNull()
        
        if (message != null && queue != null) {
            val success = queueManager.processMessageWithQueue(
                message.messageId,
                queue.queueId
            )
            
            if (success) {
                println("Processor $processorId: Processed ${message.content} with ${queue.queueName}")
            } else {
                println("Processor $processorId: Failed to process message")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateQueueTransfer(
    queueManager: MessageQueueManager,
    transferId: Int
) {
    val queues = queueManager.getAllQueues()
    
    repeat(6) { attempt ->
        val lockedQueues = queues.filter { it.locked }
        val unlockedQueues = queues.filter { !it.locked }
        
        if (lockedQueues.isNotEmpty() && unlockedQueues.isNotEmpty()) {
            val fromQueue = lockedQueues.random()
            val toQueue = unlockedQueues.random()
            
            val success = queueManager.transferQueue(fromQueue.queueId, toQueue.queueId)
            
            if (success) {
                println("Transfer $transferId: ${fromQueue.queueName} -> ${toQueue.queueName}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateQueueSwap(
    queueManager: MessageQueueManager,
    swapId: Int
) {
    val queues = queueManager.getAllQueues()
    
    repeat(5) { attempt ->
        val lockedQueues = queues.filter { it.locked }
        
        if (lockedQueues.size >= 2) {
            val queue1 = lockedQueues.random()
            val queue2 = lockedQueues.filter { it.queueId != queue1.queueId }.random()
            
            val success = queueManager.swapQueues(queue1.queueId, queue2.queueId)
            
            if (success) {
                println("Swap $swapId: ${queue1.queueName} <-> ${queue2.queueName}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorMessageQueue(
    queueManager: MessageQueueManager,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val queues = queueManager.getAllQueues()
        val messages = queueManager.getAllMessages()
        
        val locked = queues.count { it.locked }
        val processing = queues.count { it.processing }
        val queued = messages.count { it.queued }
        val lockedMessages = messages.count { it.locked }
        
        println("Monitor $monitorId: Locked=$locked, Processing=$processing, " +
                "Queued=$queued, LockedMsgs=$lockedMessages")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val queueManager = MessageQueueManager()
    
    println("Starting Message Queue Simulation...")
    println("Initial Queue Status:")
    queueManager.getAllQueues().forEach { queue ->
        println("  ${queue.queueId} (${queue.queueName}): " +
                "Locked=${queue.locked}, Processing=${queue.processing}")
    }
    println()
    
    println("Initial Message Status:")
    queueManager.getAllMessages().forEach { message ->
        println("  ${message.messageId} (${message.content}): " +
                "Queued=${message.queued}, Locked=${message.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateQueueLocking(queueManager, 1)
    })
    
    jobs.add(launch {
        simulateQueueLocking(queueManager, 2)
    })
    
    jobs.add(launch {
        simulateQueueUnlocking(queueManager, 1)
    })
    
    jobs.add(launch {
        simulateQueueUnlocking(queueManager, 2)
    })
    
    jobs.add(launch {
        simulateMessageEnqueueing(queueManager, 1)
    })
    
    jobs.add(launch {
        simulateMessageDequeuing(queueManager, 1)
    })
    
    jobs.add(launch {
        simulateMessageProcessing(queueManager, 1)
    })
    
    jobs.add(launch {
        simulateQueueTransfer(queueManager, 1)
    })
    
    jobs.add(launch {
        simulateQueueSwap(queueManager, 1)
    })
    
    jobs.add(launch {
        monitorMessageQueue(queueManager, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val queues = queueManager.getAllQueues()
    val messages = queueManager.getAllMessages()
    
    println("\n=== Final Queue Status ===")
    queues.forEach { queue ->
        println("  ${queue.queueId} (${queue.queueName}): " +
                "Locked=${queue.locked}, Processing=${queue.processing}")
    }
    
    println("\n=== Final Message Status ===")
    messages.forEach { message ->
        println("  ${message.messageId} (${message.content}): " +
                "Queued=${message.queued}, Locked=${message.locked}")
    }
    
    val locked = queues.count { it.locked }
    val processing = queues.count { it.processing }
    val queued = messages.count { it.queued }
    val lockedMessages = messages.count { it.locked }
    
    println("\nLocked Queues: $locked/${queues.size}")
    println("Processing Queues: $processing/${queues.size}")
    println("Queued Messages: $queued/${messages.size}")
    println("Locked Messages: $lockedMessages/${messages.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - lockQueue(): queuePoolMutex -> queue.mutex")
    println("  - unlockQueue(): queue.mutex -> queuePoolMutex")
    println("  - enqueueMessage(): messagePoolMutex -> message.mutex")
    println("  - dequeueMessage(): message.mutex -> messagePoolMutex")
    println("  - processMessageWithQueue(): message.mutex -> queue.mutex")
    println("  - transferQueue(): queue1.mutex -> queue2.mutex")
    println("  - swapQueues(): queue1.mutex -> queue2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}