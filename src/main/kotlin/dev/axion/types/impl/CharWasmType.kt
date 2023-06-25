package dev.axion.types.impl

import dev.axion.types.WasmType

class CharWasmType(private val char: Char) : WasmType(
    char,
    toLong = { char.code.toLong() }
)