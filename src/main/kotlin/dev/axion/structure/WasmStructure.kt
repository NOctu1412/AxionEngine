package dev.axion.structure

import dev.axion.AxionEngine
import dev.axion.getTypeSize
import dev.axion.types.PointerWasmType
import java.lang.reflect.Constructor

private val structureMapCache: MutableMap<Any, List<StructureField>> = mutableMapOf();

private fun mapStructure(structure: Class<*>): List<StructureField> {
    if(structureMapCache.containsKey(structure)) {
        return structureMapCache[structure]!!;
    }
    val result = mutableListOf<StructureField>();
    var currentOffset = 0;

    val fields = structure.declaredFields;
    for(field in fields) {
        field.getDeclaredAnnotation(DeclaredField::class.java) ?: continue;
        val type = field.type;
        val size = getTypeSize(type);
        val structureField = StructureField(type, currentOffset);
        currentOffset += size;

        result.add(structureField);
    }

    structureMapCache[structure] = result;

    return result;
}

fun <C: Any> getStructureFromPointer(pointer: PointerWasmType, structureJavaClass: Class<C>): C {
    val structureMap = mapStructure(structureJavaClass);

    val structureInstance = structureJavaClass.declaredConstructors.first{
        it.parameterCount == structureMap.size;
    }.let {
        it.isAccessible = true;
        it as Constructor<C>;
    }.newInstance(*structureMap.map {
        val type = it.type;
        val offset = it.offset;
        val value = pointer.get(type, offset);
        value
    }.toTypedArray()) as C;

    return structureInstance;
}

fun <C: Any> getPointerFromStructure(axionEngine: AxionEngine, structure: C, autoFree: Boolean = true): PointerWasmType {
    val structureMap = mapStructure(structure.javaClass);

    val structureSize = structureMap.last().offset + getTypeSize(structureMap.last().type); //super smart way
    val pointer = PointerWasmType.allocatePointer(axionEngine, structureSize, autoFree = autoFree);

    for((index, field) in structureMap.withIndex()) {
        val offset = field.offset;
        val structureField = structure.javaClass.declaredFields[index];
        structureField.isAccessible = true;
        val value = structureField.get(structure);
        pointer.write(value, offset);
    }

    return pointer;
}