import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ExceptionPropagationScope {
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

class SilentCancellationScope {
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

class CoroutineExceptionScope {
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

class ExceptionSilentCancelScope {
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

class SilentCancelExceptionScope {
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

suspend fun simulateExceptionPropagationScope(
    exceptionPropagationScope: ExceptionPropagationScope,
    exceptionPropagationScopeId: Int
) {
    repeat(10) { attempt ->
        exceptionPropagationScope.executeMultipleTasks(5)
        delay(100)
    }
    
    println("Exception propagation scope $exceptionPropagationScopeId completed")
}

suspend fun simulateSilentCancellationScope(
    silentCancellationScope: SilentCancellationScope,
    silentCancellationScopeId: Int
) {
    repeat(10) { attempt ->
        silentCancellationScope.runMultipleJobs(5)
        delay(100)
    }
    
    println("Silent cancellation scope $silentCancellationScopeId completed")
}

suspend fun simulateCoroutineExceptionScope(
    coroutineExceptionScope: CoroutineExceptionScope,
    coroutineExceptionScopeId: Int
) {
    repeat(10) { attempt ->
        coroutineExceptionScope.performMultipleOperations(5)
        delay(100)
    }
    
    println("Coroutine exception scope $coroutineExceptionScopeId completed")
}

suspend fun simulateExceptionSilentCancelScope(
    exceptionSilentCancelScope: ExceptionSilentCancelScope,
    exceptionSilentCancelScopeId: Int
) {
    repeat(10) { attempt ->
        exceptionSilentCancelScope.executeMultipleWorks(5)
        delay(100)
    }
    
    println("Exception silent cancel scope $exceptionSilentCancelScopeId completed")
}

suspend fun simulateSilentCancelExceptionScope(
    silentCancelExceptionScope: SilentCancelExceptionScope,
    silentCancelExceptionScopeId: Int
) {
    repeat(10) { attempt ->
        silentCancelExceptionScope.runMultipleTasks(5)
        delay(100)
    }
    
    println("Silent cancel exception scope $silentCancelExceptionScopeId completed")
}

suspend fun monitorExceptionPropagationSilentCancellationScope(
    exceptionPropagationScope: ExceptionPropagationScope,
    silentCancellationScope: SilentCancellationScope,
    coroutineExceptionScope: CoroutineExceptionScope,
    exceptionSilentCancelScope: ExceptionSilentCancelScope,
    silentCancelExceptionScope: SilentCancelExceptionScope,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Exception propagation scope active: true")
        println("  Silent cancellation scope active: true")
        println("  Coroutine exception scope active: true")
        println("  Exception silent cancel scope active: true")
        println("  Silent cancel exception scope active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting Exception Propagation Silent Cancellation Scope Simulation...")
    println()
    
    val exceptionPropagationScope = ExceptionPropagationScope()
    val silentCancellationScope = SilentCancellationScope()
    val coroutineExceptionScope = CoroutineExceptionScope()
    val exceptionSilentCancelScope = ExceptionSilentCancelScope()
    val silentCancelExceptionScope = SilentCancelExceptionScope()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateExceptionPropagationScope(exceptionPropagationScope, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancellationScope(silentCancellationScope, 1)
    })
    
    jobs.add(launch {
        simulateCoroutineExceptionScope(coroutineExceptionScope, 1)
    })
    
    jobs.add(launch {
        simulateExceptionSilentCancelScope(exceptionSilentCancelScope, 1)
    })
    
    jobs.add(launch {
        simulateSilentCancelExceptionScope(silentCancelExceptionScope, 1)
    })
    
    jobs.add(launch {
        monitorExceptionPropagationSilentCancellationScope(
            exceptionPropagationScope,
            silentCancellationScope,
            coroutineExceptionScope,
            exceptionSilentCancelScope,
            silentCancelExceptionScope,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Exception Propagation Silent Cancellation Scope Warning:")
    println("  The code has exception propagation silent cancellation scope issues:")
    println("  - ExceptionPropagationScope.executeTask() throws exception in task 2")
    println("  - SilentCancellationScope.runJob() throws exception in job 2")
    println("  - CoroutineExceptionScope.performOperation() throws exception in operation 2")
    println("  - ExceptionSilentCancelScope.executeWork() throws exception in work 2")
    println("  - SilentCancelExceptionScope.runTask() throws exception in task 2")
    println("  Exception propagation silent cancellation scope causes other coroutines to be cancelled silently,")
    println("  leading to tasks disappearing without any warning.")
    println("  Fix: Use SupervisorJob or supervisorScope to isolate exceptions.")
}