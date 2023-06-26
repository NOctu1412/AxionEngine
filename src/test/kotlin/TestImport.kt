/*import dev.axion.WasmImport
import dev.axion.types.EnumWasmType
import dev.axion.types.impl.FloatWasmType
import dev.axion.types.impl.IntegerWasmType

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
    listOf(EnumWasmType.FLOAT, EnumWasmType.INTEGER),  //args types
    { axionEngine, instance, args ->                                  //callback
        val firstArgument = (args[0] as FloatWasmType).value as Float
        val secondArgument = (args[1] as IntegerWasmType).value as Int

        println(firstArgument)
        println(secondArgument)

        null //for void functions, return null is good
    }
)*/