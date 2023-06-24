package dev.axion.args

import dev.axion.AxionEngine

class DoubleArgument(private val value: Double) : Argument() {
    override fun toLong(axionEngine: AxionEngine): Long {
        return value.toBits();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to-do//
    }
}