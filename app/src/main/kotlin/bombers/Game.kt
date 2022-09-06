package bombers

import java.awt.Point
import java.awt.Graphics
import java.awt.Color
import kotlin.comparisons.compareBy

class Game(var boardLayout: String = 
"""30122100
02111120
11022011
22022022
22022022
11022011
02111120
50122104""") {
    val blockSize = 40
    var maxPlayers = 3
    set(value) {
        field = value.coerceAtLeast(1)
    }
    // board[y][x]
    var board : Array<Array<Block>> = emptyArray()
    val players : MutableList<Player> = mutableListOf()
    val bombs : MutableList<Bomb> = mutableListOf()
    val explosions : MutableList<Explosion> = mutableListOf()
    val powerUps : MutableList<PowerUp> = mutableListOf()

    init { reset() }

    fun createBoard(): Array<Array<Block>> {
        var rowsAsStrings = boardLayout.split("\n")
        return Array<Array<Block>>(8) {
            columnIndex ->
            Array<Block>(8) {
                rowIndex ->
                when (rowsAsStrings[columnIndex][rowIndex]) {
                    '0' -> Block(BlockType.Empty)
                    '1' -> Block(BlockType.Breakable)
                    '2' -> Block(BlockType.Unbreakable)
                    '3' -> Block(BlockType.Empty).also { addPlayer(Point(rowIndex, columnIndex), 1) }
                    '4' -> Block(BlockType.Empty).also { addPlayer(Point(rowIndex, columnIndex), 2) }
                    '5' -> Block(BlockType.Empty).also { addPlayer(Point(rowIndex, columnIndex), 3) }
                    else -> throw Exception("Invalid board layout, $rowsAsStrings[row][column] does not represent a block.")
                }
            }
        }
        .also { players.sortWith(compareBy { it.team }) }
    }
    fun addPlayer(coord: Point, minPlayers: Int) {
        if (maxPlayers >= minPlayers && players.size < maxPlayers ) {
            players.add(Player(Team.values()[players.size % Team.values().count()], coord))
        }
    }
    fun reset() {
        players.clear()
        bombs.clear()
        explosions.clear()
        powerUps.clear()
        board = createBoard()
    }
    fun draw(graphics: Graphics) {
        for ((y, row) in board.withIndex()) {
            for ((x, block) in row.withIndex()) {
                graphics.color = when (block.type) {
                    BlockType.Empty -> Color.white
                    BlockType.Breakable -> Color.gray
                    BlockType.Unbreakable -> Color.black
                }
                graphics.fillRect(blockSize * x, blockSize * y, blockSize, blockSize)
            }
        }
        for (player in players) {
            player.draw(graphics, this)
        }
        for (powerUp in powerUps) {
            powerUp.draw(graphics, this)
        }
        for (explosion in explosions) {
            explosion.draw(graphics, this)
        }
        for (bomb in bombs) {
            bomb.draw(graphics, this)
        }
    }
    fun update(gameTime: GameTime) {
        for (player in players) {
            player.update(gameTime, this)
        }
        for (bomb in bombs) {
            bomb.update(gameTime, this)
        }
        for (explosion in explosions) {
            explosion.update(gameTime, this)
        }
        players.removeIf { it.isDead }
        bombs.removeIf { it.isDead }
        powerUps.removeIf { it.isDead }
        explosions.removeIf { it.isDead }
    }
    fun isValidCoord(coord: Point) =
        coord.x in 0 until board[0].size && coord.y in 0 until board.size
}