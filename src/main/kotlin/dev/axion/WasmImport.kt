package dev.axion

import com.github.salpadding.wasmer.HostFunction
import com.github.salpadding.wasmer.Instance
import com.github.salpadding.wasmer.ValType

class WasmImport
    (private val name: String, private val params: List<ValType>, private val returns: List<ValType>, val exec: (Instance?, LongArray?) -> LongArray)
    : HostFunction {

    override fun getName(): String {
        return name;
    }

    override fun execute(ins: Instance?, args: LongArray?): LongArray {
        return exec(ins, args);
    }

    override fun getParams(): MutableList<ValType> {
        return params.toMutableList();
    }

    override fun getRet(): MutableList<ValType> {
        return returns.toMutableList();
    }

}