import kotlinx.coroutines.*
import kotlin.random.Random

data class Service(
    val name: String,
    var initialized: Boolean = false,
    var ready: Boolean = false,
    var dependencies: List<String> = emptyList()
)

class InitializationSequenceManager {
    private val services = mutableMapOf<String, Service>()
    
    init {
        initializeServices()
    }
    
    private fun initializeServices() {
        val serviceConfigs = listOf(
            Triple("Database", listOf("Config"), 100),
            Triple("Cache", listOf("Database"), 150),
            Triple("API", listOf("Database", "Cache"), 200),
            Triple("Auth", listOf("Database"), 120),
            Triple("Logger", listOf(), 50),
            Triple("Metrics", listOf("Logger"), 80),
            Triple("WebSocket", listOf("API"), 180),
            Triple("Scheduler", listOf("Config"), 90),
            Triple("FileStorage", listOf("Database"), 160),
            Triple("Notification", listOf("API", "Auth"), 220)
        )
        
        serviceConfigs.forEach { (name, deps, initTime) ->
            services[name] = Service(
                name = name,
                initialized = false,
                ready = false,
                dependencies = deps
            )
        }
    }
    
    suspend fun initializeService(serviceName: String): Boolean {
        val service = services[serviceName] ?: return false
        
        if (service.initialized) {
            return true
        }
        
        delay(Random.nextLong(10, 50))
        
        service.initialized = true
        delay(Random.nextLong(10, 30))
        
        service.ready = true
        return true
    }
    
    suspend fun initializeAllServices() {
        coroutineScope {
            val serviceNames = services.keys.toList()
            
            serviceNames.forEach { serviceName ->
                launch {
                    initializeService(serviceName)
                }
            }
        }
    }
    
    suspend fun waitForService(serviceName: String): Boolean {
        val service = services[serviceName] ?: return false
        
        var attempts = 0
        while (!service.ready && attempts < 50) {
            delay(20)
            attempts++
        }
        
        return service.ready
    }
    
    suspend fun checkDependencies(serviceName: String): Boolean {
        val service = services[serviceName] ?: return false
        
        service.dependencies.forEach { depName ->
            val depService = services[depName]
            
            if (depService == null || !depService.ready) {
                return false
            }
        }
        
        return true
    }
    
    suspend fun useService(serviceName: String): Boolean {
        if (!checkDependencies(serviceName)) {
            return false
        }
        
        val service = services[serviceName]
        
        if (service != null && service.ready) {
            delay(Random.nextLong(5, 20))
            return true
        }
        
        return false
    }
    
    fun getServiceStatus(serviceName: String): Service? {
        return services[serviceName]
    }
    
    fun getAllServices() = services.values.toList()
}

class ServiceClient(
    val manager: InitializationSequenceManager,
    private val clientName: String
) {
    suspend fun useServices(serviceNames: List<String>): Int {
        var successful = 0
        
        serviceNames.forEach { serviceName ->
            if (manager.useService(serviceName)) {
                successful++
            }
        }
        
        return successful
    }
}

suspend fun simulateClientUsage(
    client: ServiceClient,
    clientId: Int
) {
    repeat(15) { attempt ->
        val services = client.manager.getAllServices()
        val selectedServices = services.shuffled().take(3)
        val serviceNames = selectedServices.map { it.name }
        
        val successful = client.useServices(serviceNames)
        
        if (successful < serviceNames.size) {
            println("Client $clientId: Only $successful/${serviceNames.size} services available")
        }
        
        delay(Random.nextLong(20, 80))
    }
}

suspend fun simulateServiceInitialization(
    manager: InitializationSequenceManager
) {
    repeat(10) { attempt ->
        val services = manager.getAllServices()
        val service = services.random()
        
        if (!service.initialized) {
            manager.initializeService(service.name)
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateDependencyChecks(
    manager: InitializationSequenceManager
) {
    repeat(20) { attempt ->
        val services = manager.getAllServices()
        val service = services.random()
        
        val depsReady = manager.checkDependencies(service.name)
        
        if (!depsReady && service.ready) {
            println("Service ${service.name} ready but dependencies not satisfied")
        }
        
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateServiceReinitialization(
    manager: InitializationSequenceManager
) {
    repeat(8) { attempt ->
        val services = manager.getAllServices()
        val service = services.random()
        
        service.initialized = false
        service.ready = false
        
        delay(Random.nextLong(100, 300))
        
        manager.initializeService(service.name)
    }
}

fun main() = runBlocking {
    val manager = InitializationSequenceManager()
    
    println("Starting Initialization Sequence Simulation...")
    println("Initial Service Status:")
    manager.getAllServices().forEach { service ->
        println(
            "  ${service.name}: Initialized=${service.initialized}, " +
            "Ready=${service.ready}, " +
            "Deps=${service.dependencies.joinToString()}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        manager.initializeAllServices()
    })
    
    delay(500)
    
    val clients = listOf(
        ServiceClient(manager, "Alice"),
        ServiceClient(manager, "Bob"),
        ServiceClient(manager, "Charlie"),
        ServiceClient(manager, "David"),
        ServiceClient(manager, "Eve")
    )
    
    clients.forEachIndexed { index, client ->
        jobs.add(launch {
            simulateClientUsage(client, index)
        })
    }
    
    jobs.add(launch {
        simulateServiceInitialization(manager)
    })
    
    jobs.add(launch {
        simulateDependencyChecks(manager)
    })
    
    jobs.add(launch {
        simulateServiceReinitialization(manager)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Service Status ===")
    manager.getAllServices().forEach { service ->
        println(
            "  ${service.name}: Initialized=${service.initialized}, " +
            "Ready=${service.ready}, " +
            "Deps=${service.dependencies.joinToString()}"
        )
    }
    
    val readyButDepsNotReady = manager.getAllServices()
        .filter { service ->
            service.ready && !manager.checkDependencies(service.name)
        }
    
    if (readyButDepsNotReady.isNotEmpty()) {
        println("\n⚠️  Services ready but dependencies not satisfied:")
        readyButDepsNotReady.forEach { service ->
            println("  ${service.name}: ${service.dependencies}")
        }
    } else {
        println("\n✅ All services properly initialized")
    }
    
    val totalReady = manager.getAllServices().count { it.ready }
    val totalServices = manager.getAllServices().size
    
    println("\nReady Services: $totalReady/$totalServices")
}