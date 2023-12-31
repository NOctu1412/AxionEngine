package dev.axion.types

import dev.axion.types.impl.*
import org.wasmer.Type

enum class EnumWasmType {
    BYTE,   //i8
    CHAR,   //char
    BOOLEAN, //boolean
    SHORT,  //i16
    INTEGER, //i32
    LONG,  //i64
    FLOAT,  //f32
    DOUBLE,  //f64
    STRING,  //*mut String
    CSTRING, //*mut u8
    POINTER, //*mut void
    VOID; //void

    companion object {
        inline fun <reified T: WasmType> from(): EnumWasmType {
            return when(T::class) {
                ByteWasmType::class -> BYTE
                CharWasmType::class -> CHAR
                BooleanWasmType::class -> BOOLEAN
                ShortWasmType::class -> SHORT
                IntegerWasmType::class -> INTEGER
                LongWasmType::class -> LONG
                FloatWasmType::class -> FLOAT
                DoubleWasmType::class -> DOUBLE
                StringWasmType::class -> STRING
                CStringWasmType::class -> CSTRING
                PointerWasmType::class -> POINTER
                else -> VOID
            }
        }
    }

    fun toValueType(): Type {
        return when(this) {
            BYTE -> Type.I32
            CHAR -> Type.I32
            BOOLEAN -> Type.I32
            SHORT -> Type.I32
            INTEGER -> Type.I32
            LONG -> Type.I64
            FLOAT -> Type.F32
            DOUBLE -> Type.F64
            STRING -> Type.I32 //ptr size
            CSTRING -> Type.I32 //ptr size
            POINTER -> Type.I32 //ptr size
            else -> throw Exception("Invalid type")
        }
    }
}