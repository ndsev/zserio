set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/ArraysMapping.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/ArraysMapping.h
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestBitmask.h
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

    ${ZSERIO_GEN_DIR}/array_types/subtyped_builtin_auto_array/ArrayElement.h
    ${ZSERIO_GEN_DIR}/array_types/subtyped_builtin_auto_array/SubtypedBuiltinAutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/subtyped_builtin_auto_array/SubtypedBuiltinAutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/subtyped_struct_variable_array/ArrayElement.h
    ${ZSERIO_GEN_DIR}/array_types/subtyped_struct_variable_array/SubtypedStructVariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/subtyped_struct_variable_array/SubtypedStructVariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/subtyped_struct_variable_array/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/subtyped_struct_variable_array/TestStructure.h

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
    ${ZSERIO_CPP_DIR}/ArraysMappingTest.cpp
    ${ZSERIO_CPP_DIR}/AutoArrayRecursionTest.cpp
    ${ZSERIO_CPP_DIR}/AutoArrayTest.cpp
    ${ZSERIO_CPP_DIR}/FixedArrayTest.cpp
    ${ZSERIO_CPP_DIR}/ImplicitArrayTest.cpp
    ${ZSERIO_CPP_DIR}/SubtypedBuiltinAutoArrayTest.cpp
    ${ZSERIO_CPP_DIR}/SubtypedStructVariableArrayTest.cpp
    ${ZSERIO_CPP_DIR}/VariableArrayTest.cpp
)

add_test_dependencies(array_types_zs)
