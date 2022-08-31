package bombers

import java.awt.Point

class Explosion(var coord: Point, var length: Float) {
    var isDead = false

    fun update(gameTime: GameTime, game: Game) {
        tryDamagePlayer(game)
        updateIsDead(gameTime)
    }
    fun updateIsDead(gameTime: GameTime) {
        length -= gameTime.deltaMilliseconds()
        if (length <= 0) {
            isDead = true
        }
    }
    fun tryDamagePlayer(game: Game) {
        game.players
            .firstOrNull { it.getX() == coord.x && it.getY() == coord.y }
            ?.let { it.takeDamage(1) }
    }
}