add_library(without_writer_code_zs STATIC zs/without_writer_code.zs)
zserio_generate_cpp(
    TARGET without_writer_code_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutWriterCode -withTypeInfoCode -withReflectionCode
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(without_writer_code_zs PUBLIC ZserioCppRuntime)
target_include_directories(without_writer_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(without_writer_code
    DEPENDS
        without_writer_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutWriterCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
