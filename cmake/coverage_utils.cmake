# Enable code coverage calculation for the given target by adding necessary parameters to the compiler flags.
# Note: It is suggested not to use this for optimized builds, the coverage reports may be inaccurate.
function(enable_coverage_for_target TARGET)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU")
        set_target_properties(${TARGET} PROPERTIES COMPILE_FLAGS "--coverage")
        set_target_properties(${TARGET} PROPERTIES LINK_FLAGS "--coverage")
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        set_target_properties(${TARGET} PROPERTIES COMPILE_FLAGS "-fprofile-instr-generate -fcoverage-mapping")
        set_target_properties(${TARGET} PROPERTIES LINK_FLAGS "-fprofile-instr-generate -fcoverage-mapping")
    else ()
        message(FATAL_ERROR "Coverage reports are not supported for target compiler ${CMAKE_CXX_COMPILER_ID}!")
    endif ()
endfunction()

# For current project, adds a target for generating coverage reports.
# Usage: create_coverage_target
#    INCOMPLETE_COVERAGE_FAIL "percent" - Makes the build fail if line coverage is less than percent.
#    TARGET "name" - Specifies the target name. Default is "coverage".
#    EXCLUDE_SOURCES "regex" - Skip source files with file paths that match the given regular expression.
function(create_coverage_target)
    cmake_parse_arguments(cov "" "INCOMPLETE_COVERAGE_FAIL;TARGET;EXCLUDE_SOURCES" "" ${ARGN})

    if (cov_TARGET)
        set(cov_tgt_name "${cov_TARGET}")
    else ()
        set(cov_tgt_name "coverage")
    endif ()

    set(cov_html_dir "${PROJECT_BINARY_DIR}/${cov_tgt_name}")

    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU")
        set(cov_fail)
        if (cov_INCOMPLETE_COVERAGE_FAIL)
            list(APPEND cov_fail "--fail-under-line")
            list(APPEND cov_fail "${cov_INCOMPLETE_COVERAGE_FAIL}")
        endif ()
        set(cov_exclude)
        if (cov_EXCLUDE_SOURCES)
            list(APPEND cov_exclude "--exclude=${cov_EXCLUDE_SOURCES}")
        endif ()

        set(cov_html_gcc_dir "${cov_html_dir}/gcc")
        add_custom_target(
            ${cov_tgt_name}
            COMMAND ${CMAKE_COMMAND} -E make_directory ${cov_html_gcc_dir}
            COMMAND ${GCOVR_BIN}
                -s
                -r ${PROJECT_SOURCE_DIR}
                --object-directory=${PROJECT_BINARY_DIR}
                ${cov_exclude}
            COMMAND ${GCOVR_BIN}
                -s
                --html --html-details -o "${cov_html_gcc_dir}/index.html"
                -r ${PROJECT_SOURCE_DIR}
                --object-directory=${PROJECT_BINARY_DIR}
                ${cov_fail}
                ${cov_exclude} > ${cov_html_gcc_dir}/coverage_report.txt
            WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
            VERBATIM
            COMMENT "Generating html code coverage report in ${cov_html_gcc_dir}/index.html")
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        set(cov_exclude)
        if (cov_EXCLUDE_SOURCES)
            list(APPEND cov_exclude "--ignore-filename-regex=${cov_EXCLUDE_SOURCES}")
        endif ()
        set(cov_test_exectable "${ZserioCppRuntimeTest_BINARY_DIR}/ZserioCppRuntimeTest")
        set(cov_binary_dir "${PROJECT_BINARY_DIR}/${cov_tgt_name}")
        set(cov_html_clang_dir "${cov_html_dir}/clang")
        add_custom_target(
            ${cov_tgt_name}
            COMMAND ${CMAKE_COMMAND} -E make_directory ${cov_binary_dir}
            # run tests again because ctest runs tests separately (default.profraw contains only the last test)
            COMMAND bash -c "cd ${ZserioCppRuntimeTest_BINARY_DIR} && ${cov_test_exectable} > /dev/null"
            COMMAND bash -c "rm -rf ${cov_html_clang_dir}"
            COMMAND ${LLVM_PROFDATA_BIN} merge
                --sparse ${ZserioCppRuntimeTest_BINARY_DIR}/default.profraw
                -o ${cov_binary_dir}/runtime.profdata
            COMMAND ${LLVM_COV_BIN} show ${cov_test_exectable}
                -show-expansions -show-line-counts -show-regions -use-color
                -instr-profile=${cov_binary_dir}/runtime.profdata
                -format=html -show-instantiations=false -output-dir=${cov_html_clang_dir}
                ${cov_exclude}
            # llvm-cov uses full sources paths within the html dir - make them relative
            COMMAND bash -c "\
                    mv ${cov_html_clang_dir}/coverage${PROJECT_SOURCE_DIR}/* \
                            ${cov_html_clang_dir}/coverage && \
                    rm -r ${cov_html_clang_dir}/coverage/$(echo ${PROJECT_SOURCE_DIR} | cut -d/ -f2) && \
                    SRC_DIR=${PROJECT_SOURCE_DIR}; SRC_SLASHES=\${SRC_DIR//[!\\/]}; SRC_DEPTH=\${#SRC_SLASHES} && \
                    CSS_PREFIX=\"\"; for ((i=0; i<\${SRC_DEPTH}; i++)); do CSS_PREFIX+=\"../\"; done && \
                    sed -i 's|coverage${PROJECT_SOURCE_DIR}|coverage|g' ${cov_html_clang_dir}/index.html && \
                    find ${cov_html_clang_dir}/coverage -type f -exec \
                            sed -i \"s|href='\${CSS_PREFIX}|href='|\" {} + && \
                    find ${cov_html_clang_dir}/coverage -type f -exec \
                            sed -i 's|<pre>${PROJECT_SOURCE_DIR}/|<pre>|' {} +"
            COMMAND ${LLVM_COV_BIN} report ${cov_test_exectable}
                -instr-profile=${cov_binary_dir}/runtime.profdata
                ${cov_exclude}
            COMMAND bash -c "${LLVM_COV_BIN} report ${cov_test_exectable} \
                    -instr-profile=${cov_binary_dir}/runtime.profdata ${cov_exclude} \
                    > ${cov_html_clang_dir}/coverage_report.txt"
            COMMAND bash -c "(( $(echo \" \
                    `${LLVM_COV_BIN} report ${cov_test_exectable} \
                    -instr-profile=${cov_binary_dir}/runtime.profdata ${cov_exclude} | grep TOTAL | \
                    tr -s ' ' | cut -d' ' -f 10 | cut -d% -f 1` ${cov_INCOMPLETE_COVERAGE_FAIL}\" | \
                    LC_NUMERIC=C awk '{if ($1 >= $2) print 1;}') \
                    ))"
            WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
            VERBATIM
            COMMENT "Generating html code coverage report in ${cov_html_clang_dir}/index.html")
    else ()
        message(FATAL_ERROR "Coverage reports are not supported for target compiler ${CMAKE_CXX_COMPILER_ID}!")
    endif ()
endfunction()
