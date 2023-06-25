import dev.axion.AxionEngine
import dev.axion.types.*
import dev.axion.types.impl.PointerWasmType
import java.io.File

fun main() {
    val scriptPath = "D:\\RustWasmTest\\pkg\\RustWasmTest_bg.wasm"
    val scriptFile = File(scriptPath)
    val scriptBytes = scriptFile.readBytes()

    val engine = AxionEngine(scriptBytes, listOf())

    val structure = TestStructure("John", 20, 1.8F)
    println("Base structure: $structure")

    val structurePointer = PointerWasmType.allocateStructurePointer(engine, structure, autoFree = false)
    val result = engine.callExport("test", EnumWasmType.POINTER, structurePointer) as PointerWasmType

    println("New structure: ${result.getThisPointerAsStructure(TestStructure::class.java)}")

    engine.free(structurePointer.ptr, structurePointer.size)

    engine.close()
}