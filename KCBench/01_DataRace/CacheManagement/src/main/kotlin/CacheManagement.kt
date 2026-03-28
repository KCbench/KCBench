import kotlinx.coroutines.*
import kotlin.random.Random

data class CacheEntry<T>(
    val key: String,
    val value: T,
    var timestamp: Long,
    var accessCount: Int = 0,
    var lastAccessTime: Long = System.currentTimeMillis()
)

class CacheManager<T> {
    private val cache = mutableMapOf<String, CacheEntry<T>>()
    private var hitCount = 0
    private var missCount = 0
    private var evictionCount = 0
    private val maxSize = 100
    
    suspend fun get(key: String): T? {
        val entry = cache[key]
        
        if (entry != null) {
            val currentAccessCount = entry.accessCount
            delay(Random.nextLong(1, 5))
            
            entry.accessCount = currentAccessCount + 1
            delay(Random.nextLong(1, 5))
            
            entry.lastAccessTime = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            val currentHitCount = hitCount
            delay(Random.nextLong(1, 5))
            hitCount = currentHitCount + 1
            
            return entry.value
        }
        
        val currentMissCount = missCount
        delay(Random.nextLong(1, 5))
        missCount = currentMissCount + 1
        
        return null
    }
    
    suspend fun put(key: String, value: T) {
        val currentTime = System.currentTimeMillis()
        delay(Random.nextLong(1, 10))
        
        val entry = CacheEntry(
            key = key,
            value = value,
            timestamp = currentTime
        )
        
        cache[key] = entry
        delay(Random.nextLong(1, 5))
        
        if (cache.size > maxSize) {
            evictLeastRecentlyUsed()
        }
    }
    
    suspend fun remove(key: String): Boolean {
        val entry = cache.remove(key)
        delay(Random.nextLong(1, 5))
        return entry != null
    }
    
    suspend fun clear() {
        cache.clear()
        delay(Random.nextLong(1, 5))
    }
    
    private suspend fun evictLeastRecentlyUsed() {
        val entries = cache.values.toList()
        delay(Random.nextLong(1, 5))
        
        if (entries.isNotEmpty()) {
            val lruEntry = entries.minByOrNull { it.lastAccessTime }
            delay(Random.nextLong(1, 5))
            
            if (lruEntry != null) {
                cache.remove(lruEntry.key)
                delay(Random.nextLong(1, 5))
                
                val currentEvictionCount = evictionCount
                delay(Random.nextLong(1, 5))
                evictionCount = currentEvictionCount + 1
            }
        }
    }
    
    suspend fun getStatistics(): Triple<Int, Int, Int> {
        val currentHitCount = hitCount
        delay(Random.nextLong(1, 5))
        
        val currentMissCount = missCount
        delay(Random.nextLong(1, 5))
        
        val currentEvictionCount = evictionCount
        delay(Random.nextLong(1, 5))
        
        return Triple(currentHitCount, currentMissCount, currentEvictionCount)
    }
    
    fun getSize() = cache.size
    
    fun getKeys() = cache.keys.toList()
}

class DataService(
    private val cache: CacheManager<String>
) {
    suspend fun fetchData(key: String): String {
        val cachedValue = cache.get(key)
        
        if (cachedValue != null) {
            return cachedValue
        }
        
        delay(Random.nextLong(10, 50))
        
        val newValue = "Data for $key at ${System.currentTimeMillis()}"
        cache.put(key, newValue)
        
        return newValue
    }
    
    suspend fun updateData(key: String, value: String) {
        cache.put(key, value)
    }
    
    suspend fun deleteData(key: String) {
        cache.remove(key)
    }
}

suspend fun simulateReadOperations(
    service: DataService,
    clientId: Int
) {
    repeat(50) { attempt ->
        val key = "key_${Random.nextInt(1, 21)}"
        service.fetchData(key)
        delay(Random.nextLong(1, 20))
    }
}

suspend fun simulateWriteOperations(
    service: DataService,
    clientId: Int
) {
    repeat(20) { attempt ->
        val key = "key_${Random.nextInt(1, 21)}"
        val value = "Updated data for $key at ${System.currentTimeMillis()}"
        service.updateData(key, value)
        delay(Random.nextLong(10, 50))
    }
}

suspend fun simulateDeleteOperations(
    service: DataService,
    clientId: Int
) {
    repeat(5) { attempt ->
        val key = "key_${Random.nextInt(1, 21)}"
        service.deleteData(key)
        delay(Random.nextLong(50, 100))
    }
}

suspend fun simulateCacheWarmup(
    service: DataService
) {
    repeat(20) { index ->
        val key = "key_$index"
        val value = "Initial data for $key"
        service.updateData(key, value)
        delay(Random.nextLong(10, 30))
    }
}

fun main() = runBlocking {
    val cache = CacheManager<String>()
    val service = DataService(cache)
    
    println("Starting Cache Management Simulation...")
    println("Warming up cache...")
    
    launch {
        simulateCacheWarmup(service)
    }
    
    delay(500)
    
    println("Cache size after warmup: ${cache.getSize()}")
    println()
    
    val jobs = mutableListOf<Job>()
    
    repeat(10) { clientId ->
        jobs.add(launch {
            simulateReadOperations(service, clientId)
        })
    }
    
    repeat(5) { clientId ->
        jobs.add(launch {
            simulateWriteOperations(service, clientId)
        })
    }
    
    repeat(3) { clientId ->
        jobs.add(launch {
            simulateDeleteOperations(service, clientId)
        })
    }
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (hits, misses, evictions) = cache.getStatistics()
    val totalRequests = hits + misses
    val hitRate = if (totalRequests > 0) {
        (hits.toDouble() / totalRequests * 100)
    } else {
        0.0
    }
    
    println("\n=== Cache Statistics ===")
    println("Cache Size: ${cache.getSize()}")
    println("Total Requests: $totalRequests")
    println("Hits: $hits")
    println("Misses: $misses")
    println("Evictions: $evictions")
    println("Hit Rate: ${"%.2f".format(hitRate)}%")
    
    val keys = cache.getKeys()
    println("\n=== Cache Keys ===")
    keys.take(10).forEach { key ->
        println("  $key")
    }
    if (keys.size > 10) {
        println("  ... and ${keys.size - 10} more")
    }
    
    val duplicateChecks = keys.groupingBy { it }.eachCount()
        .filter { (_, count) -> count > 1 }
    
    if (duplicateChecks.isNotEmpty()) {
        println("\n⚠️  Duplicate keys detected:")
        duplicateChecks.forEach { (key, count) ->
            println("  $key: $count entries")
        }
    } else {
        println("\n✅ No duplicate keys")
    }
}