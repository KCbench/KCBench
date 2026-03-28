import kotlinx.coroutines.*
import kotlin.random.Random
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Lock

class SynchronizedBlockingProcessor {
    private var counter = 0
    private val lock = ReentrantLock()
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing task $taskId")
            lock.lock()
            try {
                Thread.sleep(100)
                counter++
                println("Task $taskId completed (Counter: $counter)")
            } finally {
                lock.unlock()
            }
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

class SynchronizedBlockingCalculator {
    private var result = 0
    private val lock = ReentrantLock()
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Calculating $value")
            lock.lock()
            try {
                Thread.sleep(100)
                result += value
                println("Calculation completed for $value (Result: $result)")
            } finally {
                lock.unlock()
            }
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

class SynchronizedBlockingDownloader {
    private var downloadedCount = 0
    private val lock = ReentrantLock()
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading file $fileId")
            lock.lock()
            try {
                Thread.sleep(100)
                downloadedCount++
                println("File $fileId downloaded (Count: $downloadedCount)")
            } finally {
                lock.unlock()
            }
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

class SynchronizedBlockingUploader {
    private var uploadedCount = 0
    private val lock = ReentrantLock()
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading file $fileId")
            lock.lock()
            try {
                Thread.sleep(100)
                uploadedCount++
                println("File $fileId uploaded (Count: $uploadedCount)")
            } finally {
                lock.unlock()
            }
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

class SynchronizedBlockingWorker {
    private var workCount = 0
    private val lock = ReentrantLock()
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Working on $workId")
            lock.lock()
            try {
                Thread.sleep(100)
                workCount++
                println("Work $workId completed (Count: $workCount)")
            } finally {
                lock.unlock()
            }
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

suspend fun simulateSynchronizedBlockingProcessor(
    processor: SynchronizedBlockingProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(5)
        delay(100)
    }
    
    println("Synchronized blocking processor $processorId completed")
}

suspend fun simulateSynchronizedBlockingCalculator(
    calculator: SynchronizedBlockingCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5))
        delay(100)
    }
    
    println("Synchronized blocking calculator $calculatorId completed")
}

suspend fun simulateSynchronizedBlockingDownloader(
    downloader: SynchronizedBlockingDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(5)
        delay(100)
    }
    
    println("Synchronized blocking downloader $downloaderId completed")
}

suspend fun simulateSynchronizedBlockingUploader(
    uploader: SynchronizedBlockingUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(5)
        delay(100)
    }
    
    println("Synchronized blocking uploader $uploaderId completed")
}

suspend fun simulateSynchronizedBlockingWorker(
    worker: SynchronizedBlockingWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(5)
        delay(100)
    }
    
    println("Synchronized blocking worker $workerId completed")
}

suspend fun monitorSynchronizedBlockingOperations(
    processor: SynchronizedBlockingProcessor,
    calculator: SynchronizedBlockingCalculator,
    downloader: SynchronizedBlockingDownloader,
    uploader: SynchronizedBlockingUploader,
    worker: SynchronizedBlockingWorker,
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
    println("Starting Synchronized Blocking Simulation...")
    println()
    
    val processor = SynchronizedBlockingProcessor()
    val calculator = SynchronizedBlockingCalculator()
    val downloader = SynchronizedBlockingDownloader()
    val uploader = SynchronizedBlockingUploader()
    val worker = SynchronizedBlockingWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSynchronizedBlockingProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateSynchronizedBlockingProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateSynchronizedBlockingCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateSynchronizedBlockingDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateSynchronizedBlockingUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateSynchronizedBlockingWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorSynchronizedBlockingOperations(
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
    
    println("\n⚠️  Synchronized Blocking Warning:")
    println("  The code uses synchronized blocking operations in coroutines:")
    println("  - SynchronizedBlockingProcessor.processTask() uses ReentrantLock with Thread.sleep()")
    println("  - SynchronizedBlockingCalculator.calculate() uses ReentrantLock with Thread.sleep()")
    println("  - SynchronizedBlockingDownloader.downloadFile() uses ReentrantLock with Thread.sleep()")
    println("  - SynchronizedBlockingUploader.uploadFile() uses ReentrantLock with Thread.sleep()")
    println("  - SynchronizedBlockingWorker.doWork() uses ReentrantLock with Thread.sleep()")
    println("  Synchronized blocking operations block the entire thread,")
    println("  preventing other coroutines from executing and causing performance issues.")
    println("  Fix: Use Mutex instead of ReentrantLock or use delay() instead of Thread.sleep().")
}