import kotlinx.coroutines.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.random.Random

class DeadlockBlockingProcessor {
    private var counter = 0
    private val lock1 = ReentrantLock()
    private val lock2 = ReentrantLock()
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing task $taskId")
            lock1.lock()
            try {
                Thread.sleep(50)
                lock2.lock()
                try {
                    Thread.sleep(50)
                    counter++
                    println("Task $taskId completed (Counter: $counter)")
                } finally {
                    lock2.unlock()
                }
            } finally {
                lock1.unlock()
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

class DeadlockBlockingCalculator {
    private var result = 0
    private val lock1 = ReentrantLock()
    private val lock2 = ReentrantLock()
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Calculating $value")
            lock2.lock()
            try {
                Thread.sleep(50)
                lock1.lock()
                try {
                    Thread.sleep(50)
                    result += value
                    println("Calculation completed for $value (Result: $result)")
                } finally {
                    lock1.unlock()
                }
            } finally {
                lock2.unlock()
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

class DeadlockBlockingDownloader {
    private var downloadedCount = 0
    private val lock1 = ReentrantLock()
    private val lock2 = ReentrantLock()
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading file $fileId")
            lock1.lock()
            try {
                Thread.sleep(50)
                lock2.lock()
                try {
                    Thread.sleep(50)
                    downloadedCount++
                    println("File $fileId downloaded (Count: $downloadedCount)")
                } finally {
                    lock2.unlock()
                }
            } finally {
                lock1.unlock()
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

class DeadlockBlockingUploader {
    private var uploadedCount = 0
    private val lock1 = ReentrantLock()
    private val lock2 = ReentrantLock()
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading file $fileId")
            lock2.lock()
            try {
                Thread.sleep(50)
                lock1.lock()
                try {
                    Thread.sleep(50)
                    uploadedCount++
                    println("File $fileId uploaded (Count: $uploadedCount)")
                } finally {
                    lock1.unlock()
                }
            } finally {
                lock2.unlock()
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

class DeadlockBlockingWorker {
    private var workCount = 0
    private val lock1 = ReentrantLock()
    private val lock2 = ReentrantLock()
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Working on $workId")
            lock1.lock()
            try {
                Thread.sleep(50)
                lock2.lock()
                try {
                    Thread.sleep(50)
                    workCount++
                    println("Work $workId completed (Count: $workCount)")
                } finally {
                    lock2.unlock()
                }
            } finally {
                lock1.unlock()
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

suspend fun simulateDeadlockBlockingProcessor(
    processor: DeadlockBlockingProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(5)
        delay(100)
    }
    
    println("Deadlock blocking processor $processorId completed")
}

suspend fun simulateDeadlockBlockingCalculator(
    calculator: DeadlockBlockingCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5))
        delay(100)
    }
    
    println("Deadlock blocking calculator $calculatorId completed")
}

suspend fun simulateDeadlockBlockingDownloader(
    downloader: DeadlockBlockingDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(5)
        delay(100)
    }
    
    println("Deadlock blocking downloader $downloaderId completed")
}

suspend fun simulateDeadlockBlockingUploader(
    uploader: DeadlockBlockingUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(5)
        delay(100)
    }
    
    println("Deadlock blocking uploader $uploaderId completed")
}

suspend fun simulateDeadlockBlockingWorker(
    worker: DeadlockBlockingWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(5)
        delay(100)
    }
    
    println("Deadlock blocking worker $workerId completed")
}

suspend fun monitorDeadlockBlockingOperations(
    processor: DeadlockBlockingProcessor,
    calculator: DeadlockBlockingCalculator,
    downloader: DeadlockBlockingDownloader,
    uploader: DeadlockBlockingUploader,
    worker: DeadlockBlockingWorker,
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
    println("Starting Deadlock Blocking Simulation...")
    println()
    
    val processor = DeadlockBlockingProcessor()
    val calculator = DeadlockBlockingCalculator()
    val downloader = DeadlockBlockingDownloader()
    val uploader = DeadlockBlockingUploader()
    val worker = DeadlockBlockingWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateDeadlockBlockingProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockBlockingProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateDeadlockBlockingCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockBlockingDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockBlockingUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateDeadlockBlockingWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorDeadlockBlockingOperations(
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
    
    println("\n⚠️  Deadlock Blocking Warning:")
    println("  The code uses blocking operations with inconsistent lock ordering:")
    println("  - DeadlockBlockingProcessor.processTask() uses lock1 then lock2")
    println("  - DeadlockBlockingCalculator.calculate() uses lock2 then lock1")
    println("  - DeadlockBlockingDownloader.downloadFile() uses lock1 then lock2")
    println("  - DeadlockBlockingUploader.uploadFile() uses lock2 then lock1")
    println("  - DeadlockBlockingWorker.doWork() uses lock1 then lock2")
    println("  Inconsistent lock ordering can cause deadlock,")
    println("  preventing coroutines from executing and causing permanent blocking.")
    println("  Fix: Use consistent lock ordering or use Mutex instead of ReentrantLock.")
}