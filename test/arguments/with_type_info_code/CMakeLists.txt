set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/with_type_info_code/ComplexStruct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/ComplexStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/EnumUnderlyingType.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/Fts4Table.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/Fts4Table.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/ParameterizedStruct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/ParameterizedStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/RecursiveChoice.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/RecursiveChoice.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/RecursiveStruct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/RecursiveStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/RecursiveUnion.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/RecursiveUnion.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleChoice.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleChoice.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimplePubsub.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimplePubsub.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleService.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleService.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleStruct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleUnion.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/SimpleUnion.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SqlDatabase.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/SqlDatabase.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SqlTable.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/SqlTable.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/SubtypedSimpleStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/TemplatedParameterizedStruct_TS32.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/TemplatedParameterizedStruct_TS32.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/TemplatedSqlTableU8.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/TemplatedSqlTableU8.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/TemplatedSqlTable_uint32.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/TemplatedSqlTable_uint32.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/TestBitmask.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/TestEnum.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/TS32.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/TS32.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/WithoutRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/WithoutRowIdTable.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/WithTypeInfoCode.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/WithTypeInfoCode.h
)

zserio_add_library(TARGET with_type_info_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE with_type_info_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withTypeInfoCode -allowImplicitArrays -withoutSourcesAmalgamation -withReflectionCode
)

target_include_directories(with_type_info_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/DebugStringTest.cpp
    cpp/WithTypeInfoCodeCreator.cpp
    cpp/WithTypeInfoCodeCreator.h
    cpp/WithTypeInfoCodeTest.cpp
)

add_test_dependencies(with_type_info_code_zs)
