add_library(allow_implicit_arrays_zs STATIC zs/allow_implicit_arrays.zs)
zserio_generate_cpp(
    TARGET allow_implicit_arrays_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -allowImplicitArrays
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(allow_implicit_arrays_zs PUBLIC ZserioCppRuntime)
target_include_directories(allow_implicit_arrays_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

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
        ${GENERATED_SOURCES}
)
