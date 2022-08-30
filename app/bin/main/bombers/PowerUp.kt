package bombers

import java.awt.Point

abstract class PowerUp(val coord: Point) {
    abstract fun addTo(player: Player)
    var isDead = false
}

class CanPushPowerUp(coord: Point): PowerUp(coord) {
    override fun addTo(player: Player) {
        player.canPush = true
    }
}

class ExplosionRangePowerUp(coord: Point): PowerUp(coord) {
    override fun addTo(player: Player) {
        player.explosionDistance++
    }
}