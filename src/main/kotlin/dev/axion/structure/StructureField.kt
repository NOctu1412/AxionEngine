package dev.axion.structure

open class StructureField(
    open val name: String,
    open val type: Class<*>,
    open val offset: Int
)