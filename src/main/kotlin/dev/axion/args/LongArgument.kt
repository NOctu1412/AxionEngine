package dev.axion.args

import dev.axion.AxionEngine

class LongArgument(private val long: Long) : Argument(long) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return long;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}