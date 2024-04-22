add_library(with_type_info_code_zs STATIC zs/with_type_info_code.zs)
zserio_generate_cpp(
    TARGET with_type_info_code_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode -allowImplicitArrays ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(with_type_info_code_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_type_info_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(with_type_info_code
    DEPENDS
        with_type_info_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DebugStringTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithTypeInfoCodeCreator.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithTypeInfoCodeCreator.h
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithTypeInfoCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
