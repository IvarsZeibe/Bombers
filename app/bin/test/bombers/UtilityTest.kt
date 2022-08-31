package bombers

import kotlin.test.Test
import kotlin.test.assertTrue

class UtilityTest {
    @Test fun roundAwayFrom() {
        assertTrue(Utility.roundAwayFrom(1.2f, 1f) == 2)
        assertTrue(Utility.roundAwayFrom(0.999f, 1f) == 0)
        assertTrue(0f.roundAwayFrom(2f) == 0)
        assertTrue((-0.1f).roundAwayFrom(0f) == -1)
        assertTrue(Utility.roundAwayFrom(-0.1f, -0.2f) == 0)
        assertTrue((-1.1f).roundAwayFrom(-1f) == -2)
        assertTrue((-0.1f).roundAwayFrom(-0.2f) == 0)
        assertTrue((-0f).roundAwayFrom(0f) == 0)
    }
}
