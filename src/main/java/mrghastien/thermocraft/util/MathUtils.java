package mrghastien.thermocraft.util;

public class MathUtils {
    public static int scale(int value, int max, int scalar) {
        return map(value, 0, max, 0, scalar);
    }

    public static int map(int value, int min, int max, int newMin, int newMax) {
        if(max - min == 0) return 0;
        return (((value - min) * (newMax - newMin)) / (max - min) + newMin);
    }

    public static double map(double value, double min, double max, double newMin, double newMax) {
        if(max - min == 0) return 0;
        return ((value - min) / (max - min)) * (newMax - newMin) + newMin;
    }

    public static float map(float value, float min, float max, float newMin, float newMax) {
        if(max - min == 0) return 0;
        return ((value - min) / (max - min)) * (newMax - newMin) + newMin;
    }

    public static double clampedMap(double value, double min, double max, double newMin, double newMax) {
        return map(value < min ? min : Math.min(value, max), min, max, newMin, newMax);
    }

    public static int lerp(int start, int end, float t) {
        int diff = end - start;
        return (int) (start + (diff * t));
    }

    public static float inverseLerp(int start, int end, int value) {
        int diff = end - start;
        if(diff == 0)
            return start;
        return ((float) value - (float) start) / diff;
    }

    public static float clamp(float value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }
}
