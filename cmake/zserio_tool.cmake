# Script called from zserio_utils.cmake to check that tests does not fire any warnings
#
# Expected defines:
#     JAVA_BIN Java binary.
#     CORE_DIR Zserio core directory.
#     CPP_DIR Zserio C++ extension directory.
#     OUT_DIR Zserio output directory.
#     SOURCE_DIR Zserio source directory.
#     MAIN_SOURCE Zserio main source file.
#     OPTIONS Zserio tool options.
#     EXTRA_OPTIONS Zserio tool extra options.
#     EXPECTED_WARNINGS Number of expected zserio warnings to check.
#     IGNORE_ERRORS Whether to ignore errors.
#     LOG_FILENAME Name of file where to store the zserio error log.
#     APPEND_TO_LOG_FILE Whether to append errors to the zserio error log.
cmake_minimum_required(VERSION 3.1.0)

separate_arguments(OPTIONS)
separate_arguments(EXTRA_OPTIONS)

# don't use WIN32 because it can be set during cross-compilation on Linux
if ("${CMAKE_HOST_SYSTEM_NAME}" STREQUAL "Windows")
    set(JAVA_CLASSPATH_SEPARATOR ";")
else ()
    set(JAVA_CLASSPATH_SEPARATOR ":")
endif ()

execute_process(
    COMMAND ${JAVA_BIN} -cp "${CORE_DIR}/zserio_core.jar${JAVA_CLASSPATH_SEPARATOR}${CPP_DIR}/zserio_cpp.jar"
        zserio.tools.ZserioTool ${OPTIONS} ${EXTRA_OPTIONS} -cpp ${OUT_DIR} -src ${SOURCE_DIR} ${MAIN_SOURCE}
    ERROR_VARIABLE ZSERIO_LOG
    RESULT_VARIABLE ZSERIO_RESULT
)

if (NOT ${ZSERIO_RESULT} EQUAL 0)
    message(STATUS ${ZSERIO_LOG})
    if (NOT "${LOG_FILENAME}" STREQUAL "")
        if (APPEND_TO_LOG_FILE)
            file(APPEND ${LOG_FILENAME} ${ZSERIO_LOG})
        else ()
            file(WRITE ${LOG_FILENAME} ${ZSERIO_LOG})
        endif ()
    endif ()
    if (NOT IGNORE_ERRORS)
        message(FATAL_ERROR "Zserio tool failed!")
    endif ()
endif ()

set(NUM_WARNINGS 0)
if (${ZSERIO_RESULT} EQUAL 0 AND NOT "${ZSERIO_LOG}" STREQUAL "")
    message(STATUS ${ZSERIO_LOG})
    if (NOT "${LOG_FILENAME}" STREQUAL "")
        if (APPEND_TO_LOG_FILE)
            file(APPEND ${LOG_FILENAME} ${ZSERIO_LOG})
        else ()
            file(WRITE ${LOG_FILENAME} ${ZSERIO_LOG})
        endif ()
    endif ()

    string(REGEX MATCHALL "\\[WARNING\\]" WARNINGS ${ZSERIO_LOG})
    list(LENGTH WARNINGS NUM_WARNINGS)
endif ()

if (NOT ${NUM_WARNINGS} EQUAL ${EXPECTED_WARNINGS})
    message(FATAL_ERROR "Zserio tool produced ${NUM_WARNINGS} warning(s) (expected ${EXPECTED_WARNINGS})!")
endif ()
