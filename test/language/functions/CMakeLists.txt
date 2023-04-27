set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/functions/choice_array/Inner.cpp
    ${ZSERIO_GEN_DIR}/functions/choice_array/Inner.h
    ${ZSERIO_GEN_DIR}/functions/choice_array/Item.cpp
    ${ZSERIO_GEN_DIR}/functions/choice_array/Item.h
    ${ZSERIO_GEN_DIR}/functions/choice_array/ItemRef.cpp
    ${ZSERIO_GEN_DIR}/functions/choice_array/ItemRef.h
    ${ZSERIO_GEN_DIR}/functions/choice_array/OuterArray.cpp
    ${ZSERIO_GEN_DIR}/functions/choice_array/OuterArray.h

    ${ZSERIO_GEN_DIR}/functions/structure_array/Item.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_array/Item.h
    ${ZSERIO_GEN_DIR}/functions/structure_array/StructureArray.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_array/StructureArray.h

    ${ZSERIO_GEN_DIR}/functions/structure_array_param/ChildStructure.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_array_param/ChildStructure.h
    ${ZSERIO_GEN_DIR}/functions/structure_array_param/NotLeftMost.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_array_param/NotLeftMost.h
    ${ZSERIO_GEN_DIR}/functions/structure_array_param/ParentStructure.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_array_param/ParentStructure.h

    ${ZSERIO_GEN_DIR}/functions/structure_bytes/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_bytes/TestStructure.h
    ${ZSERIO_GEN_DIR}/functions/structure_bytes/Child.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_bytes/Child.h

    ${ZSERIO_GEN_DIR}/functions/structure_extern/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_extern/TestStructure.h
    ${ZSERIO_GEN_DIR}/functions/structure_extern/Child.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_extern/Child.h

    ${ZSERIO_GEN_DIR}/functions/structure_optional/ValueCalculator.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_optional/ValueCalculator.h
    ${ZSERIO_GEN_DIR}/functions/structure_optional/ValueConsumer.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_optional/ValueConsumer.h
    ${ZSERIO_GEN_DIR}/functions/structure_optional/ValueConsumerCreator.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_optional/ValueConsumerCreator.h

    ${ZSERIO_GEN_DIR}/functions/structure_param/MetresConverterCaller.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_param/MetresConverterCaller.h
    ${ZSERIO_GEN_DIR}/functions/structure_param/MetresConverter.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_param/MetresConverter.h

    ${ZSERIO_GEN_DIR}/functions/structure_parent_child_value/ParentValue.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_parent_child_value/ParentValue.h
    ${ZSERIO_GEN_DIR}/functions/structure_parent_child_value/ChildValue.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_parent_child_value/ChildValue.h

    ${ZSERIO_GEN_DIR}/functions/structure_string/CONST.h
    ${ZSERIO_GEN_DIR}/functions/structure_string/FIELD.h
    ${ZSERIO_GEN_DIR}/functions/structure_string/StringPool.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_string/StringPool.h
    ${ZSERIO_GEN_DIR}/functions/structure_string/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_string/TestStructure.h

    ${ZSERIO_GEN_DIR}/functions/structure_value/CustomVarInt.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_value/CustomVarInt.h
    ${ZSERIO_GEN_DIR}/functions/structure_value/CustomVarList.cpp
    ${ZSERIO_GEN_DIR}/functions/structure_value/CustomVarList.h

    ${ZSERIO_GEN_DIR}/functions/union_array/Inner.cpp
    ${ZSERIO_GEN_DIR}/functions/union_array/Inner.h
    ${ZSERIO_GEN_DIR}/functions/union_array/Item.cpp
    ${ZSERIO_GEN_DIR}/functions/union_array/Item.h
    ${ZSERIO_GEN_DIR}/functions/union_array/ItemRef.cpp
    ${ZSERIO_GEN_DIR}/functions/union_array/ItemRef.h
    ${ZSERIO_GEN_DIR}/functions/union_array/OuterArray.cpp
    ${ZSERIO_GEN_DIR}/functions/union_array/OuterArray.h
)

zserio_add_library(functions_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE functions.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(functions
    DEPENDS
        functions_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureArrayParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureBytesTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureExternTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureOptionalTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureParentChildValueTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureStringTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureValueTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionArrayTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
