add_library(with_range_check_code_zs STATIC zs/with_range_check_code.zs)
zserio_generate_cpp(
    TARGET with_range_check_code_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withRangeCheckCode
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(with_range_check_code_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(with_range_check_code
    DEPENDS
        with_range_check_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Bit4RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceBit4RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DynamicBitRangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DynamicIntRangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Int4RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Int7ArrayRangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Int8RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OptionalBit31RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt8RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionInt4RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarInt16RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarInt32RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarInt64RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarSizeRangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarUInt16RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarUInt32RangeCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarUInt64RangeCheckTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
