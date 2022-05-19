# set Windows system
set(CMAKE_SYSTEM_NAME Windows)

# don't warn about unsafe CRT functions
add_definitions(-D_CRT_SECURE_NO_WARNINGS)
add_definitions(-D_SCL_SECURE_NO_WARNINGS)

add_definitions(-DNOMINMAX)

# needed by GRPC
add_definitions(-D_WIN32_WINNT=0x600)
add_definitions(-D_WINSOCK_DEPRECATED_NO_WARNINGS)

# set /EHsc
set(COMPILER_EXCEPTION_FLAG "/EHsc")

# set C flags
set(CMAKE_C_FLAGS
    "${CMAKE_C_FLAGS} ${COMPILER_EXCEPTION_FLAG}" CACHE STRING "C flags")

# set CXX flags
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} ${COMPILER_EXCEPTION_FLAG}" CACHE STRING "C++ flags")
