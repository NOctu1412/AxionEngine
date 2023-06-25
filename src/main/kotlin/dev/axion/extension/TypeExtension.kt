package dev.axion.extension

import dev.axion.AxionEngine
import dev.axion.types.WasmType
import dev.axion.types.EnumWasmType
import dev.axion.types.impl.*

fun Byte.toWasmType() = ByteWasmType(this)
fun Char.toWasmType() = CharWasmType(this)
fun Boolean.toWasmType() = BooleanWasmType(this)
fun Short.toWasmType() = ShortWasmType(this)
fun Int.toWasmType() = IntegerWasmType(this)
fun Long.toWasmType() = LongWasmType(this)
fun Float.toWasmType() = FloatWasmType(this)
fun Double.toWasmType() = DoubleWasmType(this)
fun String.toWasmType() = StringWasmType(this)

fun Long.toWasmType(engine: AxionEngine, argumentType: EnumWasmType): WasmType {
    return when(argumentType) {
        EnumWasmType.BYTE -> ByteWasmType(this.toByte())
        EnumWasmType.CHAR -> CharWasmType(this.toInt().toChar())
        EnumWasmType.BOOLEAN -> BooleanWasmType(this == 1L)
        EnumWasmType.SHORT -> ShortWasmType(this.toShort())
        EnumWasmType.INTEGER -> IntegerWasmType(this.toInt())
        EnumWasmType.LONG -> LongWasmType(this)
        EnumWasmType.FLOAT -> FloatWasmType(this.toFloat())
        EnumWasmType.DOUBLE -> DoubleWasmType(this.toDouble())
        EnumWasmType.STRING -> engine.let {
            val length = it.getStringObjectLength(this)
            val buffer = it.getStringObjectBuffer(this)

            val string = it.getDefaultMemory().read(buffer.toInt(), length.toInt()).toString(Charsets.UTF_8)
            //freeing the string//

            it.free(buffer, length)
            it.destroyStringObject(this)
            StringWasmType(string)
        }
        EnumWasmType.POINTER -> return PointerWasmType(engine, this)
    }
}