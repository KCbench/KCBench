import kotlinx.coroutines.*
import kotlin.random.Random

data class Ticket(
    val ticketId: String,
    val event: String,
    val seat: String,
    val price: Double,
    var status: TicketStatus
)

enum class TicketStatus {
    AVAILABLE, RESERVED, SOLD, CANCELLED
}

class TicketBookingSystem {
    private val tickets = mutableMapOf<String, Ticket>()
    private val events = listOf(
        "Concert", "Theater", "Sports", "Movie", "Conference"
    )
    
    init {
        initializeTickets()
    }
    
    private fun initializeTickets() {
        events.forEach { event ->
            repeat(50) { index ->
                val ticket = Ticket(
                    ticketId = "${event}_${index + 1}",
                    event = event,
                    seat = "Seat_${index + 1}",
                    price = Random.nextDouble(50.0, 500.0),
                    status = TicketStatus.AVAILABLE
                )
                tickets[ticket.ticketId] = ticket
            }
        }
    }
    
    suspend fun bookTicket(ticketId: String, customer: String): Boolean {
        val ticket = tickets[ticketId] ?: return false
        
        if (ticket.status == TicketStatus.AVAILABLE) {
            delay(Random.nextLong(1, 10))
            
            ticket.status = TicketStatus.RESERVED
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun confirmBooking(ticketId: String): Boolean {
        val ticket = tickets[ticketId] ?: return false
        
        if (ticket.status == TicketStatus.RESERVED) {
            delay(Random.nextLong(1, 10))
            
            ticket.status = TicketStatus.SOLD
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun cancelBooking(ticketId: String): Boolean {
        val ticket = tickets[ticketId] ?: return false
        
        if (ticket.status == TicketStatus.RESERVED) {
            delay(Random.nextLong(1, 10))
            
            ticket.status = TicketStatus.AVAILABLE
            delay(Random.nextLong(1, 5))
            
            return true
        }
        
        return false
    }
    
    suspend fun bookMultipleTickets(
        ticketIds: List<String>,
        customer: String
    ): Int {
        var booked = 0
        
        ticketIds.forEach { ticketId ->
            if (bookTicket(ticketId, customer)) {
                booked++
            }
        }
        
        return booked
    }
    
    fun getAvailableTickets(event: String): List<Ticket> {
        return tickets.values.filter { 
            it.event == event && it.status == TicketStatus.AVAILABLE 
        }
    }
    
    fun getAllTickets() = tickets.values.toList()
}

class Customer(
    private val bookingSystem: TicketBookingSystem,
    private val name: String
) {
    suspend fun bookTickets(count: Int): Int {
        val availableTickets = bookingSystem.getAllTickets()
            .filter { it.status == TicketStatus.AVAILABLE }
        
        if (availableTickets.size < count) {
            return 0
        }
        
        val selectedTickets = availableTickets.shuffled().take(count)
        var booked = 0
        
        selectedTickets.forEach { ticket ->
            if (bookingSystem.bookTicket(ticket.ticketId, name)) {
                booked++
            }
        }
        
        return booked
    }
    
    suspend fun confirmBookings() {
        val myTickets = bookingSystem.getAllTickets()
            .filter { it.status == TicketStatus.RESERVED }
        
        myTickets.forEach { ticket ->
            bookingSystem.confirmBooking(ticket.ticketId)
            delay(Random.nextLong(10, 50))
        }
    }
}

suspend fun simulateCustomerBooking(
    customer: Customer,
    customerId: Int
) {
    repeat(10) { attempt ->
        val ticketsToBook = Random.nextInt(1, 6)
        val booked = customer.bookTickets(ticketsToBook)
        
        if (booked > 0) {
            println("Customer $customerId booked $booked tickets")
        }
        
        delay(Random.nextLong(20, 100))
        
        if (Random.nextBoolean()) {
            customer.confirmBookings()
        }
    }
}

suspend fun simulateGroupBooking(
    bookingSystem: TicketBookingSystem
) {
    repeat(15) { attempt ->
        val availableTickets = bookingSystem.getAvailableTickets("Concert")
        
        if (availableTickets.size >= 3) {
            val selectedTickets = availableTickets.shuffled().take(3)
            val ticketIds = selectedTickets.map { it.ticketId }
            
            bookingSystem.bookMultipleTickets(
                ticketIds,
                "Group_${Random.nextInt(100, 999)}"
            )
        }
        
        delay(Random.nextLong(50, 200))
    }
}

suspend fun simulateTicketCancellations(
    bookingSystem: TicketBookingSystem
) {
    repeat(10) { attempt ->
        val reservedTickets = bookingSystem.getAllTickets()
            .filter { it.status == TicketStatus.RESERVED }
        
        if (reservedTickets.isNotEmpty()) {
            val ticket = reservedTickets.random()
            bookingSystem.cancelBooking(ticket.ticketId)
        }
        
        delay(Random.nextLong(100, 300))
    }
}

fun main() = runBlocking {
    val bookingSystem = TicketBookingSystem()
    
    println("Starting Ticket Booking System Simulation...")
    println("Initial Available Tickets:")
    bookingSystem.getAllTickets()
        .groupBy { it.event }
        .forEach { (event, tickets) ->
            val available = tickets.count { it.status == TicketStatus.AVAILABLE }
            println("  $event: $available tickets available")
        }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val customers = listOf(
        Customer(bookingSystem, "Alice"),
        Customer(bookingSystem, "Bob"),
        Customer(bookingSystem, "Charlie"),
        Customer(bookingSystem, "David"),
        Customer(bookingSystem, "Eve")
    )
    
    customers.forEachIndexed { index, customer ->
        jobs.add(launch {
            simulateCustomerBooking(customer, index)
        })
    }
    
    jobs.add(launch {
        simulateGroupBooking(bookingSystem)
    })
    
    jobs.add(launch {
        simulateTicketCancellations(bookingSystem)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    println("\n=== Final Ticket Status ===")
    bookingSystem.getAllTickets()
        .groupBy { it.event }
        .forEach { (event, tickets) ->
            val available = tickets.count { it.status == TicketStatus.AVAILABLE }
            val reserved = tickets.count { it.status == TicketStatus.RESERVED }
            val sold = tickets.count { it.status == TicketStatus.SOLD }
            
            println("  $event: Available=$available, Reserved=$reserved, Sold=$sold")
        }
    
    val doubleBooked = bookingSystem.getAllTickets()
        .filter { it.status == TicketStatus.RESERVED }
    
    if (doubleBooked.size > 20) {
        println("\n⚠️  Many reserved tickets: ${doubleBooked.size}")
    }
    
    val totalRevenue = bookingSystem.getAllTickets()
        .filter { it.status == TicketStatus.SOLD }
        .sumOf { it.price }
    
    println("\nTotal Revenue: ${"%.2f".format(totalRevenue)}")
}