set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_bit8/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_bit8/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_float16/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_float16/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_int24/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_int24/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_uint8/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_uint8/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_uint64/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/implicit_array_uint64/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/lengthof_with_implicit_array/LengthOfWithImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/lengthof_with_implicit_array/LengthOfWithImplicitArray.h

    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/table_with_implicit_array/DbWithImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/table_with_implicit_array/DbWithImplicitArray.h
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/table_with_implicit_array/StructWithImplicit.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/table_with_implicit_array/StructWithImplicit.h
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/table_with_implicit_array/TableWithImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/allow_implicit_arrays/table_with_implicit_array/TableWithImplicitArray.h
)

zserio_add_library(allow_implicit_arrays_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE allow_implicit_arrays.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -allowImplicitArrays -withoutSourcesAmalgamation
)

target_include_directories(allow_implicit_arrays_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_custom_test(allow_implicit_arrays
    DEPENDS
        allow_implicit_arrays_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ImplicitArrayBit8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ImplicitArrayFloat16Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ImplicitArrayInt24Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ImplicitArrayUInt64Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ImplicitArrayUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/LengthOfWithImplicitArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/TableWithImplicitArrayTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
