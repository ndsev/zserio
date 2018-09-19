# set Windows system
set(CMAKE_SYSTEM_NAME Windows)

# don't warn about unsafe CRT functions
add_definitions(-D_CRT_SECURE_NO_WARNINGS)
add_definitions(-D_SCL_SECURE_NO_WARNINGS)

add_definitions(-DNOMINMAX)

# needed by GRPC
add_definitions(-D_WIN32_WINNT=0x600)
add_definitions(-D_WINSOCK_DEPRECATED_NO_WARNINGS)
