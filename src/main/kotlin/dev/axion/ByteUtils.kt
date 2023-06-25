package dev.axion

import dev.axion.types.PointerWasmType
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun bytesToShort(bytes: ByteArray, byteOrder: ByteOrder): Short {
    val high: Short = (bytes[0].toInt() and 0xFF).toShort()
    val low: Short = (bytes[1].toInt() and 0xFF).toShort()

    return when (byteOrder) {
        ByteOrder.BIG_ENDIAN -> (high.toInt() shl 8 or low.toInt()).toShort()
        ByteOrder.LITTLE_ENDIAN -> (low.toInt() shl 8 or high.toInt()).toShort()
        else -> 0;
    }
}

fun bytesToInt(bytes: ByteArray, byteOrder: ByteOrder): Int {
    return ByteBuffer.wrap(bytes).order(byteOrder).getInt();
}

fun bytesToLong(bytes: ByteArray, byteOrder: ByteOrder): Long {
    return when (byteOrder) {
        ByteOrder.BIG_ENDIAN -> ((bytes[0].toLong() and 0xFF) shl 56) or
                ((bytes[1].toLong() and 0xFF) shl 48) or
                ((bytes[2].toLong() and 0xFF) shl 40) or
                ((bytes[3].toLong() and 0xFF) shl 32) or
                ((bytes[4].toLong() and 0xFF) shl 24) or
                ((bytes[5].toLong() and 0xFF) shl 16) or
                ((bytes[6].toLong() and 0xFF) shl 8) or
                (bytes[7].toLong() and 0xFF)
        ByteOrder.LITTLE_ENDIAN -> ((bytes[7].toLong() and 0xFF) shl 56) or
                ((bytes[6].toLong() and 0xFF) shl 48) or
                ((bytes[5].toLong() and 0xFF) shl 40) or
                ((bytes[4].toLong() and 0xFF) shl 32) or
                ((bytes[3].toLong() and 0xFF) shl 24) or
                ((bytes[2].toLong() and 0xFF) shl 16) or
                ((bytes[1].toLong() and 0xFF) shl 8) or
                (bytes[0].toLong() and 0xFF)

        else -> 0;
    }
}

fun bytesToFloat(bytes: ByteArray, byteOrder: ByteOrder): Float {
    return Float.fromBits(bytesToInt(bytes, byteOrder))
}

fun bytesToDouble(bytes: ByteArray, byteOrder: ByteOrder): Double {
    return Double.fromBits(bytesToLong(bytes, byteOrder))
}

fun shortToBytes(value: Short, byteOrder: ByteOrder): ByteArray {
    val bytes = ByteArray(2)
    val high = ((value.toInt() shr 8) and 0xFF).toByte()
    val low = (value.toInt() and 0xFF).toByte()

    when (byteOrder) {
        ByteOrder.BIG_ENDIAN -> {
            bytes[0] = high
            bytes[1] = low
        }
        ByteOrder.LITTLE_ENDIAN -> {
            bytes[0] = low
            bytes[1] = high
        }
    }

    return bytes
}

fun intToBytes(value: Int, byteOrder: ByteOrder): ByteArray {
    val bytes = ByteArray(4)
    val buffer = ByteBuffer.allocate(4).order(byteOrder)
    buffer.putInt(value)
    buffer.flip();
    buffer.get(bytes)
    return bytes
}

fun longToBytes(value: Long, byteOrder: ByteOrder): ByteArray {
    val bytes = ByteArray(8)
    val buffer = ByteBuffer.allocate(8).order(byteOrder)
    buffer.putLong(value)
    buffer.flip();
    buffer.get(bytes)
    return bytes
}

fun floatToBytes(value: Float, byteOrder: ByteOrder): ByteArray {
    return intToBytes(value.toRawBits(), byteOrder)
}

fun doubleToBytes(value: Double, byteOrder: ByteOrder): ByteArray {
    return longToBytes(value.toRawBits(), byteOrder)
}

inline fun <reified T> getTypeSize(): Int {
    return getTypeSize(T::class.java);
}

fun getTypeSize(typeClass: Class<*>): Int {
    return when (typeClass) {
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
        else -> throw IllegalArgumentException("Unsupported type: ${typeClass.simpleName}")
    }
}