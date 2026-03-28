import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class FileHandle(
    val fileId: String,
    val filePath: String,
    var inUse: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class DirectoryLock(
    val directoryId: String,
    val directoryPath: String,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

class FileManager {
    private val files = mutableMapOf<String, FileHandle>()
    private val directories = mutableMapOf<String, DirectoryLock>()
    private val filePoolMutex = Mutex()
    private val directoryPoolMutex = Mutex()
    
    init {
        initializeFiles()
        initializeDirectories()
    }
    
    private fun initializeFiles() {
        val fileConfigs = listOf(
            Pair("FILE001", "/data/file1.txt"),
            Pair("FILE002", "/data/file2.txt"),
            Pair("FILE003", "/data/file3.txt"),
            Pair("FILE004", "/data/file4.txt"),
            Pair("FILE005", "/data/file5.txt"),
            Pair("FILE006", "/data/file6.txt"),
            Pair("FILE007", "/data/file7.txt"),
            Pair("FILE008", "/data/file8.txt"),
            Pair("FILE009", "/data/file9.txt"),
            Pair("FILE010", "/data/file10.txt")
        )
        
        fileConfigs.forEach { (fileId, filePath) ->
            files[fileId] = FileHandle(
                fileId = fileId,
                filePath = filePath,
                inUse = false,
                locked = false
            )
        }
    }
    
    private fun initializeDirectories() {
        val directoryConfigs = listOf(
            Pair("DIR001", "/data"),
            Pair("DIR002", "/logs"),
            Pair("DIR003", "/cache"),
            Pair("DIR004", "/temp"),
            Pair("DIR005", "/backup")
        )
        
        directoryConfigs.forEach { (directoryId, directoryPath) ->
            directories[directoryId] = DirectoryLock(
                directoryId = directoryId,
                directoryPath = directoryPath,
                locked = false
            )
        }
    }
    
    suspend fun openFile(fileId: String): Boolean {
        val file = files[fileId] ?: return false
        
        if (file.inUse) {
            return false
        }
        
        filePoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (file.inUse) {
                return false
            }
            
            file.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                file.inUse = true
                file.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun closeFile(fileId: String): Boolean {
        val file = files[fileId] ?: return false
        
        if (!file.inUse) {
            return false
        }
        
        file.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            filePoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                file.inUse = false
                file.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun lockDirectory(directoryId: String): Boolean {
        val directory = directories[directoryId] ?: return false
        
        if (directory.locked) {
            return false
        }
        
        directoryPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (directory.locked) {
                return false
            }
            
            directory.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                directory.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun unlockDirectory(directoryId: String): Boolean {
        val directory = directories[directoryId] ?: return false
        
        if (!directory.locked) {
            return false
        }
        
        directory.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            directoryPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                directory.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun writeFileWithDirectoryLock(
        fileId: String,
        directoryId: String
    ): Boolean {
        val file = files[fileId] ?: return false
        val directory = directories[directoryId] ?: return false
        
        if (!file.inUse || !directory.locked) {
            return false
        }
        
        file.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            directory.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                delay(Random.nextLong(20, 50))
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferFile(
        fromFileId: String,
        toFileId: String
    ): Boolean {
        val fromFile = files[fromFileId]
        val toFile = files[toFileId]
        
        if (fromFile == null || toFile == null) {
            return false
        }
        
        if (!fromFile.inUse || toFile.inUse) {
            return false
        }
        
        fromFile.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toFile.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromFile.inUse = false
                fromFile.locked = false
                toFile.inUse = true
                toFile.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapFiles(
        fileId1: String,
        fileId2: String
    ): Boolean {
        val file1 = files[fileId1]
        val file2 = files[fileId2]
        
        if (file1 == null || file2 == null) {
            return false
        }
        
        if (!file1.inUse || !file2.inUse) {
            return false
        }
        
        file1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            file2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempInUse = file1.inUse
                val tempLocked = file1.locked
                
                file1.inUse = file2.inUse
                file1.locked = file2.locked
                file2.inUse = tempInUse
                file2.locked = tempLocked
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getFileStatus(fileId: String): FileHandle? {
        val file = files[fileId] ?: return null
        
        return file.mutex.withLock {
            delay(Random.nextLong(5, 15))
            file.copy()
        }
    }
    
    suspend fun getDirectoryStatus(directoryId: String): DirectoryLock? {
        val directory = directories[directoryId] ?: return null
        
        return directory.mutex.withLock {
            delay(Random.nextLong(5, 15))
            directory.copy()
        }
    }
    
    fun getAllFiles() = files.values.toList()
    fun getAllDirectories() = directories.values.toList()
}

suspend fun simulateFileOpening(
    fileManager: FileManager,
    clientId: Int
) {
    val files = fileManager.getAllFiles()
    
    repeat(10) { attempt ->
        val file = files.filter { !it.inUse }.randomOrNull()
        
        if (file != null) {
            val success = fileManager.openFile(file.fileId)
            if (success) {
                println("Client $clientId: Opened ${file.fileId}")
            } else {
                println("Client $clientId: Failed to open ${file.fileId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateFileClosing(
    fileManager: FileManager,
    clientId: Int
) {
    val files = fileManager.getAllFiles()
    
    repeat(10) { attempt ->
        val file = files.filter { it.inUse }.randomOrNull()
        
        if (file != null) {
            val success = fileManager.closeFile(file.fileId)
            if (success) {
                println("Client $clientId: Closed ${file.fileId}")
            } else {
                println("Client $clientId: Failed to close ${file.fileId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateDirectoryLocking(
    fileManager: FileManager,
    clientId: Int
) {
    val directories = fileManager.getAllDirectories()
    
    repeat(8) { attempt ->
        val directory = directories.filter { !it.locked }.randomOrNull()
        
        if (directory != null) {
            val success = fileManager.lockDirectory(directory.directoryId)
            if (success) {
                println("Client $clientId: Locked ${directory.directoryId}")
            } else {
                println("Client $clientId: Failed to lock ${directory.directoryId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateDirectoryUnlocking(
    fileManager: FileManager,
    clientId: Int
) {
    val directories = fileManager.getAllDirectories()
    
    repeat(8) { attempt ->
        val directory = directories.filter { it.locked }.randomOrNull()
        
        if (directory != null) {
            val success = fileManager.unlockDirectory(directory.directoryId)
            if (success) {
                println("Client $clientId: Unlocked ${directory.directoryId}")
            } else {
                println("Client $clientId: Failed to unlock ${directory.directoryId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateFileWriting(
    fileManager: FileManager,
    writerId: Int
) {
    val files = fileManager.getAllFiles()
    val directories = fileManager.getAllDirectories()
    
    repeat(10) { attempt ->
        val file = files.filter { it.inUse }.randomOrNull()
        val directory = directories.filter { it.locked }.randomOrNull()
        
        if (file != null && directory != null) {
            val success = fileManager.writeFileWithDirectoryLock(
                file.fileId,
                directory.directoryId
            )
            
            if (success) {
                println("Writer $writerId: Wrote to ${file.fileId} with ${directory.directoryId}")
            } else {
                println("Writer $writerId: Failed to write")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateFileTransfer(
    fileManager: FileManager,
    transferId: Int
) {
    val files = fileManager.getAllFiles()
    
    repeat(6) { attempt ->
        val inUseFiles = files.filter { it.inUse }
        val availableFiles = files.filter { !it.inUse }
        
        if (inUseFiles.isNotEmpty() && availableFiles.isNotEmpty()) {
            val fromFile = inUseFiles.random()
            val toFile = availableFiles.random()
            
            val success = fileManager.transferFile(fromFile.fileId, toFile.fileId)
            
            if (success) {
                println("Transfer $transferId: ${fromFile.fileId} -> ${toFile.fileId}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateFileSwap(
    fileManager: FileManager,
    swapId: Int
) {
    val files = fileManager.getAllFiles()
    
    repeat(5) { attempt ->
        val inUseFiles = files.filter { it.inUse }
        
        if (inUseFiles.size >= 2) {
            val file1 = inUseFiles.random()
            val file2 = inUseFiles.filter { it.fileId != file1.fileId }.random()
            
            val success = fileManager.swapFiles(file1.fileId, file2.fileId)
            
            if (success) {
                println("Swap $swapId: ${file1.fileId} <-> ${file2.fileId}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorFileManager(
    fileManager: FileManager,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val files = fileManager.getAllFiles()
        val directories = fileManager.getAllDirectories()
        
        val inUseFiles = files.count { it.inUse }
        val lockedFiles = files.count { it.locked }
        val lockedDirectories = directories.count { it.locked }
        
        println("Monitor $monitorId: InUseFiles=$inUseFiles, LockedFiles=$lockedFiles, " +
                "LockedDirs=$lockedDirectories")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val fileManager = FileManager()
    
    println("Starting File Processing Simulation...")
    println("Initial File Status:")
    fileManager.getAllFiles().forEach { file ->
        println("  ${file.fileId} (${file.filePath}): InUse=${file.inUse}, Locked=${file.locked}")
    }
    println()
    
    println("Initial Directory Status:")
    fileManager.getAllDirectories().forEach { directory ->
        println("  ${directory.directoryId} (${directory.directoryPath}): Locked=${directory.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateFileOpening(fileManager, 1)
    })
    
    jobs.add(launch {
        simulateFileOpening(fileManager, 2)
    })
    
    jobs.add(launch {
        simulateFileClosing(fileManager, 1)
    })
    
    jobs.add(launch {
        simulateFileClosing(fileManager, 2)
    })
    
    jobs.add(launch {
        simulateDirectoryLocking(fileManager, 1)
    })
    
    jobs.add(launch {
        simulateDirectoryUnlocking(fileManager, 1)
    })
    
    jobs.add(launch {
        simulateFileWriting(fileManager, 1)
    })
    
    jobs.add(launch {
        simulateFileTransfer(fileManager, 1)
    })
    
    jobs.add(launch {
        simulateFileSwap(fileManager, 1)
    })
    
    jobs.add(launch {
        monitorFileManager(fileManager, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val files = fileManager.getAllFiles()
    val directories = fileManager.getAllDirectories()
    
    println("\n=== Final File Status ===")
    files.forEach { file ->
        println("  ${file.fileId} (${file.filePath}): InUse=${file.inUse}, Locked=${file.locked}")
    }
    
    println("\n=== Final Directory Status ===")
    directories.forEach { directory ->
        println("  ${directory.directoryId} (${directory.directoryPath}): Locked=${directory.locked}")
    }
    
    val inUseFiles = files.count { it.inUse }
    val lockedFiles = files.count { it.locked }
    val lockedDirectories = directories.count { it.locked }
    
    println("\nInUse Files: $inUseFiles/${files.size}")
    println("Locked Files: $lockedFiles/${files.size}")
    println("Locked Directories: $lockedDirectories/${directories.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - openFile(): filePoolMutex -> file.mutex")
    println("  - closeFile(): file.mutex -> filePoolMutex")
    println("  - lockDirectory(): directoryPoolMutex -> directory.mutex")
    println("  - unlockDirectory(): directory.mutex -> directoryPoolMutex")
    println("  - writeFileWithDirectoryLock(): file.mutex -> directory.mutex")
    println("  - transferFile(): file1.mutex -> file2.mutex")
    println("  - swapFiles(): file1.mutex -> file2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}