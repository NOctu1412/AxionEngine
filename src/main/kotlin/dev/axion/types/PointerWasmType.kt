package dev.axion.types

import com.github.salpadding.wasmer.Memory
import dev.axion.*
import dev.axion.structure.getStructureFromPointer
import java.nio.ByteOrder

//TODO: writing to pointers

class PointerWasmType(val axionEngine: AxionEngine, val ptr: Long = -1, val byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN) : WasmType(ptr) {
    private val memory: Memory = axionEngine.getDefaultMemory();
    private val intPtr: Int = ptr.toInt();

    //i8
    fun getAsByte(offset: Int = 0): Byte {
        return memory.read(intPtr + offset, 1)[0];
    }

    //char
    fun getAsChar(offset: Int = 0): Char {
        return getAsByte(offset).toInt().toChar();
    }

    //bool
    fun getAsBool(offset: Int = 0): Boolean {
        return getAsByte(offset) == 1.toByte();
    }

    //i16
    fun getAsShort(offset: Int = 0): Short {
        val bytes = memory.read(intPtr + offset, 2);
        return bytesToShort(bytes, byteOrder);
    }

    //i32
    fun getAsInteger(offset: Int = 0): Int {
        val bytes = memory.read(intPtr + offset, 4);
        return bytesToInt(bytes, byteOrder);
    }

    //i64
    fun getAsLong(offset: Int = 0): Long {
        val bytes = memory.read(intPtr + offset, 8);
        return bytesToLong(bytes, byteOrder);
    }

    //f32
    fun getAsFloat(offset: Int = 0): Float {
        val bytes = memory.read(intPtr + offset, 4);
        return bytesToFloat(bytes, byteOrder);
    }

    //f64
    fun getAsDouble(offset: Int = 0): Double {
        val bytes = memory.read(intPtr + offset, 8);
        return bytesToDouble(bytes, byteOrder);
    }

    //*mut void
    fun getAsPointer(offset: Int = 0): PointerWasmType {
        val pointerAddress = get<Long>(offset);
        return PointerWasmType(axionEngine, pointerAddress, byteOrder);
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

    inline fun <reified T> get(offset: Int = 0): T {
        return get(T::class.java, offset) as T;
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

    fun getArrayElement(javaClass: Class<*>, index: Int): Any {
        val typeSize = getTypeSize(javaClass);
        return PointerWasmType(axionEngine, ptr+index*typeSize, byteOrder).get(javaClass);
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

    }
}