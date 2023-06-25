package dev.axion.types.impl

import dev.axion.types.WasmType

class ShortWasmType(private val short: Short) : WasmType(
    short,
    toLong = { short.toLong() }
)