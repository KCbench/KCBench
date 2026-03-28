import kotlinx.coroutines.*
import kotlin.random.Random

class ThreadSleepProcessor {
    private var counter = 0
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing task $taskId")
            Thread.sleep(100)
            counter++
            println("Task $taskId completed (Counter: $counter)")
        }
    }
    
    suspend fun processMultipleTasks(count: Int) = coroutineScope {
        repeat(count) { index ->
            processTask(index)
            delay(50)
        }
    }
    
    fun getCounter() = counter
}

class ThreadSleepCalculator {
    private var result = 0
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Calculating $value")
            Thread.sleep(100)
            result += value
            println("Calculation completed for $value (Result: $result)")
        }
    }
    
    suspend fun calculateMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            calculate(value)
            delay(50)
        }
    }
    
    fun getResult() = result
}

class ThreadSleepDownloader {
    private var downloadedCount = 0
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading file $fileId")
            Thread.sleep(100)
            downloadedCount++
            println("File $fileId downloaded (Count: $downloadedCount)")
        }
    }
    
    suspend fun downloadMultipleFiles(count: Int) = coroutineScope {
        repeat(count) { index ->
            downloadFile(index)
            delay(50)
        }
    }
    
    fun getDownloadedCount() = downloadedCount
}

class ThreadSleepUploader {
    private var uploadedCount = 0
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading file $fileId")
            Thread.sleep(100)
            uploadedCount++
            println("File $fileId uploaded (Count: $uploadedCount)")
        }
    }
    
    suspend fun uploadMultipleFiles(count: Int) = coroutineScope {
        repeat(count) { index ->
            uploadFile(index)
            delay(50)
        }
    }
    
    fun getUploadedCount() = uploadedCount
}

class ThreadSleepWorker {
    private var workCount = 0
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Working on $workId")
            Thread.sleep(100)
            workCount++
            println("Work $workId completed (Count: $workCount)")
        }
    }
    
    suspend fun doMultipleWork(count: Int) = coroutineScope {
        repeat(count) { index ->
            doWork(index)
            delay(50)
        }
    }
    
    fun getWorkCount() = workCount
}

suspend fun simulateThreadSleepProcessor(
    processor: ThreadSleepProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(5)
        delay(100)
    }
    
    println("Thread sleep processor $processorId completed")
}

suspend fun simulateThreadSleepCalculator(
    calculator: ThreadSleepCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5))
        delay(100)
    }
    
    println("Thread sleep calculator $calculatorId completed")
}

suspend fun simulateThreadSleepDownloader(
    downloader: ThreadSleepDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(5)
        delay(100)
    }
    
    println("Thread sleep downloader $downloaderId completed")
}

suspend fun simulateThreadSleepUploader(
    uploader: ThreadSleepUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(5)
        delay(100)
    }
    
    println("Thread sleep uploader $uploaderId completed")
}

suspend fun simulateThreadSleepWorker(
    worker: ThreadSleepWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(5)
        delay(100)
    }
    
    println("Thread sleep worker $workerId completed")
}

suspend fun monitorThreadSleepOperations(
    processor: ThreadSleepProcessor,
    calculator: ThreadSleepCalculator,
    downloader: ThreadSleepDownloader,
    uploader: ThreadSleepUploader,
    worker: ThreadSleepWorker,
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
    println("Starting Thread Sleep Misuse Simulation...")
    println()
    
    val processor = ThreadSleepProcessor()
    val calculator = ThreadSleepCalculator()
    val downloader = ThreadSleepDownloader()
    val uploader = ThreadSleepUploader()
    val worker = ThreadSleepWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateThreadSleepProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateThreadSleepProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateThreadSleepCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateThreadSleepDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateThreadSleepUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateThreadSleepWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorThreadSleepOperations(
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
    
    println("\n⚠️  Thread Sleep Misuse Warning:")
    println("  The code uses Thread.sleep() in coroutines:")
    println("  - ThreadSleepProcessor.processTask() uses Thread.sleep(100)")
    println("  - ThreadSleepCalculator.calculate() uses Thread.sleep(100)")
    println("  - ThreadSleepDownloader.downloadFile() uses Thread.sleep(100)")
    println("  - ThreadSleepUploader.uploadFile() uses Thread.sleep(100)")
    println("  - ThreadSleepWorker.doWork() uses Thread.sleep(100)")
    println("  Thread.sleep() blocks the entire thread, preventing other coroutines from executing.")
    println("  Fix: Use delay() instead of Thread.sleep().")
}