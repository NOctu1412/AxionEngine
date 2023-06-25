package dev.axion

import com.github.salpadding.wasmer.Instance
import com.github.salpadding.wasmer.Memory
import com.github.salpadding.wasmer.Natives
import com.github.salpadding.wasmer.Options
import dev.axion.types.WasmType
import dev.axion.types.WasmTypes
import dev.axion.types.valueFromLong

private var nativesInitialized = false;

class AxionEngine(wasmBinary: ByteArray, imports: List<WasmImport>) {
    private var wasmerInstance: Instance;

    init {
        if(!nativesInitialized) {
            nativesInitialized = true;
            Natives.initialize(0x500); //number of axion engines that can run simultaneously//
        }
        wasmerInstance = Instance.create(wasmBinary, Options.empty(), imports);
    }

    fun allocate(size: Long): Long {
        return callExport("alloc_rust", longArrayOf(size))[0];
    }

    fun createStringObject(ptr: Long, size: Long): Long {
        return callExport("create_string", longArrayOf(ptr, size))[0];
    }

    fun getStringObjectLength(ptr: Long): Long {
        return callExport("get_string_length", longArrayOf(ptr))[0];
    }

    fun getStringObjectBuffer(ptr: Long): Long {
        return callExport("get_string_buffer", longArrayOf(ptr))[0];
    }

    fun free(ptr: Long, size: Long) {
        callExport("free_rust", longArrayOf(ptr, size));
    }

    fun destroyStringObject(ptr: Long) {
        callExport("destroy_string", longArrayOf(ptr));
    }

    private fun callExport(name: String, args: LongArray): LongArray {
        return wasmerInstance.execute(name, args);
    }

    fun callExport(name: String, returnType: WasmTypes, vararg args: WasmType): WasmType {
        return callExport(name, listOf(returnType), *args)[0];
    }

    fun callExport(name: String, returnsType: List<WasmTypes>, vararg args: WasmType): List<WasmType> {
        //build long list from arguments//
        val longArgs = LongArray(args.size);
        for (i in args.indices) {
            longArgs[i] = args[i].toLong(this);
        }

        val result = callExport(name, longArgs);

        //clean the used memory//
        for (i in args.indices) {
            args[i].cleanMemory(this);
        }

        //build return values//
        val returnValues = ArrayList<WasmType>();
        for (i in result.indices) {
            returnValues.add(valueFromLong(this, returnsType[i], result[i]));
        }

        return returnValues;
    }

    fun getDefaultMemory(): Memory {
        return getMemory("memory");
    }

    fun getMemory(name: String): Memory {
        return wasmerInstance.getMemory(name);
    }

    fun close() {
        wasmerInstance.close();
    }
}