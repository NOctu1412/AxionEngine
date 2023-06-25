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
    pub fn test_import(str: *mut String, a: i32, b: i32) -> i32;
}

#[no_mangle]
pub unsafe fn test(x: *mut TestStructure) -> i32 {
    test_import((*x).name, 32, 65)
}