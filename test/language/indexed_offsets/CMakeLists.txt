add_library(indexed_offsets_zs STATIC zs/indexed_offsets.zs)
zserio_generate_cpp(
    TARGET indexed_offsets_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(indexed_offsets_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(indexed_offsets
    DEPENDS
        indexed_offsets_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoIndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Bit5IndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BoolIndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/CompoundIndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyIndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/Int14IndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OptionalIndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OptionalNestedIndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoIndexedOffsetArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedIndexedOffsetArrayHolderTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarInt32IndexedOffsetArrayTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
