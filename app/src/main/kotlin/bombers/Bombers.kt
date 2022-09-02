package bombers

import javax.swing.*
import java.awt.*
import java.awt.event.KeyListener
import kotlin.system.measureTimeMillis

fun main() {
    var game = Game()    
    var frame = createGameFrame(game)
    val gameTime = GameTime()
    while (true) {
        if (gameTime.update()) {
            GameInput.update()
            if (InputAction.Exit in GameInput.actions) {
                System.exit(0)
            }
            game.update(gameTime)
            frame.repaint()
        }
    }
}

fun createGameFrame(game: Game): JFrame {
    return JFrame("Bombers").apply {
        setSize(800,600)
        setLocationRelativeTo(null)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        add(object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                game.draw(g)
            }
        })
        setVisible(true)
        setAlwaysOnTop(true)
    
        addKeyListener(GameInput)
    }
}
