import kotlinx.coroutines.*
import kotlin.random.Random

data class Product(
    val id: Int,
    val name: String,
    var stock: Int,
    var reserved: Int = 0,
    val price: Double
)

class InventoryManager {
    private val products = mutableMapOf<Int, Product>()
    private val totalOrders = 0
    private val failedOrders = 0
    private val successfulOrders = 0
    
    init {
        initializeProducts()
    }
    
    private fun initializeProducts() {
        val productNames = listOf(
            "Laptop", "Smartphone", "Tablet", "Headphones", "Keyboard",
            "Mouse", "Monitor", "Webcam", "Speaker", "Charger"
        )
        
        productNames.forEachIndexed { index, name ->
            products[index + 1] = Product(
                id = index + 1,
                name = name,
                stock = 100,
                price = Random.nextDouble(50.0, 1000.0)
            )
        }
    }
    
    suspend fun reserveProduct(productId: Int, quantity: Int): Boolean {
        val product = products[productId] ?: return false
        
        val currentStock = product.stock
        val currentReserved = product.reserved
        delay(Random.nextLong(1, 10))
        
        if (currentStock - currentReserved >= quantity) {
            product.reserved = currentReserved + quantity
            delay(Random.nextLong(1, 5))
            return true
        }
        
        return false
    }
    
    suspend fun confirmOrder(productId: Int, quantity: Int): Boolean {
        val product = products[productId] ?: return false
        
        val currentStock = product.stock
        val currentReserved = product.reserved
        delay(Random.nextLong(1, 10))
        
        if (currentReserved >= quantity) {
            product.stock = currentStock - quantity
            product.reserved = currentReserved - quantity
            delay(Random.nextLong(1, 5))
            return true
        }
        
        return false
    }
    
    suspend fun cancelReservation(productId: Int, quantity: Int) {
        val product = products[productId] ?: return
        
        val currentReserved = product.reserved
        delay(Random.nextLong(1, 10))
        
        product.reserved = maxOf(0, currentReserved - quantity)
    }
    
    suspend fun restockProduct(productId: Int, quantity: Int) {
        val product = products[productId] ?: return
        
        val currentStock = product.stock
        delay(Random.nextLong(1, 10))
        
        product.stock = currentStock + quantity
    }
    
    fun getProductInfo(productId: Int): Product? = products[productId]
    
    fun getAllProducts() = products.values.toList()
}

class OrderProcessor(
    private val inventory: InventoryManager
) {
    suspend fun processOrder(productId: Int, quantity: Int): Boolean {
        val reserved = inventory.reserveProduct(productId, quantity)
        
        if (!reserved) {
            return false
        }
        
        delay(Random.nextLong(5, 20))
        
        val shouldConfirm = Random.nextBoolean()
        
        return if (shouldConfirm) {
            inventory.confirmOrder(productId, quantity)
        } else {
            inventory.cancelReservation(productId, quantity)
            false
        }
    }
    
    suspend fun processBatchOrders(orders: List<Pair<Int, Int>>): Int {
        var successful = 0
        
        orders.forEach { (productId, quantity) ->
            if (processOrder(productId, quantity)) {
                successful++
            }
        }
        
        return successful
    }
}

suspend fun simulateCustomerActivity(
    inventory: InventoryManager,
    processor: OrderProcessor,
    customerId: Int
) {
    repeat(10) { attempt ->
        val productId = Random.nextInt(1, 11)
        val quantity = Random.nextInt(1, 6)
        
        processor.processOrder(productId, quantity)
        delay(Random.nextLong(1, 50))
    }
}

suspend fun simulateRestocking(
    inventory: InventoryManager
) {
    repeat(5) { attempt ->
        val productId = Random.nextInt(1, 11)
        val quantity = Random.nextInt(10, 51)
        
        inventory.restockProduct(productId, quantity)
        delay(Random.nextLong(100, 200))
    }
}

fun main() = runBlocking {
    val inventory = InventoryManager()
    val processor = OrderProcessor(inventory)
    
    println("Starting E-commerce Inventory Simulation...")
    println("Initial Inventory:")
    inventory.getAllProducts().forEach { product ->
        println("  ${product.name}: Stock=${product.stock}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateRestocking(inventory)
    })
    
    repeat(20) { customerId ->
        jobs.add(launch {
            simulateCustomerActivity(inventory, processor, customerId)
        })
    }
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Inventory Status ===")
    inventory.getAllProducts().forEach { product ->
        println(
            "  ${product.name}: Stock=${product.stock}, " +
            "Reserved=${product.reserved}, " +
            "Available=${product.stock - product.reserved}"
        )
    }
    
    val issues = mutableListOf<String>()
    inventory.getAllProducts().forEach { product ->
        if (product.reserved > product.stock) {
            issues.add(
                "Product ${product.name}: Reserved (${product.reserved}) " +
                "exceeds stock (${product.stock})"
            )
        }
    }
    
    if (issues.isNotEmpty()) {
        println("\n=== Consistency Issues Detected ===")
        issues.forEach { issue ->
            println("  ⚠️  $issue")
        }
    } else {
        println("\n✅ No consistency issues detected")
    }
    
    val totalStock = inventory.getAllProducts().sumOf { it.stock }
    val totalReserved = inventory.getAllProducts().sumOf { it.reserved }
    val expectedStock = 1000 + (5 * 30)
    
    println("\n=== Final Statistics ===")
    println("Total Stock: $totalStock")
    println("Total Reserved: $totalReserved")
    println("Expected Stock: $expectedStock")
    println("Stock Difference: ${expectedStock - totalStock}")
}