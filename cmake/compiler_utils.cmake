# A function to set pthread library correctly.
function(compiler_set_pthread)
    if (UNIX AND "${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU")
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread" PARENT_SCOPE)
    endif ()
endfunction()

# Prepares warnings setup for current target
function(compiler_get_warnings_setup VARNAME)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU")
        set(WARNINGS_SETUP "-Wall -Wextra -pedantic -Wno-long-long")
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        set(WARNINGS_SETUP_LIST "-Weverything"
                "-Wno-system-headers"
                "-Wno-c++98-compat"
                "-Wno-c++98-compat-pedantic"
                "-Wno-exit-time-destructors"
                "-Wno-weak-vtables"
                "-Wno-padded"
                "-Wno-global-constructors"
                "-Wno-covered-switch-default"
                "-Wno-missing-noreturn"
                "-Wno-switch-enum"
                "-Wno-documentation-unknown-command"
                "-Wno-documentation-html"
                "-Wno-documentation"
        )
        string(REPLACE ";" " " WARNINGS_SETUP "${WARNINGS_SETUP_LIST}")
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "MSVC")
        set(WARNINGS_SETUP "/W3 /wd4800")
    endif ()
    set(${VARNAME} "${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

# Prepares warnings setup for current target
function(compiler_get_test_warnings_setup VARNAME)
    compiler_get_warnings_setup(WARNINGS_SETUP)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        set(WARNINGS_SETUP_LIST
                "-Wno-float-equal"
                "-Wno-unused-private-field"
                "-Wno-reserved-id-macro"
        )
        string(REPLACE ";" " " WARNINGS_SETUP "${WARNINGS_SETUP} ${WARNINGS_SETUP_LIST}")
    endif ()
    set(${VARNAME} "${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

function(compiler_get_warnings_as_errors_setup VARNAME)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU")
        if (DEFINED ZSERIO_ENABLE_WERROR)
            set(WARNINGS_SETUP "-Werror")
        endif ()
        if (CMAKE_CXX_COMPILER_VERSION VERSION_EQUAL "7.5.0")
            set(WARNINGS_SETUP
                "${WARNINGS_SETUP} -Wno-unused-function") # declared ‘static’ but never defined
        endif ()
        if (CMAKE_CXX_COMPILER_VERSION VERSION_EQUAL "11.3.0" OR
                CMAKE_CXX_COMPILER_VERSION VERSION_EQUAL "11.2.0")
            set(WARNINGS_SETUP
                "${WARNINGS_SETUP} -Wno-array-bounds") # array subscript 6 is outside array bounds
        endif ()
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        if (DEFINED ZSERIO_ENABLE_WERROR)
            set(WARNINGS_SETUP "-Werror")
        endif ()
        if (CMAKE_CXX_COMPILER_VERSION VERSION_EQUAL "8.0.0")
            set(WARNINGS_SETUP_LIST
                "-Wno-error=range-loop-analysis" # && to bool array element which is not reference
                "-Wno-error=deprecated" # definition of implicit copy constructor is deprecated
            )
            string(REPLACE ";" " " WARNINGS_SETUP "${WARNINGS_SETUP} ${WARNINGS_SETUP_LIST}")
        endif ()
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "MSVC")
        if (DEFINED ZSERIO_ENABLE_WERROR)
            set(WARNINGS_SETUP "/WX")
        endif ()
        if (MSVC_VERSION STREQUAL "1900")
            set(WARNINGS_SETUP "${WARNINGS_SETUP} /wd4334")
        endif ()
    endif ()
    set(${VARNAME} "${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

# A function to enable treating warnings as errors.
function(compiler_set_warnings_as_errors)
    compiler_get_warnings_as_errors_setup(WARNINGS_SETUP)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

# A function to disable treating warnings as errors.
function(compiler_reset_warnings_as_errors)
    compiler_get_warnings_as_errors_setup(WARNINGS_SETUP)
    string(REPLACE " ${WARNINGS_SETUP}" "" NEW_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
    set(CMAKE_CXX_FLAGS "${NEW_CMAKE_CXX_FLAGS}" PARENT_SCOPE)
endfunction()

# A function to set warnings levels.
function(compiler_set_warnings)
    compiler_get_warnings_setup(WARNINGS_SETUP)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

# A function to reset warnings levels.
function(compiler_reset_warnings)
    compiler_get_warnings_setup(WARNINGS_SETUP)
    string(REPLACE " ${WARNINGS_SETUP}" "" NEW_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
    set(CMAKE_CXX_FLAGS "${NEW_CMAKE_CXX_FLAGS}" PARENT_SCOPE)
endfunction()

# A function to set warnings levels for language tests.
function(compiler_set_test_warnings)
    compiler_get_test_warnings_setup(WARNINGS_SETUP)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

# A function to reset warnings levels for language tests.
function(compiler_reset_test_warnings)
    compiler_get_test_warnings_setup(WARNINGS_SETUP)
    string(REPLACE " ${WARNINGS_SETUP}" "" NEW_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
    set(CMAKE_CXX_FLAGS "${NEW_CMAKE_CXX_FLAGS}" PARENT_SCOPE)
endfunction()

# A function to set static c libraries.
function(compiler_set_static_clibs)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU")
        set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static-libgcc -static-libstdc++" PARENT_SCOPE)
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static-libgcc -static-libstdc++" PARENT_SCOPE)
    elseif ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "MSVC")
        set(CONFIGS DEBUG MINSIZEREL RELEASE RELWITHDEBINFO)
        foreach (CONFIG ${CONFIGS})
            string(REPLACE "/MD" "/MT" NEW_CMAKE_CXX_FLAGS_${CONFIG} "${CMAKE_CXX_FLAGS_${CONFIG}}")
            set(CMAKE_CXX_FLAGS_${CONFIG} "${NEW_CMAKE_CXX_FLAGS_${CONFIG}}" PARENT_SCOPE)
            string(REPLACE "/MD" "/MT" NEW_CMAKE_C_FLAGS_${CONFIG} "${CMAKE_C_FLAGS_${CONFIG}}")
            set(CMAKE_C_FLAGS_${CONFIG} "${NEW_CMAKE_C_FLAGS_${CONFIG}}" PARENT_SCOPE)
        endforeach ()
    endif ()
endfunction()

# A function to set sanitizers.
function(compiler_set_address_sanitizer)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fsanitize=address -fno-omit-frame-pointer" PARENT_SCOPE)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fsanitize=address -fno-omit-frame-pointer" PARENT_SCOPE)
        set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -fsanitize=address" PARENT_SCOPE)
    elseif ()
        message(STATUS "AddressSanitizer is not supported for ${CMAKE_CXX_COMPILER_ID} compiler!")
    endif ()
endfunction()

# A function to get setup options for UndefinedBehaviourSanitizer.
function(compiler_get_undefined_sanitizer_setup VARNAME)
    set(SANITIZE_OPTIONS_LIST
        "undefined"
        "float-divide-by-zero"
        "nullability"
    )
    string(REPLACE ";" "," SANITIZE_OPTIONS "${SANITIZE_OPTIONS_LIST}")
    set(${VARNAME} "-fsanitize=${SANITIZE_OPTIONS} -fno-sanitize-recover=${SANITIZE_OPTIONS}" PARENT_SCOPE)
endfunction()

# A function to enable UndefinedBehaviourSanitizer.
function(compiler_set_undefined_sanitizer)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        compiler_get_undefined_sanitizer_setup(SANITIZER_SETUP)
        set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${SANITIZER_SETUP}" PARENT_SCOPE)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${SANITIZER_SETUP}" PARENT_SCOPE)
        set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ${SANITIZER_SETUP}" PARENT_SCOPE)
    else ()
        message(STATUS "UndefinedBehaviourSanitizer is not supported for ${CMAKE_CXX_COMPILER_ID} compiler!")
    endif ()
endfunction()

# A function to disable UndefinedBehaviourSanitizer.
function(compiler_reset_undefined_sanitizer)
    if ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang")
        compiler_get_undefined_sanitizer_setup(SANITIZER_SETUP)
        string(REPLACE " ${SANITIZER_SETUP}" "" NEW_CMAKE_C_FLAGS "${CMAKE_C_FLAGS}")
        set(CMAKE_C_FLAGS "${NEW_CMAKE_C_FLAGS}" PARENT_SCOPE)
        string(REPLACE " ${SANITIZER_SETUP}" "" NEW_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
        set(CMAKE_CXX_FLAGS "${NEW_CMAKE_CXX_FLAGS}" PARENT_SCOPE)
        string(REPLACE " ${SANITIZER_SETUP}" "" NEW_CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS}")
        set(CMAKE_EXE_LINKER_FLAGS "${NEW_CMAKE_EXE_LINKER_FLAGS}" PARENT_SCOPE)
    endif ()
endfunction()
