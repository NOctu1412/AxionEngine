import dev.axion.WasmImport
import dev.axion.types.EnumWasmType

class TestImport : WasmImport(
    "kotlin_print",
    EnumWasmType.VOID, //return type
    listOf(EnumWasmType.STRING),  //args types
    { axionEngine, instance, args ->                                  //callback
        val firstArgument = args[0].value as String

        println(firstArgument)

        null //for void functions, return null is good
    }
)