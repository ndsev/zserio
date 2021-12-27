package zserio.runtime.typeinfo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import test_util.AssertionUtil;
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
        AssertionUtil.assertThrows(ZserioError.class,
                () -> BuiltinTypeInfo.getFixedSignedBitField((byte)0));
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
            AssertionUtil.assertThrows(ZserioError.class,
                    () -> BuiltinTypeInfo.getFixedSignedBitField(bitSizeArg));
        }
        AssertionUtil.assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedSignedBitField((byte)255));

        // fixed unsigned bit fields
        AssertionUtil.assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getFixedUnsignedBitField((byte)0));
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
            AssertionUtil.assertThrows(ZserioError.class,
                    () -> BuiltinTypeInfo.getFixedUnsignedBitField(bitSizeArg));
        }
        AssertionUtil.assertThrows(ZserioError.class,
                () -> BuiltinTypeInfo.getFixedUnsignedBitField((byte)255));

        // dynamic signed bit fields
        AssertionUtil.assertThrows(ZserioError.class, () -> BuiltinTypeInfo.getDynamicSignedBitField((byte)0));
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
            AssertionUtil.assertThrows(ZserioError.class,
                    () -> BuiltinTypeInfo.getDynamicSignedBitField(maxBitSizeArg));
        }
        AssertionUtil.assertThrows(ZserioError.class,
                () -> BuiltinTypeInfo.getDynamicSignedBitField((byte)255));

        // dynamic unsigned bit fields
        AssertionUtil.assertThrows(ZserioError.class,
                () -> BuiltinTypeInfo.getDynamicUnsignedBitField((byte)0));
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
            AssertionUtil.assertThrows(ZserioError.class,
                    () -> BuiltinTypeInfo.getDynamicUnsignedBitField(maxBitSizeArg));
        }
        AssertionUtil.assertThrows(ZserioError.class,
                () -> BuiltinTypeInfo.getDynamicUnsignedBitField((byte)255));
    }

    @Test
    public void structTypeInfo()
    {
        final StructTypeInfo structTypeInfo = new StructTypeInfo("", "", new ArrayList<TypeInfo>(),
                new ArrayList<FieldInfo>(), new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>());
        assertEquals("", structTypeInfo.getSchemaName());
        assertEquals(SchemaType.STRUCT, structTypeInfo.getSchemaType());
        assertEquals(JavaType.STRUCT, structTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getBitSize());

        assertEquals(0, structTypeInfo.getFields().size());
        assertEquals(0, structTypeInfo.getParameters().size());
        assertEquals(0, structTypeInfo.getFunctions().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getTables());

        assertEquals("", structTypeInfo.getTemplateName());
        assertEquals(0, structTypeInfo.getTemplateArguments().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> structTypeInfo.getMethods());
    }

    @Test
    public void unionTypeInfo()
    {
        final UnionTypeInfo unionTypeInfo = new UnionTypeInfo("", "", new ArrayList<TypeInfo>(),
                new ArrayList<FieldInfo>(), new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>());
        assertEquals("", unionTypeInfo.getSchemaName());
        assertEquals(SchemaType.UNION, unionTypeInfo.getSchemaType());
        assertEquals(JavaType.UNION, unionTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getBitSize());

        assertEquals(0, unionTypeInfo.getFields().size());
        assertEquals(0, unionTypeInfo.getParameters().size());
        assertEquals(0, unionTypeInfo.getFunctions().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getTables());

        assertEquals("", unionTypeInfo.getTemplateName());
        assertEquals(0, unionTypeInfo.getTemplateArguments().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> unionTypeInfo.getMethods());
    }

    @Test
    public void choiceTypeInfo()
    {
        final ChoiceTypeInfo choiceTypeInfo = new ChoiceTypeInfo("", "", new ArrayList<TypeInfo>(),
                new ArrayList<FieldInfo>(), new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>(),
                "", new ArrayList<CaseInfo>());
        assertEquals("", choiceTypeInfo.getSchemaName());
        assertEquals(SchemaType.CHOICE, choiceTypeInfo.getSchemaType());
        assertEquals(JavaType.CHOICE, choiceTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getBitSize());

        assertEquals(0, choiceTypeInfo.getFields().size());
        assertEquals(0, choiceTypeInfo.getParameters().size());
        assertEquals(0, choiceTypeInfo.getFunctions().size());

        assertEquals("", choiceTypeInfo.getSelector());
        assertEquals(0, choiceTypeInfo.getCases().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getTables());

        assertEquals("", choiceTypeInfo.getTemplateName());
        assertEquals(0, choiceTypeInfo.getTemplateArguments().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> choiceTypeInfo.getMethods());
    }

    @Test
    public void sqlTableTypeInfo()
    {
        final SqlTableTypeInfo sqlTableTypeInfo = new SqlTableTypeInfo("", "", new ArrayList<TypeInfo>(),
                new ArrayList<ColumnInfo>(), "", "", false);
        assertEquals("", sqlTableTypeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_TABLE, sqlTableTypeInfo.getSchemaType());
        assertEquals(JavaType.SQL_TABLE, sqlTableTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getBitSize());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getFields());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getParameters());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getFunctions());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getBitmaskValues());

        assertEquals(0, sqlTableTypeInfo.getColumns().size());
        assertEquals("", sqlTableTypeInfo.getSqlConstraint());
        assertEquals("", sqlTableTypeInfo.getVirtualTableUsing());
        assertEquals(false, sqlTableTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getTables());

        assertEquals("", sqlTableTypeInfo.getTemplateName());
        assertEquals(0, sqlTableTypeInfo.getTemplateArguments().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlTableTypeInfo.getMethods());
    }

    @Test
    public void sqlDatabaseTypeInfo()
    {
        final SqlDatabaseTypeInfo sqlDatabaseTypeInfo = new SqlDatabaseTypeInfo("", new ArrayList<TableInfo>());
        assertEquals("", sqlDatabaseTypeInfo.getSchemaName());
        assertEquals(SchemaType.SQL_DATABASE, sqlDatabaseTypeInfo.getSchemaType());
        assertEquals(JavaType.SQL_DATABASE, sqlDatabaseTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getBitSize());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getFields());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getParameters());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getFunctions());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.isWithoutRowId());

        assertEquals(0, sqlDatabaseTypeInfo.getTables().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getTemplateName());
        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getTemplateArguments());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> sqlDatabaseTypeInfo.getMethods());
    }

    @Test
    public void enumTypeInfo()
    {
        final TypeInfo underlyingTypeInfo = BuiltinTypeInfo.getInt8();
        final EnumTypeInfo enumTypeInfo = new EnumTypeInfo("",
                underlyingTypeInfo, new ArrayList<String>(), new ArrayList<ItemInfo>());
        assertEquals("", enumTypeInfo.getSchemaName());
        assertEquals(SchemaType.ENUM, enumTypeInfo.getSchemaType());
        assertEquals(JavaType.ENUM, enumTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getBitSize());

        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getFields());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getParameters());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getFunctions());

        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getCases());

        assertEquals(underlyingTypeInfo, enumTypeInfo.getUnderlyingType());
        assertEquals(0, enumTypeInfo.getUnderlyingTypeArguments().size());
        assertEquals(0, enumTypeInfo.getEnumItems().size());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getTables());

        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getTemplateName());
        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getTemplateArguments());

        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> enumTypeInfo.getMethods());
    }

    @Test
    public void bitmaskTypeInfo()
    {
        final TypeInfo underlyingTypeInfo = BuiltinTypeInfo.getInt8();
        final BitmaskTypeInfo bitmaskTypeInfo = new BitmaskTypeInfo("",
                underlyingTypeInfo, new ArrayList<String>(), new ArrayList<ItemInfo>());
        assertEquals("", bitmaskTypeInfo.getSchemaName());
        assertEquals(SchemaType.BITMASK, bitmaskTypeInfo.getSchemaType());
        assertEquals(JavaType.BITMASK, bitmaskTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getBitSize());

        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getFields());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getParameters());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getFunctions());

        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getCases());

        assertEquals(underlyingTypeInfo, bitmaskTypeInfo.getUnderlyingType());
        assertEquals(0, bitmaskTypeInfo.getUnderlyingTypeArguments().size());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getEnumItems());
        assertEquals(0, bitmaskTypeInfo.getBitmaskValues().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getTables());

        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getTemplateName());
        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getTemplateArguments());

        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> bitmaskTypeInfo.getMethods());
    }

    @Test
    public void pubsubTypeInfo()
    {
        final PubsubTypeInfo pubsubTypeInfo = new PubsubTypeInfo("", new ArrayList<MessageInfo>());
        assertEquals("", pubsubTypeInfo.getSchemaName());
        assertEquals(SchemaType.PUBSUB, pubsubTypeInfo.getSchemaType());
        assertEquals(JavaType.PUBSUB, pubsubTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getBitSize());

        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getFields());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getParameters());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getFunctions());

        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getTables());

        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getTemplateName());
        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getTemplateArguments());

        assertEquals(0, pubsubTypeInfo.getMessages().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> pubsubTypeInfo.getMethods());
    }

    @Test
    public void serviceTypeInfo()
    {
        final ServiceTypeInfo serviceTypeInfo = new ServiceTypeInfo("", new ArrayList<MethodInfo>());
        assertEquals("", serviceTypeInfo.getSchemaName());
        assertEquals(SchemaType.SERVICE, serviceTypeInfo.getSchemaType());
        assertEquals(JavaType.SERVICE, serviceTypeInfo.getJavaType());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getBitSize());

        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getFields());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getParameters());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getFunctions());

        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getTables());

        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getTemplateName());
        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getTemplateArguments());

        AssertionUtil.assertThrows(ZserioError.class, () -> serviceTypeInfo.getMessages());

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
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getBitSize());

        assertEquals(typeInfo.getFields().size(), recursiveTypeInfo.getFields().size());
        assertEquals(0, recursiveTypeInfo.getParameters().size());
        assertEquals(0, recursiveTypeInfo.getFunctions().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getTables());

        assertEquals(typeInfo.getTemplateName(), recursiveTypeInfo.getTemplateName());
        assertEquals(0, recursiveTypeInfo.getTemplateArguments().size());

        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> recursiveTypeInfo.getMethods());
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
            AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getBitSize());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getFields());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getParameters());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getFunctions());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getSelector());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getCases());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getUnderlyingType());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getUnderlyingTypeArguments());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getEnumItems());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getBitmaskValues());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getColumns());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getSqlConstraint());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getVirtualTableUsing());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.isWithoutRowId());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getTables());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getTemplateName());
        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getTemplateArguments());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getMessages());

        AssertionUtil.assertThrows(ZserioError.class, () -> typeInfo.getMethods());
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
                            "recursive",
                            new RecursiveTypeInfo(new RecursiveTypeInfoGetter()), // typeInfo
                            new ArrayList<String>(), // typeArguments
                            "", // alignment
                            "", // offset
                            "", // initializer
                            true, // isOptional
                            "", // optionalCondition
                            "", // constraint
                            false, // isArray
                            "", // arrayLength
                            false, // isPacked
                            false // isImplicit
                    )
            );

            return new StructTypeInfo("RecursiveObject", "", new ArrayList<TypeInfo>(),
                    fields, new ArrayList<ParameterInfo>(), new ArrayList<FunctionInfo>());
        }
    }
}
