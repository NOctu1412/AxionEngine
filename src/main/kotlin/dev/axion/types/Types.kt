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

fun valueFromLong(axionEngine: AxionEngine, argumentType: WasmTypes, value: Long): WasmType {
    when(argumentType) {
        WasmTypes.BYTE -> {
            return ByteWasmType(value.toByte());
        }
        WasmTypes.CHAR -> {
            return CharWasmType(value.toInt().toChar());
        }
        WasmTypes.BOOLEAN -> {
            return BooleanWasmType(value == 1L);
        }
        WasmTypes.SHORT -> {
            return ShortWasmType(value.toShort());
        }
        WasmTypes.INTEGER -> {
            return IntegerWasmType(value.toInt());
        }
        WasmTypes.LONG -> {
            return LongWasmType(value);
        }
        WasmTypes.FLOAT -> {
            return FloatWasmType(value.toFloat());
        }
        WasmTypes.DOUBLE -> {
            return DoubleWasmType(value.toDouble());
        }
        WasmTypes.STRING -> {
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
        WasmTypes.POINTER -> {
            return PointerWasmType(axionEngine, value);
        }
        else -> {
            throw IllegalArgumentException("ArgumentType $argumentType is not supported!");
        }
    }
}