package dev.axion.types.impl

import dev.axion.types.WasmType

data class AllocatedStringReference(
    var pointer: Long,
    var size: Long
)

//used for String object in rust
//if you want to get a CString, use the PointerWasmType.getAsString() method
class StringWasmType(
    private val string: String,
    private val allocatedStringReference: AllocatedStringReference = AllocatedStringReference(0, 0),
) : WasmType(
    string,
    toLong = {
        allocatedStringReference.let {
            it.pointer = allocate(string.length.toLong())
            getDefaultMemory().write(it.pointer.toInt(), string.toByteArray())
            it.pointer = createStringObject(it.pointer, string.length.toLong())
            it.pointer
        }
    },
    cleanMemory = {
        allocatedStringReference.let {
            free(it.pointer, it.size)
            destroyStringObject(it.pointer)
        }
    },
)