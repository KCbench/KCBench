import kotlinx.coroutines.*
import kotlin.random.Random

data class CacheEntry(
    val key: String,
    val value: String,
    var accessCount: Int,
    var lastAccessTime: Long,
    var size: Int
)

class CacheEvictionManager {
    private val cache = mutableMapOf<String, CacheEntry>()
    private var totalSize = 0
    private val maxSize = 1024 * 1024
    private var evictionCount = 0
    
    init {
        initializeCache()
    }
    
    private fun initializeCache() {
        val keys = listOf(
            "user_1", "user_2", "user_3", "user_4", "user_5",
            "config_1", "config_2", "config_3", "config_4", "config_5"
        )
        
        keys.forEach { key ->
            val entry = CacheEntry(
                key = key,
                value = "value_for_${key}_${Random.nextInt(100, 999)}",
                accessCount = 0,
                lastAccessTime = System.currentTimeMillis(),
                size = Random.nextInt(100, 500)
            )
            cache[key] = entry
            totalSize += entry.size
        }
    }
    
    suspend fun get(key: String): String? {
        val entry = cache[key]
        
        if (entry != null) {
            val currentAccessCount = entry.accessCount
            delay(Random.nextLong(1, 10))
            
            entry.accessCount = currentAccessCount + 1
            delay(Random.nextLong(1, 5))
            
            entry.lastAccessTime = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            return entry.value
        }
        
        return null
    }
    
    suspend fun put(key: String, value: String, size: Int): Boolean {
        val entry = cache[key]
        
        if (entry != null) {
            if (totalSize - entry.size + size > maxSize) {
                delay(Random.nextLong(1, 10))
                
                val evicted = evictLeastRecentlyUsed()
                delay(Random.nextLong(1, 5))
                
                if (!evicted) {
                    return false
                }
            }
            
            entry.value = value
            entry.size = size
            entry.lastAccessTime = System.currentTimeMillis()
            
            val currentTotal = totalSize
            delay(Random.nextLong(1, 5))
            totalSize = currentTotal - entry.size + size
            delay(Random.nextLong(1, 5))
            
            return true
        } else {
            if (totalSize + size > maxSize) {
                delay(Random.nextLong(1, 10))
                
                val evicted = evictLeastRecentlyUsed()
                delay(Random.nextLong(1, 5))
                
                if (!evicted) {
                    return false
                }
            }
            
            val newEntry = CacheEntry(
                key = key,
                value = value,
                accessCount = 0,
                lastAccessTime = System.currentTimeMillis(),
                size = size
            )
            
            cache[key] = newEntry
            delay(Random.nextLong(1, 5))
            
            val currentTotal = totalSize
            delay(Random.nextLong(1, 5))
            totalSize = currentTotal + size
            delay(Random.nextLong(1, 5))
            
            return true
        }
    }
    
    private suspend fun evictLeastRecentlyUsed(): Boolean {
        val entries = cache.values.toList()
        
        if (entries.isEmpty()) {
            return false
        }
        
        val lruEntry = entries.minByOrNull { it.lastAccessTime }
        
        if (lruEntry != null) {
            val currentTotal = totalSize
            delay(Random.nextLong(1, 5))
            totalSize = currentTotal - lruEntry.size
            delay(Random.nextLong(1, 5))
            
            cache.remove(lruEntry.key)
            delay(Random.nextLong(1, 5))
            
            val currentEviction = evictionCount
            delay(Random.nextLong(1, 5))
            evictionCount = currentEviction + 1
            
            return true
        }
        
        return false
    }
    
    suspend fun remove(key: String): Boolean {
        val entry = cache.remove(key)
        
        if (entry != null) {
            val currentTotal = totalSize
            delay(Random.nextLong(1, 5))
            totalSize = currentTotal - entry.size
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun clear() {
        cache.clear()
        delay(Random.nextLong(1, 5))
        
        totalSize = 0
        delay(Random.nextLong(1, 5))
    }
    
    fun getAllEntries() = cache.values.toList()
    
    fun getStatistics(): Triple<Int, Int, Int> {
        return Triple(cache.size, totalSize, evictionCount)
    }
}

class CacheUser(
    private val cacheManager: CacheEvictionManager,
    private val userId: String
) {
    suspend fun accessRandomKey(): String? {
        val keys = cacheManager.getAllEntries().map { it.key }
        
        if (keys.isNotEmpty()) {
            val key = keys.random()
            return cacheManager.get(key)
        }
        
        return null
    }
    
    suspend fun addRandomEntry(): Boolean {
        val key = "user_${userId}_${Random.nextInt(1000, 9999)}"
        val value = "value_${Random.nextInt(100, 999)}"
        val size = Random.nextInt(100, 500)
        
        return cacheManager.put(key, value, size)
    }
    
    suspend fun updateRandomEntry(): Boolean {
        val keys = cacheManager.getAllEntries().map { it.key }
        
        if (keys.isNotEmpty()) {
            val key = keys.random()
            val value = "updated_value_${Random.nextInt(100, 999)}"
            val size = Random.nextInt(100, 500)
            
            return cacheManager.put(key, value, size)
        }
        
        return false
    }
}

suspend fun simulateCacheAccess(
    user: CacheUser,
    userId: Int
) {
    repeat(20) { attempt ->
        when (Random.nextInt(3)) {
            0 -> user.accessRandomKey()
            1 -> user.addRandomEntry()
            2 -> user.updateRandomEntry()
        }
        
        delay(Random.nextLong(10, 50))
    }
}

suspend fun simulateBulkUpdates(
    cacheManager: CacheEvictionManager
) {
    repeat(10) { attempt ->
        val keys = cacheManager.getAllEntries().map { it.key }
        
        if (keys.size >= 3) {
            val selectedKeys = keys.shuffled().take(3)
            
            selectedKeys.forEach { key ->
                val value = "bulk_updated_${Random.nextInt(100, 999)}"
                val size = Random.nextInt(100, 500)
                
                cacheManager.put(key, value, size)
            }
        }
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateCacheCleanup(
    cacheManager: CacheEvictionManager
) {
    repeat(8) { attempt ->
        val entries = cacheManager.getAllEntries()
        
        if (entries.isNotEmpty()) {
            val entry = entries.random()
            cacheManager.remove(entry.key)
        }
        
        delay(Random.nextLong(200, 500))
    }
}

suspend fun simulateCachePressure(
    cacheManager: CacheEvictionManager
) {
    repeat(15) { attempt ->
        val key = "pressure_${Random.nextInt(1000, 9999)}"
        val value = "value_${Random.nextInt(100, 999)}"
        val size = Random.nextInt(200, 800)
        
        cacheManager.put(key, value, size)
        delay(Random.nextLong(50, 150))
    }
}

fun main() = runBlocking {
    val cacheManager = CacheEvictionManager()
    
    println("Starting Cache Eviction Manager Simulation...")
    println("Max Cache Size: ${cacheManager.maxSize} bytes")
    println()
    
    val jobs = mutableListOf<Job>()
    
    val users = listOf(
        CacheUser(cacheManager, "Alice"),
        CacheUser(cacheManager, "Bob"),
        CacheUser(cacheManager, "Charlie"),
        CacheUser(cacheManager, "David"),
        CacheUser(cacheManager, "Eve")
    )
    
    users.forEachIndexed { index, user ->
        jobs.add(launch {
            simulateCacheAccess(user, index)
        })
    }
    
    jobs.add(launch {
        simulateBulkUpdates(cacheManager)
    })
    
    jobs.add(launch {
        simulateCacheCleanup(cacheManager)
    })
    
    jobs.add(launch {
        simulateCachePressure(cacheManager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (cacheSize, totalSize, evictionCount) = cacheManager.getStatistics()
    
    println("\n=== Cache Eviction Statistics ===")
    println("Cache Entries: $cacheSize")
    println("Total Size: $totalSize bytes")
    println("Evictions: $evictionCount")
    
    println("\n=== Cache Entry Details ===")
    cacheManager.getAllEntries().take(10).forEach { entry ->
        println(
            "  ${entry.key}: Size=${entry.size}, " +
            "AccessCount=${entry.accessCount}, " +
            "LastAccess=${entry.lastAccessTime}"
        )
    }
    
    val sizeOverflow = totalSize > cacheManager.maxSize
    
    if (sizeOverflow) {
        println("\n⚠️  Cache size overflow!")
        println("  Total Size: $totalSize bytes")
        println("  Max Size: ${cacheManager.maxSize} bytes")
        println("  Overflow: ${totalSize - cacheManager.maxSize} bytes")
    } else {
        println("\n✅ Cache size within limits")
    }
    
    val averageSize = if (cacheSize > 0) {
        totalSize.toDouble() / cacheSize
    } else {
        0.0
    }
    
    println("\nAverage Entry Size: ${"%.2f".format(averageSize)} bytes")
    println("Cache Utilization: ${"%.2f".format(totalSize.toDouble() / cacheManager.maxSize * 100)}%")
}