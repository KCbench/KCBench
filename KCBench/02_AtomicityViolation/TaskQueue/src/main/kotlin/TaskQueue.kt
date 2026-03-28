import kotlinx.coroutines.*
import kotlin.random.Random

data class Task(
    val taskId: String,
    val name: String,
    var status: TaskStatus,
    var priority: Int,
    var assignedTo: String? = null
)

enum class TaskStatus {
    PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, FAILED
}

class TaskQueue {
    private val tasks = mutableMapOf<String, Task>()
    private var totalTasks = 0
    private var completedTasks = 0
    private var failedTasks = 0
    
    init {
        initializeTasks()
    }
    
    private fun initializeTasks() {
        val taskNames = listOf(
            "Data Processing", "File Upload", "Email Send",
            "Report Generation", "Backup", "Sync",
            "Cache Clear", "Index Update", "Log Analysis"
        )
        
        taskNames.forEach { name ->
            val task = Task(
                taskId = "TASK_${Random.nextInt(1000, 9999)}",
                name = name,
                status = TaskStatus.PENDING,
                priority = Random.nextInt(1, 6)
            )
            tasks[task.taskId] = task
        }
    }
    
    suspend fun assignTask(
        taskId: String,
        worker: String
    ): Boolean {
        val task = tasks[taskId] ?: return false
        
        if (task.status == TaskStatus.PENDING) {
            delay(Random.nextLong(1, 10))
            
            task.status = TaskStatus.ASSIGNED
            task.assignedTo = worker
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun startTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        
        if (task.status == TaskStatus.ASSIGNED) {
            delay(Random.nextLong(1, 10))
            
            task.status = TaskStatus.IN_PROGRESS
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun completeTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        
        if (task.status == TaskStatus.IN_PROGRESS) {
            delay(Random.nextLong(1, 10))
            
            task.status = TaskStatus.COMPLETED
            delay(Random.nextLong(1, 5))
            
            val currentCompleted = completedTasks
            delay(Random.nextLong(1, 5))
            completedTasks = currentCompleted + 1
            
            return true
        }
        
        return false
    }
    
    suspend fun failTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        
        if (task.status == TaskStatus.IN_PROGRESS) {
            delay(Random.nextLong(1, 10))
            
            task.status = TaskStatus.FAILED
            delay(Random.nextLong(1, 5))
            
            val currentFailed = failedTasks
            delay(Random.nextLong(1, 5))
            failedTasks = currentFailed + 1
            
            return true
        }
        
        return false
    }
    
    suspend fun addTask(name: String, priority: Int): Task {
        val taskId = "TASK_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        
        val task = Task(
            taskId = taskId,
            name = name,
            status = TaskStatus.PENDING,
            priority = priority
        )
        
        tasks[taskId] = task
        delay(Random.nextLong(1, 5))
        
        val currentTotal = totalTasks
        delay(Random.nextLong(1, 5))
        totalTasks = currentTotal + 1
        
        return task
    }
    
    suspend fun assignMultipleTasks(
        worker: String,
        count: Int
    ): Int {
        var assigned = 0
        
        val pendingTasks = tasks.values.filter { 
            it.status == TaskStatus.PENDING 
        }.shuffled()
        
        pendingTasks.take(count).forEach { task ->
            if (assignTask(task.taskId, worker)) {
                assigned++
            }
        }
        
        return assigned
    }
    
    fun getPendingTasks(): List<Task> {
        return tasks.values.filter { it.status == TaskStatus.PENDING }
    }
    
    fun getAssignedTasks(worker: String): List<Task> {
        return tasks.values.filter { 
            it.status == TaskStatus.ASSIGNED && 
            it.assignedTo == worker 
        }
    }
    
    fun getAllTasks() = tasks.values.toList()
    
    fun getStatistics(): Triple<Int, Int, Int> {
        return Triple(totalTasks, completedTasks, failedTasks)
    }
}

class TaskWorker(
    private val taskQueue: TaskQueue,
    private val workerName: String
) {
    suspend fun workOnTasks() {
        repeat(15) { attempt ->
            val assigned = taskQueue.assignMultipleTasks(workerName, 2)
            
            if (assigned > 0) {
                val myTasks = taskQueue.getAssignedTasks(workerName)
                
                myTasks.forEach { task ->
                    if (taskQueue.startTask(task.taskId)) {
                        delay(Random.nextLong(50, 200))
                        
                        if (Random.nextDouble() < 0.1) {
                            taskQueue.failTask(task.taskId)
                        } else {
                            taskQueue.completeTask(task.taskId)
                        }
                    }
                }
            }
            
            delay(Random.nextLong(20, 80))
        }
    }
}

suspend fun simulateTaskCreation(
    taskQueue: TaskQueue
) {
    repeat(20) { attempt ->
        val taskNames = listOf(
            "Data Processing", "File Upload", "Email Send",
            "Report Generation", "Backup", "Sync",
            "Cache Clear", "Index Update", "Log Analysis"
        )
        
        taskQueue.addTask(
            taskNames.random(),
            Random.nextInt(1, 6)
        )
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateTaskMonitoring(
    taskQueue: TaskQueue
) {
    repeat(15) { attempt ->
        val pendingTasks = taskQueue.getPendingTasks()
        
        println("Pending tasks: ${pendingTasks.size}")
        
        val assignedTasks = taskQueue.getAllTasks()
            .filter { it.status == TaskStatus.ASSIGNED }
        
        println("Assigned tasks: ${assignedTasks.size}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val taskQueue = TaskQueue()
    
    println("Starting Task Queue Simulation...")
    println("Initial Pending Tasks: ${taskQueue.getPendingTasks().size}")
    println()
    
    val jobs = mutableListOf<Job>()
    
    val workers = listOf(
        TaskWorker(taskQueue, "Alice"),
        TaskWorker(taskQueue, "Bob"),
        TaskWorker(taskQueue, "Charlie"),
        TaskWorker(taskQueue, "David"),
        TaskWorker(taskQueue, "Eve")
    )
    
    workers.forEach { worker ->
        jobs.add(launch {
            worker.workOnTasks()
        })
    }
    
    jobs.add(launch {
        simulateTaskCreation(taskQueue)
    })
    
    jobs.add(launch {
        simulateTaskMonitoring(taskQueue)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (totalTasks, completedTasks, failedTasks) = taskQueue.getStatistics()
    
    println("\n=== Task Queue Statistics ===")
    println("Total Tasks: $totalTasks")
    println("Completed Tasks: $completedTasks")
    println("Failed Tasks: $failedTasks")
    
    val pending = taskQueue.getPendingTasks().size
    val assigned = taskQueue.getAllTasks().count { it.status == TaskStatus.ASSIGNED }
    val inProgress = taskQueue.getAllTasks().count { it.status == TaskStatus.IN_PROGRESS }
    
    println("\nTask Status:")
    println("  Pending: $pending")
    println("  Assigned: $assigned")
    println("  In Progress: $inProgress")
    
    val doubleAssigned = taskQueue.getAllTasks()
        .filter { it.status == TaskStatus.ASSIGNED }
    
    if (doubleAssigned.size > 10) {
        println("\n⚠️  Many assigned tasks: ${doubleAssigned.size}")
    }
    
    val completionRate = if (totalTasks > 0) {
        (completedTasks.toDouble() / totalTasks * 100)
    } else {
        0.0
    }
    
    println("\nCompletion Rate: ${"%.2f".format(completionRate)}%")
}