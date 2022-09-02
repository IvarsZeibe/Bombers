package bombers

import java.awt.event.KeyListener
import java.awt.event.KeyEvent

enum class InputAction {
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
    PlayerThreeUp,
    PlayerThreeDown,
    PlayerThreeLeft,
    PlayerThreeRight,
    PlayerThreeDropBomb,
    Exit
}

object GameInput : KeyListener {
    val actions = mutableSetOf<InputAction>()
    val keyBinds = mutableSetOf(
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('w'.code), InputAction.PlayerOneUp),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('a'.code), InputAction.PlayerOneLeft),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('d'.code), InputAction.PlayerOneRight),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('s'.code), InputAction.PlayerOneDown),
        KeyBind(KeyEvent.VK_SPACE, InputAction.PlayerOneDropBomb, KeyBind.KeyMode.Pressed),
        KeyBind(KeyEvent.VK_UP, InputAction.PlayerTwoUp),
        KeyBind(KeyEvent.VK_LEFT, InputAction.PlayerTwoLeft),
        KeyBind(KeyEvent.VK_RIGHT, InputAction.PlayerTwoRight),
        KeyBind(KeyEvent.VK_DOWN, InputAction.PlayerTwoDown),
        KeyBind(KeyEvent.VK_NUMPAD0, InputAction.PlayerTwoDropBomb, KeyBind.KeyMode.Pressed),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('i'.code), InputAction.PlayerThreeUp),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('j'.code), InputAction.PlayerThreeLeft),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('l'.code), InputAction.PlayerThreeRight),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar('k'.code), InputAction.PlayerThreeDown),
        KeyBind(KeyEvent.getExtendedKeyCodeForChar(';'.code), InputAction.PlayerThreeDropBomb, KeyBind.KeyMode.Pressed),
        KeyBind(KeyEvent.VK_ESCAPE, InputAction.Exit, KeyBind.KeyMode.Pressed),
    )
    private val oldKeys = mutableListOf<KeyEvent>()
    private val currentKeys = mutableListOf<KeyEvent>()
    private val actualKeys = mutableMapOf<Int, KeyEvent>()
    
    override fun keyTyped(e: KeyEvent) {}
    override fun keyPressed(e: KeyEvent) {
        actualKeys[e.keyCode] = e
    }
    override fun keyReleased(e: KeyEvent) {
        actualKeys.remove(e.keyCode)
    }
    fun update() {
        updateKeys()
        updateActions()
    }
    fun updateKeys() {
        oldKeys.clear()
        oldKeys.addAll(currentKeys)
        currentKeys.clear()
        currentKeys.addAll(actualKeys.values)
    }
    fun updateActions() {
        actions.clear()
        for (keyBind in keyBinds) {
            for (action in keyBind.actions) {
                val shouldAdd = when (action.second) {
                    KeyBind.KeyMode.Pressed -> currentKeys.any { it.keyCode == keyBind.key } && oldKeys.none { it.keyCode == keyBind.key }
                    KeyBind.KeyMode.Held -> currentKeys.any { it.keyCode == keyBind.key }
                    KeyBind.KeyMode.Released -> currentKeys.none { it.keyCode == keyBind.key } && oldKeys.any { it.keyCode == keyBind.key }
                }
                if (shouldAdd) {
                    actions.add(action.first)
                }
            }
        }
    }
}