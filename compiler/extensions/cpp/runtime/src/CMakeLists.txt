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
# ZSERIO_RUNTIME_INCLUDE_RELATIONAL - Set to ON or OFF. When "ON" support for Zserio relational extension is
#                                     included. When "OFF", Zserio must be used with command line argument
#                                     '-withoutSqlCode'. Default is OFF.
#
# ZSERIO_RUNTIME_SQLITE_LIBRARY     - Set SQLite library to use.
#
# ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR - Set directory with SQLite include files (sqlite3.h).
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

if (DEFINED ZSERIO_RUNTIME_INCLUDE_RELATIONAL)
    set(ZSERIO_RUNTIME_INCLUDE_RELATIONAL_DEFAULT ${ZSERIO_RUNTIME_INCLUDE_RELATIONAL})
else ()
    set(ZSERIO_RUNTIME_INCLUDE_RELATIONAL_DEFAULT OFF)
endif ()

option(ZSERIO_RUNTIME_INCLUDE_INSPECTOR "Include support for Blob Inspector."
       ${ZSERIO_RUNTIME_INCLUDE_INSPECTOR_DEFAULT})
option(ZSERIO_RUNTIME_INCLUDE_RELATIONAL "Include support for Zserio Relational Extension."
       ${ZSERIO_RUNTIME_INCLUDE_RELATIONAL_DEFAULT})

if (ZSERIO_RUNTIME_INCLUDE_RELATIONAL)
    if (DEFINED ZSERIO_RUNTIME_SQLITE_LIBRARY)
        set(ZSERIO_RUNTIME_SQLITE_LIBRARY_DEFAULT ${ZSERIO_RUNTIME_SQLITE_LIBRARY})
    else ()
        set(ZSERIO_RUNTIME_SQLITE_LIBRARY_DEFAULT "NOT-FOUND")
    endif ()

    if (DEFINED ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR)
        set(ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR_DEFAULT ${ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR})
    else ()
        set(ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR_DEFAULT "NOT-FOUND")
    endif ()

    set(ZSERIO_RUNTIME_SQLITE_LIBRARY ${ZSERIO_RUNTIME_SQLITE_LIBRARY_DEFAULT}
        CACHE PATH "SQLite3 library to use.")
    set(ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR ${ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR_DEFAULT}
        CACHE PATH "SQLite3 include directory.")

    if (${ZSERIO_RUNTIME_SQLITE_LIBRARY} STREQUAL "NOT-FOUND")
        message(FATAL_ERROR "SQLite3 library not found. Please set ZSERIO_RUNTIME_SQLITE_LIBRARY correctly.")
    endif ()

    if (NOT IS_DIRECTORY ${ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR})
        message(FATAL_ERROR
                "SQLite3 include directory not found. Please set ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR correctly.")
    endif ()
endif ()

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
    zserio/ObjectArray.h
    zserio/OptionalHolder.h
    zserio/PreWriteAction.h
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

if (ZSERIO_RUNTIME_INCLUDE_RELATIONAL)
    set(ZSERIO_CPP_RUNTIME_LIB_SRCS
        ${ZSERIO_CPP_RUNTIME_LIB_SRCS}
        zserio/ISqlDatabaseWriter.h
        zserio/SqlDatabase.cpp
        zserio/SqlDatabase.h
        zserio/SqliteException.h
    )
endif ()

if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    # allows bit stream reader optimization for 64bit platforms
    set_property(SOURCE zserio/BitStreamReader.cpp
                 APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_64BIT)
endif ()

add_library(ZserioCppRuntime STATIC ${ZSERIO_CPP_RUNTIME_LIB_SRCS})

target_include_directories(ZserioCppRuntime PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

if (ZSERIO_RUNTIME_INCLUDE_RELATIONAL)
    target_link_libraries(ZserioCppRuntime PUBLIC ${ZSERIO_RUNTIME_SQLITE_LIBRARY})
    target_include_directories(ZserioCppRuntime PUBLIC ${ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR})
endif ()
