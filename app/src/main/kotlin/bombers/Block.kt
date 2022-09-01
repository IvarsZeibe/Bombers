package bombers

enum class BlockType {
    Empty,
    Breakable,
    Unbreakable
}

data class Block(val type: BlockType)