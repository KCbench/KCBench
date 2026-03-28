import kotlinx.coroutines.*
import kotlin.random.Random

data class UserAccount(
    val userId: String,
    var balance: Double,
    var creditLimit: Double,
    var isFrozen: Boolean = false
)

class AccountManagementSystem {
    private val accounts = mutableMapOf<String, UserAccount>()
    
    init {
        initializeAccounts()
    }
    
    private fun initializeAccounts() {
        val users = listOf(
            "Alice", "Bob", "Charlie", "David", "Eve"
        )
        
        users.forEach { user ->
            accounts[user] = UserAccount(
                userId = user,
                balance = Random.nextDouble(1000.0, 5000.0),
                creditLimit = Random.nextDouble(2000.0, 5000.0)
            )
        }
    }
    
    suspend fun makePayment(
        userId: String,
        amount: Double
    ): Boolean {
        val account = accounts[userId] ?: return false
        
        if (account.balance >= amount) {
            delay(Random.nextLong(1, 10))
            
            account.balance -= amount
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun makePurchase(
        userId: String,
        amount: Double
    ): Boolean {
        val account = accounts[userId] ?: return false
        
        if (!account.isFrozen && 
            account.balance + account.creditLimit >= amount) {
            delay(Random.nextLong(1, 10))
            
            account.balance -= amount
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun freezeAccount(userId: String): Boolean {
        val account = accounts[userId] ?: return false
        
        if (!account.isFrozen) {
            delay(Random.nextLong(1, 10))
            
            account.isFrozen = true
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun unfreezeAccount(userId: String): Boolean {
        val account = accounts[userId] ?: return false
        
        if (account.isFrozen) {
            delay(Random.nextLong(1, 10))
            
            account.isFrozen = false
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun adjustCreditLimit(
        userId: String,
        newLimit: Double
    ): Boolean {
        val account = accounts[userId] ?: return false
        
        if (newLimit >= 0) {
            delay(Random.nextLong(1, 10))
            
            account.creditLimit = newLimit
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    fun getAccount(userId: String): UserAccount? {
        return accounts[userId]
    }
    
    fun getAllAccounts() = accounts.values.toList()
}

suspend fun simulateUserPurchases(
    accountSystem: AccountManagementSystem,
    userId: String
) {
    repeat(15) { attempt ->
        val amount = Random.nextDouble(50.0, 500.0)
        
        if (Random.nextBoolean()) {
            accountSystem.makePurchase(userId, amount)
        } else {
            accountSystem.makePayment(userId, amount)
        }
        
        delay(Random.nextLong(10, 50))
    }
}

suspend fun simulateAccountFreezing(
    accountSystem: AccountManagementSystem
) {
    repeat(8) { attempt ->
        val accounts = accountSystem.getAllAccounts()
        val account = accounts.random()
        
        if (Random.nextBoolean()) {
            accountSystem.freezeAccount(account.userId)
        } else {
            accountSystem.unfreezeAccount(account.userId)
        }
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateCreditAdjustments(
    accountSystem: AccountManagementSystem
) {
    repeat(10) { attempt ->
        val accounts = accountSystem.getAllAccounts()
        val account = accounts.random()
        
        val newLimit = Random.nextDouble(1000.0, 10000.0)
        accountSystem.adjustCreditLimit(account.userId, newLimit)
        
        delay(Random.nextLong(150, 400))
    }
}

suspend fun simulateLargePurchases(
    accountSystem: AccountManagementSystem
) {
    repeat(5) { attempt ->
        val accounts = accountSystem.getAllAccounts()
        val account = accounts.random()
        
        val amount = Random.nextDouble(1000.0, 3000.0)
        accountSystem.makePurchase(account.userId, amount)
        
        delay(Random.nextLong(200, 500))
    }
}

fun main() = runBlocking {
    val accountSystem = AccountManagementSystem()
    
    println("Starting Account Management System Simulation...")
    println("Initial Account Status:")
    accountSystem.getAllAccounts().forEach { account ->
        println(
            "  ${account.userId}: Balance=${"%.2f".format(account.balance)}, " +
            "CreditLimit=${"%.2f".format(account.creditLimit)}, " +
            "Frozen=${account.isFrozen}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val users = accountSystem.getAllAccounts().map { it.userId }
    
    users.forEach { userId ->
        jobs.add(launch {
            simulateUserPurchases(accountSystem, userId)
        })
    }
    
    jobs.add(launch {
        simulateAccountFreezing(accountSystem)
    })
    
    jobs.add(launch {
        simulateCreditAdjustments(accountSystem)
    })
    
    jobs.add(launch {
        simulateLargePurchases(accountSystem)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Account Status ===")
    accountSystem.getAllAccounts().forEach { account ->
        println(
            "  ${account.userId}: Balance=${"%.2f".format(account.balance)}, " +
            "CreditLimit=${"%.2f".format(account.creditLimit)}, " +
            "Frozen=${account.isFrozen}"
        )
    }
    
    val negativeBalances = accountSystem.getAllAccounts()
        .filter { it.balance < 0 }
    
    if (negativeBalances.isNotEmpty()) {
        println("\n⚠️  Negative Balances:")
        negativeBalances.forEach { account ->
            println("  ${account.userId}: ${"%.2f".format(account.balance)}")
        }
    } else {
        println("\n✅ All accounts have non-negative balances")
    }
    
    val totalBalance = accountSystem.getAllAccounts().sumOf { it.balance }
    val totalCredit = accountSystem.getAllAccounts().sumOf { it.creditLimit }
    
    println("\nTotal Balance: ${"%.2f".format(totalBalance)}")
    println("Total Credit Limit: ${"%.2f".format(totalCredit)}")
}