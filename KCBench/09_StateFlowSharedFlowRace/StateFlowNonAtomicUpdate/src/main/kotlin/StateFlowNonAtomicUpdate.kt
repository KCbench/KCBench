import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StateFlowCounter {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter
    
    suspend fun increment() {
        _counter.value = _counter.value + 1
    }
    
    suspend fun incrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            increment()
            delay(10)
        }
    }
    
    fun getValue() = counter.value
}

class StateFlowAccumulator {
    private val _accumulator = MutableStateFlow(0)
    val accumulator: StateFlow<Int> = _accumulator
    
    suspend fun add(value: Int) {
        _accumulator.value = _accumulator.value + value
    }
    
    suspend fun addMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            add(value)
            delay(10)
        }
    }
    
    fun getValue() = accumulator.value
}

class StateFlowMultiplier {
    private val _multiplier = MutableStateFlow(1)
    val multiplier: StateFlow<Int> = _multiplier
    
    suspend fun multiply(value: Int) {
        _multiplier.value = _multiplier.value * value
    }
    
    suspend fun multiplyMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            multiply(value)
            delay(10)
        }
    }
    
    fun getValue() = multiplier.value
}

class StateFlowUpdater {
    private val _updater = MutableStateFlow(0)
    val updater: StateFlow<Int> = _updater
    
    suspend fun update(value: Int) {
        _updater.value = _updater.value + value
    }
    
    suspend fun updateMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            update(value)
            delay(10)
        }
    }
    
    fun getValue() = updater.value
}

class StateFlowModifier {
    private val _modifier = MutableStateFlow(0)
    val modifier: StateFlow<Int> = _modifier
    
    suspend fun modify(value: Int) {
        _modifier.value = _modifier.value + value
    }
    
    suspend fun modifyMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            modify(value)
            delay(10)
        }
    }
    
    fun getValue() = modifier.value
}

suspend fun simulateStateFlowCounter(
    counter: StateFlowCounter,
    counterId: Int
) {
    repeat(100) { attempt ->
        counter.incrementMultiple(10)
        delay(50)
    }
    
    println("StateFlow counter $counterId completed")
}

suspend fun simulateStateFlowAccumulator(
    accumulator: StateFlowAccumulator,
    accumulatorId: Int
) {
    repeat(100) { attempt ->
        accumulator.addMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow accumulator $accumulatorId completed")
}

suspend fun simulateStateFlowMultiplier(
    multiplier: StateFlowMultiplier,
    multiplierId: Int
) {
    repeat(100) { attempt ->
        multiplier.multiplyMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow multiplier $multiplierId completed")
}

suspend fun simulateStateFlowUpdater(
    updater: StateFlowUpdater,
    updaterId: Int
) {
    repeat(100) { attempt ->
        updater.updateMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow updater $updaterId completed")
}

suspend fun simulateStateFlowModifier(
    modifier: StateFlowModifier,
    modifierId: Int
) {
    repeat(100) { attempt ->
        modifier.modifyMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow modifier $modifierId completed")
}

suspend fun monitorStateFlows(
    counter: StateFlowCounter,
    accumulator: StateFlowAccumulator,
    multiplier: StateFlowMultiplier,
    updater: StateFlowUpdater,
    modifier: StateFlowModifier,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Counter: ${counter.getValue()}")
        println("  Accumulator: ${accumulator.getValue()}")
        println("  Multiplier: ${multiplier.getValue()}")
        println("  Updater: ${updater.getValue()}")
        println("  Modifier: ${modifier.getValue()}")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting StateFlow Non-Atomic Update Simulation...")
    println()
    
    val counter = StateFlowCounter()
    val accumulator = StateFlowAccumulator()
    val multiplier = StateFlowMultiplier()
    val updater = StateFlowUpdater()
    val modifier = StateFlowModifier()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateStateFlowCounter(counter, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowCounter(counter, 2)
    })
    
    jobs.add(launch {
        simulateStateFlowAccumulator(accumulator, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowMultiplier(multiplier, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowUpdater(updater, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowModifier(modifier, 1)
    })
    
    jobs.add(launch {
        monitorStateFlows(
            counter,
            accumulator,
            multiplier,
            updater,
            modifier,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  StateFlow Non-Atomic Update Warning:")
    println("  The code uses non-atomic updates to StateFlow:")
    println("  - StateFlowCounter.increment() uses _counter.value = _counter.value + 1")
    println("  - StateFlowAccumulator.add() uses _accumulator.value = _accumulator.value + value")
    println("  - StateFlowMultiplier.multiply() uses _multiplier.value = _multiplier.value * value")
    println("  - StateFlowUpdater.update() uses _updater.value = _updater.value + value")
    println("  - StateFlowModifier.modify() uses _modifier.value = _modifier.value + value")
    println("  Non-atomic read-modify-write operations can cause race conditions,")
    println("  leading to lost updates and incorrect state.")
    println("  Fix: Use update {} function for atomic updates.")
}