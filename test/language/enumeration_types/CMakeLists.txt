add_library(enumeration_types_zs STATIC zs/enumeration_types.zs)
zserio_generate_cpp(
    TARGET enumeration_types_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(enumeration_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(enumeration_types
    DEPENDS
        enumeration_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldConstEnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldEnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DeprecatedEnumItemTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumDefinedByConstantTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumUsedByEnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleRemovedEnumItemsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/RemovedEnumItemTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt64EnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt8EnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarUIntEnumTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
