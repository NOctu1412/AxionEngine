import dev.axion.extension.toWasmType
import dev.axion.imports.WasmImport
import dev.axion.types.EnumWasmType

class KotlinPrintImport : WasmImport(
    "kotlin_print",
    EnumWasmType.VOID, //return type
    arrayOf(EnumWasmType.STRING),  //args types
    { axionEngine, args ->                                  //callback
        val firstArgument = args[0].value as String

        println(firstArgument)

        null //for void functions, return null is good
    }
)

class DoubleTestImport : WasmImport(
    "double_test",
    EnumWasmType.DOUBLE, //return type
    arrayOf(EnumWasmType.DOUBLE, EnumWasmType.DOUBLE),  //args types
    { axionEngine, args ->                                  //callback
        val firstArgument = args[0].value as Double
        val secondArgument = args[1].value as Double

        println(firstArgument)
        println(secondArgument)

        (firstArgument * secondArgument).toWasmType()
    }
)