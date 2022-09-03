package bombers

import javax.swing.*
import java.awt.*
import java.awt.event.KeyListener
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

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
        
        //getContentPane().setLayout(GridLayout(1,1))
        setLayout(GridBagLayout())
        val c = GridBagConstraints()
        c.fill = GridBagConstraints.BOTH
        c.weightx = 0.5
        c.weighty = 0.5
        c.gridx = 0
        c.gridy = 0
        add(createGamePanel(game), c)
        c.fill = GridBagConstraints.BOTH
        c.weightx = 0.5
        c.weighty = 0.9
        c.gridx = 1
        c.gridy = 0
        add(createStatsMenu(game), c)
        c.weightx = 0.5
        c.weighty = 0.1
        c.gridx = 1
        c.gridy = 1
        val resetButton = JButton("Reset")
        resetButton.addActionListener(object : ActionListener {
            override fun actionPerformed(e: ActionEvent) {
                game.reset(
"""30122105
02111120
11022011
22022022
22022022
11022011
02111120
00122104""")
                requestFocusInWindow()
            }
        })
        add(resetButton, c)
        
        setVisible(true)
        setAlwaysOnTop(true)
        requestFocusInWindow()
        addKeyListener(GameInput)
        requestFocusInWindow()
    }
}
fun createGamePanel(game: Game): JPanel {
    return object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            game.draw(g)
        }
    }.apply {
        setSize(Dimension(game.blockSize * 8, game.blockSize * 8))
    }
}
fun createStatsMenu(game: Game): JPanel {
    return object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            val font = Font("TimesRoman", Font.PLAIN, 16)
            val boldFont = Font("TimesRoman", Font.BOLD, 16)

            val fontMetrics = g.getFontMetrics(font)
            val fontHeight = fontMetrics.height

            var drawnLines = 0
            fun drawNewLine(text: String, x: Int = 0) {
                g.drawString(text, x, drawnLines * (fontHeight + 5) + fontHeight)
                drawnLines++
            }
            for (player in game.players) {
                val name = "Player ${when (player.team) {
                    Team.One -> "1 (Color red)"
                    Team.Two -> "2 (Color green)"
                    Team.Three -> "3 (Color blue)"
                }}"
                g.setFont(boldFont)
                drawNewLine(name)

                g.setFont(font)
                drawNewLine("Health: ${player.getHealth()}")
                drawNewLine("Max bombs: ${player.maxBombCount}")
                drawNewLine("Explosion distance: ${player.explosionDistance}")
                drawNewLine(if (player.canPush) "Can push bombs" else "Can't push bombs")
                drawNewLine("")
            }
        }
    }.apply {
        setSize(Dimension(game.blockSize * 3, game.blockSize * 8))
        setBackground(Color.LIGHT_GRAY)
    }
}
