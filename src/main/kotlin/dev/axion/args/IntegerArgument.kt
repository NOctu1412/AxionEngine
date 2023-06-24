package dev.axion.args

import dev.axion.AxionEngine

class IntegerArgument(private val value: Int) : Argument() {
    override fun toLong(axionEngine: AxionEngine): Long {
        return value.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}