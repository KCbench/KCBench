import kotlinx.coroutines.*
import kotlin.random.Random

data class StateMachine(
    val name: String,
    var currentState: String,
    var previousState: String? = null,
    val validTransitions: Map<String, List<String>>
)

class StateDependencyManager {
    private val stateMachines = mutableMapOf<String, StateMachine>()
    
    init {
        initializeStateMachines()
    }
    
    private fun initializeStateMachines() {
        val configs = listOf(
            Triple("Order", "Created", mapOf(
                "Created" to listOf("Validated", "Cancelled"),
                "Validated" to listOf("Processing", "Cancelled"),
                "Processing" to listOf("Shipped", "Cancelled"),
                "Shipped" to listOf("Delivered", "Returned"),
                "Delivered" to listOf("Completed"),
                "Cancelled" to listOf(),
                "Returned" to listOf("Refunded", "Cancelled"),
                "Refunded" to listOf("Completed"),
                "Completed" to listOf()
            )),
            Triple("Payment", "Pending", mapOf(
                "Pending" to listOf("Authorized", "Failed"),
                "Authorized" to listOf("Captured", "Voided"),
                "Captured" to listOf("Settled", "Refunded"),
                "Failed" to listOf(),
                "Voided" to listOf(),
                "Settled" to listOf(),
                "Refunded" to listOf()
            )),
            Triple("User", "Guest", mapOf(
                "Guest" to listOf("Registered", "Banned"),
                "Registered" to listOf("Active", "Suspended"),
                "Active" to listOf("Inactive", "Banned"),
                "Inactive" to listOf("Active"),
                "Suspended" to listOf("Active", "Banned"),
                "Banned" to listOf()
            ))
        )
        
        configs.forEach { (name, initialState, transitions) ->
            stateMachines[name] = StateMachine(
                name = name,
                currentState = initialState,
                validTransitions = transitions
            )
        }
    }
    
    suspend fun transitionTo(
        machineName: String,
        newState: String
    ): Boolean {
        val machine = stateMachines[machineName] ?: return false
        
        val validTransitions = machine.validTransitions[machine.currentState] ?: emptyList()
        
        if (newState in validTransitions) {
            delay(Random.nextLong(10, 50))
            
            machine.previousState = machine.currentState
            machine.currentState = newState
            delay(Random.nextLong(10, 30))
            
            return true
        }
        
        return false
    }
    
    suspend fun transitionMultiple(
        machineName: String,
        states: List<String>
    ): Int {
        var successful = 0
        
        states.forEach { state ->
            if (transitionTo(machineName, state)) {
                successful++
            }
        }
        
        return successful
    }
    
    suspend fun revertTransition(machineName: String): Boolean {
        val machine = stateMachines[machineName] ?: return false
        
        val previousState = machine.previousState ?: return false
        
        delay(Random.nextLong(10, 50))
        
        machine.currentState = previousState
        machine.previousState = null
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun getCurrentState(machineName: String): String? {
        val machine = stateMachines[machineName]
        return machine?.currentState
    }
    
    suspend fun getPreviousState(machineName: String): String? {
        val machine = stateMachines[machineName]
        return machine?.previousState
    }
    
    fun getAllMachines() = stateMachines.values.toList()
}

class StateOperator(
    private val manager: StateDependencyManager,
    private val operatorName: String
) {
    suspend fun performTransition(machineName: String, newState: String): Boolean {
        return manager.transitionTo(machineName, newState)
    }
    
    suspend fun performComplexTransition(machineName: String): Boolean {
        val currentState = manager.getCurrentState(machineName) ?: return false
        
        val validTransitions = manager.getAllMachines()
            .find { it.name == machineName }
            ?.validTransitions?.get(currentState) ?: emptyList()
        
        if (validTransitions.isNotEmpty()) {
            val newState = validTransitions.random()
            return performTransition(machineName, newState)
        }
        
        return false
    }
}

suspend fun simulateStateTransitions(
    operator: StateOperator,
    operatorId: Int
) {
    repeat(15) { attempt ->
        val machines = manager.getAllMachines()
        val machine = machines.random()
        
        operator.performComplexTransition(machine.name)
        delay(Random.nextLong(20, 80))
    }
}

suspend fun simulateStateReversions(
    manager: StateDependencyManager
) {
    repeat(10) { attempt ->
        val machines = manager.getAllMachines()
        val machine = machines.random()
        
        manager.revertTransition(machine.name)
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateStateDependencies(
    manager: StateDependencyManager
) {
    repeat(12) { attempt ->
        val machines = manager.getAllMachines()
        val orderMachine = machines.find { it.name == "Order" }
        
        if (orderMachine != null) {
            val currentState = orderMachine.currentState
            
            when (currentState) {
                "Validated" -> {
                    manager.transitionTo("Payment", "Authorized")
                    delay(Random.nextLong(30, 100))
                    manager.transitionTo("Payment", "Captured")
                }
                "Processing" -> {
                    manager.transitionTo("Payment", "Settled")
                }
                else -> {
                    manager.transitionTo(orderMachine.name, "Validated")
                }
            }
        }
        
        delay(Random.nextLong(40, 120))
    }
}

suspend fun simulateConcurrentStateChanges(
    manager: StateDependencyManager
) {
    repeat(15) { attempt ->
        val machines = manager.getAllMachines()
        val machine = machines.random()
        
        val currentState = manager.getCurrentState(machine.name)
        val validTransitions = manager.getAllMachines()
            .find { it.name == machine.name }
            ?.validTransitions?.get(currentState ?: "") ?: emptyList()
        
        if (validTransitions.isNotEmpty()) {
            val newState = validTransitions.random()
            manager.transitionTo(machine.name, newState)
        }
        
        delay(Random.nextLong(25, 75))
    }
}

fun main() = runBlocking {
    val manager = StateDependencyManager()
    
    println("Starting State Dependency Simulation...")
    println("Initial State:")
    manager.getAllMachines().forEach { machine ->
        println("  ${machine.name}: ${machine.currentState}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val operators = listOf(
        StateOperator(manager, "Alice"),
        StateOperator(manager, "Bob"),
        StateOperator(manager, "Charlie"),
        StateOperator(manager, "David"),
        StateOperator(manager, "Eve")
    )
    
    operators.forEachIndexed { index, operator ->
        jobs.add(launch {
            simulateStateTransitions(operator, index)
        })
    }
    
    jobs.add(launch {
        simulateStateReversions(manager)
    })
    
    jobs.add(launch {
        simulateStateDependencies(manager)
    })
    
    jobs.add(launch {
        simulateConcurrentStateChanges(manager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final State ===")
    manager.getAllMachines().forEach { machine ->
        println(
            "  ${machine.name}: ${machine.currentState} " +
            "(Previous: ${machine.previousState})"
        )
    }
    
    val invalidStates = manager.getAllMachines().filter { machine ->
        val currentState = machine.currentState
        val validTransitions = machine.validTransitions[currentState]
        validTransitions == null || validTransitions.isEmpty()
    }
    
    if (invalidStates.isNotEmpty()) {
        println("\n⚠️  Machines in invalid terminal states:")
        invalidStates.forEach { machine ->
            println("  ${machine.name}: ${machine.currentState}")
        }
    } else {
        println("\n✅ All machines in valid states")
    }
    
    val activeMachines = manager.getAllMachines().count { machine ->
        machine.currentState !in listOf("Completed", "Failed", "Banned", "Cancelled")
    }
    
    println("\nActive Machines: $activeMachines/${manager.getAllMachines().size}")
}