package dev.axion.types.impl

import dev.axion.types.WasmType

class ByteWasmType(private val byte: Byte) : WasmType(
    byte,
    toWasmerValue = { byte.toInt() }
)