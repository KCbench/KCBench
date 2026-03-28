import kotlinx.coroutines.*
import kotlin.random.Random

data class RateLimitEntry(
    val clientId: String,
    var requestCount: Int,
    var windowStart: Long,
    var lastReset: Long
)

class RateLimiter {
    private val entries = mutableMapOf<String, RateLimitEntry>()
    private val maxRequests = 100
    private val windowSize = 60000L
    
    suspend fun checkRateLimit(clientId: String): Boolean {
        val entry = entries[clientId]
        
        if (entry == null) {
            val newEntry = RateLimitEntry(
                clientId = clientId,
                requestCount = 1,
                windowStart = System.currentTimeMillis(),
                lastReset = System.currentTimeMillis()
            )
            entries[clientId] = newEntry
            delay(Random.nextLong(1, 5))
            return true
        }
        
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - entry.windowStart > windowSize) {
            delay(Random.nextLong(1, 10))
            
            entry.requestCount = 1
            entry.windowStart = currentTime
            entry.lastReset = currentTime
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        if (entry.requestCount < maxRequests) {
            delay(Random.nextLong(1, 10))
            
            entry.requestCount += 1
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun recordRequest(clientId: String): Boolean {
        val allowed = checkRateLimit(clientId)
        
        if (allowed) {
            val entry = entries[clientId]
            
            if (entry != null) {
                val currentCount = entry.requestCount
                delay(Random.nextLong(1, 5))
                
                entry.requestCount = currentCount + 1
                delay(Random.nextLong(1, 5))
            }
        }
        
        return allowed
    }
    
    suspend fun resetClient(clientId: String): Boolean {
        val entry = entries[clientId] ?: return false
        
        entry.requestCount = 0
        entry.windowStart = System.currentTimeMillis()
        entry.lastReset = System.currentTimeMillis()
        
        delay(Random.nextLong(1, 5))
        
        return true
    }
    
    suspend fun getClientStats(clientId: String): RateLimitEntry? {
        val entry = entries[clientId]
        
        if (entry != null) {
            val currentTime = System.currentTimeMillis()
            val timeInWindow = currentTime - entry.windowStart
            
            return entry.copy(
                requestCount = entry.requestCount,
                windowStart = entry.windowStart,
                lastReset = entry.lastReset
            )
        }
        
        return null
    }
    
    fun getAllEntries() = entries.values.toList()
    
    fun getMaxRequests() = maxRequests
    fun getWindowSize() = windowSize
    
    fun getBlockedClients(): List<RateLimitEntry> {
        return entries.values.filter { it.requestCount >= maxRequests }
    }
}

class ApiClient(
    private val rateLimiter: RateLimiter,
    private val clientId: String
) {
    suspend fun makeRequest(): Boolean {
        return rateLimiter.recordRequest(clientId)
    }
    
    suspend fun makeMultipleRequests(count: Int): Int {
        var successful = 0
        
        repeat(count) {
            if (makeRequest()) {
                successful++
            }
            delay(Random.nextLong(1, 10))
        }
        
        return successful
    }
}

suspend fun simulateClientRequests(
    client: ApiClient,
    clientId: Int
) {
    repeat(25) { attempt ->
        client.makeRequest()
        delay(Random.nextLong(5, 30))
    }
}

suspend fun simulateBurstRequests(
    client: ApiClient,
    clientId: Int
) {
    repeat(5) { attempt ->
        val requests = Random.nextInt(10, 30)
        val successful = client.makeMultipleRequests(requests)
        
        println("Client $clientId burst: $successful/$requests successful")
        
        delay(Random.nextLong(100, 500))
    }
}

suspend fun simulateRateLimitChecks(
    rateLimiter: RateLimiter
) {
    repeat(15) { attempt ->
        val entries = rateLimiter.getAllEntries()
        
        println("Active clients: ${entries.size}")
        
        val blocked = rateLimiter.getBlockedClients()
        println("Blocked clients: ${blocked.size}")
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun simulateClientResets(
    rateLimiter: RateLimiter
) {
    repeat(10) { attempt ->
        val entries = rateLimiter.getAllEntries()
        
        if (entries.isNotEmpty()) {
            val entry = entries.random()
            rateLimiter.resetClient(entry.clientId)
        }
        
        delay(Random.nextLong(300, 600))
    }
}

fun main() = runBlocking {
    val rateLimiter = RateLimiter()
    
    println("Starting Rate Limiter Simulation...")
    println("Max Requests: ${rateLimiter.getMaxRequests()} per ${rateLimiter.getWindowSize()}ms")
    println()
    
    val jobs = mutableListOf<Job>()
    
    val clients = listOf(
        ApiClient(rateLimiter, "Alice"),
        ApiClient(rateLimiter, "Bob"),
        ApiClient(rateLimiter, "Charlie"),
        ApiClient(rateLimiter, "David"),
        ApiClient(rateLimiter, "Eve")
    )
    
    clients.forEachIndexed { index, client ->
        jobs.add(launch {
            simulateClientRequests(client, index)
        })
    }
    
    clients.forEachIndexed { index, client ->
        jobs.add(launch {
            simulateBurstRequests(client, index)
        })
    }
    
    jobs.add(launch {
        simulateRateLimitChecks(rateLimiter)
    })
    
    jobs.add(launch {
        simulateClientResets(rateLimiter)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Rate Limiter Statistics ===")
    
    val entries = rateLimiter.getAllEntries()
    println("Total Clients: ${entries.size}")
    
    val blockedClients = rateLimiter.getBlockedClients()
    println("Blocked Clients: ${blockedClients.size}")
    
    println("\n=== Client Statistics ===")
    entries.take(10).forEach { entry ->
        println(
            "  ${entry.clientId}: Requests=${entry.requestCount}, " +
            "WindowStart=${entry.windowStart}, " +
            "LastReset=${entry.lastReset}"
        )
    }
    
    if (blockedClients.isNotEmpty()) {
        println("\n⚠️  Blocked Clients:")
        blockedClients.take(5).forEach { entry ->
            println("  ${entry.clientId}: ${entry.requestCount} requests")
        }
    }
    
    val totalRequests = entries.sumOf { it.requestCount }
    val allowedRequests = entries.filter { it.requestCount < rateLimiter.getMaxRequests() }.size
    
    println("\nTotal Requests: $totalRequests")
    println("Allowed Clients: $allowedRequests")
    
    val blockingRate = if (entries.size > 0) {
        (blockedClients.size.toDouble() / entries.size * 100)
    } else {
        0.0
    }
    
    println("Blocking Rate: ${"%.2f".format(blockingRate)}%")
}