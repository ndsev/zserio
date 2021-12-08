set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Bitmask.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Bitmask.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/BitmaskLen.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Child.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Child.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Choice.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Choice.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Empty.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Empty.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/EmptyString.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Parameterized.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Parameterized.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Selector.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/SelectorEnum.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/SelectorEnum.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Struct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Struct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Union.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/reflection/Union.h

    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/ComplexStruct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/ComplexStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/EnumUnderlyingType.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/Fts4Table.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/Fts4Table.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/ParameterizedStruct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/ParameterizedStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/RecursiveUnion.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/RecursiveUnion.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/RecursiveChoice.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/RecursiveChoice.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleChoice.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleChoice.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimplePubsub.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimplePubsub.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleService.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleService.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleStruct.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleUnion.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SimpleUnion.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SqlDatabase.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SqlDatabase.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SqlTable.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SqlTable.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/SubtypedSimpleStruct.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TemplatedParameterizedStruct_TS32.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TemplatedParameterizedStruct_TS32.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TemplatedSqlTableU8.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TemplatedSqlTableU8.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TemplatedSqlTable_uint32.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TemplatedSqlTable_uint32.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TestBitmask.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TestEnum.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TS32.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/TS32.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/WithoutRowIdTable.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/WithoutRowIdTable.h
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/WithTypeInfoCode.cpp
    ${ZSERIO_GEN_DIR}/with_type_info_code/type_info/WithTypeInfoCode.h
)

zserio_add_library(TARGET with_type_info_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE with_type_info_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withTypeInfoCode -allowImplicitArrays -withoutSourcesAmalgamation
)

target_include_directories(with_type_info_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/ReflectionTest.cpp
    cpp/TypeInfoTest.cpp
)

add_test_dependencies(with_type_info_code_zs)
