import kotlinx.coroutines.*
import kotlin.random.Random

class OperationExecutor {
    private var operationCount = 0
    private var operationJob: Job? = null
    
    suspend fun executeOperation() = coroutineScope {
        operationJob?.cancel()
        
        operationJob = launch {
            val tmp = operationCount
            delay(50)
            operationCount = tmp + 1
            println("Operation executed (Count: $operationCount)")
        }
    }
    
    suspend fun executeMultipleOperations(times: Int) = coroutineScope {
        repeat(times) {
            executeOperation()
            delay(100)
        }
    }
    
    fun getOperationCount() = operationCount
}

class TaskExecutor {
    private var taskCount = 0
    private var taskJob: Job? = null
    
    suspend fun executeTask() = coroutineScope {
        taskJob?.cancel()
        
        taskJob = launch {
            val tmp = taskCount
            delay(50)
            taskCount = tmp + 1
            println("Task executed (Count: $taskCount)")
        }
    }
    
    suspend fun executeMultipleTasks(times: Int) = coroutineScope {
        repeat(times) {
            executeTask()
            delay(100)
        }
    }
    
    fun getTaskCount() = taskCount
}

class JobExecutor {
    private var jobCount = 0
    private var jobJob: Job? = null
    
    suspend fun executeJob() = coroutineScope {
        jobJob?.cancel()
        
        jobJob = launch {
            val tmp = jobCount
            delay(50)
            jobCount = tmp + 1
            println("Job executed (Count: $jobCount)")
        }
    }
    
    suspend fun executeMultipleJobs(times: Int) = coroutineScope {
        repeat(times) {
            executeJob()
            delay(100)
        }
    }
    
    fun getJobCount() = jobCount
}

class WorkExecutor {
    private var workCount = 0
    private var workJob: Job? = null
    
    suspend fun executeWork() = coroutineScope {
        workJob?.cancel()
        
        workJob = launch {
            val tmp = workCount
            delay(50)
            workCount = tmp + 1
            println("Work executed (Count: $workCount)")
        }
    }
    
    suspend fun executeMultipleWorks(times: Int) = coroutineScope {
        repeat(times) {
            executeWork()
            delay(100)
        }
    }
    
    fun getWorkCount() = workCount
}

class ActionExecutor {
    private var actionCount = 0
    private var actionJob: Job? = null
    
    suspend fun executeAction() = coroutineScope {
        actionJob?.cancel()
        
        actionJob = launch {
            val tmp = actionCount
            delay(50)
            actionCount = tmp + 1
            println("Action executed (Count: $actionCount)")
        }
    }
    
    suspend fun executeMultipleActions(times: Int) = coroutineScope {
        repeat(times) {
            executeAction()
            delay(100)
        }
    }
    
    fun getActionCount() = actionCount
}

suspend fun simulateOperationExecutor(
    executor: OperationExecutor,
    executorId: Int
) {
    repeat(10) { attempt ->
        executor.executeOperation()
        delay(Random.nextLong(50, 150))
    }
    
    println("Operation executor $executorId completed")
}

suspend fun simulateTaskExecutor(
    executor: TaskExecutor,
    executorId: Int
) {
    repeat(10) { attempt ->
        executor.executeTask()
        delay(Random.nextLong(50, 150))
    }
    
    println("Task executor $executorId completed")
}

suspend fun simulateJobExecutor(
    executor: JobExecutor,
    executorId: Int
) {
    repeat(10) { attempt ->
        executor.executeJob()
        delay(Random.nextLong(50, 150))
    }
    
    println("Job executor $executorId completed")
}

suspend fun simulateWorkExecutor(
    executor: WorkExecutor,
    executorId: Int
) {
    repeat(10) { attempt ->
        executor.executeWork()
        delay(Random.nextLong(50, 150))
    }
    
    println("Work executor $executorId completed")
}

suspend fun simulateActionExecutor(
    executor: ActionExecutor,
    executorId: Int
) {
    repeat(10) { attempt ->
        executor.executeAction()
        delay(Random.nextLong(50, 150))
    }
    
    println("Action executor $executorId completed")
}

suspend fun monitorExecutors(
    operationExecutor: OperationExecutor,
    taskExecutor: TaskExecutor,
    jobExecutor: JobExecutor,
    workExecutor: WorkExecutor,
    actionExecutor: ActionExecutor,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Operation count: ${operationExecutor.getOperationCount()}")
        println("  Task count: ${taskExecutor.getTaskCount()}")
        println("  Job count: ${jobExecutor.getJobCount()}")
        println("  Work count: ${workExecutor.getWorkCount()}")
        println("  Action count: ${actionExecutor.getActionCount()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Operation Cancel Simulation...")
    println()
    
    val operationExecutor = OperationExecutor()
    val taskExecutor = TaskExecutor()
    val jobExecutor = JobExecutor()
    val workExecutor = WorkExecutor()
    val actionExecutor = ActionExecutor()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateOperationExecutor(operationExecutor, 1)
    })
    
    jobs.add(launch {
        simulateOperationExecutor(operationExecutor, 2)
    })
    
    jobs.add(launch {
        simulateTaskExecutor(taskExecutor, 1)
    })
    
    jobs.add(launch {
        simulateJobExecutor(jobExecutor, 1)
    })
    
    jobs.add(launch {
        simulateWorkExecutor(workExecutor, 1)
    })
    
    jobs.add(launch {
        simulateActionExecutor(actionExecutor, 1)
    })
    
    jobs.add(launch {
        monitorExecutors(
            operationExecutor,
            taskExecutor,
            jobExecutor,
            workExecutor,
            actionExecutor,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Execution Counts ===")
    println("Operation count: ${operationExecutor.getOperationCount()}")
    println("Task count: ${taskExecutor.getTaskCount()}")
    println("Job count: ${jobExecutor.getJobCount()}")
    println("Work count: ${workExecutor.getWorkCount()}")
    println("Action count: ${actionExecutor.getActionCount()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels execution jobs and immediately starts new ones:")
    println("  - OperationExecutor.executeOperation() cancels operationJob and starts new one")
    println("  - TaskExecutor.executeTask() cancels taskJob and starts new one")
    println("  - JobExecutor.executeJob() cancels jobJob and starts new one")
    println("  - WorkExecutor.executeWork() cancels workJob and starts new one")
    println("  - ActionExecutor.executeAction() cancels actionJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}