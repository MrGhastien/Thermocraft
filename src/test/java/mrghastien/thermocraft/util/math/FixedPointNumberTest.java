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
        //assertEquals(15, FixedPointNumber.valueOf(15, (short) (1 << 14)).intValue());
        assertEquals(78375, FixedPointNumber.valueOf(((double) 78375) + 0.25).intValue());

        assertEquals(-45, FixedPointNumber.valueOf(-15.5).sub(29L, (short) (1 << 15)).intValue());
        assertEquals(-78375, FixedPointNumber.valueOf(-78375.25).intValue());
    }

    @Test
    void testLongValue() {
        //assertEquals(15, FixedPointNumber.valueOf(15, (short) (1 << 14)).longValue());
        assertEquals(78375, FixedPointNumber.valueOf(((double) 78375) + 0.25).longValue());

        assertEquals(-45, FixedPointNumber.valueOf(-15.5).sub(29L, (short) (1 << 15)).longValue());
        assertEquals(-78375, FixedPointNumber.valueOf(-78375.25).longValue());
    }

    @Test
    void testFloatValue() {
        assertEquals(5.25f, FixedPointNumber.valueOf(5.25).floatValue());
        assertEquals(5.25f, FixedPointNumber.valueOf(5.25f).floatValue());
        assertEquals(78345.625f, FixedPointNumber.valueOf(78345.625).floatValue());
        assertEquals(78345.625f, FixedPointNumber.valueOf(78345.625f).floatValue());
        assertEquals(-5.25f, FixedPointNumber.valueOf(-5.25).floatValue());
        assertEquals(-5.25f, FixedPointNumber.valueOf(-5.25f).floatValue());
        assertEquals(-78345.625f, FixedPointNumber.valueOf(-78345.625).floatValue());
        assertEquals(-78345.625f, FixedPointNumber.valueOf(-78345.625f).floatValue());
        assertEquals(0.25f, FixedPointNumber.valueOf(0.25).floatValue());
        assertEquals(0.25f, FixedPointNumber.valueOf(0.25f).floatValue());
        assertEquals(0.625f, FixedPointNumber.valueOf(0.625).floatValue());
        assertEquals(0.625f, FixedPointNumber.valueOf(0.625f).floatValue());
        assertEquals(-0.25f, FixedPointNumber.valueOf(-0.25).floatValue());
        assertEquals(-0.25f, FixedPointNumber.valueOf(-0.25f).floatValue());
        assertEquals(-0.625f, FixedPointNumber.valueOf(-0.625).floatValue());
        assertEquals(-0.625f, FixedPointNumber.valueOf(-0.625f).floatValue());
    }

    @Test
    void testDoubleValue() {
        assertEquals(5.25, FixedPointNumber.valueOf(5.25).doubleValue());
        assertEquals(5.25, FixedPointNumber.valueOf(5.25f).doubleValue());
        assertEquals(78345.625, FixedPointNumber.valueOf(78345.625).doubleValue());
        assertEquals(78345.625, FixedPointNumber.valueOf(78345.625f).doubleValue());
        assertEquals(-5.25, FixedPointNumber.valueOf(-5.25).doubleValue());
        assertEquals(-5.25, FixedPointNumber.valueOf(-5.25f).doubleValue());
        assertEquals(-78345.625, FixedPointNumber.valueOf(-78345.625).doubleValue());
        assertEquals(-78345.625, FixedPointNumber.valueOf(-78345.625f).doubleValue());
        assertEquals(0.25, FixedPointNumber.valueOf(0.25).doubleValue());
        assertEquals(0.25, FixedPointNumber.valueOf(0.25f).doubleValue());
        assertEquals(0.625, FixedPointNumber.valueOf(0.625).doubleValue());
        assertEquals(0.625, FixedPointNumber.valueOf(0.625f).doubleValue());
        assertEquals(-0.25, FixedPointNumber.valueOf(-0.25).doubleValue());
        assertEquals(-0.25, FixedPointNumber.valueOf(-0.25f).doubleValue());
        assertEquals(-0.625, FixedPointNumber.valueOf(-0.625).doubleValue());
        assertEquals(-0.625, FixedPointNumber.valueOf(-0.625f).doubleValue());
    }

    @Test
    void testToString() {
        for(int i = 0; i < 1000000; i++) {
            assertEquals("5.25", FixedPointNumber.valueOf(5.25).toString());
        }

        assertEquals("78345.625", FixedPointNumber.valueOf(78345.625).toString());
        assertEquals("78345.625", FixedPointNumber.valueOf(78345.625f).toString());
        assertEquals("-5.25", FixedPointNumber.valueOf(-5.25).toString());
        assertEquals("-5.25", FixedPointNumber.valueOf(-5.25f).toString());
        assertEquals("-78345.625", FixedPointNumber.valueOf(-78345.625).toString());
        assertEquals("-78345.625", FixedPointNumber.valueOf(-78345.625f).toString());
        //assertEquals("-5.25", FixedPointNumber.valueOf(-5.25).toString());
        //assertEquals("5.25", FixedPointNumber.valueOf(5, (short) (1 << 14)).toString());

    }

    @Test
    void add() {
        assertEquals("5.25", FixedPointNumber.valueOf(2.625).add(FixedPointNumber.valueOf(2.625)).toString());
        assertEquals("2.625", FixedPointNumber.valueOf(5.25).add(FixedPointNumber.valueOf(-2.625)).toString());
        assertEquals("2.625", FixedPointNumber.valueOf(-2.625).add(FixedPointNumber.valueOf(5.25)).toString());
        assertEquals("-2.625", FixedPointNumber.valueOf(2.625).add(FixedPointNumber.valueOf(-5.25)).toString());
        assertEquals("412000.0", FixedPointNumber.valueOf(412000).add(FixedPointNumber.valueOf(-0.0)).toString());
        assertEquals(5, FixedPointNumber.valueOf(5).add(FixedPointNumber.valueOf(Double.MIN_NORMAL)).longValue());
        assertEquals(5, FixedPointNumber.valueOf(5).add(FixedPointNumber.valueOf(Double.MIN_VALUE)).longValue());
        System.out.println(FixedPointNumber.valueOf(5).add(0.25).toString());
    }

    @Test
    void sub() {
        assertEquals("2.625", FixedPointNumber.valueOf(5.25).sub(FixedPointNumber.valueOf(2.625)).toString());
        assertEquals("7.25", FixedPointNumber.valueOf(5.25).sub(-2, (short) 0).toString());
        assertEquals("-7.25", FixedPointNumber.valueOf(-5.25).sub(2, (short) 0).toString());
        assertEquals("-3.25", FixedPointNumber.valueOf(-5.25).sub(-2, (short) 0).toString());
    }
}