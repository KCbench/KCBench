import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class Account(
    val accountId: String,
    var balance: Double,
    val mutex: Mutex = Mutex()
)

class BankSystem {
    private val accounts = mutableMapOf<String, Account>()
    
    init {
        initializeAccounts()
    }
    
    private fun initializeAccounts() {
        val accountConfigs = listOf(
            Pair("ACC001", 10000.0),
            Pair("ACC002", 15000.0),
            Pair("ACC003", 8000.0),
            Pair("ACC004", 12000.0),
            Pair("ACC005", 20000.0),
            Pair("ACC006", 9000.0),
            Pair("ACC007", 11000.0),
            Pair("ACC008", 14000.0),
            Pair("ACC009", 7000.0),
            Pair("ACC010", 18000.0)
        )
        
        accountConfigs.forEach { (accountId, balance) ->
            accounts[accountId] = Account(accountId, balance)
        }
    }
    
    suspend fun transfer(
        fromAccountId: String,
        toAccountId: String,
        amount: Double
    ): Boolean {
        val fromAccount = accounts[fromAccountId]
        val toAccount = accounts[toAccountId]
        
        if (fromAccount == null || toAccount == null) {
            return false
        }
        
        if (fromAccount.balance < amount) {
            return false
        }
        
        fromAccount.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            toAccount.mutex.withLock {
                delay(Random.nextLong(10, 30))
                
                fromAccount.balance -= amount
                toAccount.balance += amount
                
                delay(Random.nextLong(5, 15))
            }
        }
        
        return true
    }
    
    suspend fun getBalance(accountId: String): Double? {
        val account = accounts[accountId] ?: return null
        
        return account.mutex.withLock {
            delay(Random.nextLong(5, 15))
            account.balance
        }
    }
    
    suspend fun deposit(accountId: String, amount: Double): Boolean {
        val account = accounts[accountId] ?: return false
        
        return account.mutex.withLock {
            delay(Random.nextLong(10, 30))
            account.balance += amount
            delay(Random.nextLong(5, 15))
            true
        }
    }
    
    suspend fun withdraw(accountId: String, amount: Double): Boolean {
        val account = accounts[accountId] ?: return false
        
        return account.mutex.withLock {
            delay(Random.nextLong(10, 30))
            
            if (account.balance >= amount) {
                account.balance -= amount
                delay(Random.nextLong(5, 15))
                true
            } else {
                false
            }
        }
    }
    
    fun getAllAccounts() = accounts.values.toList()
}

suspend fun simulateRandomTransfer(
    bankSystem: BankSystem,
    transferId: Int
) {
    val accounts = bankSystem.getAllAccounts()
    
    repeat(8) { attempt ->
        val fromAccount = accounts.random()
        val toAccount = accounts.filter { it.accountId != fromAccount.accountId }.random()
        
        val amount = Random.nextDouble(100.0, 1000.0)
        
        val success = bankSystem.transfer(
            fromAccount.accountId,
            toAccount.accountId,
            amount
        )
        
        if (success) {
            println("Transfer $transferId: $amount from ${fromAccount.accountId} to ${toAccount.accountId}")
        } else {
            println("Transfer $transferId failed")
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateBidirectionalTransfer(
    bankSystem: BankSystem,
    transferId: Int
) {
    val accounts = bankSystem.getAllAccounts()
    
    repeat(6) { attempt ->
        val account1 = accounts.random()
        val account2 = accounts.filter { it.accountId != account1.accountId }.random()
        
        val amount1 = Random.nextDouble(100.0, 500.0)
        val amount2 = Random.nextDouble(100.0, 500.0)
        
        coroutineScope {
            val job1 = launch {
                bankSystem.transfer(account1.accountId, account2.accountId, amount1)
            }
            
            val job2 = launch {
                bankSystem.transfer(account2.accountId, account1.accountId, amount2)
            }
            
            job1.join()
            job2.join()
        }
        
        delay(Random.nextLong(100, 200))
    }
}

suspend fun simulateDepositWithdraw(
    bankSystem: BankSystem,
    accountId: Int
) {
    val accounts = bankSystem.getAllAccounts()
    
    repeat(10) { attempt ->
        val account = accounts.random()
        
        val operation = Random.nextInt(0, 2)
        
        if (operation == 0) {
            val amount = Random.nextDouble(100.0, 1000.0)
            bankSystem.deposit(account.accountId, amount)
            println("Deposit: $amount to ${account.accountId}")
        } else {
            val amount = Random.nextDouble(100.0, 500.0)
            val success = bankSystem.withdraw(account.accountId, amount)
            if (success) {
                println("Withdraw: $amount from ${account.accountId}")
            } else {
                println("Withdraw failed from ${account.accountId}")
            }
        }
        
        delay(Random.nextLong(50, 100))
    }
}

suspend fun simulateBalanceCheck(
    bankSystem: BankSystem,
    checkId: Int
) {
    val accounts = bankSystem.getAllAccounts()
    
    repeat(12) { attempt ->
        val account = accounts.random()
        
        val balance = bankSystem.getBalance(account.accountId)
        
        if (balance != null) {
            println("Balance check $checkId: ${account.accountId} = $balance")
        }
        
        delay(Random.nextLong(30, 80))
    }
}

suspend fun simulateComplexTransfer(
    bankSystem: BankSystem,
    transferId: Int
) {
    val accounts = bankSystem.getAllAccounts()
    
    repeat(5) { attempt ->
        val account1 = accounts.random()
        val account2 = accounts.filter { it.accountId != account1.accountId }.random()
        val account3 = accounts.filter { 
            it.accountId != account1.accountId && it.accountId != account2.accountId 
        }.random()
        
        val amount1 = Random.nextDouble(100.0, 500.0)
        val amount2 = Random.nextDouble(100.0, 500.0)
        
        coroutineScope {
            val job1 = launch {
                bankSystem.transfer(account1.accountId, account2.accountId, amount1)
            }
            
            val job2 = launch {
                bankSystem.transfer(account2.accountId, account3.accountId, amount2)
            }
            
            val job3 = launch {
                bankSystem.transfer(account3.accountId, account1.accountId, amount1)
            }
            
            job1.join()
            job2.join()
            job3.join()
        }
        
        delay(Random.nextLong(200, 400))
    }
}

suspend fun monitorBankSystem(
    bankSystem: BankSystem,
    monitorId: Int
) {
    repeat(15) { attempt ->
        val accounts = bankSystem.getAllAccounts()
        val totalBalance = accounts.sumOf { account ->
            bankSystem.getBalance(account.accountId) ?: 0.0
        }
        
        println("Monitor $monitorId: Total balance = $totalBalance")
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val bankSystem = BankSystem()
    
    println("Starting Bank Transfer Simulation...")
    println("Initial Account Balances:")
    bankSystem.getAllAccounts().forEach { account ->
        println("  ${account.accountId}: $${account.balance}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateRandomTransfer(bankSystem, 1)
    })
    
    jobs.add(launch {
        simulateRandomTransfer(bankSystem, 2)
    })
    
    jobs.add(launch {
        simulateBidirectionalTransfer(bankSystem, 1)
    })
    
    jobs.add(launch {
        simulateBidirectionalTransfer(bankSystem, 2)
    })
    
    jobs.add(launch {
        simulateDepositWithdraw(bankSystem, 1)
    })
    
    jobs.add(launch {
        simulateDepositWithdraw(bankSystem, 2)
    })
    
    jobs.add(launch {
        simulateBalanceCheck(bankSystem, 1)
    })
    
    jobs.add(launch {
        simulateBalanceCheck(bankSystem, 2)
    })
    
    jobs.add(launch {
        simulateComplexTransfer(bankSystem, 1)
    })
    
    jobs.add(launch {
        monitorBankSystem(bankSystem, 1)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val accounts = bankSystem.getAllAccounts()
    val totalBalance = accounts.sumOf { account ->
        bankSystem.getBalance(account.accountId) ?: 0.0
    }
    
    println("\n=== Final Account Balances ===")
    accounts.forEach { account ->
        println("  ${account.accountId}: $${account.balance}")
    }
    
    println("\nTotal Balance: $${totalBalance}")
    println("Expected Total Balance: $${accounts.sumOf { 10000.0 + Random.nextDouble(0.0, 10000.0) }}")
    
    println("\n⚠️  Deadlock Warning:")
    println("  The transfer() function locks accounts in different order,")
    println("  which can cause deadlock when bidirectional transfers occur.")
    println("  Fix: Always lock accounts in a consistent order (e.g., by account ID).")
}