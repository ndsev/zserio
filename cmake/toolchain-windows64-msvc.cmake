# set Windows system
set(CMAKE_SYSTEM_NAME Windows)

# don't warn about unsafe CRT functions
add_definitions(-D_CRT_SECURE_NO_WARNINGS)
add_definitions(-D_SCL_SECURE_NO_WARNINGS)

add_definitions(-DNOMINMAX)

# set parallel compilation
set(PROCESS_MAX "$ENV{MSVC_PROCESS_MAX}")
set(COMPILER_MP_FLAG "/MP${PROCESS_MAX}")

# set big-obj flag
set(COMPILER_BIG_OBJ_FLAG "/bigobj")

# set C flags (CMake adds /EHsc by default)
set(CMAKE_C_FLAGS_INIT "${COMPILER_MP_FLAG} ${COMPILER_BIG_OBJ_FLAG}")

# set CXX flags (CMake adds /EHsc by default)
set(CMAKE_CXX_FLAGS_INIT "${COMPILER_MP_FLAG} ${COMPILER_BIG_OBJ_FLAG}")
