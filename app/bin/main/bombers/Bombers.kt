package bombers

import javax.swing.*
import java.awt.*
import java.awt.event.KeyListener
import kotlin.time.TimeSource
import kotlin.system.measureTimeMillis
import kotlin.math.sign

fun main() {
    var game = Game()    
    var frame = JFrame("Bombers")
    //frame.setUndecorated(true)
    frame.setSize(800,600)
    frame.setLocationRelativeTo(null); 
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.add(object: JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            game.draw(g)
        }
    })
    frame.setVisible(true)
    frame.setAlwaysOnTop(true)

    frame.addKeyListener(Input)

    val gameTime = GameTime()
    while (true) {
        if (gameTime.update()) {
            game.update(gameTime)
            frame.repaint()
        }
    }
}
