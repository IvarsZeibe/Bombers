package bombers

class KeyBind (key: Int, actions: MutableList<Pair<InputAction, KeyMode>>) {
    enum class KeyMode {
        Pressed,
        Held,
        Released
    }
    val key: Int = key
    val actions: MutableList<Pair<InputAction, KeyMode>> = actions
    constructor(key: Int, action: InputAction, mode: KeyMode = KeyMode.Held) : this(key, mutableListOf(Pair(action, mode)))
}