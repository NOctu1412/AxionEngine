package dev.axion.args

import dev.axion.AxionEngine

class BooleanArgument(private val boolean: Boolean) : Argument(boolean) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return if(boolean) 1L else 0L;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}