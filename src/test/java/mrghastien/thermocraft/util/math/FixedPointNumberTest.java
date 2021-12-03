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
            assertEquals(n, new FixedPointNumber(((double) n) + 0.25).intValue());
        }
    }

    @Test
    void testLongValue() {
        assertEquals(15, new FixedPointNumber(15, (short) (1 << 14)).longValue());
        //assertEquals(78375, new FixedPointNumber(((double) 78375) + 0.25).longValue());
        for(long i = 0; i < 10000L; i++) {
            assertEquals(i, new FixedPointNumber((double)i).longValue());
            assertEquals(i, new FixedPointNumber((float) i).longValue());
        }
    }

    @Test
    void testFloatValue() {
        assertEquals(78345.625f, new FixedPointNumber(78345.625f).floatValue());
        assertEquals(78345.625f, new FixedPointNumber(78345.625).floatValue());
        assertEquals(5.25f, new FixedPointNumber(5.25f).floatValue());
        assertEquals(5.25f, new FixedPointNumber(5.25).floatValue());
    }

    @Test
    void testDoubleValue() {
        assertEquals(5.25, new FixedPointNumber(5.25).doubleValue());
        assertEquals(5.25, new FixedPointNumber(5.25f).doubleValue());
        assertEquals(78345.625, new FixedPointNumber(78345.625).doubleValue());
        assertEquals(78345.625, new FixedPointNumber(78345.625f).doubleValue());
    }

    @Test
    void testToString() {
        for(int i = 0; i < 1000000; i++) {
            assertEquals("5.25", new FixedPointNumber(5.25).toString());
        }

        assertEquals("78345.625", new FixedPointNumber(78345.625).toString());
        assertEquals("78345.625", new FixedPointNumber(78345.625f).toString());
        //assertEquals("-5.25", new FixedPointNumber(-5.25).toString());
        //assertEquals("5.25", new FixedPointNumber(5, (short) (1 << 14)).toString());

    }

    @Test
    void add() {
        assertEquals("5.25", new FixedPointNumber(2.625).add(new FixedPointNumber(2.625)).toString());
    }

    @Test
    void sub() {
        assertEquals("2.625", new FixedPointNumber(5.25).sub(new FixedPointNumber(2.625)).toString());
    }
}