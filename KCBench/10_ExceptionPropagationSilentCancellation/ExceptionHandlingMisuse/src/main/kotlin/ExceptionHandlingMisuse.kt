import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ExceptionHandlingMisuse {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun executeTask(taskId: Int) = coroutineScope {
        launch {
            try {
                delay(50)
                println("Task $taskId starting")
                delay(50)
                if (taskId == 2) {
                    throw RuntimeException("Task $taskId failed")
                }
                println("Task $taskId completed")
            } catch (e: Exception) {
                println("Task $taskId caught exception: ${e.message}")
            }
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

class SilentCancellationHandling {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun runJob(jobId: Int) = coroutineScope {
        launch {
            try {
                delay(50)
                println("Job $jobId starting")
                delay(50)
                if (jobId == 2) {
                    throw RuntimeException("Job $jobId failed")
                }
                println("Job $jobId completed")
            } catch (e: Exception) {
                println("Job $jobId caught exception: ${e.message}")
            }
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

class CoroutineExceptionHandling {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun performOperation(operationId: Int) = coroutineScope {
        launch {
            try {
                delay(50)
                println("Operation $operationId starting")
                delay(50)
                if (operationId == 2) {
                    throw RuntimeException("Operation $operationId failed")
                }
                println("Operation $operationId completed")
            } catch (e: Exception) {
                println("Operation $operationId caught exception: ${e.message}")
            }
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

class ExceptionSilentCancelHandling {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun executeWork(workId: Int) = coroutineScope {
        launch {
            try {
                delay(50)
                println("Work $workId starting")
                delay(50)
                if (workId == 2) {
                    throw RuntimeException("Work $workId failed")
                }
                println("Work $workId completed")
            } catch (e: Exception) {
                println("Work $workId caught exception: ${e.message}")
            }
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

class SilentCancelExceptionHandling {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun runTask(taskId: Int) = coroutineScope {
        launch {
            try {
                delay(50)
                println("Task $taskId starting")
                delay(50)
                if (taskId == 2) {
                    throw RuntimeException("Task $taskId failed")
                }
                println("Task $taskId completed")
            } catch (e: Exception) {
                println("Task $taskId caught exception: ${e.message}")
            }
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

suspend fun simulateExceptionHandlingMisuse(
    exceptionHandlingMisuse: ExceptionHandlingMisuse,
    exceptionHandlingMisuseId: Int
) {
    repeat(10) { attempt ->
        exceptionHandlingMisuse.executeMultipleTasks(5)
        delay(100)
    }
    
    println("Exception handling misuse $exceptionHandlingMisuseId completed")
}

suspend fun simulateSilentCancellationHandling(
    silentCancellationHandling: SilentCancellationHandling,
    silentCancellationHandlingId: Int
) {
    repeat(10) { attempt ->
        silentCancellationHandling.runMultipleJobs(5)
        delay(100)
    }
    
    println("Silent cancellation handling $silentCancellationHandlingId completed")
}

suspend fun simulateCoroutineExceptionHandling(
    coroutineExceptionHandling: CoroutineExceptionHandling,
    coroutineExceptionHandlingId: Int
) {
    repeat(10) { attempt ->
        coroutineExceptionHandling.performMultipleOperations(5)
        delay(100)
    }
    
    println("Coroutine exception handling $coroutineExceptionHandlingId completed")
}

suspend fun simulateExceptionSilentCancelHandling(
    exceptionSilentCancelHandling: ExceptionSilentCancelHandling,
    exceptionSilentCancelHandlingId: Int
) {
    repeat(10) { attempt ->
        exceptionSilentCancelHandling.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Exception silent cancel handling $exceptionSilentCancelHandlingId completed")
}

suspend fun simulateSilentCancelExceptionHandling(
    silentCancelExceptionHandling: SilentCancelExceptionHandling,
    silentCancelExceptionHandlingId: Int
) {
    repeat(10) { attempt ->
        silentCancelExceptionHandling.runMultipleTasks(5)
        delay(100)
    }
    
    println("Silent cancel exception handling $silentCancelExceptionHandlingId completed")
}

suspend fun monitorExceptionHandlingMisuse(
    exceptionHandlingMisuse: ExceptionHandlingMisuse,
    silentCancellationHandling: SilentCancellationHandling,
    coroutineExceptionHandling: CoroutineExceptionHandling,
    exceptionSilentCancelHandling: ExceptionSilentCancelHandling,
    silentCancelExceptionHandling: SilentCancelExceptionHandling,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Exception handling misuse active: true")
        println("  Silent cancellation handling active: true")
        println("  Coroutine exception handling active: true")
        println("  Exception silent cancel handling active: true")
        println("  Silent cancel exception handling active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting Exception Handling Misuse Simulation...")
    println()
    
    val exceptionHandlingMisuse = ExceptionHandlingMisuse()
    val silentCancellationHandling = SilentCancellationHandling()
    val coroutineExceptionHandling = CoroutineExceptionHandling()
    val exceptionSilentCancelHandling = ExceptionSilentCancelHandling()
    val silentCancelExceptionHandling = SilentCancelExceptionHandling()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateExceptionHandlingMisuse(exceptionHandlingMisuse, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancellationHandling(silentCancellationHandling, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineExceptionHandling(coroutineExceptionHandling, 1)
    })
    
    jobs.add(launch {
        simulateExceptionSilentCancelHandling(exceptionSilentCancelHandling, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancelExceptionHandling(silentCancelExceptionHandling, 1)
    })
    
    jobs.add(launch {
        monitorExceptionHandlingMisuse(
            exceptionHandlingMisuse,
            silentCancellationHandling,
            coroutineExceptionHandling,
            exceptionSilentCancelHandling,
            silentCancelExceptionHandling,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Exception Handling Misuse Warning:")
    println("  The code misuses exception handling:")
    println("  - ExceptionHandlingMisuse.executeTask() catches Exception")
    println("  - SilentCancellationHandling.runJob() catches Exception")
    println("  - CoroutineExceptionHandling.performOperation() catches Exception")
    println("  - ExceptionSilentCancelHandling.executeWork() catches Exception")
    println("  - SilentCancelExceptionHandling.runTask() catches Exception")
    println("  Misuse of exception handling can swallow CancellationException,")
    println("  leading to coroutines continuing to execute after cancellation.")
    println("  Fix: Catch CancellationException separately and re-throw it.")
}