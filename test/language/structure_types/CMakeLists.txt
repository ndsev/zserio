add_library(structure_types_zs STATIC zs/structure_types.zs)
zserio_generate_cpp(
    TARGET structure_types_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(structure_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(structure_types
    DEPENDS
        structure_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyStructureTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyStructureWithParameterTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FieldConstructorClashingTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OneStringStructureTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SimpleStructureTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureInnerClassesClashingTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureWithRemovedEnumItemFieldTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
