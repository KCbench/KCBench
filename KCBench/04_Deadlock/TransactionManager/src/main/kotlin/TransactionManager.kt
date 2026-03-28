import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class Transaction(
    val transactionId: String,
    val transactionName: String,
    var active: Boolean = false,
    var locked: Boolean = false,
    val mutex: Mutex = Mutex()
)

data class AccountLock(
    val accountId: String,
    val accountName: String,
    var locked: Boolean = false,
    var frozen: Boolean = false,
    val mutex: Mutex = Mutex()
)

class TransactionManager {
    private val transactions = mutableMapOf<String, Transaction>()
    private val accounts = mutableMapOf<String, AccountLock>()
    private val transactionPoolMutex = Mutex()
    private val accountPoolMutex = Mutex()
    
    init {
        initializeTransactions()
        initializeAccounts()
    }
    
    private fun initializeTransactions() {
        val transactionConfigs = listOf(
            Pair("TXN001", "DepositTransaction"),
            Pair("TXN002", "WithdrawTransaction"),
            Pair("TXN003", "TransferTransaction"),
            Pair("TXN004", "PaymentTransaction"),
            Pair("TXN005", "RefundTransaction"),
            Pair("TXN006", "PurchaseTransaction"),
            Pair("TXN007", "SaleTransaction"),
            Pair("TXN008", "ExchangeTransaction"),
            Pair("TXN009", "InvestmentTransaction"),
            Pair("TXN010", "DividendTransaction")
        )
        
        transactionConfigs.forEach { (transactionId, transactionName) ->
            transactions[transactionId] = Transaction(
                transactionId = transactionId,
                transactionName = transactionName,
                active = false,
                locked = false
            )
        }
    }
    
    private fun initializeAccounts() {
        val accountConfigs = listOf(
            Pair("ACC001", "SavingsAccount"),
            Pair("ACC002", "CheckingAccount"),
            Pair("ACC003", "InvestmentAccount"),
            Pair("ACC004", "RetirementAccount"),
            Pair("ACC005", "BusinessAccount"),
            Pair("ACC006", "PersonalAccount"),
            Pair("ACC007", "JointAccount"),
            Pair("ACC008", "TrustAccount"),
            Pair("ACC009", "CustodialAccount"),
            Pair("ACC010", "EstateAccount")
        )
        
        accountConfigs.forEach { (accountId, accountName) ->
            accounts[accountId] = AccountLock(
                accountId = accountId,
                accountName = accountName,
                locked = false,
                frozen = false
            )
        }
    }
    
    suspend fun beginTransaction(transactionId: String): Boolean {
        val transaction = transactions[transactionId] ?: return false
        
        if (transaction.active) {
            return false
        }
        
        transactionPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (transaction.active) {
                return false
            }
            
            transaction.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                transaction.active = true
                transaction.locked = true
                
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
            
            transactionPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                transaction.active = false
                transaction.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun lockAccount(accountId: String): Boolean {
        val account = accounts[accountId] ?: return false
        
        if (account.locked) {
            return false
        }
        
        accountPoolMutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (account.locked) {
                return false
            }
            
            account.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                account.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun unlockAccount(accountId: String): Boolean {
        val account = accounts[accountId] ?: return false
        
        if (!account.locked) {
            return false
        }
        
        account.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            accountPoolMutex.withLock {
                delay(Random.nextLong(10, 30))
                
                account.locked = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun executeTransactionWithAccount(
        transactionId: String,
        accountId: String
    ): Boolean {
        val transaction = transactions[transactionId] ?: return false
        val account = accounts[accountId] ?: return false
        
        if (!transaction.active || !account.locked) {
            return false
        }
        
        transaction.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            account.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                account.frozen = true
                delay(Random.nextLong(20, 50))
                account.frozen = false
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun transferTransaction(
        fromTransactionId: String,
        toTransactionId: String
    ): Boolean {
        val fromTransaction = transactions[fromTransactionId]
        val toTransaction = transactions[toTransactionId]
        
        if (fromTransaction == null || toTransaction == null) {
            return false
        }
        
        if (!fromTransaction.active || toTransaction.active) {
            return false
        }
        
        fromTransaction.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toTransaction.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromTransaction.active = false
                fromTransaction.locked = false
                toTransaction.active = true
                toTransaction.locked = true
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun swapTransactions(
        transactionId1: String,
        transactionId2: String
    ): Boolean {
        val transaction1 = transactions[transactionId1]
        val transaction2 = transactions[transactionId2]
        
        if (transaction1 == null || transaction2 == null) {
            return false
        }
        
        if (!transaction1.active || !transaction2.active) {
            return false
        }
        
        transaction1.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            transaction2.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                val tempActive = transaction1.active
                val tempLocked = transaction1.locked
                
                transaction1.active = transaction2.active
                transaction1.locked = transaction2.locked
                transaction2.active = tempActive
                transaction2.locked = tempLocked
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getTransactionStatus(transactionId: String): Transaction? {
        val transaction = transactions[transactionId] ?: return null
        
        return transaction.mutex.withLock {
            delay(Random.nextLong(5, 15))
            transaction.copy()
        }
    }
    
    suspend fun getAccountStatus(accountId: String): AccountLock? {
        val account = accounts[accountId] ?: return null
        
        return account.mutex.withLock {
            delay(Random.nextLong(5, 15))
            account.copy()
        }
    }
    
    fun getAllTransactions() = transactions.values.toList()
    fun getAllAccounts() = accounts.values.toList()
}

suspend fun simulateTransactionBeginning(
    transactionManager: TransactionManager,
    managerId: Int
) {
    val transactions = transactionManager.getAllTransactions()
    
    repeat(10) { attempt ->
        val transaction = transactions.filter { !it.active }.randomOrNull()
        
        if (transaction != null) {
            val success = transactionManager.beginTransaction(transaction.transactionId)
            if (success) {
                println("Manager $managerId: Began ${transaction.transactionName}")
            } else {
                println("Manager $managerId: Failed to begin ${transaction.transactionName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateTransactionCommitting(
    transactionManager: TransactionManager,
    managerId: Int
) {
    val transactions = transactionManager.getAllTransactions()
    
    repeat(10) { attempt ->
        val transaction = transactions.filter { it.active }.randomOrNull()
        
        if (transaction != null) {
            val success = transactionManager.commitTransaction(transaction.transactionId)
            if (success) {
                println("Manager $managerId: Committed ${transaction.transactionName}")
            } else {
                println("Manager $managerId: Failed to commit ${transaction.transactionName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateAccountLocking(
    transactionManager: TransactionManager,
    managerId: Int
) {
    val accounts = transactionManager.getAllAccounts()
    
    repeat(8) { attempt ->
        val account = accounts.filter { !it.locked }.randomOrNull()
        
        if (account != null) {
            val success = transactionManager.lockAccount(account.accountId)
            if (success) {
                println("Manager $managerId: Locked ${account.accountName}")
            } else {
                println("Manager $managerId: Failed to lock ${account.accountName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateAccountUnlocking(
    transactionManager: TransactionManager,
    managerId: Int
) {
    val accounts = transactionManager.getAllAccounts()
    
    repeat(8) { attempt ->
        val account = accounts.filter { it.locked }.randomOrNull()
        
        if (account != null) {
            val success = transactionManager.unlockAccount(account.accountId)
            if (success) {
                println("Manager $managerId: Unlocked ${account.accountName}")
            } else {
                println("Manager $managerId: Failed to unlock ${account.accountName}")
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateTransactionExecution(
    transactionManager: TransactionManager,
    executorId: Int
) {
    val transactions = transactionManager.getAllTransactions()
    val accounts = transactionManager.getAllAccounts()
    
    repeat(10) { attempt ->
        val transaction = transactions.filter { it.active }.randomOrNull()
        val account = accounts.filter { it.locked }.randomOrNull()
        
        if (transaction != null && account != null) {
            val success = transactionManager.executeTransactionWithAccount(
                transaction.transactionId,
                account.accountId
            )
            
            if (success) {
                println("Executor $executorId: Executed ${transaction.transactionName} with ${account.accountName}")
            } else {
                println("Executor $executorId: Failed to execute transaction")
            }
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateTransactionTransfer(
    transactionManager: TransactionManager,
    transferId: Int
) {
    val transactions = transactionManager.getAllTransactions()
    
    repeat(6) { attempt ->
        val activeTransactions = transactions.filter { it.active }
        val inactiveTransactions = transactions.filter { !it.active }
        
        if (activeTransactions.isNotEmpty() && inactiveTransactions.isNotEmpty()) {
            val fromTransaction = activeTransactions.random()
            val toTransaction = inactiveTransactions.random()
            
            val success = transactionManager.transferTransaction(
                fromTransaction.transactionId,
                toTransaction.transactionId
            )
            
            if (success) {
                println("Transfer $transferId: ${fromTransaction.transactionName} -> ${toTransaction.transactionName}")
            } else {
                println("Transfer $transferId failed")
            }
        }
        
        delay(Random.nextLong(150, 300))
    }
}

suspend fun simulateTransactionSwap(
    transactionManager: TransactionManager,
    swapId: Int
) {
    val transactions = transactionManager.getAllTransactions()
    
    repeat(5) { attempt ->
        val activeTransactions = transactions.filter { it.active }
        
        if (activeTransactions.size >= 2) {
            val transaction1 = activeTransactions.random()
            val transaction2 = activeTransactions.filter { it.transactionId != transaction1.transactionId }.random()
            
            val success = transactionManager.swapTransactions(
                transaction1.transactionId,
                transaction2.transactionId
            )
            
            if (success) {
                println("Swap $swapId: ${transaction1.transactionName} <-> ${transaction2.transactionName}")
            } else {
                println("Swap $swapId failed")
            }
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorTransactionManager(
    transactionManager: TransactionManager,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val transactions = transactionManager.getAllTransactions()
        val accounts = transactionManager.getAllAccounts()
        
        val active = transactions.count { it.active }
        val locked = transactions.count { it.locked }
        val lockedAccounts = accounts.count { it.locked }
        val frozenAccounts = accounts.count { it.frozen }
        
        println("Monitor $monitorId: Active=$active, Locked=$locked, " +
                "LockedAccounts=$lockedAccounts, FrozenAccounts=$frozenAccounts")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val transactionManager = TransactionManager()
    
    println("Starting Transaction Manager Simulation...")
    println("Initial Transaction Status:")
    transactionManager.getAllTransactions().forEach { transaction ->
        println("  ${transaction.transactionId} (${transaction.transactionName}): " +
                "Active=${transaction.active}, Locked=${transaction.locked}")
    }
    println()
    
    println("Initial Account Status:")
    transactionManager.getAllAccounts().forEach { account ->
        println("  ${account.accountId} (${account.accountName}): " +
                "Locked=${account.locked}, Frozen=${account.frozen}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateTransactionBeginning(transactionManager, 1)
    })
    
    jobs.add(launch {
        simulateTransactionBeginning(transactionManager, 2)
    })
    
    jobs.add(launch {
        simulateTransactionCommitting(transactionManager, 1)
    })
    
    jobs.add(launch {
        simulateTransactionCommitting(transactionManager, 2)
    })
    
    jobs.add(launch {
        simulateAccountLocking(transactionManager, 1)
    })
    
    jobs.add(launch {
        simulateAccountUnlocking(transactionManager, 1)
    })
    
    jobs.add(launch {
        simulateTransactionExecution(transactionManager, 1)
    })
    
    jobs.add(launch {
        simulateTransactionTransfer(transactionManager, 1)
    })
    
    jobs.add(launch {
        simulateTransactionSwap(transactionManager, 1)
    })
    
    jobs.add(launch {
        monitorTransactionManager(transactionManager, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val transactions = transactionManager.getAllTransactions()
    val accounts = transactionManager.getAllAccounts()
    
    println("\n=== Final Transaction Status ===")
    transactions.forEach { transaction ->
        println("  ${transaction.transactionId} (${transaction.transactionName}): " +
                "Active=${transaction.active}, Locked=${transaction.locked}")
    }
    
    println("\n=== Final Account Status ===")
    accounts.forEach { account ->
        println("  ${account.accountId} (${account.accountName}): " +
                "Locked=${account.locked}, Frozen=${account.frozen}")
    }
    
    val active = transactions.count { it.active }
    val locked = transactions.count { it.locked }
    val lockedAccounts = accounts.count { it.locked }
    val frozenAccounts = accounts.count { it.frozen }
    
    println("\nActive Transactions: $active/${transactions.size}")
    println("Locked Transactions: $locked/${transactions.size}")
    println("Locked Accounts: $lockedAccounts/${accounts.size}")
    println("Frozen Accounts: $frozenAccounts/${accounts.size}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  Multiple functions lock resources in different order:")
    println("  - beginTransaction(): transactionPoolMutex -> transaction.mutex")
    println("  - commitTransaction(): transaction.mutex -> transactionPoolMutex")
    println("  - lockAccount(): accountPoolMutex -> account.mutex")
    println("  - unlockAccount(): account.mutex -> accountPoolMutex")
    println("  - executeTransactionWithAccount(): transaction.mutex -> account.mutex")
    println("  - transferTransaction(): transaction1.mutex -> transaction2.mutex")
    println("  - swapTransactions(): transaction1.mutex -> transaction2.mutex")
    println("  Fix: Always lock resources in a consistent order.")
}