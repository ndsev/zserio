package zserio.runtime.typeinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import zserio.runtime.ZserioError;
import zserio.runtime.typeinfo.TypeInfo.BitmaskTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.ChoiceTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.EnumTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.PubsubTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.RecursiveTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.ServiceTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.SqlDatabaseTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.SqlTableTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.StructTypeInfo;
import zserio.runtime.typeinfo.TypeInfo.UnionTypeInfo;

public class TypeInfoTest
{
    @Test
    public void builtinTypeInfo()
    {
        checkBuiltinTypeInfo(BuiltinTypeInfo.getBool(), "bool", SchemaType.BOOL, JavaType.BOOLEAN, 1);

        checkBuiltinTypeInfo(BuiltinTypeInfo.getInt8(), "int8", SchemaType.INT8, JavaType.BYTE, 8);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getInt16(), "int16", SchemaType.INT16, JavaType.SHORT, 16);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getInt32(), "int32", SchemaType.INT32, JavaType.INT, 32);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getInt64(), "int64", SchemaType.INT64, JavaType.LONG, 64);

        checkBuiltinTypeInfo(BuiltinTypeInfo.getUInt8(), "uint8", SchemaType.UINT8, JavaType.SHORT, 8);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getUInt16(), "uint16", SchemaType.UINT16, JavaType.INT, 16);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getUInt32(), "uint32", SchemaType.UINT32, JavaType.LONG, 32);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getUInt64(),
                "uint64", SchemaType.UINT64, JavaType.BIG_INTEGER, 64);

        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarInt16(), "varint16", SchemaType.VARINT16, JavaType.SHORT);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarInt32(), "varint32", SchemaType.VARINT32, JavaType.INT);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarInt64(), "varint64", SchemaType.VARINT64, JavaType.LONG);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarInt(), "varint", SchemaType.VARINT, JavaType.LONG);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarUInt16(), "varuint16", SchemaType.VARUINT16, JavaType.SHORT);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarUInt32(), "varuint32", SchemaType.VARUINT32, JavaType.INT);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarUInt64(),
                "varuint64", SchemaType.VARUINT64, JavaType.LONG);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarUInt(),
                "varuint", SchemaType.VARUINT, JavaType.BIG_INTEGER);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getVarSize(), "varsize", SchemaType.VARSIZE, JavaType.INT);

        checkBuiltinTypeInfo(BuiltinTypeInfo.getFloat16(), "float16", SchemaType.FLOAT16, JavaType.FLOAT, 16);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getFloat32(), "float32", SchemaType.FLOAT32, JavaType.FLOAT, 32);
        checkBuiltinTypeInfo(BuiltinTypeInfo.getFloat64(), "float64", SchemaType.FLOAT64, JavaType.DOUBLE, 64);

        checkBuiltinTypeInfo(BuiltinTypeInfo.getString(), "string", SchemaType.STRING, JavaType.STRING);

        checkBuiltinTypeInfo(BuiltinTypeInfo.getBitBuffer(), "extern", SchemaType.EXTERN, JavaType.BIT_BUFFER);

        // fixed signed bit fields
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedSignedBitField((byte)0));
        short bitSize = 1;
        for (; bitSize <= 8; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedSignedBitField((byte)bitSize), "int:" + bitSize,
                    SchemaType.FIXED_SIGNED_BITFIELD, JavaType.BYTE, bitSize);
        }
        for (; bitSize <= 16; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedSignedBitField((byte)bitSize), "int:" + bitSize,
                    SchemaType.FIXED_SIGNED_BITFIELD, JavaType.SHORT, bitSize);
        }
        for (; bitSize <= 32; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedSignedBitField((byte)bitSize), "int:" + bitSize,
                    SchemaType.FIXED_SIGNED_BITFIELD, JavaType.INT, bitSize);
        }
        for (; bitSize <= 64; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedSignedBitField((byte)bitSize), "int:" + bitSize,
                    SchemaType.FIXED_SIGNED_BITFIELD, JavaType.LONG, bitSize);
        }
        for (; bitSize < 255; ++bitSize)
        {
            final byte bitSizeArg = (byte)bitSize;
            assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedSignedBitField(bitSizeArg));
        }
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedSignedBitField((byte)255));

        // fixed unsigned bit fields
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedUnsignedBitField((byte)0));
        bitSize = 0;
        for (++bitSize ; bitSize < 8; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedUnsignedBitField((byte)bitSize), "bit:" + bitSize,
                    SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.BYTE, bitSize);
        }
        for (; bitSize < 16; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedUnsignedBitField((byte)bitSize), "bit:" + bitSize,
                    SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.SHORT, bitSize);
        }
        for (; bitSize < 32; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedUnsignedBitField((byte)bitSize), "bit:" + bitSize,
                    SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.INT, bitSize);
        }
        for (; bitSize < 64; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedUnsignedBitField((byte)bitSize), "bit:" + bitSize,
                    SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.LONG, bitSize);
        }
        for (; bitSize == 64; ++bitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getFixedUnsignedBitField((byte)bitSize), "bit:" + bitSize,
                    SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.BIG_INTEGER, bitSize);
        }
        for (; bitSize < 255; ++bitSize)
        {
            byte bitSizeArg = (byte)bitSize;
            assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedUnsignedBitField(bitSizeArg));
        }
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedUnsignedBitField((byte)255));

        // dynamic signed bit fields
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getDynamicSignedBitField((byte)0));
        short maxBitSize = 1;
        for (; maxBitSize <= 8; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicSignedBitField((byte)maxBitSize), "int<>",
                    SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.BYTE);
        }
        for (; maxBitSize <= 16; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicSignedBitField((byte)maxBitSize), "int<>",
                    SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.SHORT);
        }
        for (; maxBitSize <= 32; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicSignedBitField((byte)maxBitSize), "int<>",
                    SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.INT);
        }
        for (; maxBitSize <= 64; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicSignedBitField((byte)maxBitSize), "int<>",
                    SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.LONG);
        }
        for (; maxBitSize < 255; ++maxBitSize)
        {
            final byte maxBitSizeArg = (byte)maxBitSize;
            assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getDynamicSignedBitField(maxBitSizeArg));
        }
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getDynamicSignedBitField((byte)255));

        // dynamic unsigned bit fields
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getDynamicUnsignedBitField((byte)0));
        maxBitSize = 1;
        for (; maxBitSize < 8; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicUnsignedBitField((byte)maxBitSize), "bit<>",
                    SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.BYTE);
        }
        for (; maxBitSize < 16; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicUnsignedBitField((byte)maxBitSize), "bit<>",
                    SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.SHORT);
        }
        for (; maxBitSize < 32; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicUnsignedBitField((byte)maxBitSize), "bit<>",
                    SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.INT);
        }
        for (; maxBitSize < 64; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicUnsignedBitField((byte)maxBitSize), "bit<>",
                    SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.LONG);
        }
        for (; maxBitSize == 64; ++maxBitSize)
        {
            checkBuiltinTypeInfo(BuiltinTypeInfo.getDynamicUnsignedBitField((byte)maxBitSize), "bit<>",
                    SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.BIG_INTEGER);
        }
        for (; maxBitSize < 255; ++maxBitSize)
        {
            final byte maxBitSizeArg = (byte)maxBitSize;
            assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getDynamicUnsignedBitField(maxBitSizeArg));
        }
        assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getDynamicUnsignedBitField((byte)255));
    }

    @Test
    public void structTypeInfo()
    {
        final StructTypeInfo structTypeInfo = new StructTypeInfo("", null, "", new ArrayList<TypeInfo>(),
                new ArrayList<FieldInfo>(), new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>());
        assertEquals("", structTypeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, structTypeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, structTypeInfo.getJavaType());
        assertNull(structTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> structTypeInfo.getBitSize());

        assertEquals(0, structTypeInfo.getFields().size());
        assertEquals(0, structTypeInfo.getParameters().size());
        assertEquals(0, structTypeInfo.getFunctions().size());

        assertThrows(ZserioError.class, () -> structTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> structTypeInfo.getCases());

        assertThrows(ZserioError.class, () -> structTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> structTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> structTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> structTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> structTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> structTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> structTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> structTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> structTypeInfo.getTables());

        assertEquals("", structTypeInfo.getTemplateName());
        assertEquals(0, structTypeInfo.getTemplateArguments().size());

        assertThrows(ZserioError.class, () -> structTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> structTypeInfo.getMethods());
    }

    @Test
    public void unionTypeInfo()
    {
        final UnionTypeInfo unionTypeInfo = new UnionTypeInfo("", null, "", new ArrayList<TypeInfo>(),
                new ArrayList<FieldInfo>(), new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>());
        assertEquals("", unionTypeInfo.getSchemaName());
        assertEquals(SchemaType.UNION, unionTypeInfo.getSchemaType());
        assertEquals(JavaType.UNION, unionTypeInfo.getJavaType());
        assertNull(unionTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> unionTypeInfo.getBitSize());

        assertEquals(0, unionTypeInfo.getFields().size());
        assertEquals(0, unionTypeInfo.getParameters().size());
        assertEquals(0, unionTypeInfo.getFunctions().size());

        assertThrows(ZserioError.class, () -> unionTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> unionTypeInfo.getCases());

        assertThrows(ZserioError.class, () -> unionTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> unionTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> unionTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> unionTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> unionTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> unionTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> unionTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> unionTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> unionTypeInfo.getTables());

        assertEquals("", unionTypeInfo.getTemplateName());
        assertEquals(0, unionTypeInfo.getTemplateArguments().size());

        assertThrows(ZserioError.class, () -> unionTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> unionTypeInfo.getMethods());
    }

    @Test
    public void choiceTypeInfo()
    {
        final ChoiceTypeInfo choiceTypeInfo = new ChoiceTypeInfo("", null, "", new ArrayList<TypeInfo>(),
                new ArrayList<FieldInfo>(), new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>(),
                "", new ArrayList<CaseInfo>());
        assertEquals("", choiceTypeInfo.getSchemaName());
        assertEquals(SchemaType.CHOICE, choiceTypeInfo.getSchemaType());
        assertEquals(JavaType.CHOICE, choiceTypeInfo.getJavaType());
        assertNull(choiceTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> choiceTypeInfo.getBitSize());

        assertEquals(0, choiceTypeInfo.getFields().size());
        assertEquals(0, choiceTypeInfo.getParameters().size());
        assertEquals(0, choiceTypeInfo.getFunctions().size());

        assertEquals("", choiceTypeInfo.getSelector());
        assertEquals(0, choiceTypeInfo.getCases().size());

        assertThrows(ZserioError.class, () -> choiceTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> choiceTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> choiceTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> choiceTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> choiceTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> choiceTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> choiceTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> choiceTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> choiceTypeInfo.getTables());

        assertEquals("", choiceTypeInfo.getTemplateName());
        assertEquals(0, choiceTypeInfo.getTemplateArguments().size());

        assertThrows(ZserioError.class, () -> choiceTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> choiceTypeInfo.getMethods());
    }

    @Test
    public void sqlTableTypeInfo()
    {
        final SqlTableTypeInfo sqlTableTypeInfo = new SqlTableTypeInfo("", null, "", new ArrayList<TypeInfo>(),
                new ArrayList<ColumnInfo>(), "", "", false);
        assertEquals("", sqlTableTypeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, sqlTableTypeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, sqlTableTypeInfo.getJavaType());
        assertNull(sqlTableTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getBitSize());

        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getFields());
        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getParameters());
        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getFunctions());

        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getCases());

        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getBitmaskValues());

        assertEquals(0, sqlTableTypeInfo.getColumns().size());
        assertEquals("", sqlTableTypeInfo.getSqlConstraint());
        assertEquals("", sqlTableTypeInfo.getVirtualTableUsing());
        assertEquals(false, sqlTableTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getTables());

        assertEquals("", sqlTableTypeInfo.getTemplateName());
        assertEquals(0, sqlTableTypeInfo.getTemplateArguments().size());

        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getMethods());
    }

    @Test
    public void sqlDatabaseTypeInfo()
    {
        final SqlDatabaseTypeInfo sqlDatabaseTypeInfo = new SqlDatabaseTypeInfo("", null,
                new ArrayList<TableInfo>());
        assertEquals("", sqlDatabaseTypeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_DATABASE, sqlDatabaseTypeInfo.getSchemaType());
        assertEquals(JavaType.SQL_DATABASE, sqlDatabaseTypeInfo.getJavaType());
        assertNull(sqlDatabaseTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getBitSize());

        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getFields());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getParameters());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getFunctions());

        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getCases());

        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.isWithoutRowId());

        assertEquals(0, sqlDatabaseTypeInfo.getTables().size());

        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getTemplateName());
        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getTemplateArguments());

        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getMethods());
    }

    @Test
    public void enumTypeInfo()
    {
        final TypeInfo underlyingTypeInfo = BuiltinTypeInfo.getInt8();
        final EnumTypeInfo enumTypeInfo = new EnumTypeInfo("", null,
                underlyingTypeInfo, new ArrayList<Supplier<Object>>(), new ArrayList<ItemInfo>());
        assertEquals("", enumTypeInfo.getSchemaName());
        assertEquals(SchemaType.ENUM, enumTypeInfo.getSchemaType());
        assertEquals(JavaType.ENUM, enumTypeInfo.getJavaType());
        assertNull(enumTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getBitSize());

        assertThrows(ZserioError.class, () -> enumTypeInfo.getFields());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getParameters());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getFunctions());

        assertThrows(ZserioError.class, () -> enumTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getCases());

        assertEquals(underlyingTypeInfo, enumTypeInfo.getUnderlyingType());
        assertEquals(0, enumTypeInfo.getUnderlyingTypeArguments().size());
        assertEquals(0, enumTypeInfo.getEnumItems().size());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> enumTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> enumTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> enumTypeInfo.getTables());

        assertThrows(ZserioError.class, () -> enumTypeInfo.getTemplateName());
        assertThrows(ZserioError.class, () -> enumTypeInfo.getTemplateArguments());

        assertThrows(ZserioError.class, () -> enumTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> enumTypeInfo.getMethods());
    }

    @Test
    public void bitmaskTypeInfo()
    {
        final TypeInfo underlyingTypeInfo = BuiltinTypeInfo.getInt8();
        final BitmaskTypeInfo bitmaskTypeInfo = new BitmaskTypeInfo("", null,
                underlyingTypeInfo, new ArrayList<Supplier<Object>>(), new ArrayList<ItemInfo>());
        assertEquals("", bitmaskTypeInfo.getSchemaName());
        assertEquals(SchemaType.BITMASK, bitmaskTypeInfo.getSchemaType());
        assertEquals(JavaType.BITMASK, bitmaskTypeInfo.getJavaType());
        assertNull(bitmaskTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getBitSize());

        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getFields());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getParameters());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getFunctions());

        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getCases());

        assertEquals(underlyingTypeInfo, bitmaskTypeInfo.getUnderlyingType());
        assertEquals(0, bitmaskTypeInfo.getUnderlyingTypeArguments().size());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getEnumItems());
        assertEquals(0, bitmaskTypeInfo.getBitmaskValues().size());

        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getTables());

        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getTemplateName());
        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getTemplateArguments());

        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getMethods());
    }

    @Test
    public void pubsubTypeInfo()
    {
        final PubsubTypeInfo pubsubTypeInfo = new PubsubTypeInfo("", null, new ArrayList<MessageInfo>());
        assertEquals("", pubsubTypeInfo.getSchemaName());
        assertEquals(SchemaType.PUBSUB, pubsubTypeInfo.getSchemaType());
        assertEquals(JavaType.PUBSUB, pubsubTypeInfo.getJavaType());
        assertNull(pubsubTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getBitSize());

        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getFields());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getParameters());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getFunctions());

        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getCases());

        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getTables());

        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getTemplateName());
        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getTemplateArguments());

        assertEquals(0, pubsubTypeInfo.getMessages().size());

        assertThrows(ZserioError.class, () -> pubsubTypeInfo.getMethods());
    }

    @Test
    public void serviceTypeInfo()
    {
        final ServiceTypeInfo serviceTypeInfo = new ServiceTypeInfo("", null, new ArrayList<MethodInfo>());
        assertEquals("", serviceTypeInfo.getSchemaName());
        assertEquals(SchemaType.SERVICE, serviceTypeInfo.getSchemaType());
        assertEquals(JavaType.SERVICE, serviceTypeInfo.getJavaType());
        assertNull(serviceTypeInfo.getJavaClass());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getBitSize());

        assertThrows(ZserioError.class, () -> serviceTypeInfo.getFields());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getParameters());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getFunctions());

        assertThrows(ZserioError.class, () -> serviceTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getCases());

        assertThrows(ZserioError.class, () -> serviceTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> serviceTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> serviceTypeInfo.getTables());

        assertThrows(ZserioError.class, () -> serviceTypeInfo.getTemplateName());
        assertThrows(ZserioError.class, () -> serviceTypeInfo.getTemplateArguments());

        assertThrows(ZserioError.class, () -> serviceTypeInfo.getMessages());

        assertEquals(0, serviceTypeInfo.getMethods().size());
    }

    @Test
    public void recursiveTypeInfo()
    {
        final TypeInfo typeInfo = RecursiveObject.typeInfo();
        final TypeInfo recursiveTypeInfo = typeInfo.getFields().get(0).getTypeInfo();

        assertEquals(typeInfo.getSchemaName(), recursiveTypeInfo.getSchemaName());
        assertEquals(typeInfo.getSchemaType(), recursiveTypeInfo.getSchemaType());
        assertEquals(typeInfo.getJavaType(), recursiveTypeInfo.getJavaType());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getBitSize());

        assertEquals(typeInfo.getFields().size(), recursiveTypeInfo.getFields().size());
        assertEquals(0, recursiveTypeInfo.getParameters().size());
        assertEquals(0, recursiveTypeInfo.getFunctions().size());

        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getSelector());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getCases());

        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getColumns());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> recursiveTypeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getTables());

        assertEquals(typeInfo.getTemplateName(), recursiveTypeInfo.getTemplateName());
        assertEquals(0, recursiveTypeInfo.getTemplateArguments().size());

        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getMessages());

        assertThrows(ZserioError.class, () -> recursiveTypeInfo.getMethods());
    }

    private void checkBuiltinTypeInfo(TypeInfo typeInfo,
            String schemaName, SchemaType schemaType, JavaType javaType)
    {
        checkBuiltinTypeInfo(typeInfo, schemaName, schemaType, javaType, 0);
    }

    private void checkBuiltinTypeInfo(TypeInfo typeInfo,
            String schemaName, SchemaType schemaType, JavaType javaType, int bitSize)
    {
        assertEquals(schemaName, typeInfo.getSchemaName());
        assertEquals(schemaType, typeInfo.getSchemaType());
        assertEquals(javaType, typeInfo.getJavaType());
        if (bitSize > 0)
            assertEquals((byte)bitSize, typeInfo.getBitSize());
        else
            assertThrows(ZserioError.class, () -> typeInfo.getBitSize());

        assertThrows(ZserioError.class, () -> typeInfo.getFields());
        assertThrows(ZserioError.class, () -> typeInfo.getParameters());
        assertThrows(ZserioError.class, () -> typeInfo.getFunctions());

        assertThrows(ZserioError.class, () -> typeInfo.getSelector());
        assertThrows(ZserioError.class, () -> typeInfo.getCases());

        assertThrows(ZserioError.class, () -> typeInfo.getUnderlyingType());
        assertThrows(ZserioError.class, () -> typeInfo.getUnderlyingTypeArguments());
        assertThrows(ZserioError.class, () -> typeInfo.getEnumItems());
        assertThrows(ZserioError.class, () -> typeInfo.getBitmaskValues());

        assertThrows(ZserioError.class, () -> typeInfo.getColumns());
        assertThrows(ZserioError.class, () -> typeInfo.getSqlConstraint());
        assertThrows(ZserioError.class, () -> typeInfo.getVirtualTableUsing());
        assertThrows(ZserioError.class, () -> typeInfo.isWithoutRowId());

        assertThrows(ZserioError.class, () -> typeInfo.getTables());

        assertThrows(ZserioError.class, () -> typeInfo.getTemplateName());
        assertThrows(ZserioError.class, () -> typeInfo.getTemplateArguments());

        assertThrows(ZserioError.class, () -> typeInfo.getMessages());

        assertThrows(ZserioError.class, () -> typeInfo.getMethods());
    }

    private static class RecursiveObject
    {
        public static TypeInfo typeInfo()
        {
             class RecursiveTypeInfoGetter implements RecursiveTypeInfo.TypeInfoGetter
             {
                 @Override
                 public TypeInfo get()
                 {
                     return RecursiveObject.typeInfo();
                 }
             }

            final List<FieldInfo> fields = Arrays.asList(
                    new FieldInfo(
                            "recursive", // schemaName
                            "getRecursive", // getterName
                            "setRecursive", // setterName
                            new RecursiveTypeInfo(new RecursiveTypeInfoGetter()), // typeInfo
                            new ArrayList<BiFunction<Object, Integer, Object>>(), // typeArguments
                            null, // alignment
                            null, // offset
                            null, // initializer
                            true, // isOptional
                            null, // optionalCondition
                            "isRecursiveUsed", // isUsedindicatorName
                            "isRecursiveSet", // isSetindicatorName
                            null, // constraint
                            false, // isArray
                            null, // arrayLength
                            false, // isPacked
                            false // isImplicit
                    )
            );

            return new StructTypeInfo("RecursiveObject", null, "", new ArrayList<TypeInfo>(),
                    fields, new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>());
        }
    }
}
