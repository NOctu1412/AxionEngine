package dev.axion.args

import dev.axion.AxionEngine

class StringArgument(private val string: String) : Argument(string) {
    private var stringPointer: Long = 0;       //*mut u8
    private var stringObjectPointer: Long = 0; //String type in rust

    override fun toLong(axionEngine: AxionEngine): Long {
        stringPointer = axionEngine.allocate(string.length.toLong());
        axionEngine.getDefaultMemory().write(stringPointer.toInt(), string.toByteArray());
        stringObjectPointer = axionEngine.createStringObject(stringPointer, string.length.toLong());
        return stringObjectPointer;
    }

    override fun cleanMemory(axionEngine: AxionEngine) {
        axionEngine.free(stringPointer, string.length.toLong());
        axionEngine.destroyStringObject(stringObjectPointer);
    }
}