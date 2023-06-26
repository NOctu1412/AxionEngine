package dev.axion.types

import dev.axion.AxionEngine

open class WasmType(
    val value: Any,
    val toWasmerValue: AxionEngine.() -> Any,
    val cleanMemory: AxionEngine.() -> Unit = { /* do nothing */ }
)