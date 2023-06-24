package dev.axion.types

import dev.axion.AxionEngine

class BooleanWasmType(private val boolean: Boolean) : WasmType(boolean) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return if(boolean) 1L else 0L;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}