package dev.axion.args

import dev.axion.AxionEngine

class ShortArgument(private val value: Short) : Argument() {
    override fun toLong(axionEngine: AxionEngine): Long {
        return value.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}