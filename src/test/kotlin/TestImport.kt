import dev.axion.WasmImport
import dev.axion.extension.toWasmType
import dev.axion.types.EnumWasmType

class TestImport : WasmImport(
    "test_import",
    EnumWasmType.INTEGER, //return type
    listOf(EnumWasmType.STRING, EnumWasmType.INTEGER, EnumWasmType.INTEGER),  //args types
    { axionEngine, instance, args ->                                  //callback
        val firstArgument = args[0].value as String
        val secondArgument = args[1].value as Int
        val thirdArgument = args[2].value as Int

        println("First argument: $firstArgument")

        (secondArgument + thirdArgument).toWasmType()
    }
)