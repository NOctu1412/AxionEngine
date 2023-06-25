import dev.axion.AxionEngine
import dev.axion.types.*
import dev.axion.types.impl.PointerWasmType
import dev.axion.types.impl.StringWasmType
import java.io.File

fun main() {
    val scriptPath = "rust_exemple/pkg/RustWasmTest_bg.wasm"
    val scriptBytes = File(scriptPath).readBytes()

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