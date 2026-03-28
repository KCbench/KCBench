import kotlinx.coroutines.*
import kotlin.random.Random

data class FileMetadata(
    val fileId: String,
    val fileName: String,
    var size: Long,
    var uploadedBytes: Long,
    var status: FileStatus,
    var uploadSpeed: Double,
    var lastUpdateTime: Long
)

enum class FileStatus {
    PENDING, UPLOADING, COMPLETED, FAILED, PAUSED
}

class FileUploadManager {
    private val files = mutableMapOf<String, FileMetadata>()
    private var totalUploadSize = 0L
    private var uploadedSize = 0L
    private var activeUploads = 0
    private var completedUploads = 0
    
    suspend fun createFileUpload(
        fileName: String,
        fileSize: Long
    ): FileMetadata {
        val fileId = "file_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        val currentTime = System.currentTimeMillis()
        
        val file = FileMetadata(
            fileId = fileId,
            fileName = fileName,
            size = fileSize,
            uploadedBytes = 0L,
            status = FileStatus.PENDING,
            uploadSpeed = 0.0,
            lastUpdateTime = currentTime
        )
        
        files[fileId] = file
        delay(Random.nextLong(1, 10))
        
        val currentTotal = totalUploadSize
        delay(Random.nextLong(1, 5))
        totalUploadSize = currentTotal + fileSize
        
        return file
    }
    
    suspend fun startUpload(fileId: String): Boolean {
        val file = files[fileId] ?: return false
        
        val currentStatus = file.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == FileStatus.PENDING || 
            currentStatus == FileStatus.PAUSED) {
            file.status = FileStatus.UPLOADING
            delay(Random.nextLong(1, 5))
            
            file.lastUpdateTime = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            val currentActive = activeUploads
            delay(Random.nextLong(1, 5))
            activeUploads = currentActive + 1
            
            return true
        }
        
        return false
    }
    
    suspend fun updateUploadProgress(
        fileId: String,
        bytesUploaded: Long
    ): Boolean {
        val file = files[fileId] ?: return false
        
        val currentUploaded = file.uploadedBytes
        delay(Random.nextLong(1, 10))
        
        file.uploadedBytes = currentUploaded + bytesUploaded
        delay(Random.nextLong(1, 5))
        
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - file.lastUpdateTime
        
        if (timeDiff > 0) {
            file.uploadSpeed = bytesUploaded.toDouble() / timeDiff * 1000
        }
        
        file.lastUpdateTime = currentTime
        delay(Random.nextLong(1, 5))
        
        val currentUploadedSize = uploadedSize
        delay(Random.nextLong(1, 5))
        uploadedSize = currentUploadedSize + bytesUploaded
        
        if (file.uploadedBytes >= file.size) {
            completeUpload(fileId)
        }
        
        return true
    }
    
    private suspend fun completeUpload(fileId: String) {
        val file = files[fileId] ?: return
        
        file.status = FileStatus.COMPLETED
        delay(Random.nextLong(1, 5))
        
        file.uploadedBytes = file.size
        delay(Random.nextLong(1, 5))
        
        val currentCompleted = completedUploads
        delay(Random.nextLong(1, 5))
        completedUploads = currentCompleted + 1
        
        val currentActive = activeUploads
        delay(Random.nextLong(1, 5))
        activeUploads = maxOf(0, currentActive - 1)
    }
    
    suspend fun pauseUpload(fileId: String): Boolean {
        val file = files[fileId] ?: return false
        
        val currentStatus = file.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == FileStatus.UPLOADING) {
            file.status = FileStatus.PAUSED
            delay(Random.nextLong(1, 5))
            
            file.lastUpdateTime = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            val currentActive = activeUploads
            delay(Random.nextLong(1, 5))
            activeUploads = maxOf(0, currentActive - 1)
            
            return true
        }
        
        return false
    }
    
    suspend fun failUpload(fileId: String): Boolean {
        val file = files[fileId] ?: return false
        
        val currentStatus = file.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == FileStatus.UPLOADING) {
            file.status = FileStatus.FAILED
            delay(Random.nextLong(1, 5))
            
            file.lastUpdateTime = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            val currentActive = activeUploads
            delay(Random.nextLong(1, 5))
            activeUploads = maxOf(0, currentActive - 1)
            
            return true
        }
        
        return false
    }
    
    suspend fun getUploadProgress(fileId: String): Double? {
        val file = files[fileId] ?: return null
        
        val currentUploaded = file.uploadedBytes
        delay(Random.nextLong(1, 5))
        
        val currentSize = file.size
        delay(Random.nextLong(1, 5))
        
        return if (currentSize > 0) {
            (currentUploaded.toDouble() / currentSize * 100)
        } else {
            0.0
        }
    }
    
    suspend fun getStatistics(): Triple<Long, Long, Int> {
        val currentTotal = totalUploadSize
        delay(Random.nextLong(1, 5))
        
        val currentUploaded = uploadedSize
        delay(Random.nextLong(1, 5))
        
        val currentCompleted = completedUploads
        delay(Random.nextLong(1, 5))
        
        return Triple(currentTotal, currentUploaded, currentCompleted)
    }
    
    fun getAllFiles() = files.values.toList()
    
    fun getFilesByStatus(status: FileStatus) = 
        files.values.filter { it.status == status }
}

class FileUploader(
    private val manager: FileUploadManager
) {
    suspend fun uploadFile(
        fileId: String,
        chunkSize: Long = 1024 * 1024
    ): Boolean {
        if (!manager.startUpload(fileId)) {
            return false
        }
        
        val file = manager.getAllFiles().find { it.fileId == fileId }
        if (file == null) return false
        
        while (true) {
            val progress = manager.getUploadProgress(fileId) ?: 0.0
            
            if (progress >= 100.0) {
                break
            }
            
            val bytesToUpload = minOf(
                chunkSize,
                file.size - file.uploadedBytes
            )
            
            if (bytesToUpload <= 0) break
            
            manager.updateUploadProgress(fileId, bytesToUpload)
            
            delay(Random.nextLong(10, 50))
            
            if (Random.nextDouble() < 0.05) {
                manager.pauseUpload(fileId)
                delay(Random.nextLong(100, 500))
                manager.startUpload(fileId)
            }
        }
        
        return true
    }
}

suspend fun simulateFileUpload(
    manager: FileUploadManager,
    uploader: FileUploader,
    fileName: String
) {
    val fileSize = Random.nextLong(10 * 1024 * 1024, 100 * 1024 * 1024)
    
    val file = manager.createFileUpload(fileName, fileSize)
    println("Created file upload: ${file.fileName} (${fileSize / (1024 * 1024)} MB)")
    
    uploader.uploadFile(file.fileId)
    
    val finalProgress = manager.getUploadProgress(file.fileId)
    println("File ${file.fileName} upload progress: ${"%.2f".format(finalProgress ?: 0.0)}%")
}

suspend fun simulateMultipleUploads(
    manager: FileUploadManager,
    uploader: FileUploader,
    count: Int
) {
    coroutineScope {
        val fileNames = listOf(
            "document.pdf", "video.mp4", "archive.zip",
            "image.jpg", "database.sql", "backup.tar",
            "presentation.pptx", "spreadsheet.xlsx", "audio.mp3",
            "project.zip", "data.json", "config.xml"
        )
        
        repeat(count) { index ->
            val fileName = "${fileNames.random()}_${index}"
            
            launch {
                simulateFileUpload(manager, uploader, fileName)
            }
            
            delay(Random.nextLong(100, 500))
        }
    }
}

suspend fun simulateUploadMonitoring(
    manager: FileUploadManager
) {
    repeat(20) { attempt ->
        val activeFiles = manager.getFilesByStatus(FileStatus.UPLOADING)
        
        if (activeFiles.isNotEmpty()) {
            println("\n=== Active Uploads ===")
            activeFiles.take(5).forEach { file ->
                val progress = (file.uploadedBytes.toDouble() / file.size * 100)
                println(
                    "  ${file.fileName}: " +
                    "${"%.2f".format(progress)}% " +
                    "(${file.uploadedBytes / (1024 * 1024)}/${file.size / (1024 * 1024)} MB) " +
                    "Speed: ${"%.2f".format(file.uploadSpeed / 1024)} KB/s"
                )
            }
        }
        
        val completedFiles = manager.getFilesByStatus(FileStatus.COMPLETED)
        val failedFiles = manager.getFilesByStatus(FileStatus.FAILED)
        val pausedFiles = manager.getFilesByStatus(FileStatus.PAUSED)
        
        println(
            "Status: Active=${activeFiles.size}, " +
            "Completed=${completedFiles.size}, " +
            "Failed=${failedFiles.size}, " +
            "Paused=${pausedFiles.size}"
        )
        
        delay(Random.nextLong(500, 1000))
    }
}

suspend fun simulateUploadFailures(
    manager: FileUploadManager
) {
    repeat(5) { attempt ->
        val activeFiles = manager.getFilesByStatus(FileStatus.UPLOADING)
        
        if (activeFiles.isNotEmpty()) {
            val file = activeFiles.random()
            manager.failUpload(file.fileId)
            println("Failed upload: ${file.fileName}")
        }
        
        delay(Random.nextLong(1000, 2000))
    }
}

fun main() = runBlocking {
    val manager = FileUploadManager()
    val uploader = FileUploader(manager)
    
    println("Starting File Upload Simulation...")
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMultipleUploads(manager, uploader, 15)
    })
    
    delay(2000)
    
    jobs.add(launch {
        simulateUploadMonitoring(manager)
    })
    
    jobs.add(launch {
        simulateUploadFailures(manager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (totalSize, uploadedSize, completedUploads) = manager.getStatistics()
    
    println("\n=== Upload Statistics ===")
    println("Total Upload Size: ${totalSize / (1024 * 1024)} MB")
    println("Uploaded Size: ${uploadedSize / (1024 * 1024)} MB")
    println("Completed Uploads: $completedUploads")
    
    val statusCounts = manager.getAllFiles()
        .groupingBy { it.status }
        .eachCount()
    
    println("\n=== Upload Status Distribution ===")
    statusCounts.forEach { (status, count) ->
        println("  $status: $count")
    }
    
    println("\n=== Final File Status ===")
    manager.getAllFiles().takeLast(10).forEach { file ->
        val progress = (file.uploadedBytes.toDouble() / file.size * 100)
        println(
            "  ${file.fileName}: " +
            "${"%.2f".format(progress)}% " +
            "[${file.status}] " +
            "Speed: ${"%.2f".format(file.uploadSpeed / 1024)} KB/s"
        )
    }
    
    val incompleteFiles = manager.getAllFiles()
        .filter { it.status != FileStatus.COMPLETED }
    
    if (incompleteFiles.isNotEmpty()) {
        println("\n⚠️  Incomplete Uploads: ${incompleteFiles.size}")
        incompleteFiles.take(5).forEach { file ->
            println(
                "  ${file.fileName}: " +
                "${file.uploadedBytes / (1024 * 1024)}/${file.size / (1024 * 1024)} MB " +
                "[${file.status}]"
            )
        }
    }
    
    val averageSpeed = manager.getAllFiles()
        .filter { it.uploadSpeed > 0 }
        .map { it.uploadSpeed }
        .average()
    
    if (averageSpeed > 0) {
        println("\nAverage Upload Speed: ${"%.2f".format(averageSpeed / 1024)} KB/s")
    }
    
    val totalProgress = if (totalSize > 0) {
        (uploadedSize.toDouble() / totalSize * 100)
    } else {
        0.0
    }
    
    println("\nOverall Progress: ${"%.2f".format(totalProgress)}%")
}