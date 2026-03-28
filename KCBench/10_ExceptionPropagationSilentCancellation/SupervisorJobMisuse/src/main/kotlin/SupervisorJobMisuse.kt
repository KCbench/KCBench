import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SupervisorJobMisuse {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    suspend fun executeTask(taskId: Int) = coroutineScope {
        launch {
            delay(50)
            println("Task $taskId starting")
            delay(50)
            if (taskId == 2) {
                throw RuntimeException("Task $taskId failed")
            }
            println("Task $taskId completed")
        }
    }
    
    suspend fun executeMultipleTasks(count: Int) = coroutineScope {
        repeat(count) { index ->
            executeTask(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class SupervisorScopeMisuse {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun runJob(jobId: Int) = coroutineScope {
        launch {
            delay(50)
            println("Job $jobId starting")
            delay(50)
            if (jobId == 2) {
                throw RuntimeException("Job $jobId failed")
            }
            println("Job $jobId completed")
        }
    }
    
    suspend fun runMultipleJobs(count: Int) = coroutineScope {
        repeat(count) { index ->
            runJob(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class CoroutineScopeMisuse {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun performOperation(operationId: Int) = coroutineScope {
        launch {
            delay(50)
            println("Operation $operationId starting")
            delay(50)
            if (operationId == 2) {
                throw RuntimeException("Operation $operationId failed")
            }
            println("Operation $operationId completed")
        }
    }
    
    suspend fun performMultipleOperations(count: Int) = coroutineScope {
        repeat(count) { index ->
            performOperation(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class JobMisuse {
    private val scope = CoroutineScope(Job() + Dispatchers.Default)
    
    suspend fun executeWork(workId: Int) = coroutineScope {
        launch {
            delay(50)
            println("Work $workId starting")
            delay(50)
            if (workId == 2) {
                throw RuntimeException("Work $workId failed")
            }
            println("Work $workId completed")
        }
    }
    
    suspend fun executeMultipleWorks(count: Int) = coroutineScope {
        repeat(count) { index ->
            executeWork(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class ScopeMisuse {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun runTask(taskId: Int) = coroutineScope {
        launch {
            delay(50)
            println("Task $taskId starting")
            delay(50)
            if (taskId == 2) {
                throw RuntimeException("Task $taskId failed")
            }
            println("Task $taskId completed")
        }
    }
    
    suspend fun runMultipleTasks(count: Int) = coroutineScope {
        repeat(count) { index ->
            runTask(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

suspend fun simulateSupervisorJobMisuse(
    supervisorJobMisuse: SupervisorJobMisuse,
    supervisorJobMisuseId: Int
) {
    repeat(10) { attempt ->
        supervisorJobMisuse.executeMultipleTasks(5)
        delay(100)
    }
    
    println("SupervisorJob misuse $supervisorJobMisuseId completed")
}

suspend fun simulateSupervisorScopeMisuse(
    supervisorScopeMisuse: SupervisorScopeMisuse,
    supervisorScopeMisuseId: Int
) {
    repeat(10) { attempt ->
        supervisorScopeMisuse.runMultipleJobs(5)
        delay(100)
    }
    
    println("SupervisorScope misuse $supervisorScopeMisuseId completed")
}

suspend fun simulateCoroutineScopeMisuse(
    coroutineScopeMisuse: CoroutineScopeMisuse,
    coroutineScopeMisuseId: Int
) {
    repeat(10) { attempt ->
        coroutineScopeMisuse.performMultipleOperations(5)
        delay(100)
    }
    
    println("CoroutineScope misuse $coroutineScopeMisuseId completed")
}

suspend fun simulateJobMisuse(
    jobMisuse: JobMisuse,
    jobMisuseId: Int
) {
    repeat(10) { attempt ->
        jobMisuse.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Job misuse $jobMisuseId completed")
}

suspend fun simulateScopeMisuse(
    scopeMisuse: ScopeMisuse,
    scopeMisuseId: Int
) {
    repeat(10) { attempt ->
        scopeMisuse.runMultipleTasks(5)
        delay(100)
    }
    
    println("Scope misuse $scopeMisuseId completed")
}

suspend fun monitorSupervisorJobMisuse(
    supervisorJobMisuse: SupervisorJobMisuse,
    supervisorScopeMisuse: SupervisorScopeMisuse,
    coroutineScopeMisuse: CoroutineScopeMisuse,
    jobMisuse: JobMisuse,
    scopeMisuse: ScopeMisuse,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  SupervisorJob misuse active: true")
        println("  SupervisorScope misuse active: true")
        println("  CoroutineScope misuse active: true")
        println("  Job misuse active: true")
        println("  Scope misuse active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting SupervisorJob Misuse Simulation...")
    println()
    
    val supervisorJobMisuse = SupervisorJobMisuse()
    val supervisorScopeMisuse = SupervisorScopeMisuse()
    val coroutineScopeMisuse = CoroutineScopeMisuse()
    val jobMisuse = JobMisuse()
    val scopeMisuse = ScopeMisuse()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSupervisorJobMisuse(supervisorJobMisuse, 1)
    })
    
    jobs.add(launch {
        simulateSupervisorScopeMisuse(supervisorScopeMisuse, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineScopeMisuse(coroutineScopeMisuse, 1)
    })
    
    jobs.add(launch {
        simulateJobMisuse(jobMisuse, 1)
    })
    
    jobs.add(launch {
        simulateScopeMisuse(scopeMisuse, 1)
    })
    
    jobs.add(launch {
        monitorSupervisorJobMisuse(
            supervisorJobMisuse,
            supervisorScopeMisuse,
            coroutineScopeMisuse,
            jobMisuse,
            scopeMisuse,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  SupervisorJob Misuse Warning:")
    println("  The code misuses SupervisorJob and CoroutineScope:")
    println("  - SupervisorJobMisuse uses SupervisorJob incorrectly")
    println("  - SupervisorScopeMisuse uses coroutineScope incorrectly")
    println("  - CoroutineScopeMisuse uses CoroutineScope incorrectly")
    println("  - JobMisuse uses Job incorrectly")
    println("  - ScopeMisuse uses CoroutineScope incorrectly")
    println("  Misuse of SupervisorJob and CoroutineScope can cause exception propagation issues,")
    println("  leading to coroutines being cancelled unexpectedly.")
    println("  Fix: Use SupervisorJob correctly and use supervisorScope for exception isolation.")
}