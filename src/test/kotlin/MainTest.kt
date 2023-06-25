import dev.axion.AxionEngine
import dev.axion.types.*
import java.io.File

fun main() {
    val scriptPath = "D:\\RustWasmTest\\pkg\\RustWasmTest_bg.wasm";
    val scriptFile = File(scriptPath);
    val scriptBytes = scriptFile.readBytes();

    val axionEngine = AxionEngine(scriptBytes, listOf());

    val structure = TestStructure("John", 20, 1.8F);
    println("Base structure: $structure");

    val structurePointer = PointerWasmType.allocateStructurePointer(axionEngine, structure, autoFree = false);
    val result = axionEngine.callExport("test", WasmTypes.POINTER, structurePointer) as PointerWasmType;

    println("New structure: ${result.getThisPointerAsStructure(TestStructure::class.java)}");

    axionEngine.free(structurePointer.ptr, structurePointer.size);

    axionEngine.close();
}