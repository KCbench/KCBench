import kotlinx.coroutines.*
import kotlin.random.Random

data class ConfigSection(
    val sectionName: String,
    var loaded: Boolean = false,
    var validated: Boolean = false,
    var applied: Boolean = false,
    val dependencies: List<String>
)

class ConfigurationLoader {
    private val sections = mutableMapOf<String, ConfigSection>()
    
    init {
        initializeSections()
    }
    
    private fun initializeSections() {
        val sectionConfigs = listOf(
            Triple("Database", listOf(), 100),
            Triple("Cache", listOf("Database"), 80),
            Triple("API", listOf("Database", "Cache"), 120),
            Triple("Auth", listOf("Database"), 90),
            Triple("Logging", listOf(), 60),
            Triple("Metrics", listOf("Logging"), 70),
            Triple("Storage", listOf("Database"), 110),
            Triple("Network", listOf("API"), 100),
            Triple("Security", listOf("Auth"), 85),
            Triple("Services", listOf("API", "Storage"), 130)
        )
        
        sectionConfigs.forEach { (sectionName, deps, loadTime) ->
            sections[sectionName] = ConfigSection(
                sectionName = sectionName,
                loaded = false,
                validated = false,
                applied = false,
                dependencies = deps
            )
        }
    }
    
    suspend fun loadSection(sectionName: String): Boolean {
        val section = sections[sectionName] ?: return false
        
        if (section.loaded) {
            return true
        }
        
        val dependenciesLoaded = section.dependencies.all { depName ->
            val depSection = sections[depName]
            depSection != null && depSection.loaded
        }
        
        if (!dependenciesLoaded) {
            return false
        }
        
        delay(Random.nextLong(20, 80))
        
        section.loaded = true
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun validateSection(sectionName: String): Boolean {
        val section = sections[sectionName] ?: return false
        
        if (!section.loaded) {
            return false
        }
        
        if (section.validated) {
            return true
        }
        
        delay(Random.nextLong(15, 50))
        
        section.validated = true
        delay(Random.nextLong(10, 20))
        
        return true
    }
    
    suspend fun applySection(sectionName: String): Boolean {
        val section = sections[sectionName] ?: return false
        
        if (!section.validated) {
            return false
        }
        
        if (section.applied) {
            return true
        }
        
        delay(Random.nextLong(20, 70))
        
        section.applied = true
        delay(Random.nextLong(10, 20))
        
        return true
    }
    
    suspend fun loadAllSections(): Int {
        var loaded = 0
        
        sections.keys.forEach { sectionName ->
            if (loadSection(sectionName)) {
                loaded++
            }
        }
        
        return loaded
    }
    
    suspend fun validateAllSections(): Int {
        var validated = 0
        
        sections.keys.forEach { sectionName ->
            if (validateSection(sectionName)) {
                validated++
            }
        }
        
        return validated
    }
    
    suspend fun applyAllSections(): Int {
        var applied = 0
        
        sections.keys.forEach { sectionName ->
            if (applySection(sectionName)) {
                applied++
            }
        }
        
        return applied
    }
    
    suspend fun resetSection(sectionName: String): Boolean {
        val section = sections[sectionName] ?: return false
        
        section.loaded = false
        section.validated = false
        section.applied = false
        
        delay(Random.nextLong(10, 30))
        
        return true
    }
    
    suspend fun getSectionStatus(sectionName: String): ConfigSection? {
        return sections[sectionName]
    }
    
    fun getAllSections() = sections.values.toList()
}

class ConfigManager(
    private val loader: ConfigurationLoader,
    private val managerName: String
) {
    suspend fun loadRandomSection(): Boolean {
        val sections = loader.getAllSections()
        val unloadedSections = sections.filter { !it.loaded }
        
        if (unloadedSections.isEmpty()) {
            return false
        }
        
        val section = unloadedSections.random()
        return loader.loadSection(section.sectionName)
    }
    
    suspend fun validateRandomSection(): Boolean {
        val sections = loader.getAllSections()
        val loadedSections = sections.filter { it.loaded && !it.validated }
        
        if (loadedSections.isEmpty()) {
            return false
        }
        
        val section = loadedSections.random()
        return loader.validateSection(section.sectionName)
    }
    
    suspend fun applyRandomSection(): Boolean {
        val sections = loader.getAllSections()
        val validatedSections = sections.filter { it.validated && !it.applied }
        
        if (validatedSections.isEmpty()) {
            return false
        }
        
        val section = validatedSections.random()
        return loader.applySection(section.sectionName)
    }
}

suspend fun simulateConfigLoading(
    manager: ConfigManager,
    managerId: Int
) {
    repeat(12) { attempt ->
        manager.loadRandomSection()
        delay(Random.nextLong(30, 100))
    }
}

suspend fun simulateConfigValidation(
    manager: ConfigManager,
    managerId: Int
) {
    repeat(10) { attempt ->
        manager.validateRandomSection()
        delay(Random.nextLong(40, 120))
    }
}

suspend fun simulateConfigApplication(
    manager: ConfigManager,
    managerId: Int
) {
    repeat(10) { attempt ->
        manager.applyRandomSection()
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateConfigReset(
    loader: ConfigurationLoader
) {
    repeat(8) { attempt ->
        val sections = loader.getAllSections()
        val section = sections.random()
        
        loader.resetSection(section.sectionName)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateFullConfigLoad(
    loader: ConfigurationLoader
) {
    repeat(6) { attempt ->
        loader.loadAllSections()
        delay(Random.nextLong(200, 500))
    }
}

suspend fun simulateConfigMonitoring(
    loader: ConfigurationLoader
) {
    repeat(15) { attempt ->
        val sections = loader.getAllSections()
        
        println("Config Status:")
        println("  Loaded: ${sections.count { it.loaded }}")
        println("  Validated: ${sections.count { it.validated }}")
        println("  Applied: ${sections.count { it.applied }}")
        
        val invalidLoaded = sections.filter { section ->
            section.loaded && !section.dependencies.all { depName ->
                val depSection = loader.getSectionStatus(depName)
                depSection != null && depSection.loaded
            }
        }
        
        if (invalidLoaded.isNotEmpty()) {
            println("  Invalid loaded sections: ${invalidLoaded.size}")
        }
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val loader = ConfigurationLoader()
    
    println("Starting Configuration Loader Simulation...")
    println("Initial Section Status:")
    loader.getAllSections().forEach { section ->
        println(
            "  ${section.sectionName}: Loaded=${section.loaded}, " +
            "Deps=${section.dependencies.joinToString()}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val managers = listOf(
        ConfigManager(loader, "Alice"),
        ConfigManager(loader, "Bob"),
        ConfigManager(loader, "Charlie"),
        ConfigManager(loader, "David")
    )
    
    managers.forEachIndexed { index, manager ->
        jobs.add(launch {
            simulateConfigLoading(manager, index)
        })
    }
    
    managers.forEachIndexed { index, manager ->
        jobs.add(launch {
            simulateConfigValidation(manager, index)
        })
    }
    
    managers.forEachIndexed { index, manager ->
        jobs.add(launch {
            simulateConfigApplication(manager, index)
        })
    }
    
    jobs.add(launch {
        simulateConfigReset(loader)
    })
    
    jobs.add(launch {
        simulateFullConfigLoad(loader)
    })
    
    jobs.add(launch {
        simulateConfigMonitoring(loader)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val sections = loader.getAllSections()
    val loadedSections = sections.filter { it.loaded }
    val validatedSections = sections.filter { it.validated }
    val appliedSections = sections.filter { it.applied }
    
    println("\n=== Final Section Status ===")
    sections.forEach { section ->
        println(
            "  ${section.sectionName}: Loaded=${section.loaded}, " +
            "Validated=${section.validated}, " +
            "Applied=${section.applied}"
        )
    }
    
    val invalidLoaded = loadedSections.filter { section ->
        !section.dependencies.all { depName ->
            val depSection = loader.getSectionStatus(depName)
            depSection != null && depSection.loaded
        }
    }
    
    if (invalidLoaded.isNotEmpty()) {
        println("\n⚠️  Sections loaded before dependencies:")
        invalidLoaded.forEach { section ->
            println("  ${section.sectionName}: ${section.dependencies}")
        }
    } else {
        println("\n✅ All sections loaded in correct order")
    }
    
    val totalSections = sections.size
    println("\nLoaded: ${loadedSections.size}/$totalSections")
    println("Validated: ${validatedSections.size}/$totalSections")
    println("Applied: ${appliedSections.size}/$totalSections")
}