import kotlinx.coroutines.*
import kotlin.random.Random

data class DataRecord(
    val recordId: String,
    val key: String,
    var value: String,
    var version: Int,
    var lastModified: Long
)

class DataConsistencyManager {
    private val records = mutableMapOf<String, DataRecord>()
    private var totalUpdates = 0
    private var conflictCount = 0
    
    init {
        initializeRecords()
    }
    
    private fun initializeRecords() {
        val keys = listOf(
            "user_profile", "settings", "preferences",
            "cache_data", "session_info", "auth_token"
        )
        
        keys.forEach { key ->
            val record = DataRecord(
                recordId = "REC_${Random.nextInt(1000, 9999)}",
                key = key,
                value = "initial_value_${Random.nextInt(100, 999)}",
                version = 1,
                lastModified = System.currentTimeMillis()
            )
            records[key] = record
        }
    }
    
    suspend fun readRecord(key: String): DataRecord? {
        val record = records[key]
        
        if (record != null) {
            val currentVersion = record.version
            delay(Random.nextLong(1, 5))
            
            val currentValue = record.value
            delay(Random.nextLong(1, 5))
            
            return record.copy(
                version = currentVersion,
                value = currentValue
            )
        }
        
        return null
    }
    
    suspend fun updateRecord(
        key: String,
        newValue: String,
        expectedVersion: Int
    ): Boolean {
        val record = records[key] ?: return false
        
        if (record.version == expectedVersion) {
            delay(Random.nextLong(1, 10))
            
            record.value = newValue
            record.version = expectedVersion + 1
            record.lastModified = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            val currentTotal = totalUpdates
            delay(Random.nextLong(1, 5))
            totalUpdates = currentTotal + 1
            
            return true
        }
        
        val currentConflicts = conflictCount
        delay(Random.nextLong(1, 5))
        conflictCount = currentConflicts + 1
        
        return false
    }
    
    suspend fun conditionalUpdate(
        key: String,
        expectedValue: String,
        newValue: String
    ): Boolean {
        val record = records[key] ?: return false
        
        if (record.value == expectedValue) {
            delay(Random.nextLong(1, 10))
            
            record.value = newValue
            record.version = record.version + 1
            record.lastModified = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun compareAndSwap(
        key: String,
        expectedVersion: Int,
        newValue: String
    ): Boolean {
        val record = records[key] ?: return false
        
        if (record.version == expectedVersion) {
            delay(Random.nextLong(1, 10))
            
            record.value = newValue
            record.version = expectedVersion + 1
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun batchUpdate(
        updates: List<Triple<String, Int, String>>
    ): Int {
        var successful = 0
        
        updates.forEach { (key, expectedVersion, newValue) ->
            if (updateRecord(key, newValue, expectedVersion)) {
                successful++
            }
        }
        
        return successful
    }
    
    fun getAllRecords() = records.values.toList()
    
    fun getStatistics(): Pair<Int, Int> {
        return Pair(totalUpdates, conflictCount)
    }
}

data class Triple<A, B, C>(
    val first: A,
    val second: B,
    val third: C
)

class DataUpdater(
    private val manager: DataConsistencyManager,
    private val updaterName: String
) {
    suspend fun updateRandomRecord() {
        val records = manager.getAllRecords()
        val record = records.random()
        
        val currentRecord = manager.readRecord(record.key)
        
        if (currentRecord != null) {
            val newValue = "updated_by_${updaterName}_${Random.nextInt(1000, 9999)}"
            
            manager.updateRecord(
                record.key,
                newValue,
                currentRecord.version
            )
        }
    }
    
    suspend fun conditionalUpdateRecord() {
        val records = manager.getAllRecords()
        val record = records.random()
        
        val expectedValue = "initial_value_${Random.nextInt(100, 999)}"
        val newValue = "updated_by_${updaterName}_${Random.nextInt(1000, 9999)}"
        
        manager.conditionalUpdate(
            record.key,
            expectedValue,
            newValue
        )
    }
    
    suspend fun compareAndSwapRecord() {
        val records = manager.getAllRecords()
        val record = records.random()
        
        val currentRecord = manager.readRecord(record.key)
        
        if (currentRecord != null) {
            val newValue = "cas_updated_by_${updaterName}_${Random.nextInt(1000, 9999)}"
            
            manager.compareAndSwap(
                record.key,
                currentRecord.version,
                newValue
            )
        }
    }
}

suspend fun simulateConcurrentUpdates(
    updater: DataUpdater,
    updaterId: Int
) {
    repeat(20) { attempt ->
        when (Random.nextInt(3)) {
            0 -> updater.updateRandomRecord()
            1 -> updater.conditionalUpdateRecord()
            2 -> updater.compareAndSwapRecord()
        }
        
        delay(Random.nextLong(10, 50))
    }
}

suspend fun simulateBatchUpdates(
    manager: DataConsistencyManager
) {
    repeat(10) { attempt ->
        val records = manager.getAllRecords()
        val selectedRecords = records.shuffled().take(3)
        
        val updates = selectedRecords.map { record ->
            Triple(
                record.key,
                record.version,
                "batch_updated_${Random.nextInt(1000, 9999)}"
            )
        }
        
        manager.batchUpdate(updates)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateVersionConflicts(
    manager: DataConsistencyManager
) {
    repeat(15) { attempt ->
        val records = manager.getAllRecords()
        val record = records.random()
        
        val currentRecord = manager.readRecord(record.key)
        
        if (currentRecord != null) {
            val newValue = "conflict_update_${Random.nextInt(1000, 9999)}"
            
            manager.updateRecord(
                record.key,
                newValue,
                currentRecord.version
            )
        }
        
        delay(Random.nextLong(20, 80))
    }
}

fun main() = runBlocking {
    val manager = DataConsistencyManager()
    
    println("Starting Data Consistency Simulation...")
    println("Initial Records:")
    manager.getAllRecords().forEach { record ->
        println("  ${record.key}: Version=${record.version}, Value=${record.value}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val updaters = listOf(
        DataUpdater(manager, "Alice"),
        DataUpdater(manager, "Bob"),
        DataUpdater(manager, "Charlie"),
        DataUpdater(manager, "David"),
        DataUpdater(manager, "Eve")
    )
    
    updaters.forEachIndexed { index, updater ->
        jobs.add(launch {
            simulateConcurrentUpdates(updater, index)
        })
    }
    
    jobs.add(launch {
        simulateBatchUpdates(manager)
    })
    
    jobs.add(launch {
        simulateVersionConflicts(manager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (totalUpdates, conflictCount) = manager.getStatistics()
    
    println("\n=== Data Consistency Statistics ===")
    println("Total Updates: $totalUpdates")
    println("Conflicts: $conflictCount")
    
    println("\n=== Final Records ===")
    manager.getAllRecords().forEach { record ->
        println(
            "  ${record.key}: Version=${record.version}, " +
            "Value=${record.value}, " +
            "Modified=${record.lastModified}"
        )
    }
    
    val versionInconsistencies = manager.getAllRecords()
        .filter { it.version < 10 }
    
    if (versionInconsistencies.isNotEmpty()) {
        println("\n⚠️  Records with low versions:")
        versionInconsistencies.forEach { record ->
            println("  ${record.key}: Version=${record.version}")
        }
    }
    
    val conflictRate = if (totalUpdates > 0) {
        (conflictCount.toDouble() / totalUpdates * 100)
    } else {
        0.0
    }
    
    println("\nConflict Rate: ${"%.2f".format(conflictRate)}%")
}