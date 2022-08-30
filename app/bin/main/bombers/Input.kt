package bombers

import java.awt.event.KeyListener
import java.awt.event.KeyEvent

object Input : KeyListener {
    enum class Action {
        PlayerOneUp,
        PlayerOneDown,
        PlayerOneLeft,
        PlayerOneRight,
        PlayerOneDropBomb,
        PlayerTwoUp,
        PlayerTwoDown,
        PlayerTwoLeft,
        PlayerTwoRight,
        PlayerTwoDropBomb,
        Exit
    }
    val actions : MutableList<Action> = mutableListOf() 
    
    fun clear() : Unit {
        actions.clear()
    }
    fun print() : Unit {
        for (action in actions) {
            println(action)
        }
        println()
    }
    override fun keyTyped(e: KeyEvent) {

    }
    override fun keyPressed(e: KeyEvent) {
        
        when (e.keyCode) {
            KeyEvent.getExtendedKeyCodeForChar('w'.code) -> Action.PlayerOneUp
            KeyEvent.getExtendedKeyCodeForChar('a'.code) -> Action.PlayerOneLeft
            KeyEvent.getExtendedKeyCodeForChar('d'.code) -> Action.PlayerOneRight
            KeyEvent.getExtendedKeyCodeForChar('s'.code) -> Action.PlayerOneDown
            KeyEvent.VK_SPACE -> Action.PlayerOneDropBomb
            KeyEvent.VK_UP -> Action.PlayerTwoUp
            KeyEvent.VK_LEFT -> Action.PlayerTwoLeft
            KeyEvent.VK_RIGHT -> Action.PlayerTwoRight
            KeyEvent.VK_DOWN -> Action.PlayerTwoDown
            KeyEvent.VK_NUMPAD0 -> Action.PlayerTwoDropBomb
            KeyEvent.VK_ESCAPE -> Action.Exit
            else -> null
        }?.let{ actions.add(it) }
        
    }
    override fun keyReleased(e: KeyEvent) {
        
    }
}