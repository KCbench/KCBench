import kotlinx.coroutines.*
import kotlin.random.Random

data class Session(
    val sessionId: String,
    val userId: String,
    var data: MutableMap<String, String>,
    var lastActivity: Long,
    var isActive: Boolean = true
)

class SessionManager {
    private val sessions = mutableMapOf<String, Session>()
    private var activeSessionCount = 0
    private var totalSessionCount = 0
    private val sessionTimeout = 60000L
    
    suspend fun createSession(userId: String): Session {
        val sessionId = "session_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        val currentTime = System.currentTimeMillis()
        
        val session = Session(
            sessionId = sessionId,
            userId = userId,
            data = mutableMapOf(),
            lastActivity = currentTime
        )
        
        sessions[sessionId] = session
        delay(Random.nextLong(1, 10))
        
        val currentTotal = totalSessionCount
        delay(Random.nextLong(1, 5))
        totalSessionCount = currentTotal + 1
        
        val currentActive = activeSessionCount
        delay(Random.nextLong(1, 5))
        activeSessionCount = currentActive + 1
        
        return session
    }
    
    suspend fun getSession(sessionId: String): Session? {
        val session = sessions[sessionId]
        
        if (session != null) {
            val currentLastActivity = session.lastActivity
            delay(Random.nextLong(1, 5))
            
            session.lastActivity = System.currentTimeMillis()
            delay(Random.nextLong(1, 5))
            
            return session
        }
        
        return null
    }
    
    suspend fun updateSessionData(
        sessionId: String,
        key: String,
        value: String
    ): Boolean {
        val session = sessions[sessionId] ?: return false
        
        val currentData = session.data
        delay(Random.nextLong(1, 10))
        
        currentData[key] = value
        delay(Random.nextLong(1, 5))
        
        session.lastActivity = System.currentTimeMillis()
        delay(Random.nextLong(1, 5))
        
        return true
    }
    
    suspend fun removeSessionData(
        sessionId: String,
        key: String
    ): Boolean {
        val session = sessions[sessionId] ?: return false
        
        val currentData = session.data
        delay(Random.nextLong(1, 10))
        
        currentData.remove(key)
        delay(Random.nextLong(1, 5))
        
        session.lastActivity = System.currentTimeMillis()
        delay(Random.nextLong(1, 5))
        
        return true
    }
    
    suspend fun invalidateSession(sessionId: String): Boolean {
        val session = sessions.remove(sessionId)
        delay(Random.nextLong(1, 5))
        
        if (session != null && session.isActive) {
            session.isActive = false
            delay(Random.nextLong(1, 5))
            
            val currentActive = activeSessionCount
            delay(Random.nextLong(1, 5))
            activeSessionCount = maxOf(0, currentActive - 1)
            
            return true
        }
        
        return false
    }
    
    suspend fun cleanupExpiredSessions(): Int {
        val currentTime = System.currentTimeMillis()
        val expiredSessions = sessions.values.filter { 
            currentTime - it.lastActivity > sessionTimeout 
        }
        
        delay(Random.nextLong(1, 10))
        
        var cleanedCount = 0
        expiredSessions.forEach { session ->
            if (invalidateSession(session.sessionId)) {
                cleanedCount++
            }
        }
        
        return cleanedCount
    }
    
    suspend fun getStatistics(): Triple<Int, Int, Int> {
        val currentActive = activeSessionCount
        delay(Random.nextLong(1, 5))
        
        val currentTotal = totalSessionCount
        delay(Random.nextLong(1, 5))
        
        val currentSize = sessions.size
        delay(Random.nextLong(1, 5))
        
        return Triple(currentActive, currentTotal, currentSize)
    }
    
    fun getAllSessions() = sessions.values.toList()
}

class UserSessionHandler(
    private val sessionManager: SessionManager,
    private val userId: String
) {
    private var currentSession: Session? = null
    
    suspend fun login(): Session {
        val session = sessionManager.createSession(userId)
        currentSession = session
        return session
    }
    
    suspend fun performAction(actionName: String) {
        val session = currentSession ?: return
        
        val actionData = mapOf(
            "action" to actionName,
            "timestamp" to System.currentTimeMillis().toString()
        )
        
        actionData.forEach { (key, value) ->
            sessionManager.updateSessionData(
                session.sessionId,
                key,
                value
            )
        }
    }
    
    suspend fun logout() {
        val session = currentSession ?: return
        
        sessionManager.invalidateSession(session.sessionId)
        currentSession = null
    }
}

suspend fun simulateUserActivity(
    handler: UserSessionHandler,
    userId: Int
) {
    val session = handler.login()
    println("User $userId logged in: ${session.sessionId}")
    
    val actions = listOf(
        "view_page", "click_button", "submit_form",
        "download_file", "search_query", "add_to_cart",
        "checkout", "update_profile", "change_settings"
    )
    
    repeat(15) { attempt ->
        handler.performAction(actions.random())
        delay(Random.nextLong(10, 100))
    }
    
    handler.logout()
    println("User $userId logged out")
}

suspend fun simulateSessionCleanup(
    sessionManager: SessionManager
) {
    repeat(10) { attempt ->
        val cleaned = sessionManager.cleanupExpiredSessions()
        if (cleaned > 0) {
            println("Cleaned up $cleaned expired sessions")
        }
        delay(Random.nextLong(500, 1000))
    }
}

suspend fun simulateConcurrentAccess(
    sessionManager: SessionManager,
    sessionId: String
) {
    repeat(10) { attempt ->
        val session = sessionManager.getSession(sessionId)
        
        if (session != null) {
            sessionManager.updateSessionData(
                sessionId,
                "concurrent_access_$attempt",
                System.currentTimeMillis().toString()
            )
        }
        
        delay(Random.nextLong(5, 20))
    }
}

fun main() = runBlocking {
    val sessionManager = SessionManager()
    
    println("Starting Session Management Simulation...")
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateSessionCleanup(sessionManager)
    })
    
    val handlers = mutableListOf<UserSessionHandler>()
    
    repeat(15) { userId ->
        val handler = UserSessionHandler(
            sessionManager,
            "user_$userId"
        )
        handlers.add(handler)
        
        jobs.add(launch {
            simulateUserActivity(handler, userId)
        })
    }
    
    delay(2000)
    
    val activeSessions = sessionManager.getAllSessions()
    if (activeSessions.isNotEmpty()) {
        val randomSession = activeSessions.random()
        
        repeat(5) { index ->
            jobs.add(launch {
                simulateConcurrentAccess(
                    sessionManager,
                    randomSession.sessionId
                )
            })
        }
    }
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (active, total, size) = sessionManager.getStatistics()
    
    println("\n=== Session Statistics ===")
    println("Total Sessions Created: $total")
    println("Active Sessions: $active")
    println("Current Session Count: $size")
    
    val sessions = sessionManager.getAllSessions()
    println("\n=== Active Sessions ===")
    sessions.take(5).forEach { session ->
        println(
            "  ${session.sessionId} (User: ${session.userId}): " +
            "${session.data.size} data entries, " +
            "Last activity: ${System.currentTimeMillis() - session.lastActivity}ms ago"
        )
    }
    
    if (sessions.size > 5) {
        println("  ... and ${sessions.size - 5} more")
    }
    
    val sessionsWithData = sessions.filter { it.data.isNotEmpty() }
    println("\n=== Session Data Analysis ===")
    sessionsWithData.forEach { session ->
        println("  ${session.sessionId}: ${session.data.size} entries")
        session.data.entries.take(3).forEach { (key, value) ->
            println("    $key: $value")
        }
        if (session.data.size > 3) {
            println("    ... and ${session.data.size - 3} more")
        }
    }
    
    val duplicateData = sessions.flatMap { session ->
        session.data.keys.map { key ->
            "${session.sessionId}_$key"
        }
    }.groupingBy { it }.eachCount()
        .filter { (_, count) -> count > 1 }
    
    if (duplicateData.isNotEmpty()) {
        println("\n⚠️  Potential data conflicts detected")
    } else {
        println("\n✅ No obvious data conflicts")
    }
}