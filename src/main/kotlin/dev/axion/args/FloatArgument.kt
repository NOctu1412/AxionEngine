package dev.axion.args

import dev.axion.AxionEngine

class FloatArgument(private val value: Float) : Argument() {
    override fun toLong(axionEngine: AxionEngine): Long {
        return value.toBits().toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to-do//
    }
}