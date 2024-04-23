# set Windows system
set(CMAKE_SYSTEM_NAME Windows)

# don't warn about unsafe CRT functions
add_definitions(-D_CRT_SECURE_NO_WARNINGS)
add_definitions(-D_SCL_SECURE_NO_WARNINGS)

add_definitions(-DNOMINMAX)

# set /EHsc
set(COMPILER_EXCEPTION_FLAG "/EHsc")

# set parallel compilation
set(PROCESS_MAX "$ENV{MSVC_PROCESS_MAX}")
set(COMPILER_MP_FLAG "/MP${PROCESS_MAX}")

# set big-obj flag
set(COMPILER_BIG_OBJ_FLAG "/bigobj")

# set C flags
set(CMAKE_C_FLAGS
    "${CMAKE_C_FLAGS} ${COMPILER_EXCEPTION_FLAG} ${COMPILER_MP_FLAG} ${COMPILER_BIG_OBJ_FLAG}"
    CACHE STRING "C flags")

# set CXX flags
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} ${COMPILER_EXCEPTION_FLAG} ${COMPILER_MP_FLAG} ${COMPILER_BIG_OBJ_FLAG}"
    CACHE STRING "C++ flags")
