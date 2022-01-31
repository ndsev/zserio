#include "gtest/gtest.h"

#include "with_type_info_code/SqlDatabase.h"
#include "with_type_info_code/SimplePubsub.h"
#include "with_type_info_code/SimpleService.h"

using namespace zserio::literals;

namespace with_type_info_code
{

class WithTypeInfoCodeTest : public ::testing::Test
{
protected:
    void checkSqlDatabase(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.SqlDatabase"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::SQL_DATABASE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::SQL_DATABASE, typeInfo.getCppType());

        const zserio::Span<const zserio::TableInfo> tables = typeInfo.getTables();
        ASSERT_EQ(5, tables.size());

        // sqlTable
        const zserio::TableInfo& sqlTable = tables[0];
        ASSERT_EQ("sqlTable"_sv, sqlTable.schemaName);
        checkSqlTable(sqlTable.typeInfo);

        // templatedSqlTableU32
        const zserio::TableInfo& templatedSqlTableU32 = tables[1];
        ASSERT_EQ("templatedSqlTableU32"_sv, templatedSqlTableU32.schemaName);
        checkTemplatedSqlTable_uint32(templatedSqlTableU32.typeInfo);

        // templatedSqlTableU8
        const zserio::TableInfo& templatedSqlTableU8 = tables[2];
        ASSERT_EQ("templatedSqlTableU8"_sv, templatedSqlTableU8.schemaName);
        checkTemplatedSqlTableU8(templatedSqlTableU8.typeInfo);

        // fts4Table
        const zserio::TableInfo& fts4Table = tables[3];
        ASSERT_EQ("fts4Table"_sv, fts4Table.schemaName);
        checkFts4Table(fts4Table.typeInfo);

        // withoutRowIdTable
        const zserio::TableInfo& withoutRowIdTable = tables[4];
        ASSERT_EQ("withoutRowIdTable"_sv, withoutRowIdTable.schemaName);
        checkWithoutRowIdTable(withoutRowIdTable.typeInfo);
    }

    void checkSqlTable(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.SqlTable"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::SQL_TABLE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::SQL_TABLE, typeInfo.getCppType());

        ASSERT_EQ(""_sv, typeInfo.getSqlConstraint());
        ASSERT_EQ(""_sv, typeInfo.getVirtualTableUsing());
        ASSERT_EQ(false, typeInfo.isWithoutRowId());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::ColumnInfo> columns = typeInfo.getColumns();
        ASSERT_EQ(2, columns.size());

        // pk
        const zserio::ColumnInfo& pkColumn = columns[0];
        ASSERT_EQ("pk"_sv, pkColumn.schemaName);

        ASSERT_EQ("uint32"_sv, pkColumn.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, pkColumn.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, pkColumn.typeInfo.getCppType());
        ASSERT_EQ(32, pkColumn.typeInfo.getBitSize());

        ASSERT_EQ(0, pkColumn.typeArguments.size());
        ASSERT_EQ("INTEGER"_sv, pkColumn.sqlTypeName);
        ASSERT_EQ("PRIMARY KEY NOT NULL"_sv, pkColumn.sqlConstraint);
        ASSERT_EQ(false, pkColumn.isVirtual);

        // text
        const zserio::ColumnInfo& textColumn = columns[1];
        ASSERT_EQ("text"_sv, textColumn.schemaName);

        ASSERT_EQ("string"_sv, textColumn.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRING, textColumn.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRING, textColumn.typeInfo.getCppType());

        ASSERT_EQ(0, textColumn.typeArguments.size());
        ASSERT_EQ("TEXT"_sv, textColumn.sqlTypeName);
        ASSERT_EQ(""_sv, textColumn.sqlConstraint);
        ASSERT_EQ(false, textColumn.isVirtual);
    }

    void checkTemplatedSqlTable_uint32(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.TemplatedSqlTable_uint32"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::SQL_TABLE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::SQL_TABLE, typeInfo.getCppType());

        ASSERT_EQ("PRIMARY KEY(pk)"_sv, typeInfo.getSqlConstraint());
        ASSERT_EQ(""_sv, typeInfo.getVirtualTableUsing());
        ASSERT_EQ(false, typeInfo.isWithoutRowId());

        ASSERT_EQ("with_type_info_code.TemplatedSqlTable"_sv, typeInfo.getTemplateName());
        const zserio::Span<const zserio::TemplateArgumentInfo> templateArgs = typeInfo.getTemplateArguments();
        ASSERT_EQ(1, templateArgs.size());
        const zserio::ITypeInfo& templateArg0Info = templateArgs[0].typeInfo;
        ASSERT_EQ("uint32"_sv, templateArg0Info.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, templateArg0Info.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, templateArg0Info.getCppType());
        ASSERT_EQ(32, templateArg0Info.getBitSize());

        const zserio::Span<const zserio::ColumnInfo> columns = typeInfo.getColumns();
        ASSERT_EQ(2, columns.size());

        // pk
        const zserio::ColumnInfo& pkColumn = columns[0];
        ASSERT_EQ("pk"_sv, pkColumn.schemaName);

        ASSERT_EQ("uint32"_sv, pkColumn.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, pkColumn.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, pkColumn.typeInfo.getCppType());
        ASSERT_EQ(32, pkColumn.typeInfo.getBitSize());

        ASSERT_EQ(0, pkColumn.typeArguments.size());
        ASSERT_EQ("INTEGER"_sv, pkColumn.sqlTypeName);
        ASSERT_EQ("NOT NULL"_sv, pkColumn.sqlConstraint);
        ASSERT_EQ(false, pkColumn.isVirtual);

        // withTypeInfoCode
        const zserio::ColumnInfo& withTypeInfoCodeColumn = columns[1];
        ASSERT_EQ("withTypeInfoCode"_sv, withTypeInfoCodeColumn.schemaName);
        checkWithTypeInfoCode(withTypeInfoCodeColumn.typeInfo);
        ASSERT_EQ(0, withTypeInfoCodeColumn.typeArguments.size());
        ASSERT_EQ("BLOB"_sv, withTypeInfoCodeColumn.sqlTypeName);
        ASSERT_EQ(""_sv, withTypeInfoCodeColumn.sqlConstraint);
        ASSERT_EQ(false, withTypeInfoCodeColumn.isVirtual);
    }

    void checkWithTypeInfoCode(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.WithTypeInfoCode"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRUCT, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRUCT, typeInfo.getCppType());

        ASSERT_EQ(0, typeInfo.getParameters().size());
        ASSERT_EQ(0, typeInfo.getFunctions().size());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(12, fields.size());

        // simpleStruct
        const zserio::FieldInfo& simpleStructField = fields[0];
        ASSERT_EQ("simpleStruct"_sv, simpleStructField.schemaName);

        checkSimpleStruct(simpleStructField.typeInfo);

        ASSERT_EQ(0, simpleStructField.typeArguments.size());
        ASSERT_EQ(""_sv, simpleStructField.alignment);
        ASSERT_EQ(""_sv, simpleStructField.offset);
        ASSERT_EQ(""_sv, simpleStructField.initializer);
        ASSERT_EQ(false, simpleStructField.isOptional);
        ASSERT_EQ(""_sv, simpleStructField.optionalCondition);
        ASSERT_EQ(""_sv, simpleStructField.constraint);
        ASSERT_EQ(false, simpleStructField.isArray);
        ASSERT_EQ(""_sv, simpleStructField.arrayLength);
        ASSERT_EQ(false, simpleStructField.isPacked);
        ASSERT_EQ(false, simpleStructField.isImplicit);

        // complexStruct
        const zserio::FieldInfo& complexStructField = fields[1];
        ASSERT_EQ("complexStruct"_sv, complexStructField.schemaName);

        checkComplexStruct(complexStructField.typeInfo);

        ASSERT_EQ(0, complexStructField.typeArguments.size());
        ASSERT_EQ(""_sv, complexStructField.alignment);
        ASSERT_EQ(""_sv, complexStructField.offset);
        ASSERT_EQ(""_sv, complexStructField.initializer);
        ASSERT_EQ(false, complexStructField.isOptional);
        ASSERT_EQ(""_sv, complexStructField.optionalCondition);
        ASSERT_EQ(""_sv, complexStructField.constraint);
        ASSERT_EQ(false, complexStructField.isArray);
        ASSERT_EQ(""_sv, complexStructField.arrayLength);
        ASSERT_EQ(false, complexStructField.isPacked);
        ASSERT_EQ(false, complexStructField.isImplicit);

        // parameterizedStruct
        const zserio::FieldInfo& parameterizedStructField = fields[2];
        ASSERT_EQ("parameterizedStruct"_sv, parameterizedStructField.schemaName);

        checkParameterizedStruct(parameterizedStructField.typeInfo);

        ASSERT_EQ(1, parameterizedStructField.typeArguments.size());
        ASSERT_EQ("getSimpleStruct()"_sv, parameterizedStructField.typeArguments[0]);
        ASSERT_EQ(""_sv, parameterizedStructField.alignment);
        ASSERT_EQ(""_sv, parameterizedStructField.offset);
        ASSERT_EQ(""_sv, parameterizedStructField.initializer);
        ASSERT_EQ(false, parameterizedStructField.isOptional);
        ASSERT_EQ(""_sv, parameterizedStructField.optionalCondition);
        ASSERT_EQ(""_sv, parameterizedStructField.constraint);
        ASSERT_EQ(false, parameterizedStructField.isArray);
        ASSERT_EQ(""_sv, parameterizedStructField.arrayLength);
        ASSERT_EQ(false, parameterizedStructField.isPacked);
        ASSERT_EQ(false, parameterizedStructField.isImplicit);

        // recursiveStruct
        const zserio::FieldInfo& recursiveStructField = fields[3];
        ASSERT_EQ("recursiveStruct"_sv, recursiveStructField.schemaName);

        checkRecursiveStruct(recursiveStructField.typeInfo);

        ASSERT_EQ(0, recursiveStructField.typeArguments.size());
        ASSERT_EQ(""_sv, recursiveStructField.alignment);
        ASSERT_EQ(""_sv, recursiveStructField.offset);
        ASSERT_EQ(""_sv, recursiveStructField.initializer);
        ASSERT_EQ(false, recursiveStructField.isOptional);
        ASSERT_EQ(""_sv, recursiveStructField.optionalCondition);
        ASSERT_EQ(""_sv, recursiveStructField.constraint);
        ASSERT_EQ(false, recursiveStructField.isArray);
        ASSERT_EQ(""_sv, recursiveStructField.arrayLength);
        ASSERT_EQ(false, recursiveStructField.isPacked);
        ASSERT_EQ(false, recursiveStructField.isImplicit);

        // recursiveUnion
        const zserio::FieldInfo& recursiveUnion = fields[4];
        ASSERT_EQ("recursiveUnion"_sv, recursiveUnion.schemaName);

        checkRecursiveUnion(recursiveUnion.typeInfo);

        ASSERT_EQ(0, recursiveUnion.typeArguments.size());
        ASSERT_EQ(""_sv, recursiveUnion.alignment);
        ASSERT_EQ(""_sv, recursiveUnion.offset);
        ASSERT_EQ(""_sv, recursiveUnion.initializer);
        ASSERT_EQ(false, recursiveUnion.isOptional);
        ASSERT_EQ(""_sv, recursiveUnion.optionalCondition);
        ASSERT_EQ(""_sv, recursiveUnion.constraint);
        ASSERT_EQ(false, recursiveUnion.isArray);
        ASSERT_EQ(""_sv, recursiveUnion.arrayLength);
        ASSERT_EQ(false, recursiveUnion.isPacked);
        ASSERT_EQ(false, recursiveUnion.isImplicit);

        // recursiveChoice
        const zserio::FieldInfo& recursiveChoice = fields[5];
        ASSERT_EQ("recursiveChoice"_sv, recursiveChoice.schemaName);

        checkRecursiveChoice(recursiveChoice.typeInfo);

        ASSERT_EQ(2, recursiveChoice.typeArguments.size());
        ASSERT_EQ("true"_sv, recursiveChoice.typeArguments[0]);
        ASSERT_EQ("false"_sv, recursiveChoice.typeArguments[1]);
        ASSERT_EQ(""_sv, recursiveChoice.alignment);
        ASSERT_EQ(""_sv, recursiveChoice.offset);
        ASSERT_EQ(""_sv, recursiveChoice.initializer);
        ASSERT_EQ(false, recursiveChoice.isOptional);
        ASSERT_EQ(""_sv, recursiveChoice.optionalCondition);
        ASSERT_EQ(""_sv, recursiveChoice.constraint);
        ASSERT_EQ(false, recursiveChoice.isArray);
        ASSERT_EQ(""_sv, recursiveChoice.arrayLength);
        ASSERT_EQ(false, recursiveChoice.isPacked);
        ASSERT_EQ(false, recursiveChoice.isImplicit);

        // selector
        const zserio::FieldInfo& selectorField = fields[6];
        ASSERT_EQ("selector"_sv, selectorField.schemaName);

        checkTestEnum(selectorField.typeInfo);

        ASSERT_EQ(0, selectorField.typeArguments.size());
        ASSERT_EQ(""_sv, selectorField.alignment);
        ASSERT_EQ(""_sv, selectorField.offset);
        ASSERT_EQ(""_sv, selectorField.initializer);
        ASSERT_EQ(false, selectorField.isOptional);
        ASSERT_EQ(""_sv, selectorField.optionalCondition);
        ASSERT_EQ(""_sv, selectorField.constraint);
        ASSERT_EQ(false, selectorField.isArray);
        ASSERT_EQ(""_sv, selectorField.arrayLength);
        ASSERT_EQ(false, selectorField.isPacked);
        ASSERT_EQ(false, selectorField.isImplicit);

        // simpleChoice
        const zserio::FieldInfo& simpleChoiceField = fields[7];
        ASSERT_EQ("simpleChoice"_sv, simpleChoiceField.schemaName);

        checkSimpleChoice(simpleChoiceField.typeInfo);

        ASSERT_EQ(1, simpleChoiceField.typeArguments.size());
        ASSERT_EQ("getSelector()"_sv, simpleChoiceField.typeArguments[0]);
        ASSERT_EQ(""_sv, simpleChoiceField.alignment);
        ASSERT_EQ(""_sv, simpleChoiceField.offset);
        ASSERT_EQ(""_sv, simpleChoiceField.initializer);
        ASSERT_EQ(false, simpleChoiceField.isOptional);
        ASSERT_EQ(""_sv, simpleChoiceField.optionalCondition);
        ASSERT_EQ(""_sv, simpleChoiceField.constraint);
        ASSERT_EQ(false, simpleChoiceField.isArray);
        ASSERT_EQ(""_sv, simpleChoiceField.arrayLength);
        ASSERT_EQ(false, simpleChoiceField.isPacked);
        ASSERT_EQ(false, simpleChoiceField.isImplicit);

        // templatedStruct
        const zserio::FieldInfo& templatedStructField = fields[8];
        ASSERT_EQ("templatedStruct"_sv, templatedStructField.schemaName);

        checkTS32(templatedStructField.typeInfo);

        ASSERT_EQ(0, templatedStructField.typeArguments.size());
        ASSERT_EQ(""_sv, templatedStructField.alignment);
        ASSERT_EQ(""_sv, templatedStructField.offset);
        ASSERT_EQ(""_sv, templatedStructField.initializer);
        ASSERT_EQ(false, templatedStructField.isOptional);
        ASSERT_EQ(""_sv, templatedStructField.optionalCondition);
        ASSERT_EQ(""_sv, templatedStructField.constraint);
        ASSERT_EQ(false, templatedStructField.isArray);
        ASSERT_EQ(""_sv, templatedStructField.arrayLength);
        ASSERT_EQ(false, templatedStructField.isPacked);
        ASSERT_EQ(false, templatedStructField.isImplicit);

        // templatedParameterizedStruct
        const zserio::FieldInfo& templatedParameterizedStructField = fields[9];
        ASSERT_EQ("templatedParameterizedStruct"_sv, templatedParameterizedStructField.schemaName);

        checkTemplatedParameterizedStruct_TS32(templatedParameterizedStructField.typeInfo);

        ASSERT_EQ(1, templatedParameterizedStructField.typeArguments.size());
        ASSERT_EQ("getTemplatedStruct()"_sv, templatedParameterizedStructField.typeArguments[0]);
        ASSERT_EQ(""_sv, templatedParameterizedStructField.alignment);
        ASSERT_EQ(""_sv, templatedParameterizedStructField.offset);
        ASSERT_EQ(""_sv, templatedParameterizedStructField.initializer);
        ASSERT_EQ(false, templatedParameterizedStructField.isOptional);
        ASSERT_EQ(""_sv, templatedParameterizedStructField.optionalCondition);
        ASSERT_EQ(""_sv, templatedParameterizedStructField.constraint);
        ASSERT_EQ(false, templatedParameterizedStructField.isArray);
        ASSERT_EQ(""_sv, templatedParameterizedStructField.arrayLength);
        ASSERT_EQ(false, templatedParameterizedStructField.isPacked);
        ASSERT_EQ(false, templatedParameterizedStructField.isImplicit);

        // externData
        const zserio::FieldInfo& externDataField = fields[10];
        ASSERT_EQ("externData"_sv, externDataField.schemaName);

        ASSERT_EQ("extern"_sv, externDataField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::EXTERN, externDataField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::BIT_BUFFER, externDataField.typeInfo.getCppType());

        ASSERT_EQ(0, externDataField.typeArguments.size());
        ASSERT_EQ(""_sv, externDataField.alignment);
        ASSERT_EQ(""_sv, externDataField.offset);
        ASSERT_EQ(""_sv, externDataField.initializer);
        ASSERT_EQ(false, externDataField.isOptional);
        ASSERT_EQ(""_sv, externDataField.optionalCondition);
        ASSERT_EQ(""_sv, externDataField.constraint);
        ASSERT_EQ(false, externDataField.isArray);
        ASSERT_EQ(""_sv, externDataField.arrayLength);
        ASSERT_EQ(false, externDataField.isPacked);
        ASSERT_EQ(false, externDataField.isImplicit);

        // implicitArray
        const zserio::FieldInfo& implicitArrayField = fields[11];
        ASSERT_EQ("implicitArray"_sv, implicitArrayField.schemaName);

        ASSERT_EQ("uint32"_sv, implicitArrayField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, implicitArrayField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, implicitArrayField.typeInfo.getCppType());
        ASSERT_EQ(32, implicitArrayField.typeInfo.getBitSize());

        ASSERT_EQ(0, implicitArrayField.typeArguments.size());
        ASSERT_EQ(""_sv, implicitArrayField.alignment);
        ASSERT_EQ(""_sv, implicitArrayField.offset);
        ASSERT_EQ(""_sv, implicitArrayField.initializer);
        ASSERT_EQ(false, implicitArrayField.isOptional);
        ASSERT_EQ(""_sv, implicitArrayField.optionalCondition);
        ASSERT_EQ(""_sv, implicitArrayField.constraint);
        ASSERT_EQ(true, implicitArrayField.isArray);
        ASSERT_EQ(""_sv, implicitArrayField.arrayLength);
        ASSERT_EQ(false, implicitArrayField.isPacked);
        ASSERT_EQ(true, implicitArrayField.isImplicit);
    }

    void checkSimpleStruct(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.SimpleStruct"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRUCT, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRUCT, typeInfo.getCppType());

        ASSERT_EQ(0, typeInfo.getParameters().size());
        ASSERT_EQ(0, typeInfo.getFunctions().size());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(6, fields.size());

        // fieldU32
        const zserio::FieldInfo& fieldU32Field = fields[0];
        ASSERT_EQ("fieldU32"_sv, fieldU32Field.schemaName);

        ASSERT_EQ("uint32"_sv, fieldU32Field.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, fieldU32Field.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, fieldU32Field.typeInfo.getCppType());
        ASSERT_EQ(32, fieldU32Field.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldU32Field.typeArguments.size());
        ASSERT_EQ("8"_sv, fieldU32Field.alignment);
        ASSERT_EQ(""_sv, fieldU32Field.offset);
        ASSERT_EQ("0"_sv, fieldU32Field.initializer);
        ASSERT_EQ(false, fieldU32Field.isOptional);
        ASSERT_EQ(""_sv, fieldU32Field.optionalCondition);
        ASSERT_EQ(""_sv, fieldU32Field.constraint);
        ASSERT_EQ(false, fieldU32Field.isArray);
        ASSERT_EQ(""_sv, fieldU32Field.arrayLength);
        ASSERT_EQ(false, fieldU32Field.isPacked);
        ASSERT_EQ(false, fieldU32Field.isImplicit);

        // fieldString
        const zserio::FieldInfo& fieldStringField = fields[1];
        ASSERT_EQ("fieldString"_sv, fieldStringField.schemaName);

        ASSERT_EQ("string"_sv, fieldStringField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRING, fieldStringField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRING, fieldStringField.typeInfo.getCppType());

        ASSERT_EQ(0, fieldStringField.typeArguments.size());
        ASSERT_EQ(""_sv, fieldStringField.alignment);
        ASSERT_EQ("getFieldU32()"_sv, fieldStringField.offset);
        ASSERT_EQ("\"MyString\""_sv, fieldStringField.initializer);
        ASSERT_EQ(false, fieldStringField.isOptional);
        ASSERT_EQ(""_sv, fieldStringField.optionalCondition);
        ASSERT_EQ(""_sv, fieldStringField.constraint);
        ASSERT_EQ(false, fieldStringField.isArray);
        ASSERT_EQ(""_sv, fieldStringField.arrayLength);
        ASSERT_EQ(false, fieldStringField.isPacked);
        ASSERT_EQ(false, fieldStringField.isImplicit);

        // fieldBool
        const zserio::FieldInfo& fieldBoolField = fields[2];
        ASSERT_EQ("fieldBool"_sv, fieldBoolField.schemaName);

        ASSERT_EQ("bool"_sv, fieldBoolField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::BOOL, fieldBoolField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::BOOL, fieldBoolField.typeInfo.getCppType());
        ASSERT_EQ(1, fieldBoolField.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldBoolField.typeArguments.size());
        ASSERT_EQ(""_sv, fieldBoolField.alignment);
        ASSERT_EQ(""_sv, fieldBoolField.offset);
        ASSERT_EQ("false"_sv, fieldBoolField.initializer);
        ASSERT_EQ(false, fieldBoolField.isOptional);
        ASSERT_EQ(""_sv, fieldBoolField.optionalCondition);
        ASSERT_EQ(""_sv, fieldBoolField.constraint);
        ASSERT_EQ(false, fieldBoolField.isArray);
        ASSERT_EQ(""_sv, fieldBoolField.arrayLength);
        ASSERT_EQ(false, fieldBoolField.isPacked);
        ASSERT_EQ(false, fieldBoolField.isImplicit);

        // fieldFloat16
        const zserio::FieldInfo& fieldFloat16Field = fields[3];
        ASSERT_EQ("fieldFloat16"_sv, fieldFloat16Field.schemaName);

        ASSERT_EQ("float16"_sv, fieldFloat16Field.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::FLOAT16, fieldFloat16Field.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::FLOAT, fieldFloat16Field.typeInfo.getCppType());
        ASSERT_EQ(16, fieldFloat16Field.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldFloat16Field.typeArguments.size());
        ASSERT_EQ(""_sv, fieldFloat16Field.alignment);
        ASSERT_EQ(""_sv, fieldFloat16Field.offset);
        ASSERT_EQ("1.0f"_sv, fieldFloat16Field.initializer);
        ASSERT_EQ(false, fieldFloat16Field.isOptional);
        ASSERT_EQ(""_sv, fieldFloat16Field.optionalCondition);
        ASSERT_EQ(""_sv, fieldFloat16Field.constraint);
        ASSERT_EQ(false, fieldFloat16Field.isArray);
        ASSERT_EQ(""_sv, fieldFloat16Field.arrayLength);
        ASSERT_EQ(false, fieldFloat16Field.isPacked);
        ASSERT_EQ(false, fieldFloat16Field.isImplicit);

        // fieldFloat32
        const zserio::FieldInfo& fieldFloat32Field = fields[4];
        ASSERT_EQ("fieldFloat32"_sv, fieldFloat32Field.schemaName);

        ASSERT_EQ("float32"_sv, fieldFloat32Field.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::FLOAT32, fieldFloat32Field.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::FLOAT, fieldFloat32Field.typeInfo.getCppType());
        ASSERT_EQ(32, fieldFloat32Field.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldFloat32Field.typeArguments.size());
        ASSERT_EQ(""_sv, fieldFloat32Field.alignment);
        ASSERT_EQ(""_sv, fieldFloat32Field.offset);
        ASSERT_EQ(""_sv, fieldFloat32Field.initializer);
        ASSERT_EQ(false, fieldFloat32Field.isOptional);
        ASSERT_EQ(""_sv, fieldFloat32Field.optionalCondition);
        ASSERT_EQ(""_sv, fieldFloat32Field.constraint);
        ASSERT_EQ(false, fieldFloat32Field.isArray);
        ASSERT_EQ(""_sv, fieldFloat32Field.arrayLength);
        ASSERT_EQ(false, fieldFloat32Field.isPacked);
        ASSERT_EQ(false, fieldFloat32Field.isImplicit);

        // fieldFloat64
        const zserio::FieldInfo& fieldFloat64Field = fields[5];
        ASSERT_EQ("fieldFloat64"_sv, fieldFloat64Field.schemaName);

        ASSERT_EQ("float64"_sv, fieldFloat64Field.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::FLOAT64, fieldFloat64Field.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::DOUBLE, fieldFloat64Field.typeInfo.getCppType());
        ASSERT_EQ(64, fieldFloat64Field.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldFloat64Field.typeArguments.size());
        ASSERT_EQ(""_sv, fieldFloat64Field.alignment);
        ASSERT_EQ(""_sv, fieldFloat64Field.offset);
        ASSERT_EQ("2.0"_sv, fieldFloat64Field.initializer);
        ASSERT_EQ(false, fieldFloat64Field.isOptional);
        ASSERT_EQ(""_sv, fieldFloat64Field.optionalCondition);
        ASSERT_EQ(""_sv, fieldFloat64Field.constraint);
        ASSERT_EQ(false, fieldFloat64Field.isArray);
        ASSERT_EQ(""_sv, fieldFloat64Field.arrayLength);
        ASSERT_EQ(false, fieldFloat64Field.isPacked);
        ASSERT_EQ(false, fieldFloat64Field.isImplicit);
    }

    void checkComplexStruct(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.ComplexStruct"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRUCT, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRUCT, typeInfo.getCppType());

        ASSERT_EQ(0, typeInfo.getParameters().size());
        const zserio::Span<const zserio::FunctionInfo> functions = typeInfo.getFunctions();
        ASSERT_EQ(1, functions.size());

        const zserio::FunctionInfo& function0 = functions[0];
        ASSERT_EQ("firstArrayElement"_sv, function0.schemaName);
        ASSERT_EQ("uint32"_sv, function0.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, function0.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, function0.typeInfo.getCppType());
        ASSERT_EQ(32, function0.typeInfo.getBitSize());
        ASSERT_EQ("(getArray().size() > 0) ? getArray().at(0) : 0"_sv, function0.functionResult);

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(6, fields.size());

        // simpleStruct
        const zserio::FieldInfo& simpleStructField = fields[0];
        ASSERT_EQ("simpleStruct"_sv, simpleStructField.schemaName);

        checkSimpleStruct(simpleStructField.typeInfo);

        ASSERT_EQ(0, simpleStructField.typeArguments.size());
        ASSERT_EQ(""_sv, simpleStructField.alignment);
        ASSERT_EQ(""_sv, simpleStructField.offset);
        ASSERT_EQ(""_sv, simpleStructField.initializer);
        ASSERT_EQ(true, simpleStructField.isOptional);
        ASSERT_EQ(""_sv, simpleStructField.optionalCondition);
        ASSERT_EQ(""_sv, simpleStructField.constraint);
        ASSERT_EQ(false, simpleStructField.isArray);
        ASSERT_EQ(""_sv, simpleStructField.arrayLength);
        ASSERT_EQ(false, simpleStructField.isPacked);
        ASSERT_EQ(false, simpleStructField.isImplicit);

        // array
        const zserio::FieldInfo& arrayField = fields[1];
        ASSERT_EQ("array"_sv, arrayField.schemaName);

        ASSERT_EQ("uint32"_sv, arrayField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, arrayField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, arrayField.typeInfo.getCppType());
        ASSERT_EQ(32, arrayField.typeInfo.getBitSize());

        ASSERT_EQ(0, arrayField.typeArguments.size());
        ASSERT_EQ(""_sv, arrayField.alignment);
        ASSERT_EQ(""_sv, arrayField.offset);
        ASSERT_EQ(""_sv, arrayField.initializer);
        ASSERT_EQ(false, arrayField.isOptional);
        ASSERT_EQ(""_sv, arrayField.optionalCondition);
        ASSERT_EQ("getArray().size() > 0"_sv, arrayField.constraint);
        ASSERT_EQ(true, arrayField.isArray);
        ASSERT_EQ(""_sv, arrayField.arrayLength);
        ASSERT_EQ(false, arrayField.isPacked);
        ASSERT_EQ(false, arrayField.isImplicit);

        // arrayWithLen
        const zserio::FieldInfo& arrayWithLenField = fields[2];
        ASSERT_EQ("arrayWithLen"_sv, arrayWithLenField.schemaName);

        ASSERT_EQ("int:5"_sv, arrayWithLenField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::FIXED_SIGNED_BITFIELD, arrayWithLenField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::INT8, arrayWithLenField.typeInfo.getCppType());
        ASSERT_EQ(5, arrayWithLenField.typeInfo.getBitSize());

        ASSERT_EQ(0, arrayWithLenField.typeArguments.size());
        ASSERT_EQ(""_sv, arrayWithLenField.alignment);
        ASSERT_EQ(""_sv, arrayWithLenField.offset);
        ASSERT_EQ(""_sv, arrayWithLenField.initializer);
        ASSERT_EQ(true, arrayWithLenField.isOptional);
        ASSERT_EQ("getArray().at(0) > 0"_sv, arrayWithLenField.optionalCondition);
        ASSERT_EQ(""_sv, arrayWithLenField.constraint);
        ASSERT_EQ(true, arrayWithLenField.isArray);
        ASSERT_EQ("getArray().at(0)"_sv, arrayWithLenField.arrayLength);
        ASSERT_EQ(false, arrayWithLenField.isPacked);
        ASSERT_EQ(false, arrayWithLenField.isImplicit);

        // paramStructArray
        const zserio::FieldInfo& paramStructArrayField = fields[3];
        ASSERT_EQ("paramStructArray"_sv, paramStructArrayField.schemaName);

        checkParameterizedStruct(paramStructArrayField.typeInfo);

        ASSERT_EQ(1, paramStructArrayField.typeArguments.size());
        ASSERT_EQ("getSimpleStruct()"_sv, paramStructArrayField.typeArguments[0]);
        ASSERT_EQ(""_sv, paramStructArrayField.alignment);
        ASSERT_EQ(""_sv, paramStructArrayField.offset);
        ASSERT_EQ(""_sv, paramStructArrayField.initializer);
        ASSERT_EQ(true, paramStructArrayField.isOptional);
        ASSERT_EQ(""_sv, paramStructArrayField.optionalCondition);
        ASSERT_EQ(""_sv, paramStructArrayField.constraint);
        ASSERT_EQ(true, paramStructArrayField.isArray);
        ASSERT_EQ(""_sv, paramStructArrayField.arrayLength);
        ASSERT_EQ(false, paramStructArrayField.isPacked);
        ASSERT_EQ(false, paramStructArrayField.isImplicit);

        // dynamicBitField
        const zserio::FieldInfo& dynamicBitFieldField = fields[4];
        ASSERT_EQ("dynamicBitField"_sv, dynamicBitFieldField.schemaName);

        ASSERT_EQ("bit<>"_sv, dynamicBitFieldField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::DYNAMIC_UNSIGNED_BITFIELD, dynamicBitFieldField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT64, dynamicBitFieldField.typeInfo.getCppType());

        ASSERT_EQ(1, dynamicBitFieldField.typeArguments.size());
        ASSERT_EQ("getSimpleStruct().getFieldU32()"_sv, dynamicBitFieldField.typeArguments[0]);
        ASSERT_EQ(""_sv, dynamicBitFieldField.alignment);
        ASSERT_EQ(""_sv, dynamicBitFieldField.offset);
        ASSERT_EQ(""_sv, dynamicBitFieldField.initializer);
        ASSERT_EQ(false, dynamicBitFieldField.isOptional);
        ASSERT_EQ(""_sv, dynamicBitFieldField.optionalCondition);
        ASSERT_EQ(""_sv, dynamicBitFieldField.constraint);
        ASSERT_EQ(false, dynamicBitFieldField.isArray);
        ASSERT_EQ(""_sv, dynamicBitFieldField.arrayLength);
        ASSERT_EQ(false, dynamicBitFieldField.isPacked);
        ASSERT_EQ(false, dynamicBitFieldField.isImplicit);

        // dynamicBitFieldArray
        const zserio::FieldInfo& dynamicBitFieldArrayField = fields[5];
        ASSERT_EQ("dynamicBitFieldArray"_sv, dynamicBitFieldArrayField.schemaName);

        ASSERT_EQ("bit<>"_sv, dynamicBitFieldArrayField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::DYNAMIC_UNSIGNED_BITFIELD, dynamicBitFieldArrayField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT64, dynamicBitFieldArrayField.typeInfo.getCppType());

        ASSERT_EQ(1, dynamicBitFieldArrayField.typeArguments.size());
        ASSERT_EQ("getDynamicBitField() * 2"_sv,
                dynamicBitFieldArrayField.typeArguments[0]);
        ASSERT_EQ(""_sv, dynamicBitFieldArrayField.alignment);
        ASSERT_EQ(""_sv, dynamicBitFieldArrayField.offset);
        ASSERT_EQ(""_sv, dynamicBitFieldArrayField.initializer);
        ASSERT_EQ(false, dynamicBitFieldArrayField.isOptional);
        ASSERT_EQ(""_sv, dynamicBitFieldArrayField.optionalCondition);
        ASSERT_EQ(""_sv, dynamicBitFieldArrayField.constraint);
        ASSERT_EQ(true, dynamicBitFieldArrayField.isArray);
        ASSERT_EQ(""_sv, dynamicBitFieldArrayField.arrayLength);
        ASSERT_EQ(true, dynamicBitFieldArrayField.isPacked);
        ASSERT_EQ(false, dynamicBitFieldArrayField.isImplicit);
    }

    void checkParameterizedStruct(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.ParameterizedStruct"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRUCT, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRUCT, typeInfo.getCppType());

        const zserio::Span<const zserio::ParameterInfo> parameters = typeInfo.getParameters();
        ASSERT_EQ(1, parameters.size());

        const zserio::ParameterInfo& parameter0 = parameters[0];
        ASSERT_EQ("simple"_sv, parameter0.schemaName);
        checkSimpleStruct(parameter0.typeInfo);

        ASSERT_EQ(0, typeInfo.getFunctions().size());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(1, fields.size());

        // array
        const zserio::FieldInfo& arrayField = fields[0];
        ASSERT_EQ("array"_sv, arrayField.schemaName);

        ASSERT_EQ("uint8"_sv, arrayField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT8, arrayField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT8, arrayField.typeInfo.getCppType());
        ASSERT_EQ(8, arrayField.typeInfo.getBitSize());

        ASSERT_EQ(0, arrayField.typeArguments.size());
        ASSERT_EQ(""_sv, arrayField.alignment);
        ASSERT_EQ(""_sv, arrayField.offset);
        ASSERT_EQ(""_sv, arrayField.initializer);
        ASSERT_EQ(false, arrayField.isOptional);
        ASSERT_EQ(""_sv, arrayField.optionalCondition);
        ASSERT_EQ(""_sv, arrayField.constraint);
        ASSERT_EQ(true, arrayField.isArray);
        ASSERT_EQ("getSimple().getFieldU32()"_sv, arrayField.arrayLength);
        ASSERT_EQ(false, arrayField.isPacked);
        ASSERT_EQ(false, arrayField.isImplicit);
    }

    void checkRecursiveStruct(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.RecursiveStruct"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRUCT, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRUCT, typeInfo.getCppType());

        ASSERT_EQ(0, typeInfo.getParameters().size());
        ASSERT_EQ(0, typeInfo.getFunctions().size());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(3, fields.size());

        // fieldU32
        const zserio::FieldInfo& fieldU32Field = fields[0];
        ASSERT_EQ("fieldU32"_sv, fieldU32Field.schemaName);

        ASSERT_EQ("uint32"_sv, fieldU32Field.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, fieldU32Field.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, fieldU32Field.typeInfo.getCppType());
        ASSERT_EQ(32, fieldU32Field.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldU32Field.typeArguments.size());
        ASSERT_EQ(""_sv, fieldU32Field.alignment);
        ASSERT_EQ(""_sv, fieldU32Field.offset);
        ASSERT_EQ(""_sv, fieldU32Field.initializer);
        ASSERT_EQ(false, fieldU32Field.isOptional);
        ASSERT_EQ(""_sv, fieldU32Field.optionalCondition);
        ASSERT_EQ(""_sv, fieldU32Field.constraint);
        ASSERT_EQ(false, fieldU32Field.isArray);
        ASSERT_EQ(""_sv, fieldU32Field.arrayLength);
        ASSERT_EQ(false, fieldU32Field.isPacked);
        ASSERT_EQ(false, fieldU32Field.isImplicit);

        // fieldRecursion
        const zserio::FieldInfo& fieldRecursion = fields[1];
        ASSERT_EQ("fieldRecursion"_sv, fieldRecursion.schemaName);

        ASSERT_EQ(typeInfo.getSchemaName(), fieldRecursion.typeInfo.getSchemaName());
        ASSERT_EQ(typeInfo.getSchemaType(), fieldRecursion.typeInfo.getSchemaType());
        ASSERT_EQ(typeInfo.getCppType(), fieldRecursion.typeInfo.getCppType());
        ASSERT_EQ(&typeInfo.getFields()[0], &fieldRecursion.typeInfo.getFields()[0]);

        ASSERT_EQ(0, fieldRecursion.typeArguments.size());
        ASSERT_EQ(""_sv, fieldRecursion.alignment);
        ASSERT_EQ(""_sv, fieldRecursion.offset);
        ASSERT_EQ(""_sv, fieldRecursion.initializer);
        ASSERT_EQ(true, fieldRecursion.isOptional);
        ASSERT_EQ(""_sv, fieldRecursion.optionalCondition);
        ASSERT_EQ(""_sv, fieldRecursion.constraint);
        ASSERT_EQ(false, fieldRecursion.isArray);
        ASSERT_EQ(""_sv, fieldRecursion.arrayLength);
        ASSERT_EQ(false, fieldRecursion.isPacked);
        ASSERT_EQ(false, fieldRecursion.isImplicit);

        // arrayRecursion
        const zserio::FieldInfo& arrayRecursion = fields[2];
        ASSERT_EQ("arrayRecursion"_sv, arrayRecursion.schemaName);

        ASSERT_EQ(typeInfo.getSchemaName(), arrayRecursion.typeInfo.getSchemaName());
        ASSERT_EQ(typeInfo.getSchemaType(), arrayRecursion.typeInfo.getSchemaType());
        ASSERT_EQ(typeInfo.getCppType(), arrayRecursion.typeInfo.getCppType());
        ASSERT_EQ(&typeInfo.getFields()[0], &arrayRecursion.typeInfo.getFields()[0]);

        ASSERT_EQ(0, arrayRecursion.typeArguments.size());
        ASSERT_EQ(""_sv, arrayRecursion.alignment);
        ASSERT_EQ(""_sv, arrayRecursion.offset);
        ASSERT_EQ(""_sv, arrayRecursion.initializer);
        ASSERT_EQ(false, arrayRecursion.isOptional);
        ASSERT_EQ(""_sv, arrayRecursion.optionalCondition);
        ASSERT_EQ(""_sv, arrayRecursion.constraint);
        ASSERT_EQ(true, arrayRecursion.isArray);
        ASSERT_EQ(""_sv, arrayRecursion.arrayLength);
        ASSERT_EQ(false, arrayRecursion.isPacked);
        ASSERT_EQ(false, arrayRecursion.isImplicit);
    }

    void checkRecursiveUnion(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.RecursiveUnion"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UNION, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UNION, typeInfo.getCppType());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(2, fields.size());

        // fieldU32
        const zserio::FieldInfo& fieldU32Field = fields[0];
        ASSERT_EQ("fieldU32"_sv, fieldU32Field.schemaName);

        ASSERT_EQ("uint32"_sv, fieldU32Field.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, fieldU32Field.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, fieldU32Field.typeInfo.getCppType());
        ASSERT_EQ(32, fieldU32Field.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldU32Field.typeArguments.size());
        ASSERT_EQ(""_sv, fieldU32Field.alignment);
        ASSERT_EQ(""_sv, fieldU32Field.offset);
        ASSERT_EQ(""_sv, fieldU32Field.initializer);
        ASSERT_EQ(false, fieldU32Field.isOptional);
        ASSERT_EQ(""_sv, fieldU32Field.optionalCondition);
        ASSERT_EQ(""_sv, fieldU32Field.constraint);
        ASSERT_EQ(false, fieldU32Field.isArray);
        ASSERT_EQ(""_sv, fieldU32Field.arrayLength);
        ASSERT_EQ(false, fieldU32Field.isPacked);
        ASSERT_EQ(false, fieldU32Field.isImplicit);

        // recursive
        const zserio::FieldInfo& recursive = fields[1];
        ASSERT_EQ("recursive"_sv, recursive.schemaName);

        ASSERT_EQ(typeInfo.getSchemaName(), recursive.typeInfo.getSchemaName());
        ASSERT_EQ(typeInfo.getSchemaType(), recursive.typeInfo.getSchemaType());
        ASSERT_EQ(typeInfo.getCppType(), recursive.typeInfo.getCppType());
        ASSERT_EQ(&typeInfo.getFields()[0], &recursive.typeInfo.getFields()[0]);

        ASSERT_EQ(0, recursive.typeArguments.size());
        ASSERT_EQ(""_sv, recursive.alignment);
        ASSERT_EQ(""_sv, recursive.offset);
        ASSERT_EQ(""_sv, recursive.initializer);
        ASSERT_EQ(false, recursive.isOptional);
        ASSERT_EQ(""_sv, recursive.optionalCondition);
        ASSERT_EQ(""_sv, recursive.constraint);
        ASSERT_EQ(true, recursive.isArray);
        ASSERT_EQ(""_sv, recursive.arrayLength);
        ASSERT_EQ(false, recursive.isPacked);
        ASSERT_EQ(false, recursive.isImplicit);
    }

    void checkRecursiveChoice(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.RecursiveChoice"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::CHOICE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::CHOICE, typeInfo.getCppType());

        const zserio::Span<const zserio::ParameterInfo> parameters = typeInfo.getParameters();
        ASSERT_EQ(2, parameters.size());

        // param1
        const zserio::ParameterInfo& param1 = parameters[0];
        ASSERT_EQ("param1"_sv, param1.schemaName);
        ASSERT_EQ("bool"_sv, param1.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::BOOL, param1.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::BOOL, param1.typeInfo.getCppType());
        ASSERT_EQ(1, param1.typeInfo.getBitSize());

        // param2
        const zserio::ParameterInfo& param2 = parameters[1];
        ASSERT_EQ("param2"_sv, param2.schemaName);
        ASSERT_EQ("bool"_sv, param2.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::BOOL, param2.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::BOOL, param2.typeInfo.getCppType());
        ASSERT_EQ(1, param2.typeInfo.getBitSize());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(2, fields.size());

        // recursive
        const zserio::FieldInfo& recursive = fields[0];
        ASSERT_EQ("recursive"_sv, recursive.schemaName);

        ASSERT_EQ(typeInfo.getSchemaName(), recursive.typeInfo.getSchemaName());
        ASSERT_EQ(typeInfo.getSchemaType(), recursive.typeInfo.getSchemaType());
        ASSERT_EQ(typeInfo.getCppType(), recursive.typeInfo.getCppType());
        ASSERT_EQ(&typeInfo.getFields()[0], &recursive.typeInfo.getFields()[0]);

        ASSERT_EQ(2, recursive.typeArguments.size());
        ASSERT_EQ("getParam2()"_sv, recursive.typeArguments[0]);
        ASSERT_EQ("false"_sv, recursive.typeArguments[1]);
        ASSERT_EQ(""_sv, recursive.alignment);
        ASSERT_EQ(""_sv, recursive.offset);
        ASSERT_EQ(""_sv, recursive.initializer);
        ASSERT_EQ(false, recursive.isOptional);
        ASSERT_EQ(""_sv, recursive.optionalCondition);
        ASSERT_EQ(""_sv, recursive.constraint);
        ASSERT_EQ(true, recursive.isArray);
        ASSERT_EQ(""_sv, recursive.arrayLength);
        ASSERT_EQ(false, recursive.isPacked);
        ASSERT_EQ(false, recursive.isImplicit);

        // fieldU32
        const zserio::FieldInfo& fieldU32Field = fields[1];
        ASSERT_EQ("fieldU32"_sv, fieldU32Field.schemaName);

        ASSERT_EQ("uint32"_sv, fieldU32Field.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, fieldU32Field.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, fieldU32Field.typeInfo.getCppType());
        ASSERT_EQ(32, fieldU32Field.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldU32Field.typeArguments.size());
        ASSERT_EQ(""_sv, fieldU32Field.alignment);
        ASSERT_EQ(""_sv, fieldU32Field.offset);
        ASSERT_EQ(""_sv, fieldU32Field.initializer);
        ASSERT_EQ(false, fieldU32Field.isOptional);
        ASSERT_EQ(""_sv, fieldU32Field.optionalCondition);
        ASSERT_EQ(""_sv, fieldU32Field.constraint);
        ASSERT_EQ(false, fieldU32Field.isArray);
        ASSERT_EQ(""_sv, fieldU32Field.arrayLength);
        ASSERT_EQ(false, fieldU32Field.isPacked);
        ASSERT_EQ(false, fieldU32Field.isImplicit);
    }

    void checkTestEnum(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.TestEnum"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::ENUM, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::ENUM, typeInfo.getCppType());

        ASSERT_EQ("uint16"_sv, typeInfo.getUnderlyingType().getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT16, typeInfo.getUnderlyingType().getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT16, typeInfo.getUnderlyingType().getCppType());
        ASSERT_EQ(16, typeInfo.getUnderlyingType().getBitSize());
        ASSERT_EQ(0, typeInfo.getUnderlyingTypeArguments().size());

        const zserio::Span<const zserio::ItemInfo> items = typeInfo.getEnumItems();
        ASSERT_EQ(3, items.size());

        // One
        const zserio::ItemInfo& OneItem = items[0];
        ASSERT_EQ("One"_sv, OneItem.schemaName);
        ASSERT_EQ("UINT16_C(0)"_sv, OneItem.value);

        // TWO
        const zserio::ItemInfo& TwoItem = items[1];
        ASSERT_EQ("TWO"_sv, TwoItem.schemaName);
        ASSERT_EQ("UINT16_C(5)"_sv, TwoItem.value);

        // ItemThree
        const zserio::ItemInfo& ItemThreeItem = items[2];
        ASSERT_EQ("ItemThree"_sv, ItemThreeItem.schemaName);
        ASSERT_EQ("UINT16_C(6)"_sv, ItemThreeItem.value);
    }

    void checkSimpleChoice(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.SimpleChoice"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::CHOICE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::CHOICE, typeInfo.getCppType());

        const zserio::Span<const zserio::ParameterInfo> parameters = typeInfo.getParameters();
        ASSERT_EQ(1, parameters.size());

        const zserio::ParameterInfo& parameter0 = parameters[0];
        ASSERT_EQ("selector"_sv, parameter0.schemaName);
        checkTestEnum(parameter0.typeInfo);

        const zserio::Span<const zserio::FunctionInfo> functions = typeInfo.getFunctions();
        ASSERT_EQ(1, functions.size());

        const zserio::FunctionInfo& function0 = functions[0];
        ASSERT_EQ("fieldTwoFuncCall"_sv, function0.schemaName);
        ASSERT_EQ("uint32"_sv, function0.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, function0.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, function0.typeInfo.getCppType());
        ASSERT_EQ(32, function0.typeInfo.getBitSize());
        ASSERT_EQ("getFieldTwo().funcSimpleStructFieldU32()"_sv, function0.functionResult);

        ASSERT_EQ("getSelector()"_sv, typeInfo.getSelector());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(2, fields.size());

        // fieldTwo
        const zserio::FieldInfo& fieldTwoField = fields[0];
        ASSERT_EQ("fieldTwo"_sv, fieldTwoField.schemaName);

        checkSimpleUnion(fieldTwoField.typeInfo);

        ASSERT_EQ(0, fieldTwoField.typeArguments.size());
        ASSERT_EQ(""_sv, fieldTwoField.alignment);
        ASSERT_EQ(""_sv, fieldTwoField.offset);
        ASSERT_EQ(""_sv, fieldTwoField.initializer);
        ASSERT_EQ(false, fieldTwoField.isOptional);
        ASSERT_EQ(""_sv, fieldTwoField.optionalCondition);
        ASSERT_EQ(""_sv, fieldTwoField.constraint);
        ASSERT_EQ(false, fieldTwoField.isArray);
        ASSERT_EQ(""_sv, fieldTwoField.arrayLength);
        ASSERT_EQ(false, fieldTwoField.isPacked);
        ASSERT_EQ(false, fieldTwoField.isImplicit);

        // fieldDefault
        const zserio::FieldInfo& fieldDefaultField = fields[1];
        ASSERT_EQ("fieldDefault"_sv, fieldDefaultField.schemaName);

        ASSERT_EQ("string"_sv, fieldDefaultField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRING, fieldDefaultField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRING, fieldDefaultField.typeInfo.getCppType());

        ASSERT_EQ(0, fieldDefaultField.typeArguments.size());
        ASSERT_EQ(""_sv, fieldDefaultField.alignment);
        ASSERT_EQ(""_sv, fieldDefaultField.offset);
        ASSERT_EQ(""_sv, fieldDefaultField.initializer);
        ASSERT_EQ(false, fieldDefaultField.isOptional);
        ASSERT_EQ(""_sv, fieldDefaultField.optionalCondition);
        ASSERT_EQ(""_sv, fieldDefaultField.constraint);
        ASSERT_EQ(false, fieldDefaultField.isArray);
        ASSERT_EQ(""_sv, fieldDefaultField.arrayLength);
        ASSERT_EQ(false, fieldDefaultField.isPacked);
        ASSERT_EQ(false, fieldDefaultField.isImplicit);

        const zserio::Span<const zserio::CaseInfo> cases = typeInfo.getCases();
        ASSERT_EQ(3, cases.size());

        // case One
        const zserio::CaseInfo& case0 = cases[0];
        ASSERT_EQ(1, case0.caseExpressions.size());
        ASSERT_EQ("::with_type_info_code::TestEnum::One"_sv, case0.caseExpressions[0]);
        ASSERT_EQ(nullptr, case0.field);

        // case TWO
        const zserio::CaseInfo& case1 = cases[1];
        ASSERT_EQ(1, case1.caseExpressions.size());
        ASSERT_EQ("::with_type_info_code::TestEnum::TWO"_sv, case1.caseExpressions[0]);
        ASSERT_EQ(&fieldTwoField, case1.field);

        // default
        const zserio::CaseInfo& case2 = cases[2];
        ASSERT_EQ(0, case2.caseExpressions.size());
        ASSERT_EQ(&fieldDefaultField, case2.field);
    }

    void checkSimpleUnion(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.SimpleUnion"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UNION, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UNION, typeInfo.getCppType());

        ASSERT_EQ(0, typeInfo.getParameters().size());

        const zserio::Span<const zserio::FunctionInfo> functions = typeInfo.getFunctions();
        ASSERT_EQ(1, functions.size());

        const zserio::FunctionInfo& function0 = functions[0];
        ASSERT_EQ("simpleStructFieldU32"_sv, function0.schemaName);
        ASSERT_EQ("uint32"_sv, function0.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, function0.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, function0.typeInfo.getCppType());
        ASSERT_EQ(32, function0.typeInfo.getBitSize());
        ASSERT_EQ("getSimpleStruct().getFieldU32()"_sv, function0.functionResult);

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(2, fields.size());

        // testBitmask
        const zserio::FieldInfo& testBitmaskField = fields[0];
        ASSERT_EQ("testBitmask"_sv, testBitmaskField.schemaName);

        checkTestBitmask(testBitmaskField.typeInfo);

        ASSERT_EQ(0, testBitmaskField.typeArguments.size());
        ASSERT_EQ(""_sv, testBitmaskField.alignment);
        ASSERT_EQ(""_sv, testBitmaskField.offset);
        ASSERT_EQ(""_sv, testBitmaskField.initializer);
        ASSERT_EQ(false, testBitmaskField.isOptional);
        ASSERT_EQ(""_sv, testBitmaskField.optionalCondition);
        ASSERT_EQ(""_sv, testBitmaskField.constraint);
        ASSERT_EQ(false, testBitmaskField.isArray);
        ASSERT_EQ(""_sv, testBitmaskField.arrayLength);
        ASSERT_EQ(false, testBitmaskField.isPacked);
        ASSERT_EQ(false, testBitmaskField.isImplicit);

        // simpleStruct
        const zserio::FieldInfo& simpleStructField = fields[1];
        ASSERT_EQ("simpleStruct"_sv, simpleStructField.schemaName);

        checkSimpleStruct(simpleStructField.typeInfo);

        ASSERT_EQ(0, simpleStructField.typeArguments.size());
        ASSERT_EQ(""_sv, simpleStructField.alignment);
        ASSERT_EQ(""_sv, simpleStructField.offset);
        ASSERT_EQ(""_sv, simpleStructField.initializer);
        ASSERT_EQ(false, simpleStructField.isOptional);
        ASSERT_EQ(""_sv, simpleStructField.optionalCondition);
        ASSERT_EQ(""_sv, simpleStructField.constraint);
        ASSERT_EQ(false, simpleStructField.isArray);
        ASSERT_EQ(""_sv, simpleStructField.arrayLength);
        ASSERT_EQ(false, simpleStructField.isPacked);
        ASSERT_EQ(false, simpleStructField.isImplicit);
    }

    void checkTestBitmask(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.TestBitmask"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::BITMASK, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::BITMASK, typeInfo.getCppType());

        ASSERT_EQ("bit<>"_sv, typeInfo.getUnderlyingType().getSchemaName());
        ASSERT_EQ(zserio::SchemaType::DYNAMIC_UNSIGNED_BITFIELD, typeInfo.getUnderlyingType().getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT16, typeInfo.getUnderlyingType().getCppType());
        ASSERT_THROW(typeInfo.getUnderlyingType().getBitSize(), zserio::CppRuntimeException);
        ASSERT_EQ(1, typeInfo.getUnderlyingTypeArguments().size());
        ASSERT_EQ("10"_sv, typeInfo.getUnderlyingTypeArguments()[0]);

        const zserio::Span<const zserio::ItemInfo> values = typeInfo.getBitmaskValues();
        ASSERT_EQ(3, values.size());

        // RED
        const zserio::ItemInfo& redValue = values[0];
        ASSERT_EQ("RED"_sv, redValue.schemaName);
        ASSERT_EQ("UINT16_C(1)"_sv, redValue.value);

        // Green
        const zserio::ItemInfo& greenValue = values[1];
        ASSERT_EQ("Green"_sv, greenValue.schemaName);
        ASSERT_EQ("UINT16_C(2)"_sv, greenValue.value);

        // ColorBlue
        const zserio::ItemInfo& colorBlueValue = values[2];
        ASSERT_EQ("ColorBlue"_sv, colorBlueValue.schemaName);
        ASSERT_EQ("UINT16_C(4)"_sv, colorBlueValue.value);
    }

    void checkTS32(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.TS32"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRUCT, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRUCT, typeInfo.getCppType());

        ASSERT_EQ(0, typeInfo.getParameters().size());
        ASSERT_EQ(0, typeInfo.getFunctions().size());

        ASSERT_EQ("with_type_info_code.TemplatedStruct"_sv, typeInfo.getTemplateName());

        ASSERT_EQ(1, typeInfo.getTemplateArguments().size());

        const zserio::TemplateArgumentInfo& templateArgument0 = typeInfo.getTemplateArguments()[0];
        ASSERT_EQ("uint32"_sv, templateArgument0.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, templateArgument0.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, templateArgument0.typeInfo.getCppType());
        ASSERT_EQ(32, templateArgument0.typeInfo.getBitSize());

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(1, fields.size());

        // field
        const zserio::FieldInfo& fieldField = fields[0];
        ASSERT_EQ("field"_sv, fieldField.schemaName);

        ASSERT_EQ("uint32"_sv, fieldField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, fieldField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, fieldField.typeInfo.getCppType());
        ASSERT_EQ(32, fieldField.typeInfo.getBitSize());

        ASSERT_EQ(0, fieldField.typeArguments.size());
        ASSERT_EQ(""_sv, fieldField.alignment);
        ASSERT_EQ(""_sv, fieldField.offset);
        ASSERT_EQ(""_sv, fieldField.initializer);
        ASSERT_EQ(false, fieldField.isOptional);
        ASSERT_EQ(""_sv, fieldField.optionalCondition);
        ASSERT_EQ(""_sv, fieldField.constraint);
        ASSERT_EQ(false, fieldField.isArray);
        ASSERT_EQ(""_sv, fieldField.arrayLength);
        ASSERT_EQ(false, fieldField.isPacked);
        ASSERT_EQ(false, fieldField.isImplicit);
    }

    void checkTemplatedParameterizedStruct_TS32(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.TemplatedParameterizedStruct_TS32"_sv,
                typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRUCT, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRUCT, typeInfo.getCppType());

        ASSERT_EQ(1, typeInfo.getParameters().size());
        const zserio::ParameterInfo& parameter0 = typeInfo.getParameters()[0];
        ASSERT_EQ("param"_sv, parameter0.schemaName);
        checkTS32(parameter0.typeInfo);

        ASSERT_EQ(0, typeInfo.getFunctions().size());

        ASSERT_EQ("with_type_info_code.TemplatedParameterizedStruct"_sv, typeInfo.getTemplateName());

        ASSERT_EQ(1, typeInfo.getTemplateArguments().size());

        const zserio::TemplateArgumentInfo& templateArgument0 = typeInfo.getTemplateArguments()[0];
        checkTS32(templateArgument0.typeInfo);

        const zserio::Span<const zserio::FieldInfo> fields = typeInfo.getFields();
        ASSERT_EQ(1, fields.size());

        // array
        const zserio::FieldInfo& arrayField = fields[0];
        ASSERT_EQ("array"_sv, arrayField.schemaName);

        ASSERT_EQ("uint32"_sv, arrayField.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, arrayField.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, arrayField.typeInfo.getCppType());
        ASSERT_EQ(32, arrayField.typeInfo.getBitSize());

        ASSERT_EQ(0, arrayField.typeArguments.size());
        ASSERT_EQ(""_sv, arrayField.alignment);
        ASSERT_EQ(""_sv, arrayField.offset);
        ASSERT_EQ(""_sv, arrayField.initializer);
        ASSERT_EQ(false, arrayField.isOptional);
        ASSERT_EQ(""_sv, arrayField.optionalCondition);
        ASSERT_EQ(""_sv, arrayField.constraint);
        ASSERT_EQ(true, arrayField.isArray);
        ASSERT_EQ("getParam().getField()"_sv, arrayField.arrayLength);
        ASSERT_EQ(false, arrayField.isPacked);
        ASSERT_EQ(false, arrayField.isImplicit);
    }

    void checkTemplatedSqlTableU8(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.TemplatedSqlTableU8"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::SQL_TABLE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::SQL_TABLE, typeInfo.getCppType());

        ASSERT_EQ("PRIMARY KEY(pk)"_sv, typeInfo.getSqlConstraint());
        ASSERT_EQ(""_sv, typeInfo.getVirtualTableUsing());
        ASSERT_EQ(false, typeInfo.isWithoutRowId());

        ASSERT_EQ("with_type_info_code.TemplatedSqlTable"_sv, typeInfo.getTemplateName());
        const zserio::Span<const zserio::TemplateArgumentInfo> templateArgs = typeInfo.getTemplateArguments();
        ASSERT_EQ(1, templateArgs.size());
        const zserio::ITypeInfo& templateArg0Info = templateArgs[0].typeInfo;
        ASSERT_EQ("uint8"_sv, templateArg0Info.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT8, templateArg0Info.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT8, templateArg0Info.getCppType());
        ASSERT_EQ(8, templateArg0Info.getBitSize());

        const zserio::Span<const zserio::ColumnInfo> columns = typeInfo.getColumns();
        ASSERT_EQ(2, columns.size());

        // pk
        const zserio::ColumnInfo& pkColumn = columns[0];
        ASSERT_EQ("pk"_sv, pkColumn.schemaName);

        ASSERT_EQ("uint8"_sv, pkColumn.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT8, pkColumn.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT8, pkColumn.typeInfo.getCppType());
        ASSERT_EQ(8, pkColumn.typeInfo.getBitSize());

        ASSERT_EQ(0, pkColumn.typeArguments.size());
        ASSERT_EQ("INTEGER"_sv, pkColumn.sqlTypeName);
        ASSERT_EQ("NOT NULL"_sv, pkColumn.sqlConstraint);
        ASSERT_EQ(false, pkColumn.isVirtual);

        // withTypeInfoCode
        const zserio::ColumnInfo& withTypeInfoCodeColumn = columns[1];
        ASSERT_EQ("withTypeInfoCode"_sv, withTypeInfoCodeColumn.schemaName);
        checkWithTypeInfoCode(withTypeInfoCodeColumn.typeInfo);
        ASSERT_EQ(0, withTypeInfoCodeColumn.typeArguments.size());
        ASSERT_EQ("BLOB"_sv, withTypeInfoCodeColumn.sqlTypeName);
        ASSERT_EQ(""_sv, withTypeInfoCodeColumn.sqlConstraint);
        ASSERT_EQ(false, withTypeInfoCodeColumn.isVirtual);
    }

    void checkFts4Table(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.Fts4Table"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::SQL_TABLE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::SQL_TABLE, typeInfo.getCppType());

        ASSERT_EQ(""_sv, typeInfo.getSqlConstraint());
        ASSERT_EQ("fts4"_sv, typeInfo.getVirtualTableUsing());
        ASSERT_EQ(false, typeInfo.isWithoutRowId());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::ColumnInfo> columns = typeInfo.getColumns();
        ASSERT_EQ(2, columns.size());

        // docId
        const zserio::ColumnInfo& docIdColumn = columns[0];
        ASSERT_EQ("docId"_sv, docIdColumn.schemaName);

        ASSERT_EQ("int64"_sv, docIdColumn.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::INT64, docIdColumn.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::INT64, docIdColumn.typeInfo.getCppType());
        ASSERT_EQ(64, docIdColumn.typeInfo.getBitSize());

        ASSERT_EQ(0, docIdColumn.typeArguments.size());
        ASSERT_EQ("INTEGER"_sv, docIdColumn.sqlTypeName);
        ASSERT_EQ(""_sv, docIdColumn.sqlConstraint);
        ASSERT_EQ(true, docIdColumn.isVirtual);

        // searchTags
        const zserio::ColumnInfo& searchTagsColumn = columns[1];
        ASSERT_EQ("searchTags"_sv, searchTagsColumn.schemaName);

        ASSERT_EQ("string"_sv, searchTagsColumn.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::STRING, searchTagsColumn.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::STRING, searchTagsColumn.typeInfo.getCppType());

        ASSERT_EQ(0, searchTagsColumn.typeArguments.size());
        ASSERT_EQ("TEXT"_sv, searchTagsColumn.sqlTypeName);
        ASSERT_EQ(""_sv, searchTagsColumn.sqlConstraint);
        ASSERT_EQ(false, searchTagsColumn.isVirtual);
    }

    void checkWithoutRowIdTable(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.WithoutRowIdTable"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::SQL_TABLE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::SQL_TABLE, typeInfo.getCppType());

        ASSERT_EQ("PRIMARY KEY(pk1, pk2)"_sv, typeInfo.getSqlConstraint());
        ASSERT_EQ(""_sv, typeInfo.getVirtualTableUsing());
        ASSERT_EQ(true, typeInfo.isWithoutRowId());

        ASSERT_EQ(""_sv, typeInfo.getTemplateName());
        ASSERT_EQ(0, typeInfo.getTemplateArguments().size());

        const zserio::Span<const zserio::ColumnInfo> columns = typeInfo.getColumns();
        ASSERT_EQ(2, columns.size());

        // pk1
        const zserio::ColumnInfo& pk1Column = columns[0];
        ASSERT_EQ("pk1"_sv, pk1Column.schemaName);

        ASSERT_EQ("uint32"_sv, pk1Column.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, pk1Column.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, pk1Column.typeInfo.getCppType());
        ASSERT_EQ(32, pk1Column.typeInfo.getBitSize());

        ASSERT_EQ(0, pk1Column.typeArguments.size());
        ASSERT_EQ("INTEGER"_sv, pk1Column.sqlTypeName);
        ASSERT_EQ("NOT NULL"_sv, pk1Column.sqlConstraint);
        ASSERT_EQ(false, pk1Column.isVirtual);

        // pk2
        const zserio::ColumnInfo& pk2Column = columns[1];
        ASSERT_EQ("pk2"_sv, pk2Column.schemaName);

        ASSERT_EQ("uint32"_sv, pk2Column.typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::UINT32, pk2Column.typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::UINT32, pk2Column.typeInfo.getCppType());
        ASSERT_EQ(32, pk2Column.typeInfo.getBitSize());

        ASSERT_EQ(0, pk2Column.typeArguments.size());
        ASSERT_EQ("INTEGER"_sv, pk2Column.sqlTypeName);
        ASSERT_EQ("NOT NULL"_sv, pk2Column.sqlConstraint);
        ASSERT_EQ(false, pk2Column.isVirtual);
    }

    void checkSimplePubsub(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.SimplePubsub"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::PUBSUB, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::PUBSUB, typeInfo.getCppType());

        ASSERT_THROW(typeInfo.getTemplateName(), zserio::CppRuntimeException);
        ASSERT_THROW(typeInfo.getTemplateArguments().size(), zserio::CppRuntimeException);

        const zserio::Span<const zserio::MessageInfo> messages = typeInfo.getMessages();
        ASSERT_EQ(2, messages.size());

        // pubSimpleStruct
        const zserio::MessageInfo& pubSimpleStructMessage = messages[0];
        ASSERT_EQ("pubSimpleStruct"_sv, pubSimpleStructMessage.schemaName);
        checkSimpleStruct(pubSimpleStructMessage.typeInfo);
        ASSERT_EQ(true, pubSimpleStructMessage.isPublished);
        ASSERT_EQ(false, pubSimpleStructMessage.isSubscribed);
        ASSERT_EQ("simpleStruct"_sv, pubSimpleStructMessage.topic);

        // subSimpleStruct
        const zserio::MessageInfo& subSimpleStructMessage = messages[1];
        ASSERT_EQ("subSimpleStruct"_sv, subSimpleStructMessage.schemaName);
        checkSimpleStruct(subSimpleStructMessage.typeInfo);
        ASSERT_EQ(false, subSimpleStructMessage.isPublished);
        ASSERT_EQ(true, subSimpleStructMessage.isSubscribed);
        ASSERT_EQ("simpleStruct"_sv, subSimpleStructMessage.topic);
    }

    void checkSimpleService(const zserio::ITypeInfo& typeInfo)
    {
        ASSERT_EQ("with_type_info_code.SimpleService"_sv, typeInfo.getSchemaName());
        ASSERT_EQ(zserio::SchemaType::SERVICE, typeInfo.getSchemaType());
        ASSERT_EQ(zserio::CppType::SERVICE, typeInfo.getCppType());

        ASSERT_THROW(typeInfo.getTemplateName(), zserio::CppRuntimeException);
        ASSERT_THROW(typeInfo.getTemplateArguments().size(), zserio::CppRuntimeException);

        const zserio::Span<const zserio::MethodInfo> methods = typeInfo.getMethods();
        ASSERT_EQ(1, methods.size());

        // getSimpleStruct
        const zserio::MethodInfo& getSimpleStructMethod = methods[0];
        ASSERT_EQ("getSimpleStruct"_sv, getSimpleStructMethod.schemaName);

        checkSimpleStruct(getSimpleStructMethod.responseTypeInfo);
        checkSimpleUnion(getSimpleStructMethod.requestTypeInfo);
    }
};

TEST_F(WithTypeInfoCodeTest, checkSqlDatabase)
{
    checkSqlDatabase(SqlDatabase::typeInfo());
}

TEST_F(WithTypeInfoCodeTest, checkSimplePubsub)
{
    checkSimplePubsub(SimplePubsub::typeInfo());
}

TEST_F(WithTypeInfoCodeTest, checkSimpleService)
{
    checkSimpleService(SimpleService::typeInfo());
}

} // namespace with_type_info_code
