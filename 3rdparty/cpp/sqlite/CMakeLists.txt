# Simple CMake support for sqlite3 library as needed by Zserio
# Note: We need to use add_subdirectory for SQLite to make in independent on compiler setup of the projects
#       which uses it (e.g. in runtime tests we setup sanitizers which we don't want to use for SQLite)
cmake_minimum_required(VERSION 3.1.0)

project(sqlite3)

add_library(SQLite3 sqlite3.c sqlite3.h)

target_compile_definitions(SQLite3 PRIVATE SQLITE_ENABLE_FTS4 SQLITE_ENABLE_FTS5)

if (UNIX)
    target_link_libraries(SQLite3 PUBLIC dl)
endif ()
