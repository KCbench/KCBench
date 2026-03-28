import kotlinx.coroutines.*
import kotlin.random.Random

class DataProcessor {
    private var processedCount = 0
    private var errorCount = 0
    private val activeJobs = mutableListOf<Job>()
    
    fun processDataInGlobalScope(data: List<Int>) {
        data.forEach { item ->
            GlobalScope.launch {
                processData(item)
            }
        }
    }
    
    fun processDataInProvidedScope(data: List<Int>, scope: CoroutineScope) {
        data.forEach { item ->
            scope.launch {
                processData(item)
            }
        }
    }
    
    private suspend fun processData(item: Int) {
        delay(Random.nextLong(50, 200))
        
        if (Random.nextBoolean()) {
            processedCount++
            println("Processed: $item (Total: $processedCount)")
        } else {
            errorCount++
            println("Error processing: $item (Errors: $errorCount)")
        }
    }
    
    fun getProcessedCount() = processedCount
    fun getErrorCount() = errorCount
    fun getActiveJobsCount() = activeJobs.size
}

class BackgroundTaskManager {
    private val tasks = mutableListOf<BackgroundTask>()
    
    fun startTask(taskName: String) {
        val task = BackgroundTask(taskName)
        tasks.add(task)
        task.start()
    }
    
    fun startAllTasks(taskNames: List<String>) {
        taskNames.forEach { taskName ->
            startTask(taskName)
        }
    }
    
    fun getActiveTasks() = tasks.filter { it.isActive }
    fun getCompletedTasks() = tasks.filter { it.isCompleted }
}

class BackgroundTask(
    private val taskName: String
) {
    var isActive = false
        private set
    var isCompleted = false
        private set
    private var job: Job? = null
    
    fun start() {
        isActive = true
        
        job = GlobalScope.launch {
            executeTask()
        }
    }
    
    private suspend fun executeTask() {
        repeat(10) { iteration ->
            if (!isActive) {
                println("Task $taskName cancelled at iteration $iteration")
                return
            }
            
            delay(Random.nextLong(100, 300))
            println("Task $taskName: Iteration $iteration")
        }
        
        isCompleted = true
        isActive = false
        println("Task $taskName completed")
    }
    
    fun cancel() {
        isActive = false
        job?.cancel()
    }
}

class NetworkClient {
    private var requestCount = 0
    private var successCount = 0
    private var failureCount = 0
    
    fun makeRequestInGlobalScope(url: String) {
        GlobalScope.launch {
            try {
                val response = fetchUrl(url)
                if (response) {
                    successCount++
                    println("Request to $url succeeded (Success: $successCount)")
                } else {
                    failureCount++
                    println("Request to $url failed (Failure: $failureCount)")
                }
            } catch (e: Exception) {
                failureCount++
                println("Request to $url error: ${e.message} (Failure: $failureCount)")
            }
            requestCount++
        }
    }
    
    fun makeMultipleRequests(urls: List<String>) {
        urls.forEach { url ->
            makeRequestInGlobalScope(url)
        }
    }
    
    private suspend fun fetchUrl(url: String): Boolean {
        delay(Random.nextLong(200, 500))
        return Random.nextBoolean()
    }
    
    fun getRequestCount() = requestCount
    fun getSuccessCount() = successCount
    fun getFailureCount() = failureCount
}

class CacheManager {
    private val cache = mutableMapOf<String, String>()
    private var hitCount = 0
    private var missCount = 0
    
    fun preloadCacheInGlobalScope(keys: List<String>) {
        keys.forEach { key ->
            GlobalScope.launch {
                preloadKey(key)
            }
        }
    }
    
    private suspend fun preloadKey(key: String) {
        delay(Random.nextLong(100, 400))
        
        val value = "Value for $key"
        cache[key] = value
        println("Preloaded: $key")
    }
    
    fun get(key: String): String? {
        return if (cache.containsKey(key)) {
            hitCount++
            println("Cache hit for $key (Hits: $hitCount)")
            cache[key]
        } else {
            missCount++
            println("Cache miss for $key (Misses: $missCount)")
            null
        }
    }
    
    fun getCacheSize() = cache.size
    fun getHitCount() = hitCount
    fun getMissCount() = missCount
}

suspend fun simulateDataProcessing(
    processor: DataProcessor,
    scope: CoroutineScope
) {
    val data = (1..20).toList()
    
    processor.processDataInProvidedScope(data, scope)
    
    delay(1000)
    
    println("Data Processing Summary:")
    println("  Processed: ${processor.getProcessedCount()}")
    println("  Errors: ${processor.getErrorCount()}")
}

suspend fun simulateBackgroundTasks(
    taskManager: BackgroundTaskManager
) {
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    taskManager.startAllTasks(taskNames)
    
    delay(2000)
    
    println("\nBackground Tasks Summary:")
    println("  Active: ${taskManager.getActiveTasks().size}")
    println("  Completed: ${taskManager.getCompletedTasks().size}")
}

suspend fun simulateNetworkRequests(
    client: NetworkClient
) {
    val urls = listOf(
        "https://api.example.com/users",
        "https://api.example.com/posts",
        "https://api.example.com/comments",
        "https://api.example.com/albums",
        "https://api.example.com/photos"
    )
    
    client.makeMultipleRequests(urls)
    
    delay(3000)
    
    println("\nNetwork Requests Summary:")
    println("  Total: ${client.getRequestCount()}")
    println("  Success: ${client.getSuccessCount()}")
    println("  Failure: ${client.getFailureCount()}")
}

suspend fun simulateCachePreloading(
    cacheManager: CacheManager
) {
    val keys = listOf(
        "user:1", "user:2", "user:3", "user:4", "user:5",
        "product:1", "product:2", "product:3", "product:4", "product:5"
    )
    
    cacheManager.preloadCacheInGlobalScope(keys)
    
    delay(2000)
    
    println("\nCache Preloading Summary:")
    println("  Cache Size: ${cacheManager.getCacheSize()}")
    println("  Hits: ${cacheManager.getHitCount()}")
    println("  Misses: ${cacheManager.getMissCount()}")
}

fun main() = runBlocking {
    println("Starting GlobalScope Usage Simulation...")
    println()
    
    val processor = DataProcessor()
    val taskManager = BackgroundTaskManager()
    val client = NetworkClient()
    val cacheManager = CacheManager()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateDataProcessing(processor, this)
    })
    
    jobs.add(launch {
        simulateBackgroundTasks(taskManager)
    })
    
    jobs.add(launch {
        simulateNetworkRequests(client)
    })
    
    jobs.add(launch {
        simulateCachePreloading(cacheManager)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Data Processor:")
    println("  Processed: ${processor.getProcessedCount()}")
    println("  Errors: ${processor.getErrorCount()}")
    
    println("\nBackground Tasks:")
    println("  Active: ${taskManager.getActiveTasks().size}")
    println("  Completed: ${taskManager.getCompletedTasks().size}")
    
    println("\nNetwork Client:")
    println("  Total Requests: ${client.getRequestCount()}")
    println("  Success: ${client.getSuccessCount()}")
    println("  Failure: ${client.getFailureCount()}")
    
    println("\nCache Manager:")
    println("  Cache Size: ${cacheManager.getCacheSize()}")
    println("  Hits: ${cacheManager.getHitCount()}")
    println("  Misses: ${cacheManager.getMissCount()}")
    
    println("\n⚠️  Scope Passing Bug Warning:")
    println("  The code uses GlobalScope in multiple places:")
    println("  - DataProcessor.processDataInGlobalScope()")
    println("  - BackgroundTask.start()")
    println("  - NetworkClient.makeRequestInGlobalScope()")
    println("  - CacheManager.preloadCacheInGlobalScope()")
    println("  These coroutines will continue running even after the main scope is cancelled,")
    println("  leading to memory leaks and potential resource leaks.")
    println("  Fix: Use the provided scope parameter or lifecycle-aware scopes like viewModelScope.")
}