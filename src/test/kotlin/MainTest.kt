import dev.axion.AxionEngine
import dev.axion.args.ArgumentType
import java.io.File

fun main() {
    val scriptPath = "D:\\RustWasmTest\\pkg\\RustWasmTest_bg.wasm";
    val scriptFile = File(scriptPath);
    val scriptBytes = scriptFile.readBytes();

    val axionEngine = AxionEngine(scriptBytes, listOf());

    println(
        axionEngine.callExport("test", ArgumentType.STRING,
            doubleArg(2.5)
        ).value
    );
}