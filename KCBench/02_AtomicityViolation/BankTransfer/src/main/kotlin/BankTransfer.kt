import kotlinx.coroutines.*
import kotlin.random.Random

data class Account(
    val accountNumber: String,
    var balance: Double,
    val owner: String
)

class BankTransferSystem {
    private val accounts = mutableMapOf<String, Account>()
    
    init {
        initializeAccounts()
    }
    
    private fun initializeAccounts() {
        val owners = listOf(
            "Alice", "Bob", "Charlie", "David", "Eve"
        )
        
        owners.forEach { owner ->
            accounts[owner] = Account(
                accountNumber = "ACC_${Random.nextInt(1000, 9999)}",
                balance = Random.nextDouble(5000.0, 10000.0),
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
        
        if (sender.balance >= amount) {
            delay(Random.nextLong(1, 10))
            
            sender.balance -= amount
            delay(Random.nextLong(1, 5))
            
            receiver.balance += amount
            return true
        }
        
        return false
    }
    
    suspend fun batchTransfer(transfers: List<Triple<String, String, Double>>): Int {
        var successful = 0
        
        transfers.forEach { (from, to, amount) ->
            if (transfer(from, to, amount)) {
                successful++
            }
        }
        
        return successful
    }
    
    fun getAccountBalance(accountNumber: String): Double? {
        return accounts[accountNumber]?.balance
    }
    
    fun getAllAccounts() = accounts.values.toList()
}

data class Triple<A, B, C>(
    val first: A,
    val second: B,
    val third: C
)

suspend fun simulateCustomerTransfers(
    bank: BankTransferSystem,
    customerName: String
) {
    repeat(10) { attempt ->
        val accounts = bank.getAllAccounts()
        val sender = accounts.random()
        val receiver = accounts.random()
        
        if (sender.accountNumber != receiver.accountNumber) {
            val amount = Random.nextDouble(100.0, 1000.0)
            bank.transfer(
                sender.accountNumber,
                receiver.accountNumber,
                amount
            )
        }
        
        delay(Random.nextLong(10, 50))
    }
}

suspend fun simulateLargeTransfers(
    bank: BankTransferSystem
) {
    repeat(5) { attempt ->
        val accounts = bank.getAllAccounts()
        val sender = accounts.random()
        val receiver = accounts.random()
        
        if (sender.accountNumber != receiver.accountNumber) {
            val amount = Random.nextDouble(2000.0, 4000.0)
            bank.transfer(
                sender.accountNumber,
                receiver.accountNumber,
                amount
            )
        }
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateConcurrentWithdrawals(
    bank: BankTransferSystem,
    accountName: String
) {
    repeat(8) { attempt ->
        val accounts = bank.getAllAccounts()
        val account = accounts.find { it.owner == accountName }
        
        if (account != null) {
            val amount = Random.nextDouble(500.0, 1500.0)
            bank.transfer(
                account.accountNumber,
                "SYSTEM",
                amount
            )
        }
        
        delay(Random.nextLong(20, 80))
    }
}

fun main() = runBlocking {
    val bank = BankTransferSystem()
    
    println("Starting Bank Transfer System Simulation...")
    println("Initial Account Balances:")
    bank.getAllAccounts().forEach { account ->
        println("  ${account.owner}: ${"%.2f".format(account.balance)}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val customers = bank.getAllAccounts().map { it.owner }
    
    customers.forEach { customer ->
        jobs.add(launch {
            simulateCustomerTransfers(bank, customer)
        })
    }
    
    jobs.add(launch {
        simulateLargeTransfers(bank)
    })
    
    delay(2000)
    
    val randomCustomer = customers.random()
    jobs.add(launch {
        simulateConcurrentWithdrawals(bank, randomCustomer)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Account Balances ===")
    bank.getAllAccounts().forEach { account ->
        println("  ${account.owner}: ${"%.2f".format(account.balance)}")
    }
    
    val negativeBalances = bank.getAllAccounts()
        .filter { it.balance < 0 }
    
    if (negativeBalances.isNotEmpty()) {
        println("\n⚠️  Negative Balances Detected:")
        negativeBalances.forEach { account ->
            println("  ${account.owner}: ${"%.2f".format(account.balance)}")
        }
    } else {
        println("\n✅ All accounts have positive balances")
    }
    
    val totalBalance = bank.getAllAccounts().sumOf { it.balance }
    println("\nTotal System Balance: ${"%.2f".format(totalBalance)}")
}