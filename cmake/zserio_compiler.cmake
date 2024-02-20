# A function which allows to automatically generate C++ sources from zserio schemas.
#
# Prerequisites:
#    CMake 3.15+
#    JAVA must be available - calls find_program(JAVA java)
#    ZSERIO_JAR_FILE must be set either as an environment or CMake variable
#
# Usage:
#    zserio_generate_cpp(
#        TARGET <target>
#        [SRC_DIR <directory>]
#        [MAIN_ZS <file>]
#        [GEN_DIR <directory>]
#        [EXTRA_ARGS <argument>...]
#        [GENERATED_SOURCES_VAR <variable>]
#        [OUTPUT_VAR <variable>]
#        [ERROR_VAR <variable>]
#        [RESULT_VAR <variable>]
#        [FORCE_REGENERATION]
#        [CLEAN_GEN_DIR]
#
# Arguments:
# TARGET - Target to which the generated sources will be assigned.
# SRC_DIR - Source directory for zserio schemas. Optional, defaults to CMAKE_CURRENT_SOURCE_DIR.
# MAIN_ZS - Main zserio schema. Optional if the MAIN_ZS file is specified as a source for the given TARGET.
# GEN_DIR - Directory where the C++ sources will be generated.
# EXTRA_ARGS - Extra arguments to be passed to the Zserio tool.
# GENERATED_SOURCES_VAR - The variable will be set with a list of generated source files (full paths).
#                         Optional.
# OUTPUT_VAR - The variable will be set with the contents of the standard output pipe. Optional.
#              If not set, the standard output pipe is printed.
# ERROR_VAR - The variable will be set with the contents of the standard error pipe. Optional.
#             If not set, the standard error pipe is printed.
# RESULT_VAR - The variable will be set to contain the result of the zserio generator. Optional.
#              If not set, a FATAL_ERROR is raised in case of the zserio generator error.
# FORCE_RECONFIGURE - Forces regeneration every time the CMake configure is run.
# CLEAN_GEN_DIR - Cleans GEN_DIR when generation in CMake configure time is run.
#
# Note that OUTPUT_VAR and ERROR_VAR can be set to the same value and then both pipes will be merged.
#
# Note that OUTPUT_VAR, ERROR_VAR and RESULT_VAR are updated only when the generation is executed within
# the configure-time - i.e. for the first time or when zserio schema sources are changed, etc.
# See "How if works" for more info.
#
# Example:
#    set(CMAKE_MODULE_PATH "${ZSERIO_RELEASE}/cmake")
#    set(ZSERIO_JAR_FILE "${ZSERIO_RELEASE}/zserio.jar")
#    include(zserio_compiler)
#
#    add_library(sample_zs sample.zs)
#    zserio_generate_cpp(
#        TARGET sample_zs
#        GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
#
# How it works:
#
# First time the CMake configure is run, the sources are generated using execute_process directly in
# configure-time and auxilary information (timestamps, list of generated sources, etc.) is stored in the
# CMake cache. The auxilary info is used to define a custom command which uses the same zserio command line
# as the original execute_process and thus allows to re-generate sources when it's needed - e.g. after the
# clean step.
#
# The custom command is sufficient as long as the generated sources remains unchanged. Otherwise the
# execute_process must be re-run in configure-time to ensure that all generated sources are collected correctly.
# This functionality is achieved using the auxilary information mentioned above.
#
# List of generated sources can change in following situations:
#
# - ZSERIO_JAR_FILE is changed
# - zserio schema sources are changed
# - EXTRA_ARGS are changed

if (CMAKE_VERSION VERSION_LESS "3.15.0")
    message(FATAL_ERROR "CMake 3.15+ is required!")
endif ()

function(zserio_generate_cpp)
    find_program(JAVA java)
    if (NOT JAVA)
        message(FATAL_ERROR "Could not find java!")
    endif()
    if (DEFINED ENV{ZSERIO_JAR_FILE})
        set(ZSERIO_JAR_FILE $ENV{ZSERIO_JAR_FILE})
    endif ()
    if (NOT DEFINED ZSERIO_JAR_FILE)
        message(FATAL_ERROR "Could not find zserio.jar, ZSERIO_JAR_FILE not defined!")
    endif()
    foreach (JAR_FILE IN LISTS ZSERIO_JAR_FILE)
        if (NOT EXISTS "${JAR_FILE}")
            message(FATAL_ERROR "Zserio jar file '${JAR_FILE}' doesn't exist!")
        endif ()
    endforeach ()

    cmake_parse_arguments(ZSERIO_GENERATE
        "FORCE_REGENERATE;CLEAN_GEN_DIR"
        "TARGET;SRC_DIR;MAIN_ZS;GEN_DIR;GENERATED_SOURCES_VAR;OUTPUT_VAR;ERROR_VAR;RESULT_VAR"
        "EXTRA_ARGS"
        ${ARGN})

    if (ZSERIO_GENERATE_UNPARSED_ARGUMENTS)
        message(FATAL_ERROR "zserio_generate_cpp: Unknown arguments '${ZSERIO_GENERATE_UNPARSED_ARGUMENTS}'!")
    endif ()

    # check required arguments
    foreach (ARG TARGET GEN_DIR)
        if (NOT DEFINED ZSERIO_GENERATE_${ARG})
            message(FATAL_ERROR "No value defined for required argument ${ARG}!")
        endif ()
    endforeach ()

    # default values
    if (NOT DEFINED ZSERIO_GENERATE_SRC_DIR)
        set(ZSERIO_GENERATE_SRC_DIR "${CMAKE_CURRENT_SOURCE_DIR}")
    endif ()
    if (NOT DEFINED ZSERIO_GENERATE_MAIN_ZS)
        # try to get a single main zs
        get_target_property(ZS_SOURCES ${ZSERIO_GENERATE_TARGET} SOURCES)
        list(FILTER ZS_SOURCES INCLUDE REGEX "\\.zs$")
        list(LENGTH ZS_SOURCES ZS_SOURCES_LENGTH)
        if (${ZS_SOURCES_LENGTH} EQUAL 1)
            list(GET ZS_SOURCES 0 ZSERIO_GENERATE_MAIN_ZS)

            # try to map the found source to ZSERIO_GENERATE_SRC_DIR
            if (NOT IS_ABSOLUTE ${ZSERIO_GENERATE_MAIN_ZS})
                set(ZSERIO_GENERATE_MAIN_ZS "${CMAKE_CURRENT_SOURCE_DIR}/${ZSERIO_GENERATE_MAIN_ZS}")
            endif ()
            file(RELATIVE_PATH ZSERIO_GENERATE_MAIN_ZS
                "${ZSERIO_GENERATE_SRC_DIR}" "${ZSERIO_GENERATE_MAIN_ZS}")
        else ()
            message(FATAL_ERROR "MAIN_ZS file not specifid and cannot be detected!")
        endif ()
    endif ()

    # ensure cmake reconfigure when zserio sources are changed
    file(GLOB_RECURSE ZSERIO_SOURCES RELATIVE "${CMAKE_CURRENT_SOURCE_DIR}"
        "${ZSERIO_GENERATE_SRC_DIR}/*.zs")
    set_property(DIRECTORY APPEND PROPERTY CMAKE_CONFIGURE_DEPENDS ${ZSERIO_SOURCES} ${ZSERIO_JAR_FILE})

    # don't use WIN32 because it can be set during cross-compilation on Linux
    if ("${CMAKE_HOST_SYSTEM_NAME}" STREQUAL "Windows")
        set(JAVA_CLASSPATH_SEPARATOR ";")
    else ()
        set(JAVA_CLASSPATH_SEPARATOR ":")
    endif ()
    list(JOIN ZSERIO_JAR_FILE "${JAVA_CLASSPATH_SEPARATOR}" ZSERIO_CLASSPATH)

    set(ZSERIO_COMMAND
        zserio.tools.ZserioTool
            -src ${ZSERIO_GENERATE_SRC_DIR} ${ZSERIO_GENERATE_MAIN_ZS}
            -cpp ${ZSERIO_GENERATE_GEN_DIR}
            ${ZSERIO_GENERATE_EXTRA_ARGS}
    )

    _zserio_compiler_get_latest_timestamp(FILES ${ZSERIO_SOURCES} TIMESTAMP_VAR ZSERIO_SOURCES_TIMESTAMP)
    _zserio_compiler_get_latest_timestamp(FILES ${ZSERIO_JAR_FILE} TIMESTAMP_VAR ZSERIO_JAR_TIMESTAMP)

    set(REGENERATE_SOURCES 1)
    if (NOT ZSERIO_GENERATE_FORCE_REGENERATE AND
        DEFINED ZSERIO_COMPILER_ZSERIO_JAR_TIMESTAMP_${ZSERIO_GENERATE_TARGET} AND
        DEFINED ZSERIO_COMPILER_SOURCES_TIMESTAMP_${ZSERIO_GENERATE_TARGET} AND
        DEFINED ZSERIO_COMPILER_SOURCES_${ZSERIO_GENERATE_TARGET} AND
        DEFINED ZSERIO_COMPILER_EXTRA_ARGS_${ZSERIO_GENERATE_TARGET} AND
        DEFINED ZSERIO_COMPILER_GENERATED_SOURCES_${ZSERIO_GENERATE_TARGET})
        if (${ZSERIO_COMPILER_ZSERIO_JAR_TIMESTAMP_${ZSERIO_GENERATE_TARGET}} EQUAL ${ZSERIO_JAR_TIMESTAMP} AND
            ${ZSERIO_COMPILER_SOURCES_TIMESTAMP_${ZSERIO_GENERATE_TARGET}} EQUAL ${ZSERIO_SOURCES_TIMESTAMP} AND
            "${ZSERIO_COMPILER_EXTRA_ARGS_${ZSERIO_GENERATE_TARGET}}" STREQUAL
                "${ZSERIO_GENERATE_EXTRA_ARGS}" AND
            "${ZSERIO_COMPILER_SOURCES_${ZSERIO_GENERATE_TARGET}}" STREQUAL "${ZSERIO_SOURCES}")
            set(REGENERATE_SOURCES 0)
        endif ()
    endif ()

    set(TOOL_COMMENT "Generating C++ sources from '${ZSERIO_GENERATE_MAIN_ZS}'.")

    if (REGENERATE_SOURCES)
        if ("${ZSERIO_GENERATE_OUTPUT_VAR}" STREQUAL "${ZSERIO_GENERATE_ERROR_VAR}")
            set(SEPARATE_OUTPUT OFF)
            set(ZSERIO_OUTPUT_VAR ZSERIO_OUTPUT)
            set(ZSERIO_ERROR_VAR ZSERIO_OUTPUT)
        else ()
            set(SEPARATE_OUTPUT ON)
            set(ZSERIO_OUTPUT_VAR ZSERIO_OUTPUT)
            set(ZSERIO_ERROR_VAR ZSERIO_ERROR)
        endif ()

        if (ZSERIO_GENERATE_CLEAN_GEN_DIR)
            file(REMOVE_RECURSE ${ZSERIO_GENERATE_GEN_DIR})
            file(MAKE_DIRECTORY ${ZSERIO_GENERATE_GEN_DIR})
        endif ()

        message(STATUS ${TOOL_COMMENT})

        # run the generator during configure phase for the first time
        execute_process(
            COMMAND ${JAVA} -cp "${ZSERIO_CLASSPATH}" ${ZSERIO_COMMAND}
            WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
            OUTPUT_VARIABLE ${ZSERIO_OUTPUT_VAR}
            ERROR_VARIABLE ${ZSERIO_ERROR_VAR}
            RESULT_VARIABLE ZSERIO_RESULT)

        if (${SEPARATE_OUTPUT})
            if (DEFINED ZSERIO_GENERATE_OUTPUT_VAR)
                set(${ZSERIO_GENERATE_OUTPUT_VAR} ${ZSERIO_OUTPUT} PARENT_SCOPE)
            else ()
                message(STATUS ${ZSERIO_OUTPUT})
            endif ()

            if (DEFINED ZSERIO_GENERATE_ERROR_VAR)
                set(${ZSERIO_GENERATE_ERROR_VAR} ${ZSERIO_ERROR} PARENT_SCOPE)
            else ()
                message(STATUS ${ZSERIO_ERROR})
            endif ()
        else ()
            # both OUTPUT_VAR and ERROR_VAR are either not set or set to the same value
            if (DEFINED ZSERIO_GENERATE_OUTPUT_VAR)
                set(${ZSERIO_GENERATE_OUTPUT_VAR} ${ZSERIO_OUTPUT} PARENT_SCOPE)
            else ()
                message(STATUS ${ZSERIO_OUTPUT})
            endif ()
        endif ()

        if (DEFINED ZSERIO_GENERATE_RESULT_VAR)
            set(${ZSERIO_GENERATE_RESULT_VAR} ${ZSERIO_RESULT} PARENT_SCOPE)
        endif ()

        if (NOT ${ZSERIO_RESULT} EQUAL 0 AND NOT DEFINED ZSERIO_GENERATE_RESULT_VAR)
            message(STATUS ${${ZSERIO_ERROR_VAR}})
            message(FATAL_ERROR "Zserio generator failed!")
        endif ()

        file(GLOB_RECURSE GENERATED_SOURCES
            "${ZSERIO_GENERATE_GEN_DIR}/*.h"
            "${ZSERIO_GENERATE_GEN_DIR}/*.cpp")

        set(ZSERIO_COMPILER_ZSERIO_JAR_TIMESTAMP_${ZSERIO_GENERATE_TARGET} "${ZSERIO_JAR_TIMESTAMP}"
            CACHE INTERNAL "Timestamp of the '${ZSERIO_JAR_FILE}'"
            FORCE)
        set(ZSERIO_COMPILER_SOURCES_TIMESTAMP_${ZSERIO_GENERATE_TARGET} "${ZSERIO_SOURCES_TIMESTAMP}"
            CACHE INTERNAL "Latest timestamp of the Zserio schema sources for ${ZSERIO_GENERATE_TARGET}."
            FORCE)
        set(ZSERIO_COMPILER_SOURCES_${ZSERIO_GENERATE_TARGET} "${ZSERIO_SOURCES}"
            CACHE INTERNAL "List of Zserio schema sources for ${ZSERIO_GENERATE_TARGET}."
            FORCE)
        set(ZSERIO_COMPILER_EXTRA_ARGS_${ZSERIO_GENERATE_TARGET} "${ZSERIO_GENERATE_EXTRA_ARGS}"
            CACHE INTERNAL "Extra arguments to Zserio tool used for ${ZSERIO_GENERATE_TARGET}."
            FORCE)
        set(ZSERIO_COMPILER_GENERATED_SOURCES_${ZSERIO_GENERATE_TARGET} "${GENERATED_SOURCES}"
            CACHE INTERNAL "List of sources files generated by Zserio for ${ZSERIO_GENERATE_TARGET}."
            FORCE)
    else ()
        set(GENERATED_SOURCES "${ZSERIO_COMPILER_GENERATED_SOURCES_${ZSERIO_GENERATE_TARGET}}")
    endif ()

    if (NOT "${GENERATED_SOURCES}" STREQUAL "")
        if (DEFINED ZSERIO_GENERATE_GENERATED_SOURCES_VAR)
            set(${ZSERIO_GENERATE_GENERATED_SOURCES_VAR} "${GENERATED_SOURCES}" PARENT_SCOPE)
        endif ()

        add_custom_command(OUTPUT ${GENERATED_SOURCES}
            COMMAND ${JAVA} -cp "${ZSERIO_CLASSPATH}" ${ZSERIO_COMMAND}
            DEPENDS ${ZSERIO_SOURCES}
            COMMENT ${TOOL_COMMENT})

        target_sources(${ZSERIO_GENERATE_TARGET} PRIVATE ${GENERATED_SOURCES})
        set_source_files_properties(${GENERATED_SOURCES} PROPERTIES GENERATED TRUE)

        get_target_property(TARGET_TYPE ${ZSERIO_GENERATE_TARGET} TYPE)
        if ("${TARGET_TYPE}" STREQUAL "INTERFACE_LIBRARY")
            target_include_directories(${ZSERIO_GENERATE_TARGET} INTERFACE ${ZSERIO_GENERATE_GEN_DIR})
        else ()
            target_include_directories(${ZSERIO_GENERATE_TARGET} PUBLIC ${ZSERIO_GENERATE_GEN_DIR})
        endif ()

        set_target_properties(${ZSERIO_GENERATE_TARGET} PROPERTIES
            ADDITIONAL_CLEAN_FILES "${GENERATED_SOURCES}")
    endif ()
endfunction()

function(_zserio_compiler_get_latest_timestamp)
    cmake_parse_arguments(GET_LATEST_TIMESTAMP "" "TIMESTAMP_VAR" "FILES" ${ARGN})

    set(LATEST_TIMESTAMP 0)
    foreach(FILE ${GET_LATEST_TIMESTAMP_FILES})
        if (NOT IS_ABSOLUTE ${FILE})
            set(FILE "${CMAKE_CURRENT_SOURCE_DIR}/${FILE}")
        endif ()

        file(TIMESTAMP ${FILE} FILE_TIMESTAMP "%s")
        if (${FILE_TIMESTAMP} GREATER ${LATEST_TIMESTAMP})
            set(LATEST_TIMESTAMP ${FILE_TIMESTAMP})
        endif ()
    endforeach()
    set(${GET_LATEST_TIMESTAMP_TIMESTAMP_VAR} ${LATEST_TIMESTAMP} PARENT_SCOPE)
endfunction()
