# A function to find GRPC libraries
#
# The following variables are set in cache:
#
# GRPC_ENABLED - Whether the GRPC is enabled at all (GRPC_ROOT is a valid directory)
# GRPC_LIBDIR - Directory where the GRPC libraries are located
# GRPC_INCDIR - Directory where the GRPC includes are located
# GRPC_LIBRARIES - GRPC libraries to link with
function(find_grpc_libraries)
    set(GRPC_ROOT "/usr" CACHE PATH "GRPC root directory")
    set(GRPC_ENABLED OFF CACHE BOOL "Enable GRPC tests")

    if (GRPC_ENABLED)
        set(GRPC_LIBDIR ${GRPC_ROOT}/lib CACHE PATH "GRCP libraries directory")
        set(GRPC_INCDIR ${GRPC_ROOT}/include CACHE PATH "GRPC include directory")

        set(GRPC_LIB_NAMES "grpc++_unsecure" "grpc_unsecure" "gpr")
        if (MSVC)
            list(APPEND GRPC_LIB_NAMES "zlibstatic") # precompiled in grpc installation
            set(LIBS "ws2_32.lib") # dependency on system library
        endif ()
        foreach(lib_name ${GRPC_LIB_NAMES})
            find_library("${lib_name}" ${lib_name} PATHS ${GRPC_LIBDIR} NO_DEFAULT_PATH)
            if (${${lib_name}} STREQUAL "${lib_name}-NOTFOUND")
                message(FATAL_ERROR "GRPC: Missing ${lib_name} library!")
            endif ()
            set(LIBS "${LIBS};${${lib_name}}")
            unset(${lib_name} CACHE) # do not mess cache
        endforeach()
        set(GRPC_LIBRARIES ${LIBS} CACHE STRING "GRPC libraries to link with")
    endif ()
endfunction()
