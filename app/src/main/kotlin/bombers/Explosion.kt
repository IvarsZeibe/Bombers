package bombers

import java.awt.Point
import java.awt.Graphics
import java.awt.Color

class Explosion(var coord: Point, var length: Float) {
    var isDead = false

    fun update(gameTime: GameTime, game: Game) {
        damagePlayers(game)
        updateIsDead(gameTime)
    }
    fun draw(graphics: Graphics, game: Game) {
        val blockSize = game.blockSize
        graphics.color = Color.orange
        graphics.fillRect(
            blockSize * coord.x + (blockSize * 0.1f).toInt(),
            blockSize * coord.y + (blockSize * 0.1f).toInt(), 
            (blockSize * 0.8f).toInt(), 
            (blockSize * 0.8f).toInt())
    }
    fun damagePlayers(game: Game) {
        game.players
            .filter { it.getCoordCopy() == coord }
            .forEach { it.takeDamage(1)
        }
    }
    fun updateIsDead(gameTime: GameTime) {
        length -= gameTime.deltaMilliseconds()
        if (length <= 0) {
            isDead = true
        }
    }
}