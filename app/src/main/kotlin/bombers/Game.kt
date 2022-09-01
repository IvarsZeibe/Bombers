package bombers

import javax.swing.JFrame
import javax.swing.JLayeredPane
import javax.swing.JPanel
import java.awt.Rectangle
import java.awt.Graphics
import java.awt.geom.Rectangle2D
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.Color
import java.awt.Graphics2D
import java.awt.AlphaComposite
import java.awt.Point
import kotlin.time.milliseconds

class Game(boardLayout : String = 
"""30122100
02111120
11022011
22022022
22022022
11022011
02111120
00122104""") {

    val blockSize = 40;
    // board[y][x]
    val board : Array<Array<Block>>
    val players : MutableList<Player> = mutableListOf()
    val bombs : MutableList<Bomb> = mutableListOf()
    val explosions : MutableList<Explosion> = mutableListOf()
    val powerUps : MutableList<PowerUp> = mutableListOf()

    init { board = createBoard(boardLayout) }

    fun createBoard(boardLayout: String) : Array<Array<Block>> {
        var rowsAsStrings = boardLayout.split("\n")
        return Array<Array<Block>>(8) {
            columnIndex ->
            Array<Block>(8) {
                rowIndex ->
                when(rowsAsStrings[rowIndex][columnIndex]) {
                    '0' -> Block(BlockType.Empty)
                    '1' -> Block(BlockType.Breakable)
                    '2' -> Block(BlockType.Unbreakable)
                    '3' -> Block(BlockType.Empty).also {players.add(Player(Team.One, Point(rowIndex, columnIndex)))}
                    '4' -> Block(BlockType.Empty).also {players.add(Player(Team.Two, Point(rowIndex, columnIndex)))}
                    else -> throw Exception("Invalid board layout, $rowsAsStrings[row][column] does not represent a block.")
                }
            }
        }
    }
    fun draw(graphics: Graphics) : Unit {
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
            graphics.color = when (powerUp) {
                is CanPushPowerUp -> Color.yellow
                is ExplosionRangePowerUp -> Color.red
                else -> Color.pink
            }
            graphics.fillOval(
                blockSize * powerUp.coord.x + (blockSize * 0.2f).toInt(), 
                blockSize * powerUp.coord.y + (blockSize * 0.2f).toInt(),
                (blockSize * 0.6f).toInt(),
                (blockSize * 0.6f).toInt())
        }
        for (explosion in explosions) {
            graphics.color = Color.orange
            graphics.fillRect(
                blockSize * explosion.coord.x + (blockSize * 0.1f).toInt(),
                blockSize * explosion.coord.y + (blockSize * 0.1f).toInt(), 
                (blockSize * 0.8f).toInt(), 
                (blockSize * 0.8f).toInt())
        }
        for (bomb in bombs) {
            graphics.color = Color.black
            graphics.fillOval(
                blockSize * bomb.coord.x + (blockSize * 0.1f).toInt(), 
                blockSize * bomb.coord.y + (blockSize * 0.1f).toInt(), 
                (blockSize * 0.8f).toInt(), 
                (blockSize * 0.8f).toInt())
        }
    }
    fun update(gameTime: GameTime) : Unit {
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
    fun isValidCoord(coord: Point): Boolean {
        return coord.x in 0 until board[0].size && coord.y in 0 until board.size
    }
}

enum class BlockType {
    Empty,
    Breakable,
    Unbreakable
}

data class Block(val type: BlockType)