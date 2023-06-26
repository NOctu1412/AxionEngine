use std::any::Any;
use std::ffi::{c_char, CString};
use std::fmt::Pointer;
use std::ops::Deref;
use std::slice;

#[no_mangle]
pub fn alloc_rust(size: usize) -> *mut u8 {
    let mut buf = Vec::with_capacity(size);
    let ptr = buf.as_mut_ptr();
    std::mem::forget(buf);
    return ptr;
}

//utility to allocate a structure directly in the heap
pub fn alloc_structure_a<T>() -> *mut T {
    let structure_size = std::mem::size_of::<T>();
    let mut buf = Vec::with_capacity(structure_size);
    let ptr = buf.as_mut_ptr();
    std::mem::forget(buf);
    return ptr;
}

pub fn alloc_structure<T>(instance: T) -> *mut T {
    let ptr = alloc_structure_a::<T>();
    unsafe {
        *ptr = instance;
    }
    return ptr;
}

#[no_mangle]
pub fn free_rust(pointer: *mut u8, size: usize) {
    unsafe {
        let _ = Vec::from_raw_parts(pointer, 0, size);
    }
}

#[no_mangle]
pub fn create_string(ptr: *mut u8, length: usize) -> *mut String {
    unsafe {
        let slice = slice::from_raw_parts(ptr, length);
        let string = String::from_raw_parts(ptr as *mut _, length, length);
        std::mem::forget(slice); // Prevent the slice from being deallocated

        Box::into_raw(Box::new(string))
    }
}

#[no_mangle]
pub fn get_string_length(ptr: *mut String) -> usize {
    unsafe {
        let string = extract_mut_ptr(ptr);
        return string.len();
    }
}

#[no_mangle]
pub fn get_c_string_from_string(ptr: *mut String) -> *mut u8 {
    unsafe {
        let string = extract_mut_ptr(ptr);
        let mut vec = string.into_bytes();
        let ptr = vec.as_mut_ptr();
        std::mem::forget(vec);
        return ptr;
    }
}

#[no_mangle]
pub fn destroy_string(ptr: *mut String) {
    unsafe {
        if !ptr.is_null() {
            let _ = Box::from_raw(ptr);
        }
    }
}

pub fn c_string_length(ptr: *mut u8) -> usize {
    let mut length = 0;
    unsafe {
        while *ptr.offset(length as isize) != 0 {
            length += 1;
        }
    }
    length
}

pub fn extract_mut_ptr<T: Clone>(ptr: *mut T) -> T {
    unsafe { (*ptr).clone() }
}

pub fn into_mut_ptr<T: Clone>(object: T) -> *mut T {
    Box::into_raw(Box::new(object))
}

pub fn clone_mut_ptr<T: Clone>(ptr: *mut T) -> *mut T {
    unsafe { Box::into_raw(Box::new((*ptr).clone())) }
}