import kotlinx.coroutines.*
import kotlin.random.Random
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class BlockingDatabaseProcessor {
    private var counter = 0
    
    suspend fun processTask(taskId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing task $taskId")
            var connection: Connection? = null
            try {
                connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                val statement = connection.createStatement()
                statement.execute("CREATE TABLE IF NOT EXISTS tasks (id INT, name VARCHAR(255))")
                statement.execute("INSERT INTO tasks VALUES ($taskId, 'Task$taskId')")
                Thread.sleep(100)
                counter++
                println("Task $taskId completed (Counter: $counter)")
            } catch (e: Exception) {
                println("Error processing task $taskId: ${e.message}")
            } finally {
                connection?.close()
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

class BlockingDatabaseCalculator {
    private var result = 0
    
    suspend fun calculate(value: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Calculating $value")
            var connection: Connection? = null
            try {
                connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                val statement = connection.createStatement()
                statement.execute("CREATE TABLE IF NOT EXISTS calculations (id INT, value INT)")
                statement.execute("INSERT INTO calculations VALUES ($value, $value)")
                val resultSet = statement.executeQuery("SELECT SUM(value) FROM calculations")
                if (resultSet.next()) {
                    result = resultSet.getInt(1)
                }
                Thread.sleep(100)
                println("Calculation completed for $value (Result: $result)")
            } catch (e: Exception) {
                println("Error calculating $value: ${e.message}")
            } finally {
                connection?.close()
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

class BlockingDatabaseDownloader {
    private var downloadedCount = 0
    
    suspend fun downloadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading file $fileId")
            var connection: Connection? = null
            try {
                connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                val statement = connection.createStatement()
                statement.execute("CREATE TABLE IF NOT EXISTS files (id INT, name VARCHAR(255))")
                statement.execute("INSERT INTO files VALUES ($fileId, 'File$fileId')")
                Thread.sleep(100)
                downloadedCount++
                println("File $fileId downloaded (Count: $downloadedCount)")
            } catch (e: Exception) {
                println("Error downloading file $fileId: ${e.message}")
            } finally {
                connection?.close()
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

class BlockingDatabaseUploader {
    private var uploadedCount = 0
    
    suspend fun uploadFile(fileId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading file $fileId")
            var connection: Connection? = null
            try {
                connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                val statement = connection.createStatement()
                statement.execute("CREATE TABLE IF NOT EXISTS uploads (id INT, name VARCHAR(255))")
                statement.execute("INSERT INTO uploads VALUES ($fileId, 'Upload$fileId')")
                Thread.sleep(100)
                uploadedCount++
                println("File $fileId uploaded (Count: $uploadedCount)")
            } catch (e: Exception) {
                println("Error uploading file $fileId: ${e.message}")
            } finally {
                connection?.close()
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

class BlockingDatabaseWorker {
    private var workCount = 0
    
    suspend fun doWork(workId: Int) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Working on $workId")
            var connection: Connection? = null
            try {
                connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                val statement = connection.createStatement()
                statement.execute("CREATE TABLE IF NOT EXISTS work (id INT, name VARCHAR(255))")
                statement.execute("INSERT INTO work VALUES ($workId, 'Work$workId')")
                Thread.sleep(100)
                workCount++
                println("Work $workId completed (Count: $workCount)")
            } catch (e: Exception) {
                println("Error working on $workId: ${e.message}")
            } finally {
                connection?.close()
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

suspend fun simulateBlockingDatabaseProcessor(
    processor: BlockingDatabaseProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        processor.processMultipleTasks(5)
        delay(100)
    }
    
    println("Blocking database processor $processorId completed")
}

suspend fun simulateBlockingDatabaseCalculator(
    calculator: BlockingDatabaseCalculator,
    calculatorId: Int
) {
    repeat(10) { attempt ->
        calculator.calculateMultiple(listOf(1, 2, 3, 4, 5))
        delay(100)
    }
    
    println("Blocking database calculator $calculatorId completed")
}

suspend fun simulateBlockingDatabaseDownloader(
    downloader: BlockingDatabaseDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        downloader.downloadMultipleFiles(5)
        delay(100)
    }
    
    println("Blocking database downloader $downloaderId completed")
}

suspend fun simulateBlockingDatabaseUploader(
    uploader: BlockingDatabaseUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        uploader.uploadMultipleFiles(5)
        delay(100)
    }
    
    println("Blocking database uploader $uploaderId completed")
}

suspend fun simulateBlockingDatabaseWorker(
    worker: BlockingDatabaseWorker,
    workerId: Int
) {
    repeat(10) { attempt ->
        worker.doMultipleWork(5)
        delay(100)
    }
    
    println("Blocking database worker $workerId completed")
}

suspend fun monitorBlockingDatabaseOperations(
    processor: BlockingDatabaseProcessor,
    calculator: BlockingDatabaseCalculator,
    downloader: BlockingDatabaseDownloader,
    uploader: BlockingDatabaseUploader,
    worker: BlockingDatabaseWorker,
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
    println("Starting Blocking Database Misuse Simulation...")
    println()
    
    val processor = BlockingDatabaseProcessor()
    val calculator = BlockingDatabaseCalculator()
    val downloader = BlockingDatabaseDownloader()
    val uploader = BlockingDatabaseUploader()
    val worker = BlockingDatabaseWorker()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateBlockingDatabaseProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateBlockingDatabaseProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateBlockingDatabaseCalculator(calculator, 1)
    })
    
    jobs.add(launch {
        simulateBlockingDatabaseDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateBlockingDatabaseUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateBlockingDatabaseWorker(worker, 1)
    })
    
    jobs.add(launch {
        monitorBlockingDatabaseOperations(
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
    
    println("\n⚠️  Blocking Database Misuse Warning:")
    println("  The code uses blocking database operations in coroutines:")
    println("  - BlockingDatabaseProcessor.processTask() uses JDBC operations")
    println("  - BlockingDatabaseCalculator.calculate() uses JDBC operations")
    println("  - BlockingDatabaseDownloader.downloadFile() uses JDBC operations")
    println("  - BlockingDatabaseUploader.uploadFile() uses JDBC operations")
    println("  - BlockingDatabaseWorker.doWork() uses JDBC operations")
    println("  Blocking database operations block the entire thread,")
    println("  preventing other coroutines from executing and causing performance issues.")
    println("  Fix: Use Dispatchers.IO for blocking database operations or use async database libraries.")
}