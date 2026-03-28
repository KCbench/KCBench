import kotlinx.coroutines.*
import kotlin.random.Random

class UnstructuredTaskRunner {
    private var taskCount = 0
    private var completedCount = 0
    
    fun runTask(taskName: String) {
        GlobalScope.launch {
            executeTask(taskName)
        }
        taskCount++
        println("Started task: $taskName (Total: $taskCount)")
    }
    
    fun runMultipleTasks(taskNames: List<String>) {
        taskNames.forEach { taskName ->
            runTask(taskName)
        }
    }
    
    private suspend fun executeTask(taskName: String) {
        repeat(10) { iteration ->
            delay(Random.nextLong(100, 300))
            println("$taskName: Iteration $iteration")
        }
        completedCount++
        println("$taskName: Completed")
    }
    
    fun getTaskCount() = taskCount
    fun getCompletedCount() = completedCount
}

class IndependentCoroutineLauncher {
    private var launchCount = 0
    private var completedCount = 0
    
    fun launchCoroutine(coroutineName: String) {
        GlobalScope.launch {
            executeCoroutine(coroutineName)
        }
        launchCount++
        println("Launched coroutine: $coroutineName (Total: $launchCount)")
    }
    
    fun launchMultipleCoroutines(coroutineNames: List<String>) {
        coroutineNames.forEach { coroutineName ->
            launchCoroutine(coroutineName)
        }
    }
    
    private suspend fun executeCoroutine(coroutineName: String) {
        repeat(12) { iteration ->
            delay(Random.nextLong(100, 350))
            println("$coroutineName: Iteration $iteration")
        }
        completedCount++
        println("$coroutineName: Completed")
    }
    
    fun getLaunchCount() = launchCount
    fun getCompletedCount() = completedCount
}

class AsyncTaskExecutor {
    private var asyncCount = 0
    private var completedCount = 0
    
    fun executeAsync(taskName: String) {
        GlobalScope.async {
            executeTask(taskName)
        }
        asyncCount++
        println("Executed async: $taskName (Total: $asyncCount)")
    }
    
    fun executeMultipleAsyncs(taskNames: List<String>) {
        taskNames.forEach { taskName ->
            executeAsync(taskName)
        }
    }
    
    private suspend fun executeTask(taskName: String) {
        repeat(8) { iteration ->
            delay(Random.nextLong(100, 250))
            println("$taskName: Iteration $iteration")
        }
        completedCount++
        println("$taskName: Completed")
    }
    
    fun getAsyncCount() = asyncCount
    fun getCompletedCount() = completedCount
}

class ParallelJobRunner {
    private var jobCount = 0
    private var completedCount = 0
    
    fun runParallelJob(jobName: String) {
        GlobalScope.launch {
            executeJob(jobName)
        }
        jobCount++
        println("Started parallel job: $jobName (Total: $jobCount)")
    }
    
    fun runMultipleParallelJobs(jobNames: List<String>) {
        jobNames.forEach { jobName ->
            runParallelJob(jobName)
        }
    }
    
    private suspend fun executeJob(jobName: String) {
        repeat(10) { iteration ->
            delay(Random.nextLong(100, 300))
            println("$jobName: Iteration $iteration")
        }
        completedCount++
        println("$jobName: Completed")
    }
    
    fun getJobCount() = jobCount
    fun getCompletedCount() = completedCount
}

class DetachedTaskManager {
    private var taskCount = 0
    private var completedCount = 0
    
    fun startDetachedTask(taskName: String) {
        GlobalScope.launch {
            executeDetachedTask(taskName)
        }
        taskCount++
        println("Started detached task: $taskName (Total: $taskCount)")
    }
    
    fun startMultipleDetachedTasks(taskNames: List<String>) {
        taskNames.forEach { taskName ->
            startDetachedTask(taskName)
        }
    }
    
    private suspend fun executeDetachedTask(taskName: String) {
        repeat(15) { iteration ->
            delay(Random.nextLong(100, 400))
            println("$taskName: Iteration $iteration")
        }
        completedCount++
        println("$taskName: Completed")
    }
    
    fun getTaskCount() = taskCount
    fun getCompletedCount() = completedCount
}

suspend fun simulateUnstructuredTaskRunner(
    runner: UnstructuredTaskRunner
) {
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    println("Running unstructured tasks...")
    runner.runMultipleTasks(taskNames)
    
    delay(3000)
    
    println("\nUnstructured Task Runner Summary:")
    println("  Total tasks: ${runner.getTaskCount()}")
    println("  Completed: ${runner.getCompletedCount()}")
}

suspend fun simulateIndependentCoroutineLauncher(
    launcher: IndependentCoroutineLauncher
) {
    val coroutineNames = listOf(
        "Coroutine1", "Coroutine2", "Coroutine3", "Coroutine4", "Coroutine5"
    )
    
    println("Launching independent coroutines...")
    launcher.launchMultipleCoroutines(coroutineNames)
    
    delay(3500)
    
    println("\nIndependent Coroutine Launcher Summary:")
    println("  Total launched: ${launcher.getLaunchCount()}")
    println("  Completed: ${launcher.getCompletedCount()}")
}

suspend fun simulateAsyncTaskExecutor(
    executor: AsyncTaskExecutor
) {
    val taskNames = listOf(
        "Async1", "Async2", "Async3", "Async4", "Async5"
    )
    
    println("Executing async tasks...")
    executor.executeMultipleAsyncs(taskNames)
    
    delay(2500)
    
    println("\nAsync Task Executor Summary:")
    println("  Total async: ${executor.getAsyncCount()}")
    println("  Completed: ${executor.getCompletedCount()}")
}

suspend fun simulateParallelJobRunner(
    runner: ParallelJobRunner
) {
    val jobNames = listOf(
        "Job1", "Job2", "Job3", "Job4", "Job5"
    )
    
    println("Running parallel jobs...")
    runner.runMultipleParallelJobs(jobNames)
    
    delay(3000)
    
    println("\nParallel Job Runner Summary:")
    println("  Total jobs: ${runner.getJobCount()}")
    println("  Completed: ${runner.getCompletedCount()}")
}

suspend fun simulateDetachedTaskManager(
    manager: DetachedTaskManager
) {
    val taskNames = listOf(
        "Detached1", "Detached2", "Detached3", "Detached4", "Detached5"
    )
    
    println("Starting detached tasks...")
    manager.startMultipleDetachedTasks(taskNames)
    
    delay(5000)
    
    println("\nDetached Task Manager Summary:")
    println("  Total tasks: ${manager.getTaskCount()}")
    println("  Completed: ${manager.getCompletedCount()}")
}

fun main() = runBlocking {
    println("Starting Structured Concurrency Violation Simulation...")
    println()
    
    val runner = UnstructuredTaskRunner()
    val launcher = IndependentCoroutineLauncher()
    val executor = AsyncTaskExecutor()
    val parallelRunner = ParallelJobRunner()
    val manager = DetachedTaskManager()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateUnstructuredTaskRunner(runner)
    })
    
    jobs.add(launch {
        simulateIndependentCoroutineLauncher(launcher)
    })
    
    jobs.add(launch {
        simulateAsyncTaskExecutor(executor)
    })
    
    jobs.add(launch {
        simulateParallelJobRunner(parallelRunner)
    })
    
    jobs.add(launch {
        simulateDetachedTaskManager(manager)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Unstructured Task Runner:")
    println("  Total tasks: ${runner.getTaskCount()}")
    println("  Completed: ${runner.getCompletedCount()}")
    
    println("\nIndependent Coroutine Launcher:")
    println("  Total launched: ${launcher.getLaunchCount()}")
    println("  Completed: ${launcher.getCompletedCount()}")
    
    println("\nAsync Task Executor:")
    println("  Total async: ${executor.getAsyncCount()}")
    println("  Completed: ${executor.getCompletedCount()}")
    
    println("\nParallel Job Runner:")
    println("  Total jobs: ${parallelRunner.getJobCount()}")
    println("  Completed: ${parallelRunner.getCompletedCount()}")
    
    println("\nDetached Task Manager:")
    println("  Total tasks: ${manager.getTaskCount()}")
    println("  Completed: ${manager.getCompletedCount()}")
    
    println("\n⚠️  Structured Concurrency Violation Warning:")
    println("  The code uses GlobalScope to launch coroutines that are not structured:")
    println("  - UnstructuredTaskRunner.runTask() launches tasks in GlobalScope")
    println("  - IndependentCoroutineLauncher.launchCoroutine() launches coroutines in GlobalScope")
    println("  - AsyncTaskExecutor.executeAsync() launches async tasks in GlobalScope")
    println("  - ParallelJobRunner.runParallelJob() launches jobs in GlobalScope")
    println("  - DetachedTaskManager.startDetachedTask() launches tasks in GlobalScope")
    println("  These coroutines are not children of the parent scope and won't be cancelled,")
    println("  leading to resource leaks and uncontrolled coroutine execution.")
    println("  Fix: Use structured concurrency with coroutineScope or launch in parent scope.")
}