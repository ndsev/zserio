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
#     IGNORE_WARNINGS Whether to ignore warnings.
#     IGNORE_ERRORS Whether to ignore errors.
#     LOG_FILENAME Name of file where to store the zserio error log.
cmake_minimum_required(VERSION 3.1.0)

separate_arguments(OPTIONS)
separate_arguments(EXTRA_OPTIONS)

if (WIN32)
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
        file(APPEND ${LOG_FILENAME} ${ZSERIO_LOG})
    endif ()
    if (NOT IGNORE_ERRORS)
        message(FATAL_ERROR "Zserio tool failed!")
    endif ()
endif ()

if (${ZSERIO_RESULT} EQUAL 0 AND NOT "${ZSERIO_LOG}" STREQUAL "")
    message(STATUS ${ZSERIO_LOG})
    if (NOT "${LOG_FILENAME}" STREQUAL "")
        file(APPEND ${LOG_FILENAME} ${ZSERIO_LOG})
    endif ()
    if (NOT IGNORE_WARNINGS)
        message(FATAL_ERROR "Zserio tool produced some warnings!")
    endif ()
endif ()
