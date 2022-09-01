package bombers

import java.awt.Point
import java.awt.Color
import java.awt.Graphics

abstract class PowerUp(val coord: Point) {
    var isDead = false
    abstract protected val color: Color

    abstract fun addTo(player: Player)
    fun draw(graphics: Graphics, game: Game) {
        val blockSize = game.blockSize
        graphics.color = color
        graphics.fillOval(
            blockSize * coord.x + (blockSize * 0.2f).toInt(), 
            blockSize * coord.y + (blockSize * 0.2f).toInt(),
            (blockSize * 0.6f).toInt(),
            (blockSize * 0.6f).toInt())
    }
}

class CanPushPowerUp(coord: Point) : PowerUp(coord) {
    override val color = Color.yellow
    override fun addTo(player: Player) {
        player.canPush = true
    }
}

class ExplosionRangePowerUp(coord: Point) : PowerUp(coord) {
    override val color = Color.red
    override fun addTo(player: Player) {
        player.explosionDistance++
    }
}