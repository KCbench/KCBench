import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ExceptionPropagationSilentCancellationMisuse2 {
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

class SilentCancellationExceptionMisuse2 {
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

class CoroutineExceptionSilentCancellationMisuse2 {
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

class ExceptionSilentCancellationMisuse2 {
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

class SilentCancellationExceptionMisuse3 {
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

suspend fun simulateExceptionPropagationSilentCancellationMisuse2(
    exceptionPropagationSilentCancellationMisuse2: ExceptionPropagationSilentCancellationMisuse2,
    exceptionPropagationSilentCancellationMisuse2Id: Int
) {
    repeat(10) { attempt ->
        exceptionPropagationSilentCancellationMisuse2.executeMultipleTasks(5)
        delay(100)
    }
    
    println("Exception propagation silent cancellation misuse 2 $exceptionPropagationSilentCancellationMisuse2Id completed")
}

suspend fun simulateSilentCancellationExceptionMisuse2(
    silentCancellationExceptionMisuse2: SilentCancellationExceptionMisuse2,
    silentCancellationExceptionMisuse2Id: Int
) {
    repeat(10) { attempt ->
        silentCancellationExceptionMisuse2.runMultipleJobs(5)
        delay(100)
    }
    
    println("Silent cancellation exception misuse 2 $silentCancellationExceptionMisuse2Id completed")
}

suspend fun simulateCoroutineExceptionSilentCancellationMisuse2(
    coroutineExceptionSilentCancellationMisuse2: CoroutineExceptionSilentCancellationMisuse2,
    coroutineExceptionSilentCancellationMisuse2Id: Int
) {
    repeat(10) { attempt ->
        coroutineExceptionSilentCancellationMisuse2.performMultipleOperations(5)
        delay(100)
    }
    
    println("Coroutine exception silent cancellation misuse 2 $coroutineExceptionSilentCancellationMisuse2Id completed")
}

suspend fun simulateExceptionSilentCancellationMisuse2(
    exceptionSilentCancellationMisuse2: ExceptionSilentCancellationMisuse2,
    exceptionSilentCancellationMisuse2Id: Int
) {
    repeat(10) { attempt ->
        exceptionSilentCancellationMisuse2.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Exception silent cancellation misuse 2 $exceptionSilentCancellationMisuse2Id completed")
}

suspend fun simulateSilentCancellationExceptionMisuse3(
    silentCancellationExceptionMisuse3: SilentCancellationExceptionMisuse3,
    silentCancellationExceptionMisuse3Id: Int
) {
    repeat(10) { attempt ->
        silentCancellationExceptionMisuse3.runMultipleTasks(5)
        delay(100)
    }
    
    println("Silent cancellation exception misuse 3 $silentCancellationExceptionMisuse3Id completed")
}

suspend fun monitorExceptionPropagationSilentCancellationMisuse2(
    exceptionPropagationSilentCancellationMisuse2: ExceptionPropagationSilentCancellationMisuse2,
    silentCancellationExceptionMisuse2: SilentCancellationExceptionMisuse2,
    coroutineExceptionSilentCancellationMisuse2: CoroutineExceptionSilentCancellationMisuse2,
    exceptionSilentCancellationMisuse2: ExceptionSilentCancellationMisuse2,
    silentCancellationExceptionMisuse3: SilentCancellationExceptionMisuse3,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Exception propagation silent cancellation misuse 2 active: true")
        println("  Silent cancellation exception misuse 2 active: true")
        println("  Coroutine exception silent cancellation misuse 2 active: true")
        println("  Exception silent cancellation misuse 2 active: true")
        println("  Silent cancellation exception misuse 3 active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting Exception Propagation Silent Cancellation Misuse 2 Simulation...")
    println()
    
    val exceptionPropagationSilentCancellationMisuse2 = ExceptionPropagationSilentCancellationMisuse2()
    val silentCancellationExceptionMisuse2 = SilentCancellationExceptionMisuse2()
    val coroutineExceptionSilentCancellationMisuse2 = CoroutineExceptionSilentCancellationMisuse2()
    val exceptionSilentCancellationMisuse2 = ExceptionSilentCancellationMisuse2()
    val silentCancellationExceptionMisuse3 = SilentCancellationExceptionMisuse3()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateExceptionPropagationSilentCancellationMisuse2(exceptionPropagationSilentCancellationMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancellationExceptionMisuse2(silentCancellationExceptionMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineExceptionSilentCancellationMisuse2(coroutineExceptionSilentCancellationMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateExceptionSilentCancellationMisuse2(exceptionSilentCancellationMisuse2, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancellationExceptionMisuse3(silentCancellationExceptionMisuse3, 1)
    })
    
    jobs.add(launch {
        monitorExceptionPropagationSilentCancellationMisuse2(
            exceptionPropagationSilentCancellationMisuse2,
            silentCancellationExceptionMisuse2,
            coroutineExceptionSilentCancellationMisuse2,
            exceptionSilentCancellationMisuse2,
            silentCancellationExceptionMisuse3,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Exception Propagation Silent Cancellation Misuse 2 Warning:")
    println("  The code has exception propagation silent cancellation misuse 2 issues:")
    println("  - ExceptionPropagationSilentCancellationMisuse2.executeTask() throws exception in task 2")
    println("  - SilentCancellationExceptionMisuse2.runJob() throws exception in job 2")
    println("  - CoroutineExceptionSilentCancellationMisuse2.performOperation() throws exception in operation 2")
    println("  - ExceptionSilentCancellationMisuse2.executeWork() throws exception in work 2")
    println("  - SilentCancellationExceptionMisuse3.runTask() throws exception in task 2")
    println("  Exception propagation silent cancellation misuse 2 causes other coroutines to be cancelled silently,")
    println("  leading to tasks disappearing without any warning.")
    println("  Fix: Use SupervisorJob or supervisorScope to isolate exceptions.")
}