package dev.axion.args

import dev.axion.AxionEngine

class FloatArgument(private val float: Float) : Argument(float) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return float.toBits().toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to-do//
    }
}