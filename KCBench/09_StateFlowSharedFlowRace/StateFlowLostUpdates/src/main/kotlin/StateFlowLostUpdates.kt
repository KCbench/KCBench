import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StateFlowLostUpdates {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter
    
    suspend fun increment() {
        _counter.value = _counter.value + 1
    }
    
    suspend fun incrementMultiple(times: Int) = coroutineScope {
        repeat(times) {
            increment()
        }
    }
    
    fun getValue() = counter.value
}

class StateFlowConcurrentUpdates {
    private val _concurrent = MutableStateFlow(0)
    val concurrent: StateFlow<Int> = _concurrent
    
    suspend fun update(value: Int) {
        _concurrent.value = _concurrent.value + value
    }
    
    suspend fun updateMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            update(value)
        }
    }
    
    fun getValue() = concurrent.value
}

class StateFlowParallelUpdates {
    private val _parallel = MutableStateFlow(0)
    val parallel: StateFlow<Int> = _parallel
    
    suspend fun modify(value: Int) {
        _parallel.value = _parallel.value + value
    }
    
    suspend fun modifyMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            modify(value)
        }
    }
    
    fun getValue() = parallel.value
}

class StateFlowSimultaneousUpdates {
    private val _simultaneous = MutableStateFlow(0)
    val simultaneous: StateFlow<Int> = _simultaneous
    
    suspend fun change(value: Int) {
        _simultaneous.value = _simultaneous.value + value
    }
    
    suspend fun changeMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            change(value)
        }
    }
    
    fun getValue() = simultaneous.value
}

class StateFlowSynchronousUpdates {
    private val _synchronous = MutableStateFlow(0)
    val synchronous: StateFlow<Int> = _synchronous
    
    suspend fun alter(value: Int) {
        _synchronous.value = _synchronous.value + value
    }
    
    suspend fun alterMultiple(values: List<Int>) = coroutineScope {
        values.forEach { value ->
            alter(value)
        }
    }
    
    fun getValue() = synchronous.value
}

suspend fun simulateStateFlowLostUpdates(
    lostUpdates: StateFlowLostUpdates,
    lostUpdatesId: Int
) {
    repeat(100) { attempt ->
        lostUpdates.incrementMultiple(10)
        delay(50)
    }
    
    println("StateFlow lost updates $lostUpdatesId completed")
}

suspend fun simulateStateFlowConcurrentUpdates(
    concurrentUpdates: StateFlowConcurrentUpdates,
    concurrentUpdatesId: Int
) {
    repeat(100) { attempt ->
        concurrentUpdates.updateMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow concurrent updates $concurrentUpdatesId completed")
}

suspend fun simulateStateFlowParallelUpdates(
    parallelUpdates: StateFlowParallelUpdates,
    parallelUpdatesId: Int
) {
    repeat(100) { attempt ->
        parallelUpdates.modifyMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow parallel updates $parallelUpdatesId completed")
}

suspend fun simulateStateFlowSimultaneousUpdates(
    simultaneousUpdates: StateFlowSimultaneousUpdates,
    simultaneousUpdatesId: Int
) {
    repeat(100) { attempt ->
        simultaneousUpdates.changeMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow simultaneous updates $simultaneousUpdatesId completed")
}

suspend fun simulateStateFlowSynchronousUpdates(
    synchronousUpdates: StateFlowSynchronousUpdates,
    synchronousUpdatesId: Int
) {
    repeat(100) { attempt ->
        synchronousUpdates.alterMultiple(listOf(1, 2, 3, 4, 5))
        delay(50)
    }
    
    println("StateFlow synchronous updates $synchronousUpdatesId completed")
}

suspend fun monitorStateFlowLostUpdates(
    lostUpdates: StateFlowLostUpdates,
    concurrentUpdates: StateFlowConcurrentUpdates,
    parallelUpdates: StateFlowParallelUpdates,
    simultaneousUpdates: StateFlowSimultaneousUpdates,
    synchronousUpdates: StateFlowSynchronousUpdates,
    monitorId: Int
) {
    repeat(200) { attempt ->
        println("Monitor $monitorId:")
        println("  Lost updates counter: ${lostUpdates.getValue()}")
        println("  Concurrent updates value: ${concurrentUpdates.getValue()}")
        println("  Parallel updates value: ${parallelUpdates.getValue()}")
        println("  Simultaneous updates value: ${simultaneousUpdates.getValue()}")
        println("  Synchronous updates value: ${synchronousUpdates.getValue()}")
        
        delay(100)
    }
}

fun main() = runBlocking {
    println("Starting StateFlow Lost Updates Simulation...")
    println()
    
    val lostUpdates = StateFlowLostUpdates()
    val concurrentUpdates = StateFlowConcurrentUpdates()
    val parallelUpdates = StateFlowParallelUpdates()
    val simultaneousUpdates = StateFlowSimultaneousUpdates()
    val synchronousUpdates = StateFlowSynchronousUpdates()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateStateFlowLostUpdates(lostUpdates, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowLostUpdates(lostUpdates, 2)
    })
    
    jobs.add(launch {
        simulateStateFlowConcurrentUpdates(concurrentUpdates, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowParallelUpdates(parallelUpdates, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowSimultaneousUpdates(simultaneousUpdates, 1)
    })
    
    jobs.add(launch {
        simulateStateFlowSynchronousUpdates(synchronousUpdates, 1)
    })
    
    jobs.add(launch {
        monitorStateFlowLostUpdates(
            lostUpdates,
            concurrentUpdates,
            parallelUpdates,
            simultaneousUpdates,
            synchronousUpdates,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n⚠️  StateFlow Lost Updates Warning:")
    println("  The code has lost updates due to concurrent StateFlow updates:")
    println("  - StateFlowLostUpdates.increment() uses non-atomic updates")
    println("  - StateFlowConcurrentUpdates.update() uses non-atomic updates")
    println("  - StateFlowParallelUpdates.modify() uses non-atomic updates")
    println("  - StateFlowSimultaneousUpdates.change() uses non-atomic updates")
    println("  - StateFlowSynchronousUpdates.alter() uses non-atomic updates")
    println("  Concurrent non-atomic updates can cause lost updates and incorrect state.")
    println("  Fix: Use update {} function for atomic updates.")
}