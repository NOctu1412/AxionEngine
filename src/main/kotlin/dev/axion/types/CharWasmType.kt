package dev.axion.types

import dev.axion.AxionEngine

class CharWasmType(private val char: Char) : WasmType(char) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return char.code.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to clean//
    }
}