import kotlinx.coroutines.*
import kotlin.random.Random

class InfiniteLoopBlockingProcessor {
    private var counter = 0
    private var running = true
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing task $taskId")
            var iterations = 0
            while (running && iterations < 1000) {
                counter++
                iterations++
            }
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

class InfiniteLoopBlockingCalculator {
    private var result = 0
    private var running = true
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Calculating $value")
            var iterations = 0
            while (running && iterations < 1000) {
                result += value
                iterations++
            }
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

class InfiniteLoopBlockingDownloader {
    private var downloadedCount = 0
    private var running = true
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading file $fileId")
            var iterations = 0
            while (running && iterations < 1000) {
                downloadedCount++
                iterations++
            }
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

class InfiniteLoopBlockingUploader {
    private var uploadedCount = 0
    private var running = true
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading file $fileId")
            var iterations = 0
            while (running && iterations < 1000) {
                uploadedCount++
                iterations++
            }
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

class InfiniteLoopBlockingWorker {
    private var workCount = 0
    private var running = true
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Working on $workId")
            var iterations = 0
            while (running && iterations < 1000) {
                workCount++
                iterations++
            }
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

suspend fun simulateInfiniteLoopBlockingProcessor(
    processor: InfiniteLoopBlockingProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(5)
        delay(100)
    }
    
    println("Infinite loop blocking processor $processorId completed")
}

suspend fun simulateInfiniteLoopBlockingCalculator(
    calculator: InfiniteLoopBlockingCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5))
        delay(100)
    }
    
    println("Infinite loop blocking calculator $calculatorId completed")
}

suspend fun simulateInfiniteLoopBlockingDownloader(
    downloader: InfiniteLoopBlockingDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(5)
        delay(100)
    }
    
    println("Infinite loop blocking downloader $downloaderId completed")
}

suspend fun simulateInfiniteLoopBlockingUploader(
    uploader: InfiniteLoopBlockingUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(5)
        delay(100)
    }
    
    println("Infinite loop blocking uploader $uploaderId completed")
}

suspend fun simulateInfiniteLoopBlockingWorker(
    worker: InfiniteLoopBlockingWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(5)
        delay(100)
    }
    
    println("Infinite loop blocking worker $workerId completed")
}

suspend fun monitorInfiniteLoopBlockingOperations(
    processor: InfiniteLoopBlockingProcessor,
    calculator: InfiniteLoopBlockingCalculator,
    downloader: InfiniteLoopBlockingDownloader,
    uploader: InfiniteLoopBlockingUploader,
    worker: InfiniteLoopBlockingWorker,
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
    println("Starting Infinite Loop Blocking Simulation...")
    println()
    
    val processor = InfiniteLoopBlockingProcessor()
    val calculator = InfiniteLoopBlockingCalculator()
    val downloader = InfiniteLoopBlockingDownloader()
    val uploader = InfiniteLoopBlockingUploader()
    val worker = InfiniteLoopBlockingWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateInfiniteLoopBlockingProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateInfiniteLoopBlockingProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateInfiniteLoopBlockingCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateInfiniteLoopBlockingDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateInfiniteLoopBlockingUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateInfiniteLoopBlockingWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorInfiniteLoopBlockingOperations(
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
    
    println("\n⚠️  Infinite Loop Blocking Warning:")
    println("  The code uses infinite loops in coroutines:")
    println("  - InfiniteLoopBlockingProcessor.processTask() uses while loop without yield()")
    println("  - InfiniteLoopBlockingCalculator.calculate() uses while loop without yield()")
    println("  - InfiniteLoopBlockingDownloader.downloadFile() uses while loop without yield()")
    println("  - InfiniteLoopBlockingUploader.uploadFile() uses while loop without yield()")
    println("  - InfiniteLoopBlockingWorker.doWork() uses while loop without yield()")
    println("  Infinite loops without yield() block the entire thread,")
    println("  preventing other coroutines from executing and causing performance issues.")
    println("  Fix: Use yield() or delay() in loops to allow other coroutines to execute.")
}