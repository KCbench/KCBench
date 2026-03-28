import kotlinx.coroutines.*
import kotlin.random.Random

data class Event(
    val eventId: String,
    val eventType: String,
    val data: String,
    val timestamp: Long,
    var processed: Boolean = false
)

class EventStreamManager {
    private val events = mutableListOf<Event>()
    private val subscribers = mutableMapOf<String, MutableList<String>>()
    private val eventTypes = listOf(
        "user_created", "user_updated", "user_deleted",
        "order_created", "order_updated", "order_cancelled",
        "payment_received", "payment_failed",
        "notification_sent", "error_occurred"
    )
    
    suspend fun produceEvent(
        eventType: String,
        data: String
    ): Event {
        val event = Event(
            eventId = "EVT_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}",
            eventType = eventType,
            data = data,
            timestamp = System.currentTimeMillis(),
            processed = false
        )
        
        events.add(event)
        delay(Random.nextLong(5, 20))
        
        return event
    }
    
    suspend fun consumeEvent(eventId: String): Boolean {
        val event = events.find { it.eventId == eventId }
        
        if (event != null && !event.processed) {
            delay(Random.nextLong(10, 50))
            
            event.processed = true
            delay(Random.nextLong(5, 15))
            
            return true
        }
        
        return false
    }
    
    suspend fun subscribeToEvent(
        eventType: String,
        subscriber: String
    ): Boolean {
        if (!eventTypes.contains(eventType)) {
            return false
        }
        
        subscribers.getOrPut(eventType) { mutableListOf() }.add(subscriber)
        delay(Random.nextLong(5, 15))
        
        return true
    }
    
    suspend fun unsubscribeFromEvent(
        eventType: String,
        subscriber: String
    ): Boolean {
        val subscribersList = subscribers[eventType] ?: return false
        
        if (subscribersList.contains(subscriber)) {
            subscribersList.remove(subscriber)
            delay(Random.nextLong(5, 15))
            
            return true
        }
        
        return false
    }
    
    suspend fun getUnprocessedEvents(): List<Event> {
        return events.filter { !it.processed }
    }
    
    suspend fun getEventsByType(eventType: String): List<Event> {
        return events.filter { it.eventType == eventType }
    }
    
    suspend fun getSubscribers(eventType: String): List<String> {
        return subscribers[eventType] ?: emptyList()
    }
    
    fun getAllEvents() = events.toList()
    
    fun getAllEventTypes() = eventTypes
}

class EventProducer(
    private val eventManager: EventStreamManager,
    private val producerName: String
) {
    suspend fun produceRandomEvent(): Event {
        val eventTypes = eventManager.getAllEventTypes()
        val eventType = eventTypes.random()
        val data = "Data from $producerName at ${System.currentTimeMillis()}"
        
        return eventManager.produceEvent(eventType, data)
    }
    
    suspend fun produceMultipleEvents(count: Int): Int {
        var produced = 0
        
        repeat(count) {
            produceRandomEvent()
            produced++
        }
        
        return produced
    }
}

class EventConsumer(
    private val eventManager: EventStreamManager,
    private val consumerName: String
) {
    suspend fun consumeRandomEvent(): Boolean {
        val unprocessedEvents = eventManager.getUnprocessedEvents()
        
        if (unprocessedEvents.isEmpty()) {
            return false
        }
        
        val event = unprocessedEvents.random()
        return eventManager.consumeEvent(event.eventId)
    }
    
    suspend fun consumeMultipleEvents(count: Int): Int {
        var consumed = 0
        
        repeat(count) {
            if (consumeRandomEvent()) {
                consumed++
            }
        }
        
        return consumed
    }
    
    suspend fun subscribeToRandomEvents(): Int {
        val eventTypes = eventManager.getAllEventTypes()
        val selectedTypes = eventTypes.shuffled().take(3)
        var subscribed = 0
        
        selectedTypes.forEach { eventType ->
            if (eventManager.subscribeToEvent(eventType, consumerName)) {
                subscribed++
            }
        }
        
        return subscribed
    }
}

suspend fun simulateProducerActivity(
    producer: EventProducer,
    producerId: Int
) {
    repeat(15) { attempt ->
        producer.produceRandomEvent()
        delay(Random.nextLong(20, 80))
    }
}

suspend fun simulateConsumerActivity(
    consumer: EventConsumer,
    consumerId: Int
) {
    repeat(12) { attempt ->
        consumer.consumeMultipleEvents(2)
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateSubscriptionManagement(
    eventManager: EventStreamManager
) {
    repeat(10) { attempt ->
        val eventTypes = eventManager.getAllEventTypes()
        val eventType = eventTypes.random()
        
        if (Random.nextBoolean()) {
            eventManager.subscribeToEvent(eventType, "Subscriber_${Random.nextInt(100, 999)}")
        } else {
            val subscribers = eventManager.getSubscribers(eventType)
            
            if (subscribers.isNotEmpty()) {
                val subscriber = subscribers.random()
                eventManager.unsubscribeFromEvent(eventType, subscriber)
            }
        }
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateEventProcessing(
    eventManager: EventStreamManager
) {
    repeat(20) { attempt ->
        val unprocessedEvents = eventManager.getUnprocessedEvents()
        
        println("Unprocessed events: ${unprocessedEvents.size}")
        
        unprocessedEvents.take(5).forEach { event ->
            eventManager.consumeEvent(event.eventId)
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateEventDependencies(
    eventManager: EventStreamManager
) {
    repeat(15) { attempt ->
        val userEvents = eventManager.getEventsByType("user_created")
        
        if (userEvents.isNotEmpty()) {
            val userEvent = userEvents.random()
            
            if (!userEvent.processed) {
                delay(Random.nextLong(30, 100))
                
                val orderEvent = eventManager.produceEvent(
                    "order_created",
                    "Order for user ${userEvent.eventId}"
                )
                
                delay(Random.nextLong(20, 80))
                
                eventManager.consumeEvent(orderEvent.eventId)
            }
        }
        
        delay(Random.nextLong(40, 120))
    }
}

fun main() = runBlocking {
    val eventManager = EventStreamManager()
    
    println("Starting Event Stream Simulation...")
    println("Event Types: ${eventManager.getAllEventTypes().size}")
    println()
    
    val jobs = mutableListOf<Job>()
    
    val producers = listOf(
        EventProducer(eventManager, "Alice"),
        EventProducer(eventManager, "Bob"),
        EventProducer(eventManager, "Charlie")
    )
    
    producers.forEachIndexed { index, producer ->
        jobs.add(launch {
            simulateProducerActivity(producer, index)
        })
    }
    
    val consumers = listOf(
        EventConsumer(eventManager, "David"),
        EventConsumer(eventManager, "Eve"),
        EventConsumer(eventManager, "Frank"),
        EventConsumer(eventManager, "Grace")
    )
    
    consumers.forEachIndexed { index, consumer ->
        jobs.add(launch {
            simulateConsumerActivity(consumer, index)
        })
    }
    
    jobs.add(launch {
        simulateSubscriptionManagement(eventManager)
    })
    
    jobs.add(launch {
        simulateEventProcessing(eventManager)
    })
    
    jobs.add(launch {
        simulateEventDependencies(eventManager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val events = eventManager.getAllEvents()
    val processedEvents = events.filter { it.processed }
    val unprocessedEvents = events.filter { !it.processed }
    
    println("\n=== Event Stream Statistics ===")
    println("Total Events: ${events.size}")
    println("Processed Events: ${processedEvents.size}")
    println("Unprocessed Events: ${unprocessedEvents.size}")
    
    println("\n=== Event Type Distribution ===")
    eventManager.getAllEventTypes().forEach { eventType ->
        val typeEvents = eventManager.getEventsByType(eventType)
        val processed = typeEvents.count { it.processed }
        
        println("  $eventType: ${typeEvents.size} total, $processed processed")
    }
    
    val outOfOrderEvents = unprocessedEvents.filter { event ->
        val eventType = event.eventType
        
        when (eventType) {
            "order_created" -> {
                val userEvents = eventManager.getEventsByType("user_created")
                userEvents.any { !it.processed }
            }
            else -> false
        }
    }
    
    if (outOfOrderEvents.isNotEmpty()) {
        println("\n⚠️  Events processed out of order:")
        outOfOrderEvents.take(5).forEach { event ->
            println("  ${event.eventId}: ${event.eventType}")
        }
    } else {
        println("\n✅ Events processed in correct order")
    }
    
    val processingRate = if (events.size > 0) {
        (processedEvents.size.toDouble() / events.size * 100)
    } else {
        0.0
    }
    
    println("\nProcessing Rate: ${"%.2f".format(processingRate)}%")
}