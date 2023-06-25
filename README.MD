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
>`returnTypes`: The types of returns value (you can also provide only one in a non-list).  
>`args`: The arguments to pass to the exported function.  

Here's an example of calling axionEngine.callExport:  
```kotlin
import dev.axion.args.ArgumentType

val result = axionEngine.callExport(
    "test", //the name of the export
    WasmTypes.STRING, //the return type (you can also have multiples using listOf(...))
    doubleType(2.5), intType(5) //the arguments to pass to the export
)

println(result.value)
```

In the above example, we call the exported function named "test" with a double argument 2.5. The ArgumentType.STRING indicates the expected return type. The result is printed to the console.  

## Different types of types you can create
There are different types of arguments that can be passed to the exported function. Here's a list of them:

> `stringType(String): A string type.  
> `intType(Int)`: An integer type.  
> `doubleType(Double)`: A double type.  
> `floatType(Float)`: A float type.  
> `longType(Long)`: A long type.  
> `byteType(Byte)`: A byte type.  
> `shortType(Short)`: A short type.  
> `booleanType(Boolean)`: A boolean type.  
> `charType(Char)`: A character type.
> `pointerTypeFromPointerAddress(AxionEngine, Long ptrAddress)`: A pointer type that use a pre existing pointer.
> `allocPointerType(AxionEngine, Long size)`: A pointer type that allocates a new space of `size` size.

## Closing the `AxionEngine` Object
When you're done using the `AxionEngine` object, you should close it. This will free up any resources that were allocated by the WebAssembly module. Here's an example of how to close an `AxionEngine` object:

```kotlin
axionEngine.close()
```

## Creating structures

### Creating the structure definition
```kotlin
data class TestStructure(
    @DeclaredField
    val name: String,
    @DeclaredField
    val age: Int,
    @DeclaredField
    val height: Float,
    @DeclaredField
    val weight: Double,
    @DeclaredField
    val isAlive: Boolean,
    @DeclaredField
    val favoriteLetter: Char,
    @DeclaredField
    val favoriteNumber: Byte,
    @DeclaredField
    val favoriteColor: Short,
    @DeclaredField
    val favoriteFood: Long,
    @DeclaredField
    val favoriteDrink: PointerWasmType,
)
```

These are all the types you can use in a structure !

### Getting a pointer to the structure
Creating a PointerWasmType from a structure is the only way to pass structures to the wasm module, and we made it super easy !  
```kotlin
val structure = TestStructure(
    "Axion",
    18,
    ...
)

//By default, when you use PointerWasmType in a call, it will be auto-freed, so if you want to have the same result in the return value, it will not be possible.
//To disable the auto-free, you can use the autoFree parameter in the allocateStructurePointer method
val structurePointer = PointerWasmType.allocateStructurePointer(axionEngine, structure, autoFree = false)

//calling the export
val result = axionEngine.callExport("test", WasmTypes.POINTER, structurePointer) as PointerWasmType //the result is a PointerWasmType

//getting the structure from the pointer
val outputStructure = result.getThisPointerAsStructure(TestStructure::class.java)

println(outputStructure.name) //Axion

//manually freeing the structure
axionEngine.free(structurePointer.ptr, structurePointer.size)

//closing the Axion Engine
axionEngine.close()
```

## Pointers class:
The Pointers class is used to create and read pointers to the wasm memory.

### PointerWasmType methods:
```kotlin
//create a pointer to a structure
//axionEngine: the axionEngine instance of the wasm module
//structure: the structure to create a pointer to
//autoFree: if the pointer should be auto-freed when used in a call (true by default)
PointerWasmType.allocateStructurePointer(axionEngine: AxionEngine, structure: Any, autoFree: Boolean = true): PointerWasmType

//create a pointer to any supported type type
//axionEngine: the axionEngine instance of the wasm module
//value: the value to create a pointer to
//autoFree: if the pointer should be auto-freed when used in a call (true by default)
//byteOrder: /!\ MODIFY ONLY IF YOU KNOW WHAT YOU ARE DOING /!\ the byte order of the value (little endian by default)
allocatePointer(axionEngine: AxionEngine, defaultValue: T, byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN, autoFree: Boolean = true): PointerWasmType

//get the value at the pointer address
//offset: the offset of the value in the pointer (0 by default)
get<T>(offset: Int = 0)

//set the value at the pointer address
//value: the value to set
//offset: the offset of the value in the pointer (0 by default)
write<T>(value: T, offset: Int = 0)

//get the value at the given index of an array (in memory the array is a pointer, so you can use PointerWasmType as an array way)
getArrayElement<T>(index: Int): T

//set the value at the given index of an array (in memory the array is a pointer, so you can use PointerWasmType as an array way)
writeArrayElement<T>(value: T, index: Int)
```