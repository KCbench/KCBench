import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ExceptionPropagationSilentCancellationMisuse {
    private val scope = CoroutineScope(Dispatchers.Default)
    
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

class SilentCancellationExceptionMisuse {
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

class CoroutineExceptionSilentCancellationMisuse {
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

class ExceptionSilentCancellationMisuse {
    private val scope = CoroutineScope(Dispatchers.Default)
    
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

class SilentCancellationExceptionMisuse2 {
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

suspend fun simulateExceptionPropagationSilentCancellationMisuse(
    exceptionPropagationSilentCancellationMisuse: ExceptionPropagationSilentCancellationMisuse,
    exceptionPropagationSilentCancellationMisuseId: Int
) {
    repeat(10) { attempt ->
        exceptionPropagationSilentCancellationMisuse.executeMultipleTasks(5)
        delay(100)
    }
    
    println("Exception propagation silent cancellation misuse $exceptionPropagationSilentCancellationMisuseId completed")
}

suspend fun simulateSilentCancellationExceptionMisuse(
    silentCancellationExceptionMisuse: SilentCancellationExceptionMisuse,
    silentCancellationExceptionMisuseId: Int
) {
    repeat(10) { attempt ->
        silentCancellationExceptionMisuse.runMultipleJobs(5)
        delay(100)
    }
    
    println("Silent cancellation exception misuse $silentCancellationExceptionMisuseId completed")
}

suspend fun simulateCoroutineExceptionSilentCancellationMisuse(
    coroutineExceptionSilentCancellationMisuse: CoroutineExceptionSilentCancellationMisuse,
    coroutineExceptionSilentCancellationMisuseId: Int
) {
    repeat(10) { attempt ->
        coroutineExceptionSilentCancellationMisuse.performMultipleOperations(5)
        delay(100)
    }
    
    println("Coroutine exception silent cancellation misuse $coroutineExceptionSilentCancellationMisuseId completed")
}

suspend fun simulateExceptionSilentCancellationMisuse(
    exceptionSilentCancellationMisuse: ExceptionSilentCancellationMisuse,
    exceptionSilentCancellationMisuseId: Int
) {
    repeat(10) { attempt ->
        exceptionSilentCancellationMisuse.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Exception silent cancellation misuse $exceptionSilentCancellationMisuseId completed")
}

suspend fun simulateSilentCancellationExceptionMisuse2(
    silentCancellationExceptionMisuse2: SilentCancellationExceptionMisuse2,
    silentCancellationExceptionMisuse2Id: Int
) {
    repeat(10) { attempt ->
        silentCancellationExceptionMisuse2.runMultipleTasks(5)
        delay(100)
    }
    
    println("Silent cancellation exception misuse 2 $silentCancellationExceptionMisuse2Id completed")
}

suspend fun monitorExceptionPropagationSilentCancellationMisuse(
    exceptionPropagationSilentCancellationMisuse: ExceptionPropagationSilentCancellationMisuse,
    silentCancellationExceptionMisuse: SilentCancellationExceptionMisuse,
    coroutineExceptionSilentCancellationMisuse: CoroutineExceptionSilentCancellationMisuse,
    exceptionSilentCancellationMisuse: ExceptionSilentCancellationMisuse,
    silentCancellationExceptionMisuse2: SilentCancellationExceptionMisuse2,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Exception propagation silent cancellation misuse active: true")
        println("  Silent cancellation exception misuse active: true")
        println("  Coroutine exception silent cancellation misuse active: true")
        println("  Exception silent cancellation misuse active: true")
        println("  Silent cancellation exception misuse 2 active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting Exception Propagation Silent Cancellation Misuse Simulation...")
    println()
    
    val exceptionPropagationSilentCancellationMisuse = ExceptionPropagationSilentCancellationMisuse()
    val silentCancellationExceptionMisuse = SilentCancellationExceptionMisuse()
    val coroutineExceptionSilentCancellationMisuse = CoroutineExceptionSilentCancellationMisuse()
    val exceptionSilentCancellationMisuse = ExceptionSilentCancellationMisuse()
    val silentCancellationExceptionMisuse2 = SilentCancellationExceptionMisuse2()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateExceptionPropagationSilentCancellationMisuse(exceptionPropagationSilentCancellationMisuse, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancellationExceptionMisuse(silentCancellationExceptionMisuse, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineExceptionSilentCancellationMisuse(coroutineExceptionSilentCancellationMisuse, 1)
    })
    
    jobs.add(launch {
        simulateExceptionSilentCancellationMisuse(exceptionSilentCancellationMisuse, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancellationExceptionMisuse2(silentCancellationExceptionMisuse2, 1)
    })
    
    jobs.add(launch {
        monitorExceptionPropagationSilentCancellationMisuse(
            exceptionPropagationSilentCancellationMisuse,
            silentCancellationExceptionMisuse,
            coroutineExceptionSilentCancellationMisuse,
            exceptionSilentCancellationMisuse,
            silentCancellationExceptionMisuse2,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Exception Propagation Silent Cancellation Misuse Warning:")
    println("  The code has exception propagation silent cancellation misuse issues:")
    println("  - ExceptionPropagationSilentCancellationMisuse.executeTask() throws exception in task 2")
    println("  - SilentCancellationExceptionMisuse.runJob() throws exception in job 2")
    println("  - CoroutineExceptionSilentCancellationMisuse.performOperation() throws exception in operation 2")
    println("  - ExceptionSilentCancellationMisuse.executeWork() throws exception in work 2")
    println("  - SilentCancellationExceptionMisuse2.runTask() throws exception in task 2")
    println("  Exception propagation silent cancellation misuse causes other coroutines to be cancelled silently,")
    println("  leading to tasks disappearing without any warning.")
    println("  Fix: Use SupervisorJob or supervisorScope to isolate exceptions.")
}