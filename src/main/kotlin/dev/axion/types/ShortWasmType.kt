package dev.axion.types

import dev.axion.AxionEngine

class ShortWasmType(private val short: Short) : WasmType(short) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return short.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}