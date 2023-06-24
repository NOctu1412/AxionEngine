package dev.axion.types

import dev.axion.AxionEngine

class LongWasmType(private val long: Long) : WasmType(long) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return long;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}