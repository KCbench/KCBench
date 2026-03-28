import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class CacheEntry(
    val entryId: String,
    val key: String,
    var value: String,
    var cached: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class CacheRegion(
    val regionId: String,
    val regionName: String,
    var locked: Boolean = false,
    var active: Boolean = false,
    val mutex: Mutex = Mutex()
)

class CacheSystem {
    private val entries = mutableMapOf<String, CacheEntry>()
    private val regions = mutableMapOf<String, CacheRegion>()
    private val entryPoolMutex = Mutex()
    private val regionPoolMutex = Mutex()
    
    init {
        initializeEntries()
        initializeRegions()
    }
    
    private fun initializeEntries() {
        val entryConfigs = listOf(
            Triple("ENTRY001", "user:1", "Alice"),
            Triple("ENTRY002", "user:2", "Bob"),
            Triple("ENTRY003", "user:3", "Charlie"),
            Triple("ENTRY004", "product:1", "Laptop"),
            Triple("ENTRY005", "product:2", "Phone"),
            Triple("ENTRY006", "order:1", "Order #1234"),
            Triple("ENTRY007", "order:2", "Order #5678"),
            Triple("ENTRY008", "session:1", "Session #9012"),
            Triple("ENTRY009", "config:1", "Config #3456"),
            Triple("ENTRY010", "config:2", "Config #7890")
        )
        
        entryConfigs.forEach { (entryId, key, value) ->
            entries[entryId] = CacheEntry(
                entryId = entryId,
                key = key,
                value = value,
                cached = false,
                locked = false
            )
        }
    }
    
    private fun initializeRegions() {
        val regionConfigs = listOf(
            Pair("REGION001", "UserCache"),
            Pair("REGION002", "ProductCache"),
            Pair("REGION003", "OrderCache"),
            Pair("REGION004", "SessionCache"),
            Pair("REGION005", "ConfigCache"),
            Pair("REGION006", "DataCache"),
            Pair("REGION007", "ResultCache"),
            Pair("REGION008", "TempCache"),
            Pair("REGION009", "BackupCache"),
            Pair("REGION010", "SyncCache")
        )
        
        regionConfigs.forEach { (regionId, regionName) ->
            regions[regionId] = CacheRegion(
                regionId = regionId,
                regionName = regionName,
                locked = false,
                active = false
            )
        }
    }
    
    suspend fun cacheEntry(entryId: String): Boolean {
        val entry = entries[entryId] ?: return false
        
        if (entry.cached) {
            return true
        }
        
        entryPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (entry.cached) {
                return true
            }
            
            entry.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                entry.cached = true
                entry.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun evictEntry(entryId: String): Boolean {
        val entry = entries[entryId] ?: return false
        
        if (!entry.cached) {
            return false
        }
        
        entry.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            entryPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                entry.cached = false
                entry.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun activateRegion(regionId: String): Boolean {
        val region = regions[regionId] ?: return false
        
        if (region.active) {
            return true
        }
        
        regionPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (region.active) {
                return true
            }
            
            region.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                region.active = true
                region.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun deactivateRegion(regionId: String): Boolean {
        val region = regions[regionId] ?: return false
        
        if (!region.active) {
            return false
        }
        
        region.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            regionPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                region.active = false
                region.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun cacheEntryWithRegion(
        entryId: String,
        regionId: String
    ): Boolean {
        val entry = entries[entryId] ?: return false
        val region = regions[regionId] ?: return false
        
        if (entry.cached || !region.active) {
            return false
        }
        
        entry.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            region.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                entry.cached = true
                entry.locked = true
                
                delay(Random.nextLong(20, 50))
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferEntry(
        fromEntryId: String,
        toEntryId: String
    ): Boolean {
        val fromEntry = entries[fromEntryId]
        val toEntry = entries[toEntryId]
        
        if (fromEntry == null || toEntry == null) {
            return false
        }
        
        if (!fromEntry.cached || toEntry.cached) {
            return false
        }
        
        fromEntry.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toEntry.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromEntry.cached = false
                fromEntry.locked = false
                toEntry.cached = true
                toEntry.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapEntries(
        entryId1: String,
        entryId2: String
    ): Boolean {
        val entry1 = entries[entryId1]
        val entry2 = entries[entryId2]
        
        if (entry1 == null || entry2 == null) {
            return false
        }
        
        if (!entry1.cached || !entry2.cached) {
            return false
        }
        
        entry1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            entry2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempCached = entry1.cached
                val tempLocked = entry1.locked
                
                entry1.cached = entry2.cached
                entry1.locked = entry2.locked
                entry2.cached = tempCached
                entry2.locked = tempLocked
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getEntryStatus(entryId: String): CacheEntry? {
        val entry = entries[entryId] ?: return null
        
        return entry.mutex.withLock {
            delay(Random.nextLong(5, 15))
            entry.copy()
        }
    }
    
    suspend fun getRegionStatus(regionId: String): CacheRegion? {
        val region = regions[regionId] ?: return null
        
        return region.mutex.withLock {
            delay(Random.nextLong(5, 15))
            region.copy()
        }
    }
    
    fun getAllEntries() = entries.values.toList()
    fun getAllRegions() = regions.values.toList()
}

suspend fun simulateEntryCaching(
    cacheSystem: CacheSystem,
    cacheId: Int
) {
    val entries = cacheSystem.getAllEntries()
    
    repeat(10) { attempt ->
        val entry = entries.filter { !it.cached }.randomOrNull()
        
        if (entry != null) {
            val success = cacheSystem.cacheEntry(entry.entryId)
            if (success) {
                println("Cache $cacheId: Cached ${entry.key}")
            } else {
                println("Cache $cacheId: Failed to cache ${entry.key}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateEntryEviction(
    cacheSystem: CacheSystem,
    cacheId: Int
) {
    val entries = cacheSystem.getAllEntries()
    
    repeat(10) { attempt ->
        val entry = entries.filter { it.cached }.randomOrNull()
        
        if (entry != null) {
            val success = cacheSystem.evictEntry(entry.entryId)
            if (success) {
                println("Cache $cacheId: Evicted ${entry.key}")
            } else {
                println("Cache $cacheId: Failed to evict ${entry.key}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateRegionActivation(
    cacheSystem: CacheSystem,
    cacheId: Int
) {
    val regions = cacheSystem.getAllRegions()
    
    repeat(8) { attempt ->
        val region = regions.filter { !it.active }.randomOrNull()
        
        if (region != null) {
            val success = cacheSystem.activateRegion(region.regionId)
            if (success) {
                println("Cache $cacheId: Activated ${region.regionName}")
            } else {
                println("Cache $cacheId: Failed to activate ${region.regionName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateRegionDeactivation(
    cacheSystem: CacheSystem,
    cacheId: Int
) {
    val regions = cacheSystem.getAllRegions()
    
    repeat(8) { attempt ->
        val region = regions.filter { it.active }.randomOrNull()
        
        if (region != null) {
            val success = cacheSystem.deactivateRegion(region.regionId)
            if (success) {
                println("Cache $cacheId: Deactivated ${region.regionName}")
            } else {
                println("Cache $cacheId: Failed to deactivate ${region.regionName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateEntryCachingWithRegion(
    cacheSystem: CacheSystem,
    cacheId: Int
) {
    val entries = cacheSystem.getAllEntries()
    val regions = cacheSystem.getAllRegions()
    
    repeat(10) { attempt ->
        val entry = entries.filter { !it.cached }.randomOrNull()
        val region = regions.filter { it.active }.randomOrNull()
        
        if (entry != null && region != null) {
            val success = cacheSystem.cacheEntryWithRegion(
                entry.entryId,
                region.regionId
            )
            
            if (success) {
                println("Cache $cacheId: Cached ${entry.key} with ${region.regionName}")
            } else {
                println("Cache $cacheId: Failed to cache entry with region")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateEntryTransfer(
    cacheSystem: CacheSystem,
    transferId: Int
) {
    val entries = cacheSystem.getAllEntries()
    
    repeat(6) { attempt ->
        val cachedEntries = entries.filter { it.cached }
        val uncachedEntries = entries.filter { !it.cached }
        
        if (cachedEntries.isNotEmpty() && uncachedEntries.isNotEmpty()) {
            val fromEntry = cachedEntries.random()
            val toEntry = uncachedEntries.random()
            
            val success = cacheSystem.transferEntry(fromEntry.entryId, toEntry.entryId)
            
            if (success) {
                println("Transfer $transferId: ${fromEntry.key} -> ${toEntry.key}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateEntrySwap(
    cacheSystem: CacheSystem,
    swapId: Int
) {
    val entries = cacheSystem.getAllEntries()
    
    repeat(5) { attempt ->
        val cachedEntries = entries.filter { it.cached }
        
        if (cachedEntries.size >= 2) {
            val entry1 = cachedEntries.random()
            val entry2 = cachedEntries.filter { it.entryId != entry1.entryId }.random()
            
            val success = cacheSystem.swapEntries(entry1.entryId, entry2.entryId)
            
            if (success) {
                println("Swap $swapId: ${entry1.key} <-> ${entry2.key}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorCacheSystem(
    cacheSystem: CacheSystem,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val entries = cacheSystem.getAllEntries()
        val regions = cacheSystem.getAllRegions()
        
        val cached = entries.count { it.cached }
        val locked = entries.count { it.locked }
        val active = regions.count { it.active }
        val lockedRegions = regions.count { it.locked }
        
        println("Monitor $monitorId: Cached=$cached, Locked=$locked, " +
                "Active=$active, LockedRegions=$lockedRegions")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val cacheSystem = CacheSystem()
    
    println("Starting Cache System Simulation...")
    println("Initial Entry Status:")
    cacheSystem.getAllEntries().forEach { entry ->
        println("  ${entry.entryId} (${entry.key}): Cached=${entry.cached}, Locked=${entry.locked}")
    }
    println()
    
    println("Initial Region Status:")
    cacheSystem.getAllRegions().forEach { region ->
        println("  ${region.regionId} (${region.regionName}): Active=${region.active}, Locked=${region.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateEntryCaching(cacheSystem, 1)
    })
    
    jobs.add(launch {
        simulateEntryCaching(cacheSystem, 2)
    })
    
    jobs.add(launch {
        simulateEntryEviction(cacheSystem, 1)
    })
    
    jobs.add(launch {
        simulateEntryEviction(cacheSystem, 2)
    })
    
    jobs.add(launch {
        simulateRegionActivation(cacheSystem, 1)
    })
    
    jobs.add(launch {
        simulateRegionDeactivation(cacheSystem, 1)
    })
    
    jobs.add(launch {
        simulateEntryCachingWithRegion(cacheSystem, 1)
    })
    
    jobs.add(launch {
        simulateEntryTransfer(cacheSystem, 1)
    })
    
    jobs.add(launch {
        simulateEntrySwap(cacheSystem, 1)
    })
    
    jobs.add(launch {
        monitorCacheSystem(cacheSystem, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val entries = cacheSystem.getAllEntries()
    val regions = cacheSystem.getAllRegions()
    
    println("\n=== Final Entry Status ===")
    entries.forEach { entry ->
        println("  ${entry.entryId} (${entry.key}): Cached=${entry.cached}, Locked=${entry.locked}")
    }
    
    println("\n=== Final Region Status ===")
    regions.forEach { region ->
        println("  ${region.regionId} (${region.regionName}): Active=${region.active}, Locked=${region.locked}")
    }
    
    val cached = entries.count { it.cached }
    val locked = entries.count { it.locked }
    val active = regions.count { it.active }
    val lockedRegions = regions.count { it.locked }
    
    println("\nCached Entries: $cached/${entries.size}")
    println("Locked Entries: $locked/${entries.size}")
    println("Active Regions: $active/${regions.size}")
    println("Locked Regions: $lockedRegions/${regions.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - cacheEntry(): entryPoolMutex -> entry.mutex")
    println("  - evictEntry(): entry.mutex -> entryPoolMutex")
    println("  - activateRegion(): regionPoolMutex -> region.mutex")
    println("  - deactivateRegion(): region.mutex -> regionPoolMutex")
    println("  - cacheEntryWithRegion(): entry.mutex -> region.mutex")
    println("  - transferEntry(): entry1.mutex -> entry2.mutex")
    println("  - swapEntries(): entry1.mutex -> entry2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}