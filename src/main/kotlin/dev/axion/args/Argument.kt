package dev.axion.args

import dev.axion.AxionEngine

abstract class Argument {
    abstract fun toLong(axionEngine: AxionEngine): Long;
    abstract fun cleanMemory(axionEngine: AxionEngine);
}