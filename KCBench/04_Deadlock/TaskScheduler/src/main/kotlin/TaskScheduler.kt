import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class Task(
    val taskId: String,
    val taskName: String,
    var pending: Boolean = true,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class Worker(
    val workerId: String,
    val workerName: String,
    var busy: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

class TaskScheduler {
    private val tasks = mutableMapOf<String, Task>()
    private val workers = mutableMapOf<String, Worker>()
    private val taskPoolMutex = Mutex()
    private val workerPoolMutex = Mutex()
    
    init {
        initializeTasks()
        initializeWorkers()
    }
    
    private fun initializeTasks() {
        val taskConfigs = listOf(
            Pair("TASK001", "DataProcessing"),
            Pair("TASK002", "FileDownload"),
            Pair("TASK003", "ImageProcessing"),
            Pair("TASK004", "DatabaseQuery"),
            Pair("TASK005", "EmailSending"),
            Pair("TASK006", "ReportGeneration"),
            Pair("TASK007", "DataBackup"),
            Pair("TASK008", "LogAnalysis"),
            Pair("TASK009", "CacheRefresh"),
            Pair("TASK010", "NotificationDispatch")
        )
        
        taskConfigs.forEach { (taskId, taskName) ->
            tasks[taskId] = Task(
                taskId = taskId,
                taskName = taskName,
                pending = true,
                locked = false
            )
        }
    }
    
    private fun initializeWorkers() {
        val workerConfigs = listOf(
            Pair("WRK001", "Worker1"),
            Pair("WRK002", "Worker2"),
            Pair("WRK003", "Worker3"),
            Pair("WRK004", "Worker4"),
            Pair("WRK005", "Worker5"),
            Pair("WRK006", "Worker6"),
            Pair("WRK007", "Worker7"),
            Pair("WRK008", "Worker8"),
            Pair("WRK009", "Worker9"),
            Pair("WRK010", "Worker10")
        )
        
        workerConfigs.forEach { (workerId, workerName) ->
            workers[workerId] = Worker(
                workerId = workerId,
                workerName = workerName,
                busy = false,
                locked = false
            )
        }
    }
    
    suspend fun assignTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        
        if (!task.pending) {
            return false
        }
        
        taskPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (!task.pending) {
                return false
            }
            
            task.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                task.pending = false
                task.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun completeTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        
        if (task.pending) {
            return false
        }
        
        task.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            taskPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                task.pending = true
                task.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun assignWorker(workerId: String): Boolean {
        val worker = workers[workerId] ?: return false
        
        if (worker.busy) {
            return false
        }
        
        workerPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (worker.busy) {
                return false
            }
            
            worker.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                worker.busy = true
                worker.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun releaseWorker(workerId: String): Boolean {
        val worker = workers[workerId] ?: return false
        
        if (!worker.busy) {
            return false
        }
        
        worker.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            workerPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                worker.busy = false
                worker.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun executeTaskWithWorker(
        taskId: String,
        workerId: String
    ): Boolean {
        val task = tasks[taskId] ?: return false
        val worker = workers[workerId] ?: return false
        
        if (task.pending || worker.busy) {
            return false
        }
        
        task.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            worker.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                delay(Random.nextLong(20, 50))
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferTask(
        fromTaskId: String,
        toTaskId: String
    ): Boolean {
        val fromTask = tasks[fromTaskId]
        val toTask = tasks[toTaskId]
        
        if (fromTask == null || toTask == null) {
            return false
        }
        
        if (fromTask.pending || !toTask.pending) {
            return false
        }
        
        fromTask.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toTask.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromTask.pending = true
                fromTask.locked = false
                toTask.pending = false
                toTask.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapTasks(
        taskId1: String,
        taskId2: String
    ): Boolean {
        val task1 = tasks[taskId1]
        val task2 = tasks[taskId2]
        
        if (task1 == null || task2 == null) {
            return false
        }
        
        if (task1.pending || task2.pending) {
            return false
        }
        
        task1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            task2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempPending = task1.pending
                val tempLocked = task1.locked
                
                task1.pending = task2.pending
                task1.locked = task2.locked
                task2.pending = tempPending
                task2.locked = tempLocked
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getTaskStatus(taskId: String): Task? {
        val task = tasks[taskId] ?: return null
        
        return task.mutex.withLock {
            delay(Random.nextLong(5, 15))
            task.copy()
        }
    }
    
    suspend fun getWorkerStatus(workerId: String): Worker? {
        val worker = workers[workerId] ?: return null
        
        return worker.mutex.withLock {
            delay(Random.nextLong(5, 15))
            worker.copy()
        }
    }
    
    fun getAllTasks() = tasks.values.toList()
    fun getAllWorkers() = workers.values.toList()
}

suspend fun simulateTaskAssignment(
    taskScheduler: TaskScheduler,
    schedulerId: Int
) {
    val tasks = taskScheduler.getAllTasks()
    
    repeat(10) { attempt ->
        val task = tasks.filter { it.pending }.randomOrNull()
        
        if (task != null) {
            val success = taskScheduler.assignTask(task.taskId)
            if (success) {
                println("Scheduler $schedulerId: Assigned ${task.taskName}")
            } else {
                println("Scheduler $schedulerId: Failed to assign ${task.taskName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateTaskCompletion(
    taskScheduler: TaskScheduler,
    schedulerId: Int
) {
    val tasks = taskScheduler.getAllTasks()
    
    repeat(10) { attempt ->
        val task = tasks.filter { !it.pending }.randomOrNull()
        
        if (task != null) {
            val success = taskScheduler.completeTask(task.taskId)
            if (success) {
                println("Scheduler $schedulerId: Completed ${task.taskName}")
            } else {
                println("Scheduler $schedulerId: Failed to complete ${task.taskName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateWorkerAssignment(
    taskScheduler: TaskScheduler,
    schedulerId: Int
) {
    val workers = taskScheduler.getAllWorkers()
    
    repeat(8) { attempt ->
        val worker = workers.filter { !it.busy }.randomOrNull()
        
        if (worker != null) {
            val success = taskScheduler.assignWorker(worker.workerId)
            if (success) {
                println("Scheduler $schedulerId: Assigned ${worker.workerName}")
            } else {
                println("Scheduler $schedulerId: Failed to assign ${worker.workerName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateWorkerRelease(
    taskScheduler: TaskScheduler,
    schedulerId: Int
) {
    val workers = taskScheduler.getAllWorkers()
    
    repeat(8) { attempt ->
        val worker = workers.filter { it.busy }.randomOrNull()
        
        if (worker != null) {
            val success = taskScheduler.releaseWorker(worker.workerId)
            if (success) {
                println("Scheduler $schedulerId: Released ${worker.workerName}")
            } else {
                println("Scheduler $schedulerId: Failed to release ${worker.workerName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateTaskExecution(
    taskScheduler: TaskScheduler,
    executorId: Int
) {
    val tasks = taskScheduler.getAllTasks()
    val workers = taskScheduler.getAllWorkers()
    
    repeat(10) { attempt ->
        val task = tasks.filter { !it.pending }.randomOrNull()
        val worker = workers.filter { it.busy }.randomOrNull()
        
        if (task != null && worker != null) {
            val success = taskScheduler.executeTaskWithWorker(
                task.taskId,
                worker.workerId
            )
            
            if (success) {
                println("Executor $executorId: Executed ${task.taskName} with ${worker.workerName}")
            } else {
                println("Executor $executorId: Failed to execute task")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateTaskTransfer(
    taskScheduler: TaskScheduler,
    transferId: Int
) {
    val tasks = taskScheduler.getAllTasks()
    
    repeat(6) { attempt ->
        val executingTasks = tasks.filter { !it.pending }
        val pendingTasks = tasks.filter { it.pending }
        
        if (executingTasks.isNotEmpty() && pendingTasks.isNotEmpty()) {
            val fromTask = executingTasks.random()
            val toTask = pendingTasks.random()
            
            val success = taskScheduler.transferTask(fromTask.taskId, toTask.taskId)
            
            if (success) {
                println("Transfer $transferId: ${fromTask.taskName} -> ${toTask.taskName}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateTaskSwap(
    taskScheduler: TaskScheduler,
    swapId: Int
) {
    val tasks = taskScheduler.getAllTasks()
    
    repeat(5) { attempt ->
        val executingTasks = tasks.filter { !it.pending }
        
        if (executingTasks.size >= 2) {
            val task1 = executingTasks.random()
            val task2 = executingTasks.filter { it.taskId != task1.taskId }.random()
            
            val success = taskScheduler.swapTasks(task1.taskId, task2.taskId)
            
            if (success) {
                println("Swap $swapId: ${task1.taskName} <-> ${task2.taskName}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorTaskScheduler(
    taskScheduler: TaskScheduler,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val tasks = taskScheduler.getAllTasks()
        val workers = taskScheduler.getAllWorkers()
        
        val pending = tasks.count { it.pending }
        val locked = tasks.count { it.locked }
        val busy = workers.count { it.busy }
        val lockedWorkers = workers.count { it.locked }
        
        println("Monitor $monitorId: Pending=$pending, Locked=$locked, " +
                "Busy=$busy, LockedWorkers=$lockedWorkers")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val taskScheduler = TaskScheduler()
    
    println("Starting Task Scheduler Simulation...")
    println("Initial Task Status:")
    taskScheduler.getAllTasks().forEach { task ->
        println("  ${task.taskId} (${task.taskName}): Pending=${task.pending}, Locked=${task.locked}")
    }
    println()
    
    println("Initial Worker Status:")
    taskScheduler.getAllWorkers().forEach { worker ->
        println("  ${worker.workerId} (${worker.workerName}): Busy=${worker.busy}, Locked=${worker.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateTaskAssignment(taskScheduler, 1)
    })
    
    jobs.add(launch {
        simulateTaskAssignment(taskScheduler, 2)
    })
    
    jobs.add(launch {
        simulateTaskCompletion(taskScheduler, 1)
    })
    
    jobs.add(launch {
        simulateTaskCompletion(taskScheduler, 2)
    })
    
    jobs.add(launch {
        simulateWorkerAssignment(taskScheduler, 1)
    })
    
    jobs.add(launch {
        simulateWorkerRelease(taskScheduler, 1)
    })
    
    jobs.add(launch {
        simulateTaskExecution(taskScheduler, 1)
    })
    
    jobs.add(launch {
        simulateTaskTransfer(taskScheduler, 1)
    })
    
    jobs.add(launch {
        simulateTaskSwap(taskScheduler, 1)
    })
    
    jobs.add(launch {
        monitorTaskScheduler(taskScheduler, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val tasks = taskScheduler.getAllTasks()
    val workers = taskScheduler.getAllWorkers()
    
    println("\n=== Final Task Status ===")
    tasks.forEach { task ->
        println("  ${task.taskId} (${task.taskName}): Pending=${task.pending}, Locked=${task.locked}")
    }
    
    println("\n=== Final Worker Status ===")
    workers.forEach { worker ->
        println("  ${worker.workerId} (${worker.workerName}): Busy=${worker.busy}, Locked=${worker.locked}")
    }
    
    val pending = tasks.count { it.pending }
    val locked = tasks.count { it.locked }
    val busy = workers.count { it.busy }
    val lockedWorkers = workers.count { it.locked }
    
    println("\nPending Tasks: $pending/${tasks.size}")
    println("Locked Tasks: $locked/${tasks.size}")
    println("Busy Workers: $busy/${workers.size}")
    println("Locked Workers: $lockedWorkers/${workers.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - assignTask(): taskPoolMutex -> task.mutex")
    println("  - completeTask(): task.mutex -> taskPoolMutex")
    println("  - assignWorker(): workerPoolMutex -> worker.mutex")
    println("  - releaseWorker(): worker.mutex -> workerPoolMutex")
    println("  - executeTaskWithWorker(): task.mutex -> worker.mutex")
    println("  - transferTask(): task1.mutex -> task2.mutex")
    println("  - swapTasks(): task1.mutex -> task2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}