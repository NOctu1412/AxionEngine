package dev.axion.types.impl

import dev.axion.types.WasmType

class BooleanWasmType(private val boolean: Boolean) : WasmType(
    boolean,
    toLong = { if(boolean) 1L else 0L },
)