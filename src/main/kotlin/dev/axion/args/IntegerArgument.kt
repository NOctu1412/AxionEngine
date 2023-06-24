package dev.axion.args

import dev.axion.AxionEngine

class IntegerArgument(private val int: Int) : Argument(int) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return int.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //do nothing//
    }
}