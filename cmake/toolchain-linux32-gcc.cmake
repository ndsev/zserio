# set gcc
set(CMAKE_C_COMPILER gcc)
set(CMAKE_CXX_COMPILER g++)

# set -m32
set(COMPILER_M32_FLAG "-m32")

# set C flags
set(CMAKE_C_FLAGS
    "${CMAKE_C_FLAGS} ${COMPILER_M32_FLAG}" CACHE STRING "C flags")

# set CXX flags
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} ${COMPILER_M32_FLAG}" CACHE STRING "C++ flags")
