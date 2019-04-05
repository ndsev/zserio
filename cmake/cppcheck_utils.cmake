# A function to add custom command for cppcheck.
#
# Usage: cppcheck_add_custom_command
#    TARGET           Target for which cppcheck custom command should be added.
#    SOURCE_DIR       Directory with sources for cppcheck.
#    INCLUDE_DIR      Directory where to find headers for cppcheck.
#    SUPPRESSION_FILE Override default suppression file (optional),
#                     default is ${CMAKE_CURRENT_SOURCE_DIR}/CppcheckSuppressions.txt.
function(cppcheck_add_custom_command)
    # parse cmdline args
    foreach (ARG ${ARGV})
        if ((ARG STREQUAL TARGET) OR (ARG STREQUAL "SOURCE_DIR") OR (ARG STREQUAL "INCLUDE_DIR") OR (ARG STREQUAL "SUPPRESSION_FILE"))
            if (DEFINED CPPCHECK_VALUE_${ARG})
                message(FATAL_ERROR "Option ${ARG} used multiple times!")
            endif ()
            set(CPPCHECK_ARG_NAME ${ARG})
        else ()
            set(CPPCHECK_VALUE_${CPPCHECK_ARG_NAME} ${CPPCHECK_VALUE_${CPPCHECK_ARG_NAME}} ${ARG})
        endif ()
    endforeach ()

    foreach (ARG TARGET SOURCE_DIR)
        if (NOT DEFINED CPPCHECK_VALUE_${ARG})
            message(FATAL_ERROR "No value defined for required argument ${ARG}!")
        endif ()
    endforeach ()

    if (NOT DEFINED CPPCHECK_VALUE_SUPPRESSION_FILE)
        set(CPPCHECK_VALUE_SUPPRESSION_FILE "${CMAKE_CURRENT_SOURCE_DIR}/CppcheckSuppressions.txt")
    endif ()

    # call cppcheck tool
    if (NOT "${CPPCHECK_HOME}" STREQUAL "")
        set(CPPCHECK_TARGET ${CPPCHECK_VALUE_TARGET}-cppcheck)
        add_custom_target(${CPPCHECK_TARGET} ALL DEPENDS ${CPPCHECK_VALUE_TARGET})

        if (EXISTS "${CPPCHECK_VALUE_SUPPRESSION_FILE}")
            set(CPPCHECK_SUPPRESSION_OPTION
                --suppressions-list="${CPPCHECK_VALUE_SUPPRESSION_FILE}")
        endif ()
        if (DEFINED CPPCHECK_VALUE_INCLUDE_DIR)
            foreach (CPPCHECK_INCLUDE_DIR ${CPPCHECK_VALUE_INCLUDE_DIR})
                set(CPPCHECK_INCLUDE_OPTIONS
                    ${CPPCHECK_INCLUDE_OPTIONS}
                    -I "${CPPCHECK_INCLUDE_DIR}")
            endforeach ()
        endif ()
        add_custom_command(TARGET ${CPPCHECK_TARGET} POST_BUILD
            COMMAND ${CPPCHECK_HOME}/cppcheck ${CPPCHECK_VALUE_SOURCE_DIR}
                --enable=warning,style,performance,portability --error-exitcode=1 --template='gcc' -q
                 ${CPPCHECK_INCLUDE_OPTIONS} ${CPPCHECK_SUPPRESSION_OPTION}
            COMMENT "Running cppcheck tool for static analysis")
    endif ()
endfunction()
