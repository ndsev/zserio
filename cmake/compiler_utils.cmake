# A function to set pthread library correctly.
function(compiler_set_pthread)
    if (UNIX AND CMAKE_COMPILER_IS_GNUCXX)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread" PARENT_SCOPE)
    endif ()
endfunction()

# Prepares warnings setup for current target
function(compiler_get_warnings_setup VARNAME)
    if (CMAKE_COMPILER_IS_GNUCXX)
        set(WARNINGS_SETUP "-Wall -Wextra -pedantic -Wno-long-long")
    elseif (MSVC)
        set(WARNINGS_SETUP "/W3 /wd4800")
    endif ()
    set(${VARNAME} "${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

function(compiler_get_warnings_as_errors_setup VARNAME)
    if (CMAKE_COMPILER_IS_GNUCXX)
        set(TESTED_VERSIONS "4.4.3" "4.5.4" "5.4.0" "7.4.0")
        list(FIND TESTED_VERSIONS "${CMAKE_CXX_COMPILER_VERSION}" TESTED_VERSION_INDEX)
        if (NOT (TESTED_VERSION_INDEX EQUAL -1))
            set(WARNINGS_SETUP "-Werror")
            if (CMAKE_CXX_COMPILER_VERSION VERSION_EQUAL "4.4.3")
                set(WARNINGS_SETUP "${WARNINGS_SETUP} -Wno-error=strict-aliasing")
            endif ()
        endif ()
    elseif (MSVC)
        set(WARNINGS_SETUP "/WX")
    endif ()
    set(${VARNAME} "${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

# A function to enable treating warnings as errors.
function(compiler_set_warnings_as_errors)
    compiler_get_warnings_as_errors_setup(WARNINGS_SETUP)
    set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${WARNINGS_SETUP}" PARENT_SCOPE)
endfunction()

# A function to disable treaging warnings as errors.
function(compiler_reset_warnings_as_errors)
    compiler_get_warnings_as_errors_setup(WARNINGS_SETUP)
    string(REGEX REPLACE " ${WARNINGS_SETUP}" "" NEW_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
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
    string(REGEX REPLACE " ${WARNINGS_SETUP}" "" NEW_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
    set(CMAKE_CXX_FLAGS "${NEW_CMAKE_CXX_FLAGS}" PARENT_SCOPE)
endfunction()

# A function to set static c libraries.
function(compiler_set_static_clibs)
    if (CMAKE_COMPILER_IS_GNUCXX)
        set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static-libgcc -static-libstdc++" PARENT_SCOPE)
    elseif (MSVC)
        set(CONFIGS DEBUG MINSIZEREL RELEASE RELWITHDEBINFO)
        foreach (CONFIG ${CONFIGS})
            string(REPLACE "/MD" "/MT" NEW_CMAKE_CXX_FLAGS_${CONFIG} "${CMAKE_CXX_FLAGS_${CONFIG}}")
            set(CMAKE_CXX_FLAGS_${CONFIG} "${NEW_CMAKE_CXX_FLAGS_${CONFIG}}" PARENT_SCOPE)
            string(REPLACE "/MD" "/MT" NEW_CMAKE_C_FLAGS_${CONFIG} "${CMAKE_C_FLAGS_${CONFIG}}")
            set(CMAKE_C_FLAGS_${CONFIG} "${NEW_CMAKE_C_FLAGS_${CONFIG}}" PARENT_SCOPE)
        endforeach ()
    endif ()
endfunction()
