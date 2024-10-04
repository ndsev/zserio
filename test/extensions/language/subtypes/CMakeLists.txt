add_library(subtypes_zs STATIC ${TEST_ZS_ROOT}/subtypes.zs)
zserio_generate_cpp(
    TARGET subtypes_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(subtypes_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(subtypes
    DEPENDS
        subtypes_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskSubtypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumSubtypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParamStructureSubtypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureSubtypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypeImportedTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt16SubtypeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
