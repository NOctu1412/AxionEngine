import dev.axion.*
import dev.axion.types.*
import dev.axion.types.impl.*
import java.io.File

fun main() {
    val scriptPath = "rust_exemple/pkg/RustWasmTest_bg.wasm"
    val scriptBytes = File(scriptPath).readBytes()

    val engine = AxionEngine(scriptBytes, listOf(
        TestImport()
    ))

    //TEST 1//
    val structure = TestStructure("John", 20, 1.8F)
    println("Base structure: $structure")

    val structurePointer = PointerWasmType.allocateStructurePointer(engine, structure)
    val result = engine.callExport("test", EnumWasmType.INTEGER,
        structurePointer
    ) as IntegerWasmType

    println(result.value)

    engine.close()
}