package dev.axion.types

import dev.axion.AxionEngine

class FloatWasmType(private val float: Float) : WasmType(float) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return float.toBits().toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to-do//
    }
}