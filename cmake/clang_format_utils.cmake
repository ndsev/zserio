# A function to create clang-format target.
#
# Usage clang_format_add_custom_target
#   DEPENDS       List of dependencies.
#   SOURCES       List of source directories for clang-format.
#   SOURCES_GLOBS List of sources globbing expressions which will be searched using GLOG_RECURSE.
#   CONFIG_FILE   Path to .clang-format config file.
#   WERROR        Ends with an error in case of any format violation. Default is ON.
function(clang_format_add_custom_target CLANG_FORMAT_TARGET)
    if (CLANG_FORMAT_BIN)
        cmake_parse_arguments(CLANG_FORMAT
            ""
            "CONFIG_FILE;WERROR"
            "DEPENDS;SOURCES;SOURCES_GLOBS"
            ${ARGN}
        )

        # check required arguments
        foreach (ARG TARGET CONFIG_FILE)
            if (NOT DEFINED CLANG_FORMAT_${ARG})
                message(FATAL_ERROR "No value defined for required argument ${ARG}!")
            endif ()
        endforeach ()

        if (NOT DEFINED CLANG_FORMAT_SOURCES AND NOT DEFINED CLANG_FORMAT_SOURCES_GLOBS)
            message(FATAL_ERROR "No value defined neither for SOURCES nor SOURCES_GLOBS!")
        endif ()

        # process optional arguments
        if (NOT DEFINED CLANG_FORMAT_WERROR)
            set(CLANG_FORMAT_WERROR ON)
        endif ()

        # process sources
        list(APPEND CLANG_FORMAT_SOURCES_LIST ${CLANG_FORMAT_SOURCES})
        foreach (SOURCE_EXPRESSION ${CLANG_FORMAT_SOURCES_GLOBS})
            file(GLOB_RECURSE MATCHING_SOURCES "${SOURCE_EXPRESSION}")
            list(APPEND CLANG_FORMAT_SOURCES_LIST ${MATCHING_SOURCES})
        endforeach ()
        list(REMOVE_DUPLICATES CLANG_FORMAT_SOURCES_LIST)

        # run clang format for each source
        file(MAKE_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/clang-format")
        set(INDEX 0)
        foreach (SOURCE_FILE ${CLANG_FORMAT_SOURCES_LIST})
            get_filename_component(SOURCE_FILE_NAME ${SOURCE_FILE} NAME)
            set(CLANG_FORMAT_FILE_STAMP "clang-format/${INDEX}_${SOURCE_FILE_NAME}")
            add_custom_command(
                OUTPUT "${CLANG_FORMAT_FILE_STAMP}"
                COMMAND "${CMAKE_COMMAND}"
                    -DCLANG_FORMAT_BIN="${CLANG_FORMAT_BIN}"
                    -DSOURCE="${SOURCE_FILE}"
                    -DCONFIG_FILE="${CLANG_FORMAT_CONFIG_FILE}"
                    -DWERROR="${CLANG_FORMAT_WERROR}"
                    -P ${CMAKE_MODULE_PATH}/clang_format_tool.cmake
                COMMAND "${CMAKE_COMMAND}" -E touch "${CLANG_FORMAT_FILE_STAMP}"
                DEPENDS ${SOURCE_FILE} ${CLANG_FORMAT_CONFIG_FILE}
                COMMENT "Running clang-format on ${SOURCE_FILE}")
            list(APPEND CLANG_FORMAT_FILE_STAMPS "${CLANG_FORMAT_FILE_STAMP}")
            math(EXPR INDEX "${INDEX} + 1")
        endforeach ()
        add_custom_target(${CLANG_FORMAT_TARGET} ALL
                DEPENDS ${CLANG_FORMAT_DEPENDS} ${CLANG_FORMAT_FILE_STAMPS})
    endif ()
endfunction()
