import kotlinx.coroutines.*
import kotlin.random.Random

class MainBlockingProcessor {
    private var counter = 0
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch {
            println("Processing task $taskId on main thread")
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

class MainBlockingCalculator {
    private var result = 0
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch {
            println("Calculating $value on main thread")
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

class MainBlockingDownloader {
    private var downloadedCount = 0
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch {
            println("Downloading file $fileId on main thread")
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

class MainBlockingUploader {
    private var uploadedCount = 0
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch {
            println("Uploading file $fileId on main thread")
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

class MainBlockingWorker {
    private var workCount = 0
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch {
            println("Working on $workId on main thread")
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

suspend fun simulateMainBlockingProcessor(
    processor: MainBlockingProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(5)
        delay(100)
    }
    
    println("Main blocking processor $processorId completed")
}

suspend fun simulateMainBlockingCalculator(
    calculator: MainBlockingCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5))
        delay(100)
    }
    
    println("Main blocking calculator $calculatorId completed")
}

suspend fun simulateMainBlockingDownloader(
    downloader: MainBlockingDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(5)
        delay(100)
    }
    
    println("Main blocking downloader $downloaderId completed")
}

suspend fun simulateMainBlockingUploader(
    uploader: MainBlockingUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(5)
        delay(100)
    }
    
    println("Main blocking uploader $uploaderId completed")
}

suspend fun simulateMainBlockingWorker(
    worker: MainBlockingWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(5)
        delay(100)
    }
    
    println("Main blocking worker $workerId completed")
}

suspend fun monitorMainBlockingOperations(
    processor: MainBlockingProcessor,
    calculator: MainBlockingCalculator,
    downloader: MainBlockingDownloader,
    uploader: MainBlockingUploader,
    worker: MainBlockingWorker,
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
    println("Starting Main Thread Blocking Simulation...")
    println()
    
    val processor = MainBlockingProcessor()
    val calculator = MainBlockingCalculator()
    val downloader = MainBlockingDownloader()
    val uploader = MainBlockingUploader()
    val worker = MainBlockingWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMainBlockingProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateMainBlockingProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateMainBlockingCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateMainBlockingDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateMainBlockingUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateMainBlockingWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorMainBlockingOperations(
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
    
    println("\n⚠️  Main Thread Blocking Warning:")
    println("  The code uses blocking operations on the main thread:")
    println("  - MainBlockingProcessor.processTask() uses Thread.sleep(100) on main thread")
    println("  - MainBlockingCalculator.calculate() uses Thread.sleep(100) on main thread")
    println("  - MainBlockingDownloader.downloadFile() uses Thread.sleep(100) on main thread")
    println("  - MainBlockingUploader.uploadFile() uses Thread.sleep(100) on main thread")
    println("  - MainBlockingWorker.doWork() uses Thread.sleep(100) on main thread")
    println("  Blocking operations on the main thread can cause ANR (Application Not Responding) in UI applications.")
    println("  Fix: Use Dispatchers.IO for blocking operations or use delay() instead of Thread.sleep().")
}