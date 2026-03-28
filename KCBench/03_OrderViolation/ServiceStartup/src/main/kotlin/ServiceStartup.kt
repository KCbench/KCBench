import kotlinx.coroutines.*
import kotlin.random.Random

data class ServiceComponent(
    val componentName: String,
    var initialized: Boolean = false,
    var started: Boolean = false,
    var ready: Boolean = false,
    val dependencies: List<String>
)

class ServiceStartupManager {
    private val components = mutableMapOf<String, ServiceComponent>()
    
    init {
        initializeComponents()
    }
    
    private fun initializeComponents() {
        val componentConfigs = listOf(
            Triple("ConfigService", listOf(), 80),
            Triple("DatabaseService", listOf("ConfigService"), 120),
            Triple("CacheService", listOf("DatabaseService"), 100),
            Triple("AuthService", listOf("DatabaseService"), 90),
            Triple("APIService", listOf("DatabaseService", "CacheService", "AuthService"), 150),
            Triple("LoggerService", listOf("ConfigService"), 70),
            Triple("MetricsService", listOf("LoggerService"), 60),
            Triple("WebSocketService", listOf("APIService"), 110),
            Triple("SchedulerService", listOf("ConfigService"), 85),
            Triple("NotificationService", listOf("APIService", "AuthService"), 130)
        )
        
        componentConfigs.forEach { (componentName, deps, initTime) ->
            components[componentName] = ServiceComponent(
                componentName = componentName,
                initialized = false,
                started = false,
                ready = false,
                dependencies = deps
            )
        }
    }
    
    suspend fun initializeComponent(componentName: String): Boolean {
        val component = components[componentName] ?: return false
        
        if (component.initialized) {
            return true
        }
        
        val dependenciesInitialized = component.dependencies.all { depName ->
            val depComponent = components[depName]
            depComponent != null && depComponent.initialized
        }
        
        if (!dependenciesInitialized) {
            return false
        }
        
        delay(Random.nextLong(20, 80))
        
        component.initialized = true
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun startComponent(componentName: String): Boolean {
        val component = components[componentName] ?: return false
        
        if (!component.initialized) {
            return false
        }
        
        if (component.started) {
            return true
        }
        
        delay(Random.nextLong(30, 100))
        
        component.started = true
        delay(Random.nextLong(10, 20))
        
        return true
    }
    
    suspend fun markComponentReady(componentName: String): Boolean {
        val component = components[componentName] ?: return false
        
        if (!component.started) {
            return false
        }
        
        if (component.ready) {
            return true
        }
        
        delay(Random.nextLong(15, 50))
        
        component.ready = true
        delay(Random.nextLong(10, 20))
        
        return true
    }
    
    suspend fun startAllComponents(): Int {
        var started = 0
        
        components.keys.forEach { componentName ->
            if (initializeComponent(componentName) &&
                startComponent(componentName) &&
                markComponentReady(componentName)) {
                started++
            }
        }
        
        return started
    }
    
    suspend fun resetComponent(componentName: String): Boolean {
        val component = components[componentName] ?: return false
        
        component.initialized = false
        component.started = false
        component.ready = false
        
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun getComponentStatus(componentName: String): ServiceComponent? {
        return components[componentName]
    }
    
    fun getAllComponents() = components.values.toList()
}

class ServiceStarter(
    private val startupManager: ServiceStartupManager,
    private val starterName: String
) {
    suspend fun startRandomComponent(): Boolean {
        val components = startupManager.getAllComponents()
        val uninitializedComponents = components.filter { !it.initialized }
        
        if (uninitializedComponents.isEmpty()) {
            return false
        }
        
        val component = uninitializedComponents.random()
        
        return startupManager.initializeComponent(component.componentName) &&
               startupManager.startComponent(component.componentName) &&
               startupManager.markComponentReady(component.componentName)
    }
    
    suspend fun startMultipleComponents(count: Int): Int {
        var started = 0
        
        repeat(count) {
            if (startRandomComponent()) {
                started++
            }
        }
        
        return started
    }
}

suspend fun simulateServiceStartup(
    starter: ServiceStarter,
    starterId: Int
) {
    repeat(12) { attempt ->
        starter.startMultipleComponents(2)
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateServiceRestart(
    startupManager: ServiceStartupManager
) {
    repeat(8) { attempt ->
        val components = startupManager.getAllComponents()
        val component = components.random()
        
        startupManager.resetComponent(component.componentName)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateDependencyStartup(
    startupManager: ServiceStartupManager
) {
    repeat(15) { attempt ->
        val components = startupManager.getAllComponents()
        val uninitializedComponents = components.filter { !it.initialized }
        
        if (uninitializedComponents.isNotEmpty()) {
            val component = uninitializedComponents.random()
            
            if (component.dependencies.isEmpty()) {
                startupManager.initializeComponent(component.componentName)
            }
        }
        
        delay(Random.nextLong(40, 120))
    }
}

suspend fun simulateServiceMonitoring(
    startupManager: ServiceStartupManager
) {
    repeat(20) { attempt ->
        val components = startupManager.getAllComponents()
        
        println("Service Status:")
        println("  Initialized: ${components.count { it.initialized }}")
        println("  Started: ${components.count { it.started }}")
        println("  Ready: ${components.count { it.ready }}")
        
        val invalidInitialized = components.filter { component ->
            component.initialized && !component.dependencies.all { depName ->
                val depComponent = startupManager.getComponentStatus(depName)
                depComponent != null && depComponent.initialized
            }
        }
        
        if (invalidInitialized.isNotEmpty()) {
            println("  Invalid initialized components: ${invalidInitialized.size}")
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun simulateFullServiceStartup(
    startupManager: ServiceStartupManager
) {
    repeat(6) { attempt ->
        startupManager.startAllComponents()
        delay(Random.nextLong(200, 500))
    }
}

fun main() = runBlocking {
    val startupManager = ServiceStartupManager()
    
    println("Starting Service Startup Simulation...")
    println("Initial Component Status:")
    startupManager.getAllComponents().forEach { component ->
        println(
            "  ${component.componentName}: Initialized=${component.initialized}, " +
            "Deps=${component.dependencies.joinToString()}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val starters = listOf(
        ServiceStarter(startupManager, "Alice"),
        ServiceStarter(startupManager, "Bob"),
        ServiceStarter(startupManager, "Charlie"),
        ServiceStarter(startupManager, "David")
    )
    
    starters.forEachIndexed { index, starter ->
        jobs.add(launch {
            simulateServiceStartup(starter, index)
        })
    }
    
    jobs.add(launch {
        simulateServiceRestart(startupManager)
    })
    
    jobs.add(launch {
        simulateDependencyStartup(startupManager)
    })
    
    jobs.add(launch {
        simulateServiceMonitoring(startupManager)
    })
    
    jobs.add(launch {
        simulateFullServiceStartup(startupManager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val components = startupManager.getAllComponents()
    val initializedComponents = components.filter { it.initialized }
    val startedComponents = components.filter { it.started }
    val readyComponents = components.filter { it.ready }
    
    println("\n=== Final Component Status ===")
    components.forEach { component ->
        println(
            "  ${component.componentName}: " +
            "Initialized=${component.initialized}, " +
            "Started=${component.started}, " +
            "Ready=${component.ready}"
        )
    }
    
    val invalidInitialized = initializedComponents.filter { component ->
        !component.dependencies.all { depName ->
            val depComponent = startupManager.getComponentStatus(depName)
            depComponent != null && depComponent.initialized
        }
    }
    
    if (invalidInitialized.isNotEmpty()) {
        println("\n⚠️  Components initialized before dependencies:")
        invalidInitialized.forEach { component ->
            println("  ${component.componentName}: ${component.dependencies}")
        }
    } else {
        println("\n✅ All components initialized in correct order")
    }
    
    val totalComponents = components.size
    println("\nInitialized: ${initializedComponents.size}/$totalComponents")
    println("Started: ${startedComponents.size}/$totalComponents")
    println("Ready: ${readyComponents.size}/$totalComponents")
}