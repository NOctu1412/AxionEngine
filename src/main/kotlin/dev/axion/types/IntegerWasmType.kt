package dev.axion.types

import dev.axion.AxionEngine

class IntegerWasmType(private val int: Int) : WasmType(int) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return int.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}