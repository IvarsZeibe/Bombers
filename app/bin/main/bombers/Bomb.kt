package bombers

import java.awt.Point
import kotlin.random.Random

class Bomb(val number: Team, var coord: Point, val explosionDistance: Int = 1, var fuseTime: Long = 1500) {
    
    var isDead = false
    fun update(gameTime: GameTime, game: Game): Unit {
        fuseTime -= gameTime.deltaMilliseconds()
        if (fuseTime <= 0) {
            explode(game)
            isDead = true
        }
    }
    private fun explode(game: Game) {
        val openDirections = mutableListOf(Direction.Up, Direction.Down, Direction.Left, Direction.Right)
        val closedDirections: MutableList<Direction> = mutableListOf()

        for (i in 1..explosionDistance) {
            for (direction in openDirections) {
                val blockCoord = when (direction) {
                    Direction.Up -> Point(coord.x, coord.y - i)
                    Direction.Down -> Point(coord.x, coord.y + i)
                    Direction.Left -> Point(coord.x - i, coord.y)
                    Direction.Right -> Point(coord.x + i, coord.y)
                }
                
                if(!game.isValidCoord(blockCoord)) {
                    closedDirections.add(direction)
                    continue;
                }
                val block = game.board[blockCoord.y][blockCoord.x]
                when (block.type) {
                    BlockType.Unbreakable -> closedDirections.add(direction)
                    BlockType.Breakable -> {
                        breakBlock(blockCoord, game)
                        closedDirections.add(direction)
                    }
                    BlockType.Empty -> continue;
                }
            }
            openDirections.removeIf { closedDirections.contains(it) }
        }
    }
    private fun breakBlock(coord: Point, game: Game) {
        game.board[coord.y][coord.x] = Block(BlockType.Empty)
        if (Random.nextInt(0, 1) == 0) {
            game.powerUps.add(
                when (Random.nextInt(0, 2)) {
                    0 -> CanPushPowerUp(coord)
                    1 -> ExplosionRangePowerUp(coord)
                    else -> throw Exception("Dont have that many power ups")
                }
            )
        }

    }
}