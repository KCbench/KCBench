import kotlinx.coroutines.*
import kotlin.random.Random

class DataReloader {
    private var data = ""
    private var reloadJob: Job? = null
    
    suspend fun reloadData() = coroutineScope {
        reloadJob?.cancel()
        
        reloadJob = launch {
            val tmp = data
            delay(50)
            data = tmp + "New"
            println("Data reloaded: $data")
        }
    }
    
    suspend fun reloadMultiple(times: Int) = coroutineScope {
        repeat(times) {
            reloadData()
            delay(100)
        }
    }
    
    fun getData() = data
}

class CacheReloader {
    private var cache = mutableMapOf<String, String>()
    private var reloadJob: Job? = null
    
    suspend fun reloadCache(key: String) = coroutineScope {
        reloadJob?.cancel()
        
        reloadJob = launch {
            val tmp = cache.toMap()
            delay(50)
            cache[key] = "NewValue"
            println("Cache reloaded for $key: ${cache[key]}")
        }
    }
    
    suspend fun reloadMultipleCache(keys: List<String>) = coroutineScope {
        keys.forEach { key ->
            reloadCache(key)
            delay(100)
        }
    }
    
    fun getCache() = cache.toMap()
}

class ConfigReloader {
    private var config = mutableMapOf<String, String>()
    private var reloadJob: Job? = null
    
    suspend fun reloadConfig(configKey: String) = coroutineScope {
        reloadJob?.cancel()
        
        reloadJob = launch {
            val tmp = config.toMap()
            delay(50)
            config[configKey] = "NewConfig"
            println("Config reloaded for $configKey: ${config[configKey]}")
        }
    }
    
    suspend fun reloadMultipleConfig(configKeys: List<String>) = coroutineScope {
        configKeys.forEach { configKey ->
            reloadConfig(configKey)
            delay(100)
        }
    }
    
    fun getConfig() = config.toMap()
}

class SettingsReloader {
    private var settings = mutableMapOf<String, String>()
    private var reloadJob: Job? = null
    
    suspend fun reloadSettings(settingKey: String) = coroutineScope {
        reloadJob?.cancel()
        
        reloadJob = launch {
            val tmp = settings.toMap()
            delay(50)
            settings[settingKey] = "NewSetting"
            println("Settings reloaded for $settingKey: ${settings[settingKey]}")
        }
    }
    
    suspend fun reloadMultipleSettings(settingKeys: List<String>) = coroutineScope {
        settingKeys.forEach { settingKey ->
            reloadSettings(settingKey)
            delay(100)
        }
    }
    
    fun getSettings() = settings.toMap()
}

class StateReloader {
    private var state = mutableMapOf<String, String>()
    private var reloadJob: Job? = null
    
    suspend fun reloadState(stateKey: String) = coroutineScope {
        reloadJob?.cancel()
        
        reloadJob = launch {
            val tmp = state.toMap()
            delay(50)
            state[stateKey] = "NewState"
            println("State reloaded for $stateKey: ${state[stateKey]}")
        }
    }
    
    suspend fun reloadMultipleState(stateKeys: List<String>) = coroutineScope {
        stateKeys.forEach { stateKey ->
            reloadState(stateKey)
            delay(100)
        }
    }
    
    fun getState() = state.toMap()
}

suspend fun simulateDataReloader(
    reloader: DataReloader,
    reloaderId: Int
) {
    repeat(10) { attempt ->
        reloader.reloadData()
        delay(Random.nextLong(50, 150))
    }
    
    println("Data reloader $reloaderId completed")
}

suspend fun simulateCacheReloader(
    reloader: CacheReloader,
    reloaderId: Int
) {
    val keys = listOf(
        "key1", "key2", "key3", "key4", "key5"
    )
    
    reloader.reloadMultipleCache(keys)
    
    println("Cache reloader $reloaderId completed")
}

suspend fun simulateConfigReloader(
    reloader: ConfigReloader,
    reloaderId: Int
) {
    val configKeys = listOf(
        "config1", "config2", "config3", "config4", "config5"
    )
    
    reloader.reloadMultipleConfig(configKeys)
    
    println("Config reloader $reloaderId completed")
}

suspend fun simulateSettingsReloader(
    reloader: SettingsReloader,
    reloaderId: Int
) {
    val settingKeys = listOf(
        "setting1", "setting2", "setting3", "setting4", "setting5"
    )
    
    reloader.reloadMultipleSettings(settingKeys)
    
    println("Settings reloader $reloaderId completed")
}

suspend fun simulateStateReloader(
    reloader: StateReloader,
    reloaderId: Int
) {
    val stateKeys = listOf(
        "state1", "state2", "state3", "state4", "state5"
    )
    
    reloader.reloadMultipleState(stateKeys)
    
    println("State reloader $reloaderId completed")
}

suspend fun monitorReloaders(
    dataReloader: DataReloader,
    cacheReloader: CacheReloader,
    configReloader: ConfigReloader,
    settingsReloader: SettingsReloader,
    stateReloader: StateReloader,
    monitorId: Int
) {
    repeat(20) { attempt ->
        println("Monitor $monitorId:")
        println("  Data: ${dataReloader.getData()}")
        println("  Cache: ${cacheReloader.getCache()}")
        println("  Config: ${configReloader.getConfig()}")
        println("  Settings: ${settingsReloader.getSettings()}")
        println("  State: ${stateReloader.getState()}")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    println("Starting Data Reload Simulation...")
    println()
    
    val dataReloader = DataReloader()
    val cacheReloader = CacheReloader()
    val configReloader = ConfigReloader()
    val settingsReloader = SettingsReloader()
    val stateReloader = StateReloader()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateDataReloader(dataReloader, 1)
    })
    
    jobs.add(launch {
        simulateDataReloader(dataReloader, 2)
    })
    
    jobs.add(launch {
        simulateCacheReloader(cacheReloader, 1)
    })
    
    jobs.add(launch {
        simulateConfigReloader(configReloader, 1)
    })
    
    jobs.add(launch {
        simulateSettingsReloader(settingsReloader, 1)
    })
    
    jobs.add(launch {
        simulateStateReloader(stateReloader, 1)
    })
    
    jobs.add(launch {
        monitorReloaders(
            dataReloader,
            cacheReloader,
            configReloader,
            settingsReloader,
            stateReloader,
            1
        )
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Reloaded Data ===")
    println("Data: ${dataReloader.getData()}")
    println("Cache: ${cacheReloader.getCache()}")
    println("Config: ${configReloader.getConfig()}")
    println("Settings: ${settingsReloader.getSettings()}")
    println("State: ${stateReloader.getState()}")
    
    println("\n⚠️  Cancellation Race Warning:")
    println("  The code cancels reload jobs and immediately starts new ones:")
    println("  - DataReloader.reloadData() cancels reloadJob and starts new one")
    println("  - CacheReloader.reloadCache() cancels reloadJob and starts new one")
    println("  - ConfigReloader.reloadConfig() cancels reloadJob and starts new one")
    println("  - SettingsReloader.reloadSettings() cancels reloadJob and starts new one")
    println("  - StateReloader.reloadState() cancels reloadJob and starts new one")
    println("  The cancelled job may still be running when the new job starts,")
    println("  leading to race conditions on shared state.")
    println("  Fix: Use job.cancelAndJoin() or ensure job is cancelled before starting new one.")
}