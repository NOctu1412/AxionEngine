# AxionEngine
The `AxionEngine` class is used to interact with a WebAssembly module and call its exported functions. It provides methods for allocating memory, working with string objects, calling exports, and managing the instance.

## Creating an `AxionEngine` Object

To create an `AxionEngine` object, you need to provide the WebAssembly binary as a `ByteArray` and optionally a list of imports. Here's an example of how to create an `AxionEngine` object:

```kotlin
import dev.axion.AxionEngine
import dev.axion.args.ArgumentType
import java.io.File

fun main() {
    val scriptPath = "file.wasm"
    val scriptFile = File(scriptPath)
    val scriptBytes = scriptFile.readBytes()

    val axionEngine = AxionEngine(scriptBytes, listOf())

    // Use the axionEngine object...
}
```

In the above example, we load the WebAssembly binary from a file and create an `AxionEngine` object with an empty list of imports.

## Calling WASM export functions using `axionEngine.callExport`
The axionEngine.callExport method is used to call an exported function from the WebAssembly module. It takes the following parameters:

>`name`: The name of the exported function to call.  
>`returnTypes`: The types of returns value.  
>`args`: The arguments to pass to the exported function.  

Here's an example of calling axionEngine.callExport:  
```kotlin
import dev.axion.args.ArgumentType

val result = axionEngine.callExport(
    "test", //the name of the export
    ArgumentType.STRING, //the return type (you can also have multiples using listOf(...))
    doubleArg(2.5) //the arguments to pass to the export
)

println(result.value)
```

In the above example, we call the exported function named "test" with a double argument 2.5. The ArgumentType.STRING indicates the expected return type. The result is printed to the console.  

## Different types of arguments
There are different types of arguments that can be passed to the exported function. Here's a list of them:

>`stringArg`: A string argument.  
>`intArg`: An integer argument.  
> `doubleArg`: A double argument.  
> `floatArg`: A float argument.  
> `longArg`: A long argument.  
> `byteArg`: A byte argument.  
> `shortArg`: A short argument.  
> `booleanArg`: A boolean argument.
> `charArg`: A character argument.

## Closing the `AxionEngine` Object
When you're done using the `AxionEngine` object, you should close it. This will free up any resources that were allocated by the WebAssembly module. Here's an example of how to close an `AxionEngine` object:

```kotlin
axionEngine.close()
```