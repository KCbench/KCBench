import kotlinx.coroutines.*
import kotlin.random.Random

data class Order(
    val orderId: String,
    val customerId: String,
    val items: List<OrderItem>,
    var status: OrderStatus,
    var totalAmount: Double,
    var createdAt: Long,
    var updatedAt: Long
)

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

class OrderSystem {
    private val orders = mutableMapOf<String, Order>()
    private var totalOrders = 0
    private var totalRevenue = 0.0
    private var cancelledOrders = 0
    
    suspend fun createOrder(
        customerId: String,
        items: List<OrderItem>
    ): Order {
        val orderId = "order_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        val currentTime = System.currentTimeMillis()
        
        val totalAmount = items.sumOf { it.quantity * it.price }
        
        val order = Order(
            orderId = orderId,
            customerId = customerId,
            items = items,
            status = OrderStatus.PENDING,
            totalAmount = totalAmount,
            createdAt = currentTime,
            updatedAt = currentTime
        )
        
        orders[orderId] = order
        delay(Random.nextLong(1, 10))
        
        val currentTotal = totalOrders
        delay(Random.nextLong(1, 5))
        totalOrders = currentTotal + 1
        
        return order
    }
    
    suspend fun confirmOrder(orderId: String): Boolean {
        val order = orders[orderId] ?: return false
        
        val currentStatus = order.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == OrderStatus.PENDING) {
            order.status = OrderStatus.CONFIRMED
            delay(Random.nextLong(1, 5))
            
            order.updatedAt = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            val currentRevenue = totalRevenue
            delay(Random.nextLong(1, 5))
            totalRevenue = currentRevenue + order.totalAmount
            
            return true
        }
        
        return false
    }
    
    suspend fun processOrder(orderId: String): Boolean {
        val order = orders[orderId] ?: return false
        
        val currentStatus = order.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == OrderStatus.CONFIRMED) {
            order.status = OrderStatus.PROCESSING
            delay(Random.nextLong(1, 5))
            
            order.updatedAt = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun shipOrder(orderId: String): Boolean {
        val order = orders[orderId] ?: return false
        
        val currentStatus = order.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == OrderStatus.PROCESSING) {
            order.status = OrderStatus.SHIPPED
            delay(Random.nextLong(1, 5))
            
            order.updatedAt = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun deliverOrder(orderId: String): Boolean {
        val order = orders[orderId] ?: return false
        
        val currentStatus = order.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == OrderStatus.SHIPPED) {
            order.status = OrderStatus.DELIVERED
            delay(Random.nextLong(1, 5))
            
            order.updatedAt = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun cancelOrder(orderId: String): Boolean {
        val order = orders[orderId] ?: return false
        
        val currentStatus = order.status
        delay(Random.nextLong(1, 10))
        
        if (currentStatus == OrderStatus.PENDING || 
            currentStatus == OrderStatus.CONFIRMED) {
            order.status = OrderStatus.CANCELLED
            delay(Random.nextLong(1, 5))
            
            order.updatedAt = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            val currentCancelled = cancelledOrders
            delay(Random.nextLong(1, 5))
            cancelledOrders = currentCancelled + 1
            
            return true
        }
        
        return false
    }
    
    suspend fun getCustomerOrders(customerId: String): List<Order> {
        val allOrders = orders.values.toList()
        delay(Random.nextLong(1, 5))
        
        return allOrders.filter { it.customerId == customerId }
    }
    
    suspend fun getOrdersByStatus(status: OrderStatus): List<Order> {
        val allOrders = orders.values.toList()
        delay(Random.nextLong(1, 5))
        
        return allOrders.filter { it.status == status }
    }
    
    suspend fun getStatistics(): Triple<Int, Double, Int> {
        val currentTotal = totalOrders
        delay(Random.nextLong(1, 5))
        
        val currentRevenue = totalRevenue
        delay(Random.nextLong(1, 5))
        
        val currentCancelled = cancelledOrders
        delay(Random.nextLong(1, 5))
        
        return Triple(currentTotal, currentRevenue, currentCancelled)
    }
    
    fun getAllOrders() = orders.values.toList()
}

class OrderProcessor(
    private val orderSystem: OrderSystem
) {
    suspend fun processOrderLifecycle(orderId: String) {
        if (orderSystem.confirmOrder(orderId)) {
            delay(Random.nextLong(50, 200))
            
            if (orderSystem.processOrder(orderId)) {
                delay(Random.nextLong(100, 300))
                
                if (orderSystem.shipOrder(orderId)) {
                    delay(Random.nextLong(200, 500))
                    
                    orderSystem.deliverOrder(orderId)
                }
            }
        }
    }
    
    suspend fun cancelRandomOrder(customerId: String) {
        val orders = orderSystem.getCustomerOrders(customerId)
        
        if (orders.isNotEmpty()) {
            val pendingOrders = orders.filter { 
                it.status == OrderStatus.PENDING 
            }
            
            if (pendingOrders.isNotEmpty()) {
                val order = pendingOrders.random()
                orderSystem.cancelOrder(order.orderId)
            }
        }
    }
}

suspend fun simulateCustomerOrders(
    orderSystem: OrderSystem,
    customerId: Int
) {
    val customerIdStr = "customer_$customerId"
    
    repeat(10) { attempt ->
        val items = listOf(
            OrderItem(
                productId = "prod_${Random.nextInt(1, 101)}",
                quantity = Random.nextInt(1, 6),
                price = Random.nextDouble(10.0, 100.0)
            )
        )
        
        val order = orderSystem.createOrder(customerIdStr, items)
        println("Customer $customerId created order: ${order.orderId}")
        
        delay(Random.nextLong(20, 100))
    }
}

suspend fun simulateOrderProcessing(
    orderSystem: OrderSystem,
    processor: OrderProcessor
) {
    repeat(20) { attempt ->
        val orders = orderSystem.getOrdersByStatus(OrderStatus.PENDING)
        
        if (orders.isNotEmpty()) {
            val order = orders.random()
            processor.processOrderLifecycle(order.orderId)
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateOrderCancellations(
    orderSystem: OrderSystem,
    processor: OrderProcessor
) {
    repeat(10) { attempt ->
        val customerId = "customer_${Random.nextInt(1, 11)}"
        processor.cancelRandomOrder(customerId)
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateOrderTracking(
    orderSystem: OrderSystem
) {
    repeat(15) { attempt ->
        val orders = orderSystem.getAllOrders()
        
        if (orders.isNotEmpty()) {
            val order = orders.random()
            val status = order.status
            val age = System.currentTimeMillis() - order.updatedAt
            
            println("Order ${order.orderId}: $status (${age}ms since update)")
        }
        
        delay(Random.nextLong(200, 400))
    }
}

fun main() = runBlocking {
    val orderSystem = OrderSystem()
    val processor = OrderProcessor(orderSystem)
    
    println("Starting Order Processing Simulation...")
    println()
    
    val jobs = mutableListOf<Job>()
    
    repeat(10) { customerId ->
        jobs.add(launch {
            simulateCustomerOrders(orderSystem, customerId)
        })
    }
    
    delay(1000)
    
    jobs.add(launch {
        simulateOrderProcessing(orderSystem, processor)
    })
    
    jobs.add(launch {
        simulateOrderCancellations(orderSystem, processor)
    })
    
    jobs.add(launch {
        simulateOrderTracking(orderSystem)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (totalOrders, totalRevenue, cancelledOrders) = orderSystem.getStatistics()
    
    println("\n=== Order System Statistics ===")
    println("Total Orders: $totalOrders")
    println("Total Revenue: ${"%.2f".format(totalRevenue)}")
    println("Cancelled Orders: $cancelledOrders")
    
    val statusCounts = orderSystem.getAllOrders()
        .groupingBy { it.status }
        .eachCount()
    
    println("\n=== Order Status Distribution ===")
    statusCounts.forEach { (status, count) ->
        println("  $status: $count")
    }
    
    println("\n=== Recent Orders ===")
    orderSystem.getAllOrders().takeLast(5).forEach { order ->
        println(
            "  ${order.orderId}: " +
            "Customer: ${order.customerId}, " +
            "Amount: ${"%.2f".format(order.totalAmount)}, " +
            "Status: ${order.status}"
        )
    }
    
    val pendingOrders = orderSystem.getOrdersByStatus(OrderStatus.PENDING)
    if (pendingOrders.isNotEmpty()) {
        println("\n⚠️  Pending Orders: ${pendingOrders.size}")
        pendingOrders.take(5).forEach { order ->
            println("  ${order.orderId}: ${order.totalAmount}")
        }
    }
    
    val customerOrderCounts = orderSystem.getAllOrders()
        .groupingBy { it.customerId }
        .eachCount()
    
    println("\n=== Customer Order Counts ===")
    customerOrderCounts.entries.take(5).forEach { (customerId, count) ->
        println("  $customerId: $count orders")
    }
    
    val averageOrderValue = orderSystem.getAllOrders()
        .filter { it.status != OrderStatus.CANCELLED }
        .map { it.totalAmount }
        .average()
    
    println("\nAverage Order Value: ${"%.2f".format(averageOrderValue)}")
}