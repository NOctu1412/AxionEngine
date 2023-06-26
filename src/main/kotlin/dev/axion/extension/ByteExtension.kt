package dev.axion.extension

import dev.axion.types.impl.PointerWasmType
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteArray.toShort(byteOrder: ByteOrder): Short {
    val (high, low) = (this[0].toInt() and 0xFF).toShort() to (this[1].toInt() and 0xFF).toShort()

    return when (byteOrder) {
        ByteOrder.BIG_ENDIAN -> (high.toInt() shl 8 or low.toInt()).toShort()
        ByteOrder.LITTLE_ENDIAN -> (low.toInt() shl 8 or high.toInt()).toShort()
        else -> 0
    }
}

fun ByteArray.toInt(byteOrder: ByteOrder): Int {
    return ByteBuffer.wrap(this).order(byteOrder).getInt()
}

fun ByteArray.toLong(byteOrder: ByteOrder): Long {
    return when (byteOrder) {
        ByteOrder.BIG_ENDIAN -> ((this[0].toLong() and 0xFF) shl 56) or
                ((this[1].toLong() and 0xFF) shl 48) or
                ((this[2].toLong() and 0xFF) shl 40) or
                ((this[3].toLong() and 0xFF) shl 32) or
                ((this[4].toLong() and 0xFF) shl 24) or
                ((this[5].toLong() and 0xFF) shl 16) or
                ((this[6].toLong() and 0xFF) shl 8) or
                (this[7].toLong() and 0xFF)
        ByteOrder.LITTLE_ENDIAN -> ((this[7].toLong() and 0xFF) shl 56) or
                ((this[6].toLong() and 0xFF) shl 48) or
                ((this[5].toLong() and 0xFF) shl 40) or
                ((this[4].toLong() and 0xFF) shl 32) or
                ((this[3].toLong() and 0xFF) shl 24) or
                ((this[2].toLong() and 0xFF) shl 16) or
                ((this[1].toLong() and 0xFF) shl 8) or
                (this[0].toLong() and 0xFF)

        else -> 0
    }
}

fun ByteArray.toFloat(byteOrder: ByteOrder): Float {
    return ByteBuffer.wrap(this).order(byteOrder).getFloat()
}

fun ByteArray.toDouble(byteOrder: ByteOrder): Double {
    return Double.fromBits(this.toLong(byteOrder))
}

fun Short.toBytes(byteOrder: ByteOrder): ByteArray {
    return ByteArray(2).also {
        val (high, low) = ((this.toInt() shr 8) and 0xFF).toByte() to (this.toInt() and 0xFF).toByte()
        when (byteOrder) {
            ByteOrder.BIG_ENDIAN -> {
                it[0] = high
                it[1] = low
            }
            ByteOrder.LITTLE_ENDIAN -> {
                it[0] = low
                it[1] = high
            }
        }
    }
}

fun Int.toBytes(byteOrder: ByteOrder): ByteArray {
    return ByteArray(4).also {
        ByteBuffer.allocate(4).order(byteOrder).apply {
            putInt(this@toBytes)
            flip()
            get(it)
        }
    }
}

fun Long.toBytes(byteOrder: ByteOrder): ByteArray {
    return ByteArray(8).also {
        ByteBuffer.allocate(8).order(byteOrder).apply {
            putLong(this@toBytes)
            flip()
            get(it)
        }
    }
}

fun Float.toBytes(byteOrder: ByteOrder): ByteArray {
    return ByteBuffer.allocate(4).order(byteOrder).putFloat(this).array()
}

fun Double.toBytes(byteOrder: ByteOrder): ByteArray {
    return ByteBuffer.allocate(8).order(byteOrder).putDouble(this).array()
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