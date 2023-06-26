
# Axion Engine

This library is used to get a good interopability between Kotlin and WebAssembly (especially with Rust but I think you can port the [wasm_utils.rs](https://github.com/NOctu1412/AxionEngine/blob/master/rust_example/src/wasm_utils.rs) to other languages).




## Features

- Loading WASM modules into Kotlin
- Calling WASM exports function with Kotlin types
- WASM Imports for calling Kotlin from WASM
- Support for all primitive types + structures
- Cross platform


## Usage/Examples
Rust:

```rust
mod wasm_utils;

use wasm_utils::*;

extern "C" {
    pub fn kotlin_print(str: *mut String);
}

#[no_mangle]
pub unsafe fn test(x: [i64; 4]) -> f32 {
    kotlin_print(into_mut_ptr("Hello from Rust !".to_string()));
    x.len() as f32
}
```

Kotlin:
```kotlin
class KotlinPrintImport : WasmImport(
    "kotlin_print",
    EnumWasmType.VOID,             //return type
    arrayOf(EnumWasmType.STRING),  //args types
    { axionEngine, args ->         //callback
        val firstArgument = args[0].value as String

        println(firstArgument)

        null                        //for void functions, returning null is good
    }
)

fun main() {
    val wasmBin = File("rust.wasm").readBytes()

    val axionEngine = AxionEngine(wasmBin,
        KotlinPrintImport(),
        //...  (add all your imports)
    )

    val callResult = axionEngine.callExport<FloatWasmType>  //for void functions, do not put a type
        (
        "test",  //function name
        listOf(1L, 2L, 3L, 4L).toWasmType(axionEngine),
        //...   (add all your arguments)
        )

    println(callResult.value)
}
```

(see more examples in the [kotlin test directory](https://github.com/NOctu1412/AxionEngine/tree/master/src/test/kotlin) and in the [rust example](https://github.com/NOctu1412/AxionEngine/tree/master/rust_example))





## Authors

- [@NOctu1412](https://www.github.com/NOctu1412)

## Thanks to
- [@wasmerio](https://www.github.com/wasmerio) for the [wasmer](https://github.com/wasmerio/wasmer) and [wasmer-java](https://github.com/wasmerio/wasmer-java) librairies !

- [@beaclnd92](https://github.com/beaclnd92) for his wasmer-java [pull request](https://github.com/wasmerio/wasmer-java/pull/64)
