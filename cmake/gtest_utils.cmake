include(compiler_utils)

# A function to create gtest static library.
function(gtest_add_library GTEST_ROOT)
    if (WIN32)
        # CMake with mingw detects that pthreads are available but this triggers schizophrenia in gtest
        # where in gtest-port.h AutoHandle is not defined (because GTEST_HAS_PTHREAD is set) but then it's
        # required later (because GTEST_OS_WINDOWS).
        # GTEST_HAS_PTHREAD is automatically set by find_package(Threads) but this can be disabled by
        # gtest_disable_pthreads.
        set(gtest_disable_pthreads ON CACHE BOOL "disable pthreads in gtest")
    endif ()

    # remove strict warnings
    compiler_reset_warnings()
    compiler_reset_warnings_as_errors()

    # include google test framework
    option(BUILD_GMOCK "" OFF)
    option(INSTALL_GTEST "" OFF)
    add_subdirectory(${GTEST_ROOT} googletest)

    # override googletest hardcoded output bin and lib settings
    set_target_properties(gtest
            PROPERTIES
            RUNTIME_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/bin"
            LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/lib"
            ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/lib"
            PDB_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/bin")
    set_target_properties(gtest_main
            PROPERTIES
            RUNTIME_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/bin"
            LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/lib"
            ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/lib"
            PDB_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/googletest/bin")
endfunction()

# A function to add new test to gtest. It is copied from FindGTest.cmake module because include(FindGTest)
# tries to find gtest binaries automatically and we compile gtest from sources.
function(gtest_add_tests executable extra_args)
    if (NOT ARGN)
        message(FATAL_ERROR "Missing ARGN: Read the documentation for GTEST_ADD_TESTS!")
    endif ()
    if (ARGN STREQUAL "AUTO")
        # obtain sources used for building that executable
        get_property(ARGN TARGET ${executable} PROPERTY SOURCES)
    endif ()
    set(gtest_case_name_regex ".*\\( *([A-Za-z_0-9]+) *, *([A-Za-z_0-9]+) *\\).*")
    set(gtest_test_type_regex "(TYPED_TEST|TEST_?[FP]?)")
    foreach (source ${ARGN})
        file(READ "${source}" contents)
        string(REGEX MATCHALL "${gtest_test_type_regex} *\\(([A-Za-z_0-9 ,]+)\\)" found_tests ${contents})
        foreach (hit ${found_tests})
            string(REGEX MATCH "${gtest_test_type_regex}" test_type ${hit})

            # Parameterized tests have a different signature for the filter
            if ("x${test_type}" STREQUAL "xTEST_P")
                string(REGEX REPLACE ${gtest_case_name_regex}  "*/\\1.\\2/*" test_name ${hit})
            elseif ("x${test_type}" STREQUAL "xTEST_F" OR "x${test_type}" STREQUAL "xTEST")
                string(REGEX REPLACE ${gtest_case_name_regex} "\\1.\\2" test_name ${hit})
            elseif ("x${test_type}" STREQUAL "xTYPED_TEST")
                string(REGEX REPLACE ${gtest_case_name_regex} "\\1/*.\\2" test_name ${hit})
            else ()
                message(WARNING "Could not parse GTest ${hit} for adding to CTest.")
                continue()
            endif ()

            add_test(NAME ${test_name} COMMAND ${executable} --gtest_filter=${test_name} ${extra_args})

            # Add labels automatically - relative path from current source dir to the source file
            string(REPLACE "${CMAKE_CURRENT_SOURCE_DIR}/" "" label "${source}")
            set_tests_properties(${test_name} PROPERTIES LABELS "${label}")

            if ("x${test_type}" STREQUAL "xTEST_F")
                # add resource lock for test cases within a single test source file when the test uses a fixture
                set_tests_properties(${test_name} PROPERTIES RESOURCE_LOCK ${label})
            endif ()
        endforeach ()
    endforeach ()
endfunction()
