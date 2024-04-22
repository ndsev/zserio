add_library(offsets_zs STATIC zs/offsets.zs)
zserio_generate_cpp(
    TARGET offsets_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(offsets_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(offsets
    DEPENDS
        offsets_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArrayOffsetTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitOffsetTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/NestedOffsetTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OptionalMemberOffsetTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayOffsetTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParameterOffsetTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/TernaryOperatorOffsetTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt64OffsetTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
