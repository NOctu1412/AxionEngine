package dev.axion.types.impl

import dev.axion.types.WasmType

class IntegerWasmType(private val int: Int) : WasmType(
    int,
    toWasmerValue = { int as Integer },
)