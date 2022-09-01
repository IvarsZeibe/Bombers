package bombers

class GameTime {
    private val startTime = System.nanoTime()
    private var deltaTime: Long = 0
    private var currentTime = startTime
    private var preferredUpdateRate = 16E6.toLong()

    fun update() : Boolean {
        val newCurrentTime = System.nanoTime()
        val newDeltaTime = newCurrentTime - currentTime
        if (newDeltaTime >= preferredUpdateRate) {
            deltaTime = newDeltaTime
            currentTime = newCurrentTime
            return true
        }
        return false
    }
    fun setPreferredUpdateRateInMilliseconds(milliseconds: Long) {
        preferredUpdateRate = milliseconds * 1E6.toLong()
    }
    fun deltaMilliseconds() = deltaTime / 1000000
    fun deltaSeconds() = deltaMilliseconds() / 1000f
    fun currentMilliseconds() = currentTime / 1000000
}