add_library(with_bit_position_zs STATIC zs/with_bit_position.zs)
zserio_generate_cpp(
    TARGET with_bit_position_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode -withBitPositionCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(with_bit_position_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_bit_position_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(with_bit_position
    DEPENDS
        with_bit_position_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithBitPositionTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
