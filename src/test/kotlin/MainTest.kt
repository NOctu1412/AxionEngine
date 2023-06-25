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
    val result = engine.callExport("test", EnumWasmType.LONG,
        PointerWasmType.allocateArray(engine, arrayOf(1L, 2L, 3L, 4L))
    ) as LongWasmType

    println(result.value)

    engine.close()
}