# Script called from clang_tidy_utils.cmake to run clang-tidy and preserve it's output.
#
# Expected definitions:
#   CLANG_TIDY_BIN Clang Tidy binary.
#   SOURCES        Sources to check by clang-tidy.
#   BUILD_PATH     Build path containing compilation database. Use -DCMAKE_EXPORT_COMPILE_COMMANDS=ON.
#   CONFIG_FILE    Path to the clang-tidy config file.
#   HEADER_FILTER  Header filter to use.
#   LINE_FILTER    Line filter to use.
#   OUTPUT_FILE    Output file to use.
cmake_minimum_required(VERSION 3.1.0)

separate_arguments(SOURCES)

foreach (ARG CLANG_TIDY_BIN SOURCES BUILD_PATH CONFIG_FILE LINE_FILTER)
    if (NOT DEFINED ${ARG})
        message(FATAL_ERROR "Argument '${ARG}' not defined!")
    endif ()
endforeach ()

execute_process(
    COMMAND ${CLANG_TIDY_BIN} ${SOURCES} -p "${BUILD_PATH}"
        --config-file "${CONFIG_FILE}"
        --header-filter "${HEADER_FILTER}"
        --line-filter "${LINE_FILTER}"
    OUTPUT_VARIABLE CLANG_TIDY_OUTPUT
    ERROR_VARIABLE CLANG_TIDY_ERROR_OUTPUT # currently ignored
    RESULT_VARIABLE CLANG_TIDY_RESULT
)

file(APPEND "${OUTPUT_FILE}" "${CLANG_TIDY_OUTPUT}")

if (NOT ${CLANG_TIDY_RESULT} EQUAL 0)
    message(STATUS "Clang Tidy output:\n${CLANG_TIDY_OUTPUT}")
    message(FATAL_ERROR "Clang Tidy Tool failed!")
endif ()
