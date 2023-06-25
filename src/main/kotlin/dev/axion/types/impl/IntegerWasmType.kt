package dev.axion.types.impl

import dev.axion.types.WasmType

class IntegerWasmType(private val int: Int) : WasmType(
    int,
    toLong = { int.toLong() },
)