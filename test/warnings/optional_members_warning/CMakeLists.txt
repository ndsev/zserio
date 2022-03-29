set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_array_length/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_array_length/Container.h

    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_bitfield_length/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_bitfield_length/Container.h

    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_constraint/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_constraint/Container.h

    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_offset/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_offset/Container.h
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_offset/OffsetHolder.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_offset/OffsetHolder.h
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_offset/OptionalOffsetHolder.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_offset/OptionalOffsetHolder.h

    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_optional_clause/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_optional_clause/Container.h

    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/Another.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/Another.h
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/BasicColor.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/BasicColor.h
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/ColorTones.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/ColorTones.h
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/Container.cpp
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/Container.h
    ${ZSERIO_GEN_DIR}/optional_members_warning/optional_references_in_type_arguments/SubtypedColorTones.h
)

zserio_add_library(TARGET optional_members_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE optional_members_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 24
    ZSERIO_LOG_FILENAME zserio_log.txt
)

add_test_sources(
    cpp/OptionalMembersWarningTest.cpp
)

add_test_dependencies(optional_members_warning_zs)
