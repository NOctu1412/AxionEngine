package dev.axion.types

import dev.axion.AxionEngine

class ByteWasmType(private val byte: Byte) : WasmType(byte) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return byte.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}