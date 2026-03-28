import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class Service(
    val serviceId: String,
    val serviceName: String,
    var running: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class ServiceDependency(
    val dependencyId: String,
    val dependencyName: String,
    var active: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

class ServiceManager {
    private val services = mutableMapOf<String, Service>()
    private val dependencies = mutableMapOf<String, ServiceDependency>()
    private val servicePoolMutex = Mutex()
    private val dependencyPoolMutex = Mutex()
    
    init {
        initializeServices()
        initializeDependencies()
    }
    
    private fun initializeServices() {
        val serviceConfigs = listOf(
            Pair("SVC001", "AuthService"),
            Pair("SVC002", "DatabaseService"),
            Pair("SVC003", "CacheService"),
            Pair("SVC004", "APIService"),
            Pair("SVC005", "LoggerService"),
            Pair("SVC006", "MetricsService"),
            Pair("SVC007", "NotificationService"),
            Pair("SVC008", "SchedulerService"),
            Pair("SVC009", "WebSocketService"),
            Pair("SVC010", "FileService")
        )
        
        serviceConfigs.forEach { (serviceId, serviceName) ->
            services[serviceId] = Service(
                serviceId = serviceId,
                serviceName = serviceName,
                running = false,
                locked = false
            )
        }
    }
    
    private fun initializeDependencies() {
        val dependencyConfigs = listOf(
            Pair("DEP001", "DatabaseConnection"),
            Pair("DEP002", "RedisConnection"),
            Pair("DEP003", "MessageQueue"),
            Pair("DEP004", "FileSystem"),
            Pair("DEP005", "NetworkInterface"),
            Pair("DEP006", "ThreadPool"),
            Pair("DEP007", "EventBus"),
            Pair("DEP008", "ConfigLoader"),
            Pair("DEP009", "SecurityManager"),
            Pair("DEP010", "HealthChecker")
        )
        
        dependencyConfigs.forEach { (dependencyId, dependencyName) ->
            dependencies[dependencyId] = ServiceDependency(
                dependencyId = dependencyId,
                dependencyName = dependencyName,
                active = false,
                locked = false
            )
        }
    }
    
    suspend fun startService(serviceId: String): Boolean {
        val service = services[serviceId] ?: return false
        
        if (service.running) {
            return true
        }
        
        servicePoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (service.running) {
                return true
            }
            
            service.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                service.running = true
                service.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun stopService(serviceId: String): Boolean {
        val service = services[serviceId] ?: return false
        
        if (!service.running) {
            return true
        }
        
        service.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            servicePoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                service.running = false
                service.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun activateDependency(dependencyId: String): Boolean {
        val dependency = dependencies[dependencyId] ?: return false
        
        if (dependency.active) {
            return true
        }
        
        dependencyPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (dependency.active) {
                return true
            }
            
            dependency.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                dependency.active = true
                dependency.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun deactivateDependency(dependencyId: String): Boolean {
        val dependency = dependencies[dependencyId] ?: return false
        
        if (!dependency.active) {
            return true
        }
        
        dependency.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            dependencyPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                dependency.active = false
                dependency.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun startServiceWithDependency(
        serviceId: String,
        dependencyId: String
    ): Boolean {
        val service = services[serviceId] ?: return false
        val dependency = dependencies[dependencyId] ?: return false
        
        if (service.running || !dependency.active) {
            return false
        }
        
        service.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            dependency.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                service.running = true
                service.locked = true
                
                delay(Random.nextLong(20, 50))
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferService(
        fromServiceId: String,
        toServiceId: String
    ): Boolean {
        val fromService = services[fromServiceId]
        val toService = services[toServiceId]
        
        if (fromService == null || toService == null) {
            return false
        }
        
        if (!fromService.running || toService.running) {
            return false
        }
        
        fromService.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toService.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromService.running = false
                fromService.locked = false
                toService.running = true
                toService.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapServices(
        serviceId1: String,
        serviceId2: String
    ): Boolean {
        val service1 = services[serviceId1]
        val service2 = services[serviceId2]
        
        if (service1 == null || service2 == null) {
            return false
        }
        
        if (!service1.running || !service2.running) {
            return false
        }
        
        service1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            service2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempRunning = service1.running
                val tempLocked = service1.locked
                
                service1.running = service2.running
                service1.locked = service2.locked
                service2.running = tempRunning
                service2.locked = tempLocked
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getServiceStatus(serviceId: String): Service? {
        val service = services[serviceId] ?: return null
        
        return service.mutex.withLock {
            delay(Random.nextLong(5, 15))
            service.copy()
        }
    }
    
    suspend fun getDependencyStatus(dependencyId: String): ServiceDependency? {
        val dependency = dependencies[dependencyId] ?: return null
        
        return dependency.mutex.withLock {
            delay(Random.nextLong(5, 15))
            dependency.copy()
        }
    }
    
    fun getAllServices() = services.values.toList()
    fun getAllDependencies() = dependencies.values.toList()
}

suspend fun simulateServiceStarting(
    serviceManager: ServiceManager,
    managerId: Int
) {
    val services = serviceManager.getAllServices()
    
    repeat(10) { attempt ->
        val service = services.filter { !it.running }.randomOrNull()
        
        if (service != null) {
            val success = serviceManager.startService(service.serviceId)
            if (success) {
                println("Manager $managerId: Started ${service.serviceName}")
            } else {
                println("Manager $managerId: Failed to start ${service.serviceName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateServiceStopping(
    serviceManager: ServiceManager,
    managerId: Int
) {
    val services = serviceManager.getAllServices()
    
    repeat(10) { attempt ->
        val service = services.filter { it.running }.randomOrNull()
        
        if (service != null) {
            val success = serviceManager.stopService(service.serviceId)
            if (success) {
                println("Manager $managerId: Stopped ${service.serviceName}")
            } else {
                println("Manager $managerId: Failed to stop ${service.serviceName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateDependencyActivation(
    serviceManager: ServiceManager,
    managerId: Int
) {
    val dependencies = serviceManager.getAllDependencies()
    
    repeat(8) { attempt ->
        val dependency = dependencies.filter { !it.active }.randomOrNull()
        
        if (dependency != null) {
            val success = serviceManager.activateDependency(dependency.dependencyId)
            if (success) {
                println("Manager $managerId: Activated ${dependency.dependencyName}")
            } else {
                println("Manager $managerId: Failed to activate ${dependency.dependencyName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateDependencyDeactivation(
    serviceManager: ServiceManager,
    managerId: Int
) {
    val dependencies = serviceManager.getAllDependencies()
    
    repeat(8) { attempt ->
        val dependency = dependencies.filter { it.active }.randomOrNull()
        
        if (dependency != null) {
            val success = serviceManager.deactivateDependency(dependency.dependencyId)
            if (success) {
                println("Manager $managerId: Deactivated ${dependency.dependencyName}")
            } else {
                println("Manager $managerId: Failed to deactivate ${dependency.dependencyName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateServiceWithDependency(
    serviceManager: ServiceManager,
    managerId: Int
) {
    val services = serviceManager.getAllServices()
    val dependencies = serviceManager.getAllDependencies()
    
    repeat(10) { attempt ->
        val service = services.filter { !it.running }.randomOrNull()
        val dependency = dependencies.filter { it.active }.randomOrNull()
        
        if (service != null && dependency != null) {
            val success = serviceManager.startServiceWithDependency(
                service.serviceId,
                dependency.dependencyId
            )
            
            if (success) {
                println("Manager $managerId: Started ${service.serviceName} with ${dependency.dependencyName}")
            } else {
                println("Manager $managerId: Failed to start service with dependency")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateServiceTransfer(
    serviceManager: ServiceManager,
    transferId: Int
) {
    val services = serviceManager.getAllServices()
    
    repeat(6) { attempt ->
        val runningServices = services.filter { it.running }
        val stoppedServices = services.filter { !it.running }
        
        if (runningServices.isNotEmpty() && stoppedServices.isNotEmpty()) {
            val fromService = runningServices.random()
            val toService = stoppedServices.random()
            
            val success = serviceManager.transferService(
                fromService.serviceId,
                toService.serviceId
            )
            
            if (success) {
                println("Transfer $transferId: ${fromService.serviceName} -> ${toService.serviceName}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateServiceSwap(
    serviceManager: ServiceManager,
    swapId: Int
) {
    val services = serviceManager.getAllServices()
    
    repeat(5) { attempt ->
        val runningServices = services.filter { it.running }
        
        if (runningServices.size >= 2) {
            val service1 = runningServices.random()
            val service2 = runningServices.filter { it.serviceId != service1.serviceId }.random()
            
            val success = serviceManager.swapServices(
                service1.serviceId,
                service2.serviceId
            )
            
            if (success) {
                println("Swap $swapId: ${service1.serviceName} <-> ${service2.serviceName}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorServiceManager(
    serviceManager: ServiceManager,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val services = serviceManager.getAllServices()
        val dependencies = serviceManager.getAllDependencies()
        
        val running = services.count { it.running }
        val locked = services.count { it.locked }
        val active = dependencies.count { it.active }
        val lockedDeps = dependencies.count { it.locked }
        
        println("Monitor $monitorId: Running=$running, Locked=$locked, " +
                "ActiveDeps=$active, LockedDeps=$lockedDeps")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val serviceManager = ServiceManager()
    
    println("Starting Service Manager Simulation...")
    println("Initial Service Status:")
    serviceManager.getAllServices().forEach { service ->
        println("  ${service.serviceId} (${service.serviceName}): " +
                "Running=${service.running}, Locked=${service.locked}")
    }
    println()
    
    println("Initial Dependency Status:")
    serviceManager.getAllDependencies().forEach { dependency ->
        println("  ${dependency.dependencyId} (${dependency.dependencyName}): " +
                "Active=${dependency.active}, Locked=${dependency.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateServiceStarting(serviceManager, 1)
    })
    
    jobs.add(launch {
        simulateServiceStarting(serviceManager, 2)
    })
    
    jobs.add(launch {
        simulateServiceStopping(serviceManager, 1)
    })
    
    jobs.add(launch {
        simulateServiceStopping(serviceManager, 2)
    })
    
    jobs.add(launch {
        simulateDependencyActivation(serviceManager, 1)
    })
    
    jobs.add(launch {
        simulateDependencyDeactivation(serviceManager, 1)
    })
    
    jobs.add(launch {
        simulateServiceWithDependency(serviceManager, 1)
    })
    
    jobs.add(launch {
        simulateServiceTransfer(serviceManager, 1)
    })
    
    jobs.add(launch {
        simulateServiceSwap(serviceManager, 1)
    })
    
    jobs.add(launch {
        monitorServiceManager(serviceManager, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val services = serviceManager.getAllServices()
    val dependencies = serviceManager.getAllDependencies()
    
    println("\n=== Final Service Status ===")
    services.forEach { service ->
        println("  ${service.serviceId} (${service.serviceName}): " +
                "Running=${service.running}, Locked=${service.locked}")
    }
    
    println("\n=== Final Dependency Status ===")
    dependencies.forEach { dependency ->
        println("  ${dependency.dependencyId} (${dependency.dependencyName}): " +
                "Active=${dependency.active}, Locked=${dependency.locked}")
    }
    
    val running = services.count { it.running }
    val locked = services.count { it.locked }
    val active = dependencies.count { it.active }
    val lockedDeps = dependencies.count { it.locked }
    
    println("\nRunning Services: $running/${services.size}")
    println("Locked Services: $locked/${services.size}")
    println("Active Dependencies: $active/${dependencies.size}")
    println("Locked Dependencies: $lockedDeps/${dependencies.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - startService(): servicePoolMutex -> service.mutex")
    println("  - stopService(): service.mutex -> servicePoolMutex")
    println("  - activateDependency(): dependencyPoolMutex -> dependency.mutex")
    println("  - deactivateDependency(): dependency.mutex -> dependencyPoolMutex")
    println("  - startServiceWithDependency(): service.mutex -> dependency.mutex")
    println("  - transferService(): service1.mutex -> service2.mutex")
    println("  - swapServices(): service1.mutex -> service2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}