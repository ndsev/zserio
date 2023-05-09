set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/union_types/empty_union/EmptyUnion.cpp
    ${ZSERIO_GEN_DIR}/union_types/empty_union/EmptyUnion.h

    ${ZSERIO_GEN_DIR}/union_types/empty_union_with_parameter/EmptyUnionWithParameter.cpp
    ${ZSERIO_GEN_DIR}/union_types/empty_union_with_parameter/EmptyUnionWithParameter.h

    ${ZSERIO_GEN_DIR}/union_types/simple_union/SimpleUnion.cpp
    ${ZSERIO_GEN_DIR}/union_types/simple_union/SimpleUnion.h

    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionCompatibilityCheckVersion1.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionCompatibilityCheckVersion1.h
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionCompatibilityCheckVersion2.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionCompatibilityCheckVersion2.h
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionVersion1.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionVersion1.h
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionVersion2.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/UnionVersion2.h
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/CoordXY.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/CoordXY.h
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/CoordXYZ.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_compatibility_check/CoordXYZ.h

    ${ZSERIO_GEN_DIR}/union_types/union_with_array/Data8.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_with_array/Data8.h
    ${ZSERIO_GEN_DIR}/union_types/union_with_array/TestUnion.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_with_array/TestUnion.h

    ${ZSERIO_GEN_DIR}/union_types/union_with_parameterized_field/ArrayHolder.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_with_parameterized_field/ArrayHolder.h
    ${ZSERIO_GEN_DIR}/union_types/union_with_parameterized_field/TestUnion.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_with_parameterized_field/TestUnion.h

    ${ZSERIO_GEN_DIR}/union_types/union_with_parameter/TestUnion.cpp
    ${ZSERIO_GEN_DIR}/union_types/union_with_parameter/TestUnion.h
)

zserio_add_library(union_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE union_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(union_types
    DEPENDS
        union_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyUnionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyUnionWithParameterTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SimpleUnionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionCompatibilityCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionWithArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionWithParameterizedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionWithParameterTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
