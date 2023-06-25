mod wasm_utils;

use wasm_utils::*;

#[repr(C)]
#[derive(Debug, Clone)]
pub struct TestStructure {
    name: *mut String,
    age: u32,
    height: f32,
}

#[no_mangle]
pub fn test(x: *mut TestStructure) -> *mut String {
    let x = extract_mut_ptr(x);
    let mut name = extract_mut_ptr(x.name);
    name.push_str(" Baka");
    into_mut_ptr(name)
}