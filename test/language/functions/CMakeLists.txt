add_library(functions_zs STATIC zs/functions.zs)
zserio_generate_cpp(
    TARGET functions_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(functions_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(functions
    DEPENDS
        functions_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureArrayParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureBytesTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureExternTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureOptionalTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureParentChildValueTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureStringTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureValueTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionArrayTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
