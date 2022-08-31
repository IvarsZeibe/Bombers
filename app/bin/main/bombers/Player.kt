package bombers

import java.awt.Color
import java.awt.Point
import java.awt.Graphics



class Player(val team: Team, coord: Point) {
    val color = when (team) {
        Team.One -> Color.red
        Team.Two -> Color.green
    }
    // 0 up 1 down 2 left 3 right
    private val controls = when (team) {
        Team.One -> arrayOf(Input.Action.PlayerOneUp, Input.Action.PlayerOneDown, Input.Action.PlayerOneLeft, Input.Action.PlayerOneRight, Input.Action.PlayerOneDropBomb)
        Team.Two -> arrayOf(Input.Action.PlayerTwoUp, Input.Action.PlayerTwoDown, Input.Action.PlayerTwoLeft, Input.Action.PlayerTwoRight, Input.Action.PlayerTwoDropBomb)
    }
    //private var coord = coord

    private var position = Pair<Float, Float>(coord.x.toFloat(), coord.y.toFloat())

    var isDead = false
    private var health = 2
    
    private val immunityLength = 5000
    private var immunityLeft = 0
    private val flashingLength = 1000
    private var lastFlashed = 0L

    var canPush = false
    var explosionDistance = 1

    private var speed = 0.1f
    private val movementCooldown = 5L
    private var lastMoved = 0L

    private val bombingCooldown = 2000L
    private var lastBombed = 0L

    fun getX(): Int {
        return Math.round(position.first)
        //return coord.x
    }
    fun getY(): Int {
        return Math.round(position.second)
        //return coord.y
    }

    fun update(gameTime: GameTime, game: Game): Unit {
        updateMovement(gameTime, game)
        updateBombDropping(gameTime, game)
        immunityLeft = Math.max(0, immunityLeft - gameTime.deltaMilliseconds().toInt())
    }

    fun draw(graphics: Graphics, game: Game) {
        if (immunityLeft != 0 && (immunityLeft / flashingLength) % 2 == 1)
            return
        graphics.color = color
        //graphics.fillOval(game.blockSize * coord.x, game.blockSize * coord.y, game.blockSize, game.blockSize)
        graphics.fillOval(Math.round(game.blockSize * position.first), Math.round(game.blockSize * position.second), game.blockSize, game.blockSize)
    }

    private fun updateMovement(gameTime: GameTime, game: Game): Unit {
        if (!canMove(gameTime)) {
            return
        }
        // val xMovement = getXMovement()
        // val yMovement = getYMovement()
        // if (xMovement != 0 && tryMove(if (xMovement > 0) Direction.Right else Direction.Left, game) ||
        //     yMovement != 0 && tryMove(if (yMovement > 0) Direction.Down else Direction.Up, game)) {
        val movement = Pair(getXMovement() * speed, getYMovement() * speed)
        if (tryMoveV2(movement, game)) {
            lastMoved = gameTime.currentMilliseconds()
            val powerUp = game.powerUps.firstOrNull { it.coord == Point(getX(), getY()) }
            if (powerUp != null) {
                powerUp.addTo(this)
                powerUp.isDead = true
            }
        }
    }
    private fun canMove(gameTime: GameTime): Boolean {
        return gameTime.currentMilliseconds() - lastMoved >= movementCooldown
    }
    private fun tryMoveV2(movement: Pair<Float, Float>, game: Game): Boolean {
        var centerCoord = Pair(Math.round(position.first), Math.round(position.second))
        //var offset = Pair(position.first - centerCoord.first, position.second - centerCoord.second)
        val direction: Direction
        val targetPosition: Pair<Float, Float>
        if (movement.first != 0f /*&& Math.abs(centerCoord.second - position.second) < 0.3f*/) {
            targetPosition = Pair(position.first + movement.first, centerCoord.second.toFloat())
            if (movement.first > 0) {
                direction = Direction.Right
            } else {
                direction = Direction.Left
            }
        }
        else if (movement.second != 0f /*&& Math.abs(centerCoord.first - position.first) < 0.3f*/) {
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
            if (bomb == null || Math.abs(bomb.coord.x.toFloat() - targetPosition.first) + Math.abs(bomb.coord.y.toFloat() - targetPosition.second) < 0.5f) {
                position = targetPosition
                return true
            }
            else if (canPush && tryPush(bomb, direction, game)) {
                return true
            }
            return false
        }
        return false
    }
    // private fun tryMove(toDirection: Direction, game: Game): Boolean {
    //     val movement = getMovementFromDirection(toDirection)
    //     val newCoord = Point(coord.x + movement.x, coord.y + movement.y)
    //     if (game.isValidCoord(newCoord) && game.board[newCoord.y][newCoord.x].type == BlockType.Empty) {
    //         val bomb = game.bombs.firstOrNull { it.coord == newCoord}
    //         if (bomb == null) {
    //             coord = newCoord
    //             return true
    //         }
    //         else if (canPush && tryPush(bomb, toDirection, game)) {
    //             return true
    //         }
    //     }
    //     return false
    // }
    private fun getMovementFromDirection(direction: Direction): Point {
        return when (direction) {
            Direction.Left -> Point(-1, 0)
            Direction.Right -> Point(1, 0)
            Direction.Up -> Point(0, -1)
            Direction.Down -> Point(0, 1)
        }
    }
    private fun tryPush(bomb: Bomb, toDirection: Direction, game: Game): Boolean {
        val movement = getMovementFromDirection(toDirection)
        val newCoord = Point(bomb.coord.x + movement.x, bomb.coord.y + movement.y)
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
            game.bombs.add(Bomb(team, Point(getX(), getY()), explosionDistance))
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