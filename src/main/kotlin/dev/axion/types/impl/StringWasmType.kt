package dev.axion.types.impl

import dev.axion.types.WasmType

data class AllocatedStringReference(
    var pointer: Int,
    var size: Int
)

//used for String object in rust
//if you want to get a CString, use the CStringWasmType method
class StringWasmType(
    private val string: String,
    private val allocatedStringReference: AllocatedStringReference = AllocatedStringReference(0, 0),
    private var autoFree: Boolean = true,
) : WasmType(
    string,
    toWasmerValue = {
        allocatedStringReference.let {
            it.pointer = allocate(string.length)
            for(i in string.indices) {
                getDefaultMemory().buffer().put(it.pointer + i, string[i].code.toByte())
            }
            it.pointer = createStringObject(it.pointer, string.length)
            it.pointer
        }
    },
    cleanMemory = {
        allocatedStringReference.let {
            if(autoFree) {
                free(it.pointer, it.size)
                destroyStringObject(it.pointer)
            }
        }
    },
) {
    fun setAutoFree(autoFree: Boolean): StringWasmType {
        this.autoFree = autoFree
        return this
    }
}