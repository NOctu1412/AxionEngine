package dev.axion.types.impl

import dev.axion.types.WasmType

class CharWasmType(private val char: Char) : WasmType(
    char,
    toWasmerValue = { char.code as Integer }
)