package dev.axion.args

import dev.axion.AxionEngine

class DoubleArgument(private val double: Double) : Argument(double) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return double.toBits();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to-do//
    }
}