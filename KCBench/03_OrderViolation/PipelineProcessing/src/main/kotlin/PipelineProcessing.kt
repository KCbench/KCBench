import kotlinx.coroutines.*
import kotlin.random.Random

data class PipelineStage(
    val name: String,
    var ready: Boolean = false,
    var processing: Boolean = false,
    var completed: Boolean = false,
    val dependencies: List<String>
)

data class PipelineItem(
    val id: String,
    val data: String,
    var currentStage: String? = null,
    var completed: Boolean = false
)

class PipelineProcessingSystem {
    private val stages = mutableMapOf<String, PipelineStage>()
    private val items = mutableMapOf<String, PipelineItem>()
    
    init {
        initializeStages()
    }
    
    private fun initializeStages() {
        val stageConfigs = listOf(
            Triple("Validation", listOf(), 50),
            Triple("Transformation", listOf("Validation"), 100),
            Triple("Enrichment", listOf("Transformation"), 80),
            Triple("Filtering", listOf("Enrichment"), 60),
            Triple("Aggregation", listOf("Filtering"), 120),
            Triple("Storage", listOf("Aggregation"), 90),
            Triple("Notification", listOf("Storage"), 70)
        )
        
        stageConfigs.forEach { (name, deps, processTime) ->
            stages[name] = PipelineStage(
                name = name,
                ready = true,
                processing = false,
                completed = false,
                dependencies = deps
            )
        }
    }
    
    suspend fun prepareStage(stageName: String): Boolean {
        val stage = stages[stageName] ?: return false
        
        if (stage.ready && !stage.processing) {
            delay(Random.nextLong(10, 30))
            
            stage.processing = true
            delay(Random.nextLong(10, 20))
            
            return true
        }
        
        return false
    }
    
    suspend fun completeStage(stageName: String): Boolean {
        val stage = stages[stageName] ?: return false
        
        if (stage.processing) {
            delay(Random.nextLong(20, 80))
            
            stage.processing = false
            stage.completed = true
            delay(Random.nextLong(10, 20))
            
            return true
        }
        
        return false
    }
    
    suspend fun resetStage(stageName: String): Boolean {
        val stage = stages[stageName] ?: return false
        
        stage.ready = true
        stage.processing = false
        stage.completed = false
        
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun addItem(item: PipelineItem): Boolean {
        items[item.id] = item
        delay(Random.nextLong(5, 15))
        return true
    }
    
    suspend fun processItem(
        itemId: String,
        stageName: String
    ): Boolean {
        val item = items[itemId] ?: return false
        val stage = stages[stageName] ?: return false
        
        if (!stage.ready || stage.processing) {
            return false
        }
        
        item.currentStage = stageName
        delay(Random.nextLong(10, 30))
        
        if (prepareStage(stageName)) {
            delay(Random.nextLong(20, 80))
            
            completeStage(stageName)
            
            val nextStages = stages.values.filter { s ->
                s.dependencies.contains(stageName) && !s.completed
            }
            
            if (nextStages.isEmpty()) {
                item.completed = true
            }
            
            return true
        }
        
        return false
    }
    
    suspend fun processItemThroughPipeline(itemId: String): Boolean {
        val item = items[itemId] ?: return false
        
        val stageOrder = listOf(
            "Validation", "Transformation", "Enrichment",
            "Filtering", "Aggregation", "Storage", "Notification"
        )
        
        for (stageName in stageOrder) {
            if (!processItem(itemId, stageName)) {
                return false
            }
        }
        
        return true
    }
    
    suspend fun batchProcessItems(itemIds: List<String>): Int {
        var processed = 0
        
        itemIds.forEach { itemId ->
            if (processItemThroughPipeline(itemId)) {
                processed++
            }
        }
        
        return processed
    }
    
    fun getStageStatus(stageName: String): PipelineStage? {
        return stages[stageName]
    }
    
    fun getItemStatus(itemId: String): PipelineItem? {
        return items[itemId]
    }
    
    fun getAllStages() = stages.values.toList()
    
    fun getAllItems() = items.values.toList()
}

class PipelineWorker(
    private val pipeline: PipelineProcessingSystem,
    private val workerName: String
) {
    suspend fun processRandomItem(): Boolean {
        val items = pipeline.getAllItems()
        val unprocessedItems = items.filter { !it.completed }
        
        if (unprocessedItems.isEmpty()) {
            return false
        }
        
        val item = unprocessedItems.random()
        return pipeline.processItemThroughPipeline(item.id)
    }
    
    suspend fun processMultipleItems(count: Int): Int {
        var processed = 0
        
        repeat(count) {
            if (processRandomItem()) {
                processed++
            }
        }
        
        return processed
    }
}

suspend fun simulatePipelineProcessing(
    worker: PipelineWorker,
    workerId: Int
) {
    repeat(12) { attempt ->
        worker.processMultipleItems(3)
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateStageReset(
    pipeline: PipelineProcessingSystem
) {
    repeat(8) { attempt ->
        val stages = pipeline.getAllStages()
        val stage = stages.random()
        
        pipeline.resetStage(stage.name)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateItemInjection(
    pipeline: PipelineProcessingSystem
) {
    repeat(15) { attempt ->
        val item = PipelineItem(
            id = "ITEM_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}",
            data = "Data_${Random.nextInt(100, 999)}",
            currentStage = null,
            completed = false
        )
        
        pipeline.addItem(item)
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulatePipelineMonitoring(
    pipeline: PipelineProcessingSystem
) {
    repeat(20) { attempt ->
        val stages = pipeline.getAllStages()
        
        println("Stage Status:")
        stages.forEach { stage ->
            println(
                "  ${stage.name}: Ready=${stage.ready}, " +
                "Processing=${stage.processing}, " +
                "Completed=${stage.completed}"
            )
        }
        
        val items = pipeline.getAllItems()
        val completed = items.count { it.completed }
        val processing = items.count { !it.completed }
        
        println("Items: Completed=$completed, Processing=$processing")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val pipeline = PipelineProcessingSystem()
    
    println("Starting Pipeline Processing Simulation...")
    println("Initial Stage Status:")
    pipeline.getAllStages().forEach { stage ->
        println(
            "  ${stage.name}: Ready=${stage.ready}, " +
            "Deps=${stage.dependencies.joinToString()}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateItemInjection(pipeline)
    })
    
    val workers = listOf(
        PipelineWorker(pipeline, "Alice"),
        PipelineWorker(pipeline, "Bob"),
        PipelineWorker(pipeline, "Charlie"),
        PipelineWorker(pipeline, "David")
    )
    
    workers.forEachIndexed { index, worker ->
        jobs.add(launch {
            simulatePipelineProcessing(worker, index)
        })
    }
    
    jobs.add(launch {
        simulateStageReset(pipeline)
    })
    
    jobs.add(launch {
        simulatePipelineMonitoring(pipeline)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Stage Status ===")
    pipeline.getAllStages().forEach { stage ->
        println(
            "  ${stage.name}: Ready=${stage.ready}, " +
            "Processing=${stage.processing}, " +
            "Completed=${stage.completed}"
        )
    }
    
    val items = pipeline.getAllItems()
    val completedItems = items.filter { it.completed }
    val processingItems = items.filter { !it.completed }
    
    println("\nItem Status:")
    println("  Total: ${items.size}")
    println("  Completed: ${completedItems.size}")
    println("  Processing: ${processingItems.size}")
    
    val stuckItems = processingItems.filter { item ->
        val stage = item.currentStage
        val stageStatus = pipeline.getStageStatus(stage ?: "")
        
        stageStatus != null && stageStatus.completed
    }
    
    if (stuckItems.isNotEmpty()) {
        println("\n⚠️  Stuck Items:")
        stuckItems.take(5).forEach { item ->
            println("  ${item.id}: Current=${item.currentStage}")
        }
    }
    
    val outOfOrderItems = processingItems.filter { item ->
        val stage = item.currentStage
        val stageStatus = pipeline.getStageStatus(stage ?: "")
        
        stageStatus != null && !stageStatus.dependencies.all { dep ->
            pipeline.getStageStatus(dep)?.completed == true
        }
    }
    
    if (outOfOrderItems.isNotEmpty()) {
        println("\n⚠️  Out of Order Items:")
        outOfOrderItems.take(5).forEach { item ->
            println("  ${item.id}: Current=${item.currentStage}")
        }
    }
}