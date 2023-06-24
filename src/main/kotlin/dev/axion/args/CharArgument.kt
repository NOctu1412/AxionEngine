package dev.axion.args

import dev.axion.AxionEngine

class CharArgument(private val char: Char) : Argument(char) {
    override fun toLong(axionEngine: AxionEngine): Long {
        return char.code.toLong();
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        //nothing to clean//
    }
}