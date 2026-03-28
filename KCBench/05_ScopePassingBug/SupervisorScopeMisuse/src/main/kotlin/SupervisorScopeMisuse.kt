import kotlinx.coroutines.*
import kotlin.random.Random

class TaskCoordinator {
    private val tasks = mutableMapOf<String, Job>()
    private var taskCount = 0
    
    fun startTask(taskName: String, scope: CoroutineScope) {
        val job = scope.launch {
            executeTask(taskName)
        }
        tasks[taskName] = job
        taskCount++
        println("Started task: $taskName (Total: $taskCount)")
    }
    
    fun startMultipleTasks(taskNames: List<String>, scope: CoroutineScope) {
        taskNames.forEach { taskName ->
            startTask(taskName, scope)
        }
    }
    
    private suspend fun executeTask(taskName: String) {
        repeat(10) { iteration ->
            delay(Random.nextLong(100, 300))
            println("$taskName: Iteration $iteration")
        }
        println("$taskName: Completed")
    }
    
    fun cancelTask(taskName: String) {
        tasks[taskName]?.cancel()
        tasks.remove(taskName)
    }
    
    fun cancelAllTasks() {
        tasks.values.forEach { it.cancel() }
        tasks.clear()
    }
    
    fun getTaskCount() = taskCount
    fun getActiveTasks() = tasks.size
}

class SupervisorTaskManager {
    private val tasks = mutableMapOf<String, Job>()
    private var taskCount = 0
    
    fun startTaskWithSupervisor(taskName: String, scope: CoroutineScope) {
        scope.launch {
            supervisorScope {
                launch {
                    executeTask(taskName)
                }
            }
        }
        taskCount++
        println("Started supervised task: $taskName (Total: $taskCount)")
    }
    
    fun startMultipleTasksWithSupervisor(taskNames: List<String>, scope: CoroutineScope) {
        taskNames.forEach { taskName ->
            startTaskWithSupervisor(taskName, scope)
        }
    }
    
    private suspend fun executeTask(taskName: String) {
        repeat(10) { iteration ->
            delay(Random.nextLong(100, 300))
            println("$taskName: Iteration $iteration")
        }
        println("$taskName: Completed")
    }
    
    fun getTaskCount() = taskCount
}

class ChildTaskManager {
    private val childTasks = mutableMapOf<String, Job>()
    private var childCount = 0
    
    fun startChildTask(parentTaskName: String, childTaskName: String, scope: CoroutineScope) {
        scope.launch {
            supervisorScope {
                launch {
                    executeChildTask(parentTaskName, childTaskName)
                }
            }
        }
        childCount++
        println("Started child task: $childTaskName for $parentTaskName (Total: $childCount)")
    }
    
    fun startMultipleChildTasks(parentTaskName: String, childTaskNames: List<String>, scope: CoroutineScope) {
        childTaskNames.forEach { childTaskName ->
            startChildTask(parentTaskName, childTaskName, scope)
        }
    }
    
    private suspend fun executeChildTask(parentTaskName: String, childTaskName: String) {
        repeat(8) { iteration ->
            delay(Random.nextLong(100, 250))
            println("$parentTaskName/$childTaskName: Iteration $iteration")
        }
        println("$parentTaskName/$childTaskName: Completed")
    }
    
    fun getChildCount() = childCount
}

class IndependentTaskRunner {
    private val tasks = mutableMapOf<String, Job>()
    private var taskCount = 0
    
    fun runIndependentTask(taskName: String, scope: CoroutineScope) {
        scope.launch {
            supervisorScope {
                launch {
                    runTask(taskName)
                }
            }
        }
        taskCount++
        println("Started independent task: $taskName (Total: $taskCount)")
    }
    
    fun runMultipleIndependentTasks(taskNames: List<String>, scope: CoroutineScope) {
        taskNames.forEach { taskName ->
            runIndependentTask(taskName, scope)
        }
    }
    
    private suspend fun runTask(taskName: String) {
        repeat(12) { iteration ->
            delay(Random.nextLong(100, 350))
            println("$taskName: Iteration $iteration")
        }
        println("$taskName: Completed")
    }
    
    fun getTaskCount() = taskCount
}

class ParallelTaskExecutor {
    private val tasks = mutableMapOf<String, Job>()
    private var taskCount = 0
    
    fun executeParallelTasks(taskNames: List<String>, scope: CoroutineScope) {
        scope.launch {
            supervisorScope {
                taskNames.forEach { taskName ->
                    launch {
                        executeTask(taskName)
                    }
                }
            }
        }
        taskCount += taskNames.size
        println("Started ${taskNames.size} parallel tasks (Total: $taskCount)")
    }
    
    private suspend fun executeTask(taskName: String) {
        repeat(10) { iteration ->
            delay(Random.nextLong(100, 300))
            println("$taskName: Iteration $iteration")
        }
        println("$taskName: Completed")
    }
    
    fun getTaskCount() = taskCount
}

suspend fun simulateTaskCoordinator(
    coordinator: TaskCoordinator,
    scope: CoroutineScope
) {
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    println("Starting tasks with coordinator...")
    coordinator.startMultipleTasks(taskNames, scope)
    
    delay(3000)
    
    println("\nTask Coordinator Summary:")
    println("  Total tasks: ${coordinator.getTaskCount()}")
    println("  Active tasks: ${coordinator.getActiveTasks()}")
}

suspend fun simulateSupervisorTaskManager(
    manager: SupervisorTaskManager,
    scope: CoroutineScope
) {
    val taskNames = listOf(
        "SupervisedTask1", "SupervisedTask2", "SupervisedTask3", "SupervisedTask4", "SupervisedTask5"
    )
    
    println("Starting tasks with supervisor...")
    manager.startMultipleTasksWithSupervisor(taskNames, scope)
    
    delay(3000)
    
    println("\nSupervisor Task Manager Summary:")
    println("  Total tasks: ${manager.getTaskCount()}")
}

suspend fun simulateChildTaskManager(
    manager: ChildTaskManager,
    scope: CoroutineScope
) {
    val parentTaskName = "ParentTask"
    val childTaskNames = listOf(
        "Child1", "Child2", "Child3", "Child4", "Child5"
    )
    
    println("Starting child tasks...")
    manager.startMultipleChildTasks(parentTaskName, childTaskNames, scope)
    
    delay(2500)
    
    println("\nChild Task Manager Summary:")
    println("  Total child tasks: ${manager.getChildCount()}")
}

suspend fun simulateIndependentTaskRunner(
    runner: IndependentTaskRunner,
    scope: CoroutineScope
) {
    val taskNames = listOf(
        "Independent1", "Independent2", "Independent3", "Independent4", "Independent5"
    )
    
    println("Starting independent tasks...")
    runner.runMultipleIndependentTasks(taskNames, scope)
    
    delay(3500)
    
    println("\nIndependent Task Runner Summary:")
    println("  Total tasks: ${runner.getTaskCount()}")
}

suspend fun simulateParallelTaskExecutor(
    executor: ParallelTaskExecutor,
    scope: CoroutineScope
) {
    val taskNames = listOf(
        "Parallel1", "Parallel2", "Parallel3", "Parallel4", "Parallel5"
    )
    
    println("Starting parallel tasks...")
    executor.executeParallelTasks(taskNames, scope)
    
    delay(3000)
    
    println("\nParallel Task Executor Summary:")
    println("  Total tasks: ${executor.getTaskCount()}")
}

fun main() = runBlocking {
    println("Starting SupervisorScope Misuse Simulation...")
    println()
    
    val coordinator = TaskCoordinator()
    val supervisorManager = SupervisorTaskManager()
    val childManager = ChildTaskManager()
    val independentRunner = IndependentTaskRunner()
    val parallelExecutor = ParallelTaskExecutor()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateTaskCoordinator(coordinator, this)
    })
    
    jobs.add(launch {
        simulateSupervisorTaskManager(supervisorManager, this)
    })
    
    jobs.add(launch {
        simulateChildTaskManager(childManager, this)
    })
    
    jobs.add(launch {
        simulateIndependentTaskRunner(independentRunner, this)
    })
    
    jobs.add(launch {
        simulateParallelTaskExecutor(parallelExecutor, this)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Task Coordinator:")
    println("  Total tasks: ${coordinator.getTaskCount()}")
    println("  Active tasks: ${coordinator.getActiveTasks()}")
    
    println("\nSupervisor Task Manager:")
    println("  Total tasks: ${supervisorManager.getTaskCount()}")
    
    println("\nChild Task Manager:")
    println("  Total child tasks: ${childManager.getChildCount()}")
    
    println("\nIndependent Task Runner:")
    println("  Total tasks: ${independentRunner.getTaskCount()}")
    
    println("\nParallel Task Executor:")
    println("  Total tasks: ${parallelExecutor.getTaskCount()}")
    
    println("\n⚠️  SupervisorScope Misuse Warning:")
    println("  The code uses supervisorScope incorrectly:")
    println("  - TaskCoordinator.startTask() launches tasks in provided scope")
    println("  - SupervisorTaskManager.startTaskWithSupervisor() wraps tasks in supervisorScope")
    println("  - ChildTaskManager.startChildTask() uses supervisorScope for child tasks")
    println("  - IndependentTaskRunner.runIndependentTask() uses supervisorScope for independent tasks")
    println("  - ParallelTaskExecutor.executeParallelTasks() uses supervisorScope for parallel tasks")
    println("  Using supervisorScope inside launch defeats the purpose of structured concurrency,")
    println("  as it creates a new scope that is not properly managed by the parent.")
    println("  Fix: Use supervisorScope only at the top level or when you specifically need")
    println("  child task failures to not cancel the parent scope.")
}