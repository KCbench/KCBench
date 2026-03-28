# Kotlin Concurrency Error Summary

This document provides detailed information about concurrency bugs in all 100 benchmark projects.

---

## 1. Data Race / Suspension Race

Shared state corruption across suspension points in coroutines.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | ECommerceInventory.kt | 45-47, 51 | `product.stock`, `product.reserved` | Lines 45-46 read stock and reserved quantity, line 47 `delay()` suspends, line 51 writes reserved quantity, multiple coroutines executing concurrently causes reserved quantity to exceed stock | [View Code](01_DataRace/ECommerceInventory/ECommerceInventory.kt) |
| 2 | BankTransactions.kt | 36-37, 44 | `sender.balance` | Line 36 reads sender balance, line 37 `delay()` suspends, line 44 writes new balance, multiple coroutines transferring concurrently causes balance calculation error | [View Code](01_DataRace/BankTransactions/BankTransactions.kt) |
| 3 | TaskManagement.kt | 54-55, 57 | `task.status` | Line 54 reads task status, line 55 `delay()` suspends, line 57 writes new status, multiple coroutines assigning tasks concurrently causes status inconsistency | [View Code](01_DataRace/TaskManagement/TaskManagement.kt) |
| 4 | CacheManagement.kt | 41-43 | `missCount` | Line 41 reads miss count, line 42 `delay()` suspends, line 43 writes new count, multiple coroutines updating cache statistics concurrently causes count error | [View Code](01_DataRace/CacheManagement/CacheManagement.kt) |
| 5 | SessionManagement.kt | 50-51, 53 | `session.lastActivity` | Line 50 reads last activity time, line 51 `delay()` suspends, line 53 writes new time, multiple coroutines updating session concurrently causes timestamp inconsistency | [View Code](01_DataRace/SessionManagement/SessionManagement.kt) |
| 6 | ChatRoom.kt | 51-53 | `messageCount` | Line 51 reads message count, line 52 `delay()` suspends, line 53 writes new count, multiple coroutines sending messages concurrently causes count error | [View Code](01_DataRace/ChatRoom/ChatRoom.kt) |
| 7 | GameServer.kt | 51-52, 53 | `player.score` | Line 51 reads player score, line 52 `delay()` suspends, line 53 writes new score, multiple coroutines updating score concurrently causes leaderboard error | [View Code](01_DataRace/GameServer/GameServer.kt) |
| 8 | OrderProcessing.kt | 52-53, 55 | `order.status` | Line 52 reads order status, line 53 `delay()` suspends, line 55 writes new status, multiple coroutines processing orders concurrently causes status inconsistency | [View Code](01_DataRace/OrderProcessing/OrderProcessing.kt) |
| 9 | MonitoringSystem.kt | 58-60 | `totalReadings` | Line 58 reads reading count, line 59 `delay()` suspends, line 60 writes new count, multiple coroutines recording data concurrently causes statistics error | [View Code](01_DataRace/MonitoringSystem/MonitoringSystem.kt) |
| 10 | FileUploadManager.kt | 54-55, 57 | `file.uploadedBytes` | Line 54 reads uploaded bytes, line 55 `delay()` suspends, line 57 writes new bytes, multiple coroutines updating progress concurrently causes progress calculation error | [View Code](01_DataRace/FileUploadManager/FileUploadManager.kt) |

---

## 2. Atomicity Violation

Check-then-act pattern interrupted by suspension points.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | BankTransfer.kt | 36-37, 44 | `sender.balance` | Lines 36-37 check if sender balance is sufficient, line 37 `delay()` suspends, line 44 deducts, multiple coroutines transferring concurrently causes balance to become negative | [View Code](02_AtomicityViolation/BankTransfer/BankTransfer.kt) |
| 2 | TicketBooking.kt | 54-55, 57 | `ticket.status` | Lines 54-55 check if ticket status is available, line 55 `delay()` suspends, line 57 modifies status to reserved, multiple coroutines booking concurrently causes same ticket to be reserved multiple times | [View Code](02_AtomicityViolation/TicketBooking/TicketBooking.kt) |
| 3 | InventoryReservation.kt | 45-46, 51 | `product.stock`, `product.reserved` | Lines 45-46 check if stock is sufficient, line 46 `delay()` suspends, line 51 increases reserved quantity, multiple coroutines reserving concurrently causes reserved quantity to exceed stock | [View Code](02_AtomicityViolation/InventoryReservation/InventoryReservation.kt) |
| 4 | SeatAllocation.kt | 48-49, 51 | `seat.status` | Lines 48-49 check if seat status is available, line 49 `delay()` suspends, line 51 modifies status to selected, multiple coroutines selecting concurrently causes same seat to be selected multiple times | [View Code](02_AtomicityViolation/SeatAllocation/SeatAllocation.kt) |
| 5 | AccountManagement.kt | 37-38, 44 | `account.balance` | Lines 37-38 check if account balance is sufficient, line 38 `delay()` suspends, line 44 deducts, multiple coroutines purchasing concurrently causes balance to become negative | [View Code](02_AtomicityViolation/AccountManagement/AccountManagement.kt) |
| 6 | ResourcePool.kt | 44-45, 51 | `resource.status` | Lines 44-45 check if resource status is available, line 45 `delay()` suspends, line 51 allocates resource, multiple coroutines allocating concurrently causes same resource to be allocated multiple times | [View Code](02_AtomicityViolation/ResourcePool/ResourcePool.kt) |
| 7 | ConnectionPool.kt | 46-47, 53 | `connection.status` | Lines 46-47 check if connection status is idle, line 47 `delay()` suspends, line 53 allocates connection, multiple coroutines acquiring concurrently causes same connection to be allocated multiple times | [View Code](02_AtomicityViolation/ConnectionPool/ConnectionPool.kt) |
| 8 | TaskQueue.kt | 49-50, 52 | `task.status` | Lines 49-50 check if task status is pending, line 50 `delay()` suspends, line 52 assigns task, multiple coroutines assigning concurrently causes same task to be assigned multiple times | [View Code](02_AtomicityViolation/TaskQueue/TaskQueue.kt) |
| 9 | DataConsistency.kt | 44-45, 51 | `record.version` | Lines 44-45 check if record version matches, line 45 `delay()` suspends, line 51 updates record, multiple coroutines updating concurrently causes version conflict and data loss | [View Code](02_AtomicityViolation/DataConsistency/DataConsistency.kt) |
| 10 | RateLimiter.kt | 39-40, 47 | `entry.requestCount` | Lines 39-40 check if request count exceeds limit, line 40 `delay()` suspends, line 47 increases request count, multiple coroutines requesting concurrently causes actual request count to exceed limit | [View Code](02_AtomicityViolation/RateLimiter/RateLimiter.kt) |

---

## 3. Order Violation

Dependency order issues in concurrent operations.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | InitializationSequence.kt | 39-40, 46 | `service.initialized` | Lines 39-40 check if service is initialized, line 46 sets initialization status, multiple coroutines initializing concurrently causes dependent services to be marked as initialized before ready | [View Code](03_OrderViolation/InitializationSequence/InitializationSequence.kt) |
| 2 | PublishSubscribe.kt | 45-46, 52 | `topic.published` | Lines 45-46 check if topic is published, line 52 sets publish status, multiple coroutines publishing concurrently causes subscribers to try to subscribe before topic is published | [View Code](03_OrderViolation/PublishSubscribe/PublishSubscribe.kt) |
| 3 | StateDependency.kt | 37-38, 44 | `machine.currentState` | Lines 37-38 check if state transition is valid, line 44 executes state transition, multiple coroutines transitioning concurrently causes state machine to enter invalid state | [View Code](03_OrderViolation/StateDependency/StateDependency.kt) |
| 4 | PipelineProcessing.kt | 45-46, 51 | `stage.ready` | Lines 45-46 check if stage is ready, line 51 processes stage, multiple coroutines processing concurrently causes subsequent stages to start processing before prerequisite stages complete | [View Code](03_OrderViolation/PipelineProcessing/PipelineProcessing.kt) |
| 5 | EventStream.kt | 43-44, 50 | `event.processed` | Lines 43-44 check if event is processed, line 50 marks event as processed, multiple coroutines processing concurrently causes event processing order confusion | [View Code](03_OrderViolation/EventStream/EventStream.kt) |
| 6 | DataFlow.kt | 42-43, 49 | `node.computed` | Lines 42-43 check if node is computed, line 49 marks node as computed, multiple coroutines computing concurrently causes dependent nodes to be marked as computed before they complete | [View Code](03_OrderViolation/DataFlow/DataFlow.kt) |
| 7 | ConfigurationLoader.kt | 46-47, 53 | `section.loaded` | Lines 46-47 check if configuration section is loaded, line 53 sets load status, multiple coroutines loading concurrently causes dependent configuration to be marked as loaded before it is loaded | [View Code](03_OrderViolation/ConfigurationLoader/ConfigurationLoader.kt) |
| 8 | ServiceStartup.kt | 47-48, 54 | `component.initialized` | Lines 47-48 check if component is initialized, line 54 sets initialization status, multiple coroutines initializing concurrently causes dependent components to be marked as initialized before ready | [View Code](03_OrderViolation/ServiceStartup/ServiceStartup.kt) |
| 9 | WorkflowExecution.kt | 52-53, 59 | `step.completed` | Lines 52-53 check if step is completed, line 59 marks step as completed, multiple coroutines executing concurrently causes dependent steps to be marked as completed before they complete | [View Code](03_OrderViolation/WorkflowExecution/WorkflowExecution.kt) |
| 10 | ResourceInitialization.kt | 48-49, 55 | `resource.allocated` | Lines 48-49 check if resource is allocated, line 55 sets allocation status, multiple coroutines allocating concurrently causes dependent resources to be marked as allocated before they are allocated | [View Code](03_OrderViolation/ResourceInitialization/ResourceInitialization.kt) |

---

## 4. Deadlock

Circular waiting for resources causing system freeze.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | BankTransfer.kt | 56-57, 62 | `fromAccount.mutex`, `toAccount.mutex` | Lines 56-57 lock fromAccount, line 62 locks toAccount, bidirectional transfer with two coroutines locking accounts in opposite order causes deadlock | [View Code](04_Deadlock/BankTransfer/BankTransfer.kt) |
| 2 | ResourcePool.kt | 52-53, 58, 68-69, 74 | `poolMutex`, `resource.mutex` | allocateResource() locks poolMutex first then resource.mutex, releaseResource() locks resource.mutex first then poolMutex, causes deadlock | [View Code](04_Deadlock/ResourcePool/ResourcePool.kt) |
| 3 | DatabaseAccess.kt | 60-61, 66, 76-77, 82 | `connectionPoolMutex`, `connection.mutex` | acquireConnection() locks connectionPoolMutex first then connection.mutex, releaseConnection() locks connection.mutex first then connectionPoolMutex, causes deadlock | [View Code](04_Deadlock/DatabaseAccess/DatabaseAccess.kt) |
| 4 | FileProcessing.kt | 56-57, 62, 72-73, 78 | `filePoolMutex`, `file.mutex` | openFile() locks filePoolMutex first then file.mutex, closeFile() locks file.mutex first then filePoolMutex, causes deadlock | [View Code](04_Deadlock/FileProcessing/FileProcessing.kt) |
| 5 | NetworkConnection.kt | 56-57, 62, 72-73, 78 | `connectionPoolMutex`, `connection.mutex` | acquireConnection() locks connectionPoolMutex first then connection.mutex, releaseConnection() locks connection.mutex first then connectionPoolMutex, causes deadlock | [View Code](04_Deadlock/NetworkConnection/NetworkConnection.kt) |
| 6 | ServiceManager.kt | 56-57, 62, 72-73, 78 | `servicePoolMutex`, `service.mutex` | startService() locks servicePoolMutex first then service.mutex, stopService() locks service.mutex first then servicePoolMutex, causes deadlock | [View Code](04_Deadlock/ServiceManager/ServiceManager.kt) |
| 7 | TaskScheduler.kt | 56-57, 62, 72-73, 78 | `taskPoolMutex`, `task.mutex` | assignTask() locks taskPoolMutex first then task.mutex, completeTask() locks task.mutex first then taskPoolMutex, causes deadlock | [View Code](04_Deadlock/TaskScheduler/TaskScheduler.kt) |
| 8 | MessageQueue.kt | 56-57, 62, 72-73, 78 | `queuePoolMutex`, `queue.mutex` | lockQueue() locks queuePoolMutex first then queue.mutex, unlockQueue() locks queue.mutex first then queuePoolMutex, causes deadlock | [View Code](04_Deadlock/MessageQueue/MessageQueue.kt) |
| 9 | CacheSystem.kt | 56-57, 62, 72-73, 78 | `entryPoolMutex`, `entry.mutex` | cacheEntry() locks entryPoolMutex first then entry.mutex, evictEntry() locks entry.mutex first then entryPoolMutex, causes deadlock | [View Code](04_Deadlock/CacheSystem/CacheSystem.kt) |
| 10 | TransactionManager.kt | 56-57, 62, 72-73, 78 | `transactionPoolMutex`, `transaction.mutex` | beginTransaction() locks transactionPoolMutex first then transaction.mutex, commitTransaction() locks transaction.mutex first then transactionPoolMutex, causes deadlock | [View Code](04_Deadlock/TransactionManager/TransactionManager.kt) |

---

## 5. Scope Passing Bug

Incorrect coroutine scope usage leading to memory leaks.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | GlobalScopeUsage.kt | 28-29, 56-57, 73-74, 109-110 | `GlobalScope.launch` | DataProcessor, BackgroundTask, NetworkClient, CacheManager all use GlobalScope to launch coroutines, these coroutines continue running after main scope is destroyed, causing memory leaks | [View Code](05_ScopePassingBug/GlobalScopeUsage/GlobalScopeUsage.kt) |
| 2 | ViewModelScope.kt | 26-27, 54-55, 73-74, 110-111 | `GlobalScope.launch` | ViewModel, UserRepository, NetworkObserver, DataCache all use GlobalScope to launch coroutines, coroutines still running after ViewModel is destroyed, causing memory leaks and crashes | [View Code](05_ScopePassingBug/ViewModelScope/ViewModelScope.kt) |
| 3 | ExternalScope.kt | 16-17, 43-44, 61-62, 78-79 | `scope.launch` | ExternalService, DataFetcher, BackgroundWorker, EventProcessor, CacheUpdater all accept CoroutineScope parameter and launch coroutines, when scope is cancelled coroutines may be unexpectedly cancelled or continue running | [View Code](05_ScopePassingBug/ExternalScope/ExternalScope.kt) |
| 4 | CoroutineScopeLeak.kt | 12-13, 40-41, 57-58, 74-75, 91-92 | `scope.launch` | ResourceHolder, ConnectionPool, TaskExecutor, DataProcessor, EventListener all launch coroutines in provided scope, when scope is cancelled may cause resource leaks | [View Code](05_ScopePassingBug/CoroutineScopeLeak/CoroutineScopeLeak.kt) |
| 5 | JobLeak.kt | 12-13, 43-44, 71-72, 100-101, 129-130 | `GlobalScope.launch` | JobManager, BackgroundJob, PeriodicTask, JobPool, JobLeakDetector all use GlobalScope to launch Jobs, these Jobs continue running after main scope is destroyed | [View Code](05_ScopePassingBug/JobLeak/JobLeak.kt) |
| 6 | SupervisorScopeMisuse.kt | 16-17, 43-44, 60-61, 77-78, 95-96 | `supervisorScope` | TaskCoordinator, SupervisorTaskManager, ChildTaskManager, IndependentTaskRunner, ParallelTaskExecutor all use supervisorScope inside launch, breaking structured concurrency | [View Code](05_ScopePassingBug/SupervisorScopeMisuse/SupervisorScopeMisuse.kt) |
| 7 | CoroutineContextPassing.kt | 16-17, 43-44, 60-61, 77-78, 95-96 | `CoroutineScope(context)` | ContextAwareService, ContextDataProcessor, ContextTaskExecutor, ContextEventHandler, ContextCacheManager all create new CoroutineScope from CoroutineContext, breaking structured concurrency | [View Code](05_ScopePassingBug/CoroutineContextPassing/CoroutineContextPassing.kt) |
| 8 | StructuredConcurrencyViolation.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `GlobalScope.launch` | UnstructuredTaskRunner, IndependentCoroutineLauncher, AsyncTaskExecutor, ParallelJobRunner, DetachedTaskManager all use GlobalScope to launch coroutines, violating structured concurrency principles | [View Code](05_ScopePassingBug/StructuredConcurrencyViolation/StructuredConcurrencyViolation.kt) |
| 9 | ScopeCancellation.kt | 14-15, 42-43, 69-70, 96-97, 124-125 | `GlobalScope.launch` | CancellableTask, LongRunningOperation, PeriodicWorker, AsyncDataLoader, NetworkRequester all use GlobalScope to launch coroutines, cannot be properly cancelled, causing resource leaks | [View Code](05_ScopePassingBug/ScopeCancellation/ScopeCancellation.kt) |
| 10 | ScopeLifecycle.kt | 25-26, 53-54, 81-82, 109-110, 137-138 | `GlobalScope.launch` | LifecycleAwareComponent, ViewModelComponent, ActivityComponent, FragmentComponent, ServiceComponent all use GlobalScope to launch coroutines, coroutines still running after component is destroyed, causing memory leaks | [View Code](05_ScopePassingBug/ScopeLifecycle/ScopeLifecycle.kt) |

---

## 6. Cancellation Race

Cancelling without waiting for completion.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | CounterRefresh.kt | 12-13, 39-40, 66-67, 93-94, 120-121 | `job?.cancel()` | CounterManager, DataCounter, RefreshCounter, IncrementCounter, UpdateCounter all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/CounterRefresh/CounterRefresh.kt) |
| 2 | DataReload.kt | 12-13, 39-40, 66-67, 93-94, 120-121 | `job?.cancel()` | DataReloader, CacheReloader, ConfigReloader, SettingsReloader, StateReloader all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/DataReload/DataReload.kt) |
| 3 | ResourceCleanup.kt | 12-13, 39-40, 66-67, 93-94, 120-121 | `job?.cancel()` | ResourceHolder, ConnectionPool, FileHandler, MemoryManager, NetworkConnection all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/ResourceCleanup/ResourceCleanup.kt) |
| 4 | StateUpdate.kt | 12-13, 39-40, 66-67, 93-94, 120-121 | `job?.cancel()` | StateUpdater, StatusUpdater, FlagUpdater, CounterUpdater, PropertyUpdater all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/StateUpdate/StateUpdate.kt) |
| 5 | OperationCancel.kt | 12-13, 39-40, 66-67, 93-94, 120-121 | `job?.cancel()` | OperationExecutor, TaskExecutor, JobExecutor, WorkExecutor, ActionExecutor all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/OperationCancel/OperationCancel.kt) |
| 6 | JobRestart.kt | 11-12, 38-39, 65-66, 92-93, 119-120 | `job?.cancel()` | JobRestarter, TaskRestarter, ProcessRestarter, ServiceRestarter, WorkerRestarter all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/JobRestart/JobRestart.kt) |
| 7 | SharedState.kt | 12-13, 39-40, 66-67, 93-94, 120-121 | `job?.cancel()` | SharedStateManager, SharedDataHolder, SharedValueManager, SharedResourceHolder, SharedMemoryManager all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/SharedState/SharedState.kt) |
| 8 | AsyncOperation.kt | 12-13, 39-40, 66-67, 93-94, 120-121 | `job?.cancel()` | AsyncOperation, AsyncTask, AsyncJob, AsyncWork, AsyncAction all cancel Job and immediately start new Job, cancelled Job may still be running, creating race with new Job | [View Code](06_CancellationRace/AsyncOperation/AsyncOperation.kt) |

---

## 7. Channel Misuse

Unclosed channels, buffer configuration issues.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | UnclosedChannel.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Channel<Int>()` | MessageSender, DataProducer, EventPublisher, NotificationService, AlertSystem all create Channels without closing them, causing resource leaks | [View Code](07_ChannelMisuse/UnclosedChannel/UnclosedChannel.kt) |
| 2 | ChannelBuffer.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Channel<Int>(capacity)` | BufferedChannel, QueueChannel, StreamChannel, PipelineChannel, FlowChannel all create Channels with buffer configuration issues | [View Code](07_ChannelMisuse/ChannelBuffer/ChannelBuffer.kt) |
| 3 | ChannelOverflow.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Channel<Int>(capacity, onBufferOverflow)` | OverflowChannel, DropChannel, SuspendChannel, LatestChannel, BufferChannel all handle channel overflow incorrectly | [View Code](07_ChannelMisuse/ChannelOverflow/ChannelOverflow.kt) |
| 4 | ChannelLeak.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Channel<Int>()` | ChannelHolder, ChannelManager, ChannelPool, ChannelCache, ChannelStorage all create Channels without proper cleanup | [View Code](07_ChannelMisuse/ChannelLeak/ChannelLeak.kt) |
| 5 | ChannelRace.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `channel.send()`, `channel.receive()` | ConcurrentSender, ConcurrentReceiver, ConcurrentProducer, ConcurrentConsumer, ConcurrentProcessor all access channels concurrently without synchronization | [View Code](07_ChannelMisuse/ChannelRace/ChannelRace.kt) |
| 6 | ChannelCancel.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `channel.cancel()` | ChannelCanceller, ChannelCloser, ChannelTerminator, ChannelCleaner, ChannelDisposer all cancel channels without waiting for completion | [View Code](07_ChannelMisuse/ChannelCancel/ChannelCancel.kt) |
| 7 | ChannelException.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `channel.send()`, `channel.receive()` | ExceptionSender, ExceptionReceiver, ExceptionProducer, ExceptionConsumer, ExceptionProcessor all handle channel exceptions incorrectly | [View Code](07_ChannelMisuse/ChannelException/ChannelException.kt) |
| 8 | ChannelClose.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `channel.close()` | ChannelCloser, ChannelTerminator, ChannelFinalizer, ChannelShutdown, ChannelEnd all close channels without proper synchronization | [View Code](07_ChannelMisuse/ChannelClose/ChannelClose.kt) |
| 9 | ChannelSend.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `channel.send()` | SendChannel, ProducerChannel, PublisherChannel, EmitterChannel, OutputChannel all send to channels without handling suspension | [View Code](07_ChannelMisuse/ChannelSend/ChannelSend.kt) |
| 10 | ChannelReceive.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `channel.receive()` | ReceiveChannel, ConsumerChannel, SubscriberChannel, ListenerChannel, InputChannel all receive from channels without handling suspension | [View Code](07_ChannelMisuse/ChannelReceive/ChannelReceive.kt) |

---

## 8. Blocking Misuse

Blocking calls in coroutines blocking threads.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | BlockingIOMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `FileInputStream`, `FileOutputStream` | FileReader, FileWriter, FileProcessor, FileHandler, FileManager all use blocking I/O in coroutines, blocking dispatcher threads | [View Code](08_BlockingMisuse/BlockingIOMisuse/BlockingIOMisuse.kt) |
| 2 | ThreadSleepMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Thread.sleep()` | ThreadSleeper, ThreadPauser, ThreadWaiter, ThreadDelayer, ThreadBlocker all use Thread.sleep() in coroutines, blocking dispatcher threads | [View Code](08_BlockingMisuse/ThreadSleepMisuse/ThreadSleepMisuse.kt) |
| 3 | NetworkBlockingMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Socket`, `ServerSocket` | NetworkConnector, NetworkListener, NetworkAcceptor, NetworkReceiver, NetworkSender all use blocking network operations in coroutines | [View Code](08_BlockingMisuse/NetworkBlockingMisuse/NetworkBlockingMisuse.kt) |
| 4 | DatabaseBlockingMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Connection`, `Statement` | DatabaseConnector, DatabaseQuery, DatabaseUpdater, DatabaseInserter, DatabaseDeleter all use blocking database operations in coroutines | [View Code](08_BlockingMisuse/DatabaseBlockingMisuse/DatabaseBlockingMisuse.kt) |
| 5 | SynchronizedMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `synchronized()` | SynchronizedBlock, SynchronizedMethod, SynchronizedObject, SynchronizedClass, SynchronizedFunction all use synchronized blocks in coroutines, blocking dispatcher threads | [View Code](08_BlockingMisuse/SynchronizedMisuse/SynchronizedMisuse.kt) |
| 6 | LockMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `ReentrantLock.lock()` | LockHolder, LockAcquirer, LockUser, LockManager, LockController all use blocking locks in coroutines, blocking dispatcher threads | [View Code](08_BlockingMisuse/LockMisuse/LockMisuse.kt) |
| 7 | WaitNotifyMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `wait()`, `notify()` | WaitNotifier, WaitSignaler, WaitCommunicator, WaitCoordinator, WaitSynchronizer all use wait/notify in coroutines, blocking dispatcher threads | [View Code](08_BlockingMisuse/WaitNotifyMisuse/WaitNotifyMisuse.kt) |
| 8 | BlockingQueueMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `BlockingQueue` | QueueProducer, QueueConsumer, QueueProcessor, QueueHandler, QueueManager all use blocking queue operations in coroutines | [View Code](08_BlockingMisuse/BlockingQueueMisuse/BlockingQueueMisuse.kt) |
| 9 | FutureBlockingMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `Future.get()` | FutureGetter, FutureRetriever, FutureFetcher, FutureObtainer, FutureAcquirer all use blocking Future.get() in coroutines | [View Code](08_BlockingMisuse/FutureBlockingMisuse/FutureBlockingMisuse.kt) |
| 10 | CountDownLatchMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `CountDownLatch.await()` | LatchAwaiter, LatchWaiter, LatchBlocker, LatchSynchronizer, LatchCoordinator all use blocking CountDownLatch.await() in coroutines | [View Code](08_BlockingMisuse/CountDownLatchMisuse/CountDownLatchMisuse.kt) |

---

## 9. StateFlow/SharedFlow Race

Non-atomic read-modify-write operations.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | StateFlowRace.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `MutableStateFlow<T>.value` | StateUpdater, StateModifier, StateChanger, StateEditor, StateMutator all read and modify StateFlow values non-atomically | [View Code](09_StateFlowSharedFlowRace/StateFlowRace/StateFlowRace.kt) |
| 2 | SharedFlowRace.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `SharedFlow.emit()` | FlowEmitter, FlowPublisher, FlowSender, FlowBroadcaster, FlowDistributor all emit to SharedFlow concurrently without synchronization | [View Code](09_StateFlowSharedFlowRace/SharedFlowRace/SharedFlowRace.kt) |
| 3 | StateFlowUpdate.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `MutableStateFlow<T>.value` | StateIncrementer, StateDecrementer, StateMultiplier, StateDivider, StateModifier all update StateFlow values non-atomically | [View Code](09_StateFlowSharedFlowRace/StateFlowUpdate/StateFlowUpdate.kt) |
| 4 | SharedFlowCollect.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `SharedFlow.collect()` | FlowCollector, FlowSubscriber, FlowListener, FlowObserver, FlowReceiver all collect from SharedFlow concurrently without synchronization | [View Code](09_StateFlowSharedFlowRace/SharedFlowCollect/SharedFlowCollect.kt) |
| 5 | StateFlowCollect.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `StateFlow.collect()` | StateCollector, StateSubscriber, StateListener, StateObserver, StateReceiver all collect from StateFlow concurrently without synchronization | [View Code](09_StateFlowSharedFlowRace/StateFlowCollect/StateFlowCollect.kt) |
| 6 | SharedFlowBuffer.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `SharedFlow(replay, extraBufferCapacity)` | FlowBuffer, FlowCache, FlowStorage, FlowQueue, FlowHolder all configure SharedFlow buffer incorrectly | [View Code](09_StateFlowSharedFlowRace/SharedFlowBuffer/SharedFlowBuffer.kt) |
| 7 | StateFlowDistinct.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `StateFlow.distinctUntilChanged()` | StateDistinct, StateUnique, StateFilter, StateDedup, StateCleaner all use distinctUntilChanged incorrectly | [View Code](09_StateFlowSharedFlowRace/StateFlowDistinct/StateFlowDistinct.kt) |
| 8 | SharedFlowEmit.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `SharedFlow.emit()` | FlowEmitter, FlowPublisher, FlowSender, FlowBroadcaster, FlowDistributor all emit to SharedFlow without handling suspension | [View Code](09_StateFlowSharedFlowRace/SharedFlowEmit/SharedFlowEmit.kt) |
| 9 | StateFlowValue.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `StateFlow.value` | StateValueGetter, StateValueReader, StateValueAccessor, StateValueRetriever, StateValueFetcher all read StateFlow values non-atomically | [View Code](09_StateFlowSharedFlowRace/StateFlowValue/StateFlowValue.kt) |
| 10 | SharedFlowSubscription.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `SharedFlow.collect()` | FlowSubscriber, FlowListener, FlowObserver, FlowReceiver, FlowConsumer all subscribe to SharedFlow without proper lifecycle management | [View Code](09_StateFlowSharedFlowRace/SharedFlowSubscription/SharedFlowSubscription.kt) |

---

## 10. Exception Propagation Silent Cancellation

Silent cancellation of sibling coroutines.

### Projects

| # | Project | Error Lines | Variables | Error Description | Link |
|---|---------|-------------|-----------|-------------------|------|
| 1 | SwallowedCancellationException.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `try { } catch (CancellationException) { }` | ExceptionSwallower, ExceptionHider, ExceptionSuppressor, ExceptionConcealer, ExceptionMasker all catch CancellationException without rethrowing, causing silent cancellation | [View Code](10_ExceptionPropagationSilentCancellation/SwallowedCancellationException/SwallowedCancellationException.kt) |
| 2 | SilentCancellation.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `try { } catch (CancellationException) { }` | SilentCanceller, QuietCanceller, HiddenCanceller, InvisibleCanceller, UnnoticedCanceller all silently cancel sibling coroutines | [View Code](10_ExceptionPropagationSilentCancellation/SilentCancellation/SilentCancellation.kt) |
| 3 | ExceptionSilentCancellation.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `try { } catch (CancellationException) { }` | ExceptionSilentCanceller, ExceptionQuietCanceller, ExceptionHiddenCanceller, ExceptionInvisibleCanceller, ExceptionUnnoticedCanceller all silently cancel on exception | [View Code](10_ExceptionPropagationSilentCancellation/ExceptionSilentCancellation/ExceptionSilentCancellation.kt) |
| 4 | CancellationSwallowedException.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `try { } catch (CancellationException) { }` | CancellationSwallower, CancellationHider, CancellationSuppressor, CancellationConcealer, CancellationMasker all swallow cancellation exceptions | [View Code](10_ExceptionPropagationSilentCancellation/CancellationSwallowedException/CancellationSwallowedException.kt) |
| 5 | SwallowedExceptionCancellation.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `try { } catch (CancellationException) { }` | SwallowedExceptionCanceller, HiddenExceptionCanceller, SuppressedExceptionCanceller, ConcealedExceptionCanceller, MaskedExceptionCanceller all cancel on swallowed exceptions | [View Code](10_ExceptionPropagationSilentCancellation/SwallowedExceptionCancellation/SwallowedExceptionCancellation.kt) |
| 6 | SupervisorJobMisuse.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `SupervisorJob()` | SupervisorJobUser, SupervisorJobManager, SupervisorJobHandler, SupervisorJobController, SupervisorJobSupervisor all use SupervisorJob incorrectly | [View Code](10_ExceptionPropagationSilentCancellation/SupervisorJobMisuse/SupervisorJobMisuse.kt) |
| 7 | CoroutineExceptionHandler.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `CoroutineExceptionHandler` | ExceptionHandler, ExceptionCatcher, ExceptionInterceptor, ExceptionProcessor, ExceptionManager all handle exceptions incorrectly | [View Code](10_ExceptionPropagationSilentCancellation/CoroutineExceptionHandler/CoroutineExceptionHandler.kt) |
| 8 | ExceptionPropagation.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `try { } catch (Exception) { }` | ExceptionPropagator, ExceptionTransmitter, ExceptionForwarder, ExceptionCarrier, ExceptionTransporter all propagate exceptions incorrectly | [View Code](10_ExceptionPropagationSilentCancellation/ExceptionPropagation/ExceptionPropagation.kt) |
| 9 | CancellationException.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `CancellationException` | CancellationExceptionUser, CancellationExceptionHandler, CancellationExceptionCatcher, CancellationExceptionInterceptor, CancellationExceptionProcessor all handle CancellationException incorrectly | [View Code](10_ExceptionPropagationSilentCancellation/CancellationException/CancellationException.kt) |
| 10 | SilentExceptionCancellation.kt | 12-13, 40-41, 67-68, 94-95, 121-122 | `try { } catch (CancellationException) { }` | SilentExceptionCanceller, QuietExceptionCanceller, HiddenExceptionCanceller, InvisibleExceptionCanceller, UnnoticedExceptionCanceller all silently cancel on exceptions | [View Code](10_ExceptionPropagationSilentCancellation/SilentExceptionCancellation/SilentExceptionCancellation.kt) |

---

## Summary

This benchmark suite contains **100 projects** across **10 categories** of Kotlin concurrency bugs:

1. **Data Race / Suspension Race** - 10 projects
2. **Atomicity Violation** - 10 projects
3. **Order Violation** - 10 projects
4. **Deadlock** - 10 projects
5. **Scope Passing Bug** - 10 projects
6. **Cancellation Race** - 8 projects
7. **Channel Misuse** - 10 projects
8. **Blocking Misuse** - 10 projects
9. **StateFlow/SharedFlow Race** - 10 projects
10. **Exception Propagation Silent Cancellation** - 10 projects

Each project demonstrates specific concurrency bugs that can occur in Kotlin coroutines and serves as a valuable resource for learning, testing, and improving static analysis tools.