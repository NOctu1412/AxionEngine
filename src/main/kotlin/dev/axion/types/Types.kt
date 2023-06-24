package dev.axion.types

import dev.axion.AxionEngine
import java.nio.ByteOrder

fun byteType(value: Byte): ByteWasmType {
    return ByteWasmType(value);
}
fun charType(value: Char): CharWasmType {
    return CharWasmType(value);
}

fun booleanType(value: Boolean): BooleanWasmType {
    return BooleanWasmType(value);
}

fun shortType(value: Short): ShortWasmType {
    return ShortWasmType(value);
}

fun intType(value: Int): IntegerWasmType {
    return IntegerWasmType(value);
}

fun longType(value: Long): LongWasmType {
    return LongWasmType(value);
}

fun floatType(value: Float): FloatWasmType {
    return FloatWasmType(value);
}

fun doubleType(value: Double): DoubleWasmType {
    return DoubleWasmType(value);
}

fun stringType(value: String): StringWasmType {
    return StringWasmType(value);
}

fun pointerType(axionEngine: AxionEngine, value: Long, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN): PointerWasmType {
    return PointerWasmType(axionEngine, value, byteOrder);
}

fun valueFromLong(axionEngine: AxionEngine, argumentType: ArgumentType, value: Long): WasmType {
    when(argumentType) {
        ArgumentType.BYTE -> {
            return ByteWasmType(value.toByte());
        }
        ArgumentType.CHAR -> {
            return CharWasmType(value.toInt().toChar());
        }
        ArgumentType.BOOLEAN -> {
            return BooleanWasmType(value == 1L);
        }
        ArgumentType.SHORT -> {
            return ShortWasmType(value.toShort());
        }
        ArgumentType.INTEGER -> {
            return IntegerWasmType(value.toInt());
        }
        ArgumentType.LONG -> {
            return LongWasmType(value);
        }
        ArgumentType.FLOAT -> {
            return FloatWasmType(value.toFloat());
        }
        ArgumentType.DOUBLE -> {
            return DoubleWasmType(value.toDouble());
        }
        ArgumentType.STRING -> {
            //getting the string//
            val length = axionEngine.getStringObjectLength(value);
            val buffer = axionEngine.getStringObjectBuffer(value);
            val stringByteBuffer = axionEngine.getDefaultMemory().read(buffer.toInt(), length.toInt());
            val string = String(stringByteBuffer);
            //freeing the string//
            axionEngine.free(buffer, length);
            axionEngine.destroyStringObject(value);
            return StringWasmType(string);
        }
        ArgumentType.POINTER -> {
            return PointerWasmType(axionEngine, value);
        }
        else -> {
            throw IllegalArgumentException("ArgumentType $argumentType is not supported!");
        }
    }
}