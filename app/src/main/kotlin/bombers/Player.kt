package bombers

import java.awt.Color
import java.awt.Point
import java.awt.Graphics
import kotlin.math.sign

class Player(val team: Team, coord: Point) {
    // 0 up 1 down 2 left 3 right
    private val controls = when (team) {
        Team.One -> arrayOf(InputAction.PlayerOneUp, InputAction.PlayerOneDown, InputAction.PlayerOneLeft, InputAction.PlayerOneRight, InputAction.PlayerOneDropBomb)
        Team.Two -> arrayOf(InputAction.PlayerTwoUp, InputAction.PlayerTwoDown, InputAction.PlayerTwoLeft, InputAction.PlayerTwoRight, InputAction.PlayerTwoDropBomb)
        Team.Three -> arrayOf(InputAction.PlayerThreeUp, InputAction.PlayerThreeDown, InputAction.PlayerThreeLeft, InputAction.PlayerThreeRight, InputAction.PlayerThreeDropBomb)
    }
    val color = when (team) {
        Team.One -> Color.red
        Team.Two -> Color.green
        Team.Three -> Color.blue
    }
    private var position = Pair<Float, Float>(coord.x.toFloat(), coord.y.toFloat())

    var isDead = false
    private var health = 2
    
    private val immunityLength = 5000
    private var immunityLeft = 0
    private val flashingLength = 300
    private var lastFlashed = 0L

    var canPush = false
    var explosionDistance = 1
    var maxBombCount = 1
    var currentBombCount = 0
    private var speed = 5f

    fun getCoordCopy() = Point(Math.round(position.first), Math.round(position.second))
    fun update(gameTime: GameTime, game: Game) {
        updateMovement(gameTime, game)
        updateBombDropping(game)
        immunityLeft = Math.max(0, immunityLeft - gameTime.deltaMilliseconds().toInt())
    }
    fun draw(graphics: Graphics, game: Game) {
        if (immunityLeft != 0 && (immunityLeft / flashingLength) % 2 == 1) {
            return
        }
        graphics.color = color
        graphics.fillOval(
            Math.round(game.blockSize * position.first), 
            Math.round(game.blockSize * position.second), 
            game.blockSize, 
            game.blockSize)
    }
    private fun updateMovement(gameTime: GameTime, game: Game) {
        val movement = Pair(getXMovement() * speed * gameTime.deltaSeconds(), getYMovement() * speed * gameTime.deltaSeconds())
        if (tryMove(movement, game)) {
            val powerUp = game.powerUps.firstOrNull { it.coord == getCoordCopy() }
            if (powerUp != null) {
                powerUp.addTo(this)
                powerUp.isDead = true
            }
        }
    }
    private fun tryMove(movement: Pair<Float, Float>, game: Game): Boolean {
        var centerCoord = Pair(Math.round(position.first), Math.round(position.second))
        val direction: Direction
        val targetPosition: Pair<Float, Float>
        if (movement.first != 0f) {
            targetPosition = Pair(position.first + movement.first, centerCoord.second.toFloat())
            if (movement.first > 0) {
                direction = Direction.Right
            } else {
                direction = Direction.Left
            }
        }
        else if (movement.second != 0f) {
            targetPosition = Pair(centerCoord.first.toFloat(), position.second + movement.second)
            if (movement.second > 0) {
                direction = Direction.Down
            } else {
                direction = Direction.Up
            }
        }
        else {
            return false
        }
        val targetCoord = Point(targetPosition.first.roundAwayFrom(position.first), targetPosition.second.roundAwayFrom(position.second))
        if (game.isValidCoord(targetCoord) && game.board[targetCoord.y][targetCoord.x].type == BlockType.Empty) {
            val bomb = game.bombs.firstOrNull { it.coord == targetCoord}
            if (bomb == null || Math.abs(bomb.coord.x.toFloat() - targetPosition.first) + Math.abs(bomb.coord.y.toFloat() - targetPosition.second) < 0.8f) {
                position = targetPosition
                return true
            }
            else if (canPush && tryPush(bomb, direction, game)) {
                return true
            }
            return false
        }
        else {
            if (movement.first != 0f) 
                position = Pair(targetCoord.x - movement.first.sign * 1.000001f, position.second)
            else if (movement.second != 0f)
                position = Pair(position.first, targetCoord.y - movement.second.sign * 1.000001f)
        }
        return false
    }
    private fun tryPush(bomb: Bomb, toDirection: Direction, game: Game): Boolean {
        val movement = toDirection.asPoint()
        val newCoord = bomb.coord + movement
        if (game.isValidCoord(newCoord) 
            && game.board[newCoord.y][newCoord.x].type == BlockType.Empty
            && game.bombs.none { it.coord == newCoord }
        ) {
            bomb.coord = newCoord
            return true
        }
        return false
    }
    private fun getXMovement(): Int {
        var x = 0
        if (GameInput.actions.contains(controls[3])) {
            x++
        }
        if (GameInput.actions.contains(controls[2])) {
            x--
        }
        return x
    }
    private fun getYMovement(): Int {
        var y = 0
        if (GameInput.actions.contains(controls[1])) {
            y++
        }
        if (GameInput.actions.contains(controls[0])) {
            y--
        }
        return y
    }
    private fun updateBombDropping(game: Game) {
        if (GameInput.actions.contains(controls[4]) 
        && canBomb()) {
            val bombCoord = getCoordCopy()
            if (game.bombs.any { it.coord == bombCoord }) {
                return
            }
            val bomb = Bomb(team, bombCoord, explosionDistance)
            bomb.onDetonate += { currentBombCount-- }
            game.bombs.add(bomb)
            currentBombCount++
        }
    }
    private fun canBomb(): Boolean {
        return currentBombCount < maxBombCount
    }
    fun takeDamage(amount: Int) {
        if (immunityLeft != 0)
            return
        health = Math.max(0, health - amount)
        immunityLeft = immunityLength
        if (health == 0) {
            isDead = true
        }
    }
    fun getHealth() = health
}