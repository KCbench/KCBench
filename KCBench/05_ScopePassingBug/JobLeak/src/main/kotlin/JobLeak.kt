import kotlinx.coroutines.*
import kotlin.random.Random

class JobManager {
    private val jobs = mutableListOf<Job>()
    private var jobCount = 0
    
    fun startJob(jobName: String) {
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
        repeat(10) { iteration ->
            delay(Random.nextLong(100, 300))
            println("$jobName: Iteration $iteration")
        }
        println("$jobName: Completed")
    }
    
    fun cancelJob(jobName: String) {
        val job = jobs.find { it.toString().contains(jobName) }
        job?.cancel()
    }
    
    fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
    
    fun getJobCount() = jobCount
    fun getActiveJobs() = jobs.size
}

class BackgroundJob {
    private var job: Job? = null
    private var isActive = false
    private var completed = false
    
    fun start() {
        if (isActive) {
            return
        }
        
        isActive = true
        job = GlobalScope.launch {
            execute()
        }
    }
    
    private suspend fun execute() {
        repeat(15) { iteration ->
            if (!isActive) {
                println("BackgroundJob cancelled at iteration $iteration")
                return
            }
            
            delay(Random.nextLong(150, 350))
            println("BackgroundJob: Iteration $iteration")
        }
        
        completed = true
        isActive = false
        println("BackgroundJob: Completed")
    }
    
    fun stop() {
        isActive = false
        job?.cancel()
    }
    
    fun isActive() = isActive
    fun isCompleted() = completed
}

class PeriodicTask {
    private var job: Job? = null
    private var isRunning = false
    private var executionCount = 0
    
    fun start(intervalMs: Long) {
        if (isRunning) {
            return
        }
        
        isRunning = true
        job = GlobalScope.launch {
            while (isRunning) {
                executeTask()
                delay(intervalMs)
            }
        }
    }
    
    private suspend fun executeTask() {
        executionCount++
        println("PeriodicTask: Execution #$executionCount")
        delay(Random.nextLong(100, 200))
    }
    
    fun stop() {
        isRunning = false
        job?.cancel()
    }
    
    fun isRunning() = isRunning
    fun getExecutionCount() = executionCount
}

class JobPool {
    private val jobs = mutableMapOf<String, Job>()
    private var totalJobs = 0
    
    fun submitJob(jobId: String, durationMs: Long) {
        val job = GlobalScope.launch {
            executeJob(jobId, durationMs)
        }
        jobs[jobId] = job
        totalJobs++
        println("Submitted job: $jobId (Total: $totalJobs)")
    }
    
    fun submitMultipleJobs(jobConfigs: List<Pair<String, Long>>) {
        jobConfigs.forEach { (jobId, duration) ->
            submitJob(jobId, duration)
        }
    }
    
    private suspend fun executeJob(jobId: String, durationMs: Long) {
        val startTime = System.currentTimeMillis()
        val iterations = (durationMs / 200).toInt()
        
        repeat(iterations) { iteration ->
            delay(200)
            println("$jobId: Progress ${((iteration + 1) * 100 / iterations)}%")
        }
        
        val elapsedTime = System.currentTimeMillis() - startTime
        println("$jobId: Completed in ${elapsedTime}ms")
    }
    
    fun cancelJob(jobId: String) {
        jobs[jobId]?.cancel()
        jobs.remove(jobId)
    }
    
    fun cancelAllJobs() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
    }
    
    fun getTotalJobs() = totalJobs
    fun getActiveJobs() = jobs.size
}

class JobLeakDetector {
    private val leakedJobs = mutableListOf<Job>()
    private var leakCount = 0
    
    fun startLeakyJob(jobName: String) {
        val job = GlobalScope.launch {
            executeLeakyJob(jobName)
        }
        leakedJobs.add(job)
        leakCount++
        println("Started leaky job: $jobName (Leaks: $leakCount)")
    }
    
    fun startMultipleLeakyJobs(jobNames: List<String>) {
        jobNames.forEach { jobName ->
            startLeakyJob(jobName)
        }
    }
    
    private suspend fun executeLeakyJob(jobName: String) {
        repeat(20) { iteration ->
            delay(Random.nextLong(200, 400))
            println("$jobName: Iteration $iteration")
        }
        println("$jobName: Completed")
    }
    
    fun cleanupLeaks() {
        leakedJobs.forEach { it.cancel() }
        leakedJobs.clear()
    }
    
    fun getLeakCount() = leakCount
    fun getActiveLeaks() = leakedJobs.size
}

suspend fun simulateJobManager(
    manager: JobManager
) {
    val jobNames = listOf(
        "Job1", "Job2", "Job3", "Job4", "Job5"
    )
    
    println("Starting jobs...")
    manager.startMultipleJobs(jobNames)
    
    delay(3000)
    
    println("\nJob Manager Summary:")
    println("  Total jobs: ${manager.getJobCount()}")
    println("  Active jobs: ${manager.getActiveJobs()}")
}

suspend fun simulateBackgroundJob(
    job: BackgroundJob
) {
    println("Starting background job...")
    job.start()
    
    delay(4000)
    
    println("\nBackground Job Summary:")
    println("  Is active: ${job.isActive()}")
    println("  Is completed: ${job.isCompleted()}")
}

suspend fun simulatePeriodicTask(
    task: PeriodicTask
) {
    println("Starting periodic task...")
    task.start(500)
    
    delay(3000)
    
    println("\nPeriodic Task Summary:")
    println("  Is running: ${task.isRunning()}")
    println("  Execution count: ${task.getExecutionCount()}")
}

suspend fun simulateJobPool(
    pool: JobPool
) {
    val jobConfigs = listOf(
        Pair("JOB001", 2000),
        Pair("JOB002", 3000),
        Pair("JOB003", 2500),
        Pair("JOB004", 3500),
        Pair("JOB005", 2800)
    )
    
    println("Submitting jobs to pool...")
    pool.submitMultipleJobs(jobConfigs)
    
    delay(4000)
    
    println("\nJob Pool Summary:")
    println("  Total jobs: ${pool.getTotalJobs()}")
    println("  Active jobs: ${pool.getActiveJobs()}")
}

suspend fun simulateJobLeakDetector(
    detector: JobLeakDetector
) {
    val jobNames = listOf(
        "Leak1", "Leak2", "Leak3", "Leak4", "Leak5"
    )
    
    println("Starting leaky jobs...")
    detector.startMultipleLeakyJobs(jobNames)
    
    delay(4000)
    
    println("\nJob Leak Detector Summary:")
    println("  Total leaks: ${detector.getLeakCount()}")
    println("  Active leaks: ${detector.getActiveLeaks()}")
}

fun main() = runBlocking {
    println("Starting Job Leak Simulation...")
    println()
    
    val manager = JobManager()
    val backgroundJob = BackgroundJob()
    val periodicTask = PeriodicTask()
    val pool = JobPool()
    val detector = JobLeakDetector()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateJobManager(manager)
    })
    
    jobs.add(launch {
        simulateBackgroundJob(backgroundJob)
    })
    
    jobs.add(launch {
        simulatePeriodicTask(periodicTask)
    })
    
    jobs.add(launch {
        simulateJobPool(pool)
    })
    
    jobs.add(launch {
        simulateJobLeakDetector(detector)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("Job Manager:")
    println("  Total jobs: ${manager.getJobCount()}")
    println("  Active jobs: ${manager.getActiveJobs()}")
    
    println("\nBackground Job:")
    println("  Is active: ${backgroundJob.isActive()}")
    println("  Is completed: ${backgroundJob.isCompleted()}")
    
    println("\nPeriodic Task:")
    println("  Is running: ${periodicTask.isRunning()}")
    println("  Execution count: ${periodicTask.getExecutionCount()}")
    
    println("\nJob Pool:")
    println("  Total jobs: ${pool.getTotalJobs()}")
    println("  Active jobs: ${pool.getActiveJobs()}")
    
    println("\nJob Leak Detector:")
    println("  Total leaks: ${detector.getLeakCount()}")
    println("  Active leaks: ${detector.getActiveLeaks()}")
    
    println("\n⚠️  Job Leak Warning:")
    println("  The code uses GlobalScope to launch jobs that are not properly managed:")
    println("  - JobManager.startJob() launches jobs in GlobalScope")
    println("  - BackgroundJob.start() launches job in GlobalScope")
    println("  - PeriodicTask.start() launches job in GlobalScope")
    println("  - JobPool.submitJob() launches jobs in GlobalScope")
    println("  - JobLeakDetector.startLeakyJob() launches jobs in GlobalScope")
    println("  These jobs will continue running even after the main scope is cancelled,")
    println("  leading to memory leaks and resource leaks.")
    println("  Fix: Use structured concurrency and properly manage job lifecycles.")
}