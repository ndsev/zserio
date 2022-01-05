package with_type_info_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import with_type_info_code.type_info.SqlDatabase;
import with_type_info_code.type_info.SimplePubsub;
import with_type_info_code.type_info.SimpleService;
import with_type_info_code.type_info.TestBitmask;

import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.typeinfo.SchemaType;
import zserio.runtime.typeinfo.JavaType;
import zserio.runtime.typeinfo.TableInfo;
import zserio.runtime.typeinfo.ColumnInfo;
import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.ParameterInfo;
import zserio.runtime.typeinfo.FunctionInfo;
import zserio.runtime.typeinfo.CaseInfo;
import zserio.runtime.typeinfo.ItemInfo;
import zserio.runtime.typeinfo.MessageInfo;
import zserio.runtime.typeinfo.MethodInfo;

import zserio.runtime.ZserioError;

public class TypeInfoTest
{
    @Test
    public void checkSqlDatabase()
    {
        checkSqlDatabase(SqlDatabase.typeInfo());
    }

    @Test
    public void checkSimplePubsub()
    {
        checkSimplePubsub(SimplePubsub.typeInfo());
    }

    @Test
    public void checkSimpleService()
    {
        checkSimpleService(SimpleService.typeInfo());
    }

    @Test
    public void checkTestBitmaskUnderlyingTypeNotFixed()
    {
        assertThrows(ZserioError.class, () -> TestBitmask.typeInfo().getUnderlyingType().getBitSize());
    }

    @Test
    public void checkSimplePubsubIsNotTemplate()
    {
        assertThrows(ZserioError.class, () -> SimplePubsub.typeInfo().getTemplateArguments());
    }

    @Test
    public void checkSimpleServiceIsNotTemplate()
    {
        assertThrows(ZserioError.class, () -> SimpleService.typeInfo().getTemplateName());
    }

    private void checkSqlDatabase(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.SqlDatabase", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_DATABASE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_DATABASE, typeInfo.getJavaType());

        final List<TableInfo> tables = typeInfo.getTables();
        assertEquals(5, tables.size());

        // sqlTable
        final TableInfo sqlTable = tables.get(0);
        assertEquals("sqlTable", sqlTable.getSchemaName());
        checkSqlTable(sqlTable.getTypeInfo());

        // templatedSqlTableU32
        final TableInfo templatedSqlTableU32 = tables.get(1);
        assertEquals("templatedSqlTableU32", templatedSqlTableU32.getSchemaName());
        checkTemplatedSqlTable_uint32(templatedSqlTableU32.getTypeInfo());

        // templatedSqlTableU8
        final TableInfo templatedSqlTableU8 = tables.get(2);
        assertEquals("templatedSqlTableU8", templatedSqlTableU8.getSchemaName());
        checkTemplatedSqlTableU8(templatedSqlTableU8.getTypeInfo());

        // fts4Table
        final TableInfo fts4Table = tables.get(3);
        assertEquals("fts4Table", fts4Table.getSchemaName());
        checkFts4Table(fts4Table.getTypeInfo());

        // withoutRowIdTable
        final TableInfo withoutRowIdTable = tables.get(4);
        assertEquals("withoutRowIdTable", withoutRowIdTable.getSchemaName());
        checkWithoutRowIdTable(withoutRowIdTable.getTypeInfo());
    }

    private void checkSqlTable(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.SqlTable", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());

        assertEquals("", typeInfo.getSqlConstraint());
        assertEquals("", typeInfo.getVirtualTableUsing());
        assertFalse(typeInfo.isWithoutRowId());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<ColumnInfo> columns = typeInfo.getColumns();
        assertEquals(2, columns.size());

        // pk
        final ColumnInfo pkColumn = columns.get(0);
        assertEquals("pk", pkColumn.getSchemaName());

        assertEquals("uint32", pkColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, pkColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, pkColumn.getTypeInfo().getJavaType());
        assertEquals(32, pkColumn.getTypeInfo().getBitSize());

        assertEquals(0, pkColumn.getTypeArguments().size());
        assertEquals("INTEGER", pkColumn.getSqlTypeName());
        assertEquals("PRIMARY KEY NOT NULL", pkColumn.getSqlConstraint());
        assertFalse(pkColumn.isVirtual());

        // text
        final ColumnInfo textColumn = columns.get(1);
        assertEquals("text", textColumn.getSchemaName());

        assertEquals("string", textColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.STRING, textColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.STRING, textColumn.getTypeInfo().getJavaType());

        assertEquals(0, textColumn.getTypeArguments().size());
        assertEquals("TEXT", textColumn.getSqlTypeName());
        assertEquals("", textColumn.getSqlConstraint());
        assertFalse(textColumn.isVirtual());
    }

    private void checkTemplatedSqlTable_uint32(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.TemplatedSqlTable_uint32", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());

        assertEquals("PRIMARY KEY(pk)", typeInfo.getSqlConstraint());
        assertEquals("", typeInfo.getVirtualTableUsing());
        assertFalse(typeInfo.isWithoutRowId());

        assertEquals("with_type_info_code.type_info.TemplatedSqlTable", typeInfo.getTemplateName());
        final List<TypeInfo> templateArgs = typeInfo.getTemplateArguments();
        assertEquals(1, templateArgs.size());
        final TypeInfo templateArg0Info = templateArgs.get(0);
        assertEquals("uint32", templateArg0Info.getSchemaName());
        assertEquals(SchemaType.UINT32, templateArg0Info.getSchemaType());
        assertEquals(JavaType.LONG, templateArg0Info.getJavaType());
        assertEquals(32, templateArg0Info.getBitSize());

        final List<ColumnInfo> columns = typeInfo.getColumns();
        assertEquals(2, columns.size());

        // pk
        final ColumnInfo pkColumn = columns.get(0);
        assertEquals("pk", pkColumn.getSchemaName());

        assertEquals("uint32", pkColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, pkColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, pkColumn.getTypeInfo().getJavaType());
        assertEquals(32, pkColumn.getTypeInfo().getBitSize());

        assertEquals(0, pkColumn.getTypeArguments().size());
        assertEquals("INTEGER", pkColumn.getSqlTypeName());
        assertEquals("NOT NULL", pkColumn.getSqlConstraint());
        assertFalse(pkColumn.isVirtual());

        // withTypeInfoCode
        final ColumnInfo withTypeInfoCodeColumn = columns.get(1);
        assertEquals("withTypeInfoCode", withTypeInfoCodeColumn.getSchemaName());
        checkWithTypeInfoCode(withTypeInfoCodeColumn.getTypeInfo());
        assertEquals(0, withTypeInfoCodeColumn.getTypeArguments().size());
        assertEquals("BLOB", withTypeInfoCodeColumn.getSqlTypeName());
        assertEquals("", withTypeInfoCodeColumn.getSqlConstraint());
        assertFalse(withTypeInfoCodeColumn.isVirtual());
    }

    private void checkTemplatedSqlTableU8(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.TemplatedSqlTableU8", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());

        assertEquals("PRIMARY KEY(pk)", typeInfo.getSqlConstraint());
        assertEquals("", typeInfo.getVirtualTableUsing());
        assertFalse(typeInfo.isWithoutRowId());

        assertEquals("with_type_info_code.type_info.TemplatedSqlTable", typeInfo.getTemplateName());
        final List<TypeInfo> templateArgs = typeInfo.getTemplateArguments();
        assertEquals(1, templateArgs.size());
        final TypeInfo templateArg0Info = templateArgs.get(0);
        assertEquals("uint8", templateArg0Info.getSchemaName());
        assertEquals(SchemaType.UINT8, templateArg0Info.getSchemaType());
        assertEquals(JavaType.SHORT, templateArg0Info.getJavaType());
        assertEquals(8, templateArg0Info.getBitSize());

        final List<ColumnInfo> columns = typeInfo.getColumns();
        assertEquals(2, columns.size());

        // pk
        final ColumnInfo pkColumn = columns.get(0);
        assertEquals("pk", pkColumn.getSchemaName());

        assertEquals("uint8", pkColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT8, pkColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.SHORT, pkColumn.getTypeInfo().getJavaType());
        assertEquals(8, pkColumn.getTypeInfo().getBitSize());

        assertEquals(0, pkColumn.getTypeArguments().size());
        assertEquals("INTEGER", pkColumn.getSqlTypeName());
        assertEquals("NOT NULL", pkColumn.getSqlConstraint());
        assertFalse(pkColumn.isVirtual());

        // withTypeInfoCode
        final ColumnInfo withTypeInfoCodeColumn = columns.get(1);
        assertEquals("withTypeInfoCode", withTypeInfoCodeColumn.getSchemaName());
        checkWithTypeInfoCode(withTypeInfoCodeColumn.getTypeInfo());
        assertEquals(0, withTypeInfoCodeColumn.getTypeArguments().size());
        assertEquals("BLOB", withTypeInfoCodeColumn.getSqlTypeName());
        assertEquals("", withTypeInfoCodeColumn.getSqlConstraint());
        assertFalse(withTypeInfoCodeColumn.isVirtual());
    }

    private void checkFts4Table(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.Fts4Table", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());

        assertEquals("", typeInfo.getSqlConstraint());
        assertEquals("fts4", typeInfo.getVirtualTableUsing());
        assertFalse(typeInfo.isWithoutRowId());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<ColumnInfo> columns = typeInfo.getColumns();
        assertEquals(2, columns.size());

        // docId
        final ColumnInfo docIdColumn = columns.get(0);
        assertEquals("docId", docIdColumn.getSchemaName());

        assertEquals("int64", docIdColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.INT64, docIdColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, docIdColumn.getTypeInfo().getJavaType());
        assertEquals(64, docIdColumn.getTypeInfo().getBitSize());

        assertEquals(0, docIdColumn.getTypeArguments().size());
        assertEquals("INTEGER", docIdColumn.getSqlTypeName());
        assertEquals("", docIdColumn.getSqlConstraint());
        assertTrue(docIdColumn.isVirtual());

        // searchTags
        final ColumnInfo searchTagsColumn = columns.get(1);
        assertEquals("searchTags", searchTagsColumn.getSchemaName());

        assertEquals("string", searchTagsColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.STRING, searchTagsColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.STRING, searchTagsColumn.getTypeInfo().getJavaType());

        assertEquals(0, searchTagsColumn.getTypeArguments().size());
        assertEquals("TEXT", searchTagsColumn.getSqlTypeName());
        assertEquals("", searchTagsColumn.getSqlConstraint());
        assertFalse(searchTagsColumn.isVirtual());
    }

    private void checkWithoutRowIdTable(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.WithoutRowIdTable", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());

        assertEquals("PRIMARY KEY(pk1, pk2)", typeInfo.getSqlConstraint());
        assertEquals("", typeInfo.getVirtualTableUsing());
        assertTrue(typeInfo.isWithoutRowId());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<ColumnInfo> columns = typeInfo.getColumns();
        assertEquals(2, columns.size());

        // pk1
        final ColumnInfo pk1Column = columns.get(0);
        assertEquals("pk1", pk1Column.getSchemaName());

        assertEquals("uint32", pk1Column.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, pk1Column.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, pk1Column.getTypeInfo().getJavaType());
        assertEquals(32, pk1Column.getTypeInfo().getBitSize());

        assertEquals(0, pk1Column.getTypeArguments().size());
        assertEquals("INTEGER", pk1Column.getSqlTypeName());
        assertEquals("NOT NULL", pk1Column.getSqlConstraint());
        assertFalse(pk1Column.isVirtual());

        // pk2
        final ColumnInfo pk2Column = columns.get(1);
        assertEquals("pk2", pk2Column.getSchemaName());

        assertEquals("uint32", pk2Column.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, pk2Column.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, pk2Column.getTypeInfo().getJavaType());
        assertEquals(32, pk2Column.getTypeInfo().getBitSize());

        assertEquals(0, pk2Column.getTypeArguments().size());
        assertEquals("INTEGER", pk2Column.getSqlTypeName());
        assertEquals("NOT NULL", pk2Column.getSqlConstraint());
        assertFalse(pk2Column.isVirtual());
    }

    private void checkWithTypeInfoCode(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.WithTypeInfoCode", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(12, fields.size());

        // simpleStruct
        final FieldInfo simpleStructField = fields.get(0);
        assertEquals("simpleStruct", simpleStructField.getSchemaName());

        checkSimpleStruct(simpleStructField.getTypeInfo());

        assertEquals(0, simpleStructField.getTypeArguments().size());
        assertEquals("", simpleStructField.getAlignment());
        assertEquals("", simpleStructField.getOffset());
        assertEquals("", simpleStructField.getInitializer());
        assertFalse(simpleStructField.isOptional());
        assertEquals("", simpleStructField.getOptionalCondition());
        assertEquals("", simpleStructField.getConstraint());
        assertFalse(simpleStructField.isArray());
        assertEquals("", simpleStructField.getArrayLength());
        assertFalse(simpleStructField.isPacked());
        assertFalse(simpleStructField.isImplicit());

        // complexStruct
        final FieldInfo complexStructField = fields.get(1);
        assertEquals("complexStruct", complexStructField.getSchemaName());

        checkComplexStruct(complexStructField.getTypeInfo());

        assertEquals(0, complexStructField.getTypeArguments().size());
        assertEquals("", complexStructField.getAlignment());
        assertEquals("", complexStructField.getOffset());
        assertEquals("", complexStructField.getInitializer());
        assertFalse(complexStructField.isOptional());
        assertEquals("", complexStructField.getOptionalCondition());
        assertEquals("", complexStructField.getConstraint());
        assertFalse(complexStructField.isArray());
        assertEquals("", complexStructField.getArrayLength());
        assertFalse(complexStructField.isPacked());
        assertFalse(complexStructField.isImplicit());

        // parameterizedStruct
        final FieldInfo parameterizedStructField = fields.get(2);
        assertEquals("parameterizedStruct", parameterizedStructField.getSchemaName());

        checkParameterizedStruct(parameterizedStructField.getTypeInfo());

        assertEquals(1, parameterizedStructField.getTypeArguments().size());
        assertEquals("getSimpleStruct()", parameterizedStructField.getTypeArguments().get(0));
        assertEquals("", parameterizedStructField.getAlignment());
        assertEquals("", parameterizedStructField.getOffset());
        assertEquals("", parameterizedStructField.getInitializer());
        assertFalse(parameterizedStructField.isOptional());
        assertEquals("", parameterizedStructField.getOptionalCondition());
        assertEquals("", parameterizedStructField.getConstraint());
        assertFalse(parameterizedStructField.isArray());
        assertEquals("", parameterizedStructField.getArrayLength());
        assertFalse(parameterizedStructField.isPacked());
        assertFalse(parameterizedStructField.isImplicit());

        // recursiveStruct
        final FieldInfo recursiveStructField = fields.get(3);
        assertEquals("recursiveStruct", recursiveStructField.getSchemaName());

        checkRecursiveStruct(recursiveStructField.getTypeInfo());

        assertEquals(0, recursiveStructField.getTypeArguments().size());
        assertEquals("", recursiveStructField.getAlignment());
        assertEquals("", recursiveStructField.getOffset());
        assertEquals("", recursiveStructField.getInitializer());
        assertFalse(recursiveStructField.isOptional());
        assertEquals("", recursiveStructField.getOptionalCondition());
        assertEquals("", recursiveStructField.getConstraint());
        assertFalse(recursiveStructField.isArray());
        assertEquals("", recursiveStructField.getArrayLength());
        assertFalse(recursiveStructField.isPacked());
        assertFalse(recursiveStructField.isImplicit());

        // recursiveUnion
        final FieldInfo recursiveUnion = fields.get(4);
        assertEquals("recursiveUnion", recursiveUnion.getSchemaName());

        checkRecursiveUnion(recursiveUnion.getTypeInfo());

        assertEquals(0, recursiveUnion.getTypeArguments().size());
        assertEquals("", recursiveUnion.getAlignment());
        assertEquals("", recursiveUnion.getOffset());
        assertEquals("", recursiveUnion.getInitializer());
        assertFalse(recursiveUnion.isOptional());
        assertEquals("", recursiveUnion.getOptionalCondition());
        assertEquals("", recursiveUnion.getConstraint());
        assertFalse(recursiveUnion.isArray());
        assertEquals("", recursiveUnion.getArrayLength());
        assertFalse(recursiveUnion.isPacked());
        assertFalse(recursiveUnion.isImplicit());

        // recursiveChoice
        final FieldInfo recursiveChoice = fields.get(5);
        assertEquals("recursiveChoice", recursiveChoice.getSchemaName());

        checkRecursiveChoice(recursiveChoice.getTypeInfo());

        assertEquals(2, recursiveChoice.getTypeArguments().size());
        assertEquals("true", recursiveChoice.getTypeArguments().get(0));
        assertEquals("false", recursiveChoice.getTypeArguments().get(1));
        assertEquals("", recursiveChoice.getAlignment());
        assertEquals("", recursiveChoice.getOffset());
        assertEquals("", recursiveChoice.getInitializer());
        assertFalse(recursiveChoice.isOptional());
        assertEquals("", recursiveChoice.getOptionalCondition());
        assertEquals("", recursiveChoice.getConstraint());
        assertFalse(recursiveChoice.isArray());
        assertEquals("", recursiveChoice.getArrayLength());
        assertFalse(recursiveChoice.isPacked());
        assertFalse(recursiveChoice.isImplicit());

        // selector
        final FieldInfo selectorField = fields.get(6);
        assertEquals("selector", selectorField.getSchemaName());

        checkTestEnum(selectorField.getTypeInfo());

        assertEquals(0, selectorField.getTypeArguments().size());
        assertEquals("", selectorField.getAlignment());
        assertEquals("", selectorField.getOffset());
        assertEquals("", selectorField.getInitializer());
        assertFalse(selectorField.isOptional());
        assertEquals("", selectorField.getOptionalCondition());
        assertEquals("", selectorField.getConstraint());
        assertFalse(selectorField.isArray());
        assertEquals("", selectorField.getArrayLength());
        assertFalse(selectorField.isPacked());
        assertFalse(selectorField.isImplicit());

        // simpleChoice
        final FieldInfo simpleChoiceField = fields.get(7);
        assertEquals("simpleChoice", simpleChoiceField.getSchemaName());

        checkSimpleChoice(simpleChoiceField.getTypeInfo());

        assertEquals(1, simpleChoiceField.getTypeArguments().size());
        assertEquals("getSelector()", simpleChoiceField.getTypeArguments().get(0));
        assertEquals("", simpleChoiceField.getAlignment());
        assertEquals("", simpleChoiceField.getOffset());
        assertEquals("", simpleChoiceField.getInitializer());
        assertFalse(simpleChoiceField.isOptional());
        assertEquals("", simpleChoiceField.getOptionalCondition());
        assertEquals("", simpleChoiceField.getConstraint());
        assertFalse(simpleChoiceField.isArray());
        assertEquals("", simpleChoiceField.getArrayLength());
        assertFalse(simpleChoiceField.isPacked());
        assertFalse(simpleChoiceField.isImplicit());

        // templatedStruct
        final FieldInfo templatedStructField = fields.get(8);
        assertEquals("templatedStruct", templatedStructField.getSchemaName());

        checkTS32(templatedStructField.getTypeInfo());

        assertEquals(0, templatedStructField.getTypeArguments().size());
        assertEquals("", templatedStructField.getAlignment());
        assertEquals("", templatedStructField.getOffset());
        assertEquals("", templatedStructField.getInitializer());
        assertFalse(templatedStructField.isOptional());
        assertEquals("", templatedStructField.getOptionalCondition());
        assertEquals("", templatedStructField.getConstraint());
        assertFalse(templatedStructField.isArray());
        assertEquals("", templatedStructField.getArrayLength());
        assertFalse(templatedStructField.isPacked());
        assertFalse(templatedStructField.isImplicit());

        // templatedParameterizedStruct
        final FieldInfo templatedParameterizedStructField = fields.get(9);
        assertEquals("templatedParameterizedStruct", templatedParameterizedStructField.getSchemaName());

        checkTemplatedParameterizedStruct_TS32(templatedParameterizedStructField.getTypeInfo());

        assertEquals(1, templatedParameterizedStructField.getTypeArguments().size());
        assertEquals("getTemplatedStruct()", templatedParameterizedStructField.getTypeArguments().get(0));
        assertEquals("", templatedParameterizedStructField.getAlignment());
        assertEquals("", templatedParameterizedStructField.getOffset());
        assertEquals("", templatedParameterizedStructField.getInitializer());
        assertFalse(templatedParameterizedStructField.isOptional());
        assertEquals("", templatedParameterizedStructField.getOptionalCondition());
        assertEquals("", templatedParameterizedStructField.getConstraint());
        assertFalse(templatedParameterizedStructField.isArray());
        assertEquals("", templatedParameterizedStructField.getArrayLength());
        assertFalse(templatedParameterizedStructField.isPacked());
        assertFalse(templatedParameterizedStructField.isImplicit());

        // externData
        final FieldInfo externDataField = fields.get(10);
        assertEquals("externData", externDataField.getSchemaName());

        assertEquals("extern", externDataField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.EXTERN, externDataField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIT_BUFFER, externDataField.getTypeInfo().getJavaType());

        assertEquals(0, externDataField.getTypeArguments().size());
        assertEquals("", externDataField.getAlignment());
        assertEquals("", externDataField.getOffset());
        assertEquals("", externDataField.getInitializer());
        assertFalse(externDataField.isOptional());
        assertEquals("", externDataField.getOptionalCondition());
        assertEquals("", externDataField.getConstraint());
        assertFalse(externDataField.isArray());
        assertEquals("", externDataField.getArrayLength());
        assertFalse(externDataField.isPacked());
        assertFalse(externDataField.isImplicit());

        // implicitArray
        final FieldInfo implicitArrayField = fields.get(11);
        assertEquals("implicitArray", implicitArrayField.getSchemaName());

        assertEquals("uint32", implicitArrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, implicitArrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, implicitArrayField.getTypeInfo().getJavaType());
        assertEquals(32, implicitArrayField.getTypeInfo().getBitSize());

        assertEquals(0, implicitArrayField.getTypeArguments().size());
        assertEquals("", implicitArrayField.getAlignment());
        assertEquals("", implicitArrayField.getOffset());
        assertEquals("", implicitArrayField.getInitializer());
        assertFalse(implicitArrayField.isOptional());
        assertEquals("", implicitArrayField.getOptionalCondition());
        assertEquals("", implicitArrayField.getConstraint());
        assertTrue(implicitArrayField.isArray());
        assertEquals("", implicitArrayField.getArrayLength());
        assertFalse(implicitArrayField.isPacked());
        assertTrue(implicitArrayField.isImplicit());
    }

    private void checkSimpleStruct(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.SimpleStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(6, fields.size());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(0);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals("8", fieldU32Field.getAlignment());
        assertEquals("", fieldU32Field.getOffset());
        assertEquals("0", fieldU32Field.getInitializer());
        assertFalse(fieldU32Field.isOptional());
        assertEquals("", fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals("", fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());

        // fieldString
        final FieldInfo fieldStringField = fields.get(1);
        assertEquals("fieldString", fieldStringField.getSchemaName());

        assertEquals("string", fieldStringField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.STRING, fieldStringField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.STRING, fieldStringField.getTypeInfo().getJavaType());

        assertEquals(0, fieldStringField.getTypeArguments().size());
        assertEquals("", fieldStringField.getAlignment());
        assertEquals("getFieldU32()", fieldStringField.getOffset());
        assertEquals("\"My\" + \"String\"", fieldStringField.getInitializer());
        assertFalse(fieldStringField.isOptional());
        assertEquals("", fieldStringField.getOptionalCondition());
        assertEquals("", fieldStringField.getConstraint());
        assertFalse(fieldStringField.isArray());
        assertEquals("", fieldStringField.getArrayLength());
        assertFalse(fieldStringField.isPacked());
        assertFalse(fieldStringField.isImplicit());

        // fieldBool
        final FieldInfo fieldBoolField = fields.get(2);
        assertEquals("fieldBool", fieldBoolField.getSchemaName());

        assertEquals("bool", fieldBoolField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.BOOL, fieldBoolField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BOOLEAN, fieldBoolField.getTypeInfo().getJavaType());
        assertEquals(1, fieldBoolField.getTypeInfo().getBitSize());

        assertEquals(0, fieldBoolField.getTypeArguments().size());
        assertEquals("", fieldBoolField.getAlignment());
        assertEquals("", fieldBoolField.getOffset());
        assertEquals("false", fieldBoolField.getInitializer());
        assertFalse(fieldBoolField.isOptional());
        assertEquals("", fieldBoolField.getOptionalCondition());
        assertEquals("", fieldBoolField.getConstraint());
        assertFalse(fieldBoolField.isArray());
        assertEquals("", fieldBoolField.getArrayLength());
        assertFalse(fieldBoolField.isPacked());
        assertFalse(fieldBoolField.isImplicit());

        // fieldFloat16
        final FieldInfo fieldFloat16Field = fields.get(3);
        assertEquals("fieldFloat16", fieldFloat16Field.getSchemaName());

        assertEquals("float16", fieldFloat16Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FLOAT16, fieldFloat16Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.FLOAT, fieldFloat16Field.getTypeInfo().getJavaType());
        assertEquals(16, fieldFloat16Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldFloat16Field.getTypeArguments().size());
        assertEquals("", fieldFloat16Field.getAlignment());
        assertEquals("", fieldFloat16Field.getOffset());
        assertEquals("1.0f", fieldFloat16Field.getInitializer());
        assertFalse(fieldFloat16Field.isOptional());
        assertEquals("", fieldFloat16Field.getOptionalCondition());
        assertEquals("", fieldFloat16Field.getConstraint());
        assertFalse(fieldFloat16Field.isArray());
        assertEquals("", fieldFloat16Field.getArrayLength());
        assertFalse(fieldFloat16Field.isPacked());
        assertFalse(fieldFloat16Field.isImplicit());

        // fieldFloat32
        final FieldInfo fieldFloat32Field = fields.get(4);
        assertEquals("fieldFloat32", fieldFloat32Field.getSchemaName());

        assertEquals("float32", fieldFloat32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FLOAT32, fieldFloat32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.FLOAT, fieldFloat32Field.getTypeInfo().getJavaType());
        assertEquals(32, fieldFloat32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldFloat32Field.getTypeArguments().size());
        assertEquals("", fieldFloat32Field.getAlignment());
        assertEquals("", fieldFloat32Field.getOffset());
        assertEquals("", fieldFloat32Field.getInitializer());
        assertFalse(fieldFloat32Field.isOptional());
        assertEquals("", fieldFloat32Field.getOptionalCondition());
        assertEquals("", fieldFloat32Field.getConstraint());
        assertFalse(fieldFloat32Field.isArray());
        assertEquals("", fieldFloat32Field.getArrayLength());
        assertFalse(fieldFloat32Field.isPacked());
        assertFalse(fieldFloat32Field.isImplicit());

        // fieldFloat64
        final FieldInfo fieldFloat64Field = fields.get(5);
        assertEquals("fieldFloat64", fieldFloat64Field.getSchemaName());

        assertEquals("float64", fieldFloat64Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FLOAT64, fieldFloat64Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.DOUBLE, fieldFloat64Field.getTypeInfo().getJavaType());
        assertEquals(64, fieldFloat64Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldFloat64Field.getTypeArguments().size());
        assertEquals("", fieldFloat64Field.getAlignment());
        assertEquals("", fieldFloat64Field.getOffset());
        assertEquals("2.0", fieldFloat64Field.getInitializer());
        assertFalse(fieldFloat64Field.isOptional());
        assertEquals("", fieldFloat64Field.getOptionalCondition());
        assertEquals("", fieldFloat64Field.getConstraint());
        assertFalse(fieldFloat64Field.isArray());
        assertEquals("", fieldFloat64Field.getArrayLength());
        assertFalse(fieldFloat64Field.isPacked());
        assertFalse(fieldFloat64Field.isImplicit());
    }

    private void checkComplexStruct(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.ComplexStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());

        assertEquals(0, typeInfo.getParameters().size());
        final List<FunctionInfo> functions = typeInfo.getFunctions();
        assertEquals(1, functions.size());

        final FunctionInfo function0 = functions.get(0);
        assertEquals("firstArrayElement", function0.getSchemaName());
        assertEquals("uint32", function0.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, function0.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, function0.getTypeInfo().getJavaType());
        assertEquals(32, function0.getTypeInfo().getBitSize());
        assertEquals("(getArray().length > 0) ? getArray()[0] : 0", function0.getFunctionResult());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(6, fields.size());

        // simpleStruct
        final FieldInfo simpleStructField = fields.get(0);
        assertEquals("simpleStruct", simpleStructField.getSchemaName());

        checkSimpleStruct(simpleStructField.getTypeInfo());

        assertEquals(0, simpleStructField.getTypeArguments().size());
        assertEquals("", simpleStructField.getAlignment());
        assertEquals("", simpleStructField.getOffset());
        assertEquals("", simpleStructField.getInitializer());
        assertTrue(simpleStructField.isOptional());
        assertEquals("", simpleStructField.getOptionalCondition());
        assertEquals("", simpleStructField.getConstraint());
        assertFalse(simpleStructField.isArray());
        assertEquals("", simpleStructField.getArrayLength());
        assertFalse(simpleStructField.isPacked());
        assertFalse(simpleStructField.isImplicit());

        // array
        final FieldInfo arrayField = fields.get(1);
        assertEquals("array", arrayField.getSchemaName());

        assertEquals("uint32", arrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, arrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, arrayField.getTypeInfo().getJavaType());
        assertEquals(32, arrayField.getTypeInfo().getBitSize());

        assertEquals(0, arrayField.getTypeArguments().size());
        assertEquals("", arrayField.getAlignment());
        assertEquals("", arrayField.getOffset());
        assertEquals("", arrayField.getInitializer());
        assertFalse(arrayField.isOptional());
        assertEquals("", arrayField.getOptionalCondition());
        assertEquals("getArray().length > 0", arrayField.getConstraint());
        assertTrue(arrayField.isArray());
        assertEquals("", arrayField.getArrayLength());
        assertFalse(arrayField.isPacked());
        assertFalse(arrayField.isImplicit());

        // arrayWithLen
        final FieldInfo arrayWithLenField = fields.get(2);
        assertEquals("arrayWithLen", arrayWithLenField.getSchemaName());

        assertEquals("int:5", arrayWithLenField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FIXED_SIGNED_BITFIELD, arrayWithLenField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BYTE, arrayWithLenField.getTypeInfo().getJavaType());
        assertEquals(5, arrayWithLenField.getTypeInfo().getBitSize());

        assertEquals(0, arrayWithLenField.getTypeArguments().size());
        assertEquals("", arrayWithLenField.getAlignment());
        assertEquals("", arrayWithLenField.getOffset());
        assertEquals("", arrayWithLenField.getInitializer());
        assertTrue(arrayWithLenField.isOptional());
        assertEquals("getArray()[0] > 0", arrayWithLenField.getOptionalCondition());
        assertEquals("", arrayWithLenField.getConstraint());
        assertTrue(arrayWithLenField.isArray());
        assertEquals("getArray()[0]", arrayWithLenField.getArrayLength());
        assertFalse(arrayWithLenField.isPacked());
        assertFalse(arrayWithLenField.isImplicit());

        // paramStructArray
        final FieldInfo paramStructArrayField = fields.get(3);
        assertEquals("paramStructArray", paramStructArrayField.getSchemaName());

        checkParameterizedStruct(paramStructArrayField.getTypeInfo());

        assertEquals(1, paramStructArrayField.getTypeArguments().size());
        assertEquals("getSimpleStruct()", paramStructArrayField.getTypeArguments().get(0));
        assertEquals("", paramStructArrayField.getAlignment());
        assertEquals("", paramStructArrayField.getOffset());
        assertEquals("", paramStructArrayField.getInitializer());
        assertTrue(paramStructArrayField.isOptional());
        assertEquals("", paramStructArrayField.getOptionalCondition());
        assertEquals("", paramStructArrayField.getConstraint());
        assertTrue(paramStructArrayField.isArray());
        assertEquals("", paramStructArrayField.getArrayLength());
        assertFalse(paramStructArrayField.isPacked());
        assertFalse(paramStructArrayField.isImplicit());

        // dynamicBitField
        final FieldInfo dynamicBitFieldField = fields.get(4);
        assertEquals("dynamicBitField", dynamicBitFieldField.getSchemaName());

        assertEquals("bit<>", dynamicBitFieldField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.DYNAMIC_UNSIGNED_BITFIELD, dynamicBitFieldField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIG_INTEGER, dynamicBitFieldField.getTypeInfo().getJavaType());

        assertEquals(1, dynamicBitFieldField.getTypeArguments().size());
        assertEquals("getSimpleStruct().getFieldU32()", dynamicBitFieldField.getTypeArguments().get(0));
        assertEquals("", dynamicBitFieldField.getAlignment());
        assertEquals("", dynamicBitFieldField.getOffset());
        assertEquals("", dynamicBitFieldField.getInitializer());
        assertFalse(dynamicBitFieldField.isOptional());
        assertEquals("", dynamicBitFieldField.getOptionalCondition());
        assertEquals("", dynamicBitFieldField.getConstraint());
        assertFalse(dynamicBitFieldField.isArray());
        assertEquals("", dynamicBitFieldField.getArrayLength());
        assertFalse(dynamicBitFieldField.isPacked());
        assertFalse(dynamicBitFieldField.isImplicit());

        // dynamicBitFieldArray
        final FieldInfo dynamicBitFieldArrayField = fields.get(5);
        assertEquals("dynamicBitFieldArray", dynamicBitFieldArrayField.getSchemaName());

        assertEquals("bit<>", dynamicBitFieldArrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.DYNAMIC_UNSIGNED_BITFIELD, dynamicBitFieldArrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIG_INTEGER, dynamicBitFieldArrayField.getTypeInfo().getJavaType());

        assertEquals(1, dynamicBitFieldArrayField.getTypeArguments().size());
        assertEquals("(getDynamicBitField().multiply(new java.math.BigInteger(\"2\"))).longValue()",
                dynamicBitFieldArrayField.getTypeArguments().get(0));
        assertEquals("", dynamicBitFieldArrayField.getAlignment());
        assertEquals("", dynamicBitFieldArrayField.getOffset());
        assertEquals("", dynamicBitFieldArrayField.getInitializer());
        assertFalse(dynamicBitFieldArrayField.isOptional());
        assertEquals("", dynamicBitFieldArrayField.getOptionalCondition());
        assertEquals("", dynamicBitFieldArrayField.getConstraint());
        assertTrue(dynamicBitFieldArrayField.isArray());
        assertEquals("", dynamicBitFieldArrayField.getArrayLength());
        assertTrue(dynamicBitFieldArrayField.isPacked());
        assertFalse(dynamicBitFieldArrayField.isImplicit());
    }

    private void checkParameterizedStruct(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.ParameterizedStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());

        final List<ParameterInfo> parameters = typeInfo.getParameters();
        assertEquals(1, parameters.size());

        final ParameterInfo parameter0 = parameters.get(0);
        assertEquals("simple", parameter0.getSchemaName());
        checkSimpleStruct(parameter0.getTypeInfo());

        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(1, fields.size());

        // array
        final FieldInfo arrayField = fields.get(0);
        assertEquals("array", arrayField.getSchemaName());

        assertEquals("uint8", arrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT8, arrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.SHORT, arrayField.getTypeInfo().getJavaType());
        assertEquals(8, arrayField.getTypeInfo().getBitSize());

        assertEquals(0, arrayField.getTypeArguments().size());
        assertEquals("", arrayField.getAlignment());
        assertEquals("", arrayField.getOffset());
        assertEquals("", arrayField.getInitializer());
        assertFalse(arrayField.isOptional());
        assertEquals("", arrayField.getOptionalCondition());
        assertEquals("", arrayField.getConstraint());
        assertTrue(arrayField.isArray());
        assertEquals("getSimple().getFieldU32()", arrayField.getArrayLength());
        assertFalse(arrayField.isPacked());
        assertFalse(arrayField.isImplicit());
    }

    private void checkRecursiveStruct(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.RecursiveStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(3, fields.size());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(0);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals("", fieldU32Field.getAlignment());
        assertEquals("", fieldU32Field.getOffset());
        assertEquals("", fieldU32Field.getInitializer());
        assertFalse(fieldU32Field.isOptional());
        assertEquals("", fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals("", fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());

        // fieldRecursion
        final FieldInfo fieldRecursion = fields.get(1);
        assertEquals("fieldRecursion", fieldRecursion.getSchemaName());

        assertEquals(typeInfo.getSchemaName(), fieldRecursion.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), fieldRecursion.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), fieldRecursion.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getFields().size(), fieldRecursion.getTypeInfo().getFields().size());

        assertEquals(0, fieldRecursion.getTypeArguments().size());
        assertEquals("", fieldRecursion.getAlignment());
        assertEquals("", fieldRecursion.getOffset());
        assertEquals("", fieldRecursion.getInitializer());
        assertTrue(fieldRecursion.isOptional());
        assertEquals("", fieldRecursion.getOptionalCondition());
        assertEquals("", fieldRecursion.getConstraint());
        assertFalse(fieldRecursion.isArray());
        assertEquals("", fieldRecursion.getArrayLength());
        assertFalse(fieldRecursion.isPacked());
        assertFalse(fieldRecursion.isImplicit());

        // arrayRecursion
        final FieldInfo arrayRecursion = fields.get(2);
        assertEquals("arrayRecursion", arrayRecursion.getSchemaName());

        assertEquals(typeInfo.getSchemaName(), arrayRecursion.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), arrayRecursion.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), arrayRecursion.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getFields().size(), arrayRecursion.getTypeInfo().getFields().size());

        assertEquals(0, arrayRecursion.getTypeArguments().size());
        assertEquals("", arrayRecursion.getAlignment());
        assertEquals("", arrayRecursion.getOffset());
        assertEquals("", arrayRecursion.getInitializer());
        assertFalse(arrayRecursion.isOptional());
        assertEquals("", arrayRecursion.getOptionalCondition());
        assertEquals("", arrayRecursion.getConstraint());
        assertTrue(arrayRecursion.isArray());
        assertEquals("", arrayRecursion.getArrayLength());
        assertFalse(arrayRecursion.isPacked());
        assertFalse(arrayRecursion.isImplicit());
    }

    private void checkRecursiveUnion(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.RecursiveUnion", typeInfo.getSchemaName());
        assertEquals(SchemaType.UNION, typeInfo.getSchemaType());
        assertEquals(JavaType.UNION, typeInfo.getJavaType());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(0);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals("", fieldU32Field.getAlignment());
        assertEquals("", fieldU32Field.getOffset());
        assertEquals("", fieldU32Field.getInitializer());
        assertFalse(fieldU32Field.isOptional());
        assertEquals("", fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals("", fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());

        // recursive
        final FieldInfo recursive = fields.get(1);
        assertEquals("recursive", recursive.getSchemaName());

        assertEquals(typeInfo.getSchemaName(), recursive.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), recursive.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), recursive.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getFields().size(), recursive.getTypeInfo().getFields().size());

        assertEquals(0, recursive.getTypeArguments().size());
        assertEquals("", recursive.getAlignment());
        assertEquals("", recursive.getOffset());
        assertEquals("", recursive.getInitializer());
        assertFalse(recursive.isOptional());
        assertEquals("", recursive.getOptionalCondition());
        assertEquals("", recursive.getConstraint());
        assertTrue(recursive.isArray());
        assertEquals("", recursive.getArrayLength());
        assertFalse(recursive.isPacked());
        assertFalse(recursive.isImplicit());
    }

    private void checkRecursiveChoice(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.RecursiveChoice", typeInfo.getSchemaName());
        assertEquals(SchemaType.CHOICE, typeInfo.getSchemaType());
        assertEquals(JavaType.CHOICE, typeInfo.getJavaType());

        final List<ParameterInfo> parameters = typeInfo.getParameters();
        assertEquals(2, parameters.size());

        // param1
        final ParameterInfo param1 = parameters.get(0);
        assertEquals("param1", param1.getSchemaName());
        assertEquals("bool", param1.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.BOOL, param1.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BOOLEAN, param1.getTypeInfo().getJavaType());
        assertEquals(1, param1.getTypeInfo().getBitSize());

        // param2
        final ParameterInfo param2 = parameters.get(1);
        assertEquals("param2", param2.getSchemaName());
        assertEquals("bool", param2.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.BOOL, param2.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BOOLEAN, param2.getTypeInfo().getJavaType());
        assertEquals(1, param2.getTypeInfo().getBitSize());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // recursive
        final FieldInfo recursive = fields.get(0);
        assertEquals("recursive", recursive.getSchemaName());

        assertEquals(typeInfo.getSchemaName(), recursive.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), recursive.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), recursive.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getFields().size(), recursive.getTypeInfo().getFields().size());

        assertEquals(2, recursive.getTypeArguments().size());
        assertEquals("getParam2()", recursive.getTypeArguments().get(0));
        assertEquals("false", recursive.getTypeArguments().get(1));
        assertEquals("", recursive.getAlignment());
        assertEquals("", recursive.getOffset());
        assertEquals("", recursive.getInitializer());
        assertFalse(recursive.isOptional());
        assertEquals("", recursive.getOptionalCondition());
        assertEquals("", recursive.getConstraint());
        assertTrue(recursive.isArray());
        assertEquals("", recursive.getArrayLength());
        assertFalse(recursive.isPacked());
        assertFalse(recursive.isImplicit());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(1);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals("", fieldU32Field.getAlignment());
        assertEquals("", fieldU32Field.getOffset());
        assertEquals("", fieldU32Field.getInitializer());
        assertFalse(fieldU32Field.isOptional());
        assertEquals("", fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals("", fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());
    }

    private void checkTestEnum(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.TestEnum", typeInfo.getSchemaName());
        assertEquals(SchemaType.ENUM, typeInfo.getSchemaType());
        assertEquals(JavaType.ENUM, typeInfo.getJavaType());

        assertEquals("uint16", typeInfo.getUnderlyingType().getSchemaName());
        assertEquals(SchemaType.UINT16, typeInfo.getUnderlyingType().getSchemaType());
        assertEquals(JavaType.INT, typeInfo.getUnderlyingType().getJavaType());
        assertEquals(16, typeInfo.getUnderlyingType().getBitSize());
        assertEquals(0, typeInfo.getUnderlyingTypeArguments().size());

        final List<ItemInfo> items = typeInfo.getEnumItems();
        assertEquals(3, items.size());

        // One
        final ItemInfo OneItem = items.get(0);
        assertEquals("One", OneItem.getSchemaName());
        assertEquals("0", OneItem.getValue());

        // TWO
        final ItemInfo TwoItem = items.get(1);
        assertEquals("TWO", TwoItem.getSchemaName());
        assertEquals("5", TwoItem.getValue());

        // ItemThree
        final ItemInfo ItemThreeItem = items.get(2);
        assertEquals("ItemThree", ItemThreeItem.getSchemaName());
        assertEquals("6", ItemThreeItem.getValue());
    }

    private void checkSimpleChoice(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.SimpleChoice", typeInfo.getSchemaName());
        assertEquals(SchemaType.CHOICE, typeInfo.getSchemaType());
        assertEquals(JavaType.CHOICE, typeInfo.getJavaType());

        final List<ParameterInfo> parameters = typeInfo.getParameters();
        assertEquals(1, parameters.size());

        final ParameterInfo parameter0 = parameters.get(0);
        assertEquals("selector", parameter0.getSchemaName());
        checkTestEnum(parameter0.getTypeInfo());

        final List<FunctionInfo> functions = typeInfo.getFunctions();
        assertEquals(1, functions.size());

        final FunctionInfo function0 = functions.get(0);
        assertEquals("fieldTwoFuncCall", function0.getSchemaName());
        assertEquals("uint32", function0.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, function0.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, function0.getTypeInfo().getJavaType());
        assertEquals(32, function0.getTypeInfo().getBitSize());
        assertEquals("getFieldTwo().funcSimpleStructFieldU32()", function0.getFunctionResult());

        assertEquals("getSelector()", typeInfo.getSelector());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // fieldTwo
        final FieldInfo fieldTwoField = fields.get(0);
        assertEquals("fieldTwo", fieldTwoField.getSchemaName());

        checkSimpleUnion(fieldTwoField.getTypeInfo());

        assertEquals(0, fieldTwoField.getTypeArguments().size());
        assertEquals("", fieldTwoField.getAlignment());
        assertEquals("", fieldTwoField.getOffset());
        assertEquals("", fieldTwoField.getInitializer());
        assertFalse(fieldTwoField.isOptional());
        assertEquals("", fieldTwoField.getOptionalCondition());
        assertEquals("", fieldTwoField.getConstraint());
        assertFalse(fieldTwoField.isArray());
        assertEquals("", fieldTwoField.getArrayLength());
        assertFalse(fieldTwoField.isPacked());
        assertFalse(fieldTwoField.isImplicit());

        // fieldDefault
        final FieldInfo fieldDefaultField = fields.get(1);
        assertEquals("fieldDefault", fieldDefaultField.getSchemaName());

        assertEquals("string", fieldDefaultField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.STRING, fieldDefaultField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.STRING, fieldDefaultField.getTypeInfo().getJavaType());

        assertEquals(0, fieldDefaultField.getTypeArguments().size());
        assertEquals("", fieldDefaultField.getAlignment());
        assertEquals("", fieldDefaultField.getOffset());
        assertEquals("", fieldDefaultField.getInitializer());
        assertFalse(fieldDefaultField.isOptional());
        assertEquals("", fieldDefaultField.getOptionalCondition());
        assertEquals("", fieldDefaultField.getConstraint());
        assertFalse(fieldDefaultField.isArray());
        assertEquals("", fieldDefaultField.getArrayLength());
        assertFalse(fieldDefaultField.isPacked());
        assertFalse(fieldDefaultField.isImplicit());

        final List<CaseInfo> cases = typeInfo.getCases();
        assertEquals(3, cases.size());

        // case One
        final CaseInfo case0 = cases.get(0);
        assertEquals(1, case0.getCaseExpressions().size());
        assertEquals("One", case0.getCaseExpressions().get(0));
        assertEquals(null, case0.getField());

        // case TWO
        final CaseInfo case1 = cases.get(1);
        assertEquals(1, case1.getCaseExpressions().size());
        assertEquals("TWO", case1.getCaseExpressions().get(0));
        assertEquals(fieldTwoField, case1.getField());

        // default
        final CaseInfo case2 = cases.get(2);
        assertEquals(0, case2.getCaseExpressions().size());
        assertEquals(fieldDefaultField, case2.getField());
    }

    private void checkSimpleUnion(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.SimpleUnion", typeInfo.getSchemaName());
        assertEquals(SchemaType.UNION, typeInfo.getSchemaType());
        assertEquals(JavaType.UNION, typeInfo.getJavaType());

        assertEquals(0, typeInfo.getParameters().size());

        final List<FunctionInfo> functions = typeInfo.getFunctions();
        assertEquals(1, functions.size());

        final FunctionInfo function0 = functions.get(0);
        assertEquals("simpleStructFieldU32", function0.getSchemaName());
        assertEquals("uint32", function0.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, function0.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, function0.getTypeInfo().getJavaType());
        assertEquals(32, function0.getTypeInfo().getBitSize());
        assertEquals("getSimpleStruct().getFieldU32()", function0.getFunctionResult());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // testBitmask
        final FieldInfo testBitmaskField = fields.get(0);
        assertEquals("testBitmask", testBitmaskField.getSchemaName());

        checkTestBitmask(testBitmaskField.getTypeInfo());

        assertEquals(0, testBitmaskField.getTypeArguments().size());
        assertEquals("", testBitmaskField.getAlignment());
        assertEquals("", testBitmaskField.getOffset());
        assertEquals("", testBitmaskField.getInitializer());
        assertFalse(testBitmaskField.isOptional());
        assertEquals("", testBitmaskField.getOptionalCondition());
        assertEquals("", testBitmaskField.getConstraint());
        assertFalse(testBitmaskField.isArray());
        assertEquals("", testBitmaskField.getArrayLength());
        assertFalse(testBitmaskField.isPacked());
        assertFalse(testBitmaskField.isImplicit());

        // simpleStruct
        final FieldInfo simpleStructField = fields.get(1);
        assertEquals("simpleStruct", simpleStructField.getSchemaName());

        checkSimpleStruct(simpleStructField.getTypeInfo());

        assertEquals(0, simpleStructField.getTypeArguments().size());
        assertEquals("", simpleStructField.getAlignment());
        assertEquals("", simpleStructField.getOffset());
        assertEquals("", simpleStructField.getInitializer());
        assertFalse(simpleStructField.isOptional());
        assertEquals("", simpleStructField.getOptionalCondition());
        assertEquals("", simpleStructField.getConstraint());
        assertFalse(simpleStructField.isArray());
        assertEquals("", simpleStructField.getArrayLength());
        assertFalse(simpleStructField.isPacked());
        assertFalse(simpleStructField.isImplicit());
    }

    private void checkTestBitmask(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.TestBitmask", typeInfo.getSchemaName());
        assertEquals(SchemaType.BITMASK, typeInfo.getSchemaType());
        assertEquals(JavaType.BITMASK, typeInfo.getJavaType());

        assertEquals("bit<>", typeInfo.getUnderlyingType().getSchemaName());
        assertEquals(SchemaType.DYNAMIC_UNSIGNED_BITFIELD, typeInfo.getUnderlyingType().getSchemaType());
        assertEquals(JavaType.SHORT, typeInfo.getUnderlyingType().getJavaType());
        assertEquals(1, typeInfo.getUnderlyingTypeArguments().size());
        assertEquals("10", typeInfo.getUnderlyingTypeArguments().get(0));

        final List<ItemInfo> values = typeInfo.getBitmaskValues();
        assertEquals(3, values.size());

        // RED
        final ItemInfo redValue = values.get(0);
        assertEquals("RED", redValue.getSchemaName());
        assertEquals("(short)1", redValue.getValue());

        // Green
        final ItemInfo greenValue = values.get(1);
        assertEquals("Green", greenValue.getSchemaName());
        assertEquals("(short)2", greenValue.getValue());

        // ColorBlue
        final ItemInfo colorBlueValue = values.get(2);
        assertEquals("ColorBlue", colorBlueValue.getSchemaName());
        assertEquals("(short)4", colorBlueValue.getValue());
    }

    private void checkTS32(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.TS32", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("with_type_info_code.type_info.TemplatedStruct", typeInfo.getTemplateName());

        assertEquals(1, typeInfo.getTemplateArguments().size());

        final TypeInfo templateArgument0 = typeInfo.getTemplateArguments().get(0);
        assertEquals("uint32", templateArgument0.getSchemaName());
        assertEquals(SchemaType.UINT32, templateArgument0.getSchemaType());
        assertEquals(JavaType.LONG, templateArgument0.getJavaType());
        assertEquals(32, templateArgument0.getBitSize());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(1, fields.size());

        // field
        final FieldInfo fieldField = fields.get(0);
        assertEquals("field", fieldField.getSchemaName());

        assertEquals("uint32", fieldField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldField.getTypeInfo().getJavaType());
        assertEquals(32, fieldField.getTypeInfo().getBitSize());

        assertEquals(0, fieldField.getTypeArguments().size());
        assertEquals("", fieldField.getAlignment());
        assertEquals("", fieldField.getOffset());
        assertEquals("", fieldField.getInitializer());
        assertFalse(fieldField.isOptional());
        assertEquals("", fieldField.getOptionalCondition());
        assertEquals("", fieldField.getConstraint());
        assertFalse(fieldField.isArray());
        assertEquals("", fieldField.getArrayLength());
        assertFalse(fieldField.isPacked());
        assertFalse(fieldField.isImplicit());
    }

    private void checkTemplatedParameterizedStruct_TS32(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.TemplatedParameterizedStruct_TS32",
                typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());

        assertEquals(1, typeInfo.getParameters().size());
        final ParameterInfo parameter0 = typeInfo.getParameters().get(0);
        assertEquals("param", parameter0.getSchemaName());
        checkTS32(parameter0.getTypeInfo());

        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("with_type_info_code.type_info.TemplatedParameterizedStruct", typeInfo.getTemplateName());

        assertEquals(1, typeInfo.getTemplateArguments().size());

        final TypeInfo templateArgument0 = typeInfo.getTemplateArguments().get(0);
        checkTS32(templateArgument0);

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(1, fields.size());

        // array
        final FieldInfo arrayField = fields.get(0);
        assertEquals("array", arrayField.getSchemaName());

        assertEquals("uint32", arrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, arrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, arrayField.getTypeInfo().getJavaType());
        assertEquals(32, arrayField.getTypeInfo().getBitSize());

        assertEquals(0, arrayField.getTypeArguments().size());
        assertEquals("", arrayField.getAlignment());
        assertEquals("", arrayField.getOffset());
        assertEquals("", arrayField.getInitializer());
        assertFalse(arrayField.isOptional());
        assertEquals("", arrayField.getOptionalCondition());
        assertEquals("", arrayField.getConstraint());
        assertTrue(arrayField.isArray());
        assertEquals("getParam().getField()", arrayField.getArrayLength());
        assertFalse(arrayField.isPacked());
        assertFalse(arrayField.isImplicit());
    }

    private void checkSimplePubsub(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.SimplePubsub", typeInfo.getSchemaName());
        assertEquals(SchemaType.PUBSUB, typeInfo.getSchemaType());
        assertEquals(JavaType.PUBSUB, typeInfo.getJavaType());

        final List<MessageInfo> messages = typeInfo.getMessages();
        assertEquals(2, messages.size());

        // pubSimpleStruct
        final MessageInfo pubSimpleStructMessage = messages.get(0);
        assertEquals("pubSimpleStruct", pubSimpleStructMessage.getSchemaName());
        checkSimpleStruct(pubSimpleStructMessage.getTypeInfo());
        assertTrue(pubSimpleStructMessage.isPublished());
        assertFalse(pubSimpleStructMessage.isSubscribed());
        assertEquals("simpleStruct", pubSimpleStructMessage.getTopic());

        // subSimpleStruct
        final MessageInfo subSimpleStructMessage = messages.get(1);
        assertEquals("subSimpleStruct", subSimpleStructMessage.getSchemaName());
        checkSimpleStruct(subSimpleStructMessage.getTypeInfo());
        assertFalse(subSimpleStructMessage.isPublished());
        assertTrue(subSimpleStructMessage.isSubscribed());
        assertEquals("simpleStruct", subSimpleStructMessage.getTopic());
    }

    private void checkSimpleService(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.type_info.SimpleService", typeInfo.getSchemaName());
        assertEquals(SchemaType.SERVICE, typeInfo.getSchemaType());
        assertEquals(JavaType.SERVICE, typeInfo.getJavaType());

        final List<MethodInfo> methods = typeInfo.getMethods();
        assertEquals(1, methods.size());

        // getSimpleStruct
        final MethodInfo getSimpleStructMethod = methods.get(0);
        assertEquals("getSimpleStruct", getSimpleStructMethod.getSchemaName());

        checkSimpleStruct(getSimpleStructMethod.getResponseTypeInfo());
        checkSimpleUnion(getSimpleStructMethod.getRequestTypeInfo());
    }
}
