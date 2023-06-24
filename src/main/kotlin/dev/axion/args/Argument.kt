package dev.axion.args

import dev.axion.AxionEngine

abstract class Argument(val value: Any) {
    abstract fun toLong(axionEngine: AxionEngine): Long;
    abstract fun cleanMemory(axionEngine: AxionEngine);
}