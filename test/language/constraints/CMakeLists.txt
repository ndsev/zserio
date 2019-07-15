set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/constraints/choice_constraints/ChoiceConstraints.cpp
    ${ZSERIO_GEN_DIR}/constraints/choice_constraints/ChoiceConstraints.h

    ${ZSERIO_GEN_DIR}/constraints/structure_constraints/BasicColor.cpp
    ${ZSERIO_GEN_DIR}/constraints/structure_constraints/BasicColor.h
    ${ZSERIO_GEN_DIR}/constraints/structure_constraints/ExtendedColor.cpp
    ${ZSERIO_GEN_DIR}/constraints/structure_constraints/ExtendedColor.h
    ${ZSERIO_GEN_DIR}/constraints/structure_constraints/StructureConstraints.cpp
    ${ZSERIO_GEN_DIR}/constraints/structure_constraints/StructureConstraints.h

    ${ZSERIO_GEN_DIR}/constraints/array_lengthof_constraint/ArrayLengthofConstraint.cpp
    ${ZSERIO_GEN_DIR}/constraints/array_lengthof_constraint/ArrayLengthofConstraint.h
)

zserio_add_library(TARGET constraints_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE constraints.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/ArrayLengthofConstraintTest.cpp
    cpp/ChoiceConstraintsTest.cpp
    cpp/StructureConstraintsTest.cpp
)

add_test_dependencies(constraints_zs)
