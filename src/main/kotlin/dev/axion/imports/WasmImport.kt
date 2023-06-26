package dev.axion.imports

import dev.axion.AxionEngine
import dev.axion.types.EnumWasmType
import dev.axion.types.WasmType

open class WasmImport(
    val name: String,
    val returnType: EnumWasmType,
    val argsTypes: Array<EnumWasmType>,
    val callback: (axionEngine: AxionEngine, args: Array<WasmType>) -> WasmType?,
    val namespace: String = "env"
)