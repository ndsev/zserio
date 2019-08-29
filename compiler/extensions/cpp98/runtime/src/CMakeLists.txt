# Zserio C++ runtime library.
#
# This CMake file defines a static library target named ZserioCppRuntime that contains the zserio C++
# runtime.
#
# This CMake file is designed to be included directly without any further dependencies.
#
# The following variables can be set in CMake before adding this file:
#
# ZSERIO_RUNTIME_INCLUDE_INSPECTOR - Set to ON or OFF. When "ON" support for blob inspector is included.
#                                    In this case, application sources must be compiled with compilation
#                                    macro ZSERIO_RUNTIME_INCLUDE_INSPECTOR. When not defined
#                                    behaves as if it's set to OFF.
#
# An example application CMakeLists.txt follows:
#
# project(ZserioTestApp)
#
# set(ZSERIO_RUNTIME_INCLUDE_INSPECTOR ON)
# add_subdirectory(runtime_libs/cpp ZserioCppRuntime)
#
# set_property(SOURCE HelloWorld.cpp APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_INCLUDE_INSPECTOR)
# add_executable(ZserioTestApp HelloWorld.cpp)
# target_link_libraries(ZserioTestApp ZserioCppRuntime)

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppRuntime)

if (DEFINED ZSERIO_RUNTIME_INCLUDE_INSPECTOR)
    set(ZSERIO_RUNTIME_INCLUDE_INSPECTOR_DEFAULT ${ZSERIO_RUNTIME_INCLUDE_INSPECTOR})
else ()
    set(ZSERIO_RUNTIME_INCLUDE_INSPECTOR_DEFAULT OFF)
endif ()

option(ZSERIO_RUNTIME_INCLUDE_INSPECTOR "Include support for Blob Inspector."
       ${ZSERIO_RUNTIME_INCLUDE_INSPECTOR_DEFAULT})

set(ZSERIO_CPP_RUNTIME_LIB_SRCS
    zserio/AlignedStorage.h
    zserio/AnyHolder.h
    zserio/ArrayBase.h
    zserio/BasicArray.h
    zserio/BitFieldArray.h
    zserio/BitFieldUtil.cpp
    zserio/BitFieldUtil.h
    zserio/BitPositionUtil.cpp
    zserio/BitPositionUtil.h
    zserio/BitSizeOfCalculator.cpp
    zserio/BitSizeOfCalculator.h
    zserio/BitStreamException.h
    zserio/BitStreamReader.cpp
    zserio/BitStreamReader.h
    zserio/BitStreamWriter.cpp
    zserio/BitStreamWriter.h
    zserio/BuildInOperators.cpp
    zserio/BuildInOperators.h
    zserio/ConstraintException.h
    zserio/Container.h
    zserio/CppRuntimeException.h
    zserio/FloatUtil.cpp
    zserio/FloatUtil.h
    zserio/HashCodeUtil.h
    zserio/ISqliteDatabase.h
    zserio/ISqliteDatabaseReader.h
    zserio/ObjectArray.h
    zserio/OptionalHolder.h
    zserio/PreWriteAction.h
    zserio/SqliteConnection.h
    zserio/SqliteException.h
    zserio/StringConvertUtil.cpp
    zserio/StringConvertUtil.h
    zserio/StringHolder.h
    zserio/Types.h
    zserio/VarUInt64Util.cpp
    zserio/VarUInt64Util.h
)

if (ZSERIO_RUNTIME_INCLUDE_INSPECTOR)
    set(ZSERIO_CPP_RUNTIME_LIB_SRCS
        ${ZSERIO_CPP_RUNTIME_LIB_SRCS}
        zserio/inspector/BlobInspectorTree.cpp
        zserio/inspector/BlobInspectorTree.h
        zserio/inspector/BlobInspectorTreeUtil.cpp
        zserio/inspector/BlobInspectorTreeUtil.h
        zserio/inspector/BlobInspectorValue.cpp
        zserio/inspector/BlobInspectorValue.h
        zserio/inspector/BlobTreeArrayHandler.h
        zserio/inspector/IBlobInspectorValueListener.h
    )
    set_property(SOURCE ${ZSERIO_CPP_RUNTIME_LIB_SRCS}
                 APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_INCLUDE_INSPECTOR)
endif ()

if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    # allows bit stream reader optimization for 64bit platforms
    set_property(SOURCE zserio/BitStreamReader.cpp
                 APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_64BIT)
endif ()

add_library(ZserioCppRuntime STATIC ${ZSERIO_CPP_RUNTIME_LIB_SRCS})

target_include_directories(ZserioCppRuntime PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

