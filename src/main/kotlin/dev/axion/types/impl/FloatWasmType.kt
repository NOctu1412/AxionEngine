package dev.axion.types.impl

import dev.axion.types.WasmType

class FloatWasmType(private val float: Float) : WasmType(
    float,
    toWasmerValue = { float },
)