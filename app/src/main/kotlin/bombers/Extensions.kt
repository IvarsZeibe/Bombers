package bombers

fun Float.roundAwayFrom(number: Float): Int {
    return Utility.roundAwayFrom(this, number)
}
fun Float.roundAwayFrom(number: Int): Int {
    return Utility.roundAwayFrom(this, number)
}