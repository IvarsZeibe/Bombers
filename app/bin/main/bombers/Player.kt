package bombers

import java.awt.Color
import java.awt.Point
import java.awt.Graphics
import kotlin.math.sign



class Player(val team: Team, coord: Point) {
    // 0 up 1 down 2 left 3 right
    private val controls = when (team) {
        Team.One -> arrayOf(Input.Action.PlayerOneUp, Input.Action.PlayerOneDown, Input.Action.PlayerOneLeft, Input.Action.PlayerOneRight, Input.Action.PlayerOneDropBomb)
        Team.Two -> arrayOf(Input.Action.PlayerTwoUp, Input.Action.PlayerTwoDown, Input.Action.PlayerTwoLeft, Input.Action.PlayerTwoRight, Input.Action.PlayerTwoDropBomb)
    }
    val color = when (team) {
        Team.One -> Color.red
        Team.Two -> Color.green
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
    private var speed = 5f

    private val bombingCooldown = 2000L
    private var lastBombed = 0L

    fun getCoordCopy() = Point(Math.round(position.first), Math.round(position.second))
    fun update(gameTime: GameTime, game: Game) {
        updateMovement(gameTime, game)
        updateBombDropping(gameTime, game)
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
        if (game.isValidCoord(newCoord) && game.board[newCoord.y][newCoord.x].type == BlockType.Empty) {
            bomb.coord = newCoord
            return true
        }
        return false
    }
    private fun getXMovement(): Int {
        var x = 0
        if (Input.actions.contains(controls[3])) {
            x++
        }
        if (Input.actions.contains(controls[2])) {
            x--
        }
        return x
    }
    private fun getYMovement(): Int {
        var y = 0
        if (Input.actions.contains(controls[1])) {
            y++
        }
        if (Input.actions.contains(controls[0])) {
            y--
        }
        return y
    }
    private fun updateBombDropping(gameTime: GameTime, game: Game) {
        if (Input.actions.contains(controls[4]) && canBomb(gameTime)) {
            game.bombs.add(Bomb(team, getCoordCopy(), explosionDistance))
            lastBombed = gameTime.currentMilliseconds()
        }
    }
    private fun canBomb(gameTime: GameTime): Boolean {
        return gameTime.currentMilliseconds() - lastBombed >= bombingCooldown
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
}