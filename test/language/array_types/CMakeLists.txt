set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/ArraysMapping.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/ArraysMapping.h
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestEnum.h
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestStructure.h

    ${ZSERIO_GEN_DIR}/array_types/auto_array_recursion/AutoArrayRecursion.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_recursion/AutoArrayRecursion.h

    ${ZSERIO_GEN_DIR}/array_types/auto_array/AutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array/AutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/fixed_array/FixedArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/fixed_array/FixedArray.h

    ${ZSERIO_GEN_DIR}/array_types/implicit_array/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/implicit_array/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/array_types/variable_array/VariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array/VariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/variable_array/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array/TestStructure.h
)

zserio_add_library(TARGET array_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE array_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/ArraysMappingTest.cpp
    cpp/AutoArrayRecursionTest.cpp
    cpp/AutoArrayTest.cpp
    cpp/FixedArrayTest.cpp
    cpp/ImplicitArrayTest.cpp
    cpp/VariableArrayTest.cpp
)

add_test_dependencies(array_types_zs)
