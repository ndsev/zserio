add_library(without_writer_without_setters_code_zs STATIC ${TEST_ZS_ROOT}/without_writer_code.zs)
zserio_generate_cpp(
    TARGET without_writer_without_setters_code_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_without_setters
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutWriterCode -withoutSettersCode -withTypeInfoCode -withReflectionCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(without_writer_without_setters_code_zs PUBLIC ZserioCppRuntime)
target_include_directories(without_writer_without_setters_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_wos_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_library(without_writer_with_setters_code_zs STATIC ${TEST_ZS_ROOT}/without_writer_code.zs)
zserio_generate_cpp(
    TARGET without_writer_with_setters_code_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_with_setters
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutWriterCode -withSettersCode -withTypeInfoCode -withReflectionCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(without_writer_with_setters_code_zs PUBLIC ZserioCppRuntime)
target_include_directories(without_writer_with_setters_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_ws_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(without_writer_without_setters_code
    DEPENDS
        without_writer_without_setters_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutWriterWithoutSettersCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)

add_custom_test(without_writer_with_setters_code
    DEPENDS
        without_writer_with_setters_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutWriterWithSettersCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
