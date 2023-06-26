mod wasm_utils;

use std::ptr::null;

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
    pub fn double_test(x: f64, y: f64) -> f64;
}

#[no_mangle]
pub unsafe fn test(x: [i64; 4]) -> f32 {
    kotlin_print(into_mut_ptr("Hello from Rust !".to_string()));
    (x[0] + x[1] + x[2] + x[3]) as f32
}

#[no_mangle]
pub unsafe fn test2(x: *mut TestStructure) -> *mut String {
    let s = extract_mut_ptr(x);
    let mut new_name = extract_mut_ptr(s.name);
    new_name.push_str(" Chan");
    into_mut_ptr(new_name)
}

#[no_mangle]
pub unsafe fn test3(x: f64) -> f64 {
    double_test(x, x)
}

#[no_mangle]
pub unsafe fn test4() {
    kotlin_print(into_mut_ptr("Void test working !".to_string()));
}