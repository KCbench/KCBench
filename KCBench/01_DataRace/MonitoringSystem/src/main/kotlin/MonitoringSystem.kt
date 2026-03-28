import kotlinx.coroutines.*
import kotlin.random.Random

data class SensorReading(
    val sensorId: String,
    val value: Double,
    val timestamp: Long,
    val unit: String
)

data class Alert(
    val alertId: String,
    val sensorId: String,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long,
    var acknowledged: Boolean = false
)

enum class AlertSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

class MonitoringSystem {
    private val sensors = mutableMapOf<String, SensorConfig>()
    private val readings = mutableListOf<SensorReading>()
    private val alerts = mutableListOf<Alert>()
    private var totalReadings = 0
    private var activeAlerts = 0
    
    init {
        initializeSensors()
    }
    
    private fun initializeSensors() {
        val sensorConfigs = listOf(
            SensorConfig("temp_1", "Temperature", "°C", 0.0, 100.0),
            SensorConfig("temp_2", "Temperature", "°C", 0.0, 100.0),
            SensorConfig("humidity_1", "Humidity", "%", 0.0, 100.0),
            SensorConfig("pressure_1", "Pressure", "hPa", 800.0, 1200.0),
            SensorConfig("vibration_1", "Vibration", "mm/s", 0.0, 50.0)
        )
        
        sensorConfigs.forEach { config ->
            sensors[config.sensorId] = config
        }
    }
    
    suspend fun recordReading(sensorId: String, value: Double): SensorReading? {
        val config = sensors[sensorId] ?: return null
        
        val reading = SensorReading(
            sensorId = sensorId,
            value = value,
            timestamp = System.currentTimeMillis(),
            unit = config.unit
        )
        
        readings.add(reading)
        delay(Random.nextLong(1, 10))
        
        val currentTotal = totalReadings
        delay(Random.nextLong(1, 5))
        totalReadings = currentTotal + 1
        
        checkThresholds(reading)
        
        return reading
    }
    
    private suspend fun checkThresholds(reading: SensorReading) {
        val config = sensors[reading.sensorId] ?: return
        
        val severity = when {
            reading.value > config.maxValue * 1.2 -> AlertSeverity.CRITICAL
            reading.value > config.maxValue -> AlertSeverity.HIGH
            reading.value < config.minValue * 0.8 -> AlertSeverity.MEDIUM
            reading.value < config.minValue -> AlertSeverity.LOW
            else -> null
        }
        
        if (severity != null) {
            createAlert(
                reading.sensorId,
                "${config.name} out of range: ${reading.value}${reading.unit}",
                severity
            )
        }
    }
    
    suspend fun createAlert(
        sensorId: String,
        message: String,
        severity: AlertSeverity
    ): Alert {
        val alertId = "alert_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        val currentTime = System.currentTimeMillis()
        
        val alert = Alert(
            alertId = alertId,
            sensorId = sensorId,
            message = message,
            severity = severity,
            timestamp = currentTime
        )
        
        alerts.add(alert)
        delay(Random.nextLong(1, 10))
        
        val currentActive = activeAlerts
        delay(Random.nextLong(1, 5))
        activeAlerts = currentActive + 1
        
        return alert
    }
    
    suspend fun acknowledgeAlert(alertId: String): Boolean {
        val alert = alerts.find { it.alertId == alertId }
        
        if (alert != null && !alert.acknowledged) {
            val currentAcknowledged = alert.acknowledged
            delay(Random.nextLong(1, 10))
            
            alert.acknowledged = true
            delay(Random.nextLong(1, 5))
            
            val currentActive = activeAlerts
            delay(Random.nextLong(1, 5))
            activeAlerts = maxOf(0, currentActive - 1)
            
            return true
        }
        
        return false
    }
    
    suspend fun getRecentReadings(sensorId: String, limit: Int): List<SensorReading> {
        val allReadings = readings.toList()
        delay(Random.nextLong(1, 5))
        
        return allReadings
            .filter { it.sensorId == sensorId }
            .takeLast(limit)
    }
    
    suspend fun getActiveAlerts(): List<Alert> {
        val allAlerts = alerts.toList()
        delay(Random.nextLong(1, 5))
        
        return allAlerts.filter { !it.acknowledged }
    }
    
    suspend fun getStatistics(): Triple<Int, Int, Int> {
        val currentReadings = totalReadings
        delay(Random.nextLong(1, 5))
        
        val currentAlerts = alerts.size
        delay(Random.nextLong(1, 5))
        
        val currentActive = activeAlerts
        delay(Random.nextLong(1, 5))
        
        return Triple(currentReadings, currentAlerts, currentActive)
    }
    
    fun getAllReadings() = readings.toList()
    
    fun getAllAlerts() = alerts.toList()
    
    fun getSensors() = sensors.values.toList()
}

data class SensorConfig(
    val sensorId: String,
    val name: String,
    val unit: String,
    val minValue: Double,
    val maxValue: Double
)

class SensorSimulator(
    private val monitoringSystem: MonitoringSystem,
    private val sensorId: String
) {
    private var currentValue = 50.0
    
    suspend fun simulateReading() {
        val config = monitoringSystem.getSensors()
            .find { it.sensorId == sensorId }
        
        if (config != null) {
            currentValue += Random.nextDouble(-10.0, 10.0)
            currentValue = currentValue.coerceIn(
                config.minValue - 20.0,
                config.maxValue + 20.0
            )
            
            monitoringSystem.recordReading(sensorId, currentValue)
        }
    }
}

suspend fun simulateSensorData(
    monitoringSystem: MonitoringSystem,
    sensorId: String
) {
    val simulator = SensorSimulator(monitoringSystem, sensorId)
    
    repeat(30) { attempt ->
        simulator.simulateReading()
        delay(Random.nextLong(50, 200))
    }
}

suspend fun simulateAlertHandling(
    monitoringSystem: MonitoringSystem
) {
    repeat(20) { attempt ->
        val activeAlerts = monitoringSystem.getActiveAlerts()
        
        if (activeAlerts.isNotEmpty()) {
            val alert = activeAlerts.random()
            monitoringSystem.acknowledgeAlert(alert.alertId)
            println("Acknowledged alert: ${alert.alertId}")
        }
        
        delay(Random.nextLong(100, 300))
    }
}

suspend fun simulateDataAnalysis(
    monitoringSystem: MonitoringSystem
) {
    repeat(15) { attempt ->
        val sensors = monitoringSystem.getSensors()
        
        sensors.forEach { sensor ->
            val readings = monitoringSystem.getRecentReadings(
                sensor.sensorId,
                10
            )
            
            if (readings.isNotEmpty()) {
                val avgValue = readings.map { it.value }.average()
                val maxValue = readings.maxOfOrNull { it.value } ?: 0.0
                val minValue = readings.minOfOrNull { it.value } ?: 0.0
                
                println(
                    "Sensor ${sensor.sensorId}: " +
                    "Avg=${"%.2f".format(avgValue)}, " +
                    "Max=${"%.2f".format(maxValue)}, " +
                    "Min=${"%.2f".format(minValue)}"
                )
            }
        }
        
        delay(Random.nextLong(500, 1000))
    }
}

fun main() = runBlocking {
    val monitoringSystem = MonitoringSystem()
    
    println("Starting Monitoring System Simulation...")
    println("Initialized Sensors:")
    monitoringSystem.getSensors().forEach { sensor ->
        println("  ${sensor.sensorId}: ${sensor.name} (${sensor.unit})")
    }
    println()
    
    val jobs = mutableListOf<Job>()
    
    val sensorIds = monitoringSystem.getSensors().map { it.sensorId }
    
    sensorIds.forEach { sensorId ->
        jobs.add(launch {
            simulateSensorData(monitoringSystem, sensorId)
        })
    }
    
    jobs.add(launch {
        simulateAlertHandling(monitoringSystem)
    })
    
    jobs.add(launch {
        simulateDataAnalysis(monitoringSystem)
    })
    
    jobs.forEach { it.join() }
    
    delay(500)
    
    val (totalReadings, totalAlerts, activeAlerts) = monitoringSystem.getStatistics()
    
    println("\n=== Monitoring System Statistics ===")
    println("Total Readings: $totalReadings")
    println("Total Alerts: $totalAlerts")
    println("Active Alerts: $activeAlerts")
    
    println("\n=== Readings by Sensor ===")
    monitoringSystem.getSensors().forEach { sensor ->
        val readings = monitoringSystem.getRecentReadings(sensor.sensorId, Int.MAX_VALUE)
        println(
            "  ${sensor.sensorId}: ${readings.size} readings, " +
            "Avg=${"%.2f".format(readings.map { it.value }.average())}"
        )
    }
    
    val severityCounts = monitoringSystem.getAllAlerts()
        .groupingBy { it.severity }
        .eachCount()
    
    println("\n=== Alert Severity Distribution ===")
    severityCounts.forEach { (severity, count) ->
        println("  $severity: $count")
    }
    
    val recentAlerts = monitoringSystem.getAllAlerts().takeLast(10)
    if (recentAlerts.isNotEmpty()) {
        println("\n=== Recent Alerts ===")
        recentAlerts.forEach { alert ->
            println(
                "  ${alert.alertId}: ${alert.message} " +
                "[${alert.severity}] " +
                "Acknowledged: ${alert.acknowledged}"
            )
        }
    }
    
    val unacknowledgedAlerts = monitoringSystem.getActiveAlerts()
    if (unacknowledgedAlerts.isNotEmpty()) {
        println("\n⚠️  Unacknowledged Alerts: ${unacknowledgedAlerts.size}")
        unacknowledgedAlerts.take(5).forEach { alert ->
            println("  ${alert.alertId}: ${alert.message}")
        }
    } else {
        println("\n✅ All alerts acknowledged")
    }
    
    val criticalAlerts = monitoringSystem.getAllAlerts()
        .filter { it.severity == AlertSeverity.CRITICAL }
    
    if (criticalAlerts.isNotEmpty()) {
        println("\n🚨 Critical Alerts: ${criticalAlerts.size}")
        criticalAlerts.take(5).forEach { alert ->
            println("  ${alert.alertId}: ${alert.message}")
        }
    }
}