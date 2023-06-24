package dev.axion.args

import dev.axion.AxionEngine

class LongArgument(private val value: Long) : Argument() {
    override fun toLong(axionEngine: AxionEngine): Long {
        return value;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}