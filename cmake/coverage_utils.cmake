# Enable code coverage calculation for the given target by adding necessary parameters to the compiler flags.
# Note: It is suggested not to use this for optimized builds, the coverage reports may be inaccurate.
function(enable_coverage_for_target TARGET)
    if("${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU")
        set_target_properties(${TARGET} PROPERTIES COMPILE_FLAGS "--coverage")
        set_target_properties(${TARGET} PROPERTIES LINK_FLAGS "--coverage")
    else()
        message(FATAL_ERROR "Coverage reports are not supported for target compiler.")
    endif()
endfunction()

# For current project, adds a target for generating coverage reports.
# Usage: create_coverage_target
#    INCOMPLETE_COVERAGE_FAIL - Makes the build fail if line coverage is not 100%.
#    MAKE_HTML_REPORT - Generate detailed HTML report instead of default text report.
#    EXCLUDE "pattern" - Exclude source files matching given pattern (regex) from the coverage report.
#            There may be multiple exclude patterns.
function(create_coverage_target)
    include(CMakeParseArguments)
    cmake_parse_arguments(cov "INCOMPLETE_COVERAGE_FAIL;MAKE_HTML_REPORT" "TARGET" "EXCLUDE" ${ARGN})

    if (cov_TARGET)
        set(cov_tgt_name "${cov_TARGET}")
    else ()
        set(cov_tgt_name "coverage")
    endif ()

    set(cov_excludes)
    foreach (exc ${cov_EXCLUDE})
        list(APPEND cov_excludes "-e")
        list(APPEND cov_excludes "${exc}")
    endforeach ()

    set(cov_fail)
    if (cov_INCOMPLETE_COVERAGE_FAIL)
        set(cov_fail "--fail-under-line" "100")
    endif ()

    set(cov_message "Generating code coverage report.")

    set(cov_html)
    if (cov_MAKE_HTML_REPORT)
        set(cov_html "--html" "--html-details" "-o" "${cov_tgt_name}/index.html")
        set(cov_message "Generating html code coverage report in ${cov_tgt_name}/index.html")
    endif ()

    add_custom_target(
        ${cov_tgt_name}
        COMMAND ${CMAKE_COMMAND} -E make_directory ${PROJECT_BINARY_DIR}/${cov_tgt_name}
        COMMAND ${GCOVR_BIN}
            -s
            ${cov_html}
            -r ${PROJECT_SOURCE_DIR}
            --object-directory=${PROJECT_BINARY_DIR}
            ${cov_excludes}
            ${cov_fail}
        WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
        VERBATIM
        COMMENT ${cov_message})
endfunction()
