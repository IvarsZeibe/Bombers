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

class Game(boardLayout : String = "30000000\n01111110\n01111110\n02222220\n00000000\n00000000\n00000000\n00000000") {

    val blockSize = 30;
    // board[y][x]
    val board : Array<Array<Block>>
    val players : MutableList<Player> = mutableListOf()
    val bombs : MutableList<Bomb> = mutableListOf()
    val powerUps : MutableList<PowerUp> = mutableListOf()

    init { board = createBoard(boardLayout) }

    fun createBoard(boardLayout: String) : Array<Array<Block>> {
        var rowsAsStrings = boardLayout.split("\n")
        return Array<Array<Block>>(8) {
            rowIndex ->
            Array<Block>(8) {
                columnIndex ->
                when(rowsAsStrings[rowIndex][columnIndex]) {
                    '0' -> Block(BlockType.Empty)
                    '1' -> Block(BlockType.Breakable)
                    '2' -> Block(BlockType.Unbreakable)
                    '3' -> Block(BlockType.Empty).also {players.add(Player(Team.One, Point(rowIndex, columnIndex)))}
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
                val blockArea = Area(Rectangle(blockSize * x, blockSize * y, blockSize, blockSize))
                graphics as Graphics2D
                val player = players.firstOrNull { it.getX() == x && it.getY() == y }
                val bomb = bombs.firstOrNull { it.coord.x == x && it.coord.y == y }
                val powerUp = powerUps.firstOrNull { it.coord.x == x && it.coord.y == y }
                if (player != null && bomb != null) {
                    val playerArea = Area(Ellipse2D.Float(blockSize * x * 1f, blockSize * y * 1f, blockSize * 1f, blockSize * 1f))
                    val bombArea = Area(Ellipse2D.Float(blockSize * x * 1f + blockSize * 0.1f, blockSize * y * 1f + blockSize * 0.1f, blockSize * 0.8f, blockSize * 0.8f))
                    playerArea.subtract(bombArea)
                    blockArea.subtract(playerArea)
                    blockArea.subtract(bombArea)
                    graphics.fill(blockArea)
                    graphics.color = player.color
                    graphics.fill(playerArea)
                    graphics.color = Color.black
                    graphics.fill(bombArea)
                }
                else if (player != null){
                    val playerArea = Area(Ellipse2D.Float(blockSize * x * 1f, blockSize * y * 1f, blockSize * 1f, blockSize * 1f))
                    blockArea.subtract(playerArea)
                    graphics.fill(blockArea)
                    graphics.color = player.color
                    graphics.fill(playerArea)
                }
                else if (powerUp != null && bomb != null) {
                    val bombArea = Area(Ellipse2D.Float(blockSize * x * 1f + blockSize * 0.1f, blockSize * y * 1f + blockSize * 0.1f, blockSize * 0.8f, blockSize * 0.8f))
                    val powerUpArea = Area(Ellipse2D.Float(blockSize * x * 1f + blockSize * 0.2f, blockSize * y * 1f + blockSize * 0.2f, blockSize * 0.6f, blockSize * 0.6f))
                    bombArea.subtract(powerUpArea)
                    blockArea.subtract(bombArea)
                    blockArea.subtract(powerUpArea)
                    graphics.fill(blockArea)
                    graphics.color = Color.black
                    graphics.fill(bombArea)
                    graphics.color = when (powerUp) {
                        is CanPushPowerUp -> Color.yellow
                        is ExplosionRangePowerUp -> Color.red
                        else -> Color.pink
                    }
                    graphics.fill(powerUpArea)
                }
                else if (bomb != null) {
                    val bombArea = Area(Ellipse2D.Float(blockSize * x * 1f + blockSize * 0.1f, blockSize * y * 1f + blockSize * 0.1f, blockSize * 0.8f, blockSize * 0.8f))
                    blockArea.subtract(bombArea)
                    graphics.fill(blockArea)
                    graphics.color = Color.black
                    graphics.fill(bombArea)
                }
                else if (powerUp != null) {
                    val powerUpArea = Area(Ellipse2D.Float(blockSize * x * 1f + blockSize * 0.2f, blockSize * y * 1f + blockSize * 0.2f, blockSize * 0.6f, blockSize * 0.6f))
                    blockArea.subtract(powerUpArea)
                    graphics.fill(blockArea)
                    graphics.color = when (powerUp) {
                        is CanPushPowerUp -> Color.yellow
                        is ExplosionRangePowerUp -> Color.red
                        else -> Color.pink
                    }
                    graphics.fill(powerUpArea)
                }
                else {
                    graphics.fill(blockArea)
                }
            }
        }
    }
    fun update(gameTime: GameTime) : Unit {
        for (player in players) {
            player.update(gameTime, this)
        }
        for (bomb in bombs) {
            bomb.update(gameTime, this)
        }
        bombs.removeIf { it.isDead }
        powerUps.removeIf { it.isDead }
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