import dev.axion.*
import dev.axion.extension.toWasmType
import dev.axion.types.*
import dev.axion.types.impl.*
import java.io.File

fun main() {
    val scriptPath = "rust_exemple/pkg/RustWasmTest_bg.wasm"
    val scriptBytes = File(scriptPath).readBytes()

    val engine = AxionEngine(scriptBytes,
        KotlinPrintImport(),
        DoubleTestImport(),
    )

    //TEST 1//
    println("------------TEST 1------------")
    val result = engine.callExport<FloatWasmType>("test",
        listOf(1L, 2L, 3L, 4L).toWasmType(engine),
    )

    println(result.value)

    //TEST 2//
    println("------------TEST 2------------")
    val baseStructure = TestStructure("Tom", 18, 1.75f)
    val result2 = engine.callExport<StringWasmType>("test2",
        PointerWasmType.allocateStructurePointer(engine, baseStructure, autoFree = false)
    )

    println(result2.value)

    //TEST 3//
    println("------------TEST 3------------")
    val result3 = engine.callExport<DoubleWasmType>("test3",
        9.0.toWasmType()
    )

    println(result3.value)

    //TEST 4//
    println("------------TEST 4------------")
    //for void return functions, do not specify the return type
    engine.callExport("test4")

    engine.close()
}