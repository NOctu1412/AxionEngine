package dev.axion

import dev.axion.types.WasmType
import dev.axion.types.EnumWasmType
import dev.axion.extension.toWasmType
import dev.axion.imports.WasmImport
import org.wasmer.Imports
import org.wasmer.Instance
import org.wasmer.Memory
import org.wasmer.Module

class AxionEngine(wasmBinary: ByteArray, vararg imports: WasmImport) {
    private val wasmModule: Module
    private val wasmInstance: Instance

    init {
        wasmModule = Module(wasmBinary)
        wasmInstance = wasmModule.instantiate(Imports.from(imports.map { importToWasmerImportSpec(it) }, wasmModule))
    }

    fun allocate(size: Int): Int {
        return callExport("alloc_rust", arrayOf(size))[0] as Int
    }

    fun createStringObject(ptr: Int, size: Int): Int {
        return callExport("create_string", arrayOf(ptr, size))[0] as Int
    }

    fun getStringObjectLength(ptr: Int): Int {
        return callExport("get_string_length", arrayOf(ptr))[0] as Int
    }

    //returns pointer
    fun getStringObjectBuffer(ptr: Int): Int {
        return callExport("get_c_string_from_string", arrayOf(ptr))[0] as Int
    }

    fun free(ptr: Int, size: Int) {
        callExport("free_rust", arrayOf(ptr, size))
    }

    fun destroyStringObject(ptr: Int) {
        callExport("destroy_string", arrayOf(ptr))
    }

    private fun callExport(name: String, args: Array<Any>): Array<Any> {
        return wasmInstance.exports.getFunction(name).apply(*args) ?: return emptyArray<Any>()
    }

    inline fun <reified T: WasmType> callExport(name: String, vararg args: WasmType): T {
        return callExport(name, EnumWasmType.from<T>(), *args) as T
    }

    fun callExport(name: String, vararg args: WasmType) {
        callExport(name, EnumWasmType.VOID, *args)
    }

    fun callExport(name: String, returnType: EnumWasmType, vararg args: WasmType): WasmType? {
        val result = callExport(name, listOf(returnType), *args)
        if(result.isEmpty()) return null
        return result[0]
    }

    fun callExport(name: String, returnsType: List<EnumWasmType>, vararg args: WasmType): List<WasmType> {
        //build long list from arguments//
        val valueArgs = arrayListOf<Any>()
        for (i in args.indices) {
            valueArgs.add(args[i].toWasmerValue(this))
        }

        val result = callExport(name, valueArgs.toTypedArray())

        //clean the used memory//
        for (i in args.indices) {
            args[i].cleanMemory(this)
        }

        //build return values//
        val returnValues = ArrayList<WasmType>()
        for (i in result.indices) {
            returnValues.add(result[i].toWasmType(this, returnsType[i]))
        }

        return returnValues
    }

    private fun importToWasmerImportSpec(import: WasmImport): Imports.Spec {
        val argsTypes = import.argsTypes.map { it.toValueType() }

        val function = fun(args: List<Number>): List<Number> {
            val argsValues = ArrayList<WasmType>()
            for (i in args.indices) {
                argsValues.add(args[i].toWasmType(this, import.argsTypes[i]))
            }

            val result = import.callback(this, argsValues.toTypedArray()) ?: return emptyList()

            return listOf(result.toWasmerValue(this) as Number)
        }

        return Imports.Spec(import.namespace,
            import.name,
            function,
            argsTypes,
            if (import.returnType == EnumWasmType.VOID) emptyList()
            else listOf(import.returnType.toValueType()))
    }

    fun getDefaultMemory(): Memory {
        return getMemory("memory")
    }

    fun getMemory(name: String): Memory {
        return wasmInstance.exports.getMemory(name)
    }

    fun close() {
        wasmInstance.close()
    }
}