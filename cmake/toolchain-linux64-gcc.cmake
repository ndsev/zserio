# set gcc
set(CMAKE_C_COMPILER gcc)
set(CMAKE_CXX_COMPILER g++)

# set -m64
set(COMPILER_M64_FLAG "-m64")

# set C flags
set(CMAKE_C_FLAGS
    "${CMAKE_C_FLAGS} ${COMPILER_M64_FLAG}" CACHE STRING "C flags")

# set CXX flags
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} ${COMPILER_M64_FLAG}" CACHE STRING "C++ flags")
