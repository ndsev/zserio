# A function to create a zserio C++ runtime static library.
#
# Usage: zserio_add_runtime_library
#   RUNTIME_LIBRARY_DIR runtime_library_dir
function(zserio_add_runtime_library)
    # parse cmdline args
    foreach (ARG ${ARGV})
        if ((ARG STREQUAL RUNTIME_LIBRARY_DIR))
            if (DEFINED VALUE_${ARG})
                message(FATAL_ERROR "Option ${ARG} used multiple times!")
            endif ()
            set(ARG_NAME ${ARG})
        else ()
            if (DEFINED VALUE_${ARG_NAME})
                message(FATAL_ERROR "Argument ${ARG_NAME} requires exactly one value!")
            endif ()
            set(VALUE_${ARG_NAME} ${ARG})
        endif ()
    endforeach ()

    foreach (ARG RUNTIME_LIBRARY_DIR)
        if (NOT DEFINED VALUE_${ARG})
            message(FATAL_ERROR "No value defined for required argument ${ARG}!")
        endif ()
    endforeach ()

    add_subdirectory(${VALUE_RUNTIME_LIBRARY_DIR} ZserioCppRuntime)
endfunction()

# A function to create a static library out of Zserio-generated sources.
#
# Usage: zserio_add_library
#   TARGET target_name
#   SOURCE_DIR src_dir
#   MAIN_SOURCE src_file
#   SOURCES all source files (optional, relative to SOURCE_DIR)
#   OUT_DIR out_dir
#   OUT_FILES out_files...
#   ZSERIO_CORE_DIR zserio_core_dir
#   ZSERIO_CPP_DIR zserio_cpp_dir
#   ZSERIO_OPTIONS ... (optional)
#   IGNORE_WARNINGS ON|OFF (optional, default OFF)
#   IGNORE_ERRORS ON|OFF (optional, default OFF)
#   ZSERIO_LOG_FILENAME (optional)
#
# Only the files mentioned in OUT_FILES will be added to the static library target.
# OUT_FILES can be EMPTY if no output should be generated.
# Glob is not used because using GLOB for sources is frowned upon in CMake world.
# (CMake doesn't pick up changes in the glob, e.g. added files.)
#
# The actual Zserio generation target is added to the top-level target "gen".
function(zserio_add_library)
    # parse cmdline args
    foreach (ARG ${ARGV})
        if ((ARG STREQUAL TARGET) OR
            (ARG STREQUAL SOURCE_DIR) OR
            (ARG STREQUAL MAIN_SOURCE) OR
            (ARG STREQUAL SOURCES) OR
            (ARG STREQUAL OUT_DIR) OR
            (ARG STREQUAL OUT_FILES) OR
            (ARG STREQUAL ZSERIO_CORE_DIR) OR
            (ARG STREQUAL ZSERIO_CPP_DIR) OR
            (ARG STREQUAL ZSERIO_OPTIONS) OR
            (ARG STREQUAL IGNORE_WARNINGS) OR
            (ARG STREQUAL IGNORE_ERRORS) OR
            (ARG STREQUAL ZSERIO_LOG_FILENAME))
            if (DEFINED VALUE_${ARG})
                message(FATAL_ERROR "Option ${ARG} used multiple times!")
            endif ()
            set(ARG_NAME ${ARG})
        else ()
            list(APPEND VALUE_${ARG_NAME} ${ARG})
        endif ()
    endforeach ()

    foreach (ARG TARGET SOURCE_DIR MAIN_SOURCE OUT_DIR OUT_FILES ZSERIO_CORE_DIR ZSERIO_CPP_DIR)
        if (NOT DEFINED VALUE_${ARG})
            message(FATAL_ERROR "No value defined for required argument ${ARG}!")
        endif ()
    endforeach ()

    foreach (ARG TARGET SOURCE_DIR MAIN_SOURCE OUT_DIR ZSERIO_CORE_DIR ZSERIO_CPP_DIR)
        list(LENGTH VALUE_${ARG} LEN)
        if (NOT(LEN EQUAL 1))
            message(FATAL_ERROR "Argument ${ARG} requires exactly one value!")
        endif ()
    endforeach ()

    # create ALL_SOURCES list with full paths
    foreach (SOURCE ${VALUE_SOURCES})
        list(APPEND ALL_SOURCES ${VALUE_SOURCE_DIR}/${SOURCE})
    endforeach ()
    list(APPEND ALL_SOURCES ${VALUE_SOURCE_DIR}/${VALUE_MAIN_SOURCE})

    # Java is required, so search for it already here at file-scope
    if (NOT DEFINED JAVA_BIN OR JAVA_BIN STREQUAL "JAVA_BIN-NOTFOUND")
        find_program(JAVA_BIN java PATHS $ENV{JAVA_HOME}/bin ENV PATH NO_DEFAULT_PATH)
        if (JAVA_BIN STREQUAL "JAVA_BIN-NOTFOUND")
            message(FATAL_ERROR "Java not found, define JAVA_BIN in CMake or JAVA_HOME in environment!")
        endif ()
    endif ()

    # check if library is header only
    if ("${VALUE_OUT_FILES}" STREQUAL "EMPTY")
        set(VALUE_OUT_FILES "")
    endif ()
    string(FIND "${VALUE_OUT_FILES}" ".cpp" SOURCE_FILE_POSITION)

    # set ${VALUE_OUT_FILES} as GENERATED
    set_source_files_properties(${VALUE_OUT_FILES} PROPERTIES GENERATED TRUE)

    # set zserio extra options given by environment
    set(ZSERIO_EXTRA_OPTIONS "$ENV{ZSERIO_EXTRA_ARGS}")
    separate_arguments(ZSERIO_EXTRA_OPTIONS)

    # hack to always re-run Zserio compiler (Zserio itself can skip sources generations if it's not needed)
    # - uses ${VALUE_TARGET}_ALWAYS_GENERATE output which will be never generated and thus it will always re-run
    add_custom_command(OUTPUT ${VALUE_TARGET}_ALWAYS_GENERATE
        COMMAND ${CMAKE_COMMAND} -DJAVA_BIN=${JAVA_BIN}
            -DCORE_DIR=${VALUE_ZSERIO_CORE_DIR} -DCPP_DIR=${VALUE_ZSERIO_CPP_DIR} -DOUT_DIR=${VALUE_OUT_DIR}
            -DSOURCE_DIR=${VALUE_SOURCE_DIR} -DMAIN_SOURCE=${VALUE_MAIN_SOURCE}
            -DOPTIONS="${VALUE_ZSERIO_OPTIONS}" -DEXTRA_OPTIONS="${ZSERIO_EXTRA_OPTIONS}"
            -DIGNORE_WARNINGS=${VALUE_IGNORE_WARNINGS}
            -DIGNORE_ERRORS=${VALUE_IGNORE_ERRORS}
            -DLOG_FILENAME="${VALUE_ZSERIO_LOG_FILENAME}"
            -P ${CMAKE_MODULE_PATH}/zserio_tool.cmake
        COMMENT "Generating sources with Zserio from ${VALUE_MAIN_SOURCE}")

    # add a custom target for the generation step
    add_custom_target(${VALUE_TARGET}_generate
        DEPENDS ${VALUE_TARGET}_ALWAYS_GENERATE)

    # add to custom "gen" target
    if (NOT TARGET gen)
        add_custom_target(gen COMMENT "Trigger compilation of all included zserio files.")
    endif ()
    add_dependencies(gen ${VALUE_TARGET}_generate)

    # delete whole directory even if Zserio generated a file that's not listed in ZSERIO_GENERATED_SOURCES
    set_property(DIRECTORY APPEND PROPERTY ADDITIONAL_MAKE_CLEAN_FILES ${VALUE_OUT_DIR})

    # add a static library
    if (SOURCE_FILE_POSITION EQUAL -1)
        add_library(${VALUE_TARGET} INTERFACE)
        add_dependencies(${VALUE_TARGET} ${VALUE_TARGET}_generate)
        target_include_directories(${VALUE_TARGET} INTERFACE ${VALUE_OUT_DIR})
        target_link_libraries(${VALUE_TARGET} INTERFACE ZserioCppRuntime)
    else ()
        add_library(${VALUE_TARGET} STATIC ${VALUE_OUT_FILES})
        add_dependencies(${VALUE_TARGET} ${VALUE_TARGET}_generate)
        target_include_directories(${VALUE_TARGET} PUBLIC ${VALUE_OUT_DIR})
        target_link_libraries(${VALUE_TARGET} PUBLIC ZserioCppRuntime)
        if (SOURCE_FILE_POSITION EQUAL -1)
            # make sure that cmake knows language when no sources are available
            set_target_properties(${VALUE_TARGET} PROPERTIES LINKER_LANGUAGE CXX)
        endif ()
        set_target_properties(${VALUE_TARGET} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES
                CXX_EXTENSIONS NO)
    endif ()

    # add cppcheck custom command (cppcheck fails if no sources to check are available)
    if (NOT(SOURCE_FILE_POSITION EQUAL -1))
        include(cppcheck_utils)
        set(SUPPRESSION_FILE_NAME "${CMAKE_CURRENT_SOURCE_DIR}/cpp/CppcheckSuppressions.txt")
        cppcheck_add_custom_command(TARGET ${VALUE_TARGET}
                                    SOURCE_DIR "${VALUE_OUT_DIR}"
                                    INCLUDE_DIR "${VALUE_OUT_DIR}"
                                    SUPPRESSION_FILE "${SUPPRESSION_FILE_NAME}"
                                    # add suppression needed due to generated field constructors
                                    OPTIONS --suppress=useInitializationList:*gen*/*.h)
    endif ()
endfunction()
