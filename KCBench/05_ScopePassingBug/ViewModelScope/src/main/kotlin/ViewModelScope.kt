import kotlinx.coroutines.*
import kotlin.random.Random

class ViewModel {
    private var isActive = true
    private val jobs = mutableListOf<Job>()
    private var dataLoaded = false
    private var loadingError = false
    private var requestCount = 0
    
    fun loadData() {
        if (!isActive) {
            println("ViewModel is not active, cannot load data")
            return
        }
        
        val job = GlobalScope.launch {
            try {
                performDataLoad()
            } catch (e: CancellationException) {
                println("Data load cancelled: ${e.message}")
            } catch (e: Exception) {
                println("Data load error: ${e.message}")
                loadingError = true
            }
        }
        
        jobs.add(job)
    }
    
    fun loadMultipleDataSources() {
        val sources = listOf(
            "Users", "Posts", "Comments", "Albums", "Photos"
        )
        
        sources.forEach { source ->
            loadDataSource(source)
        }
    }
    
    private fun loadDataSource(source: String) {
        val job = GlobalScope.launch {
            try {
                performDataSourceLoad(source)
            } catch (e: CancellationException) {
                println("$source load cancelled: ${e.message}")
            } catch (e: Exception) {
                println("$source load error: ${e.message}")
            }
        }
        
        jobs.add(job)
    }
    
    fun refreshData() {
        if (!isActive) {
            println("ViewModel is not active, cannot refresh data")
            return
        }
        
        val job = GlobalScope.launch {
            try {
                performDataRefresh()
            } catch (e: CancellationException) {
                println("Data refresh cancelled: ${e.message}")
            } catch (e: Exception) {
                println("Data refresh error: ${e.message}")
            }
        }
        
        jobs.add(job)
    }
    
    private suspend fun performDataLoad() {
        delay(Random.nextLong(500, 1500))
        
        if (Random.nextBoolean()) {
            dataLoaded = true
            println("Data loaded successfully")
        } else {
            throw Exception("Failed to load data")
        }
    }
    
    private suspend fun performDataSourceLoad(source: String) {
        delay(Random.nextLong(300, 1000))
        
        if (Random.nextBoolean()) {
            println("$source loaded successfully")
        } else {
            throw Exception("Failed to load $source")
        }
    }
    
    private suspend fun performDataRefresh() {
        delay(Random.nextLong(400, 1200))
        
        if (Random.nextBoolean()) {
            dataLoaded = true
            println("Data refreshed successfully")
        } else {
            throw Exception("Failed to refresh data")
        }
    }
    
    fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }
    
    fun destroy() {
        isActive = false
        cancelAllJobs()
    }
    
    fun isActive() = isActive
    fun isDataLoaded() = dataLoaded
    fun hasLoadingError() = loadingError
    fun getRequestCount() = requestCount
}

class UserRepository {
    private var usersLoaded = false
    private var userCount = 0
    
    fun loadUsers(viewModelScope: CoroutineScope) {
        viewModelScope.launch {
            try {
                performUserLoad()
            } catch (e: CancellationException) {
                println("User load cancelled: ${e.message}")
            } catch (e: Exception) {
                println("User load error: ${e.message}")
            }
        }
    }
    
    fun loadUsersInGlobalScope() {
        GlobalScope.launch {
            try {
                performUserLoad()
            } catch (e: CancellationException) {
                println("User load cancelled: ${e.message}")
            } catch (e: Exception) {
                println("User load error: ${e.message}")
            }
        }
    }
    
    private suspend fun performUserLoad() {
        delay(Random.nextLong(300, 800))
        
        userCount = Random.nextInt(10, 100)
        usersLoaded = true
        println("Loaded $userCount users")
    }
    
    fun isUsersLoaded() = usersLoaded
    fun getUserCount() = userCount
}

class NetworkObserver {
    private var isObserving = false
    private var observationCount = 0
    
    fun startObserving(viewModelScope: CoroutineScope) {
        if (isObserving) {
            return
        }
        
        isObserving = true
        
        viewModelScope.launch {
            try {
                performObservation()
            } catch (e: CancellationException) {
                println("Observation cancelled: ${e.message}")
                isObserving = false
            } catch (e: Exception) {
                println("Observation error: ${e.message}")
                isObserving = false
            }
        }
    }
    
    fun startObservingInGlobalScope() {
        if (isObserving) {
            return
        }
        
        isObserving = true
        
        GlobalScope.launch {
            try {
                performObservation()
            } catch (e: CancellationException) {
                println("Observation cancelled: ${e.message}")
                isObserving = false
            } catch (e: Exception) {
                println("Observation error: ${e.message}")
                isObserving = false
            }
        }
    }
    
    private suspend fun performObservation() {
        repeat(10) { iteration ->
            if (!isObserving) {
                return
            }
            
            delay(Random.nextLong(200, 500))
            observationCount++
            println("Network observation #$observationCount")
        }
        
        isObserving = false
    }
    
    fun stopObserving() {
        isObserving = false
    }
    
    fun isObserving() = isObserving
    fun getObservationCount() = observationCount
}

class DataCache {
    private val cache = mutableMapOf<String, String>()
    private var cacheSize = 0
    
    fun preloadCache(viewModelScope: CoroutineScope) {
        val keys = listOf(
            "key1", "key2", "key3", "key4", "key5"
        )
        
        keys.forEach { key ->
            viewModelScope.launch {
                try {
                    preloadKey(key)
                } catch (e: CancellationException) {
                    println("Cache preload cancelled for $key: ${e.message}")
                } catch (e: Exception) {
                    println("Cache preload error for $key: ${e.message}")
                }
            }
        }
    }
    
    fun preloadCacheInGlobalScope() {
        val keys = listOf(
            "key1", "key2", "key3", "key4", "key5"
        )
        
        keys.forEach { key ->
            GlobalScope.launch {
                try {
                    preloadKey(key)
                } catch (e: CancellationException) {
                    println("Cache preload cancelled for $key: ${e.message}")
                } catch (e: Exception) {
                    println("Cache preload error for $key: ${e.message}")
                }
            }
        }
    }
    
    private suspend fun preloadKey(key: String) {
        delay(Random.nextLong(100, 400))
        
        val value = "Value for $key"
        cache[key] = value
        cacheSize++
        println("Preloaded: $key")
    }
    
    fun getCacheSize() = cacheSize
}

suspend fun simulateViewModelLifecycle(
    viewModel: ViewModel,
    scope: CoroutineScope
) {
    println("ViewModel created and active")
    
    viewModel.loadData()
    delay(500)
    
    viewModel.loadMultipleDataSources()
    delay(1000)
    
    viewModel.refreshData()
    delay(500)
    
    println("\nViewModel is being destroyed...")
    viewModel.destroy()
    
    delay(2000)
    
    println("ViewModel destroyed")
}

suspend fun simulateUserRepository(
    repository: UserRepository,
    scope: CoroutineScope
) {
    println("Loading users with provided scope")
    repository.loadUsers(scope)
    delay(1000)
    
    println("Loading users with GlobalScope")
    repository.loadUsersInGlobalScope()
    delay(1000)
    
    println("\nUser Repository Summary:")
    println("  Users loaded: ${repository.isUsersLoaded()}")
    println("  User count: ${repository.getUserCount()}")
}

suspend fun simulateNetworkObserver(
    observer: NetworkObserver,
    scope: CoroutineScope
) {
    println("Starting network observation with provided scope")
    observer.startObserving(scope)
    delay(2000)
    
    println("Stopping network observation")
    observer.stopObserving()
    delay(500)
    
    println("Starting network observation with GlobalScope")
    observer.startObservingInGlobalScope()
    delay(2000)
    
    println("\nNetwork Observer Summary:")
    println("  Is observing: ${observer.isObserving()}")
    println("  Observation count: ${observer.getObservationCount()}")
}

suspend fun simulateDataCache(
    cache: DataCache,
    scope: CoroutineScope
) {
    println("Preloading cache with provided scope")
    cache.preloadCache(scope)
    delay(1000)
    
    println("Preloading cache with GlobalScope")
    cache.preloadCacheInGlobalScope()
    delay(1000)
    
    println("\nData Cache Summary:")
    println("  Cache size: ${cache.getCacheSize()}")
}

fun main() = runBlocking {
    println("Starting ViewModel Scope Simulation...")
    println()
    
    val viewModel = ViewModel()
    val repository = UserRepository()
    val observer = NetworkObserver()
    val cache = DataCache()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateViewModelLifecycle(viewModel, this)
    })
    
    jobs.add(launch {
        simulateUserRepository(repository, this)
    })
    
    jobs.add(launch {
        simulateNetworkObserver(observer, this)
    })
    
    jobs.add(launch {
        simulateDataCache(cache, this)
    })
    
    jobs.forEach { it.join() }
    
    delay(1000)
    
    println("\n=== Final Summary ===")
    println("ViewModel:")
    println("  Active: ${viewModel.isActive()}")
    println("  Data loaded: ${viewModel.isDataLoaded()}")
    println("  Loading error: ${viewModel.hasLoadingError()}")
    
    println("\nUser Repository:")
    println("  Users loaded: ${repository.isUsersLoaded()}")
    println("  User count: ${repository.getUserCount()}")
    
    println("\nNetwork Observer:")
    println("  Is observing: ${observer.isObserving()}")
    println("  Observation count: ${observer.getObservationCount()}")
    
    println("\nData Cache:")
    println("  Cache size: ${cache.getCacheSize()}")
    
    println("\n⚠️  Scope Passing Bug Warning:")
    println("  The code uses GlobalScope in multiple places:")
    println("  - ViewModel.loadData()")
    println("  - ViewModel.loadDataSource()")
    println("  - ViewModel.refreshData()")
    println("  - UserRepository.loadUsersInGlobalScope()")
    println("  - NetworkObserver.startObservingInGlobalScope()")
    println("  - DataCache.preloadCacheInGlobalScope()")
    println("  These coroutines will continue running even after the ViewModel is destroyed,")
    println("  leading to memory leaks and potential crashes.")
    println("  Fix: Use viewModelScope or lifecycle-aware scopes instead of GlobalScope.")
}