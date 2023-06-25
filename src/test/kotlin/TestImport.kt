import dev.axion.WasmImport
import dev.axion.types.EnumWasmType

class KotlinPrintImport : WasmImport(
    "kotlin_print",
    EnumWasmType.VOID, //return type
    listOf(EnumWasmType.STRING),  //args types
    { axionEngine, instance, args ->                                  //callback
        val firstArgument = args[0].value as String

        println(firstArgument)

        null //for void functions, return null is good
    }
)

class DoubleTestImport : WasmImport(
    "double_test",
    EnumWasmType.VOID, //return type
    listOf(EnumWasmType.DOUBLE),  //args types
    { axionEngine, instance, args ->                                  //callback
        val firstArgument = args[0].value as Double

        println(firstArgument)

        null //for void functions, return null is good
    }
)