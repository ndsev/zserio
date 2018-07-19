# hack for older distributions that place 32bit libs in /usr/lib32 (or perhaps /lib32)
if (IS_DIRECTORY "/lib32")
    set(ENV{LIB} "$ENV{LIB}:/lib32")
endif ()

if (IS_DIRECTORY "/usr/lib32")
    set(ENV{LIB} "$ENV{LIB}:/usr/lib32")
endif ()

# set -m32
set(COMPILER_M32_FLAG "-m32")

# set C flags
set(CMAKE_C_FLAGS
    "${CMAKE_C_FLAGS} ${COMPILER_M32_FLAG}" CACHE STRING "C flags")

# set CXX flags
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} ${COMPILER_M32_FLAG}" CACHE STRING "C++ flags")
