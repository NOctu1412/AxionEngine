package dev.axion.types.impl

import dev.axion.types.WasmType

data class AllocatedCStringReference(
    var pointer: Long,
    var size: Long
)

class CStringWasmType(
    private val string: String,
    private val allocatedStringReference: AllocatedStringReference = AllocatedStringReference(0, 0),
    private var autoFree: Boolean = true,
) : WasmType(
    string,
    toLong = {
        allocatedStringReference.let {
            it.pointer = allocate(string.length.toLong()+1)
            getDefaultMemory().write(it.pointer.toInt(), string.toByteArray())
            getDefaultMemory().write(it.pointer.toInt()+string.length, byteArrayOf(0)) //null terminator
            it.pointer
        }
    },
    cleanMemory = {
        allocatedStringReference.let {
            if(autoFree) {
                free(it.pointer, it.size)
            }
        }
    },
) {
    fun setAutoFree(autoFree: Boolean): CStringWasmType {
        this.autoFree = autoFree
        return this
    }
}