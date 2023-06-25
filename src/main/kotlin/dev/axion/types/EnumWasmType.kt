package dev.axion.types

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
}