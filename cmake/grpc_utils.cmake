# A function to find GRPC libraries
#
# The following variables in the parent scope are set:
#
# GRPC_ENABLED - Whether the GRPC is enabled at all (GRPC_ROOT is a valid directory)
# GRPC_LIBDIR - Directory where the GRPC libraries are located
# GRPC_INCDIR - Directory where the GRPC includes are located
# GRPC_LIBRARIES - GRPC libraries to link with
function(find_grpc_libraries)
    set(GRPC_ROOT "" CACHE PATH "GRPC root directory")

    if ("${GRPC_ROOT}" STREQUAL "")
        set(GRPC_ENABLED OFF)
        message(STATUS "GRPC is disabled")
    else()
        set(GRPC_ENABLED ON)
        if (NOT IS_DIRECTORY "${GRPC_ROOT}")
            message(FATAL_ERROR
                "GRPC_ROOT '${GRPC_ROOT}' is not a valid directory! Please set GRPC_ROOT correctly.")
        endif ()
        message(STATUS "GRPC is enabled, GRPC_ROOT=${GRPC_ROOT}")
    endif()

    if (GRPC_ENABLED)
        set(GRPC_LIBDIR ${GRPC_ROOT}/libs/opt CACHE PATH "GRCP libraries directory")
        set(GRPC_INCDIR ${GRPC_ROOT}/include CACHE PATH "GRPC include directory")

        set(GRPC_LIB_NAMES "grpc++_unsecure" "grpc_unsecure" "gpr")
        foreach(lib_name ${GRPC_LIB_NAMES})
            find_library("${lib_name}" ${lib_name} PATHS ${GRPC_LIBDIR} NO_DEFAULT_PATH)
            if (${${lib_name}} STREQUAL "${lib_name}-NOTFOUND")
                message(FATAL_ERROR "GRPC: Missing ${lib_name} library!")
            endif ()
            set(libs "${libs};${${lib_name}}")
            unset(${lib_name} CACHE) # do not mess cache
        endforeach()
        set(GRPC_LIBRARIES ${libs} CACHE STRING "GRPC libraries to link with")
    endif ()

    set(GRPC_ENABLED ${GRPC_ENABLED} PARENT_SCOPE)
endfunction()
