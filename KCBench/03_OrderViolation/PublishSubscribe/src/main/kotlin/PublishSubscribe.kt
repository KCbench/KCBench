import kotlinx.coroutines.*
import kotlin.random.Random

data class Topic(
    val name: String,
    var published: Boolean = false,
    var subscribers: MutableList<String> = mutableListOf()
)

data class Message(
    val topic: String,
    val content: String,
    val timestamp: Long
)

class PublishSubscribeSystem {
    private val topics = mutableMapOf<String, Topic>()
    private val messages = mutableListOf<Message>()
    private val subscriptions = mutableMapOf<String, MutableList<String>>()
    
    init {
        initializeTopics()
    }
    
    private fun initializeTopics() {
        val topicNames = listOf(
            "news", "weather", "sports", "finance", "technology",
            "entertainment", "health", "education", "travel", "food"
        )
        
        topicNames.forEach { name ->
            topics[name] = Topic(
                name = name,
                published = false,
                subscribers = mutableListOf()
            )
        }
    }
    
    suspend fun publishTopic(topicName: String): Boolean {
        val topic = topics[topicName] ?: return false
        
        if (!topic.published) {
            delay(Random.nextLong(10, 50))
            
            topic.published = true
            delay(Random.nextLong(10, 30))
            
            return true
        }
        
        return false
    }
    
    suspend fun publishMessage(topicName: String, content: String): Boolean {
        val topic = topics[topicName] ?: return false
        
        if (!topic.published) {
            return false
        }
        
        val message = Message(
            topic = topicName,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        
        messages.add(message)
        delay(Random.nextLong(5, 20))
        
        return true
    }
    
    suspend fun subscribe(topicName: String, subscriber: String): Boolean {
        val topic = topics[topicName] ?: return false
        
        if (!topic.published) {
            return false
        }
        
        if (!topic.subscribers.contains(subscriber)) {
            topic.subscribers.add(subscriber)
            delay(Random.nextLong(5, 15))
            
            subscriptions.getOrPut(subscriber) { mutableListOf() }.add(topicName)
            delay(Random.nextLong(5, 15))
            
            return true
        }
        
        return false
    }
    
    suspend fun unsubscribe(topicName: String, subscriber: String): Boolean {
        val topic = topics[topicName] ?: return false
        
        if (topic.subscribers.contains(subscriber)) {
            topic.subscribers.remove(subscriber)
            delay(Random.nextLong(5, 15))
            
            subscriptions[subscriber]?.remove(topicName)
            delay(Random.nextLong(5, 15))
            
            return true
        }
        
        return false
    }
    
    suspend fun getMessages(topicName: String, limit: Int = 10): List<Message> {
        val topicMessages = messages.filter { it.topic == topicName }
        return topicMessages.takeLast(limit)
    }
    
    suspend fun getSubscribers(topicName: String): List<String> {
        val topic = topics[topicName] ?: return emptyList()
        return topic.subscribers.toList()
    }
    
    fun getAllTopics() = topics.values.toList()
    
    fun getAllMessages() = messages.toList()
}

class Publisher(
    val pubSubSystem: PublishSubscribeSystem,
    private val publisherName: String
) {
    suspend fun publishToTopic(topicName: String, content: String): Boolean {
        if (!pubSubSystem.publishTopic(topicName)) {
            return false
        }
        
        delay(Random.nextLong(10, 30))
        
        return pubSubSystem.publishMessage(topicName, content)
    }
    
    suspend fun publishMultiple(topics: List<String>): Int {
        var published = 0
        
        topics.forEach { topicName ->
            val content = "Message from $publisherName at ${System.currentTimeMillis()}"
            
            if (publishToTopic(topicName, content)) {
                published++
            }
        }
        
        return published
    }
}

class Subscriber(
    val pubSubSystem: PublishSubscribeSystem,
    private val subscriberName: String
) {
    suspend fun subscribeToTopics(topicNames: List<String>): Int {
        var subscribed = 0
        
        topicNames.forEach { topicName ->
            if (pubSubSystem.subscribe(topicName, subscriberName)) {
                subscribed++
            }
        }
        
        return subscribed
    }
    
    suspend fun readMessages(topicName: String): List<Message> {
        return pubSubSystem.getMessages(topicName)
    }
}

suspend fun simulatePublisherActivity(
    publisher: Publisher,
    publisherId: Int
) {
    repeat(15) { attempt ->
        val topics = publisher.pubSubSystem.getAllTopics()
        val selectedTopics = topics.shuffled().take(2)
        val topicNames = selectedTopics.map { it.name }
        
        publisher.publishMultiple(topicNames)
        delay(Random.nextLong(20, 80))
    }
}

suspend fun simulateSubscriberActivity(
    subscriber: Subscriber,
    subscriberId: Int
) {
    repeat(12) { attempt ->
        val topics = subscriber.pubSubSystem.getAllTopics()
        val selectedTopics = topics.shuffled().take(3)
        val topicNames = selectedTopics.map { it.name }
        
        subscriber.subscribeToTopics(topicNames)
        delay(Random.nextLong(30, 100))
        
        topicNames.forEach { topicName ->
            val messages = subscriber.readMessages(topicName)
            if (messages.isNotEmpty()) {
                println("Subscriber $subscriberId received ${messages.size} messages from $topicName")
            }
        }
    }
}

suspend fun simulateTopicPublishing(
    pubSubSystem: PublishSubscribeSystem
) {
    repeat(10) { attempt ->
        val topics = pubSubSystem.getAllTopics()
        val topic = topics.random()
        
        pubSubSystem.publishTopic(topic.name)
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateSubscriptionManagement(
    pubSubSystem: PublishSubscribeSystem
) {
    repeat(8) { attempt ->
        val topics = pubSubSystem.getAllTopics()
        val topic = topics.random()
        
        if (topic.published) {
            val subscribers = pubSubSystem.getSubscribers(topic.name)
            
            if (subscribers.isNotEmpty()) {
                val subscriber = subscribers.random()
                pubSubSystem.unsubscribe(topic.name, subscriber)
            }
        }
        
        delay(Random.nextLong(100, 300))
    }
}

fun main() = runBlocking {
    val pubSubSystem = PublishSubscribeSystem()
    
    println("Starting Publish-Subscribe System Simulation...")
    println("Initial Topics:")
    pubSubSystem.getAllTopics().forEach { topic ->
        println("  ${topic.name}: Published=${topic.published}, Subscribers=${topic.subscribers.size}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateTopicPublishing(pubSubSystem)
    })
    
    val publishers = listOf(
        Publisher(pubSubSystem, "Alice"),
        Publisher(pubSubSystem, "Bob"),
        Publisher(pubSubSystem, "Charlie")
    )
    
    publishers.forEachIndexed { index, publisher ->
        jobs.add(launch {
            simulatePublisherActivity(publisher, index)
        })
    }
    
    val subscribers = listOf(
        Subscriber(pubSubSystem, "David"),
        Subscriber(pubSubSystem, "Eve"),
        Subscriber(pubSubSystem, "Frank"),
        Subscriber(pubSubSystem, "Grace")
    )
    
    subscribers.forEachIndexed { index, subscriber ->
        jobs.add(launch {
            simulateSubscriberActivity(subscriber, index)
        })
    }
    
    jobs.add(launch {
        simulateSubscriptionManagement(pubSubSystem)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Topic Status ===")
    pubSubSystem.getAllTopics().forEach { topic ->
        println(
            "  ${topic.name}: Published=${topic.published}, " +
            "Subscribers=${topic.subscribers.size}"
        )
    }
    
    val publishedTopics = pubSubSystem.getAllTopics().filter { it.published }
    val unpublishedTopics = pubSubSystem.getAllTopics().filter { !it.published }
    
    println("\nPublished Topics: ${publishedTopics.size}")
    println("Unpublished Topics: ${unpublishedTopics.size}")
    
    val messages = pubSubSystem.getAllMessages()
    println("\nTotal Messages: ${messages.size}")
    
    val topicsWithSubscribers = pubSubSystem.getAllTopics()
        .filter { it.subscribers.isNotEmpty() }
    
    if (topicsWithSubscribers.isNotEmpty()) {
        println("\nTopics with Subscribers:")
        topicsWithSubscribers.take(5).forEach { topic ->
            println("  ${topic.name}: ${topic.subscribers.joinToString()}")
        }
    }
    
    val emptyTopics = pubSubSystem.getAllTopics()
        .filter { topic -> topic.published && topic.subscribers.isEmpty() }
    
    if (emptyTopics.isNotEmpty()) {
        println("\n⚠️  Published topics with no subscribers:")
        emptyTopics.forEach { topic ->
            println("  ${topic.name}")
        }
    }
}