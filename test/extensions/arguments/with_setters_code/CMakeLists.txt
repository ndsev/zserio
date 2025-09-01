add_library(with_setters_without_writer_code_zs STATIC ${TEST_ZS_ROOT}/with_setters_code.zs)
zserio_generate_cpp(
    TARGET with_setters_without_writer_code_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withSettersCode -withoutWriterCode -setTopLevelPackage without_writer ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_WOW
    OUTPUT_VAR ZSERIO_LOG_WOW
    ERROR_VAR ZSERIO_LOG_WOW
)
target_link_libraries(with_setters_without_writer_code_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_setters_without_writer_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_WOW)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_setters_without_writer.txt "${ZSERIO_LOG_WOW}")
    check_zserio_warnings("${ZSERIO_LOG_WOW}" 0)
endif ()

add_library(with_setters_with_writer_code_zs STATIC ${TEST_ZS_ROOT}/with_setters_code.zs)
zserio_generate_cpp(
    TARGET with_setters_with_writer_code_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withSettersCode -withWriterCode -setTopLevelPackage with_writer ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_WW
    OUTPUT_VAR ZSERIO_LOG_WW
    ERROR_VAR ZSERIO_LOG_WW
)
target_link_libraries(with_setters_with_writer_code_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_setters_with_writer_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_WW)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_setters_with_writer.txt "${ZSERIO_LOG_WW}")
    check_zserio_warnings("${ZSERIO_LOG_WW}" 0)
endif ()

add_custom_test(with_setters_code
    DEPENDS
        with_setters_without_writer_code_zs
        with_setters_with_writer_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithSettersCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES_WOW}
        ${GENERATED_SOURCES_WW}
)
