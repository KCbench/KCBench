import kotlinx.coroutines.*
import kotlin.random.Random
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class BlockingIOProcessor {
    private var counter = 0
    
    suspend fun processFile(filePath: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing file $filePath")
            val content = Files.readString(Paths.get(filePath))
            Thread.sleep(100)
            counter++
            println("File $filePath processed (Counter: $counter)")
        }
    }
    
    suspend fun processMultipleFiles(filePaths: List<String>) = coroutineScope {
        filePaths.forEach { filePath ->
            processFile(filePath)
            delay(50)
        }
    }
    
    fun getCounter() = counter
}

class BlockingIOReader {
    private var readCount = 0
    
    suspend fun readFile(filePath: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Reading file $filePath")
            val content = Files.readString(Paths.get(filePath))
            Thread.sleep(100)
            readCount++
            println("File $filePath read (Count: $readCount)")
        }
    }
    
    suspend fun readMultipleFiles(filePaths: List<String>) = coroutineScope {
        filePaths.forEach { filePath ->
            readFile(filePath)
            delay(50)
        }
    }
    
    fun getReadCount() = readCount
}

class BlockingIOWriter {
    private var writeCount = 0
    
    suspend fun writeFile(filePath: String, content: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Writing to file $filePath")
            Files.writeString(Paths.get(filePath), content)
            Thread.sleep(100)
            writeCount++
            println("File $filePath written (Count: $writeCount)")
        }
    }
    
    suspend fun writeMultipleFiles(filePaths: List<String>) = coroutineScope {
        filePaths.forEach { filePath ->
            writeFile(filePath, "Content for $filePath")
            delay(50)
        }
    }
    
    fun getWriteCount() = writeCount
}

class BlockingIOCopier {
    private var copyCount = 0
    
    suspend fun copyFile(sourcePath: String, targetPath: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Copying file from $sourcePath to $targetPath")
            val content = Files.readString(Paths.get(sourcePath))
            Files.writeString(Paths.get(targetPath), content)
            Thread.sleep(100)
            copyCount++
            println("File copied from $sourcePath to $targetPath (Count: $copyCount)")
        }
    }
    
    suspend fun copyMultipleFiles(sourcePaths: List<String>, targetPaths: List<String>) = coroutineScope {
        sourcePaths.zip(targetPaths).forEach { (source, target) ->
            copyFile(source, target)
            delay(50)
        }
    }
    
    fun getCopyCount() = copyCount
}

class BlockingIODeleter {
    private var deleteCount = 0
    
    suspend fun deleteFile(filePath: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Deleting file $filePath")
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
                Thread.sleep(100)
                deleteCount++
                println("File $filePath deleted (Count: $deleteCount)")
            }
        }
    }
    
    suspend fun deleteMultipleFiles(filePaths: List<String>) = coroutineScope {
        filePaths.forEach { filePath ->
            deleteFile(filePath)
            delay(50)
        }
    }
    
    fun getDeleteCount() = deleteCount
}

suspend fun simulateBlockingIOProcessor(
    processor: BlockingIOProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        val filePaths = listOf(
            "file1_$attempt.txt",
            "file2_$attempt.txt",
            "file3_$attempt.txt",
            "file4_$attempt.txt",
            "file5_$attempt.txt"
        )
        processor.processMultipleFiles(filePaths)
        delay(100)
    }
    
    println("Blocking IO processor $processorId completed")
}

suspend fun simulateBlockingIOReader(
    reader: BlockingIOReader,
    readerId: Int
) {
    repeat(10) { attempt ->
        val filePaths = listOf(
            "file1_$attempt.txt",
            "file2_$attempt.txt",
            "file3_$attempt.txt",
            "file4_$attempt.txt",
            "file5_$attempt.txt"
        )
        reader.readMultipleFiles(filePaths)
        delay(100)
    }
    
    println("Blocking IO reader $readerId completed")
}

suspend fun simulateBlockingIOWriter(
    writer: BlockingIOWriter,
    writerId: Int
) {
    repeat(10) { attempt ->
        val filePaths = listOf(
            "file1_$attempt.txt",
            "file2_$attempt.txt",
            "file3_$attempt.txt",
            "file4_$attempt.txt",
            "file5_$attempt.txt"
        )
        writer.writeMultipleFiles(filePaths)
        delay(100)
    }
    
    println("Blocking IO writer $writerId completed")
}

suspend fun simulateBlockingIOCopier(
    copier: BlockingIOCopier,
    copierId: Int
) {
    repeat(10) { attempt ->
        val sourcePaths = listOf(
            "source1_$attempt.txt",
            "source2_$attempt.txt",
            "source3_$attempt.txt",
            "source4_$attempt.txt",
            "source5_$attempt.txt"
        )
        val targetPaths = listOf(
            "target1_$attempt.txt",
            "target2_$attempt.txt",
            "target3_$attempt.txt",
            "target4_$attempt.txt",
            "target5_$attempt.txt"
        )
        copier.copyMultipleFiles(sourcePaths, targetPaths)
        delay(100)
    }
    
    println("Blocking IO copier $copierId completed")
}

suspend fun simulateBlockingIODeleter(
    deleter: BlockingIODeleter,
    deleterId: Int
) {
    repeat(10) { attempt ->
        val filePaths = listOf(
            "file1_$attempt.txt",
            "file2_$attempt.txt",
            "file3_$attempt.txt",
            "file4_$attempt.txt",
            "file5_$attempt.txt"
        )
        deleter.deleteMultipleFiles(filePaths)
        delay(100)
    }
    
    println("Blocking IO deleter $deleterId completed")
}

suspend fun monitorBlockingIOOperations(
    processor: BlockingIOProcessor,
    reader: BlockingIOReader,
    writer: BlockingIOWriter,
    copier: BlockingIOCopier,
    deleter: BlockingIODeleter,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Processor counter: ${processor.getCounter()}")
        println("  Reader count: ${reader.getReadCount()}")
        println("  Writer count: ${writer.getWriteCount()}")
        println("  Copier count: ${copier.getCopyCount()}")
        println("  Deleter count: ${deleter.getDeleteCount()}")
        
        delay(200)
    }
}

fun main() = runBlocking {
    println("Starting Blocking IO Misuse Simulation...")
    println()
    
    val processor = BlockingIOProcessor()
    val reader = BlockingIOReader()
    val writer = BlockingIOWriter()
    val copier = BlockingIOCopier()
    val deleter = BlockingIODeleter()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateBlockingIOProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateBlockingIOProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateBlockingIOReader(reader, 1)
    })
    
    jobs.add(launch {
        simulateBlockingIOWriter(writer, 1)
    })
    
    jobs.add(launch {
        simulateBlockingIOCopier(copier, 1)
    })
    
    jobs.add(launch {
        simulateBlockingIODeleter(deleter, 1)
    })
    
    jobs.add(launch {
        monitorBlockingIOOperations(
            processor,
            reader,
            writer,
            copier,
            deleter,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Blocking IO Misuse Warning:")
    println("  The code uses blocking IO operations in coroutines:")
    println("  - BlockingIOProcessor.processFile() uses Files.readString()")
    println("  - BlockingIOReader.readFile() uses Files.readString()")
    println("  - BlockingIOWriter.writeFile() uses Files.writeString()")
    println("  - BlockingIOCopier.copyFile() uses Files.readString() and Files.writeString()")
    println("  - BlockingIODeleter.deleteFile() uses File.delete()")
    println("  Blocking IO operations block the entire thread, preventing other coroutines from executing.")
    println("  Fix: Use Dispatchers.IO for blocking IO operations or use async IO libraries.")
}