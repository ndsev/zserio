add_library(sql_tables_zs STATIC zs/sql_tables.zs)
zserio_generate_cpp(
    TARGET sql_tables_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(sql_tables_zs PUBLIC ZserioCppRuntime)
target_include_directories(sql_tables_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 1)
endif ()

add_custom_test(sql_tables
    DEPENDS
        sql_tables_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BlobFieldWithChildrenInitializationTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BlobOffsetsParamTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BlobParamTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ColumnParamTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ComplexTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ConstParamTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DynamicBitFieldEnumFieldTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultiplePkTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypedBitmaskFieldTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypedBoolFieldTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypedEnumFieldTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypedTableTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutPkTableTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
