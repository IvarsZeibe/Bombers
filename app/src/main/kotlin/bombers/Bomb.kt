package bombers

import java.awt.Point
import java.awt.Graphics
import java.awt.Color
import kotlin.random.Random

class Bomb(
    val number: Team, 
    var coord: Point, 
    val explosionDistance: Int = 1, 
    var fuseTime: Long = 1500
) {
    var isDead = false
    var explosionLength = 500f

    val onDetonate = Action()
    
    fun update(gameTime: GameTime, game: Game) {
        fuseTime -= gameTime.deltaMilliseconds()
        if (fuseTime <= 0 || game.explosions.any { it.coord == coord }) {
            detonate(game)
            isDead = true
        }
    }
    fun draw(graphics: Graphics, game: Game) {
        val blockSize = game.blockSize
        graphics.color = Color.black
        graphics.fillOval(
            blockSize * coord.x + (blockSize * 0.1f).toInt(), 
            blockSize * coord.y + (blockSize * 0.1f).toInt(), 
            (blockSize * 0.8f).toInt(), 
            (blockSize * 0.8f).toInt())
    }
    private fun detonate(game: Game) {
        onDetonate()
        explodeAt(coord, game)
        if (isExplosionStoppedBy(game.board[coord.y][coord.x].type)) {
            return
        }
        loop@ for (direction in Direction.values()) {
            for (offsetFromCenter in 1..explosionDistance) {
                val blockCoord = coord + direction.asPoint() * offsetFromCenter
                if (!game.isValidCoord(blockCoord)) {
                    continue@loop
                }
                val blockType = game.board[blockCoord.y][blockCoord.x].type
                explodeAt(blockCoord, game)
                if (isExplosionStoppedBy(blockType)) {
                    continue@loop
                }
            }
        }
    }
    private fun explodeAt(blockCoord: Point, game: Game) {
        val block = game.board[blockCoord.y][blockCoord.x]
        when (block.type) {
            BlockType.Unbreakable -> {}
            BlockType.Breakable -> {
                breakBlock(blockCoord, game)
                addExplosion(blockCoord, game)
            }
            BlockType.Empty -> {
                destroyPowerUp(blockCoord, game)
                addExplosion(blockCoord, game)
            }
        }
    }
    private fun destroyPowerUp(blockCoord: Point, game: Game) {
        game.powerUps
            .filter { it.coord == blockCoord }
            .forEach { it.isDead = true }
    }
    private fun breakBlock(blockCoord: Point, game: Game) {
        game.board[blockCoord.y][blockCoord.x] = Block(BlockType.Empty)
        if (Random.nextInt(0, 1) == 0) {
            game.powerUps.add(
                when (Random.nextInt(0, 3)) {
                    0 -> CanPushPowerUp(blockCoord)
                    1 -> ExplosionRangePowerUp(blockCoord)
                    2 -> BombCountPowerUp(blockCoord)
                    else -> throw Exception("Don't have that many power ups")
                }
            )
        }
    }
    private fun addExplosion(blockCoord: Point, game: Game) { 
        if (!game.explosions.any { it.coord == blockCoord }) {
            game.explosions.add(Explosion(blockCoord, explosionLength))
        }
    }
    private fun isExplosionStoppedBy(blockType: BlockType) = blockType != BlockType.Empty
}