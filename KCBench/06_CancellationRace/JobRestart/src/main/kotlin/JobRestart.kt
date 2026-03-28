import kotlinx.coroutines.*
import kotlin.random.Random

class JobRestarter {
    private var job: Job? = null
    private var restartCount = 0
    
    suspend fun restartJob() = coroutineScope {
        job?.cancel()
        
        job = launch {
            val tmp = restartCount
            delay(50)
            restartCount = tmp + 1
            println("Job restarted (Count: $restartCount)")
        }
    }
    
    suspend fun restartMultipleJobs(times: Int) = coroutineScope {
        repeat(times) {
            restartJob()
            delay(100)
        }
    }
    
    fun getRestartCount() = restartCount
}

class TaskRestarter {
    private var task: Job? = null
    private var restartCount = 0
    
    suspend fun restartTask() = coroutineScope {
        task?.cancel()
        
        task = launch {
            val tmp = restartCount
            delay(50)
            restartCount = tmp + 1
            println("Task restarted (Count: $restartCount)")
        }
    }
    
    suspend fun restartMultipleTasks(times: Int) = coroutineScope {
        repeat(times) {
            restartTask()
            delay(100)
        }
    }
    
    fun getRestartCount() = restartCount
}

class ProcessRestarter {
    private var process: Job? = null
    private var restartCount = 0
    
    suspend fun restartProcess() = coroutineScope {
        process?.cancel()
        
        process = launch {
            val tmp = restartCount
            delay(50)
            restartCount = tmp + 1
            println("Process restarted (Count: $restartCount)")
        }
    }
    
    suspend fun restartMultipleProcesses(times: Int) = coroutineScope {
        repeat(times) {
            restartProcess()
            delay(100)
        }
    }
    
    fun getRestartCount() = restartCount
}

class ServiceRestarter {
    private var service: Job? = null
    private var restartCount = 0
    
    suspend fun restartService() = coroutineScope {
        service?.cancel()
        
        service = launch {
            val tmp = restartCount
            delay(50)
            restartCount = tmp + 1
            println("Service restarted (Count: $restartCount)")
        }
    }
    
    suspend fun restartMultipleServices(times: Int) = coroutineScope {
        repeat(times) {
            restartService()
            delay(100)
        }
    }
    
    fun getRestartCount() = restartCount
}

class WorkerRestarter {
    private var worker: Job? = null
    private var restartCount = 0
    
    suspend fun restartWorker() = coroutineScope {
        worker?.cancel()
        
        worker = launch {
            val tmp = restartCount
            delay(50)
            restartCount = tmp + 1
            println("Worker restarted (Count: $restartCount)")
        }
    }
    
    suspend fun restartMultipleWorkers(times: Int) = coroutineScope {
        repeat(times) {
            restartWorker()
            delay(100)
        }
    }
    
    fun getRestartCount() = restartCount
}

suspend fun simulateJobRestarter(
    restarter: JobRestarter,
    restarterId: Int
) {
    repeat(10) { attempt ->
        restarter.restartJob()
        delay(Random.nextLong(50, 150))
    }
    
    println("Job restarter $restarterId completed")
}

suspend fun simulateTaskRestarter(
    restarter: TaskRestarter,
    restarterId: Int
) {
    repeat(10) { attempt ->
        restarter.restartTask()
        delay(Random.nextLong(50, 150))
    }
    
    println("Task restarter $restarterId completed")
}

suspend fun simulateProcessRestarter(
    restarter: ProcessRestarter,
    restarterId: Int
) {
    repeat(10) { attempt ->
        restarter.restartProcess()
        delay(Random.nextLong(50, 150))
    }
    
    println("Process restarter $restarterId completed")
}

suspend fun simulateServiceRestarter(
    restarter: ServiceRestarter,
    restarterId: Int
) {
    repeat(10) { attempt ->
        restarter.restartService()
        delay(Random.nextLong(50, 150))
    }
    
    println("Service restarter $restarterId completed")
}

suspend fun simulateWorkerRestarter(
    restarter: WorkerRestarter,
    restarterId: Int
) {
    repeat(10) { attempt ->
        restarter.restartWorker()
        delay(Random.nextLong(50, 150))
    }
    
    println("Worker restarter $restarterId completed")
}

suspend fun monitorRestarters(
    jobRestarter: JobRestarter,
    taskRestarter: TaskRestarter,
    processRestarter: ProcessRestarter,
    serviceRestarter: ServiceRestarter,
    workerRestarter: WorkerRestarter,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Job restarts: ${jobRestarter.getRestartCount()}")
        println("  Task restarts: ${taskRestarter.getRestartCount()}")
        println("  Process restarts: ${processRestarter.getRestartCount()}")
        println("  Service restarts: ${serviceRestarter.getRestartCount()}")
        println("  Worker restarts: ${workerRestarter.getRestartCount()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Job Restart Simulation...")
    println()
    
    val jobRestarter = JobRestarter()
    val taskRestarter = TaskRestarter()
    val processRestarter = ProcessRestarter()
    val serviceRestarter = ServiceRestarter()
    val workerRestarter = WorkerRestarter()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateJobRestarter(jobRestarter, 1)
    })
    
    jobs.add(launch {
        simulateJobRestarter(jobRestarter, 2)
    })
    
    jobs.add(launch {
        simulateTaskRestarter(taskRestarter, 1)
    })
    
    jobs.add(launch {
        simulateProcessRestarter(processRestarter, 1)
    })
    
    jobs.add(launch {
        simulateServiceRestarter(serviceRestarter, 1)
    })
    
    jobs.add(launch {
        simulateWorkerRestarter(workerRestarter, 1)
    })
    
    jobs.add(launch {
        monitorRestarters(
            jobRestarter,
            taskRestarter,
            processRestarter,
            serviceRestarter,
            workerRestarter,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Restart Counts ===")
    println("Job restarts: ${jobRestarter.getRestartCount()}")
    println("Task restarts: ${taskRestarter.getRestartCount()}")
    println("Process restarts: ${processRestarter.getRestartCount()}")
    println("Service restarts: ${serviceRestarter.getRestartCount()}")
    println("Worker restarts: ${workerRestarter.getRestartCount()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels jobs and immediately restarts them:")
    println("  - JobRestarter.restartJob() cancels job and starts new one")
    println("  - TaskRestarter.restartTask() cancels task and starts new one")
    println("  - ProcessRestarter.restartProcess() cancels process and starts new one")
    println("  - ServiceRestarter.restartService() cancels service and starts new one")
    println("  - WorkerRestarter.restartWorker() cancels worker and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}