package dev.axion.types

import com.github.salpadding.wasmer.ValType

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

    fun toValType(): ValType {
        return when(this) {
            BYTE -> ValType.I32
            CHAR -> ValType.I32
            BOOLEAN -> ValType.I32
            SHORT -> ValType.I32
            INTEGER -> ValType.I32
            LONG -> ValType.I64
            FLOAT -> ValType.F32
            DOUBLE -> ValType.F64
            STRING -> ValType.I32 //ptr size
            CSTRING -> ValType.I32 //ptr size
            POINTER -> ValType.I32 //ptr size
            VOID -> ValType.I64 //don't ask me why please
        }
    }
}