package dev.axion.types.impl

import com.github.salpadding.wasmer.Memory
import dev.axion.*
import dev.axion.extension.*
import dev.axion.structure.getPointerFromStructure
import dev.axion.structure.getStructureFromPointer
import dev.axion.types.WasmType
import java.nio.ByteOrder

//TODO: writing to pointers

class PointerWasmType(
    private val engine: AxionEngine,
    val ptr: Long = -1,
    val size: Long = -1L,
    private val byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN,
    private var autoFree: Boolean = true
) : WasmType(
    ptr,
    toLong = { ptr },
    cleanMemory = cleanMemory@{
        if(size == -1L || !autoFree) return@cleanMemory
        engine.free(ptr, size)
    }
) {

    private val memory: Memory = engine.getDefaultMemory()
    private val intPtr: Int = ptr.toInt()

    companion object {
        fun <C: Any> allocateStructurePointer(engine: AxionEngine, structure: C, autoFree: Boolean = true): PointerWasmType {
            return getPointerFromStructure(engine, structure, autoFree = autoFree)
        }

        inline fun <reified T> allocatePointer(engine: AxionEngine, defaultValue: T, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, autoFree: Boolean = true): PointerWasmType {
            val result = allocatePointerA(
                engine = engine,
                size = defaultValue!!::class.java.getWasmTypeSize().toLong(),
                byteOrder = byteOrder,
                autoFree = autoFree
            )
            result.write<T>(defaultValue)
            return result
        }

        fun allocatePointerA(engine: AxionEngine, size: Long, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, autoFree: Boolean = true): PointerWasmType {
            val ptrAddress = engine.allocate(size)
            return PointerWasmType(
                engine = engine,
                ptr = ptrAddress,
                byteOrder = byteOrder,
                size = size,
                autoFree = autoFree
            )
        }
    }

    //i8
    fun getAsByte(offset: Int = 0) = memory.read(intPtr + offset, 1)[0]

    fun writeByte(byte: Byte, offset: Int = 0) {
        memory.write(intPtr + offset, byteArrayOf(byte))
    }

    //char
    fun getAsChar(offset: Int = 0) = getAsByte(offset).toInt().toChar()

    fun writeChar(char: Char, offset: Int = 0) {
        writeByte(char.code.toByte(), offset)
    }

    //bool
    fun getAsBool(offset: Int = 0) = getAsByte(offset) == 1.toByte()

    fun writeBool(bool: Boolean, offset: Int = 0) {
        writeByte(if (bool) 1 else 0, offset)
    }

    //i16
    fun getAsShort(offset: Int = 0) = memory.read(intPtr + offset, 2).toShort(byteOrder)

    fun writeShort(short: Short, offset: Int = 0) {
        memory.write(intPtr + offset, short.toBytes(byteOrder))
    }

    //i32
    fun getAsInteger(offset: Int = 0) = memory.read(intPtr + offset, 4).toInt(byteOrder)

    fun writeInteger(int: Int, offset: Int = 0) {
        memory.write(intPtr + offset, int.toBytes(byteOrder))
    }

    //i64
    fun getAsLong(offset: Int = 0) = memory.read(intPtr + offset, 8).toLong(byteOrder)

    fun writeLong(long: Long, offset: Int = 0) {
        memory.write(intPtr + offset, long.toBytes(byteOrder))
    }

    //f32
    fun getAsFloat(offset: Int = 0) = memory.read(intPtr + offset, 4).toFloat(byteOrder)

    fun writeFloat(float: Float, offset: Int = 0) {
        memory.write(intPtr + offset, float.toBytes(byteOrder))
    }

    //f64
    fun getAsDouble(offset: Int = 0) = memory.read(intPtr + offset, 8).toDouble(byteOrder)

    fun writeDouble(double: Double, offset: Int = 0) {
        memory.write(intPtr + offset, double.toBytes(byteOrder))
    }

    //*mut void
    fun getAsPointer(offset: Int = 0): PointerWasmType {
        val pointerAddress = get<Long>(offset)
        return PointerWasmType(engine = engine, ptr = pointerAddress, byteOrder = byteOrder)
    }

    fun writePointer(pointer: PointerWasmType, offset: Int = 0) {
        writeLong(pointer.ptr, offset)
    }

    //*mut String
    fun getAsString(offset: Int = 0): String {
        //getting the string//
        val stringAddress = get<PointerWasmType>(offset)

        return engine.let {
            val length = it.getStringObjectLength(stringAddress.ptr + offset)
            val buffer = it.getStringObjectBuffer(stringAddress.ptr + offset)
            engine.getDefaultMemory().read(buffer.toInt(), length.toInt()).toString(Charsets.UTF_8)
        }
    }

    fun writeString(string: String, offset: Int = 0) {
        //writing the string//
        engine.apply {
            val stringPointer = allocate(string.length.toLong())
            getDefaultMemory().write(stringPointer.toInt(), string.toByteArray())
            val stringObjectPointer = createStringObject(stringPointer, string.length.toLong())
            writeLong(stringObjectPointer, offset) //writing the string object pointer address, it is the same as writePointer literally//
        }
    }

    inline fun <reified T> get(offset: Int = 0): T {
        return get(T::class.java, offset) as T
    }

    inline fun <reified T> write(value: T, offset: Int = 0) {
        writeA(value as Any, offset)
    }

    fun get(javaClass: Class<*>, offset: Int = 0): Any {
        return when (javaClass) {
            Long::class.java, java.lang.Long::class.java -> getAsLong(offset)
            Byte::class.java, java.lang.Byte::class.java -> getAsByte(offset)
            Char::class.java, java.lang.Character::class.java -> getAsChar(offset)
            Boolean::class.java, java.lang.Boolean::class.java -> getAsBool(offset)
            Short::class.java, java.lang.Short::class.java -> getAsShort(offset)
            Int::class.java, java.lang.Integer::class.java -> getAsInteger(offset)
            Float::class.java, java.lang.Float::class.java -> getAsFloat(offset)
            Double::class.java, java.lang.Double::class.java -> getAsDouble(offset)
            String::class.java, java.lang.String::class.java -> getAsString(offset)
            PointerWasmType::class.java -> getAsPointer(offset)
            else -> throw IllegalArgumentException("Unsupported type: ${javaClass.name}")
        }
    }

    fun writeA(value: Any, offset: Int = 0) {
        when(value::class.java) {
            Long::class.java, java.lang.Long::class.java -> writeLong(value as Long, offset)
            Byte::class.java, java.lang.Byte::class.java -> writeByte(value as Byte, offset)
            Char::class.java, java.lang.Character::class.java -> writeChar(value as Char, offset)
            Boolean::class.java, java.lang.Boolean::class.java -> writeBool(value as Boolean, offset)
            Short::class.java, java.lang.Short::class.java -> writeShort(value as Short, offset)
            Int::class.java, java.lang.Integer::class.java -> writeInteger(value as Int, offset)
            Float::class.java, java.lang.Float::class.java -> writeFloat(value as Float, offset)
            Double::class.java, java.lang.Double::class.java -> writeDouble(value as Double, offset)
            String::class.java, java.lang.String::class.java -> writeString(value as String, offset)
            PointerWasmType::class.java -> writePointer(value as PointerWasmType, offset)
            else -> throw IllegalArgumentException("Unsupported type: ${javaClass.name}")
        }
    }

    fun getArrayElement(javaClass: Class<*>, index: Int): Any {
        val typeSize = javaClass.getWasmTypeSize()
        return PointerWasmType(
            engine = engine,
            ptr = ptr + index *typeSize,
            byteOrder = byteOrder
        ).get(javaClass)
    }

    fun writeArrayElement(value: Any, index: Int) {
        val typeSize = value::class.java.getWasmTypeSize()
        PointerWasmType(
            engine = engine,
            ptr = ptr + index * typeSize,
            byteOrder = byteOrder
        ).write(value, 0)
    }

    inline fun <reified T> getArrayElement(index: Int): T {
        return getArrayElement(T::class.java, index) as T
    }

    fun <C: Any> getThisPointerAsStructure(structureJavaClass: Class<C>): C {
        return getStructureFromPointer(this, structureJavaClass)
    }
}