import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SupervisorScopeMisuse2 {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun executeTask(taskId: Int) = supervisorScope {
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
    
    suspend fun executeMultipleTasks(count: Int) = supervisorScope {
        repeat(count) { index ->
            executeTask(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class SupervisorJobMisuse2 {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun runJob(jobId: Int) = supervisorScope {
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
    
    suspend fun runMultipleJobs(count: Int) = supervisorScope {
        repeat(count) { index ->
            runJob(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class CoroutineScopeMisuse2 {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun performOperation(operationId: Int) = supervisorScope {
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
    
    suspend fun performMultipleOperations(count: Int) = supervisorScope {
        repeat(count) { index ->
            performOperation(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class JobMisuse2 {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun executeWork(workId: Int) = supervisorScope {
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
    
    suspend fun executeMultipleWorks(count: Int) = supervisorScope {
        repeat(count) { index ->
            executeWork(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class ScopeMisuse2 {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun runTask(taskId: Int) = supervisorScope {
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
    
    suspend fun runMultipleTasks(count: Int) = supervisorScope {
        repeat(count) { index ->
            runTask(index)
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

suspend fun simulateSupervisorScopeMisuse2(
    supervisorScopeMisuse2: SupervisorScopeMisuse2,
    supervisorScopeMisuse2Id: Int
) {
    repeat(10) { attempt ->
        supervisorScopeMisuse2.executeMultipleTasks(5)
        delay(100)
    }
    
    println("SupervisorScope misuse 2 $supervisorScopeMisuse2Id completed")
}

suspend fun simulateSupervisorJobMisuse2(
    supervisorJobMisuse2: SupervisorJobMisuse2,
    supervisorJobMisuse2Id: Int
) {
    repeat(10) { attempt ->
        supervisorJobMisuse2.runMultipleJobs(5)
        delay(100)
    }
    
    println("SupervisorJob misuse 2 $supervisorJobMisuse2Id completed")
}

suspend fun simulateCoroutineScopeMisuse2(
    coroutineScopeMisuse2: CoroutineScopeMisuse2,
    coroutineScopeMisuse2Id: Int
) {
    repeat(10) { attempt ->
        coroutineScopeMisuse2.performMultipleOperations(5)
        delay(100)
    }
    
    println("CoroutineScope misuse 2 $coroutineScopeMisuse2Id completed")
}

suspend fun simulateJobMisuse2(
    jobMisuse2: JobMisuse2,
    jobMisuse2Id: Int
) {
    repeat(10) { attempt ->
        jobMisuse2.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Job misuse 2 $jobMisuse2Id completed")
}

suspend fun simulateScopeMisuse2(
    scopeMisuse2: ScopeMisuse2,
    scopeMisuse2Id: Int
) {
    repeat(10) { attempt ->
        scopeMisuse2.runMultipleTasks(5)
        delay(100)
    }
    
    println("Scope misuse 2 $scopeMisuse2Id completed")
}

suspend fun monitorSupervisorScopeMisuse2(
    supervisorScopeMisuse2: SupervisorScopeMisuse2,
    supervisorJobMisuse2: SupervisorJobMisuse2,
    coroutineScopeMisuse2: CoroutineScopeMisuse2,
    jobMisuse2: JobMisuse2,
    scopeMisuse2: ScopeMisuse2,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  SupervisorScope misuse 2 active: true")
        println("  SupervisorJob misuse 2 active: true")
        println("  CoroutineScope misuse 2 active: true")
        println("  Job misuse 2 active: true")
        println("  Scope misuse 2 active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting SupervisorScope Misuse 2 Simulation...")
    println()
    
    val supervisorScopeMisuse2 = SupervisorScopeMisuse2()
    val supervisorJobMisuse2 = SupervisorJobMisuse2()
    val coroutineScopeMisuse2 = CoroutineScopeMisuse2()
    val jobMisuse2 = JobMisuse2()
    val scopeMisuse2 = ScopeMisuse2()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSupervisorScopeMisuse2(supervisorScopeMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateSupervisorJobMisuse2(supervisorJobMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineScopeMisuse2(coroutineScopeMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateJobMisuse2(jobMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateScopeMisuse2(scopeMisuse2, 1)
    })
    
    jobs.add(launch {
        monitorSupervisorScopeMisuse2(
            supervisorScopeMisuse2,
            supervisorJobMisuse2,
            coroutineScopeMisuse2,
            jobMisuse2,
            scopeMisuse2,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  SupervisorScope Misuse 2 Warning:")
    println("  The code misuses SupervisorScope:")
    println("  - SupervisorScopeMisuse2 uses supervisorScope incorrectly")
    println("  - SupervisorJobMisuse2 uses supervisorScope incorrectly")
    println("  - CoroutineScopeMisuse2 uses supervisorScope incorrectly")
    println("  - JobMisuse2 uses supervisorScope incorrectly")
    println("  - ScopeMisuse2 uses supervisorScope incorrectly")
    println("  Misuse of SupervisorScope can cause exception propagation issues,")
    println("  leading to coroutines being cancelled unexpectedly.")
    println("  Fix: Use SupervisorScope correctly and combine with SupervisorJob.")
}