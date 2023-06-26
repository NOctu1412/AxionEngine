package dev.axion.types.impl

import dev.axion.types.WasmType

class DoubleWasmType(private val double: Double) : WasmType(
    double,
    toWasmerValue = { double },
)