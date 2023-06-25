package dev.axion.types.impl

import dev.axion.types.WasmType

class LongWasmType(private val long: Long) : WasmType(
    long,
    toLong = { long },
)