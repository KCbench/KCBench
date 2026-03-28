import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StateFlowRaceCondition {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter
    
    suspend fun increment() {
        val current = _counter.value
        delay(10)
        _counter.value = current + 1
    }
    
    suspend fun incrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            increment()
        }
    }
    
    fun getValue() = counter.value
}

class StateFlowDataRace {
    private val _data = MutableStateFlow(0)
    val data: StateFlow<Int> = _data
    
    suspend fun updateData(value: Int) {
        val current = _data.value
        delay(10)
        _data.value = current + value
    }
    
    suspend fun updateMultipleData(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            updateData(value)
        }
    }
    
    fun getValue() = data.value
}

class StateFlowValueRace {
    private val _value = MutableStateFlow(0)
    val value: StateFlow<Int> = _value
    
    suspend fun modifyValue(value: Int) {
        val current = _value.value
        delay(10)
        _value.value = current + value
    }
    
    suspend fun modifyMultipleValues(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            modifyValue(value)
        }
    }
    
    fun getValue() = value.value
}

class StateFlowStateRace {
    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int> = _state
    
    suspend fun changeState(value: Int) {
        val current = _state.value
        delay(10)
        _state.value = current + value
    }
    
    suspend fun changeMultipleStates(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            changeState(value)
        }
    }
    
    fun getValue() = state.value
}

class StateFlowStatusRace {
    private val _status = MutableStateFlow(0)
    val status: StateFlow<Int> = _status
    
    suspend fun updateStatus(value: Int) {
        val current = _status.value
        delay(10)
        _status.value = current + value
    }
    
    suspend fun updateMultipleStatus(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            updateStatus(value)
        }
    }
    
    fun getValue() = status.value
}

suspend fun simulateStateFlowRaceCondition(
    raceCondition: StateFlowRaceCondition,
    raceConditionId: Int
) {
    repeat(100) { attempt ->
        raceCondition.incrementMultiple(10)
        delay(50)
    }
    
    println("StateFlow race condition $raceConditionId completed")
}

suspend fun simulateStateFlowDataRace(
    dataRace: StateFlowDataRace,
    dataRaceId: Int
) {
    repeat(100) { attempt ->
        dataRace.updateMultipleData(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow data race $dataRaceId completed")
}

suspend fun simulateStateFlowValueRace(
    valueRace: StateFlowValueRace,
    valueRaceId: Int
) {
    repeat(100) { attempt ->
        valueRace.modifyMultipleValues(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow value race $valueRaceId completed")
}

suspend fun simulateStateFlowStateRace(
    stateRace: StateFlowStateRace,
    stateRaceId: Int
) {
    repeat(100) { attempt ->
        stateRace.changeMultipleStates(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow state race $stateRaceId completed")
}

suspend fun simulateStateFlowStatusRace(
    statusRace: StateFlowStatusRace,
    statusRaceId: Int
) {
    repeat(100) { attempt ->
        statusRace.updateMultipleStatus(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow status race $statusRaceId completed")
}

suspend fun monitorStateFlowRaces(
    raceCondition: StateFlowRaceCondition,
    dataRace: StateFlowDataRace,
    valueRace: StateFlowValueRace,
    stateRace: StateFlowStateRace,
    statusRace: StateFlowStatusRace,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Race condition counter: ${raceCondition.getValue()}")
        println("  Data race value: ${dataRace.getValue()}")
        println("  Value race value: ${valueRace.getValue()}")
        println("  State race value: ${stateRace.getValue()}")
        println("  Status race value: ${statusRace.getValue()}")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting StateFlow Race Condition Simulation...")
    println()
    
    val raceCondition = StateFlowRaceCondition()
    val dataRace = StateFlowDataRace()
    val valueRace = StateFlowValueRace()
    val stateRace = StateFlowStateRace()
    val statusRace = StateFlowStatusRace()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateStateFlowRaceCondition(raceCondition, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowRaceCondition(raceCondition, 2)
    })
    
    jobs.add(launch {
        simulateStateFlowDataRace(dataRace, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowValueRace(valueRace, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowStateRace(stateRace, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowStatusRace(statusRace, 1)
    })
    
    jobs.add(launch {
        monitorStateFlowRaces(
            raceCondition,
            dataRace,
            valueRace,
            stateRace,
            statusRace,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  StateFlow Race Condition Warning:")
    println("  The code has race conditions in StateFlow updates:")
    println("  - StateFlowRaceCondition.increment() reads and writes with delay")
    println("  - StateFlowDataRace.updateData() reads and writes with delay")
    println("  - StateFlowValueRace.modifyValue() reads and writes with delay")
    println("  - StateFlowStateRace.changeState() reads and writes with delay")
    println("  - StateFlowStatusRace.updateStatus() reads and writes with delay")
    println("  Race conditions can cause lost updates and incorrect state.")
    println("  Fix: Use update {} function for atomic updates.")
}