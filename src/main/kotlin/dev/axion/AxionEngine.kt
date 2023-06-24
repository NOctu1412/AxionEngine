package dev.axion

import com.github.salpadding.wasmer.Instance
import com.github.salpadding.wasmer.Memory
import com.github.salpadding.wasmer.Natives
import com.github.salpadding.wasmer.Options
import dev.axion.args.Argument

class AxionEngine constructor(wasmBinary: ByteArray, imports: List<WasmImport>) {
    private var wasmerInstance: Instance;

    init {
        Natives.initialize(0x500);
        wasmerInstance = Instance.create(wasmBinary, Options.empty(), imports);
    }

    fun allocate(size: Long): Long {
        return callExport("alloc_rust", longArrayOf(size))[0];
    }

    fun createStringObject(ptr: Long, size: Long): Long {
        return callExport("create_string", longArrayOf(ptr, size))[0];
    }

    fun free(ptr: Long, size: Long) {
        callExport("free_rust", longArrayOf(ptr, size));
    }

    fun destroyStringObject(ptr: Long) {
        callExport("destroy_string", longArrayOf(ptr));
    }

    fun callExport(name: String, args: LongArray): LongArray {
        return wasmerInstance.execute(name, args);
    }

    fun callExport(name: String, vararg args: Argument): LongArray {
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

        return result;
    }

    fun getDefaultMemory(): Memory {
        return wasmerInstance.getMemory("memory");
    }

    fun close() {
        wasmerInstance?.close();
    }
}