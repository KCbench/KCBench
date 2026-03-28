import kotlinx.coroutines.*
import kotlin.random.Random

data class Message(
    val id: String,
    val sender: String,
    val content: String,
    var status: MessageStatus,
    var timestamp: Long,
    var readBy: MutableSet<String> = mutableSetOf()
)

enum class MessageStatus {
    SENT, DELIVERED, READ, FAILED
}

class ChatRoom {
    private val messages = mutableListOf<Message>()
    private val members = mutableSetOf<String>()
    private var messageCount = 0
    private var readCount = 0
    
    suspend fun addMember(memberId: String) {
        members.add(memberId)
        delay(Random.nextLong(1, 5))
    }
    
    suspend fun removeMember(memberId: String) {
        members.remove(memberId)
        delay(Random.nextLong(1, 5))
    }
    
    suspend fun sendMessage(
        sender: String,
        content: String
    ): Message {
        val messageId = "msg_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        val currentTime = System.currentTimeMillis()
        
        val message = Message(
            id = messageId,
            sender = sender,
            content = content,
            status = MessageStatus.SENT,
            timestamp = currentTime
        )
        
        messages.add(message)
        delay(Random.nextLong(1, 10))
        
        val currentCount = messageCount
        delay(Random.nextLong(1, 5))
        messageCount = currentCount + 1
        
        return message
    }
    
    suspend fun markAsDelivered(messageId: String): Boolean {
        val message = messages.find { it.id == messageId }
        
        if (message != null) {
            val currentStatus = message.status
            delay(Random.nextLong(1, 10))
            
            if (currentStatus == MessageStatus.SENT) {
                message.status = MessageStatus.DELIVERED
                delay(Random.nextLong(1, 5))
                return true
            }
        }
        
        return false
    }
    
    suspend fun markAsRead(
        messageId: String,
        reader: String
    ): Boolean {
        val message = messages.find { it.id == messageId }
        
        if (message != null) {
            val currentStatus = message.status
            delay(Random.nextLong(1, 10))
            
            if (currentStatus != MessageStatus.FAILED) {
                message.status = MessageStatus.READ
                delay(Random.nextLong(1, 5))
                
                val currentReadBy = message.readBy
                delay(Random.nextLong(1, 5))
                currentReadBy.add(reader)
                delay(Random.nextLong(1, 5))
                
                val currentReadCount = readCount
                delay(Random.nextLong(1, 5))
                readCount = currentReadCount + 1
                
                return true
            }
        }
        
        return false
    }
    
    suspend fun getRecentMessages(limit: Int): List<Message> {
        val allMessages = messages.toList()
        delay(Random.nextLong(1, 5))
        
        return allMessages.takeLast(limit)
    }
    
    suspend fun getUnreadMessages(userId: String): List<Message> {
        val allMessages = messages.toList()
        delay(Random.nextLong(1, 5))
        
        return allMessages.filter { message ->
            message.status != MessageStatus.FAILED &&
            !message.readBy.contains(userId)
        }
    }
    
    suspend fun getStatistics(): Triple<Int, Int, Int> {
        val currentMessageCount = messageCount
        delay(Random.nextLong(1, 5))
        
        val currentReadCount = readCount
        delay(Random.nextLong(1, 5))
        
        val currentMemberCount = members.size
        delay(Random.nextLong(1, 5))
        
        return Triple(currentMessageCount, currentReadCount, currentMemberCount)
    }
    
    fun getAllMessages() = messages.toList()
    
    fun getMembers() = members.toList()
}

class ChatUser(
    private val chatRoom: ChatRoom,
    private val userId: String
) {
    suspend fun join() {
        chatRoom.addMember(userId)
        println("$userId joined the chat")
    }
    
    suspend fun leave() {
        chatRoom.removeMember(userId)
        println("$userId left the chat")
    }
    
    suspend fun sendMessage(content: String) {
        val message = chatRoom.sendMessage(userId, content)
        println("$userId sent: $content")
        
        delay(Random.nextLong(10, 50))
        
        chatRoom.markAsDelivered(message.id)
    }
    
    suspend fun readMessages() {
        val unreadMessages = chatRoom.getUnreadMessages(userId)
        
        unreadMessages.forEach { message ->
            chatRoom.markAsRead(message.id, userId)
            println("$userId read message from ${message.sender}")
            delay(Random.nextLong(5, 20))
        }
    }
    
    suspend fun browseRecentMessages() {
        val recent = chatRoom.getRecentMessages(5)
        println("$userId browsing ${recent.size} recent messages")
    }
}

suspend fun simulateUserChatActivity(
    user: ChatUser,
    userId: Int
) {
    user.join()
    
    val messages = listOf(
        "Hello everyone!", "How are you?", "What's up?",
        "Nice to meet you", "Great discussion", "I agree",
        "Interesting point", "Let me think about that",
        "That makes sense", "Thanks for sharing"
    )
    
    repeat(15) { attempt ->
        when (Random.nextInt(3)) {
            0 -> user.sendMessage(messages.random())
            1 -> user.readMessages()
            2 -> user.browseRecentMessages()
        }
        delay(Random.nextLong(20, 100))
    }
    
    user.leave()
}

suspend fun simulateMessageDelivery(
    chatRoom: ChatRoom
) {
    repeat(20) { attempt ->
        val messages = chatRoom.getAllMessages()
        
        if (messages.isNotEmpty()) {
            val message = messages.random()
            chatRoom.markAsDelivered(message.id)
        }
        
        delay(Random.nextLong(30, 80))
    }
}

suspend fun simulateMessageReading(
    chatRoom: ChatRoom,
    userId: String
) {
    repeat(10) { attempt ->
        val unreadMessages = chatRoom.getUnreadMessages(userId)
        
        if (unreadMessages.isNotEmpty()) {
            val message = unreadMessages.random()
            chatRoom.markAsRead(message.id, userId)
        }
        
        delay(Random.nextLong(50, 150))
    }
}

fun main() = runBlocking {
    val chatRoom = ChatRoom()
    
    println("Starting Chat Room Simulation...")
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMessageDelivery(chatRoom)
    })
    
    val users = listOf(
        ChatUser(chatRoom, "Alice"),
        ChatUser(chatRoom, "Bob"),
        ChatUser(chatRoom, "Charlie"),
        ChatUser(chatRoom, "David"),
        ChatUser(chatRoom, "Eve")
    )
    
    users.forEachIndexed { index, user ->
        jobs.add(launch {
            simulateUserChatActivity(user, index)
        })
    }
    
    delay(3000)
    
    val members = chatRoom.getMembers()
    if (members.isNotEmpty()) {
        val randomMember = members.random()
        
        jobs.add(launch {
            simulateMessageReading(chatRoom, randomMember)
        })
    }
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (messageCount, readCount, memberCount) = chatRoom.getStatistics()
    
    println("\n=== Chat Room Statistics ===")
    println("Total Messages: $messageCount")
    println("Read Messages: $readCount")
    println("Active Members: $memberCount")
    
    val messages = chatRoom.getAllMessages()
    println("\n=== Recent Messages ===")
    messages.takeLast(10).forEach { message ->
        println(
            "  ${message.sender}: ${message.content} " +
            "[${message.status}] Read by: ${message.readBy.size}"
        )
    }
    
    val statusCounts = messages.groupingBy { it.status }.eachCount()
    println("\n=== Message Status Distribution ===")
    statusCounts.forEach { (status, count) ->
        println("  $status: $count")
    }
    
    val unreadByAll = messages.filter { message ->
        message.status != MessageStatus.FAILED &&
        message.readBy.isEmpty()
    }
    
    if (unreadByAll.isNotEmpty()) {
        println("\n⚠️  Messages not read by anyone: ${unreadByAll.size}")
    }
    
    val readByMultiple = messages.filter { message ->
        message.readBy.size > 1
    }
    
    if (readByMultiple.isNotEmpty()) {
        println("\nMessages read by multiple users:")
        readByMultiple.take(5).forEach { message ->
            println("  ${message.id}: ${message.readBy.joinToString()}")
        }
    }
}