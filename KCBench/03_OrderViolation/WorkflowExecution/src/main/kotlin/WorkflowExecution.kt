import kotlinx.coroutines.*
import kotlin.random.Random

data class WorkflowStep(
    val stepId: String,
    val stepName: String,
    var pending: Boolean = true,
    var running: Boolean = false,
    var completed: Boolean = false,
    val dependencies: List<String>
)

data class WorkflowInstance(
    val instanceId: String,
    val workflowName: String,
    val steps: MutableList<WorkflowStep>,
    var started: Boolean = false,
    var completed: Boolean = false
)

class WorkflowExecutionManager {
    private val workflows = mutableMapOf<String, WorkflowInstance>()
    
    init {
        initializeWorkflows()
    }
    
    private fun initializeWorkflows() {
        val workflowConfigs = listOf(
            Triple(
                "OrderProcessing",
                listOf(
                    Pair("ValidateOrder", listOf()),
                    Pair("CheckInventory", listOf("ValidateOrder")),
                    Pair("ReserveInventory", listOf("CheckInventory")),
                    Pair("ProcessPayment", listOf("ReserveInventory")),
                    Pair("ConfirmOrder", listOf("ProcessPayment")),
                    Pair("ShipOrder", listOf("ConfirmOrder")),
                    Pair("NotifyCustomer", listOf("ShipOrder"))
                )
            ),
            Triple(
                "UserRegistration",
                listOf(
                    Pair("ValidateEmail", listOf()),
                    Pair("CheckDuplicate", listOf("ValidateEmail")),
                    Pair("CreateAccount", listOf("CheckDuplicate")),
                    Pair("SendVerification", listOf("CreateAccount")),
                    Pair("CompleteRegistration", listOf("SendVerification"))
                )
            ),
            Triple(
                "DataProcessing",
                listOf(
                    Pair("IngestData", listOf()),
                    Pair("ValidateData", listOf("IngestData")),
                    Pair("TransformData", listOf("ValidateData")),
                    Pair("EnrichData", listOf("TransformData")),
                    Pair("StoreData", listOf("EnrichData")),
                    Pair("GenerateReport", listOf("StoreData"))
                )
            )
        )
        
        workflowConfigs.forEach { (workflowName, steps) ->
            val workflowSteps = steps.map { (stepName, deps) ->
                WorkflowStep(
                    stepId = "${workflowName}_${stepName}",
                    stepName = stepName,
                    pending = true,
                    running = false,
                    completed = false,
                    dependencies = deps
                )
            }.toMutableList()
            
            workflows[workflowName] = WorkflowInstance(
                instanceId = "WF_${workflowName}_${System.currentTimeMillis()}",
                workflowName = workflowName,
                steps = workflowSteps,
                started = false,
                completed = false
            )
        }
    }
    
    suspend fun startWorkflow(workflowName: String): Boolean {
        val workflow = workflows[workflowName] ?: return false
        
        if (workflow.started) {
            return true
        }
        
        workflow.started = true
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun executeStep(
        workflowName: String,
        stepId: String
    ): Boolean {
        val workflow = workflows[workflowName] ?: return false
        val step = workflow.steps.find { it.stepId == stepId } ?: return false
        
        if (step.completed) {
            return true
        }
        
        if (step.running) {
            return false
        }
        
        val dependenciesCompleted = step.dependencies.all { depStepId ->
            val depStep = workflow.steps.find { it.stepId == depStepId }
            depStep != null && depStep.completed
        }
        
        if (!dependenciesCompleted) {
            return false
        }
        
        step.pending = false
        step.running = true
        delay(Random.nextLong(20, 80))
        
        step.running = false
        step.completed = true
        delay(Random.nextLong(10, 20))
        
        val allCompleted = workflow.steps.all { it.completed }
        if (allCompleted) {
            workflow.completed = true
        }
        
        return true
    }
    
    suspend fun executeWorkflow(workflowName: String): Int {
        val workflow = workflows[workflowName] ?: return 0
        
        if (!workflow.started) {
            startWorkflow(workflowName)
        }
        
        var executed = 0
        
        workflow.steps.forEach { step ->
            if (executeStep(workflowName, step.stepId)) {
                executed++
            }
        }
        
        return executed
    }
    
    suspend fun resetStep(
        workflowName: String,
        stepId: String
    ): Boolean {
        val workflow = workflows[workflowName] ?: return false
        val step = workflow.steps.find { it.stepId == stepId } ?: return false
        
        step.pending = true
        step.running = false
        step.completed = false
        
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun resetWorkflow(workflowName: String): Boolean {
        val workflow = workflows[workflowName] ?: return false
        
        workflow.steps.forEach { step ->
            step.pending = true
            step.running = false
            step.completed = false
        }
        
        workflow.started = false
        workflow.completed = false
        
        delay(Random.nextLong(20, 50))
        
        return true
    }
    
    suspend fun getWorkflowStatus(workflowName: String): WorkflowInstance? {
        return workflows[workflowName]
    }
    
    fun getAllWorkflows() = workflows.values.toList()
}

class WorkflowExecutor(
    private val workflowManager: WorkflowExecutionManager,
    private val executorName: String
) {
    suspend fun executeRandomWorkflowStep(): Boolean {
        val workflows = workflowManager.getAllWorkflows()
        val activeWorkflows = workflows.filter { it.started && !it.completed }
        
        if (activeWorkflows.isEmpty()) {
            return false
        }
        
        val workflow = activeWorkflows.random()
        val pendingSteps = workflow.steps.filter { !it.completed && !it.running }
        
        if (pendingSteps.isEmpty()) {
            return false
        }
        
        val step = pendingSteps.random()
        return workflowManager.executeStep(workflow.workflowName, step.stepId)
    }
    
    suspend fun executeMultipleSteps(count: Int): Int {
        var executed = 0
        
        repeat(count) {
            if (executeRandomWorkflowStep()) {
                executed++
            }
        }
        
        return executed
    }
}

suspend fun simulateWorkflowExecution(
    executor: WorkflowExecutor,
    executorId: Int
) {
    repeat(15) { attempt ->
        executor.executeMultipleSteps(3)
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateWorkflowRestart(
    workflowManager: WorkflowExecutionManager
) {
    repeat(8) { attempt ->
        val workflows = workflowManager.getAllWorkflows()
        val workflow = workflows.random()
        
        workflowManager.resetWorkflow(workflow.workflowName)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateWorkflowStart(
    workflowManager: WorkflowExecutionManager
) {
    repeat(12) { attempt ->
        val workflows = workflowManager.getAllWorkflows()
        val workflow = workflows.random()
        
        if (!workflow.started) {
            workflowManager.startWorkflow(workflow.workflowName)
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateWorkflowMonitoring(
    workflowManager: WorkflowExecutionManager
) {
    repeat(20) { attempt ->
        val workflows = workflowManager.getAllWorkflows()
        
        println("Workflow Status:")
        workflows.forEach { workflow ->
            val pending = workflow.steps.count { it.pending }
            val running = workflow.steps.count { it.running }
            val completed = workflow.steps.count { it.completed }
            
            println(
                "  ${workflow.workflowName}: " +
                "Started=${workflow.started}, " +
                "Completed=${workflow.completed}, " +
                "Steps: Pending=$pending, Running=$running, Completed=$completed"
            )
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun simulateFullWorkflowExecution(
    workflowManager: WorkflowExecutionManager
) {
    repeat(6) { attempt ->
        val workflows = workflowManager.getAllWorkflows()
        workflows.forEach { workflow ->
            workflowManager.executeWorkflow(workflow.workflowName)
        }
        delay(Random.nextLong(200, 500))
    }
}

fun main() = runBlocking {
    val workflowManager = WorkflowExecutionManager()
    
    println("Starting Workflow Execution Simulation...")
    println("Initial Workflow Status:")
    workflowManager.getAllWorkflows().forEach { workflow ->
        println(
            "  ${workflow.workflowName}: Started=${workflow.started}, " +
            "Steps=${workflow.steps.size}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val executors = listOf(
        WorkflowExecutor(workflowManager, "Alice"),
        WorkflowExecutor(workflowManager, "Bob"),
        WorkflowExecutor(workflowManager, "Charlie"),
        WorkflowExecutor(workflowManager, "David")
    )
    
    executors.forEachIndexed { index, executor ->
        jobs.add(launch {
            simulateWorkflowExecution(executor, index)
        })
    }
    
    jobs.add(launch {
        simulateWorkflowRestart(workflowManager)
    })
    
    jobs.add(launch {
        simulateWorkflowStart(workflowManager)
    })
    
    jobs.add(launch {
        simulateWorkflowMonitoring(workflowManager)
    })
    
    jobs.add(launch {
        simulateFullWorkflowExecution(workflowManager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val workflows = workflowManager.getAllWorkflows()
    
    println("\n=== Final Workflow Status ===")
    workflows.forEach { workflow ->
        println(
            "  ${workflow.workflowName}: " +
            "Started=${workflow.started}, " +
            "Completed=${workflow.completed}"
        )
        
        workflow.steps.forEach { step ->
            println(
                "    ${step.stepName}: " +
                "Pending=${step.pending}, " +
                "Running=${step.running}, " +
                "Completed=${step.completed}, " +
                "Deps=${step.dependencies.joinToString()}"
            )
        }
    }
    
    val outOfOrderSteps = workflows.flatMap { workflow ->
        workflow.steps.filter { step ->
            step.completed && !step.dependencies.all { depStepId ->
                val depStep = workflow.steps.find { it.stepId == depStepId }
                depStep != null && depStep.completed
            }
        }
    }
    
    if (outOfOrderSteps.isNotEmpty()) {
        println("\n⚠️  Steps completed before dependencies:")
        outOfOrderSteps.take(5).forEach { step ->
            println("  ${step.stepId}: ${step.dependencies}")
        }
    } else {
        println("\n✅ All steps completed in correct order")
    }
    
    val totalSteps = workflows.sumOf { it.steps.size }
    val completedSteps = workflows.sumOf { it.steps.count { it.completed } }
    
    println("\nCompleted Steps: $completedSteps/$totalSteps")
}