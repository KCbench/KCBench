import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class DatabaseConnection(
    val connectionId: String,
    var inUse: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class DatabaseTransaction(
    val transactionId: String,
    var active: Boolean = false,
    var committed: Boolean = false,
    val mutex: Mutex = Mutex()
)

class DatabaseManager {
    private val connections = mutableMapOf<String, DatabaseConnection>()
    private val transactions = mutableMapOf<String, DatabaseTransaction>()
    private val connectionPoolMutex = Mutex()
    private val transactionMutex = Mutex()
    
    init {
        initializeConnections()
    }
    
    private fun initializeConnections() {
        val connectionConfigs = listOf(
            "CONN001", "CONN002", "CONN003", "CONN004", "CONN005",
            "CONN006", "CONN007", "CONN008", "CONN009", "CONN010"
        )
        
        connectionConfigs.forEach { connectionId ->
            connections[connectionId] = DatabaseConnection(
                connectionId = connectionId,
                inUse = false,
                locked = false
            )
        }
    }
    
    suspend fun acquireConnection(connectionId: String): Boolean {
        val connection = connections[connectionId] ?: return false
        
        if (connection.inUse) {
            return false
        }
        
        connectionPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (connection.inUse) {
                return false
            }
            
            connection.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                connection.inUse = true
                connection.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun releaseConnection(connectionId: String): Boolean {
        val connection = connections[connectionId] ?: return false
        
        if (!connection.inUse) {
            return false
        }
        
        connection.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            connectionPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                connection.inUse = false
                connection.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun beginTransaction(transactionId: String): Boolean {
        val transaction = transactions[transactionId]
        
        if (transaction != null && transaction.active) {
            return false
        }
        
        transactionMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (transaction != null && transaction.active) {
                return false
            }
            
            val newTransaction = DatabaseTransaction(
                transactionId = transactionId,
                active = true,
                committed = false
            )
            
            newTransaction.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                transactions[transactionId] = newTransaction
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun commitTransaction(transactionId: String): Boolean {
        val transaction = transactions[transactionId] ?: return false
        
        if (!transaction.active) {
            return false
        }
        
        transaction.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            transactionMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                transaction.active = false
                transaction.committed = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun executeQueryWithTransaction(
        connectionId: String,
        transactionId: String
    ): Boolean {
        val connection = connections[connectionId] ?: return false
        val transaction = transactions[transactionId] ?: return false
        
        if (!connection.inUse || !transaction.active) {
            return false
        }
        
        connection.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            transaction.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                delay(Random.nextLong(20, 50))
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferConnection(
        fromConnectionId: String,
        toConnectionId: String
    ): Boolean {
        val fromConnection = connections[fromConnectionId]
        val toConnection = connections[toConnectionId]
        
        if (fromConnection == null || toConnection == null) {
            return false
        }
        
        if (!fromConnection.inUse || toConnection.inUse) {
            return false
        }
        
        fromConnection.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toConnection.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromConnection.inUse = false
                fromConnection.locked = false
                toConnection.inUse = true
                toConnection.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getConnectionStatus(connectionId: String): DatabaseConnection? {
        val connection = connections[connectionId] ?: return null
        
        return connection.mutex.withLock {
            delay(Random.nextLong(5, 15))
            connection.copy()
        }
    }
    
    suspend fun getTransactionStatus(transactionId: String): DatabaseTransaction? {
        val transaction = transactions[transactionId] ?: return null
        
        return transaction.mutex.withLock {
            delay(Random.nextLong(5, 15))
            transaction.copy()
        }
    }
    
    fun getAllConnections() = connections.values.toList()
    fun getAllTransactions() = transactions.values.toList()
}

suspend fun simulateConnectionAcquisition(
    dbManager: DatabaseManager,
    clientId: Int
) {
    val connections = dbManager.getAllConnections()
    
    repeat(10) { attempt ->
        val connection = connections.filter { !it.inUse }.randomOrNull()
        
        if (connection != null) {
            val success = dbManager.acquireConnection(connection.connectionId)
            if (success) {
                println("Client $clientId: Acquired ${connection.connectionId}")
            } else {
                println("Client $clientId: Failed to acquire ${connection.connectionId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateConnectionRelease(
    dbManager: DatabaseManager,
    clientId: Int
) {
    val connections = dbManager.getAllConnections()
    
    repeat(10) { attempt ->
        val connection = connections.filter { it.inUse }.randomOrNull()
        
        if (connection != null) {
            val success = dbManager.releaseConnection(connection.connectionId)
            if (success) {
                println("Client $clientId: Released ${connection.connectionId}")
            } else {
                println("Client $clientId: Failed to release ${connection.connectionId}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateTransactionExecution(
    dbManager: DatabaseManager,
    transactionId: Int
) {
    val connections = dbManager.getAllConnections()
    
    repeat(8) { attempt ->
        val connection = connections.filter { it.inUse }.randomOrNull()
        
        if (connection != null) {
            val txId = "TXN${transactionId}_${attempt}"
            
            dbManager.beginTransaction(txId)
            delay(Random.nextLong(20, 50))
            
            dbManager.executeQueryWithTransaction(connection.connectionId, txId)
            delay(Random.nextLong(20, 50))
            
            dbManager.commitTransaction(txId)
            
            println("Transaction $txId: Executed on ${connection.connectionId}")
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateConnectionTransfer(
    dbManager: DatabaseManager,
    transferId: Int
) {
    val connections = dbManager.getAllConnections()
    
    repeat(6) { attempt ->
        val inUseConnections = connections.filter { it.inUse }
        val availableConnections = connections.filter { !it.inUse }
        
        if (inUseConnections.isNotEmpty() && availableConnections.isNotEmpty()) {
            val fromConnection = inUseConnections.random()
            val toConnection = availableConnections.random()
            
            val success = dbManager.transferConnection(
                fromConnection.connectionId,
                toConnection.connectionId
            )
            
            if (success) {
                println("Transfer $transferId: ${fromConnection.connectionId} -> ${toConnection.connectionId}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateBidirectionalConnectionTransfer(
    dbManager: DatabaseManager,
    transferId: Int
) {
    val connections = dbManager.getAllConnections()
    
    repeat(5) { attempt ->
        val connection1 = connections.random()
        val connection2 = connections.filter { it.connectionId != connection1.connectionId }.random()
        
        val job1 = launch {
            dbManager.transferConnection(connection1.connectionId, connection2.connectionId)
        }
        
        val job2 = launch {
            dbManager.transferConnection(connection2.connectionId, connection1.connectionId)
        }
        
        job1.join()
        job2.join()
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorDatabase(
    dbManager: DatabaseManager,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val connections = dbManager.getAllConnections()
        val transactions = dbManager.getAllTransactions()
        
        val inUse = connections.count { it.inUse }
        val locked = connections.count { it.locked }
        val activeTransactions = transactions.count { it.active }
        val committedTransactions = transactions.count { it.committed }
        
        println("Monitor $monitorId: InUse=$inUse, Locked=$locked, " +
                "ActiveTxn=$activeTransactions, CommittedTxn=$committedTransactions")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val dbManager = DatabaseManager()
    
    println("Starting Database Access Simulation...")
    println("Initial Connection Status:")
    dbManager.getAllConnections().forEach { connection ->
        println("  ${connection.connectionId}: InUse=${connection.inUse}, Locked=${connection.locked}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateConnectionAcquisition(dbManager, 1)
    })
    
    jobs.add(launch {
        simulateConnectionAcquisition(dbManager, 2)
    })
    
    jobs.add(launch {
        simulateConnectionRelease(dbManager, 1)
    })
    
    jobs.add(launch {
        simulateConnectionRelease(dbManager, 2)
    })
    
    jobs.add(launch {
        simulateTransactionExecution(dbManager, 1)
    })
    
    jobs.add(launch {
        simulateTransactionExecution(dbManager, 2)
    })
    
    jobs.add(launch {
        simulateConnectionTransfer(dbManager, 1)
    })
    
    jobs.add(launch {
        simulateBidirectionalConnectionTransfer(dbManager, 1)
    })
    
    jobs.add(launch {
        monitorDatabase(dbManager, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val connections = dbManager.getAllConnections()
    val transactions = dbManager.getAllTransactions()
    
    println("\n=== Final Connection Status ===")
    connections.forEach { connection ->
        println("  ${connection.connectionId}: InUse=${connection.inUse}, Locked=${connection.locked}")
    }
    
    val inUse = connections.count { it.inUse }
    val locked = connections.count { it.locked }
    val activeTransactions = transactions.count { it.active }
    val committedTransactions = transactions.count { it.committed }
    
    println("\nInUse: $inUse/${connections.size}")
    println("Locked: $locked/${connections.size}")
    println("Active Transactions: $activeTransactions")
    println("Committed Transactions: $committedTransactions")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - acquireConnection(): connectionPoolMutex -> connection.mutex")
    println("  - releaseConnection(): connection.mutex -> connectionPoolMutex")
    println("  - beginTransaction(): transactionMutex -> transaction.mutex")
    println("  - commitTransaction(): transaction.mutex -> transactionMutex")
    println("  - executeQueryWithTransaction(): connection.mutex -> transaction.mutex")
    println("  - transferConnection(): connection1.mutex -> connection2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}