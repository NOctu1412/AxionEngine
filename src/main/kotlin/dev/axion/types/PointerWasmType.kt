package dev.axion.types

import com.github.salpadding.wasmer.Memory
import dev.axion.*
import dev.axion.structure.getPointerFromStructure
import dev.axion.structure.getStructureFromPointer
import java.nio.ByteOrder

//TODO: writing to pointers

class PointerWasmType(val axionEngine: AxionEngine, val ptr: Long = -1, val byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, val size: Long = -1L, private var autoFree: Boolean = true) : WasmType(ptr) {
    private val memory: Memory = axionEngine.getDefaultMemory();
    private val intPtr: Int = ptr.toInt();

    companion object {
        fun <C: Any> allocateStructurePointer(axionEngine: AxionEngine, structure: C, autoFree: Boolean = true): PointerWasmType {
            return getPointerFromStructure(axionEngine, structure, autoFree = autoFree);
        }

        inline fun <reified T> allocatePointer(axionEngine: AxionEngine, defaultValue: T, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, autoFree: Boolean = true): PointerWasmType {
            val result = allocatePointerA(axionEngine, getTypeSize<T>().toLong(), byteOrder, autoFree);
            result.write<T>(defaultValue);
            return result;
        }

        fun allocatePointerA(axionEngine: AxionEngine, size: Long, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, autoFree: Boolean = true): PointerWasmType {
            val ptrAddress = axionEngine.allocate(size);
            return PointerWasmType(axionEngine, ptrAddress, byteOrder, size = size, autoFree = autoFree);
        }
    }

    //i8
    fun getAsByte(offset: Int = 0): Byte {
        return memory.read(intPtr + offset, 1)[0];
    }

    fun writeByte(byte: Byte, offset: Int = 0) {
        memory.write(intPtr + offset, byteArrayOf(byte));
    }

    //char
    fun getAsChar(offset: Int = 0): Char {
        return getAsByte(offset).toInt().toChar();
    }

    fun writeChar(char: Char, offset: Int = 0) {
        writeByte(char.code.toByte(), offset);
    }

    //bool
    fun getAsBool(offset: Int = 0): Boolean {
        return getAsByte(offset) == 1.toByte();
    }

    fun writeBool(bool: Boolean, offset: Int = 0) {
        writeByte(if (bool) 1 else 0, offset);
    }

    //i16
    fun getAsShort(offset: Int = 0): Short {
        val bytes = memory.read(intPtr + offset, 2);
        return bytesToShort(bytes, byteOrder);
    }

    fun writeShort(short: Short, offset: Int = 0) {
        memory.write(intPtr + offset, shortToBytes(short, byteOrder));
    }

    //i32
    fun getAsInteger(offset: Int = 0): Int {
        val bytes = memory.read(intPtr + offset, 4);
        return bytesToInt(bytes, byteOrder);
    }

    fun writeInteger(int: Int, offset: Int = 0) {
        memory.write(intPtr + offset, intToBytes(int, byteOrder));
    }

    //i64
    fun getAsLong(offset: Int = 0): Long {
        val bytes = memory.read(intPtr + offset, 8);
        return bytesToLong(bytes, byteOrder);
    }

    fun writeLong(long: Long, offset: Int = 0) {
        memory.write(intPtr + offset, longToBytes(long, byteOrder));
    }

    //f32
    fun getAsFloat(offset: Int = 0): Float {
        val bytes = memory.read(intPtr + offset, 4);
        return bytesToFloat(bytes, byteOrder);
    }

    fun writeFloat(float: Float, offset: Int = 0) {
        memory.write(intPtr + offset, floatToBytes(float, byteOrder));
    }

    //f64
    fun getAsDouble(offset: Int = 0): Double {
        val bytes = memory.read(intPtr + offset, 8);
        return bytesToDouble(bytes, byteOrder);
    }

    fun writeDouble(double: Double, offset: Int = 0) {
        memory.write(intPtr + offset, doubleToBytes(double, byteOrder));
    }

    //*mut void
    fun getAsPointer(offset: Int = 0): PointerWasmType {
        val pointerAddress = get<Long>(offset);
        return PointerWasmType(axionEngine, pointerAddress, byteOrder);
    }

    fun writePointer(pointer: PointerWasmType, offset: Int = 0) {
        writeLong(pointer.ptr, offset);
    }

    //*mut String
    fun getAsString(offset: Int = 0): String {
        //getting the string//
        val stringAddress = get<PointerWasmType>(offset);
        val length = axionEngine.getStringObjectLength(stringAddress.ptr + offset);
        val buffer = axionEngine.getStringObjectBuffer(stringAddress.ptr + offset);
        val stringByteBuffer = axionEngine.getDefaultMemory().read(buffer.toInt(), length.toInt());
        return String(stringByteBuffer);
    }

    fun writeString(string: String, offset: Int = 0) {
        //writing the string//
        val stringPointer = axionEngine.allocate(string.length.toLong());
        axionEngine.getDefaultMemory().write(stringPointer.toInt(), string.toByteArray());
        val stringObjectPointer = axionEngine.createStringObject(stringPointer, string.length.toLong());
        writeLong(stringObjectPointer, offset); //writing the string object pointer address, it is the same as writePointer literally//
    }

    inline fun <reified T> get(offset: Int = 0): T {
        return get(T::class.java, offset) as T;
    }

    inline fun <reified T> write(value: T, offset: Int = 0) {
        writeA(value as Any, offset);
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
        val javaClass: Class<*> = value::class.java;
        when(javaClass) {
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
        val typeSize = getTypeSize(javaClass);
        return PointerWasmType(axionEngine, ptr+index*typeSize, byteOrder).get(javaClass);
    }

    fun writeArrayElement(value: Any, index: Int) {
        val typeSize = getTypeSize(value::class.java);
        PointerWasmType(axionEngine, ptr+index*typeSize, byteOrder).write(value, 0);
    }

    inline fun <reified T> getArrayElement(index: Int): T {
        return getArrayElement(T::class.java, index) as T;
    }

    fun <C: Any> getThisPointerAsStructure(structureJavaClass: Class<C>): C {
        return getStructureFromPointer(this, structureJavaClass);
    }

    override fun toLong(axionEngine: AxionEngine): Long {
        return ptr;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        if(size == -1L || !autoFree) return;
        axionEngine.free(ptr, size);
    }
}