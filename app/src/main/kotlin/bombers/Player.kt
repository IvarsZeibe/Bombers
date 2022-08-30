package bombers

import java.awt.Color
import java.awt.Point



class Player(val team: Team, coord: Point) {
    val color = when (team) {
        Team.One -> Color.yellow
        Team.Two -> Color.red
    }
    // 0 up 1 down 2 left 3 right
    private val controls = when (team) {
        Team.One -> arrayOf(Input.Action.PlayerOneUp, Input.Action.PlayerOneDown, Input.Action.PlayerOneLeft, Input.Action.PlayerOneRight, Input.Action.PlayerOneDropBomb)
        Team.Two -> arrayOf(Input.Action.PlayerTwoUp, Input.Action.PlayerTwoDown, Input.Action.PlayerTwoLeft, Input.Action.PlayerTwoRight, Input.Action.PlayerTwoDropBomb)
    }
    private var coord = coord 

    var canPush = false
    var explosionDistance = 1

    private val movementCooldown = 500L
    private var lastMoved = 0L

    private val bombingCooldown = 2000L
    private var lastBombed = 0L

    fun getX(): Int {
        return coord.x
    }
    fun getY(): Int {
        return coord.y
    }

    fun update(gameTime: GameTime, game: Game): Unit {
        updateMovement(gameTime, game)
        updateBombDropping(gameTime, game)
    }

    private fun updateMovement(gameTime: GameTime, game: Game): Unit {
        if (!canMove(gameTime)) {
            return
        }
        val xMovement = getXMovement()
        val yMovement = getYMovement()
        if (xMovement != 0 && tryMove(if (xMovement > 0) Direction.Right else Direction.Left, game) ||
            yMovement != 0 && tryMove(if (yMovement > 0) Direction.Down else Direction.Up, game)) {

            lastMoved = gameTime.currentMilliseconds()
            val powerUp = game.powerUps.firstOrNull { it.coord == coord }
            if (powerUp != null) {
                powerUp.addTo(this)
                powerUp.isDead = true
            }
        }
    }
    private fun canMove(gameTime: GameTime): Boolean {
        return gameTime.currentMilliseconds() - lastMoved >= movementCooldown
    }
    private fun tryMove(toDirection: Direction, game: Game): Boolean {
        val movement = getMovementFromDirection(toDirection)
        val newCoord = Point(coord.x + movement.x, coord.y + movement.y)
        if (game.isValidCoord(newCoord) && game.board[newCoord.y][newCoord.x].type == BlockType.Empty) {
            val bomb = game.bombs.firstOrNull { it.coord == newCoord}
            if (bomb == null) {
                coord = newCoord
                return true
            }
            else if (canPush && tryPush(bomb, toDirection, game)) {
                return true
            }
        }
        return false
    }
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
            game.bombs.add(Bomb(team, Point(coord), explosionDistance))
            lastBombed = gameTime.currentMilliseconds()
        }
    }
    private fun canBomb(gameTime: GameTime): Boolean {
        return gameTime.currentMilliseconds() - lastBombed >= bombingCooldown
    }
}