import kotlinx.coroutines.*
import kotlin.random.Random

class WaitNotifyBlockingProcessor {
    private var counter = 0
    private val lock = Any()
    private var condition = false
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing task $taskId")
            synchronized(lock) {
                while (!condition) {
                    try {
                        (lock as java.lang.Object).wait(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                condition = false
                Thread.sleep(100)
                counter++
                println("Task $taskId completed (Counter: $counter)")
            }
        }
    }
    
    suspend fun notifyTask() = coroutineScope {
        launch(Dispatchers.Default) {
            synchronized(lock) {
                condition = true
                (lock as java.lang.Object).notifyAll()
            }
        }
    }
    
    suspend fun processMultipleTasks(count: Int) = coroutineScope {
        repeat(count) { index ->
            processTask(index)
            delay(50)
            notifyTask()
        }
    }
    
    fun getCounter() = counter
}

class WaitNotifyBlockingCalculator {
    private var result = 0
    private val lock = Any()
    private var condition = false
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Calculating $value")
            synchronized(lock) {
                while (!condition) {
                    try {
                        (lock as java.lang.Object).wait(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                condition = false
                Thread.sleep(100)
                result += value
                println("Calculation completed for $value (Result: $result)")
            }
        }
    }
    
    suspend fun notifyCalculate() = coroutineScope {
        launch(Dispatchers.Default) {
            synchronized(lock) {
                condition = true
                (lock as java.lang.Object).notifyAll()
            }
        }
    }
    
    suspend fun calculateMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            calculate(value)
            delay(50)
            notifyCalculate()
        }
    }
    
    fun getResult() = result
}

class WaitNotifyBlockingDownloader {
    private var downloadedCount = 0
    private val lock = Any()
    private var condition = false
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading file $fileId")
            synchronized(lock) {
                while (!condition) {
                    try {
                        (lock as java.lang.Object).wait(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                condition = false
                Thread.sleep(100)
                downloadedCount++
                println("File $fileId downloaded (Count: $downloadedCount)")
            }
        }
    }
    
    suspend fun notifyDownload() = coroutineScope {
        launch(Dispatchers.Default) {
            synchronized(lock) {
                condition = true
                (lock as java.lang.Object).notifyAll()
            }
        }
    }
    
    suspend fun downloadMultipleFiles(count: Int) = coroutineScope {
        repeat(count) { index ->
            downloadFile(index)
            delay(50)
            notifyDownload()
        }
    }
    
    fun getDownloadedCount() = downloadedCount
}

class WaitNotifyBlockingUploader {
    private var uploadedCount = 0
    private val lock = Any()
    private var condition = false
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading file $fileId")
            synchronized(lock) {
                while (!condition) {
                    try {
                        (lock as java.lang.Object).wait(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                condition = false
                Thread.sleep(100)
                uploadedCount++
                println("File $fileId uploaded (Count: $uploadedCount)")
            }
        }
    }
    
    suspend fun notifyUpload() = coroutineScope {
        launch(Dispatchers.Default) {
            synchronized(lock) {
                condition = true
                (lock as java.lang.Object).notifyAll()
            }
        }
    }
    
    suspend fun uploadMultipleFiles(count: Int) = coroutineScope {
        repeat(count) { index ->
            uploadFile(index)
            delay(50)
            notifyUpload()
        }
    }
    
    fun getUploadedCount() = uploadedCount
}

class WaitNotifyBlockingWorker {
    private var workCount = 0
    private val lock = Any()
    private var condition = false
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Working on $workId")
            synchronized(lock) {
                while (!condition) {
                    try {
                        (lock as java.lang.Object).wait(100)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                condition = false
                Thread.sleep(100)
                workCount++
                println("Work $workId completed (Count: $workCount)")
            }
        }
    }
    
    suspend fun notifyWork() = coroutineScope {
        launch(Dispatchers.Default) {
            synchronized(lock) {
                condition = true
                (lock as java.lang.Object).notifyAll()
            }
        }
    }
    
    suspend fun doMultipleWork(count: Int) = coroutineScope {
        repeat(count) { index ->
            doWork(index)
            delay(50)
            notifyWork()
        }
    }
    
    fun getWorkCount() = workCount
}

suspend fun simulateWaitNotifyBlockingProcessor(
    processor: WaitNotifyBlockingProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(5)
        delay(100)
    }
    
    println("Wait-notify blocking processor $processorId completed")
}

suspend fun simulateWaitNotifyBlockingCalculator(
    calculator: WaitNotifyBlockingCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5))
        delay(100)
    }
    
    println("Wait-notify blocking calculator $calculatorId completed")
}

suspend fun simulateWaitNotifyBlockingDownloader(
    downloader: WaitNotifyBlockingDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(5)
        delay(100)
    }
    
    println("Wait-notify blocking downloader $downloaderId completed")
}

suspend fun simulateWaitNotifyBlockingUploader(
    uploader: WaitNotifyBlockingUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(5)
        delay(100)
    }
    
    println("Wait-notify blocking uploader $uploaderId completed")
}

suspend fun simulateWaitNotifyBlockingWorker(
    worker: WaitNotifyBlockingWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(5)
        delay(100)
    }
    
    println("Wait-notify blocking worker $workerId completed")
}

suspend fun monitorWaitNotifyBlockingOperations(
    processor: WaitNotifyBlockingProcessor,
    calculator: WaitNotifyBlockingCalculator,
    downloader: WaitNotifyBlockingDownloader,
    uploader: WaitNotifyBlockingUploader,
    worker: WaitNotifyBlockingWorker,
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
    println("Starting Wait-Notify Blocking Simulation...")
    println()
    
    val processor = WaitNotifyBlockingProcessor()
    val calculator = WaitNotifyBlockingCalculator()
    val downloader = WaitNotifyBlockingDownloader()
    val uploader = WaitNotifyBlockingUploader()
    val worker = WaitNotifyBlockingWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateWaitNotifyBlockingProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateWaitNotifyBlockingProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateWaitNotifyBlockingCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateWaitNotifyBlockingDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateWaitNotifyBlockingUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateWaitNotifyBlockingWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorWaitNotifyBlockingOperations(
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
    
    println("\n⚠️  Wait-Notify Blocking Warning:")
    println("  The code uses wait-notify blocking operations in coroutines:")
    println("  - WaitNotifyBlockingProcessor.processTask() uses Object.wait() and Object.notifyAll()")
    println("  - WaitNotifyBlockingCalculator.calculate() uses Object.wait() and Object.notifyAll()")
    println("  - WaitNotifyBlockingDownloader.downloadFile() uses Object.wait() and Object.notifyAll()")
    println("  - WaitNotifyBlockingUploader.uploadFile() uses Object.wait() and Object.notifyAll()")
    println("  - WaitNotifyBlockingWorker.doWork() uses Object.wait() and Object.notifyAll()")
    println("  Wait-notify blocking operations block the entire thread,")
    println("  preventing other coroutines from executing and causing performance issues.")
    println("  Fix: Use Channel or Mutex instead of wait-notify.")
}