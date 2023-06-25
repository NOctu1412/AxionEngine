import dev.axion.AxionEngine
import dev.axion.extension.toCStringWasmType
import dev.axion.extension.toWasmType
import dev.axion.types.*
import dev.axion.types.impl.CStringWasmType
import dev.axion.types.impl.PointerWasmType
import dev.axion.types.impl.StringWasmType
import java.io.File

fun main() {
    val scriptPath = "D:\\RustWasmTest\\pkg\\RustWasmTest_bg.wasm"
    val scriptFile = File(scriptPath)
    val scriptBytes = scriptFile.readBytes()

    val engine = AxionEngine(scriptBytes, listOf())

    //TEST 1//
    val structure = TestStructure("John", 20, 1.8F)
    println("Base structure: $structure")

    val structurePointer = PointerWasmType.allocateStructurePointer(engine, structure)
    val result = engine.callExport("test", EnumWasmType.STRING,
        structurePointer
    ) as StringWasmType

    println(result.value)

    engine.free(structurePointer.ptr, structurePointer.size)

    engine.close()
}