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
fun String.toCStringWasmType() = CStringWasmType(this)
fun List<Any>.toWasmType(axionEngine: AxionEngine) = PointerWasmType.allocateArray(axionEngine, this.toTypedArray())

fun Any.toWasmType(engine: AxionEngine, wasmType: EnumWasmType): WasmType {
    return when(wasmType) {
        EnumWasmType.BYTE -> ByteWasmType((this as Int).toByte())
        EnumWasmType.CHAR -> CharWasmType((this as Int).toChar())
        EnumWasmType.BOOLEAN -> BooleanWasmType((this as Int) == 1)
        EnumWasmType.SHORT -> ShortWasmType((this as Int).toShort())
        EnumWasmType.INTEGER -> IntegerWasmType(this as Int)
        EnumWasmType.LONG -> LongWasmType(this as Long)
        EnumWasmType.FLOAT -> FloatWasmType(this as Float)
        EnumWasmType.DOUBLE -> DoubleWasmType(this as Double)
        EnumWasmType.CSTRING -> engine.let {
            val pointer = PointerWasmType(engine, this as Int)
            var string = ""
            var i = 0

            while (true) {
                val char: Char = pointer.getArrayElement(i)
                if (char == 0.toChar()) break
                string += char
                i++
            }

            CStringWasmType(string)
        }
        EnumWasmType.STRING -> engine.let {
            val length = it.getStringObjectLength(this as Int)
            val bufferAddress = it.getStringObjectBuffer(this)

            var string = ""
            for(i in 0 until length) {
                string += it.getDefaultMemory().buffer().get(bufferAddress + i).toInt().toChar()
            }
            //freeing the string//

            it.free(bufferAddress, length)
            it.destroyStringObject(this)
            StringWasmType(string)
        }
        EnumWasmType.POINTER -> PointerWasmType(engine, this as Int)
        else -> throw IllegalArgumentException("Unknown argument type: $wasmType")
    }
}

fun Class<*>.getWasmTypeSize(): Int {
    return when (this) {
        Byte::class.java, java.lang.Byte::class.java -> 1
        Char::class.java, java.lang.Character::class.java -> 2
        Boolean::class.java, java.lang.Boolean::class.java -> 1
        Short::class.java, java.lang.Short::class.java -> 2
        Int::class.java, java.lang.Integer::class.java -> 4
        Long::class.java, java.lang.Long::class.java -> 8
        Float::class.java, java.lang.Float::class.java -> 4
        Double::class.java, java.lang.Double::class.java -> 8
        String::class.java, java.lang.String::class.java -> 4 //pointer size
        PointerWasmType::class.java -> 4 //pointer size
        else -> throw IllegalArgumentException("Unsupported type: ${this.simpleName}")
    }
}