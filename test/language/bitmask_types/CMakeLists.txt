add_library(bitmask_types_zs STATIC zs/bitmask_types.zs)
zserio_generate_cpp(
    TARGET bitmask_types_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(bitmask_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(bitmask_types
    DEPENDS
        bitmask_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldBitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldConstBitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskDefinedByConstantTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskUsedByBitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskWithoutNoneTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt64BitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt8BitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarUIntBitmaskTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
