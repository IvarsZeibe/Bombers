package bombers

class Utility {
    companion object {
        fun roundAwayFrom(number: Float, from: Float): Int {
            if (number - from < 0) {
                return Math.floor(number.toDouble()).toInt()
            } else {
                return Math.ceil(number.toDouble()).toInt()
            }
        }
        fun roundAwayFrom(number: Float, from: Int): Int {
            return roundAwayFrom(number, from.toFloat())
        }
    }
}