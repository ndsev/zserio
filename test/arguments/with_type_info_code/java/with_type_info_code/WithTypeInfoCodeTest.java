package with_type_info_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.io.IOException;
import java.io.File;
import java.math.BigInteger;

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
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamWriter;
import zserio.runtime.io.BitBuffer;

public class WithTypeInfoCodeTest
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

    @Test
    public void writeReadFileWithOptionals() throws IOException
    {
        final WithTypeInfoCode withTypeInfoCode = WithTypeInfoCodeCreator.createWithTypeInfoCode();
        final File file = new File(BLOB_NAME_WITH_OPTIONALS);
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        withTypeInfoCode.write(writer);
        writer.close();

        final WithTypeInfoCode readWithTypeInfoCode = new WithTypeInfoCode(file);
        assertEquals(withTypeInfoCode, readWithTypeInfoCode);
    }

    @Test
    public void writeReadFileWithoutOptionals() throws IOException
    {
        final boolean createdOptionals = false;
        final WithTypeInfoCode withTypeInfoCode =
                WithTypeInfoCodeCreator.createWithTypeInfoCode(createdOptionals);
        final File file = new File(BLOB_NAME_WITHOUT_OPTIONALS);
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        withTypeInfoCode.write(writer);
        writer.close();

        final WithTypeInfoCode readWithTypeInfoCode = new WithTypeInfoCode(file);
        assertEquals(withTypeInfoCode, readWithTypeInfoCode);
    }

    private void checkSqlDatabase(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.SqlDatabase", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_DATABASE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_DATABASE, typeInfo.getJavaType());
        assertEquals(SqlDatabase.class, typeInfo.getJavaClass());

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
        assertEquals("with_type_info_code.SqlTable", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());
        assertEquals(SqlTable.class, typeInfo.getJavaClass());

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
        assertEquals(long.class, pkColumn.getTypeInfo().getJavaClass());
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
        assertEquals(String.class, textColumn.getTypeInfo().getJavaClass());

        assertEquals(0, textColumn.getTypeArguments().size());
        assertEquals("TEXT", textColumn.getSqlTypeName());
        assertEquals("", textColumn.getSqlConstraint());
        assertFalse(textColumn.isVirtual());
    }

    private void checkTemplatedSqlTable_uint32(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.TemplatedSqlTable_uint32", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());
        assertEquals(TemplatedSqlTable_uint32.class, typeInfo.getJavaClass());

        assertEquals("PRIMARY KEY(pk)", typeInfo.getSqlConstraint());
        assertEquals("", typeInfo.getVirtualTableUsing());
        assertFalse(typeInfo.isWithoutRowId());

        assertEquals("with_type_info_code.TemplatedSqlTable", typeInfo.getTemplateName());
        final List<TypeInfo> templateArgs = typeInfo.getTemplateArguments();
        assertEquals(1, templateArgs.size());
        final TypeInfo templateArg0Info = templateArgs.get(0);
        assertEquals("uint32", templateArg0Info.getSchemaName());
        assertEquals(SchemaType.UINT32, templateArg0Info.getSchemaType());
        assertEquals(JavaType.LONG, templateArg0Info.getJavaType());
        assertEquals(long.class, templateArg0Info.getJavaClass());
        assertEquals(32, templateArg0Info.getBitSize());

        final List<ColumnInfo> columns = typeInfo.getColumns();
        assertEquals(2, columns.size());

        // pk
        final ColumnInfo pkColumn = columns.get(0);
        assertEquals("pk", pkColumn.getSchemaName());

        assertEquals("uint32", pkColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, pkColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, pkColumn.getTypeInfo().getJavaType());
        assertEquals(long.class, pkColumn.getTypeInfo().getJavaClass());
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
        assertEquals("with_type_info_code.TemplatedSqlTableU8", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());
        assertEquals(TemplatedSqlTableU8.class, typeInfo.getJavaClass());

        assertEquals("PRIMARY KEY(pk)", typeInfo.getSqlConstraint());
        assertEquals("", typeInfo.getVirtualTableUsing());
        assertFalse(typeInfo.isWithoutRowId());

        assertEquals("with_type_info_code.TemplatedSqlTable", typeInfo.getTemplateName());
        final List<TypeInfo> templateArgs = typeInfo.getTemplateArguments();
        assertEquals(1, templateArgs.size());
        final TypeInfo templateArg0Info = templateArgs.get(0);
        assertEquals("uint8", templateArg0Info.getSchemaName());
        assertEquals(SchemaType.UINT8, templateArg0Info.getSchemaType());
        assertEquals(JavaType.SHORT, templateArg0Info.getJavaType());
        assertEquals(short.class, templateArg0Info.getJavaClass());
        assertEquals(8, templateArg0Info.getBitSize());

        final List<ColumnInfo> columns = typeInfo.getColumns();
        assertEquals(2, columns.size());

        // pk
        final ColumnInfo pkColumn = columns.get(0);
        assertEquals("pk", pkColumn.getSchemaName());

        assertEquals("uint8", pkColumn.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT8, pkColumn.getTypeInfo().getSchemaType());
        assertEquals(JavaType.SHORT, pkColumn.getTypeInfo().getJavaType());
        assertEquals(short.class, pkColumn.getTypeInfo().getJavaClass());
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
        assertEquals("with_type_info_code.Fts4Table", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());
        assertEquals(Fts4Table.class, typeInfo.getJavaClass());

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
        assertEquals(long.class, docIdColumn.getTypeInfo().getJavaClass());
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
        assertEquals(String.class, searchTagsColumn.getTypeInfo().getJavaClass());

        assertEquals(0, searchTagsColumn.getTypeArguments().size());
        assertEquals("TEXT", searchTagsColumn.getSqlTypeName());
        assertEquals("", searchTagsColumn.getSqlConstraint());
        assertFalse(searchTagsColumn.isVirtual());
    }

    private void checkWithoutRowIdTable(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.WithoutRowIdTable", typeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, typeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, typeInfo.getJavaType());
        assertEquals(WithoutRowIdTable.class, typeInfo.getJavaClass());

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
        assertEquals(long.class, pk1Column.getTypeInfo().getJavaClass());
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
        assertEquals(long.class, pk2Column.getTypeInfo().getJavaClass());
        assertEquals(32, pk2Column.getTypeInfo().getBitSize());

        assertEquals(0, pk2Column.getTypeArguments().size());
        assertEquals("INTEGER", pk2Column.getSqlTypeName());
        assertEquals("NOT NULL", pk2Column.getSqlConstraint());
        assertFalse(pk2Column.isVirtual());
    }

    private void checkWithTypeInfoCode(TypeInfo typeInfo)
    {
        final SimpleStruct simpleStruct = new SimpleStruct((long)0, (long)10, "Text", true, 1.0f, 2.0f, 4.0);
        final RecursiveChoice recursiveChoice = new RecursiveChoice(true, false);
        final SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setSimpleStruct(simpleStruct);
        final SimpleChoice simpleChoice = new SimpleChoice(TestEnum.TWO);
        simpleChoice.setFieldTwo(simpleUnion);
        final TS32 ts32 = new TS32((long)11);
        final TemplatedParameterizedStruct_TS32 templatedParameterizedStruct_TS32 =
                new TemplatedParameterizedStruct_TS32(ts32);
        final WithTypeInfoCode withTypeInfoCode = new WithTypeInfoCode();
        withTypeInfoCode.setSimpleStruct(simpleStruct);
        withTypeInfoCode.setRecursiveChoice(recursiveChoice);
        withTypeInfoCode.setSelector(TestEnum.TWO);
        withTypeInfoCode.setSimpleChoice(simpleChoice);
        withTypeInfoCode.setTemplatedStruct(ts32);
        withTypeInfoCode.setTemplatedParameterizedStruct(templatedParameterizedStruct_TS32);

        assertEquals("with_type_info_code.WithTypeInfoCode", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());
        assertEquals(WithTypeInfoCode.class, typeInfo.getJavaClass());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(13, fields.size());

        // simpleStruct
        final FieldInfo simpleStructField = fields.get(0);
        assertEquals("simpleStruct", simpleStructField.getSchemaName());
        assertEquals("getSimpleStruct", simpleStructField.getGetterName());
        assertEquals("setSimpleStruct", simpleStructField.getSetterName());

        checkSimpleStruct(simpleStructField.getTypeInfo());

        assertEquals(0, simpleStructField.getTypeArguments().size());
        assertEquals(null, simpleStructField.getAlignment());
        assertEquals(null, simpleStructField.getOffset());
        assertEquals(null, simpleStructField.getInitializer());
        assertFalse(simpleStructField.isOptional());
        assertEquals(null, simpleStructField.getOptionalCondition());
        assertEquals("", simpleStructField.getIsUsedIndicatorName());
        assertEquals("", simpleStructField.getIsSetIndicatorName());
        assertEquals(null, simpleStructField.getConstraint());
        assertFalse(simpleStructField.isArray());
        assertEquals(null, simpleStructField.getArrayLength());
        assertFalse(simpleStructField.isPacked());
        assertFalse(simpleStructField.isImplicit());

        // complexStruct
        final FieldInfo complexStructField = fields.get(1);
        assertEquals("complexStruct", complexStructField.getSchemaName());
        assertEquals("getComplexStruct", complexStructField.getGetterName());
        assertEquals("setComplexStruct", complexStructField.getSetterName());

        checkComplexStruct(complexStructField.getTypeInfo());

        assertEquals(0, complexStructField.getTypeArguments().size());
        assertEquals(null, complexStructField.getAlignment());
        assertEquals(null, complexStructField.getOffset());
        assertEquals(null, complexStructField.getInitializer());
        assertFalse(complexStructField.isOptional());
        assertEquals(null, complexStructField.getOptionalCondition());
        assertEquals("", complexStructField.getIsUsedIndicatorName());
        assertEquals("", complexStructField.getIsSetIndicatorName());
        assertEquals(null, complexStructField.getConstraint());
        assertFalse(complexStructField.isArray());
        assertEquals(null, complexStructField.getArrayLength());
        assertFalse(complexStructField.isPacked());
        assertFalse(complexStructField.isImplicit());

        // parameterizedStruct
        final FieldInfo parameterizedStructField = fields.get(2);
        assertEquals("parameterizedStruct", parameterizedStructField.getSchemaName());
        assertEquals("getParameterizedStruct", parameterizedStructField.getGetterName());
        assertEquals("setParameterizedStruct", parameterizedStructField.getSetterName());

        checkParameterizedStruct(parameterizedStructField.getTypeInfo());

        assertEquals(1, parameterizedStructField.getTypeArguments().size());
        assertEquals(withTypeInfoCode.getSimpleStruct(),
                parameterizedStructField.getTypeArguments().get(0).apply(withTypeInfoCode, null));
        assertEquals(null, parameterizedStructField.getAlignment());
        assertEquals(null, parameterizedStructField.getOffset());
        assertEquals(null, parameterizedStructField.getInitializer());
        assertFalse(parameterizedStructField.isOptional());
        assertEquals(null, parameterizedStructField.getOptionalCondition());
        assertEquals("", parameterizedStructField.getIsUsedIndicatorName());
        assertEquals("", parameterizedStructField.getIsSetIndicatorName());
        assertEquals(null, parameterizedStructField.getConstraint());
        assertFalse(parameterizedStructField.isArray());
        assertEquals(null, parameterizedStructField.getArrayLength());
        assertFalse(parameterizedStructField.isPacked());
        assertFalse(parameterizedStructField.isImplicit());

        // recursiveStruct
        final FieldInfo recursiveStructField = fields.get(3);
        assertEquals("recursiveStruct", recursiveStructField.getSchemaName());
        assertEquals("getRecursiveStruct", recursiveStructField.getGetterName());
        assertEquals("setRecursiveStruct", recursiveStructField.getSetterName());

        checkRecursiveStruct(recursiveStructField.getTypeInfo());

        assertEquals(0, recursiveStructField.getTypeArguments().size());
        assertEquals(null, recursiveStructField.getAlignment());
        assertEquals(null, recursiveStructField.getOffset());
        assertEquals(null, recursiveStructField.getInitializer());
        assertFalse(recursiveStructField.isOptional());
        assertEquals(null, recursiveStructField.getOptionalCondition());
        assertEquals("", recursiveStructField.getIsUsedIndicatorName());
        assertEquals("", recursiveStructField.getIsSetIndicatorName());
        assertEquals(null, recursiveStructField.getConstraint());
        assertFalse(recursiveStructField.isArray());
        assertEquals(null, recursiveStructField.getArrayLength());
        assertFalse(recursiveStructField.isPacked());
        assertFalse(recursiveStructField.isImplicit());

        // recursiveUnion
        final FieldInfo recursiveUnion = fields.get(4);
        assertEquals("recursiveUnion", recursiveUnion.getSchemaName());
        assertEquals("getRecursiveUnion", recursiveUnion.getGetterName());
        assertEquals("setRecursiveUnion", recursiveUnion.getSetterName());

        checkRecursiveUnion(recursiveUnion.getTypeInfo());

        assertEquals(0, recursiveUnion.getTypeArguments().size());
        assertEquals(null, recursiveUnion.getAlignment());
        assertEquals(null, recursiveUnion.getOffset());
        assertEquals(null, recursiveUnion.getInitializer());
        assertFalse(recursiveUnion.isOptional());
        assertEquals(null, recursiveUnion.getOptionalCondition());
        assertEquals("", recursiveUnion.getIsUsedIndicatorName());
        assertEquals("", recursiveUnion.getIsSetIndicatorName());
        assertEquals(null, recursiveUnion.getConstraint());
        assertFalse(recursiveUnion.isArray());
        assertEquals(null, recursiveUnion.getArrayLength());
        assertFalse(recursiveUnion.isPacked());
        assertFalse(recursiveUnion.isImplicit());

        // recursiveChoice
        final FieldInfo recursiveChoiceField = fields.get(5);
        assertEquals("recursiveChoice", recursiveChoiceField.getSchemaName());
        assertEquals("getRecursiveChoice", recursiveChoiceField.getGetterName());
        assertEquals("setRecursiveChoice", recursiveChoiceField.getSetterName());

        checkRecursiveChoice(recursiveChoiceField.getTypeInfo());

        assertEquals(2, recursiveChoiceField.getTypeArguments().size());
        assertEquals(true, recursiveChoiceField.getTypeArguments().get(0).apply(withTypeInfoCode, null));
        assertEquals(false, recursiveChoiceField.getTypeArguments().get(1).apply(withTypeInfoCode, null));
        assertEquals(null, recursiveChoiceField.getAlignment());
        assertEquals(null, recursiveChoiceField.getOffset());
        assertEquals(null, recursiveChoiceField.getInitializer());
        assertFalse(recursiveChoiceField.isOptional());
        assertEquals(null, recursiveChoiceField.getOptionalCondition());
        assertEquals("", recursiveChoiceField.getIsUsedIndicatorName());
        assertEquals("", recursiveChoiceField.getIsSetIndicatorName());
        assertEquals(null, recursiveChoiceField.getConstraint());
        assertFalse(recursiveChoiceField.isArray());
        assertEquals(null, recursiveChoiceField.getArrayLength());
        assertFalse(recursiveChoiceField.isPacked());
        assertFalse(recursiveChoiceField.isImplicit());

        // selector
        final FieldInfo selectorField = fields.get(6);
        assertEquals("selector", selectorField.getSchemaName());
        assertEquals("getSelector", selectorField.getGetterName());
        assertEquals("setSelector", selectorField.getSetterName());

        checkTestEnum(selectorField.getTypeInfo());

        assertEquals(0, selectorField.getTypeArguments().size());
        assertEquals(null, selectorField.getAlignment());
        assertEquals(null, selectorField.getOffset());
        assertEquals(null, selectorField.getInitializer());
        assertFalse(selectorField.isOptional());
        assertEquals(null, selectorField.getOptionalCondition());
        assertEquals("", selectorField.getIsUsedIndicatorName());
        assertEquals("", selectorField.getIsSetIndicatorName());
        assertEquals(null, selectorField.getConstraint());
        assertFalse(selectorField.isArray());
        assertEquals(null, selectorField.getArrayLength());
        assertFalse(selectorField.isPacked());
        assertFalse(selectorField.isImplicit());

        // simpleChoice
        final FieldInfo simpleChoiceField = fields.get(7);
        assertEquals("simpleChoice", simpleChoiceField.getSchemaName());
        assertEquals("getSimpleChoice", simpleChoiceField.getGetterName());
        assertEquals("setSimpleChoice", simpleChoiceField.getSetterName());

        checkSimpleChoice(simpleChoiceField.getTypeInfo());

        assertEquals(1, simpleChoiceField.getTypeArguments().size());
        assertEquals(withTypeInfoCode.getSelector(),
                simpleChoiceField.getTypeArguments().get(0).apply(withTypeInfoCode, null));
        assertEquals(null, simpleChoiceField.getAlignment());
        assertEquals(null, simpleChoiceField.getOffset());
        assertEquals(null, simpleChoiceField.getInitializer());
        assertFalse(simpleChoiceField.isOptional());
        assertEquals(null, simpleChoiceField.getOptionalCondition());
        assertEquals("", simpleChoiceField.getIsUsedIndicatorName());
        assertEquals("", simpleChoiceField.getIsSetIndicatorName());
        assertEquals(null, simpleChoiceField.getConstraint());
        assertFalse(simpleChoiceField.isArray());
        assertEquals(null, simpleChoiceField.getArrayLength());
        assertFalse(simpleChoiceField.isPacked());
        assertFalse(simpleChoiceField.isImplicit());

        // templatedStruct
        final FieldInfo templatedStructField = fields.get(8);
        assertEquals("templatedStruct", templatedStructField.getSchemaName());
        assertEquals("getTemplatedStruct", templatedStructField.getGetterName());
        assertEquals("setTemplatedStruct", templatedStructField.getSetterName());

        checkTS32(templatedStructField.getTypeInfo());

        assertEquals(0, templatedStructField.getTypeArguments().size());
        assertEquals(null, templatedStructField.getAlignment());
        assertEquals(null, templatedStructField.getOffset());
        assertEquals(null, templatedStructField.getInitializer());
        assertFalse(templatedStructField.isOptional());
        assertEquals(null, templatedStructField.getOptionalCondition());
        assertEquals("", templatedStructField.getIsUsedIndicatorName());
        assertEquals("", templatedStructField.getIsSetIndicatorName());
        assertEquals(null, templatedStructField.getConstraint());
        assertFalse(templatedStructField.isArray());
        assertEquals(null, templatedStructField.getArrayLength());
        assertFalse(templatedStructField.isPacked());
        assertFalse(templatedStructField.isImplicit());

        // templatedParameterizedStruct
        final FieldInfo templatedParameterizedStructField = fields.get(9);
        assertEquals("templatedParameterizedStruct", templatedParameterizedStructField.getSchemaName());
        assertEquals("getTemplatedParameterizedStruct", templatedParameterizedStructField.getGetterName());
        assertEquals("setTemplatedParameterizedStruct", templatedParameterizedStructField.getSetterName());

        checkTemplatedParameterizedStruct_TS32(templatedParameterizedStructField.getTypeInfo());

        assertEquals(1, templatedParameterizedStructField.getTypeArguments().size());
        assertEquals(withTypeInfoCode.getTemplatedStruct(),
                templatedParameterizedStructField.getTypeArguments().get(0).apply(withTypeInfoCode, null));
        assertEquals(null, templatedParameterizedStructField.getAlignment());
        assertEquals(null, templatedParameterizedStructField.getOffset());
        assertEquals(null, templatedParameterizedStructField.getInitializer());
        assertFalse(templatedParameterizedStructField.isOptional());
        assertEquals(null, templatedParameterizedStructField.getOptionalCondition());
        assertEquals("", templatedParameterizedStructField.getIsUsedIndicatorName());
        assertEquals("", templatedParameterizedStructField.getIsSetIndicatorName());
        assertEquals(null, templatedParameterizedStructField.getConstraint());
        assertFalse(templatedParameterizedStructField.isArray());
        assertEquals(null, templatedParameterizedStructField.getArrayLength());
        assertFalse(templatedParameterizedStructField.isPacked());
        assertFalse(templatedParameterizedStructField.isImplicit());

        // externData
        final FieldInfo externDataField = fields.get(10);
        assertEquals("externData", externDataField.getSchemaName());
        assertEquals("getExternData", externDataField.getGetterName());
        assertEquals("setExternData", externDataField.getSetterName());

        assertEquals("extern", externDataField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.EXTERN, externDataField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIT_BUFFER, externDataField.getTypeInfo().getJavaType());
        assertEquals(BitBuffer.class, externDataField.getTypeInfo().getJavaClass());

        assertEquals(0, externDataField.getTypeArguments().size());
        assertEquals(null, externDataField.getAlignment());
        assertEquals(null, externDataField.getOffset());
        assertEquals(null, externDataField.getInitializer());
        assertFalse(externDataField.isOptional());
        assertEquals(null, externDataField.getOptionalCondition());
        assertEquals("", externDataField.getIsUsedIndicatorName());
        assertEquals("", externDataField.getIsSetIndicatorName());
        assertEquals(null, externDataField.getConstraint());
        assertFalse(externDataField.isArray());
        assertEquals(null, externDataField.getArrayLength());
        assertFalse(externDataField.isPacked());
        assertFalse(externDataField.isImplicit());

        // externArray
        final FieldInfo externArrayField = fields.get(11);
        assertEquals("externArray", externArrayField.getSchemaName());
        assertEquals("getExternArray", externArrayField.getGetterName());
        assertEquals("setExternArray", externArrayField.getSetterName());

        assertEquals("extern", externArrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.EXTERN, externArrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIT_BUFFER, externArrayField.getTypeInfo().getJavaType());
        assertEquals(BitBuffer.class, externArrayField.getTypeInfo().getJavaClass());

        assertEquals(0, externArrayField.getTypeArguments().size());
        assertEquals(null, externArrayField.getAlignment());
        assertEquals(null, externArrayField.getOffset());
        assertEquals(null, externArrayField.getInitializer());
        assertFalse(externArrayField.isOptional());
        assertEquals(null, externArrayField.getOptionalCondition());
        assertEquals("", externArrayField.getIsUsedIndicatorName());
        assertEquals("", externArrayField.getIsSetIndicatorName());
        assertEquals(null, externArrayField.getConstraint());
        assertTrue(externArrayField.isArray());
        assertEquals(null, externArrayField.getArrayLength());
        assertFalse(externArrayField.isPacked());
        assertFalse(externArrayField.isImplicit());

        // implicitArray
        final FieldInfo implicitArrayField = fields.get(12);
        assertEquals("implicitArray", implicitArrayField.getSchemaName());
        assertEquals("getImplicitArray", implicitArrayField.getGetterName());
        assertEquals("setImplicitArray", implicitArrayField.getSetterName());

        assertEquals("uint32", implicitArrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, implicitArrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, implicitArrayField.getTypeInfo().getJavaType());
        assertEquals(long.class, implicitArrayField.getTypeInfo().getJavaClass());
        assertEquals(32, implicitArrayField.getTypeInfo().getBitSize());

        assertEquals(0, implicitArrayField.getTypeArguments().size());
        assertEquals(null, implicitArrayField.getAlignment());
        assertEquals(null, implicitArrayField.getOffset());
        assertEquals(null, implicitArrayField.getInitializer());
        assertFalse(implicitArrayField.isOptional());
        assertEquals(null, implicitArrayField.getOptionalCondition());
        assertEquals("", implicitArrayField.getIsUsedIndicatorName());
        assertEquals("", implicitArrayField.getIsSetIndicatorName());
        assertEquals(null, implicitArrayField.getConstraint());
        assertTrue(implicitArrayField.isArray());
        assertEquals(null, implicitArrayField.getArrayLength());
        assertFalse(implicitArrayField.isPacked());
        assertTrue(implicitArrayField.isImplicit());
    }

    private void checkSimpleStruct(TypeInfo typeInfo)
    {
        final SimpleStruct simpleStruct = new SimpleStruct((long)0, (long)10, "Text", true, 1.0f, 2.0f, 4.0);

        assertEquals("with_type_info_code.SimpleStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());
        assertEquals(SimpleStruct.class, typeInfo.getJavaClass());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(7, fields.size());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(0);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());
        assertEquals("getFieldU32", fieldU32Field.getGetterName());
        assertEquals("setFieldU32", fieldU32Field.getSetterName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(long.class, fieldU32Field.getTypeInfo().getJavaClass());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals(8, fieldU32Field.getAlignment().getAsInt());
        assertEquals(null, fieldU32Field.getOffset());
        assertEquals(10, fieldU32Field.getInitializer().get());
        assertFalse(fieldU32Field.isOptional());
        assertEquals(null, fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getIsUsedIndicatorName());
        assertEquals("", fieldU32Field.getIsSetIndicatorName());
        assertEquals(null, fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals(null, fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());

        // fieldOffset
        final FieldInfo fieldOffsetField = fields.get(1);
        assertEquals("fieldOffset", fieldOffsetField.getSchemaName());
        assertEquals("getFieldOffset", fieldOffsetField.getGetterName());
        assertEquals("setFieldOffset", fieldOffsetField.getSetterName());

        assertEquals("uint32", fieldOffsetField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldOffsetField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldOffsetField.getTypeInfo().getJavaType());
        assertEquals(long.class, fieldOffsetField.getTypeInfo().getJavaClass());
        assertEquals(32, fieldOffsetField.getTypeInfo().getBitSize());

        assertEquals(0, fieldOffsetField.getTypeArguments().size());
        assertEquals(null, fieldOffsetField.getAlignment());
        assertEquals(null, fieldOffsetField.getOffset());
        assertEquals(null, fieldOffsetField.getInitializer());
        assertFalse(fieldOffsetField.isOptional());
        assertEquals(null, fieldOffsetField.getOptionalCondition());
        assertEquals("", fieldOffsetField.getIsUsedIndicatorName());
        assertEquals("", fieldOffsetField.getIsSetIndicatorName());
        assertEquals(null, fieldOffsetField.getConstraint());
        assertFalse(fieldOffsetField.isArray());
        assertEquals(null, fieldOffsetField.getArrayLength());
        assertFalse(fieldOffsetField.isPacked());
        assertFalse(fieldOffsetField.isImplicit());

        // fieldString
        final FieldInfo fieldStringField = fields.get(2);
        assertEquals("fieldString", fieldStringField.getSchemaName());
        assertEquals("getFieldString", fieldStringField.getGetterName());
        assertEquals("setFieldString", fieldStringField.getSetterName());

        assertEquals("string", fieldStringField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.STRING, fieldStringField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.STRING, fieldStringField.getTypeInfo().getJavaType());
        assertEquals(String.class, fieldStringField.getTypeInfo().getJavaClass());

        assertEquals(0, fieldStringField.getTypeArguments().size());
        assertEquals(null, fieldStringField.getAlignment());
        assertEquals((long)10, fieldStringField.getOffset().apply(simpleStruct, null));
        assertEquals("MyString", fieldStringField.getInitializer().get());
        assertFalse(fieldStringField.isOptional());
        assertEquals(null, fieldStringField.getOptionalCondition());
        assertEquals("", fieldStringField.getIsUsedIndicatorName());
        assertEquals("", fieldStringField.getIsSetIndicatorName());
        assertEquals(null, fieldStringField.getConstraint());
        assertFalse(fieldStringField.isArray());
        assertEquals(null, fieldStringField.getArrayLength());
        assertFalse(fieldStringField.isPacked());
        assertFalse(fieldStringField.isImplicit());

        // fieldBool
        final FieldInfo fieldBoolField = fields.get(3);
        assertEquals("fieldBool", fieldBoolField.getSchemaName());
        assertEquals("getFieldBool", fieldBoolField.getGetterName());
        assertEquals("setFieldBool", fieldBoolField.getSetterName());

        assertEquals("bool", fieldBoolField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.BOOL, fieldBoolField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BOOLEAN, fieldBoolField.getTypeInfo().getJavaType());
        assertEquals(boolean.class, fieldBoolField.getTypeInfo().getJavaClass());
        assertEquals(1, fieldBoolField.getTypeInfo().getBitSize());

        assertEquals(0, fieldBoolField.getTypeArguments().size());
        assertEquals(null, fieldBoolField.getAlignment());
        assertEquals(null, fieldBoolField.getOffset());
        assertEquals(false, fieldBoolField.getInitializer().get());
        assertFalse(fieldBoolField.isOptional());
        assertEquals(null, fieldBoolField.getOptionalCondition());
        assertEquals("", fieldBoolField.getIsUsedIndicatorName());
        assertEquals("", fieldBoolField.getIsSetIndicatorName());
        assertEquals(null, fieldBoolField.getConstraint());
        assertFalse(fieldBoolField.isArray());
        assertEquals(null, fieldBoolField.getArrayLength());
        assertFalse(fieldBoolField.isPacked());
        assertFalse(fieldBoolField.isImplicit());

        // fieldFloat16
        final FieldInfo fieldFloat16Field = fields.get(4);
        assertEquals("fieldFloat16", fieldFloat16Field.getSchemaName());
        assertEquals("getFieldFloat16", fieldFloat16Field.getGetterName());
        assertEquals("setFieldFloat16", fieldFloat16Field.getSetterName());

        assertEquals("float16", fieldFloat16Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FLOAT16, fieldFloat16Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.FLOAT, fieldFloat16Field.getTypeInfo().getJavaType());
        assertEquals(float.class, fieldFloat16Field.getTypeInfo().getJavaClass());
        assertEquals(16, fieldFloat16Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldFloat16Field.getTypeArguments().size());
        assertEquals(null, fieldFloat16Field.getAlignment());
        assertEquals(null, fieldFloat16Field.getOffset());
        assertEquals(1.0f, fieldFloat16Field.getInitializer().get());
        assertFalse(fieldFloat16Field.isOptional());
        assertEquals(null, fieldFloat16Field.getOptionalCondition());
        assertEquals("", fieldFloat16Field.getIsUsedIndicatorName());
        assertEquals("", fieldFloat16Field.getIsSetIndicatorName());
        assertEquals(null, fieldFloat16Field.getConstraint());
        assertFalse(fieldFloat16Field.isArray());
        assertEquals(null, fieldFloat16Field.getArrayLength());
        assertFalse(fieldFloat16Field.isPacked());
        assertFalse(fieldFloat16Field.isImplicit());

        // fieldFloat32
        final FieldInfo fieldFloat32Field = fields.get(5);
        assertEquals("fieldFloat32", fieldFloat32Field.getSchemaName());
        assertEquals("getFieldFloat32", fieldFloat32Field.getGetterName());
        assertEquals("setFieldFloat32", fieldFloat32Field.getSetterName());

        assertEquals("float32", fieldFloat32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FLOAT32, fieldFloat32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.FLOAT, fieldFloat32Field.getTypeInfo().getJavaType());
        assertEquals(float.class, fieldFloat32Field.getTypeInfo().getJavaClass());
        assertEquals(32, fieldFloat32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldFloat32Field.getTypeArguments().size());
        assertEquals(null, fieldFloat32Field.getAlignment());
        assertEquals(null, fieldFloat32Field.getOffset());
        assertEquals(null, fieldFloat32Field.getInitializer());
        assertFalse(fieldFloat32Field.isOptional());
        assertEquals(null, fieldFloat32Field.getOptionalCondition());
        assertEquals("", fieldFloat32Field.getIsUsedIndicatorName());
        assertEquals("", fieldFloat32Field.getIsSetIndicatorName());
        assertEquals(null, fieldFloat32Field.getConstraint());
        assertFalse(fieldFloat32Field.isArray());
        assertEquals(null, fieldFloat32Field.getArrayLength());
        assertFalse(fieldFloat32Field.isPacked());
        assertFalse(fieldFloat32Field.isImplicit());

        // fieldFloat64
        final FieldInfo fieldFloat64Field = fields.get(6);
        assertEquals("fieldFloat64", fieldFloat64Field.getSchemaName());
        assertEquals("getFieldFloat64", fieldFloat64Field.getGetterName());
        assertEquals("setFieldFloat64", fieldFloat64Field.getSetterName());

        assertEquals("float64", fieldFloat64Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FLOAT64, fieldFloat64Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.DOUBLE, fieldFloat64Field.getTypeInfo().getJavaType());
        assertEquals(double.class, fieldFloat64Field.getTypeInfo().getJavaClass());
        assertEquals(64, fieldFloat64Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldFloat64Field.getTypeArguments().size());
        assertEquals(null, fieldFloat64Field.getAlignment());
        assertEquals(null, fieldFloat64Field.getOffset());
        assertEquals(2.0, fieldFloat64Field.getInitializer().get());
        assertFalse(fieldFloat64Field.isOptional());
        assertEquals(null, fieldFloat64Field.getOptionalCondition());
        assertEquals("", fieldFloat64Field.getIsUsedIndicatorName());
        assertEquals("", fieldFloat64Field.getIsSetIndicatorName());
        assertEquals(null, fieldFloat64Field.getConstraint());
        assertFalse(fieldFloat64Field.isArray());
        assertEquals(null, fieldFloat64Field.getArrayLength());
        assertFalse(fieldFloat64Field.isPacked());
        assertFalse(fieldFloat64Field.isImplicit());
    }

    private void checkComplexStruct(TypeInfo typeInfo)
    {
        final SimpleStruct simpleStruct = new SimpleStruct((long)0, (long)10, "Text", true, 1.0f, 2.0f, 4.0);
        final ComplexStruct complexStruct = new ComplexStruct();
        complexStruct.setSimpleStruct(simpleStruct);
        complexStruct.setArray(new long[] {(long)1 });
        complexStruct.setDynamicBitField(BigInteger.TEN);
        final ParameterizedStruct parameterizedStruct =  new ParameterizedStruct(simpleStruct);
        complexStruct.setParamStructArray(new ParameterizedStruct[] {parameterizedStruct});

        assertEquals("with_type_info_code.ComplexStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());
        assertEquals(ComplexStruct.class, typeInfo.getJavaClass());

        assertEquals(0, typeInfo.getParameters().size());
        final List<FunctionInfo> functions = typeInfo.getFunctions();
        assertEquals(1, functions.size());

        final FunctionInfo function0 = functions.get(0);
        assertEquals("firstArrayElement", function0.getSchemaName());
        assertEquals("uint32", function0.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, function0.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, function0.getTypeInfo().getJavaType());
        assertEquals(long.class, function0.getTypeInfo().getJavaClass());
        assertEquals(32, function0.getTypeInfo().getBitSize());
        assertEquals((long)1, function0.getFunctionResult().apply(complexStruct));

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(13, fields.size());

        // simpleStruct
        final FieldInfo simpleStructField = fields.get(0);
        assertEquals("simpleStruct", simpleStructField.getSchemaName());
        assertEquals("getSimpleStruct", simpleStructField.getGetterName());
        assertEquals("setSimpleStruct", simpleStructField.getSetterName());

        checkSimpleStruct(simpleStructField.getTypeInfo());

        assertEquals(0, simpleStructField.getTypeArguments().size());
        assertEquals(null, simpleStructField.getAlignment());
        assertEquals(null, simpleStructField.getOffset());
        assertEquals(null, simpleStructField.getInitializer());
        assertFalse(simpleStructField.isOptional());
        assertEquals(null, simpleStructField.getOptionalCondition());
        assertEquals("", simpleStructField.getIsUsedIndicatorName());
        assertEquals("", simpleStructField.getIsSetIndicatorName());
        assertEquals(null, simpleStructField.getConstraint());
        assertFalse(simpleStructField.isArray());
        assertEquals(null, simpleStructField.getArrayLength());
        assertFalse(simpleStructField.isPacked());
        assertFalse(simpleStructField.isImplicit());

        // anotherSimpleStruct
        final FieldInfo anotherSimpleStructField = fields.get(1);
        assertEquals("anotherSimpleStruct", anotherSimpleStructField.getSchemaName());
        assertEquals("getAnotherSimpleStruct", anotherSimpleStructField.getGetterName());
        assertEquals("setAnotherSimpleStruct", anotherSimpleStructField.getSetterName());

        checkSimpleStruct(anotherSimpleStructField.getTypeInfo());

        assertEquals(0, anotherSimpleStructField.getTypeArguments().size());
        assertEquals(null, anotherSimpleStructField.getAlignment());
        assertEquals(null, anotherSimpleStructField.getOffset());
        assertEquals(null, anotherSimpleStructField.getInitializer());
        assertFalse(anotherSimpleStructField.isOptional());
        assertEquals(null, anotherSimpleStructField.getOptionalCondition());
        assertEquals("", anotherSimpleStructField.getIsUsedIndicatorName());
        assertEquals("", anotherSimpleStructField.getIsSetIndicatorName());
        assertEquals(null, anotherSimpleStructField.getConstraint());
        assertFalse(anotherSimpleStructField.isArray());
        assertEquals(null, anotherSimpleStructField.getArrayLength());
        assertFalse(anotherSimpleStructField.isPacked());
        assertFalse(anotherSimpleStructField.isImplicit());

        // optionalSimpleStruct
        final FieldInfo optionalSimpleStructField = fields.get(2);
        assertEquals("optionalSimpleStruct", optionalSimpleStructField.getSchemaName());
        assertEquals("getOptionalSimpleStruct", optionalSimpleStructField.getGetterName());
        assertEquals("setOptionalSimpleStruct", optionalSimpleStructField.getSetterName());

        checkSimpleStruct(optionalSimpleStructField.getTypeInfo());

        assertEquals(0, optionalSimpleStructField.getTypeArguments().size());
        assertEquals(null, optionalSimpleStructField.getAlignment());
        assertEquals(null, optionalSimpleStructField.getOffset());
        assertEquals(null, optionalSimpleStructField.getInitializer());
        assertTrue(optionalSimpleStructField.isOptional());
        assertEquals(null, optionalSimpleStructField.getOptionalCondition());
        assertEquals("isOptionalSimpleStructUsed", optionalSimpleStructField.getIsUsedIndicatorName());
        assertEquals("isOptionalSimpleStructSet", optionalSimpleStructField.getIsSetIndicatorName());
        assertEquals(null, optionalSimpleStructField.getConstraint());
        assertFalse(optionalSimpleStructField.isArray());
        assertEquals(null, optionalSimpleStructField.getArrayLength());
        assertFalse(optionalSimpleStructField.isPacked());
        assertFalse(optionalSimpleStructField.isImplicit());

        // array
        final FieldInfo arrayField = fields.get(3);
        assertEquals("array", arrayField.getSchemaName());
        assertEquals("getArray", arrayField.getGetterName());
        assertEquals("setArray", arrayField.getSetterName());

        assertEquals("uint32", arrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, arrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, arrayField.getTypeInfo().getJavaType());
        assertEquals(long.class, arrayField.getTypeInfo().getJavaClass());
        assertEquals(32, arrayField.getTypeInfo().getBitSize());

        assertEquals(0, arrayField.getTypeArguments().size());
        assertEquals(null, arrayField.getAlignment());
        assertEquals(null, arrayField.getOffset());
        assertEquals(null, arrayField.getInitializer());
        assertFalse(arrayField.isOptional());
        assertEquals(null, arrayField.getOptionalCondition());
        assertEquals("", arrayField.getIsUsedIndicatorName());
        assertEquals("", arrayField.getIsSetIndicatorName());
        assertEquals(true, arrayField.getConstraint().test(complexStruct));
        assertTrue(arrayField.isArray());
        assertEquals(null, arrayField.getArrayLength());
        assertFalse(arrayField.isPacked());
        assertFalse(arrayField.isImplicit());

        // arrayWithLen
        final FieldInfo arrayWithLenField = fields.get(4);
        assertEquals("arrayWithLen", arrayWithLenField.getSchemaName());
        assertEquals("getArrayWithLen", arrayWithLenField.getGetterName());
        assertEquals("setArrayWithLen", arrayWithLenField.getSetterName());

        assertEquals("int:5", arrayWithLenField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.FIXED_SIGNED_BITFIELD, arrayWithLenField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BYTE, arrayWithLenField.getTypeInfo().getJavaType());
        assertEquals(byte.class, arrayWithLenField.getTypeInfo().getJavaClass());
        assertEquals(5, arrayWithLenField.getTypeInfo().getBitSize());

        assertEquals(0, arrayWithLenField.getTypeArguments().size());
        assertEquals(null, arrayWithLenField.getAlignment());
        assertEquals(null, arrayWithLenField.getOffset());
        assertEquals(null, arrayWithLenField.getInitializer());
        assertTrue(arrayWithLenField.isOptional());
        assertEquals(true, arrayWithLenField.getOptionalCondition().test(complexStruct));
        assertEquals("isArrayWithLenUsed", arrayWithLenField.getIsUsedIndicatorName());
        assertEquals("isArrayWithLenSet", arrayWithLenField.getIsSetIndicatorName());
        assertEquals(null, arrayWithLenField.getConstraint());
        assertTrue(arrayWithLenField.isArray());
        assertEquals((long)1, arrayWithLenField.getArrayLength().applyAsInt(complexStruct));
        assertFalse(arrayWithLenField.isPacked());
        assertFalse(arrayWithLenField.isImplicit());

        // paramStructArray
        final FieldInfo paramStructArrayField = fields.get(5);
        assertEquals("paramStructArray", paramStructArrayField.getSchemaName());
        assertEquals("getParamStructArray", paramStructArrayField.getGetterName());
        assertEquals("setParamStructArray", paramStructArrayField.getSetterName());

        checkParameterizedStruct(paramStructArrayField.getTypeInfo());

        assertEquals(1, paramStructArrayField.getTypeArguments().size());
        assertEquals(complexStruct.getSimpleStruct(),
                paramStructArrayField.getTypeArguments().get(0).apply(complexStruct, 0));
        assertEquals(null, paramStructArrayField.getAlignment());
        assertEquals(null, paramStructArrayField.getOffset());
        assertEquals(null, paramStructArrayField.getInitializer());
        assertTrue(paramStructArrayField.isOptional());
        assertEquals(null, paramStructArrayField.getOptionalCondition());
        assertEquals("isParamStructArrayUsed", paramStructArrayField.getIsUsedIndicatorName());
        assertEquals("isParamStructArraySet", paramStructArrayField.getIsSetIndicatorName());
        assertEquals(null, paramStructArrayField.getConstraint());
        assertTrue(paramStructArrayField.isArray());
        assertEquals(null, paramStructArrayField.getArrayLength());
        assertFalse(paramStructArrayField.isPacked());
        assertFalse(paramStructArrayField.isImplicit());

        // dynamicBitField
        final FieldInfo dynamicBitFieldField = fields.get(6);
        assertEquals("dynamicBitField", dynamicBitFieldField.getSchemaName());
        assertEquals("getDynamicBitField", dynamicBitFieldField.getGetterName());
        assertEquals("setDynamicBitField", dynamicBitFieldField.getSetterName());

        assertEquals("bit<>", dynamicBitFieldField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.DYNAMIC_UNSIGNED_BITFIELD, dynamicBitFieldField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIG_INTEGER, dynamicBitFieldField.getTypeInfo().getJavaType());
        assertEquals(BigInteger.class, dynamicBitFieldField.getTypeInfo().getJavaClass());

        assertEquals(1, dynamicBitFieldField.getTypeArguments().size());
        assertEquals((long)0, dynamicBitFieldField.getTypeArguments().get(0).apply(complexStruct, null));
        assertEquals(null, dynamicBitFieldField.getAlignment());
        assertEquals(null, dynamicBitFieldField.getOffset());
        assertEquals(null, dynamicBitFieldField.getInitializer());
        assertFalse(dynamicBitFieldField.isOptional());
        assertEquals(null, dynamicBitFieldField.getOptionalCondition());
        assertEquals("", dynamicBitFieldField.getIsUsedIndicatorName());
        assertEquals("", dynamicBitFieldField.getIsSetIndicatorName());
        assertEquals(null, dynamicBitFieldField.getConstraint());
        assertFalse(dynamicBitFieldField.isArray());
        assertEquals(null, dynamicBitFieldField.getArrayLength());
        assertFalse(dynamicBitFieldField.isPacked());
        assertFalse(dynamicBitFieldField.isImplicit());

        // dynamicBitFieldArray
        final FieldInfo dynamicBitFieldArrayField = fields.get(7);
        assertEquals("dynamicBitFieldArray", dynamicBitFieldArrayField.getSchemaName());
        assertEquals("getDynamicBitFieldArray", dynamicBitFieldArrayField.getGetterName());
        assertEquals("setDynamicBitFieldArray", dynamicBitFieldArrayField.getSetterName());

        assertEquals("bit<>", dynamicBitFieldArrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.DYNAMIC_UNSIGNED_BITFIELD,
                dynamicBitFieldArrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIG_INTEGER, dynamicBitFieldArrayField.getTypeInfo().getJavaType());
        assertEquals(BigInteger.class, dynamicBitFieldArrayField.getTypeInfo().getJavaClass());

        assertEquals(1, dynamicBitFieldArrayField.getTypeArguments().size());
        assertEquals((long)20, dynamicBitFieldArrayField.getTypeArguments().get(0).apply(complexStruct, null));
        assertEquals(null, dynamicBitFieldArrayField.getAlignment());
        assertEquals(null, dynamicBitFieldArrayField.getOffset());
        assertEquals(null, dynamicBitFieldArrayField.getInitializer());
        assertFalse(dynamicBitFieldArrayField.isOptional());
        assertEquals(null, dynamicBitFieldArrayField.getOptionalCondition());
        assertEquals("", dynamicBitFieldArrayField.getIsUsedIndicatorName());
        assertEquals("", dynamicBitFieldArrayField.getIsSetIndicatorName());
        assertEquals(null, dynamicBitFieldArrayField.getConstraint());
        assertTrue(dynamicBitFieldArrayField.isArray());
        assertEquals(null, dynamicBitFieldArrayField.getArrayLength());
        assertTrue(dynamicBitFieldArrayField.isPacked());
        assertFalse(dynamicBitFieldArrayField.isImplicit());

        // optionalEnum
        final FieldInfo optionalEnumField = fields.get(8);
        assertEquals("optionalEnum", optionalEnumField.getSchemaName());
        assertEquals("getOptionalEnum", optionalEnumField.getGetterName());
        assertEquals("setOptionalEnum", optionalEnumField.getSetterName());

        checkTestEnum(optionalEnumField.getTypeInfo());

        assertEquals(0, optionalEnumField.getTypeArguments().size());
        assertEquals(null, optionalEnumField.getAlignment());
        assertEquals(null, optionalEnumField.getOffset());
        assertEquals(null, optionalEnumField.getInitializer());
        assertTrue(optionalEnumField.isOptional());
        assertEquals(null, optionalEnumField.getOptionalCondition());
        assertEquals("isOptionalEnumUsed", optionalEnumField.getIsUsedIndicatorName());
        assertEquals("isOptionalEnumSet", optionalEnumField.getIsSetIndicatorName());
        assertEquals(null, optionalEnumField.getConstraint());
        assertFalse(optionalEnumField.isArray());
        assertEquals(null, optionalEnumField.getArrayLength());
        assertFalse(optionalEnumField.isPacked());
        assertFalse(optionalEnumField.isImplicit());

        // optionalBitmask
        final FieldInfo optionalBitmaskField = fields.get(9);
        assertEquals("optionalBitmask", optionalBitmaskField.getSchemaName());
        assertEquals("getOptionalBitmask", optionalBitmaskField.getGetterName());
        assertEquals("setOptionalBitmask", optionalBitmaskField.getSetterName());

        checkTestBitmask(optionalBitmaskField.getTypeInfo());

        assertEquals(0, optionalBitmaskField.getTypeArguments().size());
        assertEquals(null, optionalBitmaskField.getAlignment());
        assertEquals(null, optionalBitmaskField.getOffset());
        assertEquals(null, optionalBitmaskField.getInitializer());
        assertTrue(optionalBitmaskField.isOptional());
        assertEquals(null, optionalBitmaskField.getOptionalCondition());
        assertEquals("isOptionalBitmaskUsed", optionalBitmaskField.getIsUsedIndicatorName());
        assertEquals("isOptionalBitmaskSet", optionalBitmaskField.getIsSetIndicatorName());
        assertEquals(null, optionalBitmaskField.getConstraint());
        assertFalse(optionalBitmaskField.isArray());
        assertEquals(null, optionalBitmaskField.getArrayLength());
        assertFalse(optionalBitmaskField.isPacked());
        assertFalse(optionalBitmaskField.isImplicit());

        // optionalExtern
        final FieldInfo optionalExternField = fields.get(10);
        assertEquals("optionalExtern", optionalExternField.getSchemaName());
        assertEquals("getOptionalExtern", optionalExternField.getGetterName());
        assertEquals("setOptionalExtern", optionalExternField.getSetterName());

        assertEquals("extern", optionalExternField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.EXTERN, optionalExternField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BIT_BUFFER, optionalExternField.getTypeInfo().getJavaType());
        assertEquals(BitBuffer.class, optionalExternField.getTypeInfo().getJavaClass());

        assertEquals(0, optionalExternField.getTypeArguments().size());
        assertEquals(null, optionalExternField.getAlignment());
        assertEquals(null, optionalExternField.getOffset());
        assertEquals(null, optionalExternField.getInitializer());
        assertTrue(optionalExternField.isOptional());
        assertEquals(null, optionalExternField.getOptionalCondition());
        assertEquals("isOptionalExternUsed", optionalExternField.getIsUsedIndicatorName());
        assertEquals("isOptionalExternSet", optionalExternField.getIsSetIndicatorName());
        assertEquals(null, optionalExternField.getConstraint());
        assertFalse(optionalExternField.isArray());
        assertEquals(null, optionalExternField.getArrayLength());
        assertFalse(optionalExternField.isPacked());
        assertFalse(optionalExternField.isImplicit());

        // enumArray
        final FieldInfo enumArrayField = fields.get(11);
        assertEquals("enumArray", enumArrayField.getSchemaName());
        assertEquals("getEnumArray", enumArrayField.getGetterName());
        assertEquals("setEnumArray", enumArrayField.getSetterName());

        checkTestEnum(enumArrayField.getTypeInfo());

        assertEquals(0, enumArrayField.getTypeArguments().size());
        assertEquals(null, enumArrayField.getAlignment());
        assertEquals(null, enumArrayField.getOffset());
        assertEquals(null, enumArrayField.getInitializer());
        assertFalse(enumArrayField.isOptional());
        assertEquals(null, enumArrayField.getOptionalCondition());
        assertEquals("", enumArrayField.getIsUsedIndicatorName());
        assertEquals("", enumArrayField.getIsSetIndicatorName());
        assertEquals(null, enumArrayField.getConstraint());
        assertTrue(enumArrayField.isArray());
        assertEquals(2, enumArrayField.getArrayLength().applyAsInt(complexStruct));
        assertFalse(enumArrayField.isPacked());
        assertFalse(enumArrayField.isImplicit());

        // bitmaskArray
        final FieldInfo bitmaskArrayField = fields.get(12);
        assertEquals("bitmaskArray", bitmaskArrayField.getSchemaName());
        assertEquals("getBitmaskArray", bitmaskArrayField.getGetterName());
        assertEquals("setBitmaskArray", bitmaskArrayField.getSetterName());

        checkTestBitmask(bitmaskArrayField.getTypeInfo());

        assertEquals(0, bitmaskArrayField.getTypeArguments().size());
        assertEquals(null, bitmaskArrayField.getAlignment());
        assertEquals(null, bitmaskArrayField.getOffset());
        assertEquals(null, bitmaskArrayField.getInitializer());
        assertFalse(bitmaskArrayField.isOptional());
        assertEquals(null, bitmaskArrayField.getOptionalCondition());
        assertEquals("", bitmaskArrayField.getIsUsedIndicatorName());
        assertEquals("", bitmaskArrayField.getIsSetIndicatorName());
        assertEquals(null, bitmaskArrayField.getConstraint());
        assertTrue(bitmaskArrayField.isArray());
        assertEquals(5, bitmaskArrayField.getArrayLength().applyAsInt(complexStruct));
        assertFalse(bitmaskArrayField.isPacked());
        assertFalse(bitmaskArrayField.isImplicit());
    }

    private void checkParameterizedStruct(TypeInfo typeInfo)
    {
        final SimpleStruct simpleStruct = new SimpleStruct((long)0, (long)10, "Text", true, 1.0f, 2.0f, 4.0);
        final ParameterizedStruct parameterizedStruct =  new ParameterizedStruct(simpleStruct);

        assertEquals("with_type_info_code.ParameterizedStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());
        assertEquals(ParameterizedStruct.class, typeInfo.getJavaClass());

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
        assertEquals("getArray", arrayField.getGetterName());
        assertEquals("setArray", arrayField.getSetterName());

        assertEquals("uint8", arrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT8, arrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.SHORT, arrayField.getTypeInfo().getJavaType());
        assertEquals(short.class, arrayField.getTypeInfo().getJavaClass());
        assertEquals(8, arrayField.getTypeInfo().getBitSize());

        assertEquals(0, arrayField.getTypeArguments().size());
        assertEquals(null, arrayField.getAlignment());
        assertEquals(null, arrayField.getOffset());
        assertEquals(null, arrayField.getInitializer());
        assertFalse(arrayField.isOptional());
        assertEquals(null, arrayField.getOptionalCondition());
        assertEquals("", arrayField.getIsUsedIndicatorName());
        assertEquals("", arrayField.getIsSetIndicatorName());
        assertEquals(null, arrayField.getConstraint());
        assertTrue(arrayField.isArray());
        assertEquals((long)0, arrayField.getArrayLength().applyAsInt(parameterizedStruct));
        assertFalse(arrayField.isPacked());
        assertFalse(arrayField.isImplicit());
    }

    private void checkRecursiveStruct(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.RecursiveStruct", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());
        assertEquals(RecursiveStruct.class, typeInfo.getJavaClass());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(3, fields.size());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(0);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());
        assertEquals("getFieldU32", fieldU32Field.getGetterName());
        assertEquals("setFieldU32", fieldU32Field.getSetterName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(long.class, fieldU32Field.getTypeInfo().getJavaClass());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals(null, fieldU32Field.getAlignment());
        assertEquals(null, fieldU32Field.getOffset());
        assertEquals(null, fieldU32Field.getInitializer());
        assertFalse(fieldU32Field.isOptional());
        assertEquals(null, fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getIsUsedIndicatorName());
        assertEquals("", fieldU32Field.getIsSetIndicatorName());
        assertEquals(null, fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals(null, fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());

        // fieldRecursion
        final FieldInfo fieldRecursion = fields.get(1);
        assertEquals("fieldRecursion", fieldRecursion.getSchemaName());
        assertEquals("getFieldRecursion", fieldRecursion.getGetterName());
        assertEquals("setFieldRecursion", fieldRecursion.getSetterName());

        assertEquals(typeInfo.getSchemaName(), fieldRecursion.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), fieldRecursion.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), fieldRecursion.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getJavaClass(), fieldRecursion.getTypeInfo().getJavaClass());
        assertEquals(typeInfo.getFields().size(), fieldRecursion.getTypeInfo().getFields().size());

        assertEquals(0, fieldRecursion.getTypeArguments().size());
        assertEquals(null, fieldRecursion.getAlignment());
        assertEquals(null, fieldRecursion.getOffset());
        assertEquals(null, fieldRecursion.getInitializer());
        assertTrue(fieldRecursion.isOptional());
        assertEquals(null, fieldRecursion.getOptionalCondition());
        assertEquals("isFieldRecursionUsed", fieldRecursion.getIsUsedIndicatorName());
        assertEquals("isFieldRecursionSet", fieldRecursion.getIsSetIndicatorName());
        assertEquals(null, fieldRecursion.getConstraint());
        assertFalse(fieldRecursion.isArray());
        assertEquals(null, fieldRecursion.getArrayLength());
        assertFalse(fieldRecursion.isPacked());
        assertFalse(fieldRecursion.isImplicit());

        // arrayRecursion
        final FieldInfo arrayRecursion = fields.get(2);
        assertEquals("arrayRecursion", arrayRecursion.getSchemaName());
        assertEquals("getArrayRecursion", arrayRecursion.getGetterName());
        assertEquals("setArrayRecursion", arrayRecursion.getSetterName());

        assertEquals(typeInfo.getSchemaName(), arrayRecursion.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), arrayRecursion.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), arrayRecursion.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getJavaClass(), arrayRecursion.getTypeInfo().getJavaClass());
        assertEquals(typeInfo.getFields().size(), arrayRecursion.getTypeInfo().getFields().size());

        assertEquals(0, arrayRecursion.getTypeArguments().size());
        assertEquals(null, arrayRecursion.getAlignment());
        assertEquals(null, arrayRecursion.getOffset());
        assertEquals(null, arrayRecursion.getInitializer());
        assertFalse(arrayRecursion.isOptional());
        assertEquals(null, arrayRecursion.getOptionalCondition());
        assertEquals("", arrayRecursion.getIsUsedIndicatorName());
        assertEquals("", arrayRecursion.getIsSetIndicatorName());
        assertEquals(null, arrayRecursion.getConstraint());
        assertTrue(arrayRecursion.isArray());
        assertEquals(null, arrayRecursion.getArrayLength());
        assertFalse(arrayRecursion.isPacked());
        assertFalse(arrayRecursion.isImplicit());
    }

    private void checkRecursiveUnion(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.RecursiveUnion", typeInfo.getSchemaName());
        assertEquals(SchemaType.UNION, typeInfo.getSchemaType());
        assertEquals(JavaType.UNION, typeInfo.getJavaType());
        assertEquals(RecursiveUnion.class, typeInfo.getJavaClass());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(0);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());
        assertEquals("getFieldU32", fieldU32Field.getGetterName());
        assertEquals("setFieldU32", fieldU32Field.getSetterName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(long.class, fieldU32Field.getTypeInfo().getJavaClass());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals(null, fieldU32Field.getAlignment());
        assertEquals(null, fieldU32Field.getOffset());
        assertEquals(null, fieldU32Field.getInitializer());
        assertFalse(fieldU32Field.isOptional());
        assertEquals(null, fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getIsUsedIndicatorName());
        assertEquals("", fieldU32Field.getIsSetIndicatorName());
        assertEquals(null, fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals(null, fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());

        // recursive
        final FieldInfo recursive = fields.get(1);
        assertEquals("recursive", recursive.getSchemaName());
        assertEquals("getRecursive", recursive.getGetterName());
        assertEquals("setRecursive", recursive.getSetterName());

        assertEquals(typeInfo.getSchemaName(), recursive.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), recursive.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), recursive.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getJavaClass(), recursive.getTypeInfo().getJavaClass());
        assertEquals(typeInfo.getFields().size(), recursive.getTypeInfo().getFields().size());

        assertEquals(0, recursive.getTypeArguments().size());
        assertEquals(null, recursive.getAlignment());
        assertEquals(null, recursive.getOffset());
        assertEquals(null, recursive.getInitializer());
        assertFalse(recursive.isOptional());
        assertEquals(null, recursive.getOptionalCondition());
        assertEquals("", recursive.getIsUsedIndicatorName());
        assertEquals("", recursive.getIsSetIndicatorName());
        assertEquals(null, recursive.getConstraint());
        assertTrue(recursive.isArray());
        assertEquals(null, recursive.getArrayLength());
        assertFalse(recursive.isPacked());
        assertFalse(recursive.isImplicit());
    }

    private void checkRecursiveChoice(TypeInfo typeInfo)
    {
        final RecursiveChoice recursiveChoice = new RecursiveChoice(true, false);
        recursiveChoice.setRecursive(new RecursiveChoice[] {new RecursiveChoice(false, false)});

        assertEquals("with_type_info_code.RecursiveChoice", typeInfo.getSchemaName());
        assertEquals(SchemaType.CHOICE, typeInfo.getSchemaType());
        assertEquals(JavaType.CHOICE, typeInfo.getJavaType());
        assertEquals(RecursiveChoice.class, typeInfo.getJavaClass());

        final List<ParameterInfo> parameters = typeInfo.getParameters();
        assertEquals(2, parameters.size());

        // param1
        final ParameterInfo param1 = parameters.get(0);
        assertEquals("param1", param1.getSchemaName());
        assertEquals("bool", param1.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.BOOL, param1.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BOOLEAN, param1.getTypeInfo().getJavaType());
        assertEquals(boolean.class, param1.getTypeInfo().getJavaClass());
        assertEquals(1, param1.getTypeInfo().getBitSize());

        // param2
        final ParameterInfo param2 = parameters.get(1);
        assertEquals("param2", param2.getSchemaName());
        assertEquals("bool", param2.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.BOOL, param2.getTypeInfo().getSchemaType());
        assertEquals(JavaType.BOOLEAN, param2.getTypeInfo().getJavaType());
        assertEquals(boolean.class, param2.getTypeInfo().getJavaClass());
        assertEquals(1, param2.getTypeInfo().getBitSize());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // recursive
        final FieldInfo recursive = fields.get(0);
        assertEquals("recursive", recursive.getSchemaName());
        assertEquals("getRecursive", recursive.getGetterName());
        assertEquals("setRecursive", recursive.getSetterName());

        assertEquals(typeInfo.getSchemaName(), recursive.getTypeInfo().getSchemaName());
        assertEquals(typeInfo.getSchemaType(), recursive.getTypeInfo().getSchemaType());
        assertEquals(typeInfo.getJavaType(), recursive.getTypeInfo().getJavaType());
        assertEquals(typeInfo.getJavaClass(), recursive.getTypeInfo().getJavaClass());
        assertEquals(typeInfo.getFields().size(), recursive.getTypeInfo().getFields().size());

        assertEquals(2, recursive.getTypeArguments().size());
        assertEquals(false, recursive.getTypeArguments().get(0).apply(recursiveChoice, null));
        assertEquals(false, recursive.getTypeArguments().get(1).apply(recursiveChoice, null));
        assertEquals(null, recursive.getAlignment());
        assertEquals(null, recursive.getOffset());
        assertEquals(null, recursive.getInitializer());
        assertFalse(recursive.isOptional());
        assertEquals(null, recursive.getOptionalCondition());
        assertEquals("", recursive.getIsUsedIndicatorName());
        assertEquals("", recursive.getIsSetIndicatorName());
        assertEquals(null, recursive.getConstraint());
        assertTrue(recursive.isArray());
        assertEquals(null, recursive.getArrayLength());
        assertFalse(recursive.isPacked());
        assertFalse(recursive.isImplicit());

        // fieldU32
        final FieldInfo fieldU32Field = fields.get(1);
        assertEquals("fieldU32", fieldU32Field.getSchemaName());
        assertEquals("getFieldU32", fieldU32Field.getGetterName());
        assertEquals("setFieldU32", fieldU32Field.getSetterName());

        assertEquals("uint32", fieldU32Field.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldU32Field.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldU32Field.getTypeInfo().getJavaType());
        assertEquals(long.class, fieldU32Field.getTypeInfo().getJavaClass());
        assertEquals(32, fieldU32Field.getTypeInfo().getBitSize());

        assertEquals(0, fieldU32Field.getTypeArguments().size());
        assertEquals(null, fieldU32Field.getAlignment());
        assertEquals(null, fieldU32Field.getOffset());
        assertEquals(null, fieldU32Field.getInitializer());
        assertFalse(fieldU32Field.isOptional());
        assertEquals(null, fieldU32Field.getOptionalCondition());
        assertEquals("", fieldU32Field.getIsUsedIndicatorName());
        assertEquals("", fieldU32Field.getIsSetIndicatorName());
        assertEquals(null, fieldU32Field.getConstraint());
        assertFalse(fieldU32Field.isArray());
        assertEquals(null, fieldU32Field.getArrayLength());
        assertFalse(fieldU32Field.isPacked());
        assertFalse(fieldU32Field.isImplicit());
    }

    private void checkTestEnum(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.TestEnum", typeInfo.getSchemaName());
        assertEquals(SchemaType.ENUM, typeInfo.getSchemaType());
        assertEquals(JavaType.ENUM, typeInfo.getJavaType());
        assertEquals(TestEnum.class, typeInfo.getJavaClass());

        assertEquals("uint16", typeInfo.getUnderlyingType().getSchemaName());
        assertEquals(SchemaType.UINT16, typeInfo.getUnderlyingType().getSchemaType());
        assertEquals(JavaType.INT, typeInfo.getUnderlyingType().getJavaType());
        assertEquals(int.class, typeInfo.getUnderlyingType().getJavaClass());
        assertEquals(16, typeInfo.getUnderlyingType().getBitSize());
        assertEquals(0, typeInfo.getUnderlyingTypeArguments().size());

        final List<ItemInfo> items = typeInfo.getEnumItems();
        assertEquals(3, items.size());

        // One
        final ItemInfo OneItem = items.get(0);
        assertEquals("One", OneItem.getSchemaName());
        assertEquals(0, OneItem.getValue().get().intValue());

        // TWO
        final ItemInfo TwoItem = items.get(1);
        assertEquals("TWO", TwoItem.getSchemaName());
        assertEquals(5, TwoItem.getValue().get().intValue());

        // ItemThree
        final ItemInfo ItemThreeItem = items.get(2);
        assertEquals("ItemThree", ItemThreeItem.getSchemaName());
        assertEquals(6, ItemThreeItem.getValue().get().intValue());
    }

    private void checkSimpleChoice(TypeInfo typeInfo)
    {
        final SimpleStruct simpleStruct = new SimpleStruct((long)0, (long)10, "Text", true, 1.0f, 2.0f, 4.0);
        final SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setSimpleStruct(simpleStruct);
        final SimpleChoice simpleChoice = new SimpleChoice(TestEnum.TWO);
        simpleChoice.setFieldTwo(simpleUnion);

        assertEquals("with_type_info_code.SimpleChoice", typeInfo.getSchemaName());
        assertEquals(SchemaType.CHOICE, typeInfo.getSchemaType());
        assertEquals(JavaType.CHOICE, typeInfo.getJavaType());
        assertEquals(SimpleChoice.class, typeInfo.getJavaClass());

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
        assertEquals(long.class, function0.getTypeInfo().getJavaClass());
        assertEquals(32, function0.getTypeInfo().getBitSize());
        assertEquals((long)0, function0.getFunctionResult().apply(simpleChoice));

        assertEquals("getSelector()", typeInfo.getSelector());

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // fieldTwo
        final FieldInfo fieldTwoField = fields.get(0);
        assertEquals("fieldTwo", fieldTwoField.getSchemaName());
        assertEquals("getFieldTwo", fieldTwoField.getGetterName());
        assertEquals("setFieldTwo", fieldTwoField.getSetterName());

        checkSimpleUnion(fieldTwoField.getTypeInfo());

        assertEquals(0, fieldTwoField.getTypeArguments().size());
        assertEquals(null, fieldTwoField.getAlignment());
        assertEquals(null, fieldTwoField.getOffset());
        assertEquals(null, fieldTwoField.getInitializer());
        assertFalse(fieldTwoField.isOptional());
        assertEquals(null, fieldTwoField.getOptionalCondition());
        assertEquals("", fieldTwoField.getIsUsedIndicatorName());
        assertEquals("", fieldTwoField.getIsSetIndicatorName());
        assertEquals(null, fieldTwoField.getConstraint());
        assertFalse(fieldTwoField.isArray());
        assertEquals(null, fieldTwoField.getArrayLength());
        assertFalse(fieldTwoField.isPacked());
        assertFalse(fieldTwoField.isImplicit());

        // fieldDefault
        final FieldInfo fieldDefaultField = fields.get(1);
        assertEquals("fieldDefault", fieldDefaultField.getSchemaName());
        assertEquals("getFieldDefault", fieldDefaultField.getGetterName());
        assertEquals("setFieldDefault", fieldDefaultField.getSetterName());

        assertEquals("string", fieldDefaultField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.STRING, fieldDefaultField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.STRING, fieldDefaultField.getTypeInfo().getJavaType());
        assertEquals(String.class, fieldDefaultField.getTypeInfo().getJavaClass());

        assertEquals(0, fieldDefaultField.getTypeArguments().size());
        assertEquals(null, fieldDefaultField.getAlignment());
        assertEquals(null, fieldDefaultField.getOffset());
        assertEquals(null, fieldDefaultField.getInitializer());
        assertFalse(fieldDefaultField.isOptional());
        assertEquals(null, fieldDefaultField.getOptionalCondition());
        assertEquals("", fieldDefaultField.getIsUsedIndicatorName());
        assertEquals("", fieldDefaultField.getIsSetIndicatorName());
        assertEquals(null, fieldDefaultField.getConstraint());
        assertFalse(fieldDefaultField.isArray());
        assertEquals(null, fieldDefaultField.getArrayLength());
        assertFalse(fieldDefaultField.isPacked());
        assertFalse(fieldDefaultField.isImplicit());

        final List<CaseInfo> cases = typeInfo.getCases();
        assertEquals(3, cases.size());

        // case One
        final CaseInfo case0 = cases.get(0);
        assertEquals(1, case0.getCaseExpressions().size());
        assertEquals(TestEnum.One, case0.getCaseExpressions().get(0).get());
        assertEquals(null, case0.getField());

        // case TWO
        final CaseInfo case1 = cases.get(1);
        assertEquals(1, case1.getCaseExpressions().size());
        assertEquals(TestEnum.TWO, case1.getCaseExpressions().get(0).get());
        assertEquals(fieldTwoField, case1.getField());

        // default
        final CaseInfo case2 = cases.get(2);
        assertEquals(0, case2.getCaseExpressions().size());
        assertEquals(fieldDefaultField, case2.getField());
    }

    private void checkSimpleUnion(TypeInfo typeInfo)
    {
        final SimpleStruct simpleStruct = new SimpleStruct((long)0, (long)10, "Text", true, 1.0f, 2.0f, 4.0);
        final SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setSimpleStruct(simpleStruct);

        assertEquals("with_type_info_code.SimpleUnion", typeInfo.getSchemaName());
        assertEquals(SchemaType.UNION, typeInfo.getSchemaType());
        assertEquals(JavaType.UNION, typeInfo.getJavaType());
        assertEquals(SimpleUnion.class, typeInfo.getJavaClass());

        assertEquals(0, typeInfo.getParameters().size());

        final List<FunctionInfo> functions = typeInfo.getFunctions();
        assertEquals(1, functions.size());

        final FunctionInfo function0 = functions.get(0);
        assertEquals("simpleStructFieldU32", function0.getSchemaName());
        assertEquals("uint32", function0.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, function0.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, function0.getTypeInfo().getJavaType());
        assertEquals(long.class, function0.getTypeInfo().getJavaClass());
        assertEquals(32, function0.getTypeInfo().getBitSize());
        assertEquals((long)0, function0.getFunctionResult().apply(simpleUnion));

        assertEquals("", typeInfo.getTemplateName());
        assertEquals(0, typeInfo.getTemplateArguments().size());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(2, fields.size());

        // testBitmask
        final FieldInfo testBitmaskField = fields.get(0);
        assertEquals("testBitmask", testBitmaskField.getSchemaName());
        assertEquals("getTestBitmask", testBitmaskField.getGetterName());
        assertEquals("setTestBitmask", testBitmaskField.getSetterName());

        checkTestBitmask(testBitmaskField.getTypeInfo());

        assertEquals(0, testBitmaskField.getTypeArguments().size());
        assertEquals(null, testBitmaskField.getAlignment());
        assertEquals(null, testBitmaskField.getOffset());
        assertEquals(null, testBitmaskField.getInitializer());
        assertFalse(testBitmaskField.isOptional());
        assertEquals(null, testBitmaskField.getOptionalCondition());
        assertEquals("", testBitmaskField.getIsUsedIndicatorName());
        assertEquals("", testBitmaskField.getIsSetIndicatorName());
        assertEquals(null, testBitmaskField.getConstraint());
        assertFalse(testBitmaskField.isArray());
        assertEquals(null, testBitmaskField.getArrayLength());
        assertFalse(testBitmaskField.isPacked());
        assertFalse(testBitmaskField.isImplicit());

        // simpleStruct
        final FieldInfo simpleStructField = fields.get(1);
        assertEquals("simpleStruct", simpleStructField.getSchemaName());
        assertEquals("getSimpleStruct", simpleStructField.getGetterName());
        assertEquals("setSimpleStruct", simpleStructField.getSetterName());

        checkSimpleStruct(simpleStructField.getTypeInfo());

        assertEquals(0, simpleStructField.getTypeArguments().size());
        assertEquals(null, simpleStructField.getAlignment());
        assertEquals(null, simpleStructField.getOffset());
        assertEquals(null, simpleStructField.getInitializer());
        assertFalse(simpleStructField.isOptional());
        assertEquals(null, simpleStructField.getOptionalCondition());
        assertEquals("", simpleStructField.getIsUsedIndicatorName());
        assertEquals("", simpleStructField.getIsSetIndicatorName());
        assertEquals(null, simpleStructField.getConstraint());
        assertFalse(simpleStructField.isArray());
        assertEquals(null, simpleStructField.getArrayLength());
        assertFalse(simpleStructField.isPacked());
        assertFalse(simpleStructField.isImplicit());
    }

    private void checkTestBitmask(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.TestBitmask", typeInfo.getSchemaName());
        assertEquals(SchemaType.BITMASK, typeInfo.getSchemaType());
        assertEquals(JavaType.BITMASK, typeInfo.getJavaType());
        assertEquals(TestBitmask.class, typeInfo.getJavaClass());

        assertEquals("bit<>", typeInfo.getUnderlyingType().getSchemaName());
        assertEquals(SchemaType.DYNAMIC_UNSIGNED_BITFIELD, typeInfo.getUnderlyingType().getSchemaType());
        assertEquals(JavaType.SHORT, typeInfo.getUnderlyingType().getJavaType());
        assertEquals(short.class, typeInfo.getUnderlyingType().getJavaClass());
        assertEquals(1, typeInfo.getUnderlyingTypeArguments().size());
        assertEquals(10, typeInfo.getUnderlyingTypeArguments().get(0).get());

        final List<ItemInfo> values = typeInfo.getBitmaskValues();
        assertEquals(3, values.size());

        // RED
        final ItemInfo redValue = values.get(0);
        assertEquals("RED", redValue.getSchemaName());
        assertEquals((short)1, redValue.getValue().get().shortValue());

        // Green
        final ItemInfo greenValue = values.get(1);
        assertEquals("Green", greenValue.getSchemaName());
        assertEquals((short)2, greenValue.getValue().get().shortValue());

        // ColorBlue
        final ItemInfo colorBlueValue = values.get(2);
        assertEquals("ColorBlue", colorBlueValue.getSchemaName());
        assertEquals((short)4, colorBlueValue.getValue().get().shortValue());
    }

    private void checkTS32(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.TS32", typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());
        assertEquals(TS32.class, typeInfo.getJavaClass());

        assertEquals(0, typeInfo.getParameters().size());
        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("with_type_info_code.TemplatedStruct", typeInfo.getTemplateName());

        assertEquals(1, typeInfo.getTemplateArguments().size());

        final TypeInfo templateArgument0 = typeInfo.getTemplateArguments().get(0);
        assertEquals("uint32", templateArgument0.getSchemaName());
        assertEquals(SchemaType.UINT32, templateArgument0.getSchemaType());
        assertEquals(JavaType.LONG, templateArgument0.getJavaType());
        assertEquals(long.class, templateArgument0.getJavaClass());
        assertEquals(32, templateArgument0.getBitSize());

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(1, fields.size());

        // field
        final FieldInfo fieldField = fields.get(0);
        assertEquals("field", fieldField.getSchemaName());
        assertEquals("getField", fieldField.getGetterName());
        assertEquals("setField", fieldField.getSetterName());

        assertEquals("uint32", fieldField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, fieldField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, fieldField.getTypeInfo().getJavaType());
        assertEquals(long.class, fieldField.getTypeInfo().getJavaClass());
        assertEquals(32, fieldField.getTypeInfo().getBitSize());

        assertEquals(0, fieldField.getTypeArguments().size());
        assertEquals(null, fieldField.getAlignment());
        assertEquals(null, fieldField.getOffset());
        assertEquals(null, fieldField.getInitializer());
        assertFalse(fieldField.isOptional());
        assertEquals(null, fieldField.getOptionalCondition());
        assertEquals("", fieldField.getIsUsedIndicatorName());
        assertEquals("", fieldField.getIsSetIndicatorName());
        assertEquals(null, fieldField.getConstraint());
        assertFalse(fieldField.isArray());
        assertEquals(null, fieldField.getArrayLength());
        assertFalse(fieldField.isPacked());
        assertFalse(fieldField.isImplicit());
    }

    private void checkTemplatedParameterizedStruct_TS32(TypeInfo typeInfo)
    {
        final TS32 ts32 = new TS32((long)11);
        final TemplatedParameterizedStruct_TS32 templatedParameterizedStruct_TS32 =
                new TemplatedParameterizedStruct_TS32(ts32);

        assertEquals("with_type_info_code.TemplatedParameterizedStruct_TS32",
                typeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, typeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, typeInfo.getJavaType());
        assertEquals(TemplatedParameterizedStruct_TS32.class, typeInfo.getJavaClass());

        assertEquals(1, typeInfo.getParameters().size());
        final ParameterInfo parameter0 = typeInfo.getParameters().get(0);
        assertEquals("param", parameter0.getSchemaName());
        checkTS32(parameter0.getTypeInfo());

        assertEquals(0, typeInfo.getFunctions().size());

        assertEquals("with_type_info_code.TemplatedParameterizedStruct", typeInfo.getTemplateName());

        assertEquals(1, typeInfo.getTemplateArguments().size());

        final TypeInfo templateArgument0 = typeInfo.getTemplateArguments().get(0);
        checkTS32(templateArgument0);

        final List<FieldInfo> fields = typeInfo.getFields();
        assertEquals(1, fields.size());

        // array
        final FieldInfo arrayField = fields.get(0);
        assertEquals("array", arrayField.getSchemaName());
        assertEquals("getArray", arrayField.getGetterName());
        assertEquals("setArray", arrayField.getSetterName());

        assertEquals("uint32", arrayField.getTypeInfo().getSchemaName());
        assertEquals(SchemaType.UINT32, arrayField.getTypeInfo().getSchemaType());
        assertEquals(JavaType.LONG, arrayField.getTypeInfo().getJavaType());
        assertEquals(long.class, arrayField.getTypeInfo().getJavaClass());
        assertEquals(32, arrayField.getTypeInfo().getBitSize());

        assertEquals(0, arrayField.getTypeArguments().size());
        assertEquals(null, arrayField.getAlignment());
        assertEquals(null, arrayField.getOffset());
        assertEquals(null, arrayField.getInitializer());
        assertFalse(arrayField.isOptional());
        assertEquals(null, arrayField.getOptionalCondition());
        assertEquals("", arrayField.getIsUsedIndicatorName());
        assertEquals("", arrayField.getIsSetIndicatorName());
        assertEquals(null, arrayField.getConstraint());
        assertTrue(arrayField.isArray());
        assertEquals((long)11, arrayField.getArrayLength().applyAsInt(templatedParameterizedStruct_TS32));
        assertFalse(arrayField.isPacked());
        assertFalse(arrayField.isImplicit());
    }

    private void checkSimplePubsub(TypeInfo typeInfo)
    {
        assertEquals("with_type_info_code.SimplePubsub", typeInfo.getSchemaName());
        assertEquals(SchemaType.PUBSUB, typeInfo.getSchemaType());
        assertEquals(JavaType.PUBSUB, typeInfo.getJavaType());
        assertEquals(SimplePubsub.class, typeInfo.getJavaClass());

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
        assertEquals("with_type_info_code.SimpleService", typeInfo.getSchemaName());
        assertEquals(SchemaType.SERVICE, typeInfo.getSchemaType());
        assertEquals(JavaType.SERVICE, typeInfo.getJavaType());
        assertEquals(SimpleService.class, typeInfo.getJavaClass());

        final List<MethodInfo> methods = typeInfo.getMethods();
        assertEquals(1, methods.size());

        // getSimpleStruct
        final MethodInfo getSimpleStructMethod = methods.get(0);
        assertEquals("getSimpleStruct", getSimpleStructMethod.getSchemaName());

        checkSimpleStruct(getSimpleStructMethod.getResponseTypeInfo());
        checkSimpleUnion(getSimpleStructMethod.getRequestTypeInfo());
    }

    private static final String BLOB_NAME_WITH_OPTIONALS = "with_type_info_code_optionals.blob";
    private static final String BLOB_NAME_WITHOUT_OPTIONALS = "with_type_info_code.blob";
}
