import kotlinx.coroutines.*
import kotlin.random.Random

data class Product(
    val productId: String,
    val name: String,
    var stock: Int,
    var reserved: Int = 0,
    val price: Double
)

class InventoryReservationSystem {
    private val products = mutableMapOf<String, Product>()
    
    init {
        initializeProducts()
    }
    
    private fun initializeProducts() {
        val productNames = listOf(
            "Laptop", "Smartphone", "Tablet", "Headphones", "Keyboard",
            "Mouse", "Monitor", "Webcam", "Speaker", "Charger"
        )
        
        productNames.forEach { name ->
            products[name] = Product(
                productId = "PROD_${Random.nextInt(1000, 9999)}",
                name = name,
                stock = Random.nextInt(50, 150),
                price = Random.nextDouble(50.0, 1000.0)
            )
        }
    }
    
    suspend fun reserveStock(
        productName: String,
        quantity: Int
    ): Boolean {
        val product = products[productName] ?: return false
        
        if (product.stock - product.reserved >= quantity) {
            delay(Random.nextLong(1, 10))
            
            product.reserved += quantity
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun confirmReservation(
        productName: String,
        quantity: Int
    ): Boolean {
        val product = products[productName] ?: return false
        
        if (product.reserved >= quantity) {
            delay(Random.nextLong(1, 10))
            
            product.stock -= quantity
            product.reserved -= quantity
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun cancelReservation(
        productName: String,
        quantity: Int
    ): Boolean {
        val product = products[productName] ?: return false
        
        if (product.reserved >= quantity) {
            delay(Random.nextLong(1, 10))
            
            product.reserved -= quantity
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun restockProduct(
        productName: String,
        quantity: Int
    ): Boolean {
        val product = products[productName] ?: return false
        
        delay(Random.nextLong(1, 10))
        
        product.stock += quantity
        delay(Random.nextLong(1, 5))
        
        return true
    }
    
    suspend fun batchReserve(
        items: List<Pair<String, Int>>
    ): Boolean {
        var allReserved = true
        
        items.forEach { (name, quantity) ->
            if (!reserveStock(name, quantity)) {
                allReserved = false
            }
        }
        
        return allReserved
    }
    
    fun getProductInfo(productName: String): Product? {
        return products[productName]
    }
    
    fun getAllProducts() = products.values.toList()
}

class OrderProcessor(
    private val inventory: InventoryReservationSystem
) {
    suspend fun processOrder(
        items: List<Pair<String, Int>>
    ): Boolean {
        val reserved = inventory.batchReserve(items)
        
        if (reserved) {
            delay(Random.nextLong(50, 200))
            
            items.forEach { (name, quantity) ->
                inventory.confirmReservation(name, quantity)
            }
            
            return true
        }
        
        return false
    }
}

suspend fun simulateCustomerOrders(
    inventory: InventoryReservationSystem,
    customerId: Int
) {
    repeat(15) { attempt ->
        val products = inventory.getAllProducts()
        val selectedProduct = products.random()
        val quantity = Random.nextInt(1, 6)
        
        if (inventory.reserveStock(
                selectedProduct.name,
                quantity
            )) {
            delay(Random.nextLong(20, 100))
            
            if (Random.nextBoolean()) {
                inventory.confirmReservation(
                    selectedProduct.name,
                    quantity
                )
            } else {
                inventory.cancelReservation(
                    selectedProduct.name,
                    quantity
                )
            }
        }
        
        delay(Random.nextLong(10, 50))
    }
}

suspend fun simulateBulkOrders(
    inventory: InventoryReservationSystem,
    processor: OrderProcessor
) {
    repeat(10) { attempt ->
        val products = inventory.getAllProducts()
        val selectedProducts = products.shuffled().take(3)
        
        val items = selectedProducts.map { product ->
            Pair(product.name, Random.nextInt(1, 4))
        }
        
        processor.processOrder(items)
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateRestocking(
    inventory: InventoryReservationSystem
) {
    repeat(8) { attempt ->
        val products = inventory.getAllProducts()
        val product = products.random()
        
        inventory.restockProduct(
            product.name,
            Random.nextInt(20, 50)
        )
        
        delay(Random.nextLong(200, 500))
    }
}

fun main() = runBlocking {
    val inventory = InventoryReservationSystem()
    val processor = OrderProcessor(inventory)
    
    println("Starting Inventory Reservation System Simulation...")
    println("Initial Inventory:")
    inventory.getAllProducts().forEach { product ->
        println(
            "  ${product.name}: Stock=${product.stock}, " +
            "Reserved=${product.reserved}, " +
            "Available=${product.stock - product.reserved}"
        )
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    repeat(10) { customerId ->
        jobs.add(launch {
            simulateCustomerOrders(inventory, customerId)
        })
    }
    
    jobs.add(launch {
        simulateBulkOrders(inventory, processor)
    })
    
    jobs.add(launch {
        simulateRestocking(inventory)
    })
    
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
    
    val overReserved = inventory.getAllProducts()
        .filter { it.reserved > it.stock }
    
    if (overReserved.isNotEmpty()) {
        println("\n⚠️  Over-reserved Products:")
        overReserved.forEach { product ->
            println(
                "  ${product.name}: Reserved=${product.reserved}, " +
                "Stock=${product.stock}"
            )
        }
    } else {
        println("\n✅ No over-reserved products")
    }
    
    val totalStock = inventory.getAllProducts().sumOf { it.stock }
    val totalReserved = inventory.getAllProducts().sumOf { it.reserved }
    
    println("\nTotal Stock: $totalStock")
    println("Total Reserved: $totalReserved")
}