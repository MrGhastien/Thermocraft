package mrghastien.thermocraft.common.network;

import mrghastien.thermocraft.common.network.data.DataType;

public class DataTypeMismatchException extends RuntimeException {

    public DataTypeMismatchException(DataType<?> expected, DataType<?> given) {
        this("Expected " + expected + ", found " + given);
    }

    public DataTypeMismatchException(String message) {
        super(message);
    }

    public DataTypeMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
