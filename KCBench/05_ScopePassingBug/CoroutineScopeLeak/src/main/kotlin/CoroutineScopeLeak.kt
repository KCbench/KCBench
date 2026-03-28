import kotlinx.coroutines.*
import kotlin.random.Random

class ResourceHolder {
    private var resourceCount = 0
    private val resources = mutableListOf<Resource>()
    
    fun acquireResource(scope: CoroutineScope) {
        scope.launch {
            val resource = Resource("Resource${resourceCount++}")
            resources.add(resource)
            println("Acquired: ${resource.name}")
            
            delay(Random.nextLong(1000, 3000))
            
            println("Releasing: ${resource.name}")
            resources.remove(resource)
        }
    }
    
    fun acquireMultipleResources(count: Int, scope: CoroutineScope) {
        repeat(count) {
            acquireResource(scope)
        }
    }
    
    fun getResourceCount() = resourceCount
    fun getActiveResources() = resources.size
}

data class Resource(
    val name: String
)

class ConnectionPool {
    private var connectionCount = 0
    private val connections = mutableListOf<Connection>()
    
    fun createConnection(scope: CoroutineScope) {
        scope.launch {
            val connection = Connection("Connection${connectionCount++}")
            connections.add(connection)
            println("Created: ${connection.name}")
            
            delay(Random.nextLong(1500, 4000))
            
            println("Closing: ${connection.name}")
            connections.remove(connection)
        }
    }
    
    fun createMultipleConnections(count: Int, scope: CoroutineScope) {
        repeat(count) {
            createConnection(scope)
        }
    }
    
    fun getConnectionCount() = connectionCount
    fun getActiveConnections() = connections.size
}

data class Connection(
    val name: String
)

class TaskExecutor {
    private var taskCount = 0
    private val activeTasks = mutableListOf<String>()
    
    fun executeTask(taskName: String, scope: CoroutineScope) {
        scope.launch {
            activeTasks.add(taskName)
            println("Started: $taskName")
            
            delay(Random.nextLong(500, 2000))
            
            println("Completed: $taskName")
            activeTasks.remove(taskName)
        }
    }
    
    fun executeMultipleTasks(taskNames: List<String>, scope: CoroutineScope) {
        taskNames.forEach { taskName ->
            executeTask(taskName, scope)
        }
    }
    
    fun getTaskCount() = taskCount
    fun getActiveTasks() = activeTasks.size
}

class DataProcessor {
    private var processingCount = 0
    private val activeProcesses = mutableListOf<String>()
    
    fun processData(dataId: String, scope: CoroutineScope) {
        scope.launch {
            activeProcesses.add(dataId)
            println("Processing: $dataId")
            
            delay(Random.nextLong(800, 2500))
            
            println("Processed: $dataId")
            activeProcesses.remove(dataId)
        }
    }
    
    fun processMultipleData(dataIds: List<String>, scope: CoroutineScope) {
        dataIds.forEach { dataId ->
            processData(dataId, scope)
        }
    }
    
    fun getProcessingCount() = processingCount
    fun getActiveProcesses() = activeProcesses.size
}

class EventListener {
    private var listenerCount = 0
    private val activeListeners = mutableListOf<String>()
    
    fun listenToEvent(eventType: String, scope: CoroutineScope) {
        scope.launch {
            activeListeners.add(eventType)
            println("Listening to: $eventType")
            
            repeat(5) { iteration ->
                delay(Random.nextLong(200, 500))
                println("$eventType event #$iteration")
            }
            
            println("Stopped listening to: $eventType")
            activeListeners.remove(eventType)
        }
    }
    
    fun listenToMultipleEvents(eventTypes: List<String>, scope: CoroutineScope) {
        eventTypes.forEach { eventType ->
            listenToEvent(eventType, scope)
        }
    }
    
    fun getListenerCount() = listenerCount
    fun getActiveListeners() = activeListeners.size
}

suspend fun simulateResourceHolder(
    holder: ResourceHolder,
    scope: CoroutineScope
) {
    println("Acquiring resources...")
    holder.acquireMultipleResources(5, scope)
    
    delay(2000)
    
    println("\nResource Holder Summary:")
    println("  Total resources: ${holder.getResourceCount()}")
    println("  Active resources: ${holder.getActiveResources()}")
}

suspend fun simulateConnectionPool(
    pool: ConnectionPool,
    scope: CoroutineScope
) {
    println("Creating connections...")
    pool.createMultipleConnections(5, scope)
    
    delay(3000)
    
    println("\nConnection Pool Summary:")
    println("  Total connections: ${pool.getConnectionCount()}")
    println("  Active connections: ${pool.getActiveConnections()}")
}

suspend fun simulateTaskExecutor(
    executor: TaskExecutor,
    scope: CoroutineScope
) {
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    println("Executing tasks...")
    executor.executeMultipleTasks(taskNames, scope)
    
    delay(2000)
    
    println("\nTask Executor Summary:")
    println("  Total tasks: ${executor.getTaskCount()}")
    println("  Active tasks: ${executor.getActiveTasks()}")
}

suspend fun simulateDataProcessor(
    processor: DataProcessor,
    scope: CoroutineScope
) {
    val dataIds = listOf(
        "Data1", "Data2", "Data3", "Data4", "Data5"
    )
    
    println("Processing data...")
    processor.processMultipleData(dataIds, scope)
    
    delay(2500)
    
    println("\nData Processor Summary:")
    println("  Total processing: ${processor.getProcessingCount()}")
    println("  Active processes: ${processor.getActiveProcesses()}")
}

suspend fun simulateEventListener(
    listener: EventListener,
    scope: CoroutineScope
) {
    val eventTypes = listOf(
        "Click", "Scroll", "KeyPress", "MouseMove", "Resize"
    )
    
    println("Listening to events...")
    listener.listenToMultipleEvents(eventTypes, scope)
    
    delay(2000)
    
    println("\nEvent Listener Summary:")
    println("  Total listeners: ${listener.getListenerCount()}")
    println("  Active listeners: ${listener.getActiveListeners()}")
}

fun main() = runBlocking {
    println("Starting CoroutineScope Leak Simulation...")
    println()
    
    val holder = ResourceHolder()
    val pool = ConnectionPool()
    val executor = TaskExecutor()
    val processor = DataProcessor()
    val listener = EventListener()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateResourceHolder(holder, this)
    })
    
    jobs.add(launch {
        simulateConnectionPool(pool, this)
    })
    
    jobs.add(launch {
        simulateTaskExecutor(executor, this)
    })
    
    jobs.add(launch {
        simulateDataProcessor(processor, this)
    })
    
    jobs.add(launch {
        simulateEventListener(listener, this)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Resource Holder:")
    println("  Total resources: ${holder.getResourceCount()}")
    println("  Active resources: ${holder.getActiveResources()}")
    
    println("\nConnection Pool:")
    println("  Total connections: ${pool.getConnectionCount()}")
    println("  Active connections: ${pool.getActiveConnections()}")
    
    println("\nTask Executor:")
    println("  Total tasks: ${executor.getTaskCount()}")
    println("  Active tasks: ${executor.getActiveTasks()}")
    
    println("\nData Processor:")
    println("  Total processing: ${processor.getProcessingCount()}")
    println("  Active processes: ${processor.getActiveProcesses()}")
    
    println("\nEvent Listener:")
    println("  Total listeners: ${listener.getListenerCount()}")
    println("  Active listeners: ${listener.getActiveListeners()}")
    
    println("\n⚠️  CoroutineScope Leak Warning:")
    println("  The code launches coroutines in provided scopes that may be cancelled:")
    println("  - ResourceHolder.acquireResource(scope)")
    println("  - ConnectionPool.createConnection(scope)")
    println("  - TaskExecutor.executeTask(taskName, scope)")
    println("  - DataProcessor.processData(dataId, scope)")
    println("  - EventListener.listenToEvent(eventType, scope)")
    println("  When the provided scope is cancelled, these coroutines may be cancelled mid-operation,")
    println("  leading to resource leaks and inconsistent state.")
    println("  Fix: Use structured concurrency and ensure coroutines complete before scope cancellation.")
}