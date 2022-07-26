# set Windows system
set(CMAKE_SYSTEM_NAME Windows)

# set toolchain (use native MinGW on Windows)
set(TOOLCHAIN_ROOT "$ENV{MINGW64_TOOLCHAIN_ROOT}")
if (UNIX)
    set(TOOLCHAIN_PREFIX_NAME "x86_64-w64-mingw32-")
else ()
    set(TOOLCHAIN_PREFIX_NAME "")
endif ()

# needed for find_xxxx command to search in ${TOOLCHAIN_ROOT)/lib(include) directories
set(CMAKE_SYSTEM_LIBRARY_PATH ${CMAKE_SYSTEM_LIBRARY_PATH} /lib)
set(CMAKE_SYSTEM_INCLUDE_PATH ${CMAKE_SYSTEM_INCLUDE_PATH} /include)

# specify path to toolchain
set(CMAKE_FIND_ROOT_PATH
    ${TOOLCHAIN_ROOT})

# specify the cross compiler
find_program(CMAKE_C_COMPILER "${TOOLCHAIN_PREFIX_NAME}gcc" PATHS "${TOOLCHAIN_ROOT}/bin"
             NO_DEFAULT_PATH NO_CMAKE_FIND_ROOT_PATH)
find_program(CMAKE_CXX_COMPILER "${TOOLCHAIN_PREFIX_NAME}g++" PATHS "${TOOLCHAIN_ROOT}/bin"
             NO_DEFAULT_PATH NO_CMAKE_FIND_ROOT_PATH)
find_program(CMAKE_RC_COMPILER "${TOOLCHAIN_PREFIX_NAME}windres" PATHS "${TOOLCHAIN_ROOT}/bin"
             NO_DEFAULT_PATH ONLY_CMAKE_FIND_ROOT_PATH)

# check that the toolchain is available
if (CMAKE_C_COMPILER STREQUAL "CMAKE_C_COMPILER-NOTFOUND")
    message(FATAL_ERROR "Could not find '${TOOLCHAIN_PREFIX_NAME}gcc' in the specified toolchain: "
                        "${TOOLCHAIN_ROOT}'!")
endif ()
if (CMAKE_CXX_COMPILER STREQUAL "CMAKE_CXX_COMPILER-NOTFOUND")
    message(FATAL_ERROR "Could not find '${TOOLCHAIN_PREFIX_NAME}g++' in the specified toolchain: "
                        "${TOOLCHAIN_ROOT}'!")
endif ()
if (CMAKE_RC_COMPILER STREQUAL "CMAKE_RC_COMPILER-NOTFOUND")
    message(FATAL_ERROR "Could not find '${TOOLCHAIN_PREFIX_NAME}windres' in the specified toolchain: "
                        "${TOOLCHAIN_ROOT}'!")
endif ()

# search for programs in the build host directories
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)

# for libraries and headers in the target directories
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE BOTH)

# set big-obj flag for assembler
set(COMPILER_BIG_OBJ_FLAG "-Wa,-mbig-obj")

# set C flags
set(CMAKE_C_FLAGS
    "${CMAKE_C_FLAGS} ${COMPILER_BIG_OBJ_FLAG}" CACHE STRING "C flags")

# set CXX flags
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} ${COMPILER_BIG_OBJ_FLAG}" CACHE STRING "C++ flags")
