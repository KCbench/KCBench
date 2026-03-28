import kotlinx.coroutines.*
import kotlin.random.Random

class ExternalService {
    private var requestCount = 0
    private var successCount = 0
    private var failureCount = 0
    
    fun makeRequest(url: String, scope: CoroutineScope) {
        scope.launch {
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
    
    fun makeMultipleRequests(urls: List<String>, scope: CoroutineScope) {
        urls.forEach { url ->
            makeRequest(url, scope)
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

class DataFetcher {
    private var fetchedCount = 0
    private var errorCount = 0
    
    fun fetchData(source: String, scope: CoroutineScope) {
        scope.launch {
            try {
                val data = performFetch(source)
                if (data != null) {
                    fetchedCount++
                    println("Fetched data from $source (Total: $fetchedCount)")
                } else {
                    errorCount++
                    println("Failed to fetch data from $source (Errors: $errorCount)")
                }
            } catch (e: CancellationException) {
                println("Fetch from $source cancelled: ${e.message}")
            } catch (e: Exception) {
                errorCount++
                println("Fetch from $source error: ${e.message} (Errors: $errorCount)")
            }
        }
    }
    
    fun fetchFromMultipleSources(sources: List<String>, scope: CoroutineScope) {
        sources.forEach { source ->
            fetchData(source, scope)
        }
    }
    
    private suspend fun performFetch(source: String): String? {
        delay(Random.nextLong(300, 800))
        return if (Random.nextBoolean()) {
            "Data from $source"
        } else {
            null
        }
    }
    
    fun getFetchedCount() = fetchedCount
    fun getErrorCount() = errorCount
}

class BackgroundWorker {
    private var taskCount = 0
    private var completedCount = 0
    
    fun executeTask(taskName: String, scope: CoroutineScope) {
        scope.launch {
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
    
    fun executeMultipleTasks(taskNames: List<String>, scope: CoroutineScope) {
        taskNames.forEach { taskName ->
            executeTask(taskName, scope)
        }
    }
    
    private suspend fun performTask(taskName: String) {
        delay(Random.nextLong(400, 1000))
        println("Executing task: $taskName")
    }
    
    fun getTaskCount() = taskCount
    fun getCompletedCount() = completedCount
}

class EventProcessor {
    private var processedCount = 0
    private var droppedCount = 0
    
    fun processEvent(eventId: String, scope: CoroutineScope) {
        scope.launch {
            try {
                val processed = performProcessing(eventId)
                if (processed) {
                    processedCount++
                    println("Event $eventId processed (Processed: $processedCount)")
                } else {
                    droppedCount++
                    println("Event $eventId dropped (Dropped: $droppedCount)")
                }
            } catch (e: CancellationException) {
                println("Event $eventId processing cancelled: ${e.message}")
            } catch (e: Exception) {
                droppedCount++
                println("Event $eventId processing error: ${e.message} (Dropped: $droppedCount)")
            }
        }
    }
    
    fun processMultipleEvents(eventIds: List<String>, scope: CoroutineScope) {
        eventIds.forEach { eventId ->
            processEvent(eventId, scope)
        }
    }
    
    private suspend fun performProcessing(eventId: String): Boolean {
        delay(Random.nextLong(200, 600))
        return Random.nextBoolean()
    }
    
    fun getProcessedCount() = processedCount
    fun getDroppedCount() = droppedCount
}

class CacheUpdater {
    private var updateCount = 0
    private var errorCount = 0
    
    fun updateCache(key: String, value: String, scope: CoroutineScope) {
        scope.launch {
            try {
                performUpdate(key, value)
                updateCount++
                println("Cache updated for $key (Updates: $updateCount)")
            } catch (e: CancellationException) {
                println("Cache update for $key cancelled: ${e.message}")
            } catch (e: Exception) {
                errorCount++
                println("Cache update for $key error: ${e.message} (Errors: $errorCount)")
            }
        }
    }
    
    fun updateMultipleCacheEntries(entries: Map<String, String>, scope: CoroutineScope) {
        entries.forEach { (key, value) ->
            updateCache(key, value, scope)
        }
    }
    
    private suspend fun performUpdate(key: String, value: String) {
        delay(Random.nextLong(150, 450))
        println("Updating cache: $key = $value")
    }
    
    fun getUpdateCount() = updateCount
    fun getErrorCount() = errorCount
}

suspend fun simulateExternalService(
    service: ExternalService,
    scope: CoroutineScope
) {
    val urls = listOf(
        "https://api.example.com/users",
        "https://api.example.com/posts",
        "https://api.example.com/comments",
        "https://api.example.com/albums",
        "https://api.example.com/photos"
    )
    
    service.makeMultipleRequests(urls, scope)
    
    delay(3000)
    
    println("\nExternal Service Summary:")
    println("  Total requests: ${service.getRequestCount()}")
    println("  Success: ${service.getSuccessCount()}")
    println("  Failure: ${service.getFailureCount()}")
}

suspend fun simulateDataFetcher(
    fetcher: DataFetcher,
    scope: CoroutineScope
) {
    val sources = listOf(
        "Database", "API", "Cache", "LocalStorage", "RemoteServer"
    )
    
    fetcher.fetchFromMultipleSources(sources, scope)
    
    delay(3000)
    
    println("\nData Fetcher Summary:")
    println("  Fetched: ${fetcher.getFetchedCount()}")
    println("  Errors: ${fetcher.getErrorCount()}")
}

suspend fun simulateBackgroundWorker(
    worker: BackgroundWorker,
    scope: CoroutineScope
) {
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    worker.executeMultipleTasks(taskNames, scope)
    
    delay(4000)
    
    println("\nBackground Worker Summary:")
    println("  Total tasks: ${worker.getTaskCount()}")
    println("  Completed: ${worker.getCompletedCount()}")
}

suspend fun simulateEventProcessor(
    processor: EventProcessor,
    scope: CoroutineScope
) {
    val eventIds = listOf(
        "EVT001", "EVT002", "EVT003", "EVT004", "EVT005"
    )
    
    processor.processMultipleEvents(eventIds, scope)
    
    delay(2000)
    
    println("\nEvent Processor Summary:")
    println("  Processed: ${processor.getProcessedCount()}")
    println("  Dropped: ${processor.getDroppedCount()}")
}

suspend fun simulateCacheUpdater(
    updater: CacheUpdater,
    scope: CoroutineScope
) {
    val entries = mapOf(
        "key1" to "value1",
        "key2" to "value2",
        "key3" to "value3",
        "key4" to "value4",
        "key5" to "value5"
    )
    
    updater.updateMultipleCacheEntries(entries, scope)
    
    delay(2000)
    
    println("\nCache Updater Summary:")
    println("  Updates: ${updater.getUpdateCount()}")
    println("  Errors: ${updater.getErrorCount()}")
}

fun main() = runBlocking {
    println("Starting External Scope Simulation...")
    println()
    
    val service = ExternalService()
    val fetcher = DataFetcher()
    val worker = BackgroundWorker()
    val processor = EventProcessor()
    val updater = CacheUpdater()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateExternalService(service, this)
    })
    
    jobs.add(launch {
        simulateDataFetcher(fetcher, this)
    })
    
    jobs.add(launch {
        simulateBackgroundWorker(worker, this)
    })
    
    jobs.add(launch {
        simulateEventProcessor(processor, this)
    })
    
    jobs.add(launch {
        simulateCacheUpdater(updater, this)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("External Service:")
    println("  Total requests: ${service.getRequestCount()}")
    println("  Success: ${service.getSuccessCount()}")
    println("  Failure: ${service.getFailureCount()}")
    
    println("\nData Fetcher:")
    println("  Fetched: ${fetcher.getFetchedCount()}")
    println("  Errors: ${fetcher.getErrorCount()}")
    
    println("\nBackground Worker:")
    println("  Total tasks: ${worker.getTaskCount()}")
    println("  Completed: ${worker.getCompletedCount()}")
    
    println("\nEvent Processor:")
    println("  Processed: ${processor.getProcessedCount()}")
    println("  Dropped: ${processor.getDroppedCount()}")
    
    println("\nCache Updater:")
    println("  Updates: ${updater.getUpdateCount()}")
    println("  Errors: ${updater.getErrorCount()}")
    
    println("\n⚠️  Scope Passing Bug Warning:")
    println("  The code accepts CoroutineScope as parameter and launches coroutines in it:")
    println("  - ExternalService.makeRequest(url, scope)")
    println("  - DataFetcher.fetchData(source, scope)")
    println("  - BackgroundWorker.executeTask(taskName, scope)")
    println("  - EventProcessor.processEvent(eventId, scope)")
    println("  - CacheUpdater.updateCache(key, value, scope)")
    println("  This can lead to issues if the provided scope is cancelled or destroyed,")
    println("  causing coroutines to be cancelled unexpectedly or continue running after they should stop.")
    println("  Fix: Use structured concurrency and avoid passing CoroutineScope to external functions.")
}