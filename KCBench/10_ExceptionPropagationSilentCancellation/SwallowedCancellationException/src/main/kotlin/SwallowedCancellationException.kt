import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SwallowedCancellationException {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun foo() {
        try {
            suspendingFunction()
        } catch (e: Exception) {
            println("Caught exception: ${e.message}")
        }
    }
    
    suspend fun bar() {
        try {
            suspendingFunction()
        } catch (e: Exception) {
            println("Caught exception: ${e.message}")
        }
    }
    
    suspend fun baz() {
        try {
            suspendingFunction()
        } catch (e: Exception) {
            println("Caught exception: ${e.message}")
        }
    }
    
    suspend fun qux() {
        try {
            suspendingFunction()
        } catch (e: Exception) {
            println("Caught exception: ${e.message}")
        }
    }
    
    suspend fun quux() {
        try {
            suspendingFunction()
        } catch (e: Exception) {
            println("Caught exception: ${e.message}")
        }
    }
    
    suspend fun suspendingFunction() {
        delay(50)
        println("Suspending function executed")
    }
    
    suspend fun runMultipleFunctions(count: Int) = coroutineScope {
        repeat(count) { index ->
            when (index % 5) {
                0 -> foo()
                1 -> bar()
                2 -> baz()
                3 -> qux()
                4 -> quux()
            }
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class CancellationExceptionSwallowed {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun executeTask(taskId: Int) {
        try {
            suspendingTask(taskId)
        } catch (e: Exception) {
            println("Task $taskId caught exception: ${e.message}")
        }
    }
    
    suspend fun executeJob(jobId: Int) {
        try {
            suspendingJob(jobId)
        } catch (e: Exception) {
            println("Job $jobId caught exception: ${e.message}")
        }
    }
    
    suspend fun executeOperation(operationId: Int) {
        try {
            suspendingOperation(operationId)
        } catch (e: Exception) {
            println("Operation $operationId caught exception: ${e.message}")
        }
    }
    
    suspend fun executeWork(workId: Int) {
        try {
            suspendingWork(workId)
        } catch (e: Exception) {
            println("Work $workId caught exception: ${e.message}")
        }
    }
    
    suspend fun executeFunction(functionId: Int) {
        try {
            suspendingFunction(functionId)
        } catch (e: Exception) {
            println("Function $functionId caught exception: ${e.message}")
        }
    }
    
    suspend fun suspendingTask(taskId: Int) {
        delay(50)
        println("Task $taskId executed")
    }
    
    suspend fun suspendingJob(jobId: Int) {
        delay(50)
        println("Job $jobId executed")
    }
    
    suspend fun suspendingOperation(operationId: Int) {
        delay(50)
        println("Operation $operationId executed")
    }
    
    suspend fun suspendingWork(workId: Int) {
        delay(50)
        println("Work $workId executed")
    }
    
    suspend fun suspendingFunction(functionId: Int) {
        delay(50)
        println("Function $functionId executed")
    }
    
    suspend fun runMultipleTasks(count: Int) = coroutineScope {
        repeat(count) { index ->
            when (index % 5) {
                0 -> executeTask(index)
                1 -> executeJob(index)
                2 -> executeOperation(index)
                3 -> executeWork(index)
                4 -> executeFunction(index)
            }
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class ExceptionSwallowedCancellation {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun performAction(actionId: Int) {
        try {
            suspendingAction(actionId)
        } catch (e: Exception) {
            println("Action $actionId caught exception: ${e.message}")
        }
    }
    
    suspend fun performActivity(activityId: Int) {
        try {
            suspendingActivity(activityId)
        } catch (e: Exception) {
            println("Activity $activityId caught exception: ${e.message}")
        }
    }
    
    suspend fun performProcess(processId: Int) {
        try {
            suspendingProcess(processId)
        } catch (e: Exception) {
            println("Process $processId caught exception: ${e.message}")
        }
    }
    
    suspend fun performProcedure(procedureId: Int) {
        try {
            suspendingProcedure(procedureId)
        } catch (e: Exception) {
            println("Procedure $procedureId caught exception: ${e.message}")
        }
    }
    
    suspend fun performRoutine(routineId: Int) {
        try {
            suspendingRoutine(routineId)
        } catch (e: Exception) {
            println("Routine $routineId caught exception: ${e.message}")
        }
    }
    
    suspend fun suspendingAction(actionId: Int) {
        delay(50)
        println("Action $actionId executed")
    }
    
    suspend fun suspendingActivity(activityId: Int) {
        delay(50)
        println("Activity $activityId executed")
    }
    
    suspend fun suspendingProcess(processId: Int) {
        delay(50)
        println("Process $processId executed")
    }
    
    suspend fun suspendingProcedure(procedureId: Int) {
        delay(50)
        println("Procedure $procedureId executed")
    }
    
    suspend fun suspendingRoutine(routineId: Int) {
        delay(50)
        println("Routine $routineId executed")
    }
    
    suspend fun runMultipleActions(count: Int) = coroutineScope {
        repeat(count) { index ->
            when (index % 5) {
                0 -> performAction(index)
                1 -> performActivity(index)
                2 -> performProcess(index)
                3 -> performProcedure(index)
                4 -> performRoutine(index)
            }
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class SwallowedExceptionCancellation {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun doTask(taskId: Int) {
        try {
            suspendingTask(taskId)
        } catch (e: Exception) {
            println("Task $taskId caught exception: ${e.message}")
        }
    }
    
    suspend fun doJob(jobId: Int) {
        try {
            suspendingJob(jobId)
        } catch (e: Exception) {
            println("Job $jobId caught exception: ${e.message}")
        }
    }
    
    suspend fun doOperation(operationId: Int) {
        try {
            suspendingOperation(operationId)
        } catch (e: Exception) {
            println("Operation $operationId caught exception: ${e.message}")
        }
    }
    
    suspend fun doWork(workId: Int) {
        try {
            suspendingWork(workId)
        } catch (e: Exception) {
            println("Work $workId caught exception: ${e.message}")
        }
    }
    
    suspend fun doFunction(functionId: Int) {
        try {
            suspendingFunction(functionId)
        } catch (e: Exception) {
            println("Function $functionId caught exception: ${e.message}")
        }
    }
    
    suspend fun suspendingTask(taskId: Int) {
        delay(50)
        println("Task $taskId executed")
    }
    
    suspend fun suspendingJob(jobId: Int) {
        delay(50)
        println("Job $jobId executed")
    }
    
    suspend fun suspendingOperation(operationId: Int) {
        delay(50)
        println("Operation $operationId executed")
    }
    
    suspend fun suspendingWork(workId: Int) {
        delay(50)
        println("Work $workId executed")
    }
    
    suspend fun suspendingFunction(functionId: Int) {
        delay(50)
        println("Function $functionId executed")
    }
    
    suspend fun runMultipleTasks(count: Int) = coroutineScope {
        repeat(count) { index ->
            when (index % 5) {
                0 -> doTask(index)
                1 -> doJob(index)
                2 -> doOperation(index)
                3 -> doWork(index)
                4 -> doFunction(index)
            }
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

class CancellationSwallowedException {
    private val scope = CoroutineScope(Dispatchers.Default)
    
    suspend fun runAction(actionId: Int) {
        try {
            suspendingAction(actionId)
        } catch (e: Exception) {
            println("Action $actionId caught exception: ${e.message}")
        }
    }
    
    suspend fun runActivity(activityId: Int) {
        try {
            suspendingActivity(activityId)
        } catch (e: Exception) {
            println("Activity $activityId caught exception: ${e.message}")
        }
    }
    
    suspend fun runProcess(processId: Int) {
        try {
            suspendingProcess(processId)
        } catch (e: Exception) {
            println("Process $processId caught exception: ${e.message}")
        }
    }
    
    suspend fun runProcedure(procedureId: Int) {
        try {
            suspendingProcedure(procedureId)
        } catch (e: Exception) {
            println("Procedure $procedureId caught exception: ${e.message}")
        }
    }
    
    suspend fun runRoutine(routineId: Int) {
        try {
            suspendingRoutine(routineId)
        } catch (e: Exception) {
            println("Routine $routineId caught exception: ${e.message}")
        }
    }
    
    suspend fun suspendingAction(actionId: Int) {
        delay(50)
        println("Action $actionId executed")
    }
    
    suspend fun suspendingActivity(activityId: Int) {
        delay(50)
        println("Activity $activityId executed")
    }
    
    suspend fun suspendingProcess(processId: Int) {
        delay(50)
        println("Process $processId executed")
    }
    
    suspend fun suspendingProcedure(procedureId: Int) {
        delay(50)
        println("Procedure $procedureId executed")
    }
    
    suspend fun suspendingRoutine(routineId: Int) {
        delay(50)
        println("Routine $routineId executed")
    }
    
    suspend fun runMultipleActions(count: Int) = coroutineScope {
        repeat(count) { index ->
            when (index % 5) {
                0 -> runAction(index)
                1 -> runActivity(index)
                2 -> runProcess(index)
                3 -> runProcedure(index)
                4 -> runRoutine(index)
            }
            delay(10)
        }
    }
    
    fun cancelAll() {
        scope.cancel()
    }
}

suspend fun simulateSwallowedCancellationException(
    swallowedCancellationException: SwallowedCancellationException,
    swallowedCancellationExceptionId: Int
) {
    repeat(10) { attempt ->
        swallowedCancellationException.runMultipleFunctions(5)
        delay(100)
    }
    
    println("Swallowed cancellation exception $swallowedCancellationExceptionId completed")
}

suspend fun simulateCancellationExceptionSwallowed(
    cancellationExceptionSwallowed: CancellationExceptionSwallowed,
    cancellationExceptionSwallowedId: Int
) {
    repeat(10) { attempt ->
        cancellationExceptionSwallowed.runMultipleTasks(5)
        delay(100)
    }
    
    println("Cancellation exception swallowed $cancellationExceptionSwallowedId completed")
}

suspend fun simulateExceptionSwallowedCancellation(
    exceptionSwallowedCancellation: ExceptionSwallowedCancellation,
    exceptionSwallowedCancellationId: Int
) {
    repeat(10) { attempt ->
        exceptionSwallowedCancellation.runMultipleActions(5)
        delay(100)
    }
    
    println("Exception swallowed cancellation $exceptionSwallowedCancellationId completed")
}

suspend fun simulateSwallowedExceptionCancellation(
    swallowedExceptionCancellation: SwallowedExceptionCancellation,
    swallowedExceptionCancellationId: Int
) {
    repeat(10) { attempt ->
        swallowedExceptionCancellation.runMultipleTasks(5)
        delay(100)
    }
    
    println("Swallowed exception cancellation $swallowedExceptionCancellationId completed")
}

suspend fun simulateCancellationSwallowedException(
    cancellationSwallowedException: CancellationSwallowedException,
    cancellationSwallowedExceptionId: Int
) {
    repeat(10) { attempt ->
        cancellationSwallowedException.runMultipleActions(5)
        delay(100)
    }
    
    println("Cancellation swallowed exception $cancellationSwallowedExceptionId completed")
}

suspend fun monitorSwallowedCancellationExceptions(
    swallowedCancellationException: SwallowedCancellationException,
    cancellationExceptionSwallowed: CancellationExceptionSwallowed,
    exceptionSwallowedCancellation: ExceptionSwallowedCancellation,
    swallowedExceptionCancellation: SwallowedExceptionCancellation,
    cancellationSwallowedException: CancellationSwallowedException,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Swallowed cancellation exception active: true")
        println("  Cancellation exception swallowed active: true")
        println("  Exception swallowed cancellation active: true")
        println("  Swallowed exception cancellation active: true")
        println("  Cancellation swallowed exception active: true")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting Swallowed CancellationException Simulation...")
    println()
    
    val swallowedCancellationException = SwallowedCancellationException()
    val cancellationExceptionSwallowed = CancellationExceptionSwallowed()
    val exceptionSwallowedCancellation = ExceptionSwallowedCancellation()
    val swallowedExceptionCancellation = SwallowedExceptionCancellation()
    val cancellationSwallowedException = CancellationSwallowedException()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSwallowedCancellationException(swallowedCancellationException, 1)
    })
    
    jobs.add(launch {
        simulateCancellationExceptionSwallowed(cancellationExceptionSwallowed, 1)
    })
    
    jobs.add(launch {
        simulateExceptionSwallowedCancellation(exceptionSwallowedCancellation, 1)
    })
    
    jobs.add(launch {
        simulateSwallowedExceptionCancellation(swallowedExceptionCancellation, 1)
    })
    
    jobs.add(launch {
        simulateCancellationSwallowedException(cancellationSwallowedException, 1)
    })
    
    jobs.add(launch {
        monitorSwallowedCancellationExceptions(
            swallowedCancellationException,
            cancellationExceptionSwallowed,
            exceptionSwallowedCancellation,
            swallowedExceptionCancellation,
            cancellationSwallowedException,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Swallowed CancellationException Warning:")
    println("  The code uses wide catch blocks that swallow CancellationException:")
    println("  - SwallowedCancellationException.foo() catches Exception")
    println("  - CancellationExceptionSwallowed.executeTask() catches Exception")
    println("  - ExceptionSwallowedCancellation.performAction() catches Exception")
    println("  - SwallowedExceptionCancellation.doTask() catches Exception")
    println("  - CancellationSwallowedException.runAction() catches Exception")
    println("  Swallowing CancellationException prevents coroutines from being cancelled,")
    println("  leading to coroutines continuing to execute after cancellation.")
    println("  Fix: Catch CancellationException separately and re-throw it.")
}