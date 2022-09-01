package bombers

import java.awt.Point

fun Float.roundAwayFrom(number: Float): Int {
    return Utility.roundAwayFrom(this, number)
}
fun Float.roundAwayFrom(number: Int): Int {
    return Utility.roundAwayFrom(this, number)
}
operator fun Point.minus(point: Point): Point {
    return Point(x - point.x, y - point.y)
} 
operator fun Point.plus(point: Point): Point {
    return Point(x + point.x, y + point.y)
} 
operator fun Point.times(point: Point): Point {
    return Point(x * point.x, y * point.y)
} 
operator fun Point.times(number: Int): Point {
    return Point(x * number, y * number)
} 