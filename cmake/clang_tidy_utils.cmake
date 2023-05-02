# A function to create clang-tidy target.
#
# Usage clang_tidy_add_custom_target
#   DEPENDS           Optional list of dependencies.
#   SOURCES           List of source directories for clang-tidy.
#   SOURCES_GLOBS     List of sources globbing expressions which will be searched using GLOG_RECURSE.
#   BUILD_PATH        Build path which contains the compilation database.
#                     Use -DCMAKE_EXPORT_COMPILE_COMMANDS=ON!
#   CONFIG_FILE       Path to .clang-tidy config file.
#                     Note that WarningsAsError should not be used in the config file since this
#                     utility implements its own logic based on the SUPPRESSIONS_FILE.
#   HEADER_FILTER     Optional header filter which will be passed as a --header-filter clang-tidy
#                     argument. Default is '.*'.
#   OUTPUT_FILE       Optional file name where the clang-tidy output will be stored.
#                     Default is 'clang-tidy.log'.
#   SUPPRESSIONS_FILE Suppressions file for clang-tidy warnings. If any warnings are fired but not suppressed,
#                     the clang-tidy target will fail. See clang_tidy_check.cmake for syntax. If omitted,
#                     the ClangTidySuppressions.txt placed in ${CMAKE_CURRENT_SOURCE_DIR} is used if it exists.
#   WERROR            Ends with an error in case of any unsuppressed clang-tidy warnings. Default is ON.
#   WERROR_UNUSED_SUPPRESSIONS
#                     Fires an error in case of unused suppressions. Default is OFF.
# Note that only implementation files ('*.cpp') are used as sources.
function(clang_tidy_add_custom_target CLANG_TIDY_TARGET)
    cmake_parse_arguments(CLANG_TIDY
        ""
        "BUILD_PATH;CONFIG_FILE;HEADER_FILTER;OUTPUT_FILE;SUPPRESSIONS_FILE;WERROR;WERROR_UNUSED_SUPPRESSIONS"
        "DEPENDS;SOURCES;SOURCES_GLOBS"
        ${ARGN}
    )

    # check required arguments
    foreach (ARG TARGET BUILD_PATH CONFIG_FILE)
        if (NOT DEFINED CLANG_TIDY_${ARG})
            message(FATAL_ERROR "No value defined for required argument ${ARG}!")
        endif ()
    endforeach ()

    if (NOT DEFINED CLANG_TIDY_SOURCES AND NOT DEFINED CLANG_TIDY_SOURCES_GLOBS)
        message(FATAL_ERROR "No value defined neither for SOURCES nor SOURCES_GLOBS!")
    endif ()

    # process optional arguments
    if (NOT DEFINED CLANG_TIDY_HEADER_FILTER)
        set(CLANG_TIDY_HEADER_FILTER ".*")
    endif ()

    if (NOT DEFINED CLANG_TIDY_OUTPUT_FILE)
        set(CLANG_TIDY_OUTPUT_FILE "clang-tidy-report.txt")
    endif ()

    if (NOT DEFINED CLANG_TIDY_SUPPRESSIONS_FILE)
        if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/ClangTidySuppressions.txt")
            set(CLANG_TIDY_SUPPRESSIONS_FILE "${CMAKE_CURRENT_SOURCE_DIR}/ClangTidySuppressions.txt")
        endif ()
    endif ()

    if (NOT DEFINED CLANG_TIDY_WERROR)
        set(CLANG_TIDY_WERROR ON)
    endif ()

    if (NOT DEFINED CLANG_TIDY_WERROR_UNUSED_SUPPRESSIONS)
        set(CLANG_TIDY_WERROR_UNUSED_SUPPRESSIONS OFF)
    endif ()

    # process sources
    list(APPEND CLANG_TIDY_SOURCES_LIST ${CLANG_TIDY_SOURCES})
    foreach (SOURCE_EXPRESSION ${CLANG_TIDY_SOURCES_GLOBS})
        file(GLOB_RECURSE MATCHING_SOURCES "${SOURCE_EXPRESSION}")
        list(APPEND CLANG_TIDY_SOURCES_LIST ${MATCHING_SOURCES})
    endforeach ()
    list(REMOVE_DUPLICATES CLANG_TIDY_SOURCES_LIST)
    list(FILTER CLANG_TIDY_SOURCES_LIST INCLUDE REGEX ".*\.c(pp)?$")

    if (CLANG_TIDY_BIN)
        set(CLANG_TIDY_TIMESTAMP_FILE "clang-tidy/${CLANG_TIDY_TARGET}-timestamp")
        add_custom_command(
            OUTPUT "${CLANG_TIDY_TIMESTAMP_FILE}" "${CLANG_TIDY_OUTPUT_FILE}"
            COMMAND "${CMAKE_COMMAND}" -E rm -f "${CLANG_TIDY_OUTPUT_FILE}"
            COMMAND "${CMAKE_COMMAND}" -E touch "${CLANG_TIDY_OUTPUT_FILE}"
            COMMAND "${CMAKE_COMMAND}" -E make_directory clang-tidy
            COMMAND "${CMAKE_COMMAND}" -E touch "${CLANG_TIDY_TIMESTAMP_FILE}"
            DEPENDS "${CLANG_TIDY_DEPENDS}" "${CLANG_TIDY_CONFIG_FILE}"
            COMMENT "Prepare clear ${CLANG_TIDY_OUTPUT_FILE} in ${CLANG_TIDY_TARGET}"
        )

        set(INDEX 0)
        foreach (SOURCE_FILE ${CLANG_TIDY_SOURCES_LIST})
            set(CLANG_TIDY_FILE_STAMP "clang-tidy/${CLANG_TIDY_TARGET}-${INDEX}")
            add_custom_command(
                OUTPUT "${CLANG_TIDY_FILE_STAMP}"
                COMMAND "${CMAKE_COMMAND}"
                    -DCLANG_TIDY_BIN="${CLANG_TIDY_BIN}"
                    -DSOURCES="${SOURCE_FILE}"
                    -DBUILD_PATH="${CLANG_TIDY_BUILD_PATH}"
                    -DCONFIG_FILE="${CLANG_TIDY_CONFIG_FILE}"
                    -DHEADER_FILTER="${CLANG_TIDY_HEADER_FILTER}"
                    -DOUTPUT_FILE="${CLANG_TIDY_OUTPUT_FILE}"
                    -P ${CMAKE_MODULE_PATH}/clang_tidy_tool.cmake
                COMMAND "${CMAKE_COMMAND}" -E touch "${CLANG_TIDY_FILE_STAMP}"
                DEPENDS "${CLANG_TIDY_TIMESTAMP_FILE}"
                COMMENT "Running clang-tidy on ${SOURCE_FILE}(${INDEX})"
            )
            list(APPEND CLANG_TIDY_FILE_STAMPS "${CLANG_TIDY_FILE_STAMP}")
            math(EXPR INDEX "${INDEX} + 1")
        endforeach ()

        set(CLANG_TIDY_CHECK_FILE "clang-tidy/${CLANG_TIDY_TARGET}-check")
        add_custom_command(
            OUTPUT "${CLANG_TIDY_CHECK_FILE}"
            COMMAND ${CMAKE_COMMAND}
                -DLOG_FILE="${CLANG_TIDY_OUTPUT_FILE}"
                -DSUPPRESSIONS_FILE="${CLANG_TIDY_SUPPRESSIONS_FILE}"
                -DWERROR="${CLANG_TIDY_WERROR}"
                -DWERROR_UNUSED_SUPPRESSIONS="${CLANG_TIDY_WERROR_UNUSED_SUPPRESSIONS}"
                -P ${CMAKE_MODULE_PATH}/clang_tidy_check.cmake
            COMMAND "${CMAKE_COMMAND}" -E touch "${CLANG_TIDY_CHECK_FILE}"
            DEPENDS "${CLANG_TIDY_FILE_STAMPS}" "${CLANG_TIDY_SUPPRESSIONS_FILE}"
            COMMENT "Checking clang-tidy warnigns in ${CLANG_TIDY_TARGET}"
        )

        add_custom_target(${CLANG_TIDY_TARGET} ALL DEPENDS ${CLANG_TIDY_CHECK_FILE})
    endif ()
endfunction()
