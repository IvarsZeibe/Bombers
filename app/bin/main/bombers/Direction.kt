package bombers

import java.awt.Point

enum class Direction {
    Up,
    Down,
    Left,
    Right;

    fun asPoint() = when (this) {
        Direction.Up -> Point(0, -1)
        Direction.Down -> Point(0, 1)
        Direction.Left -> Point(-1, 0)
        Direction.Right -> Point(1, 0)
    }
}