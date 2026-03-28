import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CoroutineExceptionHandlerMisuse {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception: ${exception.message}")
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)
    
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

class ExceptionHandlerMisuse {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception: ${exception.message}")
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)
    
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

class CoroutineScopeExceptionHandlerMisuse {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception: ${exception.message}")
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)
    
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

class ExceptionScopeHandlerMisuse {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception: ${exception.message}")
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)
    
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

class CoroutineExceptionHandlerScopeMisuse {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception: ${exception.message}")
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)
    
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

suspend fun simulateCoroutineExceptionHandlerMisuse(
    coroutineExceptionHandlerMisuse: CoroutineExceptionHandlerMisuse,
    coroutineExceptionHandlerMisuseId: Int
) {
    repeat(10) { attempt ->
        coroutineExceptionHandlerMisuse.executeMultipleTasks(5)
        delay(100)
    }
    
    println("CoroutineExceptionHandler misuse $coroutineExceptionHandlerMisuseId completed")
}

suspend fun simulateExceptionHandlerMisuse(
    exceptionHandlerMisuse: ExceptionHandlerMisuse,
    exceptionHandlerMisuseId: Int
) {
    repeat(10) { attempt ->
        exceptionHandlerMisuse.runMultipleJobs(5)
        delay(100)
    }
    
    println("ExceptionHandler misuse $exceptionHandlerMisuseId completed")
}

suspend fun simulateCoroutineScopeExceptionHandlerMisuse(
    coroutineScopeExceptionHandlerMisuse: CoroutineScopeExceptionHandlerMisuse,
    coroutineScopeExceptionHandlerMisuseId: Int
) {
    repeat(10) { attempt ->
        coroutineScopeExceptionHandlerMisuse.performMultipleOperations(5)
        delay(100)
    }
    
    println("CoroutineScope exception handler misuse $coroutineScopeExceptionHandlerMisuseId completed")
}

suspend fun simulateExceptionScopeHandlerMisuse(
    exceptionScopeHandlerMisuse: ExceptionScopeHandlerMisuse,
    exceptionScopeHandlerMisuseId: Int
) {
    repeat(10) { attempt ->
        exceptionScopeHandlerMisuse.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Exception scope handler misuse $exceptionScopeHandlerMisuseId completed")
}

suspend fun simulateCoroutineExceptionHandlerScopeMisuse(
    coroutineExceptionHandlerScopeMisuse: CoroutineExceptionHandlerScopeMisuse,
    coroutineExceptionHandlerScopeMisuseId: Int
) {
    repeat(10) { attempt ->
        coroutineExceptionHandlerScopeMisuse.runMultipleTasks(5)
        delay(100)
    }
    
    println("CoroutineExceptionHandler scope misuse $coroutineExceptionHandlerScopeMisuseId completed")
}

suspend fun monitorCoroutineExceptionHandlerMisuse(
    coroutineExceptionHandlerMisuse: CoroutineExceptionHandlerMisuse,
    exceptionHandlerMisuse: ExceptionHandlerMisuse,
    coroutineScopeExceptionHandlerMisuse: CoroutineScopeExceptionHandlerMisuse,
    exceptionScopeHandlerMisuse: ExceptionScopeHandlerMisuse,
    coroutineExceptionHandlerScopeMisuse: CoroutineExceptionHandlerScopeMisuse,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  CoroutineExceptionHandler misuse active: true")
        println("  ExceptionHandler misuse active: true")
        println("  CoroutineScope exception handler misuse active: true")
        println("  Exception scope handler misuse active: true")
        println("  CoroutineExceptionHandler scope misuse active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting CoroutineExceptionHandler Misuse Simulation...")
    println()
    
    val coroutineExceptionHandlerMisuse = CoroutineExceptionHandlerMisuse()
    val exceptionHandlerMisuse = ExceptionHandlerMisuse()
    val coroutineScopeExceptionHandlerMisuse = CoroutineScopeExceptionHandlerMisuse()
    val exceptionScopeHandlerMisuse = ExceptionScopeHandlerMisuse()
    val coroutineExceptionHandlerScopeMisuse = CoroutineExceptionHandlerScopeMisuse()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateCoroutineExceptionHandlerMisuse(coroutineExceptionHandlerMisuse, 1)
    })
    
    jobs.add(launch {
        simulateExceptionHandlerMisuse(exceptionHandlerMisuse, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineScopeExceptionHandlerMisuse(coroutineScopeExceptionHandlerMisuse, 1)
    })
    
    jobs.add(launch {
        simulateExceptionScopeHandlerMisuse(exceptionScopeHandlerMisuse, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineExceptionHandlerScopeMisuse(coroutineExceptionHandlerScopeMisuse, 1)
    })
    
    jobs.add(launch {
        monitorCoroutineExceptionHandlerMisuse(
            coroutineExceptionHandlerMisuse,
            exceptionHandlerMisuse,
            coroutineScopeExceptionHandlerMisuse,
            exceptionScopeHandlerMisuse,
            coroutineExceptionHandlerScopeMisuse,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  CoroutineExceptionHandler Misuse Warning:")
    println("  The code misuses CoroutineExceptionHandler:")
    println("  - CoroutineExceptionHandlerMisuse uses CoroutineExceptionHandler incorrectly")
    println("  - ExceptionHandlerMisuse uses CoroutineExceptionHandler incorrectly")
    println("  - CoroutineScopeExceptionHandlerMisuse uses CoroutineExceptionHandler incorrectly")
    println("  - ExceptionScopeHandlerMisuse uses CoroutineExceptionHandler incorrectly")
    println("  - CoroutineExceptionHandlerScopeMisuse uses CoroutineExceptionHandler incorrectly")
    println("  Misuse of CoroutineExceptionHandler can cause exception handling issues,")
    println("  leading to exceptions not being handled properly.")
    println("  Fix: Use CoroutineExceptionHandler correctly and combine with SupervisorJob.")
}