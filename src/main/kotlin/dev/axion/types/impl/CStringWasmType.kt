package dev.axion.types.impl

import dev.axion.types.WasmType

data class AllocatedCStringReference(
    var pointer: Int,
    var size: Int
)

class CStringWasmType(
    private val string: String,
    private val allocatedStringReference: AllocatedStringReference = AllocatedStringReference(0, 0),
    private var autoFree: Boolean = true,
) : WasmType(
    string,
    toWasmerValue = {
        allocatedStringReference.let {
            it.pointer = allocate(string.length+1)
            getDefaultMemory().buffer().put(string.toByteArray(), it.pointer, string.length+1)
            getDefaultMemory().buffer().put(it.pointer+string.length, 0) //null terminator
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