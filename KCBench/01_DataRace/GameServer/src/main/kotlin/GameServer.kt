import kotlinx.coroutines.*
import kotlin.random.Random

data class Player(
    val id: String,
    val name: String,
    var score: Int,
    var level: Int,
    var health: Int,
    var experience: Int
)

class GameServer {
    private val players = mutableMapOf<String, Player>()
    private var totalPlayers = 0
    private var totalScore = 0
    private var activeMatches = 0
    
    init {
        runBlocking {
            initializeDefaultPlayers()
        }
    }
    
    private suspend fun initializeDefaultPlayers() {
        val names = listOf(
            "DragonSlayer", "NightHawk", "ShadowMaster",
            "FireStorm", "IceQueen", "ThunderBolt",
            "StarGazer", "MoonWalker", "SunRider", "WindRunner"
        )
        
        names.forEachIndexed { index, name ->
            createPlayer(name)
        }
    }
    
    suspend fun createPlayer(name: String): Player {
        val playerId = "player_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        
        val player = Player(
            id = playerId,
            name = name,
            score = 0,
            level = 1,
            health = 100,
            experience = 0
        )
        
        players[playerId] = player
        delay(Random.nextLong(1, 10))
        
        val currentTotal = totalPlayers
        delay(Random.nextLong(1, 5))
        totalPlayers = currentTotal + 1
        
        return player
    }
    
    suspend fun updateScore(playerId: String, points: Int): Boolean {
        val player = players[playerId] ?: return false
        
        val currentScore = player.score
        delay(Random.nextLong(1, 10))
        
        player.score = currentScore + points
        delay(Random.nextLong(1, 5))
        
        val currentTotal = totalScore
        delay(Random.nextLong(1, 5))
        totalScore = currentTotal + points
        
        return true
    }
    
    suspend fun updateHealth(playerId: String, amount: Int): Boolean {
        val player = players[playerId] ?: return false
        
        val currentHealth = player.health
        delay(Random.nextLong(1, 10))
        
        player.health = (currentHealth + amount).coerceIn(0, 100)
        delay(Random.nextLong(1, 5))
        
        return true
    }
    
    suspend fun addExperience(playerId: String, exp: Int): Boolean {
        val player = players[playerId] ?: return false
        
        val currentExp = player.experience
        delay(Random.nextLong(1, 10))
        
        player.experience = currentExp + exp
        delay(Random.nextLong(1, 5))
        
        val expNeeded = player.level * 100
        if (player.experience >= expNeeded) {
            val currentLevel = player.level
            delay(Random.nextLong(1, 5))
            
            player.level = currentLevel + 1
            delay(Random.nextLong(1, 5))
            
            player.experience = player.experience - expNeeded
            delay(Random.nextLong(1, 5))
        }
        
        return true
    }
    
    suspend fun startMatch(playerIds: List<String>): Boolean {
        val validPlayers = playerIds.mapNotNull { players[it] }
        
        if (validPlayers.size < 2) return false
        
        val currentMatches = activeMatches
        delay(Random.nextLong(1, 5))
        activeMatches = currentMatches + 1
        
        validPlayers.forEach { player ->
            updateHealth(player.id, -Random.nextInt(10, 30))
        }
        
        return true
    }
    
    suspend fun endMatch(winnerId: String, loserId: String): Boolean {
        val winner = players[winnerId]
        val loser = players[loserId]
        
        if (winner != null && loser != null) {
            updateScore(winnerId, 100)
            updateScore(loserId, 10)
            addExperience(winnerId, 50)
            addExperience(loserId, 20)
            
            val currentMatches = activeMatches
            delay(Random.nextLong(1, 5))
            activeMatches = maxOf(0, currentMatches - 1)
            
            return true
        }
        
        return false
    }
    
    suspend fun getLeaderboard(limit: Int): List<Player> {
        val allPlayers = players.values.toList()
        delay(Random.nextLong(1, 5))
        
        return allPlayers.sortedByDescending { it.score }.take(limit)
    }
    
    suspend fun getStatistics(): Triple<Int, Int, Int> {
        val currentTotal = totalPlayers
        delay(Random.nextLong(1, 5))
        
        val currentScore = totalScore
        delay(Random.nextLong(1, 5))
        
        val currentMatches = activeMatches
        delay(Random.nextLong(1, 5))
        
        return Triple(currentTotal, currentScore, currentMatches)
    }
    
    fun getAllPlayers() = players.values.toList()
}

class GameClient(
    val server: GameServer,
    val playerId: String
) {
    suspend fun playMatch(opponentId: String) {
        server.startMatch(listOf(playerId, opponentId))
        
        delay(Random.nextLong(100, 500))
        
        val winner = if (Random.nextBoolean()) playerId else opponentId
        val loser = if (winner == playerId) opponentId else playerId
        
        server.endMatch(winner, loser)
    }
    
    suspend fun gainExperience() {
        val exp = Random.nextInt(10, 50)
        server.addExperience(playerId, exp)
    }
    
    suspend fun takeDamage() {
        val damage = Random.nextInt(5, 25)
        server.updateHealth(playerId, -damage)
    }
    
    suspend fun heal() {
        val healing = Random.nextInt(10, 30)
        server.updateHealth(playerId, healing)
    }
}

suspend fun simulatePlayerGameplay(
    client: GameClient
) {
    repeat(20) { attempt ->
        when (Random.nextInt(4)) {
            0 -> client.gainExperience()
            1 -> client.takeDamage()
            2 -> client.heal()
            3 -> {
                val opponents = client.server.getAllPlayers()
                    .filter { it.id != client.playerId }
                
                if (opponents.isNotEmpty()) {
                    val opponent = opponents.random()
                    client.playMatch(opponent.id)
                }
            }
        }
        
        delay(Random.nextLong(20, 100))
    }
}

suspend fun simulateMatchmaking(
    server: GameServer
) {
    repeat(15) { attempt ->
        val players = server.getAllPlayers()
        
        if (players.size >= 2) {
            val shuffled = players.shuffled()
            val match = shuffled.take(2).map { it.id }
            
            server.startMatch(match)
            
            delay(Random.nextLong(200, 500))
            
            val winner = match.random()
            val loser = match.find { it != winner }!!
            
            server.endMatch(winner, loser)
        }
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateLeaderboardUpdates(
    server: GameServer
) {
    repeat(10) { attempt ->
        val leaderboard = server.getLeaderboard(5)
        
        println("Current Leaderboard:")
        leaderboard.forEachIndexed { index, player ->
            println("  ${index + 1}. ${player.name}: ${player.score} points (Level ${player.level})")
        }
        
        delay(Random.nextLong(500, 1000))
    }
}

fun main() = runBlocking {
    val server = GameServer()
    
    println("Starting Game Server Simulation...")
    println("Initial Players:")
    server.getAllPlayers().forEach { player ->
        println("  ${player.name}: Level ${player.level}, Score: ${player.score}")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    jobs.add(launch {
        simulateMatchmaking(server)
    })
    
    jobs.add(launch {
        simulateLeaderboardUpdates(server)
    })
    
    val players = server.getAllPlayers()
    val clients = players.map { player ->
        GameClient(server, player.id)
    }
    
    clients.forEachIndexed { index, client ->
        jobs.add(launch {
            simulatePlayerGameplay(client)
        })
    }
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (totalPlayers, totalScore, activeMatches) = server.getStatistics()
    
    println("\n=== Server Statistics ===")
    println("Total Players: $totalPlayers")
    println("Total Score: $totalScore")
    println("Active Matches: $activeMatches")
    
    println("\n=== Final Leaderboard ===")
    val leaderboard = server.getLeaderboard(10)
    leaderboard.forEachIndexed { index, player ->
        println(
            "  ${index + 1}. ${player.name}: " +
            "Score: ${player.score}, " +
            "Level: ${player.level}, " +
            "Health: ${player.health}, " +
            "Experience: ${player.experience}"
        )
    }
    
    val healthIssues = server.getAllPlayers().filter { it.health <= 20 }
    if (healthIssues.isNotEmpty()) {
        println("\n⚠️  Players with low health:")
        healthIssues.forEach { player ->
            println("  ${player.name}: ${player.health} HP")
        }
    }
    
    val levelDistribution = server.getAllPlayers()
        .groupingBy { it.level }
        .eachCount()
    
    println("\n=== Level Distribution ===")
    levelDistribution.forEach { (level, count) ->
        println("  Level $level: $count players")
    }
    
    val averageScore = server.getAllPlayers()
        .map { it.score }
        .average()
    
    println("\nAverage Score: ${"%.2f".format(averageScore)}")
}