import kotlin.test.Test
import kotlin.test.assertEquals

internal class JoinToStringTest {

    @Test
    fun `given list of integers, when joinToString is invoked then it returns correct String`() {
        // given
        val l = L.of(1, 2, 3, 4, 5)

        // when
        val actual = l.joinToString("(", ")")

        // then
        assertEquals("( 1 2 3 4 5 )", actual)
    }

    @Test
    fun `given empty list, when joinToString is invoked then it returns correct String`() {
        //given
        val l = L.empty<Int>()

        // when
        val actual = l.joinToString("(", ")")

        // then
        assertEquals("( )", actual)
    }

    @Test
    fun `given custom transform, when joinToString is invoked then it returns correct String`() {
        // given
        val l = L.of("1", "22", "333", "4444", "55555")

        // when
        val actual = l.joinToString("(", ")") { it.length.toString() }

        // then
        assertEquals("( 1 2 3 4 5 )", actual)
    }
}
