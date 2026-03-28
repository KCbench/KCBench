import kotlinx.coroutines.*
import kotlin.random.Random

class AsyncOperation {
    private var operationCount = 0
    private var asyncJob: Job? = null
    
    suspend fun performAsyncOperation() = coroutineScope {
        asyncJob?.cancel()
        
        asyncJob = launch {
            val tmp = operationCount
            delay(50)
            operationCount = tmp + 1
            println("Async operation performed (Count: $operationCount)")
        }
    }
    
    suspend fun performMultipleAsyncOperations(times: Int) = coroutineScope {
        repeat(times) {
            performAsyncOperation()
            delay(100)
        }
    }
    
    fun getOperationCount() = operationCount
}

class AsyncTask {
    private var taskCount = 0
    private var asyncJob: Job? = null
    
    suspend fun performAsyncTask() = coroutineScope {
        asyncJob?.cancel()
        
        asyncJob = launch {
            val tmp = taskCount
            delay(50)
            taskCount = tmp + 1
            println("Async task performed (Count: $taskCount)")
        }
    }
    
    suspend fun performMultipleAsyncTasks(times: Int) = coroutineScope {
        repeat(times) {
            performAsyncTask()
            delay(100)
        }
    }
    
    fun getTaskCount() = taskCount
}

class AsyncJob {
    private var jobCount = 0
    private var asyncJob: Job? = null
    
    suspend fun performAsyncJob() = coroutineScope {
        asyncJob?.cancel()
        
        asyncJob = launch {
            val tmp = jobCount
            delay(50)
            jobCount = tmp + 1
            println("Async job performed (Count: $jobCount)")
        }
    }
    
    suspend fun performMultipleAsyncJobs(times: Int) = coroutineScope {
        repeat(times) {
            performAsyncJob()
            delay(100)
        }
    }
    
    fun getJobCount() = jobCount
}

class AsyncWork {
    private var workCount = 0
    private var asyncJob: Job? = null
    
    suspend fun performAsyncWork() = coroutineScope {
        asyncJob?.cancel()
        
        asyncJob = launch {
            val tmp = workCount
            delay(50)
            workCount = tmp + 1
            println("Async work performed (Count: $workCount)")
        }
    }
    
    suspend fun performMultipleAsyncWorks(times: Int) = coroutineScope {
        repeat(times) {
            performAsyncWork()
            delay(100)
        }
    }
    
    fun getWorkCount() = workCount
}

class AsyncAction {
    private var actionCount = 0
    private var asyncJob: Job? = null
    
    suspend fun performAsyncAction() = coroutineScope {
        asyncJob?.cancel()
        
        asyncJob = launch {
            val tmp = actionCount
            delay(50)
            actionCount = tmp + 1
            println("Async action performed (Count: $actionCount)")
        }
    }
    
    suspend fun performMultipleAsyncActions(times: Int) = coroutineScope {
        repeat(times) {
            performAsyncAction()
            delay(100)
        }
    }
    
    fun getActionCount() = actionCount
}

suspend fun simulateAsyncOperation(
    operation: AsyncOperation,
    operationId: Int
) {
    repeat(10) { attempt ->
        operation.performAsyncOperation()
        delay(Random.nextLong(50, 150))
    }
    
    println("Async operation $operationId completed")
}

suspend fun simulateAsyncTask(
    task: AsyncTask,
    taskId: Int
) {
    repeat(10) { attempt ->
        task.performAsyncTask()
        delay(Random.nextLong(50, 150))
    }
    
    println("Async task $taskId completed")
}

suspend fun simulateAsyncJob(
    job: AsyncJob,
    jobId: Int
) {
    repeat(10) { attempt ->
        job.performAsyncJob()
        delay(Random.nextLong(50, 150))
    }
    
    println("Async job $jobId completed")
}

suspend fun simulateAsyncWork(
    work: AsyncWork,
    workId: Int
) {
    repeat(10) { attempt ->
        work.performAsyncWork()
        delay(Random.nextLong(50, 150))
    }
    
    println("Async work $workId completed")
}

suspend fun simulateAsyncAction(
    action: AsyncAction,
    actionId: Int
) {
    repeat(10) { attempt ->
        action.performAsyncAction()
        delay(Random.nextLong(50, 150))
    }
    
    println("Async action $actionId completed")
}

suspend fun monitorAsyncOperations(
    asyncOperation: AsyncOperation,
    asyncTask: AsyncTask,
    asyncJob: AsyncJob,
    asyncWork: AsyncWork,
    asyncAction: AsyncAction,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Operation count: ${asyncOperation.getOperationCount()}")
        println("  Task count: ${asyncTask.getTaskCount()}")
        println("  Job count: ${asyncJob.getJobCount()}")
        println("  Work count: ${asyncWork.getWorkCount()}")
        println("  Action count: ${asyncAction.getActionCount()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Async Operation Simulation...")
    println()
    
    val asyncOperation = AsyncOperation()
    val asyncTask = AsyncTask()
    val asyncJob = AsyncJob()
    val asyncWork = AsyncWork()
    val asyncAction = AsyncAction()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateAsyncOperation(asyncOperation, 1)
    })
    
    jobs.add(launch {
        simulateAsyncOperation(asyncOperation, 2)
    })
    
    jobs.add(launch {
        simulateAsyncTask(asyncTask, 1)
    })
    
    jobs.add(launch {
        simulateAsyncJob(asyncJob, 1)
    })
    
    jobs.add(launch {
        simulateAsyncWork(asyncWork, 1)
    })
    
    jobs.add(launch {
        simulateAsyncAction(asyncAction, 1)
    })
    
    jobs.add(launch {
        monitorAsyncOperations(
            asyncOperation,
            asyncTask,
            asyncJob,
            asyncWork,
            asyncAction,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Async Operation Counts ===")
    println("Operation count: ${asyncOperation.getOperationCount()}")
    println("Task count: ${asyncTask.getTaskCount()}")
    println("Job count: ${asyncJob.getJobCount()}")
    println("Work count: ${asyncWork.getWorkCount()}")
    println("Action count: ${asyncAction.getActionCount()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels async jobs and immediately starts new ones:")
    println("  - AsyncOperation.performAsyncOperation() cancels asyncJob and starts new one")
    println("  - AsyncTask.performAsyncTask() cancels asyncJob and starts new one")
    println("  - AsyncJob.performAsyncJob() cancels asyncJob and starts new one")
    println("  - AsyncWork.performAsyncWork() cancels asyncJob and starts new one")
    println("  - AsyncAction.performAsyncAction() cancels asyncJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}