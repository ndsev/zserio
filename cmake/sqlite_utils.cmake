include(gcc_utils)

# A function to add SQLite library target
function(sqlite_add_library ${ZSERIO_PROJECT_ROOT})
    set(SQLITE_ROOT ${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/sqlite)

    # remove strict warning
    gcc_reset_warnings()

    # add sqlite library
    add_library(SQLite3 ${SQLITE_ROOT}/sqlite3.c)

    # configuration
    target_compile_definitions(SQLite3 PRIVATE SQLITE_ENABLE_FTS4 SQLITE_ENABLE_FTS5)

    if (UNIX)
        target_link_libraries(SQLite3 PUBLIC dl)
    endif ()

    set(ZSERIO_RUNTIME_SQLITE_LIBRARY SQLite3 PARENT_SCOPE)
    set(ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR ${SQLITE_ROOT} PARENT_SCOPE)
endfunction()
