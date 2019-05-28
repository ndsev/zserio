# set Windows system
set(CMAKE_SYSTEM_NAME Windows)

# set default toolchain path
set(TOOLCHAIN_NAME "i686-w64-mingw32")
if ("$ENV{MINGW32_TOOLCHAIN_ROOT}" STREQUAL "")
    if (UNIX)
        set(TOOLCHAIN_ROOT "/opt/${TOOLCHAIN_NAME}")
    else ()
        set(TOOLCHAIN_ROOT "C:/Programs/${TOOLCHAIN_NAME}")
    endif ()
else ()
    set(TOOLCHAIN_ROOT "$ENV{MINGW32_TOOLCHAIN_ROOT}")
endif ()

# needed for find_xxxx command to search in ${TOOLCHAIN_ROOT)/lib(include) directories
set(CMAKE_SYSTEM_LIBRARY_PATH ${CMAKE_SYSTEM_LIBRARY_PATH} /lib)
set(CMAKE_SYSTEM_INCLUDE_PATH ${CMAKE_SYSTEM_INCLUDE_PATH} /include)

# specify path to toolchain
set(CMAKE_FIND_ROOT_PATH
    ${TOOLCHAIN_ROOT}
    ${TOOLCHAIN_ROOT}/toolchain)

# specify the cross compiler
find_program(CMAKE_C_COMPILER "${TOOLCHAIN_NAME}-gcc" PATHS "${TOOLCHAIN_ROOT}/toolchain/bin"
             NO_DEFAULT_PATH NO_CMAKE_FIND_ROOT_PATH)
find_program(CMAKE_CXX_COMPILER "${TOOLCHAIN_NAME}-g++" PATHS "${TOOLCHAIN_ROOT}/toolchain/bin"
             NO_DEFAULT_PATH NO_CMAKE_FIND_ROOT_PATH)
find_program(CMAKE_RC_COMPILER "${TOOLCHAIN_NAME}-windres" PATHS "${TOOLCHAIN_ROOT}/toolchain/bin"
             NO_DEFAULT_PATH ONLY_CMAKE_FIND_ROOT_PATH)

# check that the toolchain is available
if (CMAKE_C_COMPILER STREQUAL "CMAKE_C_COMPILER-NOTFOUND")
    message(FATAL_ERROR "Could not find '${TOOLCHAIN_NAME}-gcc' in the specified toolchain: "
                        "${TOOLCHAIN_ROOT}'!")
endif ()
if (CMAKE_CXX_COMPILER STREQUAL "CMAKE_CXX_COMPILER-NOTFOUND")
    message(FATAL_ERROR "Could not find '${TOOLCHAIN_NAME}-g++' in the specified toolchain: "
                        "${TOOLCHAIN_ROOT}'!")
endif ()
if (CMAKE_RC_COMPILER STREQUAL "CMAKE_RC_COMPILER-NOTFOUND")
    message(FATAL_ERROR "Could not find '${TOOLCHAIN_NAME}-windres' in the specified toolchain: "
                        "${TOOLCHAIN_ROOT}'!")
endif ()

# search for programs in the build host directories
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)

# for libraries and headers in the target directories
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE BOTH)
