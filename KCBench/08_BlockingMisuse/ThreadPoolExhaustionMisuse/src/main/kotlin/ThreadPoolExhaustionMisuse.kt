import kotlinx.coroutines.*
import kotlin.random.Random

class ThreadPoolExhaustionProcessor {
    private var counter = 0
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing task $taskId")
            Thread.sleep(1000)
            counter++
            println("Task $taskId completed (Counter: $counter)")
        }
    }
    
    suspend fun processMultipleTasks(count: Int) = coroutineScope {
        repeat(count) { index ->
            processTask(index)
            delay(10)
        }
    }
    
    fun getCounter() = counter
}

class ThreadPoolExhaustionCalculator {
    private var result = 0
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Calculating $value")
            Thread.sleep(1000)
            result += value
            println("Calculation completed for $value (Result: $result)")
        }
    }
    
    suspend fun calculateMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            calculate(value)
            delay(10)
        }
    }
    
    fun getResult() = result
}

class ThreadPoolExhaustionDownloader {
    private var downloadedCount = 0
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading file $fileId")
            Thread.sleep(1000)
            downloadedCount++
            println("File $fileId downloaded (Count: $downloadedCount)")
        }
    }
    
    suspend fun downloadMultipleFiles(count: Int) = coroutineScope {
        repeat(count) { index ->
            downloadFile(index)
            delay(10)
        }
    }
    
    fun getDownloadedCount() = downloadedCount
}

class ThreadPoolExhaustionUploader {
    private var uploadedCount = 0
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading file $fileId")
            Thread.sleep(1000)
            uploadedCount++
            println("File $fileId uploaded (Count: $uploadedCount)")
        }
    }
    
    suspend fun uploadMultipleFiles(count: Int) = coroutineScope {
        repeat(count) { index ->
            uploadFile(index)
            delay(10)
        }
    }
    
    fun getUploadedCount() = uploadedCount
}

class ThreadPoolExhaustionWorker {
    private var workCount = 0
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Working on $workId")
            Thread.sleep(1000)
            workCount++
            println("Work $workId completed (Count: $workCount)")
        }
    }
    
    suspend fun doMultipleWork(count: Int) = coroutineScope {
        repeat(count) { index ->
            doWork(index)
            delay(10)
        }
    }
    
    fun getWorkCount() = workCount
}

suspend fun simulateThreadPoolExhaustionProcessor(
    processor: ThreadPoolExhaustionProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(20)
        delay(100)
    }
    
    println("Thread pool exhaustion processor $processorId completed")
}

suspend fun simulateThreadPoolExhaustionCalculator(
    calculator: ThreadPoolExhaustionCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20))
        delay(100)
    }
    
    println("Thread pool exhaustion calculator $calculatorId completed")
}

suspend fun simulateThreadPoolExhaustionDownloader(
    downloader: ThreadPoolExhaustionDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(20)
        delay(100)
    }
    
    println("Thread pool exhaustion downloader $downloaderId completed")
}

suspend fun simulateThreadPoolExhaustionUploader(
    uploader: ThreadPoolExhaustionUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(20)
        delay(100)
    }
    
    println("Thread pool exhaustion uploader $uploaderId completed")
}

suspend fun simulateThreadPoolExhaustionWorker(
    worker: ThreadPoolExhaustionWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(20)
        delay(100)
    }
    
    println("Thread pool exhaustion worker $workerId completed")
}

suspend fun monitorThreadPoolExhaustionOperations(
    processor: ThreadPoolExhaustionProcessor,
    calculator: ThreadPoolExhaustionCalculator,
    downloader: ThreadPoolExhaustionDownloader,
    uploader: ThreadPoolExhaustionUploader,
    worker: ThreadPoolExhaustionWorker,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Processor counter: ${processor.getCounter()}")
        println("  Calculator result: ${calculator.getResult()}")
        println("  Downloader count: ${downloader.getDownloadedCount()}")
        println("  Uploader count: ${uploader.getUploadedCount()}")
        println("  Worker count: ${worker.getWorkCount()}")
        
        delay(200)
    }
}

fun main() = runBlocking {
    println("Starting Thread Pool Exhaustion Simulation...")
    println()
    
    val processor = ThreadPoolExhaustionProcessor()
    val calculator = ThreadPoolExhaustionCalculator()
    val downloader = ThreadPoolExhaustionDownloader()
    val uploader = ThreadPoolExhaustionUploader()
    val worker = ThreadPoolExhaustionWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateThreadPoolExhaustionProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateThreadPoolExhaustionProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateThreadPoolExhaustionCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateThreadPoolExhaustionDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateThreadPoolExhaustionUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateThreadPoolExhaustionWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorThreadPoolExhaustionOperations(
            processor,
            calculator,
            downloader,
            uploader,
            worker,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Thread Pool Exhaustion Warning:")
    println("  The code uses blocking operations that exhaust the thread pool:")
    println("  - ThreadPoolExhaustionProcessor.processTask() uses Thread.sleep(1000)")
    println("  - ThreadPoolExhaustionCalculator.calculate() uses Thread.sleep(1000)")
    println("  - ThreadPoolExhaustionDownloader.downloadFile() uses Thread.sleep(1000)")
    println("  - ThreadPoolExhaustionUploader.uploadFile() uses Thread.sleep(1000)")
    println("  - ThreadPoolExhaustionWorker.doWork() uses Thread.sleep(1000)")
    println("  Blocking operations with many concurrent tasks can exhaust the thread pool,")
    println("  preventing other coroutines from executing and causing performance issues.")
    println("  Fix: Use Dispatchers.IO for blocking operations or use delay() instead of Thread.sleep().")
}