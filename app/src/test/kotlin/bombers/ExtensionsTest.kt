package bombers

import kotlin.test.Test
import kotlin.test.assertTrue
import java.awt.Point

class ExtensionsTest {
    @Test fun pointTimes() {
        assertTrue(Point(1, 2) * Point(3, -2) == Point(3, -4))
        assertTrue(Point(0, 1) * Point(-3, 0) == Point(0, 0))
    }
}
