package dev.axion.types

import dev.axion.AxionEngine

class DoubleWasmType(private val double: Double) : WasmType(double) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return double.toBits();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to-do//
    }
}