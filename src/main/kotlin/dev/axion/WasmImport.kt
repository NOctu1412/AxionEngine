package dev.axion

import com.github.salpadding.wasmer.HostFunction
import com.github.salpadding.wasmer.Instance
import com.github.salpadding.wasmer.ValType
import dev.axion.extension.toWasmType
import dev.axion.types.EnumWasmType
import dev.axion.types.WasmType

open class WasmImport(
    private val name: String,
    private val returnType: EnumWasmType,
    private val parameterTypes: List<EnumWasmType>,
    val exec: (Instance, List<WasmType>) -> WasmType?,
) : HostFunction {
    private lateinit var axionEngine: AxionEngine

    fun setEngine(engine: AxionEngine) {
        axionEngine = engine
    }

    override fun getName(): String {
        return name
    }

    override fun execute(ins: Instance, args: LongArray): LongArray {
        val argsList = arrayListOf<WasmType>()
        for((index, arg) in args.withIndex()) {
            argsList.add(arg.toWasmType(axionEngine, parameterTypes[index]))
        }

        val result = exec(ins, argsList) ?: return longArrayOf()

        return longArrayOf(result.toLong(axionEngine))
    }

    override fun getParams(): List<ValType> {
        val paramsList = arrayListOf<ValType>()
        for(param in parameterTypes) {
            paramsList.add(param.toValType())
        }
        return paramsList
    }

    override fun getRet(): List<ValType> {
        return listOf(returnType.toValType())
    }
}