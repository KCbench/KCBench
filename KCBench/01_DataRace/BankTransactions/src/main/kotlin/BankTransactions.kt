import kotlinx.coroutines.*
import kotlin.random.Random

data class Account(
    val accountNumber: String,
    var balance: Double,
    val owner: String
)

class BankSystem {
    private val accounts = mutableMapOf<String, Account>()
    private val transactionCount = 0
    
    init {
        initializeAccounts()
    }
    
    private fun initializeAccounts() {
        val owners = listOf(
            "Alice", "Bob", "Charlie", "David", "Eve",
            "Frank", "Grace", "Henry", "Ivy", "Jack"
        )
        
        owners.forEachIndexed { index, owner ->
            accounts["ACC${1000 + index}"] = Account(
                accountNumber = "ACC${1000 + index}",
                balance = Random.nextDouble(1000.0, 10000.0),
                owner = owner
            )
        }
    }
    
    suspend fun transfer(
        fromAccount: String,
        toAccount: String,
        amount: Double
    ): Boolean {
        val sender = accounts[fromAccount] ?: return false
        val receiver = accounts[toAccount] ?: return false
        
        val senderBalance = sender.balance
        delay(Random.nextLong(1, 10))
        
        if (senderBalance >= amount) {
            val receiverBalance = receiver.balance
            delay(Random.nextLong(1, 10))
            
            sender.balance = senderBalance - amount
            delay(Random.nextLong(1, 5))
            
            receiver.balance = receiverBalance + amount
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun deposit(accountNumber: String, amount: Double): Boolean {
        val account = accounts[accountNumber] ?: return false
        
        val currentBalance = account.balance
        delay(Random.nextLong(1, 10))
        
        account.balance = currentBalance + amount
        delay(Random.nextLong(1, 5))
        
        return true
    }
    
    suspend fun withdraw(accountNumber: String, amount: Double): Boolean {
        val account = accounts[accountNumber] ?: return false
        
        val currentBalance = account.balance
        delay(Random.nextLong(1, 10))
        
        if (currentBalance >= amount) {
            account.balance = currentBalance - amount
            delay(Random.nextLong(1, 5))
            return true
        }
        
        return false
    }
    
    suspend fun applyInterest(accountNumber: String, rate: Double) {
        val account = accounts[accountNumber] ?: return
        
        val currentBalance = account.balance
        delay(Random.nextLong(1, 10))
        
        val interest = currentBalance * rate
        delay(Random.nextLong(1, 5))
        
        account.balance = currentBalance + interest
    }
    
    fun getAccountBalance(accountNumber: String): Double? {
        return accounts[accountNumber]?.balance
    }
    
    fun getAllAccounts() = accounts.values.toList()
    
    fun getTotalBalance(): Double {
        return accounts.values.sumOf { it.balance }
    }
}

class TransactionProcessor(
    private val bank: BankSystem
) {
    suspend fun processRandomTransaction(): Boolean {
        val accounts = bank.getAllAccounts()
        if (accounts.size < 2) return false
        
        val fromAccount = accounts.random().accountNumber
        val toAccount = accounts.random().accountNumber
        
        if (fromAccount == toAccount) return false
        
        val amount = Random.nextDouble(10.0, 500.0)
        
        return bank.transfer(fromAccount, toAccount, amount)
    }
    
    suspend fun processDeposit(accountNumber: String): Boolean {
        val amount = Random.nextDouble(100.0, 1000.0)
        return bank.deposit(accountNumber, amount)
    }
    
    suspend fun processWithdraw(accountNumber: String): Boolean {
        val amount = Random.nextDouble(50.0, 500.0)
        return bank.withdraw(accountNumber, amount)
    }
}

suspend fun simulateCustomerTransactions(
    bank: BankSystem,
    processor: TransactionProcessor,
    customerId: Int
) {
    repeat(20) { attempt ->
        when (Random.nextInt(3)) {
            0 -> processor.processRandomTransaction()
            1 -> {
                val accounts = bank.getAllAccounts()
                if (accounts.isNotEmpty()) {
                    processor.processDeposit(accounts.random().accountNumber)
                }
            }
            2 -> {
                val accounts = bank.getAllAccounts()
                if (accounts.isNotEmpty()) {
                    processor.processWithdraw(accounts.random().accountNumber)
                }
            }
        }
        delay(Random.nextLong(1, 50))
    }
}

suspend fun simulateInterestApplication(
    bank: BankSystem
) {
    repeat(10) { attempt ->
        val accounts = bank.getAllAccounts()
        if (accounts.isNotEmpty()) {
            val account = accounts.random()
            bank.applyInterest(account.accountNumber, 0.01)
        }
        delay(Random.nextLong(100, 200))
    }
}

fun main() = runBlocking {
    val bank = BankSystem()
    val processor = TransactionProcessor(bank)
    
    println("Starting Bank Transaction Simulation...")
    println("Initial Total Balance: ${bank.getTotalBalance()}")
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateInterestApplication(bank)
    })
    
    repeat(15) { customerId ->
        jobs.add(launch {
            simulateCustomerTransactions(bank, processor, customerId)
        })
    }
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Account Balances ===")
    bank.getAllAccounts().forEach { account ->
        println("  ${account.accountNumber} (${account.owner}): ${account.balance}")
    }
    
    val finalTotal = bank.getTotalBalance()
    val initialTotal = bank.getAllAccounts().sumOf { 
        1000.0 + Random.nextDouble(1000.0, 10000.0) 
    }
    
    println("\n=== Final Statistics ===")
    println("Final Total Balance: $finalTotal")
    println("Expected balance should be close to initial due to transfers")
    
    val negativeBalances = bank.getAllAccounts().filter { it.balance < 0 }
    if (negativeBalances.isNotEmpty()) {
        println("\n⚠️  Negative balances detected:")
        negativeBalances.forEach { account ->
            println("  ${account.accountNumber}: ${account.balance}")
        }
    } else {
        println("\n✅ No negative balances")
    }
}