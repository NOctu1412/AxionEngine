package dev.axion.args

import dev.axion.AxionEngine

class StringArgument(private val value: String) : Argument() {
    private var stringPointer: Long = 0;       //*mut u8
    private var stringObjectPointer: Long = 0; //String type in rust

    override fun toLong(axionEngine: AxionEngine): Long {
        stringPointer = axionEngine.allocate(value.length.toLong());
        axionEngine.getDefaultMemory().write(stringPointer.toInt(), value.toByteArray());
        stringObjectPointer = axionEngine.createStringObject(stringPointer, value.length.toLong());
        return stringObjectPointer;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        axionEngine.free(stringPointer, value.length.toLong());
        axionEngine.destroyStringObject(stringObjectPointer);
    }
}