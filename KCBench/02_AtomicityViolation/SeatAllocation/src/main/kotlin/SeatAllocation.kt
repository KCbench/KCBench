import kotlinx.coroutines.*
import kotlin.random.Random

data class Seat(
    val seatId: String,
    val row: Int,
    val column: Int,
    var status: SeatStatus,
    val price: Double
)

enum class SeatStatus {
    AVAILABLE, SELECTED, BOOKED, OCCUPIED
}

class SeatAllocationSystem {
    private val seats = mutableMapOf<String, Seat>()
    private val rows = 10
    private val columns = 10
    
    init {
        initializeSeats()
    }
    
    private fun initializeSeats() {
        for (row in 1..rows) {
            for (col in 1..columns) {
                val seat = Seat(
                    seatId = "R${row}C${col}",
                    row = row,
                    column = col,
                    status = SeatStatus.AVAILABLE,
                    price = Random.nextDouble(50.0, 200.0)
                )
                seats[seat.seatId] = seat
            }
        }
    }
    
    suspend fun selectSeat(seatId: String, customer: String): Boolean {
        val seat = seats[seatId] ?: return false
        
        if (seat.status == SeatStatus.AVAILABLE) {
            delay(Random.nextLong(1, 10))
            
            seat.status = SeatStatus.SELECTED
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun confirmSeat(seatId: String): Boolean {
        val seat = seats[seatId] ?: return false
        
        if (seat.status == SeatStatus.SELECTED) {
            delay(Random.nextLong(1, 10))
            
            seat.status = SeatStatus.BOOKED
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun releaseSeat(seatId: String): Boolean {
        val seat = seats[seatId] ?: return false
        
        if (seat.status == SeatStatus.SELECTED) {
            delay(Random.nextLong(1, 10))
            
            seat.status = SeatStatus.AVAILABLE
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun bookMultipleSeats(
        seatIds: List<String>,
        customer: String
    ): Int {
        var booked = 0
        
        seatIds.forEach { seatId ->
            if (selectSeat(seatId, customer)) {
                booked++
            }
        }
        
        return booked
    }
    
    suspend fun confirmMultipleSeats(seatIds: List<String>): Int {
        var confirmed = 0
        
        seatIds.forEach { seatId ->
            if (confirmSeat(seatId)) {
                confirmed++
            }
        }
        
        return confirmed
    }
    
    fun getAvailableSeats(): List<Seat> {
        return seats.values.filter { it.status == SeatStatus.AVAILABLE }
    }
    
    fun getSelectedSeats(): List<Seat> {
        return seats.values.filter { it.status == SeatStatus.SELECTED }
    }
    
    fun getAllSeats() = seats.values.toList()
}

class SeatSelector(
    private val allocationSystem: SeatAllocationSystem,
    private val customerName: String
) {
    suspend fun selectSeats(count: Int): List<String> {
        val availableSeats = allocationSystem.getAvailableSeats()
        
        if (availableSeats.size < count) {
            return emptyList()
        }
        
        val selectedSeats = availableSeats.shuffled().take(count)
        val seatIds = mutableListOf<String>()
        
        selectedSeats.forEach { seat ->
            if (allocationSystem.selectSeat(seat.seatId, customerName)) {
                seatIds.add(seat.seatId)
            }
        }
        
        return seatIds
    }
    
    suspend fun confirmSelection(seatIds: List<String>): Int {
        var confirmed = 0
        
        seatIds.forEach { seatId ->
            if (allocationSystem.confirmSeat(seatId)) {
                confirmed++
            }
        }
        
        return confirmed
    }
}

suspend fun simulateCustomerSeatSelection(
    selector: SeatSelector,
    allocationSystem: SeatAllocationSystem,
    customerId: Int
) {
    repeat(12) { attempt ->
        val seatsToSelect = Random.nextInt(1, 5)
        val selectedSeats = selector.selectSeats(seatsToSelect)
        
        if (selectedSeats.isNotEmpty()) {
            println("Customer $customerId selected ${selectedSeats.size} seats")
            
            delay(Random.nextLong(20, 100))
            
            if (Random.nextBoolean()) {
                val confirmed = selector.confirmSelection(selectedSeats)
                println("Customer $customerId confirmed $confirmed seats")
            } else {
                selectedSeats.forEach { seatId ->
                    allocationSystem.releaseSeat(seatId)
                }
            }
        }
        
        delay(Random.nextLong(10, 50))
    }
}

suspend fun simulateGroupBooking(
    allocationSystem: SeatAllocationSystem
) {
    repeat(15) { attempt ->
        val availableSeats = allocationSystem.getAvailableSeats()
        
        if (availableSeats.size >= 4) {
            val selectedSeats = availableSeats.shuffled().take(4)
            val seatIds = selectedSeats.map { it.seatId }
            
            val booked = allocationSystem.bookMultipleSeats(
                seatIds,
                "Group_${Random.nextInt(100, 999)}"
            )
            
            if (booked > 0) {
                delay(Random.nextLong(50, 200))
                
                allocationSystem.confirmMultipleSeats(seatIds)
            }
        }
        
        delay(Random.nextLong(50, 150))
    }
}

suspend fun simulateSeatReleases(
    allocationSystem: SeatAllocationSystem
) {
    repeat(10) { attempt ->
        val selectedSeats = allocationSystem.getSelectedSeats()
        
        if (selectedSeats.isNotEmpty()) {
            val seat = selectedSeats.random()
            allocationSystem.releaseSeat(seat.seatId)
        }
        
        delay(Random.nextLong(100, 300))
    }
}

fun main() = runBlocking {
    val allocationSystem = SeatAllocationSystem()
    
    println("Starting Seat Allocation System Simulation...")
    println("Initial Available Seats: ${allocationSystem.getAvailableSeats().size}")
    println()
    
    val jobs = mutableListOf<Job>()
    
    val selectors = listOf(
        SeatSelector(allocationSystem, "Alice"),
        SeatSelector(allocationSystem, "Bob"),
        SeatSelector(allocationSystem, "Charlie"),
        SeatSelector(allocationSystem, "David"),
        SeatSelector(allocationSystem, "Eve")
    )
    
    selectors.forEachIndexed { index, selector ->
        jobs.add(launch {
            simulateCustomerSeatSelection(selector, allocationSystem, index)
        })
    }
    
    jobs.add(launch {
        simulateGroupBooking(allocationSystem)
    })
    
    jobs.add(launch {
        simulateSeatReleases(allocationSystem)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Seat Status ===")
    val available = allocationSystem.getAvailableSeats().size
    val selected = allocationSystem.getSelectedSeats().size
    val booked = allocationSystem.getAllSeats().count { it.status == SeatStatus.BOOKED }
    
    println("Available: $available")
    println("Selected: $selected")
    println("Booked: $booked")
    
    val doubleSelected = allocationSystem.getSelectedSeats()
    
    if (doubleSelected.size > 10) {
        println("\n⚠️  Many selected seats: ${doubleSelected.size}")
    }
    
    val totalRevenue = allocationSystem.getAllSeats()
        .filter { it.status == SeatStatus.BOOKED }
        .sumOf { it.price }
    
    println("\nTotal Revenue: ${"%.2f".format(totalRevenue)}")
}