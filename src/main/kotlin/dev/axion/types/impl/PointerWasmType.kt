package dev.axion.types.impl

import dev.axion.*
import dev.axion.extension.*
import dev.axion.structure.getPointerFromStructure
import dev.axion.structure.getStructureFromPointer
import dev.axion.types.WasmType
import org.wasmer.Memory
import java.nio.ByteOrder

class PointerWasmType(
    private val engine: AxionEngine,
    val ptr: Int,
    val size: Int = -1,
    private val byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN,
    private var autoFree: Boolean = true
) : WasmType(
    ptr,
    toWasmerValue = { ptr },
    cleanMemory = cleanMemory@{
        if(size == -1 || !autoFree) return@cleanMemory
        engine.free(ptr, size)
    }
) {

    private val memory: Memory = engine.getDefaultMemory()

    companion object {
        inline fun <reified C: Any> allocateArray(engine: AxionEngine, array: Array<C>, autoFree: Boolean = true): PointerWasmType {
            if(array.isEmpty()) throw IllegalArgumentException("Array cannot be empty")

            val result = allocatePointerA(
                engine = engine,
                size = array.size * array[0]::class.java.getWasmTypeSize(),
                autoFree = autoFree
            )
            array.forEachIndexed { index, value ->
                result.writeArrayElement(value, index)
            }
            return result
        }

        fun <C: Any> allocateStructurePointer(engine: AxionEngine, structure: C, autoFree: Boolean = true): PointerWasmType {
            return getPointerFromStructure(engine, structure, autoFree = autoFree)
        }

        inline fun <reified T> allocatePointer(engine: AxionEngine, defaultValue: T, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, autoFree: Boolean = true): PointerWasmType {
            val result = allocatePointerA(
                engine = engine,
                size = defaultValue!!::class.java.getWasmTypeSize(),
                byteOrder = byteOrder,
                autoFree = autoFree
            )
            result.write<T>(defaultValue)
            return result
        }

        fun allocatePointerA(engine: AxionEngine, size: Int, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, autoFree: Boolean = true): PointerWasmType {
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
    fun getAsByte(offset: Int = 0) = memory.buffer().get(ptr + offset)

    fun writeByte(byte: Byte, offset: Int = 0) {
        memory.buffer().put(ptr + offset, byte)
    }

    //char
    fun getAsChar(offset: Int = 0) = memory.buffer().getChar(offset)

    fun writeChar(char: Char, offset: Int = 0) {
        writeByte(char.code.toByte(), offset)
    }

    //bool
    fun getAsBool(offset: Int = 0) = getAsByte(offset) == 1.toByte()

    fun writeBool(bool: Boolean, offset: Int = 0) {
        writeByte(if (bool) 1 else 0, offset)
    }

    //i16
    fun getAsShort(offset: Int = 0) = memory.buffer().getShort(ptr + offset)

    fun writeShort(short: Short, offset: Int = 0) {
        memory.buffer().putShort(ptr + offset, short)
    }

    //i32
    fun getAsInteger(offset: Int = 0) = memory.buffer().getInt(ptr + offset)

    fun writeInteger(int: Int, offset: Int = 0) {
        memory.buffer().putInt(ptr + offset, int)
    }

    //i64
    fun getAsLong(offset: Int = 0) = memory.buffer().getLong(ptr + offset)

    fun writeLong(long: Long, offset: Int = 0) {
        memory.buffer().putLong(ptr + offset, long)
    }

    //f32
    fun getAsFloat(offset: Int = 0) = memory.buffer().getFloat(ptr + offset)

    fun writeFloat(float: Float, offset: Int = 0) {
        memory.buffer().putFloat(ptr + offset, float)
    }

    //f64
    fun getAsDouble(offset: Int = 0) = memory.buffer().getDouble(ptr + offset)

    fun writeDouble(double: Double, offset: Int = 0) {
        memory.buffer().putDouble(ptr + offset, double)
    }

    //*mut void
    fun getAsPointer(offset: Int = 0): PointerWasmType {
        val pointerAddress = get<Int>(ptr + offset)
        return PointerWasmType(engine = engine, ptr = pointerAddress, byteOrder = byteOrder)
    }

    fun writePointer(pointer: PointerWasmType, offset: Int = 0) {
        writeInteger(pointer.ptr, offset)
    }

    //*mut String
    fun getAsString(offset: Int = 0): String {
        //getting the string//
        val stringAddress = get<PointerWasmType>(offset)

        return engine.let {
            val length = it.getStringObjectLength(stringAddress.ptr + offset)
            val buffer = it.getStringObjectBuffer(stringAddress.ptr + offset)
            val stringBuffer = byteArrayOf()
            memory.buffer().get(stringBuffer, 0, buffer)
            String(stringBuffer, 0, length)
        }
    }

    fun writeString(string: String, offset: Int = 0) {
        //writing the string//
        engine.apply {
            val stringPointer = allocate(string.length)
            for(i in string.indices) {
                getDefaultMemory().buffer().put(stringPointer + i, string[i].code.toByte())
            }
            val stringObjectPointer = createStringObject(stringPointer, string.length)
            writeInteger(stringObjectPointer, offset) //writing the string object pointer address, it is the same as writePointer literally//
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