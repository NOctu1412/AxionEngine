package dev.axion.structure

import dev.axion.AxionEngine
import dev.axion.extension.getWasmTypeSize
import dev.axion.types.impl.PointerWasmType
import java.lang.reflect.Constructor

private val structureMapCache: MutableMap<Any, List<StructureField>> = mutableMapOf()

private fun mapStructure(structure: Class<*>): List<StructureField> {
    if(structureMapCache.containsKey(structure)) {
        return structureMapCache[structure]!!
    }
    val result = mutableListOf<StructureField>()
    var currentOffset = 0

    structure.declaredFields.forEach { field ->
        field.getDeclaredAnnotation(DeclaredField::class.java) ?: return@forEach
        val type = field.type
        val size = type.getWasmTypeSize()
        val structureField = StructureField(type, currentOffset)
        currentOffset += size

        result.add(structureField)
    }

    structureMapCache[structure] = result

    return result
}

@Suppress("UNCHECKED_CAST")
fun <C: Any> getStructureFromPointer(pointer: PointerWasmType, structureJavaClass: Class<C>): C {
    val structureMap = mapStructure(structureJavaClass)

    val structureInstance = structureJavaClass.declaredConstructors.first{
        it.parameterCount == structureMap.size
    }.let {
        it.isAccessible = true
        it as Constructor<*>
    }.newInstance(*structureMap.map {
        val type = it.type
        val offset = it.offset

        pointer.get(type, offset)
    }.toTypedArray()) as C

    return structureInstance
}

fun <C: Any> getPointerFromStructure(engine: AxionEngine, structure: C, autoFree: Boolean = true): PointerWasmType {
    val structureMap = mapStructure(structure.javaClass)

    val structureSize = structureMap.last().offset + structureMap.last().type.getWasmTypeSize() //super smart way
    val pointer = PointerWasmType.allocatePointer(engine, structureSize, autoFree = autoFree)

    structureMap.withIndex().forEach { (index, field) ->
        val offset = field.offset
        val structureField = structure.javaClass.declaredFields[index].also { it.isAccessible = true }
        val value = structureField.get(structure)
        pointer.write(value, offset)
    }

    return pointer
}