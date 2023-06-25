mod wasm_utils;

use wasm_utils::*;

#[repr(C)]
#[derive(Debug, Clone)]
pub struct TestStructure {
    name: *mut String,
    age: u32,
    height: f32,
}

extern "C" {
    pub fn kotlin_print(str: *mut String);
}

#[no_mangle]
pub unsafe fn test(x: [i64; 4]) -> i64 {
    kotlin_print(into_mut_ptr(String::from("Hello from Rust !")));
    x[0] + x[1] + x[2] + x[3]
}