import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ExceptionPropagation {
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

class SilentCancellation {
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

class CoroutineException {
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

class ExceptionSilentCancel {
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

class SilentCancelException {
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

suspend fun simulateExceptionPropagation(
    propagation: ExceptionPropagation,
    propagationId: Int
) {
    repeat(10) { attempt ->
        propagation.executeMultipleTasks(5)
        delay(100)
    }
    
    println("Exception propagation $propagationId completed")
}

suspend fun simulateSilentCancellation(
    silentCancellation: SilentCancellation,
    silentCancellationId: Int
) {
    repeat(10) { attempt ->
        silentCancellation.runMultipleJobs(5)
        delay(100)
    }
    
    println("Silent cancellation $silentCancellationId completed")
}

suspend fun simulateCoroutineException(
    coroutineException: CoroutineException,
    coroutineExceptionId: Int
) {
    repeat(10) { attempt ->
        coroutineException.performMultipleOperations(5)
        delay(100)
    }
    
    println("Coroutine exception $coroutineExceptionId completed")
}

suspend fun simulateExceptionSilentCancel(
    exceptionSilentCancel: ExceptionSilentCancel,
    exceptionSilentCancelId: Int
) {
    repeat(10) { attempt ->
        exceptionSilentCancel.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Exception silent cancel $exceptionSilentCancelId completed")
}

suspend fun simulateSilentCancelException(
    silentCancelException: SilentCancelException,
    silentCancelExceptionId: Int
) {
    repeat(10) { attempt ->
        silentCancelException.runMultipleTasks(5)
        delay(100)
    }
    
    println("Silent cancel exception $silentCancelExceptionId completed")
}

suspend fun monitorExceptionPropagationSilentCancellation(
    propagation: ExceptionPropagation,
    silentCancellation: SilentCancellation,
    coroutineException: CoroutineException,
    exceptionSilentCancel: ExceptionSilentCancel,
    silentCancelException: SilentCancelException,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Exception propagation active: true")
        println("  Silent cancellation active: true")
        println("  Coroutine exception active: true")
        println("  Exception silent cancel active: true")
        println("  Silent cancel exception active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting Exception Propagation Silent Cancellation Simulation...")
    println()
    
    val propagation = ExceptionPropagation()
    val silentCancellation = SilentCancellation()
    val coroutineException = CoroutineException()
    val exceptionSilentCancel = ExceptionSilentCancel()
    val silentCancelException = SilentCancelException()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateExceptionPropagation(propagation, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancellation(silentCancellation, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineException(coroutineException, 1)
    })
    
    jobs.add(launch {
        simulateExceptionSilentCancel(exceptionSilentCancel, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancelException(silentCancelException, 1)
    })
    
    jobs.add(launch {
        monitorExceptionPropagationSilentCancellation(
            propagation,
            silentCancellation,
            coroutineException,
            exceptionSilentCancel,
            silentCancelException,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Exception Propagation Silent Cancellation Warning:")
    println("  The code has exception propagation silent cancellation issues:")
    println("  - ExceptionPropagation.executeTask() throws exception in task 2")
    println("  - SilentCancellation.runJob() throws exception in job 2")
    println("  - CoroutineException.performOperation() throws exception in operation 2")
    println("  - ExceptionSilentCancel.executeWork() throws exception in work 2")
    println("  - SilentCancelException.runTask() throws exception in task 2")
    println("  Exception propagation silent cancellation causes other coroutines to be cancelled silently,")
    println("  leading to tasks disappearing without any warning.")
    println("  Fix: Use SupervisorJob or supervisorScope to isolate exceptions.")
}