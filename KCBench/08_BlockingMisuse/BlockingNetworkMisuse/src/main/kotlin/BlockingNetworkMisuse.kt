import kotlinx.coroutines.*
import kotlin.random.Random
import java.net.URL
import java.net.HttpURLConnection

class BlockingNetworkProcessor {
    private var counter = 0
    
    suspend fun processRequest(url: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Processing request to $url")
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val responseCode = connection.responseCode
            Thread.sleep(100)
            counter++
            println("Request to $url completed (Code: $responseCode, Counter: $counter)")
            connection.disconnect()
        }
    }
    
    suspend fun processMultipleRequests(urls: List<String>) = coroutineScope {
        urls.forEach { url ->
            processRequest(url)
            delay(50)
        }
    }
    
    fun getCounter() = counter
}

class BlockingNetworkDownloader {
    private var downloadedCount = 0
    
    suspend fun downloadContent(url: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Downloading content from $url")
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val content = connection.inputStream.bufferedReader().readText()
            Thread.sleep(100)
            downloadedCount++
            println("Content downloaded from $url (Count: $downloadedCount)")
            connection.disconnect()
        }
    }
    
    suspend fun downloadMultipleContents(urls: List<String>) = coroutineScope {
        urls.forEach { url ->
            downloadContent(url)
            delay(50)
        }
    }
    
    fun getDownloadedCount() = downloadedCount
}

class BlockingNetworkUploader {
    private var uploadedCount = 0
    
    suspend fun uploadData(url: String, data: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Uploading data to $url")
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doOutput = true
            connection.outputStream.bufferedWriter().use { it.write(data) }
            Thread.sleep(100)
            uploadedCount++
            println("Data uploaded to $url (Count: $uploadedCount)")
            connection.disconnect()
        }
    }
    
    suspend fun uploadMultipleData(urls: List<String>, dataList: List<String>) = coroutineScope {
        urls.zip(dataList).forEach { (url, data) ->
            uploadData(url, data)
            delay(50)
        }
    }
    
    fun getUploadedCount() = uploadedCount
}

class BlockingNetworkPinger {
    private var pingCount = 0
    
    suspend fun pingHost(host: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Pinging host $host")
            val address = java.net.InetAddress.getByName(host)
            val isReachable = address.isReachable(5000)
            Thread.sleep(100)
            pingCount++
            println("Host $host pinged (Reachable: $isReachable, Count: $pingCount)")
        }
    }
    
    suspend fun pingMultipleHosts(hosts: List<String>) = coroutineScope {
        hosts.forEach { host ->
            pingHost(host)
            delay(50)
        }
    }
    
    fun getPingCount() = pingCount
}

class BlockingNetworkResolver {
    private var resolveCount = 0
    
    suspend fun resolveHost(host: String) = coroutineScope {
        launch(Dispatchers.Default) {
            println("Resolving host $host")
            val address = java.net.InetAddress.getByName(host)
            Thread.sleep(100)
            resolveCount++
            println("Host $host resolved to ${address.hostAddress} (Count: $resolveCount)")
        }
    }
    
    suspend fun resolveMultipleHosts(hosts: List<String>) = coroutineScope {
        hosts.forEach { host ->
            resolveHost(host)
            delay(50)
        }
    }
    
    fun getResolveCount() = resolveCount
}

suspend fun simulateBlockingNetworkProcessor(
    processor: BlockingNetworkProcessor,
    processorId: Int
) {
    repeat(10) { attempt ->
        val urls = listOf(
            "https://example.com/api1",
            "https://example.com/api2",
            "https://example.com/api3",
            "https://example.com/api4",
            "https://example.com/api5"
        )
        processor.processMultipleRequests(urls)
        delay(100)
    }
    
    println("Blocking network processor $processorId completed")
}

suspend fun simulateBlockingNetworkDownloader(
    downloader: BlockingNetworkDownloader,
    downloaderId: Int
) {
    repeat(10) { attempt ->
        val urls = listOf(
            "https://example.com/content1",
            "https://example.com/content2",
            "https://example.com/content3",
            "https://example.com/content4",
            "https://example.com/content5"
        )
        downloader.downloadMultipleContents(urls)
        delay(100)
    }
    
    println("Blocking network downloader $downloaderId completed")
}

suspend fun simulateBlockingNetworkUploader(
    uploader: BlockingNetworkUploader,
    uploaderId: Int
) {
    repeat(10) { attempt ->
        val urls = listOf(
            "https://example.com/upload1",
            "https://example.com/upload2",
            "https://example.com/upload3",
            "https://example.com/upload4",
            "https://example.com/upload5"
        )
        val dataList = listOf(
            "Data1",
            "Data2",
            "Data3",
            "Data4",
            "Data5"
        )
        uploader.uploadMultipleData(urls, dataList)
        delay(100)
    }
    
    println("Blocking network uploader $uploaderId completed")
}

suspend fun simulateBlockingNetworkPinger(
    pinger: BlockingNetworkPinger,
    pingerId: Int
) {
    repeat(10) { attempt ->
        val hosts = listOf(
            "example.com",
            "google.com",
            "github.com",
            "stackoverflow.com",
            "kotlinlang.org"
        )
        pinger.pingMultipleHosts(hosts)
        delay(100)
    }
    
    println("Blocking network pinger $pingerId completed")
}

suspend fun simulateBlockingNetworkResolver(
    resolver: BlockingNetworkResolver,
    resolverId: Int
) {
    repeat(10) { attempt ->
        val hosts = listOf(
            "example.com",
            "google.com",
            "github.com",
            "stackoverflow.com",
            "kotlinlang.org"
        )
        resolver.resolveMultipleHosts(hosts)
        delay(100)
    }
    
    println("Blocking network resolver $resolverId completed")
}

suspend fun monitorBlockingNetworkOperations(
    processor: BlockingNetworkProcessor,
    downloader: BlockingNetworkDownloader,
    uploader: BlockingNetworkUploader,
    pinger: BlockingNetworkPinger,
    resolver: BlockingNetworkResolver,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Processor counter: ${processor.getCounter()}")
        println("  Downloader count: ${downloader.getDownloadedCount()}")
        println("  Uploader count: ${uploader.getUploadedCount()}")
        println("  Pinger count: ${pinger.getPingCount()}")
        println("  Resolver count: ${resolver.getResolveCount()}")
        
        delay(200)
    }
}

fun main() = runBlocking {
    println("Starting Blocking Network Misuse Simulation...")
    println()
    
    val processor = BlockingNetworkProcessor()
    val downloader = BlockingNetworkDownloader()
    val uploader = BlockingNetworkUploader()
    val pinger = BlockingNetworkPinger()
    val resolver = BlockingNetworkResolver()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateBlockingNetworkProcessor(processor, 1)
    })
    
    jobs.add(launch {
        simulateBlockingNetworkProcessor(processor, 2)
    })
    
    jobs.add(launch {
        simulateBlockingNetworkDownloader(downloader, 1)
    })
    
    jobs.add(launch {
        simulateBlockingNetworkUploader(uploader, 1)
    })
    
    jobs.add(launch {
        simulateBlockingNetworkPinger(pinger, 1)
    })
    
    jobs.add(launch {
        simulateBlockingNetworkResolver(resolver, 1)
    })
    
    jobs.add(launch {
        monitorBlockingNetworkOperations(
            processor,
            downloader,
            uploader,
            pinger,
            resolver,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  Blocking Network Misuse Warning:")
    println("  The code uses blocking network operations in coroutines:")
    println("  - BlockingNetworkProcessor.processRequest() uses HttpURLConnection")
    println("  - BlockingNetworkDownloader.downloadContent() uses HttpURLConnection")
    println("  - BlockingNetworkUploader.uploadData() uses HttpURLConnection")
    println("  - BlockingNetworkPinger.pingHost() uses InetAddress.isReachable()")
    println("  - BlockingNetworkResolver.resolveHost() uses InetAddress.getByName()")
    println("  Blocking network operations block the entire thread, preventing other coroutines from executing.")
    println("  Fix: Use Dispatchers.IO for blocking network operations or use async network libraries.")
}