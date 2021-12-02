package mrghastien.thermocraft.util.math;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FixedPointNumberTest {

    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testIntValue() {
        for(int i = 0; i < 10; i++) {
            int n = random.nextInt(10000);
            assertEquals(n, new FixedPointNumber(n, (short) (1 << 14)).intValue());
            assertEquals(n, new FixedPointNumber(n + 0.25).intValue());
        }
    }

    @Test
    void testLongValue() {
        for(int i = 0; i < 10; i++) {
            long n = random.nextInt(1000000);
            assertEquals(n, new FixedPointNumber(n, (short) (1 << 14)).longValue());
            assertEquals(n, new FixedPointNumber(n + 0.25).longValue());
        }
    }

    @Test
    void testFloatValue() {
    }

    @Test
    void testDoubleValue() {
    }

    @Test
    void testToString() {
        for(int i = 0; i < 1000000; i++) {
            assertEquals("5.25", new FixedPointNumber(5.25).toString());
        }
        //assertEquals("5.25", new FixedPointNumber(5, (short) (1 << 14)).toString());

    }

    @Test
    void add() {
        assertEquals("5.25", new FixedPointNumber(2.125).add(new FixedPointNumber(2.125)).toString());
    }

    @Test
    void sub() {
    }
}