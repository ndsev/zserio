add_library(gif_zs STATIC zs/gif.zs)
zserio_generate_cpp(
    TARGET gif_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(gif_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_command(OUTPUT gif_data_copy
                   COMMAND ${CMAKE_COMMAND} -E copy_directory ${CMAKE_SOURCE_DIR}/data/others/gif
                           ${CMAKE_CURRENT_BINARY_DIR}/data
                   DEPENDS ${CMAKE_SOURCE_DIR}/data/others/gif
                   COMMENT "Copying data directory for gif test")
add_custom_target(gif_data_copy_target DEPENDS gif_data_copy)
add_dependencies(gif_zs gif_data_copy_target)

add_custom_test(gif
    DEPENDS
        gif_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/GifTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
