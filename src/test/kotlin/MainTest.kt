import dev.axion.*
import dev.axion.extension.toWasmType
import dev.axion.types.*
import dev.axion.types.impl.*
import java.io.File

fun main() {
    val scriptPath = "rust_exemple/pkg/RustWasmTest_bg.wasm"
    val scriptBytes = File(scriptPath).readBytes()

    val engine = AxionEngine(scriptBytes)

    //TEST 1//
    val result = engine.callExport("test", EnumWasmType.FLOAT,
        PointerWasmType.allocateArray(engine, arrayOf(1L, 2L, 3L, 4L))
    ) as FloatWasmType

    println(result.value)

    //TEST 2//
    val baseStructure = TestStructure("Tom", 18, 1.75f)
    val result2 = engine.callExport("test2", EnumWasmType.STRING,
        PointerWasmType.allocateStructurePointer(engine, baseStructure, autoFree = false)
    ) as StringWasmType

    println(result2.value)

    engine.close()
}