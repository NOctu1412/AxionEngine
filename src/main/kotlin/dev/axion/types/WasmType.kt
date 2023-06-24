package dev.axion.types

import dev.axion.AxionEngine

abstract class WasmType(val value: Any) {
    abstract fun toLong(axionEngine: AxionEngine): Long;
    abstract fun cleanMemory(axionEngine: AxionEngine);
}