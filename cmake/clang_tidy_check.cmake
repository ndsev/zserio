# Script called from clang_tidy_utils.cmake to run checks for unexpected warnings.
#
# Expected definitions:
#   LOG_FILE          File containing clang-tidy log.
#   SUPPRESSIONS_FILE Suppressions file for clang-tidy warnings. If any warnings are fired but not suppressed,
#                     the clang-tidy target will fail.
#                     Syntax:
#                       rule-definition                    # suppress rule everywhere
#                       rule-definition:path-regex         # suppress rule on matching paths
#                       rule-definition:path-regex:10      # suppress rule on matching line
#                       rule-definition:path-regex:10:1    # suppress rule on matching location
#   WERROR            Ends with an error in case of any unsuppressed clang-tidy warnings.
#   WERROR_UNUSED_SUPPRESSIONS
#                     Fires an error in case of unused suppressions.
cmake_minimum_required(VERSION 3.6.0)

# splits suppression to RULE and PATH_MATCHER parts
function(get_matchers SUPPRESSION RULE_VAR PATH_MATCHER_VAR)
    set(RULE)
    set(PATH_MATCHER)

    string(REPLACE ":" ";" SUPPRESSION_PARTS "${SUPPRESSION}")
    list(LENGTH SUPPRESSION_PARTS NUM_PARTS)
    list(GET SUPPRESSION_PARTS 0 RULE)
    set("${RULE_VAR}" "${RULE}" PARENT_SCOPE)

    if (${NUM_PARTS} GREATER 1)
        list(GET SUPPRESSION_PARTS 1 PATH_MATCHER)
    endif ()
    if (${NUM_PARTS} GREATER 2)
        list(GET SUPPRESSION_PARTS 2 LINE)
        set(PATH_MATCHER "${PATH_MATCHER}:${LINE}")
    endif ()
    if (${NUM_PARTS} GREATER 3)
        list(GET SUPPRESSION_PARTS 3 COLUMN)
        set(PATH_MATCHER "${PATH_MATCHER}:${COLUMN}")
    endif ()
    set("${PATH_MATCHER_VAR}" "${PATH_MATCHER}" PARENT_SCOPE)
endfunction()

# strips commensts and both leading and trailing whitespaces
function(strip_suppressions SUPPRESSIONS_LINES SUPPRESSIONS_LINES_VAR)
    foreach(SUPPRESSION ${SUPPRESSIONS_LINES})
        string(REGEX REPLACE "#.*$" "" SUPPRESSION_STRIPPED "${SUPPRESSION}")
        string(STRIP "${SUPPRESSION_STRIPPED}" SUPPRESSION_STRIPPED)
        if (NOT "${SUPPRESSION_STRIPPED}" STREQUAL "")
            list(APPEND SUPPRESSION_LINES_STRIPPED "${SUPPRESSION_STRIPPED}")
        endif ()
    endforeach()
    set("${SUPPRESSIONS_LINES_VAR}" "${SUPPRESSION_LINES_STRIPPED}" PARENT_SCOPE)
endfunction()

foreach (ARG LOG_FILE SUPPRESSIONS_FILE)
    if (NOT DEFINED ${ARG})
        message(FATAL_ERROR "Argument '${ARG}' not defined!")
    endif ()
endforeach ()

# cannot use file(STRING) since the log contains semicolons
file(READ "${LOG_FILE}" LOG_CONTENT)
string(REPLACE ";" ":" LOG_CONTENT "${LOG_CONTENT}")
string(REGEX REPLACE "\r?\n" ";" LOG_LINES "${LOG_CONTENT}")

file(STRINGS "${SUPPRESSIONS_FILE}" SUPPRESSIONS_LINES)
strip_suppressions("${SUPPRESSIONS_LINES}" SUPPRESSIONS_LINES)

list(LENGTH SUPPRESSIONS_LINES NUM_SUPPRESSIONS)
set(UNUSED_SUPPRESSIONS "${SUPPRESSIONS_LINES}")
set(NUM_UNSUPPRESSED_WARNINGS 0)
set(IS_AFTER_MATCH FALSE)
foreach (LINE ${LOG_LINES})
    if (NOT "${LINE}" MATCHES ".*warning:.*\\[.*\\]\$")
        if (NOT ${IS_AFTER_MATCH})
            list(APPEND UNSUPPRESSED_LINES ${LINE})
        endif ()
    else ()
        set(IS_AFTER_MATCH FALSE)

        set(SUPPRESSION_INDEX 0)
        while (${SUPPRESSION_INDEX} LESS ${NUM_SUPPRESSIONS})
            list(GET SUPPRESSIONS_LINES ${SUPPRESSION_INDEX} SUPPRESSION)
            get_matchers(${SUPPRESSION} RULE PATH_MATCHER)
            if ("${LINE}" MATCHES "${PATH_MATCHER}:.*\\[${RULE}\\]")
                set(IS_AFTER_MATCH TRUE)
                list(APPEND USED_SUPPRESSIONS_INDEXES ${SUPPRESSION_INDEX})
            endif ()

            math(EXPR SUPPRESSION_INDEX "${SUPPRESSION_INDEX} + 1")
            if (${IS_AFTER_MATCH})
                break()
            endif ()
        endwhile ()

        if (NOT ${IS_AFTER_MATCH})
            list(APPEND UNSUPPRESSED_LINES ${LINE})
            math(EXPR NUM_UNSUPPRESSED_WARNINGS "${NUM_UNSUPPRESSED_WARNINGS} + 1")
        endif ()
    endif ()
endforeach ()

# ensure that all suppressions are used
list(SORT USED_SUPPRESSIONS_INDEXES)
list(REMOVE_DUPLICATES USED_SUPPRESSIONS_INDEXES)
list(LENGTH USED_SUPPRESSIONS_INDEXES NUM_USED_SUPPRESSIONS)
if (NUM_USED_SUPPRESSIONS GREATER 0)
    list(REMOVE_AT SUPPRESSIONS_LINES ${USED_SUPPRESSIONS_INDEXES})
endif ()
if (SUPPRESSIONS_LINES)
    list(JOIN SUPPRESSIONS_LINES "\n" UNUSED_SUPPRESSIONS)
    message(STATUS "Unused suppressions:\n${UNUSED_SUPPRESSIONS}")
    if (WERROR_UNUSED_SUPPRESSIONS)
        message(FATAL_ERROR "Unused suppressions detected in ${SUPPRESSIONS_FILE}!")
    endif ()
endif ()

# report unsuppressed warnings
if (UNSUPPRESSED_LINES)
    list(JOIN UNSUPPRESSED_LINES "\n" UNSUPPRESSED_LOG)
    message(STATUS "Clang Tidy output:\n${UNSUPPRESSED_LOG}")
    if (WERROR) # emulate WarningsAsErrors
        message(FATAL_ERROR
            "Clang Tidy produced ${NUM_UNSUPPRESSED_WARNINGS} warnings which are not suppressed!")
    endif ()
endif ()
