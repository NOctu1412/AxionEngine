package dev.axion.args

import dev.axion.AxionEngine

class ByteArgument(private val byte: Byte) : Argument() {
    override fun toLong(axionEngine: AxionEngine): Long {
        return byte.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}