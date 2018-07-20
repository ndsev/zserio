set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(DOC_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/doc)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/metamodel/Arity.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Arity.h
    ${ZSERIO_GEN_DIR}/metamodel/ArrayInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/ArrayInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/Atom.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Atom.h
    ${ZSERIO_GEN_DIR}/metamodel/AtomInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/AtomInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/AtomType.cpp
    ${ZSERIO_GEN_DIR}/metamodel/AtomType.h
    ${ZSERIO_GEN_DIR}/metamodel/Binary.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Binary.h
    ${ZSERIO_GEN_DIR}/metamodel/BinaryOperator.cpp
    ${ZSERIO_GEN_DIR}/metamodel/BinaryOperator.h
    ${ZSERIO_GEN_DIR}/metamodel/BitfieldInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/BitfieldInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/BuiltInInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/BuiltInInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/Category.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Category.h
    ${ZSERIO_GEN_DIR}/metamodel/CompoundInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/CompoundInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/ConstType.h
    ${ZSERIO_GEN_DIR}/metamodel/EnumInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/EnumInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/Expression.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Expression.h
    ${ZSERIO_GEN_DIR}/metamodel/ExpressionId.h
    ${ZSERIO_GEN_DIR}/metamodel/ExpressionInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/ExpressionInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/Field.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Field.h
    ${ZSERIO_GEN_DIR}/metamodel/FieldId.h
    ${ZSERIO_GEN_DIR}/metamodel/GlobalFieldRef.cpp
    ${ZSERIO_GEN_DIR}/metamodel/GlobalFieldRef.h
    ${ZSERIO_GEN_DIR}/metamodel/Import.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Import.h
    ${ZSERIO_GEN_DIR}/metamodel/LocalFieldRef.cpp
    ${ZSERIO_GEN_DIR}/metamodel/LocalFieldRef.h
    ${ZSERIO_GEN_DIR}/metamodel/Model.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Model.h
    ${ZSERIO_GEN_DIR}/metamodel/NameId.h
    ${ZSERIO_GEN_DIR}/metamodel/Package.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Package.h
    ${ZSERIO_GEN_DIR}/metamodel/ParamInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/ParamInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/ParamRef.cpp
    ${ZSERIO_GEN_DIR}/metamodel/ParamRef.h
    ${ZSERIO_GEN_DIR}/metamodel/StructureInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/Ternary.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Ternary.h
    ${ZSERIO_GEN_DIR}/metamodel/TernaryOperator.cpp
    ${ZSERIO_GEN_DIR}/metamodel/TernaryOperator.h
    ${ZSERIO_GEN_DIR}/metamodel/Type.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Type.h
    ${ZSERIO_GEN_DIR}/metamodel/TypeId.h
    ${ZSERIO_GEN_DIR}/metamodel/TypeInfo.cpp
    ${ZSERIO_GEN_DIR}/metamodel/TypeInfo.h
    ${ZSERIO_GEN_DIR}/metamodel/TypeRef.h
    ${ZSERIO_GEN_DIR}/metamodel/Unary.cpp
    ${ZSERIO_GEN_DIR}/metamodel/Unary.h
    ${ZSERIO_GEN_DIR}/metamodel/UnaryOperator.cpp
    ${ZSERIO_GEN_DIR}/metamodel/UnaryOperator.h
    ${ZSERIO_GEN_DIR}/metamodel/Value.h
)

zserio_add_library(TARGET metamodel_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE metamodel.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(
)

add_test_dependencies(metamodel_zs)
