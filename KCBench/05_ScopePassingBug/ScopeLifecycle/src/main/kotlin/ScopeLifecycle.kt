import kotlinx.coroutines.*
import kotlin.random.Random

class LifecycleAwareComponent {
    private var isAlive = true
    private var initialized = false
    private var destroyed = false
    private val jobs = mutableListOf<Job>()
    private var jobCount = 0
    
    fun initialize() {
        if (initialized) {
            return
        }
        
        initialized = true
        println("LifecycleAwareComponent initialized")
    }
    
    fun startBackgroundTask(taskName: String) {
        if (!isAlive) {
            println("Component is not alive, cannot start task: $taskName")
            return
        }
        
        val job = GlobalScope.launch {
            executeTask(taskName)
        }
        jobs.add(job)
        jobCount++
        println("Started background task: $taskName (Total: $jobCount)")
    }
    
    fun startMultipleBackgroundTasks(taskNames: List<String>) {
        taskNames.forEach { taskName ->
            startBackgroundTask(taskName)
        }
    }
    
    private suspend fun executeTask(taskName: String) {
        repeat(15) { iteration ->
            if (!isAlive) {
                println("Task $taskName cancelled at iteration $iteration")
                return
            }
            
            delay(Random.nextLong(100, 300))
            println("$taskName: Iteration $iteration")
        }
        
        println("$taskName: Completed")
    }
    
    fun destroy() {
        if (destroyed) {
            return
        }
        
        isAlive = false
        destroyed = true
        
        jobs.forEach { it.cancel() }
        jobs.clear()
        
        println("LifecycleAwareComponent destroyed")
    }
    
    fun isAlive() = isAlive
    fun isInitialized() = initialized
    fun isDestroyed() = destroyed
    fun getJobCount() = jobCount
}

class ViewModelComponent {
    private var isActive = true
    private var onClearedCalled = false
    private val jobs = mutableListOf<Job>()
    private var requestCount = 0
    
    fun loadData() {
        if (!isActive) {
            println("ViewModel is not active, cannot load data")
            return
        }
        
        val job = GlobalScope.launch {
            performDataLoad()
        }
        jobs.add(job)
        requestCount++
        println("Started data load (Total: $requestCount)")
    }
    
    fun loadMultipleDataSources(sources: List<String>) {
        sources.forEach { source ->
            loadDataSource(source)
        }
    }
    
    fun loadDataSource(source: String) {
        if (!isActive) {
            println("ViewModel is not active, cannot load $source")
            return
        }
        
        val job = GlobalScope.launch {
            performDataSourceLoad(source)
        }
        jobs.add(job)
        requestCount++
        println("Started loading $source (Total: $requestCount)")
    }
    
    private suspend fun performDataLoad() {
        delay(Random.nextLong(500, 1500))
        println("Data loaded successfully")
    }
    
    private suspend fun performDataSourceLoad(source: String) {
        delay(Random.nextLong(300, 1000))
        println("$source loaded successfully")
    }
    
    fun onCleared() {
        if (onClearedCalled) {
            return
        }
        
        isActive = false
        onClearedCalled = true
        
        jobs.forEach { it.cancel() }
        jobs.clear()
        
        println("ViewModel onCleared called")
    }
    
    fun isActive() = isActive
    fun isOnClearedCalled() = onClearedCalled
    fun getRequestCount() = requestCount
}

class ActivityComponent {
    private var isResumed = false
    private var isPaused = false
    private var isDestroyed = false
    private val jobs = mutableListOf<Job>()
    private var taskCount = 0
    
    fun onResume() {
        isResumed = true
        isPaused = false
        println("ActivityComponent resumed")
    }
    
    fun onPause() {
        isPaused = true
        isResumed = false
        println("ActivityComponent paused")
    }
    
    fun onDestroy() {
        if (isDestroyed) {
            return
        }
        
        isResumed = false
        isPaused = false
        isDestroyed = true
        
        jobs.forEach { it.cancel() }
        jobs.clear()
        
        println("ActivityComponent destroyed")
    }
    
    fun startTask(taskName: String) {
        if (!isResumed) {
            println("Activity is not resumed, cannot start task: $taskName")
            return
        }
        
        val job = GlobalScope.launch {
            executeTask(taskName)
        }
        jobs.add(job)
        taskCount++
        println("Started task: $taskName (Total: $taskCount)")
    }
    
    fun startMultipleTasks(taskNames: List<String>) {
        taskNames.forEach { taskName ->
            startTask(taskName)
        }
    }
    
    private suspend fun executeTask(taskName: String) {
        repeat(12) { iteration ->
            if (isDestroyed) {
                println("Task $taskName cancelled at iteration $iteration")
                return
            }
            
            delay(Random.nextLong(100, 250))
            println("$taskName: Iteration $iteration")
        }
        
        println("$taskName: Completed")
    }
    
    fun isResumed() = isResumed
    fun isPaused() = isPaused
    fun isDestroyed() = isDestroyed
    fun getTaskCount() = taskCount
}

class FragmentComponent {
    private var isAttached = false
    private var isDetached = false
    private val jobs = mutableListOf<Job>()
    private var operationCount = 0
    
    fun onAttach() {
        isAttached = true
        isDetached = false
        println("FragmentComponent attached")
    }
    
    fun onDetach() {
        if (isDetached) {
            return
        }
        
        isAttached = false
        isDetached = true
        
        jobs.forEach { it.cancel() }
        jobs.clear()
        
        println("FragmentComponent detached")
    }
    
    fun startOperation(operationName: String) {
        if (!isAttached) {
            println("Fragment is not attached, cannot start operation: $operationName")
            return
        }
        
        val job = GlobalScope.launch {
            executeOperation(operationName)
        }
        jobs.add(job)
        operationCount++
        println("Started operation: $operationName (Total: $operationCount)")
    }
    
    fun startMultipleOperations(operationNames: List<String>) {
        operationNames.forEach { operationName ->
            startOperation(operationName)
        }
    }
    
    private suspend fun executeOperation(operationName: String) {
        repeat(10) { iteration ->
            if (isDetached) {
                println("Operation $operationName cancelled at iteration $iteration")
                return
            }
            
            delay(Random.nextLong(100, 200))
            println("$operationName: Iteration $iteration")
        }
        
        println("$operationName: Completed")
    }
    
    fun isAttached() = isAttached
    fun isDetached() = isDetached
    fun getOperationCount() = operationCount
}

class ServiceComponent {
    private var isStarted = false
    private var isStopped = false
    private val jobs = mutableListOf<Job>()
    private var jobCount = 0
    
    fun onStart() {
        isStarted = true
        isStopped = false
        println("ServiceComponent started")
    }
    
    fun onStop() {
        if (isStopped) {
            return
        }
        
        isStarted = false
        isStopped = true
        
        jobs.forEach { it.cancel() }
        jobs.clear()
        
        println("ServiceComponent stopped")
    }
    
    fun startJob(jobName: String) {
        if (!isStarted) {
            println("Service is not started, cannot start job: $jobName")
            return
        }
        
        val job = GlobalScope.launch {
            executeJob(jobName)
        }
        jobs.add(job)
        jobCount++
        println("Started job: $jobName (Total: $jobCount)")
    }
    
    fun startMultipleJobs(jobNames: List<String>) {
        jobNames.forEach { jobName ->
            startJob(jobName)
        }
    }
    
    private suspend fun executeJob(jobName: String) {
        repeat(15) { iteration ->
            if (isStopped) {
                println("Job $jobName cancelled at iteration $iteration")
                return
            }
            
            delay(Random.nextLong(100, 300))
            println("$jobName: Iteration $iteration")
        }
        
        println("$jobName: Completed")
    }
    
    fun isStarted() = isStarted
    fun isStopped() = isStopped
    fun getJobCount() = jobCount
}

suspend fun simulateLifecycleAwareComponent(
    component: LifecycleAwareComponent
) {
    println("Simulating lifecycle-aware component...")
    component.initialize()
    
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    component.startMultipleBackgroundTasks(taskNames)
    
    delay(2000)
    
    println("Destroying component...")
    component.destroy()
    
    delay(1000)
    
    println("\nLifecycle Aware Component Summary:")
    println("  Is alive: ${component.isAlive()}")
    println("  Is initialized: ${component.isInitialized()}")
    println("  Is destroyed: ${component.isDestroyed()}")
    println("  Job count: ${component.getJobCount()}")
}

suspend fun simulateViewModelComponent(
    viewModel: ViewModelComponent
) {
    println("Simulating ViewModel component...")
    
    viewModel.loadData()
    delay(500)
    
    val sources = listOf(
        "Users", "Posts", "Comments", "Albums", "Photos"
    )
    
    viewModel.loadMultipleDataSources(sources)
    
    delay(2000)
    
    println("Clearing ViewModel...")
    viewModel.onCleared()
    
    delay(1000)
    
    println("\nViewModel Component Summary:")
    println("  Is active: ${viewModel.isActive()}")
    println("  Is onCleared called: ${viewModel.isOnClearedCalled()}")
    println("  Request count: ${viewModel.getRequestCount()}")
}

suspend fun simulateActivityComponent(
    activity: ActivityComponent
) {
    println("Simulating Activity component...")
    
    activity.onResume()
    
    val taskNames = listOf(
        "Task1", "Task2", "Task3", "Task4", "Task5"
    )
    
    activity.startMultipleTasks(taskNames)
    
    delay(2000)
    
    println("Pausing activity...")
    activity.onPause()
    
    delay(1000)
    
    println("Destroying activity...")
    activity.onDestroy()
    
    delay(1000)
    
    println("\nActivity Component Summary:")
    println("  Is resumed: ${activity.isResumed()}")
    println("  Is paused: ${activity.isPaused()}")
    println("  Is destroyed: ${activity.isDestroyed()}")
    println("  Task count: ${activity.getTaskCount()}")
}

suspend fun simulateFragmentComponent(
    fragment: FragmentComponent
) {
    println("Simulating Fragment component...")
    
    fragment.onAttach()
    
    val operationNames = listOf(
        "Operation1", "Operation2", "Operation3", "Operation4", "Operation5"
    )
    
    fragment.startMultipleOperations(operationNames)
    
    delay(2000)
    
    println("Detaching fragment...")
    fragment.onDetach()
    
    delay(1000)
    
    println("\nFragment Component Summary:")
    println("  Is attached: ${fragment.isAttached()}")
    println("  Is detached: ${fragment.isDetached()}")
    println("  Operation count: ${fragment.getOperationCount()}")
}

suspend fun simulateServiceComponent(
    service: ServiceComponent
) {
    println("Simulating Service component...")
    
    service.onStart()
    
    val jobNames = listOf(
        "Job1", "Job2", "Job3", "Job4", "Job5"
    )
    
    service.startMultipleJobs(jobNames)
    
    delay(2000)
    
    println("Stopping service...")
    service.onStop()
    
    delay(1000)
    
    println("\nService Component Summary:")
    println("  Is started: ${service.isStarted()}")
    println("  Is stopped: ${service.isStopped()}")
    println("  Job count: ${service.getJobCount()}")
}

fun main() = runBlocking {
    println("Starting Scope Lifecycle Simulation...")
    println()
    
    val lifecycleComponent = LifecycleAwareComponent()
    val viewModelComponent = ViewModelComponent()
    val activityComponent = ActivityComponent()
    val fragmentComponent = FragmentComponent()
    val serviceComponent = ServiceComponent()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateLifecycleAwareComponent(lifecycleComponent)
    })
    
    jobs.add(launch {
        simulateViewModelComponent(viewModelComponent)
    })
    
    jobs.add(launch {
        simulateActivityComponent(activityComponent)
    })
    
    jobs.add(launch {
        simulateFragmentComponent(fragmentComponent)
    })
    
    jobs.add(launch {
        simulateServiceComponent(serviceComponent)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Lifecycle Aware Component:")
    println("  Is alive: ${lifecycleComponent.isAlive()}")
    println("  Is initialized: ${lifecycleComponent.isInitialized()}")
    println("  Is destroyed: ${lifecycleComponent.isDestroyed()}")
    println("  Job count: ${lifecycleComponent.getJobCount()}")
    
    println("\nViewModel Component:")
    println("  Is active: ${viewModelComponent.isActive()}")
    println("  Is onCleared called: ${viewModelComponent.isOnClearedCalled()}")
    println("  Request count: ${viewModelComponent.getRequestCount()}")
    
    println("\nActivity Component:")
    println("  Is resumed: ${activityComponent.isResumed()}")
    println("  Is paused: ${activityComponent.isPaused()}")
    println("  Is destroyed: ${activityComponent.isDestroyed()}")
    println("  Task count: ${activityComponent.getTaskCount()}")
    
    println("\nFragment Component:")
    println("  Is attached: ${fragmentComponent.isAttached()}")
    println("  Is detached: ${fragmentComponent.isDetached()}")
    println("  Operation count: ${fragmentComponent.getOperationCount()}")
    
    println("\nService Component:")
    println("  Is started: ${serviceComponent.isStarted()}")
    println("  Is stopped: ${serviceComponent.isStopped()}")
    println("  Job count: ${serviceComponent.getJobCount()}")
    
    println("\n⚠️  Scope Lifecycle Warning:")
    println("  The code uses GlobalScope to launch coroutines in lifecycle-aware components:")
    println("  - LifecycleAwareComponent.startBackgroundTask() launches tasks in GlobalScope")
    println("  - ViewModelComponent.loadData() launches tasks in GlobalScope")
    println("  - ViewModelComponent.loadDataSource() launches tasks in GlobalScope")
    println("  - ActivityComponent.startTask() launches tasks in GlobalScope")
    println("  - FragmentComponent.startOperation() launches operations in GlobalScope")
    println("  - ServiceComponent.startJob() launches jobs in GlobalScope")
    println("  These coroutines will continue running even after the component is destroyed,")
    println("  leading to memory leaks and potential crashes.")
    println("  Fix: Use lifecycle-aware scopes like viewModelScope, lifecycleScope, or")
    println("  pass the component's scope to functions instead of using GlobalScope.")
}