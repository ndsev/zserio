add_library(with_setters_code_wow_zs STATIC ${TEST_ZS_ROOT}/with_setters_code.zs)
zserio_generate_cpp(
    TARGET with_setters_code_wow_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_wow
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutWriterCode -setTopLevelPackage without_writer ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_WOW
    OUTPUT_VAR ZSERIO_LOG_WOW
    ERROR_VAR ZSERIO_LOG_WOW
)
target_link_libraries(with_setters_code_wow_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_setters_code_wow_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_WOW)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_setters_wow.txt "${ZSERIO_LOG_WOW}")
    check_zserio_warnings("${ZSERIO_LOG_WOW}" 0)
endif ()

add_library(with_setters_code_ww_zs STATIC ${TEST_ZS_ROOT}/with_setters_code.zs)
zserio_generate_cpp(
    TARGET with_setters_code_ww_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_ww
    EXTRA_ARGS -withoutSourcesAmalgamation -withWriterCode -setTopLevelPackage with_writer ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_WW
    OUTPUT_VAR ZSERIO_LOG_WW
    ERROR_VAR ZSERIO_LOG_WW
)
target_link_libraries(with_setters_code_ww_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_setters_code_ww_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_WW)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_setters_ww.txt "${ZSERIO_LOG_WW}")
    check_zserio_warnings("${ZSERIO_LOG_WW}" 0)
endif ()

add_library(with_setters_code_wow_ws_zs STATIC ${TEST_ZS_ROOT}/with_setters_code.zs)
zserio_generate_cpp(
    TARGET with_setters_code_wow_ws_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_wow_ws
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutWriterCode -withSettersCode -setTopLevelPackage without_writer_with_setters ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_WOW_WS
    OUTPUT_VAR ZSERIO_LOG_WOW_WS
    ERROR_VAR ZSERIO_LOG_WOW_WS
)
target_link_libraries(with_setters_code_wow_ws_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_setters_code_wow_ws_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_WOW_WS)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_setters_wow_ws.txt "${ZSERIO_LOG_WOW_WS}")
    check_zserio_warnings("${ZSERIO_LOG_WOW_WS}" 0)
endif ()

add_custom_test(with_setters_code
    DEPENDS
        with_setters_code_wow_zs
        with_setters_code_ww_zs
        with_setters_code_wow_ws_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithSettersCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES_WOW}
        ${GENERATED_SOURCES_WW}
        ${GENERATED_SOURCES_WOW_WS}
)
