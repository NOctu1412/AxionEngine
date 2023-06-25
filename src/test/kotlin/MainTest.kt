import dev.axion.AxionEngine
import dev.axion.types.*
import java.io.File

fun main() {
    val scriptPath = "D:\\RustWasmTest\\pkg\\RustWasmTest_bg.wasm";
    val scriptFile = File(scriptPath);
    val scriptBytes = scriptFile.readBytes();

    val axionEngine = AxionEngine(scriptBytes, listOf());

    val result = axionEngine.callExport("test", WasmTypes.POINTER) as PointerWasmType;
    val structure = result.getThisPointerAsStructure(TestStructure::class.java);

    println(structure);
}