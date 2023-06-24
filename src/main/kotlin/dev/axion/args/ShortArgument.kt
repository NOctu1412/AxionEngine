package dev.axion.args

import dev.axion.AxionEngine

class ShortArgument(private val short: Short) : Argument(short) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return short.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}