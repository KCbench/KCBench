import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class ContextAwareService {
    private var requestCount = 0
    private var successCount = 0
    private var failureCount = 0
    
    fun makeRequest(context: CoroutineContext, url: String) {
        CoroutineScope(context).launch {
            try {
                val result = performRequest(url)
                if (result) {
                    successCount++
                    println("Request to $url succeeded (Success: $successCount)")
                } else {
                    failureCount++
                    println("Request to $url failed (Failure: $failureCount)")
                }
            } catch (e: CancellationException) {
                println("Request to $url cancelled: ${e.message}")
            } catch (e: Exception) {
                failureCount++
                println("Request to $url error: ${e.message} (Failure: $failureCount)")
            }
            requestCount++
        }
    }
    
    fun makeMultipleRequests(context: CoroutineContext, urls: List<String>) {
        urls.forEach { url ->
            makeRequest(context, url)
        }
    }
    
    private suspend fun performRequest(url: String): Boolean {
        delay(Random.nextLong(200, 500))
        return Random.nextBoolean()
    }
    
    fun getRequestCount() = requestCount
    fun getSuccessCount() = successCount
    fun getFailureCount() = failureCount
}

class ContextDataProcessor {
    private var processedCount = 0
    private var errorCount = 0
    
    fun processData(context: CoroutineContext, dataId: String) {
        CoroutineScope(context).launch {
            try {
                val result = performProcessing(dataId)
                if (result) {
                    processedCount++
                    println("Processed $dataId (Total: $processedCount)")
                } else {
                    errorCount++
                    println("Error processing $dataId (Errors: $errorCount)")
                }
            } catch (e: CancellationException) {
                println("Processing $dataId cancelled: ${e.message}")
            } catch (e: Exception) {
                errorCount++
                println("Processing $dataId error: ${e.message} (Errors: $errorCount)")
            }
        }
    }
    
    fun processMultipleData(context: CoroutineContext, dataIds: List<String>) {
        dataIds.forEach { dataId ->
            processData(context, dataId)
        }
    }
    
    private suspend fun performProcessing(dataId: String): Boolean {
        delay(Random.nextLong(300, 800))
        return Random.nextBoolean()
    }
    
    fun getProcessedCount() = processedCount
    fun getErrorCount() = errorCount
}

class ContextTaskExecutor {
    private var taskCount = 0
    private var completedCount = 0
    
    fun executeTask(context: CoroutineContext, taskName: String) {
        CoroutineScope(context).launch {
            try {
                performTask(taskName)
                completedCount++
                println("Task $taskName completed (Completed: $completedCount)")
            } catch (e: CancellationException) {
                println("Task $taskName cancelled: ${e.message}")
            } catch (e: Exception) {
                println("Task $taskName error: ${e.message}")
            }
            taskCount++
        }
    }
    
    fun executeMultipleTasks(context: CoroutineContext, taskNames: List<String>) {
        taskNames.forEach { taskName ->
            executeTask(context, taskName)
        }
    }
    
    private suspend fun performTask(taskName: String) {
        delay(Random.nextLong(400, 1000))
        println("Executing task: $taskName")
    }
    
    fun getTaskCount() = taskCount
    fun getCompletedCount() = completedCount
}

class ContextEventHandler {
    private var eventCount = 0
    private var handledCount = 0
    
    fun handleEvent(context: CoroutineContext, eventId: String) {
        CoroutineScope(context).launch {
            try {
                val handled = performEventHandling(eventId)
                if (handled) {
                    handledCount++
                    println("Event $eventId handled (Handled: $handledCount)")
                } else {
                    println("Event $eventId not handled")
                }
            } catch (e: CancellationException) {
                println("Event $eventId handling cancelled: ${e.message}")
            } catch (e: Exception) {
                println("Event $eventId handling error: ${e.message}")
            }
            eventCount++
        }
    }
    
    fun handleMultipleEvents(context: CoroutineContext, eventIds: List<String>) {
        eventIds.forEach { eventId ->
            handleEvent(context, eventId)
        }
    }
    
    private suspend fun performEventHandling(eventId: String): Boolean {
        delay(Random.nextLong(200, 600))
        return Random.nextBoolean()
    }
    
    fun getEventCount() = eventCount
    fun getHandledCount() = handledCount
}

class ContextCacheManager {
    private var cacheSize = 0
    private var hitCount = 0
    private var missCount = 0
    
    fun preloadCache(context: CoroutineContext, keys: List<String>) {
        keys.forEach { key ->
            CoroutineScope(context).launch {
                try {
                    preloadKey(key)
                    cacheSize++
                    println("Preloaded: $key (Cache size: $cacheSize)")
                } catch (e: CancellationException) {
                    println("Cache preload cancelled for $key: ${e.message}")
                } catch (e: Exception) {
                    println("Cache preload error for $key: ${e.message}")
                }
            }
        }
    }
    
    private suspend fun preloadKey(key: String) {
        delay(Random.nextLong(150, 450))
        println("Preloading: $key")
    }
    
    fun get(key: String): Boolean {
        return if (Random.nextBoolean()) {
            hitCount++
            println("Cache hit for $key (Hits: $hitCount)")
            true
        } else {
            missCount++
            println("Cache miss for $key (Misses: $missCount)")
            false
        }
    }
    
    fun getCacheSize() = cacheSize
    fun getHitCount() = hitCount
    fun getMissCount() = missCount
}

suspend fun simulateContextAwareService(
    service: ContextAwareService,
    context: CoroutineContext
) {
    val urls = listOf(
        "https://api.example.com/users",
        "https://api.example.com/posts",
        "https://api.example.com/comments",
        "https://api.example.com/albums",
        "https://api.example.com/photos"
    )
    
    println("Making requests with context...")
    service.makeMultipleRequests(context, urls)
    
    delay(3000)
    
    println("\nContext Aware Service Summary:")
    println("  Total requests: ${service.getRequestCount()}")
    println("  Success: ${service.getSuccessCount()}")
    println("  Failure: ${service.getFailureCount()}")
}

suspend fun simulateContextDataProcessor(
    processor: ContextDataProcessor,
    context: CoroutineContext
) {
    val dataIds = listOf(
        "Data1", "Data2", "Data3", "Data4", "Data5"
    )
    
    println("Processing data with context...")
    processor.processMultipleData(context, dataIds)
    
    delay(3000)
    
    println("\nContext Data Processor Summary:")
    println("  Processed: ${processor.getProcessedCount()}")
    println("  Errors: ${processor.getErrorCount()}")
}

suspend fun simulateContextTaskExecutor(
    executor: ContextTaskExecutor,
    context: CoroutineContext
) {
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    println("Executing tasks with context...")
    executor.executeMultipleTasks(context, taskNames)
    
    delay(4000)
    
    println("\nContext Task Executor Summary:")
    println("  Total tasks: ${executor.getTaskCount()}")
    println("  Completed: ${executor.getCompletedCount()}")
}

suspend fun simulateContextEventHandler(
    handler: ContextEventHandler,
    context: CoroutineContext
) {
    val eventIds = listOf(
        "EVT001", "EVT002", "EVT003", "EVT004", "EVT005"
    )
    
    println("Handling events with context...")
    handler.handleMultipleEvents(context, eventIds)
    
    delay(2000)
    
    println("\nContext Event Handler Summary:")
    println("  Total events: ${handler.getEventCount()}")
    println("  Handled: ${handler.getHandledCount()}")
}

suspend fun simulateContextCacheManager(
    manager: ContextCacheManager,
    context: CoroutineContext
) {
    val keys = listOf(
        "key1", "key2", "key3", "key4", "key5"
    )
    
    println("Preloading cache with context...")
    manager.preloadCache(context, keys)
    
    delay(2000)
    
    println("\nContext Cache Manager Summary:")
    println("  Cache size: ${manager.getCacheSize()}")
    println("  Hits: ${manager.getHitCount()}")
    println("  Misses: ${manager.getMissCount()}")
}

fun main() = runBlocking {
    println("Starting CoroutineContext Passing Simulation...")
    println()
    
    val service = ContextAwareService()
    val processor = ContextDataProcessor()
    val executor = ContextTaskExecutor()
    val handler = ContextEventHandler()
    val manager = ContextCacheManager()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateContextAwareService(service, this.coroutineContext)
    })
    
    jobs.add(launch {
        simulateContextDataProcessor(processor, this.coroutineContext)
    })
    
    jobs.add(launch {
        simulateContextTaskExecutor(executor, this.coroutineContext)
    })
    
    jobs.add(launch {
        simulateContextEventHandler(handler, this.coroutineContext)
    })
    
    jobs.add(launch {
        simulateContextCacheManager(manager, this.coroutineContext)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Context Aware Service:")
    println("  Total requests: ${service.getRequestCount()}")
    println("  Success: ${service.getSuccessCount()}")
    println("  Failure: ${service.getFailureCount()}")
    
    println("\nContext Data Processor:")
    println("  Processed: ${processor.getProcessedCount()}")
    println("  Errors: ${processor.getErrorCount()}")
    
    println("\nContext Task Executor:")
    println("  Total tasks: ${executor.getTaskCount()}")
    println("  Completed: ${executor.getCompletedCount()}")
    
    println("\nContext Event Handler:")
    println("  Total events: ${handler.getEventCount()}")
    println("  Handled: ${handler.getHandledCount()}")
    
    println("\nContext Cache Manager:")
    println("  Cache size: ${manager.getCacheSize()}")
    println("  Hits: ${manager.getHitCount()}")
    println("  Misses: ${manager.getMissCount()}")
    
    println("\n⚠️  CoroutineContext Passing Warning:")
    println("  The code creates new CoroutineScope from passed CoroutineContext:")
    println("  - ContextAwareService.makeRequest(context, url)")
    println("  - ContextDataProcessor.processData(context, dataId)")
    println("  - ContextTaskExecutor.executeTask(context, taskName)")
    println("  - ContextEventHandler.handleEvent(context, eventId)")
    println("  - ContextCacheManager.preloadCache(context, keys)")
    println("  Creating new CoroutineScope from CoroutineContext breaks structured concurrency,")
    println("  as the new scope is not a child of the parent scope and won't be cancelled properly.")
    println("  Fix: Pass CoroutineScope instead of CoroutineContext, or use withContext.")
}