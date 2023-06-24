package dev.axion.types

import com.github.salpadding.wasmer.Memory
import dev.axion.*
import java.nio.ByteOrder

//TODO: writing to pointers

class PointerWasmType(val axionEngine: AxionEngine, val ptr: Long = -1, val byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN) : WasmType(ptr) {
    private val memory: Memory = axionEngine.getDefaultMemory();
    private val intPtr: Int = ptr.toInt();

    fun getAsByte(): Byte {
        return memory.read(intPtr, 1)[0];
    }

    fun getAsChar(): Char {
        return getAsByte().toInt().toChar();
    }

    fun getAsBool(): Boolean {
        return getAsByte() == 1.toByte();
    }

    fun getAsShort(): Short {
        val bytes = memory.read(intPtr, 2);
        return bytesToShort(bytes, byteOrder);
    }

    fun getAsInteger(): Int {
        val bytes = memory.read(intPtr, 4);
        return bytesToInt(bytes, byteOrder);
    }

    fun getAsLong(): Long {
        val bytes = memory.read(intPtr, 8);
        return bytesToLong(bytes, byteOrder);
    }

    fun getAsFloat(): Float {
        val bytes = memory.read(intPtr, 4);
        return bytesToFloat(bytes, byteOrder);
    }

    fun getAsDouble(): Double {
        val bytes = memory.read(intPtr, 8);
        return bytesToDouble(bytes, byteOrder);
    }

    fun getAsPointer(): PointerWasmType {
        val pointerAddress = get<Long>();
        return PointerWasmType(axionEngine, pointerAddress, byteOrder);
    }

    //rust string object
    fun getAsString(): String {
        //getting the string//
        val length = axionEngine.getStringObjectLength(ptr);
        val buffer = axionEngine.getStringObjectBuffer(ptr);
        val stringByteBuffer = axionEngine.getDefaultMemory().read(buffer.toInt(), length.toInt());
        val string = String(stringByteBuffer);
        //freeing the string//
        axionEngine.free(buffer, length);
        axionEngine.destroyStringObject(ptr);
        return StringWasmType(string).value as String;
    }

    inline fun <reified T> get(): T {
        return when (T::class) {
            Byte::class -> getAsByte() as T
            Char::class -> getAsChar() as T
            Boolean::class -> getAsBool() as T
            Short::class -> getAsShort() as T
            Int::class -> getAsInteger() as T
            Long::class -> getAsLong() as T
            Float::class -> getAsFloat() as T
            Double::class -> getAsDouble() as T
            String::class -> getAsString() as T
            PointerWasmType::class -> getAsPointer() as T
            else -> throw IllegalArgumentException("Unsupported type: ${T::class.simpleName}")
        }
    }

    inline fun <reified T> getArrayElement(index: Int): T {
        val typeSize = getTypeSize<T>();
        return PointerWasmType(axionEngine, ptr+index*typeSize, byteOrder).get<T>();
    }

    override fun toLong(axionEngine: AxionEngine): Long {
        return ptr;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {

    }
}