import kotlinx.coroutines.*
import kotlin.random.Random

class CancellableTask {
    private var job: Job? = null
    private var isActive = false
    private var isCancelled = false
    private var completed = false
    
    fun start() {
        if (isActive) {
            return
        }
        
        isActive = true
        job = GlobalScope.launch {
            executeTask()
        }
        println("CancellableTask started")
    }
    
    private suspend fun executeTask() {
        repeat(20) { iteration ->
            if (!isActive) {
                println("CancellableTask cancelled at iteration $iteration")
                return
            }
            
            delay(Random.nextLong(100, 300))
            println("CancellableTask: Iteration $iteration")
        }
        
        completed = true
        isActive = false
        println("CancellableTask: Completed")
    }
    
    fun cancel() {
        isActive = false
        isCancelled = true
        job?.cancel()
        println("CancellableTask cancelled")
    }
    
    fun isActive() = isActive
    fun isCancelled() = isCancelled
    fun isCompleted() = completed
}

class LongRunningOperation {
    private var job: Job? = null
    private var progress = 0
    private var isCancelled = false
    
    fun start() {
        if (job != null && job!!.isActive) {
            return
        }
        
        progress = 0
        isCancelled = false
        job = GlobalScope.launch {
            performOperation()
        }
        println("LongRunningOperation started")
    }
    
    private suspend fun performOperation() {
        val totalSteps = 100
        
        while (progress < totalSteps && !isCancelled) {
            delay(Random.nextLong(50, 150))
            progress++
            
            if (progress % 10 == 0) {
                println("LongRunningOperation: Progress $progress%")
            }
        }
        
        if (!isCancelled) {
            println("LongRunningOperation: Completed")
        } else {
            println("LongRunningOperation: Cancelled at $progress%")
        }
    }
    
    fun cancel() {
        isCancelled = true
        job?.cancel()
        println("LongRunningOperation cancelled")
    }
    
    fun getProgress() = progress
    fun isCancelled() = isCancelled
}

class PeriodicWorker {
    private var job: Job? = null
    private var isRunning = false
    private var executionCount = 0
    
    fun start(intervalMs: Long) {
        if (isRunning) {
            return
        }
        
        isRunning = true
        executionCount = 0
        job = GlobalScope.launch {
            while (isRunning) {
                executeWork()
                delay(intervalMs)
            }
        }
        println("PeriodicWorker started")
    }
    
    private suspend fun executeWork() {
        executionCount++
        println("PeriodicWorker: Execution #$executionCount")
        delay(Random.nextLong(100, 200))
    }
    
    fun stop() {
        isRunning = false
        job?.cancel()
        println("PeriodicWorker stopped")
    }
    
    fun isRunning() = isRunning
    fun getExecutionCount() = executionCount
}

class AsyncDataLoader {
    private var job: Job? = null
    private var loaded = false
    private var isCancelled = false
    
    fun loadData(source: String) {
        if (job != null && job!!.isActive) {
            return
        }
        
        loaded = false
        isCancelled = false
        job = GlobalScope.launch {
            performLoad(source)
        }
        println("AsyncDataLoader started loading from $source")
    }
    
    private suspend fun performLoad(source: String) {
        delay(Random.nextLong(500, 1500))
        
        if (!isCancelled) {
            loaded = true
            println("AsyncDataLoader: Loaded data from $source")
        } else {
            println("AsyncDataLoader: Loading cancelled for $source")
        }
    }
    
    fun cancel() {
        isCancelled = true
        job?.cancel()
        println("AsyncDataLoader cancelled")
    }
    
    fun isLoaded() = loaded
    fun isCancelled() = isCancelled
}

class NetworkRequester {
    private var job: Job? = null
    private var requestCount = 0
    private var successCount = 0
    private var isCancelled = false
    
    fun makeRequest(url: String) {
        if (job != null && job!!.isActive) {
            return
        }
        
        isCancelled = false
        job = GlobalScope.launch {
            performRequest(url)
        }
        println("NetworkRequester started request to $url")
    }
    
    private suspend fun performRequest(url: String) {
        delay(Random.nextLong(300, 1000))
        
        if (!isCancelled) {
            requestCount++
            if (Random.nextBoolean()) {
                successCount++
                println("NetworkRequester: Request to $url succeeded")
            } else {
                println("NetworkRequester: Request to $url failed")
            }
        } else {
            println("NetworkRequester: Request to $url cancelled")
        }
    }
    
    fun cancel() {
        isCancelled = true
        job?.cancel()
        println("NetworkRequester cancelled")
    }
    
    fun getRequestCount() = requestCount
    fun getSuccessCount() = successCount
    fun isCancelled() = isCancelled
}

suspend fun simulateCancellableTask(
    task: CancellableTask
) {
    println("Starting cancellable task...")
    task.start()
    
    delay(2000)
    
    println("Cancelling task...")
    task.cancel()
    
    delay(500)
    
    println("\nCancellable Task Summary:")
    println("  Is active: ${task.isActive()}")
    println("  Is cancelled: ${task.isCancelled()}")
    println("  Is completed: ${task.isCompleted()}")
}

suspend fun simulateLongRunningOperation(
    operation: LongRunningOperation
) {
    println("Starting long running operation...")
    operation.start()
    
    delay(3000)
    
    println("Cancelling operation...")
    operation.cancel()
    
    delay(500)
    
    println("\nLong Running Operation Summary:")
    println("  Progress: ${operation.getProgress()}%")
    println("  Is cancelled: ${operation.isCancelled()}")
}

suspend fun simulatePeriodicWorker(
    worker: PeriodicWorker
) {
    println("Starting periodic worker...")
    worker.start(500)
    
    delay(3000)
    
    println("Stopping worker...")
    worker.stop()
    
    delay(500)
    
    println("\nPeriodic Worker Summary:")
    println("  Is running: ${worker.isRunning()}")
    println("  Execution count: ${worker.getExecutionCount()}")
}

suspend fun simulateAsyncDataLoader(
    loader: AsyncDataLoader
) {
    println("Starting async data loader...")
    loader.loadData("Database")
    
    delay(1000)
    
    println("Cancelling data loader...")
    loader.cancel()
    
    delay(500)
    
    println("\nAsync Data Loader Summary:")
    println("  Is loaded: ${loader.isLoaded()}")
    println("  Is cancelled: ${loader.isCancelled()}")
}

suspend fun simulateNetworkRequester(
    requester: NetworkRequester
) {
    println("Starting network requester...")
    requester.makeRequest("https://api.example.com/data")
    
    delay(800)
    
    println("Cancelling network requester...")
    requester.cancel()
    
    delay(500)
    
    println("\nNetwork Requester Summary:")
    println("  Request count: ${requester.getRequestCount()}")
    println("  Success count: ${requester.getSuccessCount()}")
    println("  Is cancelled: ${requester.isCancelled()}")
}

fun main() = runBlocking {
    println("Starting Scope Cancellation Simulation...")
    println()
    
    val task = CancellableTask()
    val operation = LongRunningOperation()
    val worker = PeriodicWorker()
    val loader = AsyncDataLoader()
    val requester = NetworkRequester()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateCancellableTask(task)
    })
    
    jobs.add(launch {
        simulateLongRunningOperation(operation)
    })
    
    jobs.add(launch {
        simulatePeriodicWorker(worker)
    })
    
    jobs.add(launch {
        simulateAsyncDataLoader(loader)
    })
    
    jobs.add(launch {
        simulateNetworkRequester(requester)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Cancellable Task:")
    println("  Is active: ${task.isActive()}")
    println("  Is cancelled: ${task.isCancelled()}")
    println("  Is completed: ${task.isCompleted()}")
    
    println("\nLong Running Operation:")
    println("  Progress: ${operation.getProgress()}%")
    println("  Is cancelled: ${operation.isCancelled()}")
    
    println("\nPeriodic Worker:")
    println("  Is running: ${worker.isRunning()}")
    println("  Execution count: ${worker.getExecutionCount()}")
    
    println("\nAsync Data Loader:")
    println("  Is loaded: ${loader.isLoaded()}")
    println("  Is cancelled: ${loader.isCancelled()}")
    
    println("\nNetwork Requester:")
    println("  Request count: ${requester.getRequestCount()}")
    println("  Success count: ${requester.getSuccessCount()}")
    println("  Is cancelled: ${requester.isCancelled()}")
    
    println("\n⚠️  Scope Cancellation Warning:")
    println("  The code uses GlobalScope to launch coroutines that cannot be properly cancelled:")
    println("  - CancellableTask.start() launches task in GlobalScope")
    println("  - LongRunningOperation.start() launches operation in GlobalScope")
    println("  - PeriodicWorker.start() launches worker in GlobalScope")
    println("  - AsyncDataLoader.loadData() launches loader in GlobalScope")
    println("  - NetworkRequester.makeRequest() launches requester in GlobalScope")
    println("  These coroutines are not children of the parent scope and won't be cancelled")
    println("  when the parent scope is cancelled, leading to resource leaks.")
    println("  Fix: Use structured concurrency with coroutineScope or launch in parent scope.")
}