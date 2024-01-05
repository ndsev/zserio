# Script called from clang_format_utils.cmake to run clang-format and preserve it's output.
#
# Expected definitions:
#   CLANG_FORMAT_BIN Clang format binary.
#   SOURCE           Source to check by clang-format.
#   CONFIG_FILE      Path to the clang-format config file.
#   WERROR           Ends with an error in case of any format violation.
cmake_minimum_required(VERSION 3.6.0)

foreach (ARG CLANG_FORMAT_BIN SOURCE CONFIG_FILE WERROR)
    if (NOT DEFINED ${ARG})
        message(FATAL_ERROR "Argument '${ARG}' not defined!")
    endif ()
endforeach ()

if (WERROR)
    set(WERROR_OPTION --Werror)
endif ()

execute_process(
    COMMAND ${CLANG_FORMAT_BIN} --style=file:${CONFIG_FILE} --dry-run ${WERROR_OPTION} ${SOURCE}
    RESULT_VARIABLE CLANG_FORMAT_RESULT
)

if (NOT ${CLANG_FORMAT_RESULT} EQUAL 0)
    message(NOTICE "Command hints to reformat source using clang-format tool:")
    message(NOTICE "    git clang-format")
    message(NOTICE "    clang-format --style=file:${CONFIG_FILE} -i ${SOURCE}")
    message(FATAL_ERROR "Clang Format Tool failed!")
endif ()
