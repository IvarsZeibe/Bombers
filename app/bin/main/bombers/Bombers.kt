package bombers

import javax.swing.*
import java.awt.*
import java.awt.event.KeyListener
import kotlin.time.TimeSource
import kotlin.system.measureTimeMillis

fun main() {
    val screenSize = Toolkit.getDefaultToolkit().screenSize;
    
    var frame = JFrame("Bombers")
    // {
    //     init {
    //         // var blockPane = object: Canvas() {
    //         //     override fun paint(g: Graphics): Unit {
    //         //         super.paint(g)
    //         //         g.color = Color.yellow
    //         //         g.fillRect(0, 0, 100, 10)
    //         //     }
    //         // }
    //         // var blockPane2 = object: Canvas() {
    //         //     override fun paint(g: Graphics): Unit {
    //         //         super.paint(g)
    //         //         g.color = Color.yellow
    //         //         g.fillRect(50, 0, 150, 10)
    //         //     }
    //         // }
    //         var top = JButton();  
    //         top.setBackground(Color.white);  
    //         top.setBounds(20, 20, 50, 50);  
    //         var middle = JButton();  
    //         middle.setBackground(Color.red);  
    //         middle.setBounds(40, 40, 50, 50);  
    //         var layeredPane = getLayeredPane()
    //         layeredPane.add(top, 1)
    //         layeredPane.add(middle, 2)
    //     }
    // }
    frame.setUndecorated(true)
    frame.setSize(800,600)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setLocation((screenSize.width - frame.size.width) / 2, (screenSize.height - frame.size.height) / 2)
    // frame.add(object: Canvas() {
    //     override fun paint(g: Graphics): Unit {
    //         super.paint(g)
    //         g.fillRect(0, 0, 100, 10)
    //         setVisible(true)
    //     }
    // })
    // var layered = JLayeredPane()
    // layered.setPreferredSize(Dimension(300, 300))
    // layered.add(JLabel("test"))
    // layered.add(object: Canvas() {
    //     override fun paint(g: Graphics): Unit {
    //         super.paint(g)
    //         g.color = Color.yellow
    //         g.fillRect(0, 0, 100, 10)
    //     }
    // }, 0)
    // layered.add(object: Canvas() {
    //     override fun paint(g: Graphics): Unit {
    //         super.paint(g)
    //         g.color = Color.red
    //         g.fillRect(50, 0, 150, 10)
    //     }
    // }, 1)
    //frame.setContentPane(layered)
    //frame.add(layered)
    //frame.add(JLabel("test"))
    //frame.pack()
    // var panel = LayeredPaneExample()  
    // panel.setVisible(true)
    // frame.setContentPane(panel)
    
    frame.setVisible(true)

    frame.addKeyListener(Input)

    val gameTime = GameTime()

    var game = Game()
    // var timer = Timer(10) {
    //     gameTime.update()
    //     game.update(gameTime)
    //     Input.clear()
    // }
    // timer.start()
    while (true) {
        if (gameTime.update()) {
            game.update(gameTime)
            Input.clear()
            game.draw(frame.graphics)
        }
    }
}
