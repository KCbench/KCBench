import kotlinx.coroutines.*
import kotlin.random.Random

data class Task(
    val id: Int,
    val title: String,
    var status: TaskStatus,
    var priority: Int,
    var assignedTo: String? = null
)

enum class TaskStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}

class TaskManager {
    private val tasks = mutableMapOf<Int, Task>()
    private var nextTaskId = 1
    private var completedCount = 0
    private var cancelledCount = 0
    
    init {
        GlobalScope.launch {
            initializeDefaultTasks()
        }
    }
    
    private suspend fun initializeDefaultTasks() {
        val taskTitles = listOf(
            "Design System", "API Integration", "Database Schema",
            "User Authentication", "Payment Gateway", "Dashboard",
            "Reports Module", "Notification System", "File Upload",
            "Search Functionality"
        )
        
        taskTitles.forEach { title ->
            createTask(title, Random.nextInt(1, 6))
        }
    }
    
    suspend fun createTask(title: String, priority: Int): Task {
        val taskId = nextTaskId
        val currentNextId = nextTaskId
        delay(Random.nextLong(1, 5))
        nextTaskId = currentNextId + 1
        
        val task = Task(
            id = taskId,
            title = title,
            status = TaskStatus.PENDING,
            priority = priority
        )
        
        tasks[taskId] = task
        delay(Random.nextLong(1, 5))
        
        return task
    }
    
    suspend fun assignTask(taskId: Int, assignee: String): Boolean {
        val task = tasks[taskId] ?: return false
        
        val currentStatus = task.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == TaskStatus.PENDING) {
            task.status = TaskStatus.IN_PROGRESS
            delay(Random.nextLong(1, 5))
            
            task.assignedTo = assignee
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun completeTask(taskId: Int): Boolean {
        val task = tasks[taskId] ?: return false
        
        val currentStatus = task.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == TaskStatus.IN_PROGRESS) {
            task.status = TaskStatus.COMPLETED
            delay(Random.nextLong(1, 5))
            
            val currentCount = completedCount
            delay(Random.nextLong(1, 5))
            completedCount = currentCount + 1
            
            return true
        }
        
        return false
    }
    
    suspend fun cancelTask(taskId: Int): Boolean {
        val task = tasks[taskId] ?: return false
        
        val currentStatus = task.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus != TaskStatus.COMPLETED) {
            task.status = TaskStatus.CANCELLED
            delay(Random.nextLong(1, 5))
            
            val currentCount = cancelledCount
            delay(Random.nextLong(1, 5))
            cancelledCount = currentCount + 1
            
            return true
        }
        
        return false
    }
    
    suspend fun updateTaskPriority(taskId: Int, newPriority: Int): Boolean {
        val task = tasks[taskId] ?: return false
        
        val currentStatus = task.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == TaskStatus.PENDING) {
            task.priority = newPriority
            delay(Random.nextLong(1, 5))
            return true
        }
        
        return false
    }
    
    fun getTask(taskId: Int): Task? = tasks[taskId]
    
    fun getAllTasks() = tasks.values.toList()
    
    fun getTasksByStatus(status: TaskStatus): List<Task> {
        return tasks.values.filter { it.status == status }
    }
    
    fun getStatistics(): Triple<Int, Int, Int> {
        return Triple(
            completedCount,
            cancelledCount,
            tasks.size
        )
    }
}

class TaskWorker(
    private val taskManager: TaskManager,
    private val workerName: String
) {
    suspend fun workOnTasks() {
        repeat(10) { attempt ->
            val pendingTasks = taskManager.getTasksByStatus(TaskStatus.PENDING)
            
            if (pendingTasks.isNotEmpty()) {
                val task = pendingTasks.random()
                
                if (taskManager.assignTask(task.id, workerName)) {
                    delay(Random.nextLong(50, 200))
                    
                    if (Random.nextBoolean()) {
                        taskManager.completeTask(task.id)
                    } else {
                        taskManager.cancelTask(task.id)
                    }
                }
            }
            
            delay(Random.nextLong(10, 50))
        }
    }
}

suspend fun simulateTaskCreation(
    taskManager: TaskManager
) {
    repeat(20) { attempt ->
        val titles = listOf(
            "Bug Fix", "Feature Request", "Refactoring",
            "Documentation", "Testing", "Code Review",
            "Performance Optimization", "Security Update"
        )
        
        taskManager.createTask(
            titles.random(),
            Random.nextInt(1, 6)
        )
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulatePriorityUpdates(
    taskManager: TaskManager
) {
    repeat(15) { attempt ->
        val tasks = taskManager.getTasksByStatus(TaskStatus.PENDING)
        
        if (tasks.isNotEmpty()) {
            val task = tasks.random()
            taskManager.updateTaskPriority(
                task.id,
                Random.nextInt(1, 6)
            )
        }
        
        delay(Random.nextLong(50, 150))
    }
}

fun main() = runBlocking {
    val taskManager = TaskManager()
    delay(100)
    val workers = listOf(
        TaskWorker(taskManager, "Alice"),
        TaskWorker(taskManager, "Bob"),
        TaskWorker(taskManager, "Charlie"),
        TaskWorker(taskManager, "David")
    )
    
    println("Starting Task Management Simulation...")
    println("Initial Tasks:")
    taskManager.getAllTasks().forEach { task ->
        println("  ${task.id}: ${task.title} [${task.status}] Priority: ${task.priority}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateTaskCreation(taskManager)
    })
    
    jobs.add(launch {
        simulatePriorityUpdates(taskManager)
    })
    
    workers.forEach { worker ->
        jobs.add(launch {
            worker.workOnTasks()
        })
    }
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Task Status ===")
    taskManager.getAllTasks().forEach { task ->
        println(
            "  ${task.id}: ${task.title} [${task.status}] " +
            "Priority: ${task.priority} Assigned: ${task.assignedTo ?: "None"}"
        )
    }
    
    val (completed, cancelled, total) = taskManager.getStatistics()
    val pending = taskManager.getTasksByStatus(TaskStatus.PENDING).size
    val inProgress = taskManager.getTasksByStatus(TaskStatus.IN_PROGRESS).size
    
    println("\n=== Statistics ===")
    println("Total Tasks: $total")
    println("Completed: $completed")
    println("Cancelled: $cancelled")
    println("Pending: $pending")
    println("In Progress: $inProgress")
    
    val assignedToMultiple = taskManager.getAllTasks()
        .filter { it.assignedTo != null }
        .groupBy { it.assignedTo }
        .filter { (_, tasks) -> tasks.size > 10 }
    
    if (assignedToMultiple.isNotEmpty()) {
        println("\n⚠️  Workers with many assignments:")
        assignedToMultiple.forEach { (worker, tasks) ->
            println("  $worker: ${tasks.size} tasks")
        }
    }
}