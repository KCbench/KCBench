import kotlinx.coroutines.*
import kotlin.random.Random

data class DataNode(
    val nodeId: String,
    var value: String,
    var computed: Boolean = false,
    val dependencies: List<String>
)

class DataFlowManager {
    private val nodes = mutableMapOf<String, DataNode>()
    
    init {
        initializeNodes()
    }
    
    private fun initializeNodes() {
        val nodeConfigs = listOf(
            Triple("Source1", listOf(), "Source Data 1"),
            Triple("Source2", listOf(), "Source Data 2"),
            Triple("Transform1", listOf("Source1"), "Transformed Data 1"),
            Triple("Transform2", listOf("Source2"), "Transformed Data 2"),
            Triple("Merge1", listOf("Transform1", "Transform2"), "Merged Data 1"),
            Triple("Filter1", listOf("Merge1"), "Filtered Data 1"),
            Triple("Aggregate1", listOf("Filter1"), "Aggregated Data 1"),
            Triple("Output1", listOf("Aggregate1"), "Final Output 1"),
            Triple("Source3", listOf(), "Source Data 3"),
            Triple("Transform3", listOf("Source3"), "Transformed Data 3"),
            Triple("Merge2", listOf("Aggregate1", "Transform3"), "Merged Data 2"),
            Triple("Output2", listOf("Merge2"), "Final Output 2")
        )
        
        nodeConfigs.forEach { (nodeId, deps, value) ->
            nodes[nodeId] = DataNode(
                nodeId = nodeId,
                value = value,
                computed = false,
                dependencies = deps
            )
        }
    }
    
    suspend fun computeNode(nodeId: String): Boolean {
        val node = nodes[nodeId] ?: return false
        
        if (node.computed) {
            return true
        }
        
        val dependenciesReady = node.dependencies.all { depId ->
            val depNode = nodes[depId]
            depNode != null && depNode.computed
        }
        
        if (!dependenciesReady) {
            return false
        }
        
        delay(Random.nextLong(10, 50))
        
        node.computed = true
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun computeAllNodes(): Int {
        var computed = 0
        
        nodes.keys.forEach { nodeId ->
            if (computeNode(nodeId)) {
                computed++
            }
        }
        
        return computed
    }
    
    suspend fun resetNode(nodeId: String): Boolean {
        val node = nodes[nodeId] ?: return false
        
        node.computed = false
        delay(Random.nextLong(5, 20))
        
        return true
    }
    
    suspend fun resetAllNodes(): Int {
        var reset = 0
        
        nodes.keys.forEach { nodeId ->
            if (resetNode(nodeId)) {
                reset++
            }
        }
        
        return reset
    }
    
    suspend fun getNodeValue(nodeId: String): String? {
        val node = nodes[nodeId]
        
        if (node != null && node.computed) {
            return node.value
        }
        
        return null
    }
    
    suspend fun getComputedNodes(): List<DataNode> {
        return nodes.values.filter { it.computed }
    }
    
    suspend fun getUncomputedNodes(): List<DataNode> {
        return nodes.values.filter { !it.computed }
    }
    
    fun getAllNodes() = nodes.values.toList()
}

class DataFlowProcessor(
    private val flowManager: DataFlowManager,
    private val processorName: String
) {
    suspend fun processRandomNode(): Boolean {
        val uncomputedNodes = flowManager.getUncomputedNodes()
        
        if (uncomputedNodes.isEmpty()) {
            return false
        }
        
        val node = uncomputedNodes.random()
        return flowManager.computeNode(node.nodeId)
    }
    
    suspend fun processMultipleNodes(count: Int): Int {
        var processed = 0
        
        repeat(count) {
            if (processRandomNode()) {
                processed++
            }
        }
        
        return processed
    }
}

suspend fun simulateDataFlowProcessing(
    processor: DataFlowProcessor,
    processorId: Int
) {
    repeat(15) { attempt ->
        processor.processMultipleNodes(3)
        delay(Random.nextLong(20, 80))
    }
}

suspend fun simulateDataFlowReset(
    flowManager: DataFlowManager
) {
    repeat(10) { attempt ->
        val nodes = flowManager.getAllNodes()
        val node = nodes.random()
        
        flowManager.resetNode(node.nodeId)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateDependencyComputation(
    flowManager: DataFlowManager
) {
    repeat(12) { attempt ->
        val nodes = flowManager.getAllNodes()
        val uncomputedNodes = nodes.filter { !it.computed }
        
        if (uncomputedNodes.isNotEmpty()) {
            val node = uncomputedNodes.random()
            
            if (node.dependencies.isEmpty()) {
                flowManager.computeNode(node.nodeId)
            }
        }
        
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateDataFlowValidation(
    flowManager: DataFlowManager
) {
    repeat(15) { attempt ->
        val computedNodes = flowManager.getComputedNodes()
        val uncomputedNodes = flowManager.getUncomputedNodes()
        
        println("Data Flow Status:")
        println("  Computed: ${computedNodes.size}")
        println("  Uncomputed: ${uncomputedNodes.size}")
        
        val invalidNodes = computedNodes.filter { node ->
            !node.dependencies.all { depId ->
                val depNode = flowManager.getAllNodes().find { it.nodeId == depId }
                depNode != null && depNode.computed
            }
        }
        
        if (invalidNodes.isNotEmpty()) {
            println("  Invalid computed nodes: ${invalidNodes.size}")
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun simulateBatchComputation(
    flowManager: DataFlowManager
) {
    repeat(8) { attempt ->
        flowManager.computeAllNodes()
        delay(Random.nextLong(150, 400))
    }
}

fun main() = runBlocking {
    val flowManager = DataFlowManager()
    
    println("Starting Data Flow Simulation...")
    println("Initial Node Status:")
    flowManager.getAllNodes().forEach { node ->
        println(
            "  ${node.nodeId}: Computed=${node.computed}, " +
            "Deps=${node.dependencies.joinToString()}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val processors = listOf(
        DataFlowProcessor(flowManager, "Alice"),
        DataFlowProcessor(flowManager, "Bob"),
        DataFlowProcessor(flowManager, "Charlie"),
        DataFlowProcessor(flowManager, "David")
    )
    
    processors.forEachIndexed { index, processor ->
        jobs.add(launch {
            simulateDataFlowProcessing(processor, index)
        })
    }
    
    jobs.add(launch {
        simulateDataFlowReset(flowManager)
    })
    
    jobs.add(launch {
        simulateDependencyComputation(flowManager)
    })
    
    jobs.add(launch {
        simulateDataFlowValidation(flowManager)
    })
    
    jobs.add(launch {
        simulateBatchComputation(flowManager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val computedNodes = flowManager.getComputedNodes()
    val uncomputedNodes = flowManager.getUncomputedNodes()
    
    println("\n=== Final Node Status ===")
    computedNodes.forEach { node ->
        println(
            "  ${node.nodeId}: Computed=${node.computed}, " +
            "Deps=${node.dependencies.joinToString()}"
        )
    }
    
    val invalidComputed = computedNodes.filter { node ->
        !node.dependencies.all { depId ->
            val depNode = flowManager.getAllNodes().find { it.nodeId == depId }
            depNode != null && depNode.computed
        }
    }
    
    if (invalidComputed.isNotEmpty()) {
        println("\n⚠️  Nodes computed before dependencies:")
        invalidComputed.forEach { node ->
            println("  ${node.nodeId}: ${node.dependencies}")
        }
    } else {
        println("\n✅ All nodes computed in correct order")
    }
    
    val totalNodes = flowManager.getAllNodes().size
    val computationRate = if (totalNodes > 0) {
        (computedNodes.size.toDouble() / totalNodes * 100)
    } else {
        0.0
    }
    
    println("\nComputation Rate: ${"%.2f".format(computationRate)}%")
    println("Computed Nodes: ${computedNodes.size}/$totalNodes")
}