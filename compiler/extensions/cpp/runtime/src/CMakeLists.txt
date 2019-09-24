# Zserio C++ runtime library.
#
# This CMake file defines a static library target named ZserioCppRuntime that contains the zserio C++
# runtime.
#
# This CMake file is designed to be included directly without any further dependencies.
#
# An example application CMakeLists.txt follows:
#
# project(ZserioTestApp)
#
# add_subdirectory(runtime_libs/cpp ZserioCppRuntime)
#
# add_executable(ZserioTestApp HelloWorld.cpp)
# target_link_libraries(ZserioTestApp ZserioCppRuntime)

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppRuntime)

set(ZSERIO_CPP_RUNTIME_LIB_SRCS
    zserio/Arrays.h
    zserio/BitFieldUtil.cpp
    zserio/BitFieldUtil.h
    zserio/BitPositionUtil.cpp
    zserio/BitPositionUtil.h
    zserio/BitSizeOfCalculator.cpp
    zserio/BitSizeOfCalculator.h
    zserio/BitStream.cpp
    zserio/BitStream.h
    zserio/BitStreamException.h
    zserio/BitStreamReader.cpp
    zserio/BitStreamReader.h
    zserio/BitStreamWriter.cpp
    zserio/BitStreamWriter.h
    zserio/BuildInOperators.cpp
    zserio/BuildInOperators.h
    zserio/ConstraintException.h
    zserio/CppRuntimeException.h
    zserio/Enums.h
    zserio/FloatUtil.cpp
    zserio/FloatUtil.h
    zserio/HashCodeUtil.h
    zserio/ISqliteDatabase.h
    zserio/ISqliteDatabaseReader.h
    zserio/PreWriteAction.h
    zserio/SqliteConnection.h
    zserio/SqliteException.h
    zserio/StringConvertUtil.cpp
    zserio/StringConvertUtil.h
    zserio/Types.h
    zserio/VarUInt64Util.cpp
    zserio/VarUInt64Util.h
)

if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    # allows bit stream reader optimization for 64bit platforms
    set_property(SOURCE zserio/BitStreamReader.cpp
                 APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_64BIT)
endif ()

add_library(ZserioCppRuntime STATIC ${ZSERIO_CPP_RUNTIME_LIB_SRCS})

target_include_directories(ZserioCppRuntime PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
