package dev.axion.types

import dev.axion.AxionEngine

open class WasmType(
    val value: Any,
    val toLong: AxionEngine.() -> Long,
    val cleanMemory: AxionEngine.() -> Unit = { /* do nothing */ }
)