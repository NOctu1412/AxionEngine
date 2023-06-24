import dev.axion.args.*

fun byteArg(value: Byte): ByteArgument {
    return ByteArgument(value);
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