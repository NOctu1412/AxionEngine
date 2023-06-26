package dev.axion.types.impl

import dev.axion.types.WasmType

class BooleanWasmType(private val boolean: Boolean) : WasmType(
    boolean,
    toWasmerValue = { if(boolean) 1 else 0 },
)