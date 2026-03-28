import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StateFlowUpdateConflict {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter
    
    suspend fun increment() {
        _counter.value = _counter.value + 1
    }
    
    suspend fun decrement() {
        _counter.value = _counter.value - 1
    }
    
    suspend fun incrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            increment()
            delay(10)
        }
    }
    
    suspend fun decrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            decrement()
            delay(10)
        }
    }
    
    fun getValue() = counter.value
}

class StateFlowValueConflict {
    private val _value = MutableStateFlow(0)
    val value: StateFlow<Int> = _value
    
    suspend fun add(amount: Int) {
        _value.value = _value.value + amount
    }
    
    suspend fun subtract(amount: Int) {
        _value.value = _value.value - amount
    }
    
    suspend fun addMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            add(amount)
            delay(10)
        }
    }
    
    suspend fun subtractMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            subtract(amount)
            delay(10)
        }
    }
    
    fun getValue() = value.value
}

class StateFlowStateConflict {
    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int> = _state
    
    suspend fun increase(amount: Int) {
        _state.value = _state.value + amount
    }
    
    suspend fun decrease(amount: Int) {
        _state.value = _state.value - amount
    }
    
    suspend fun increaseMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            increase(amount)
            delay(10)
        }
    }
    
    suspend fun decreaseMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            decrease(amount)
            delay(10)
        }
    }
    
    fun getValue() = state.value
}

class StateFlowStatusConflict {
    private val _status = MutableStateFlow(0)
    val status: StateFlow<Int> = _status
    
    suspend fun upgrade(amount: Int) {
        _status.value = _status.value + amount
    }
    
    suspend fun downgrade(amount: Int) {
        _status.value = _status.value - amount
    }
    
    suspend fun upgradeMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            upgrade(amount)
            delay(10)
        }
    }
    
    suspend fun downgradeMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            downgrade(amount)
            delay(10)
        }
    }
    
    fun getValue() = status.value
}

class StateFlowLevelConflict {
    private val _level = MutableStateFlow(0)
    val level: StateFlow<Int> = _level
    
    suspend fun raise(amount: Int) {
        _level.value = _level.value + amount
    }
    
    suspend fun lower(amount: Int) {
        _level.value = _level.value - amount
    }
    
    suspend fun raiseMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            raise(amount)
            delay(10)
        }
    }
    
    suspend fun lowerMultiple(amounts: List<Int>) = coroutineScope {
        amounts.forEach { amount ->
            lower(amount)
            delay(10)
        }
    }
    
    fun getValue() = level.value
}

suspend fun simulateStateFlowUpdateConflict(
    updateConflict: StateFlowUpdateConflict,
    updateConflictId: Int
) {
    repeat(100) { attempt ->
        updateConflict.incrementMultiple(5)
        updateConflict.decrementMultiple(5)
        delay(50)
    }
    
    println("StateFlow update conflict $updateConflictId completed")
}

suspend fun simulateStateFlowValueConflict(
    valueConflict: StateFlowValueConflict,
    valueConflictId: Int
) {
    repeat(100) { attempt ->
        valueConflict.addMultiple(listOf(1, 2, 3, 4, 5))
        valueConflict.subtractMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow value conflict $valueConflictId completed")
}

suspend fun simulateStateFlowStateConflict(
    stateConflict: StateFlowStateConflict,
    stateConflictId: Int
) {
    repeat(100) { attempt ->
        stateConflict.increaseMultiple(listOf(1, 2, 3, 4, 5))
        stateConflict.decreaseMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow state conflict $stateConflictId completed")
}

suspend fun simulateStateFlowStatusConflict(
    statusConflict: StateFlowStatusConflict,
    statusConflictId: Int
) {
    repeat(100) { attempt ->
        statusConflict.upgradeMultiple(listOf(1, 2, 3, 4, 5))
        statusConflict.downgradeMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow status conflict $statusConflictId completed")
}

suspend fun simulateStateFlowLevelConflict(
    levelConflict: StateFlowLevelConflict,
    levelConflictId: Int
) {
    repeat(100) { attempt ->
        levelConflict.raiseMultiple(listOf(1, 2, 3, 4, 5))
        levelConflict.lowerMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow level conflict $levelConflictId completed")
}

suspend fun monitorStateFlowUpdateConflicts(
    updateConflict: StateFlowUpdateConflict,
    valueConflict: StateFlowValueConflict,
    stateConflict: StateFlowStateConflict,
    statusConflict: StateFlowStatusConflict,
    levelConflict: StateFlowLevelConflict,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Update conflict counter: ${updateConflict.getValue()}")
        println("  Value conflict value: ${valueConflict.getValue()}")
        println("  State conflict value: ${stateConflict.getValue()}")
        println("  Status conflict value: ${statusConflict.getValue()}")
        println("  Level conflict value: ${levelConflict.getValue()}")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting StateFlow Update Conflict Simulation...")
    println()
    
    val updateConflict = StateFlowUpdateConflict()
    val valueConflict = StateFlowValueConflict()
    val stateConflict = StateFlowStateConflict()
    val statusConflict = StateFlowStatusConflict()
    val levelConflict = StateFlowLevelConflict()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateStateFlowUpdateConflict(updateConflict, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowUpdateConflict(updateConflict, 2)
    })
    
    jobs.add(launch {
        simulateStateFlowValueConflict(valueConflict, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowStateConflict(stateConflict, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowStatusConflict(statusConflict, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowLevelConflict(levelConflict, 1)
    })
    
    jobs.add(launch {
        monitorStateFlowUpdateConflicts(
            updateConflict,
            valueConflict,
            stateConflict,
            statusConflict,
            levelConflict,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  StateFlow Update Conflict Warning:")
    println("  The code has update conflicts due to concurrent StateFlow updates:")
    println("  - StateFlowUpdateConflict has increment and decrement conflicts")
    println("  - StateFlowValueConflict has add and subtract conflicts")
    println("  - StateFlowStateConflict has increase and decrease conflicts")
    println("  - StateFlowStatusConflict has upgrade and downgrade conflicts")
    println("  - StateFlowLevelConflict has raise and lower conflicts")
    println("  Concurrent updates can cause conflicts and incorrect state.")
    println("  Fix: Use update {} function for atomic updates.")
}