import dev.axion.AxionEngine
import dev.axion.args.*

fun byteArg(value: Byte): ByteArgument {
    return ByteArgument(value);
}
fun charArg(value: Char): CharArgument {
    return CharArgument(value);
}

fun booleanArg(value: Boolean): BooleanArgument {
    return BooleanArgument(value);
}

fun shortArg(value: Short): ShortArgument {
    return ShortArgument(value);
}

fun intArg(value: Int): IntegerArgument {
    return IntegerArgument(value);
}

fun longArg(value: Long): LongArgument {
    return LongArgument(value);
}

fun floatArg(value: Float): FloatArgument {
    return FloatArgument(value);
}

fun doubleArg(value: Double): DoubleArgument {
    return DoubleArgument(value);
}

fun stringArg(value: String): StringArgument {
    return StringArgument(value);
}

fun valueFromLong(axionEngine: AxionEngine, argumentType: ArgumentType, value: Long): Argument {
    when(argumentType) {
        ArgumentType.BYTE -> {
            return ByteArgument(value.toByte());
        }
        ArgumentType.CHAR -> {
            return CharArgument(value.toInt().toChar());
        }
        ArgumentType.BOOLEAN -> {
            return BooleanArgument(value == 1L);
        }
        ArgumentType.SHORT -> {
            return ShortArgument(value.toShort());
        }
        ArgumentType.INTEGER -> {
            return IntegerArgument(value.toInt());
        }
        ArgumentType.LONG -> {
            return LongArgument(value);
        }
        ArgumentType.FLOAT -> {
            return FloatArgument(value.toFloat());
        }
        ArgumentType.DOUBLE -> {
            return DoubleArgument(value.toDouble());
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
            return StringArgument(string);
        }
    }
}