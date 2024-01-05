package zserio.runtime.typeinfo;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;

/**
 * Type information interface which is returned from the generated zserio objects.
 *
 * This interface provides additional schema information of the corresponded zserio object, like schema
 * name, schema type, etc...
 *
 * Not all methods are implemented for all zserio objects. For example, the method getFields() is implemented
 * for compound types only.
 */
public interface TypeInfo
{
    /**
     * Gets the schema name.
     *
     * @return The zserio full name stored in schema.
     */
    String getSchemaName();

    /**
     * Gets the schema type.
     *
     * @return The zserio type stored in schema.
     */
    SchemaType getSchemaType();

    /**
     * Gets the Java type.
     *
     * @return The Java type to which zserio type is mapped.
     */
    JavaType getJavaType();

    /**
     * Gets the Java Class object.
     *
     * @return The Java Class object.
     */
    Class<?> getJavaClass();

    /**
     * Gets the bit size of the fixed size integral schema type.
     *
     * @return The bit size of zserio type.
     *
     * @throws ZserioError If the zserio type is not fixed size integral (e.g. varint).
     */
    byte getBitSize();

    // methods for compound types

    /**
     * Gets the type information for compound type fields.
     *
     * @return Sequence of type informations for fields.
     *
     * @throws ZserioError If the zserio type is not compound type.
     */
    List<FieldInfo> getFields();

    /**
     * Gets the type information for compound type parameters.
     *
     * @return Sequence of type informations for parameters.
     *
     * @throws ZserioError If the zserio type is not compound type.
     */
    List<ParameterInfo> getParameters();

    /**
     * Gets the type information for compound type functions.
     *
     * @return Sequence of type informations for functions.
     *
     * @throws ZserioError If the zserio type is not compound type.
     */
    List<FunctionInfo> getFunctions();

    // methods for choice type

    /**
     * Gets the selector for choice type.
     *
     * @return Selector expression of choice type.
     *
     * @throws ZserioError If the zserio type is not choice type.
     */
    String getSelector();

    /**
     * Gets the type information for choice type cases.
     *
     * @return Sequence of type informations for choice type cases.
     *
     * @throws ZserioError If the zserio type is not choice type.
     */
    List<CaseInfo> getCases();

    // methods for enumeration and bitmask types

    /**
     * Gets the reference to type information of underlying zserio type.
     *
     * @return Reference to type information of underlying zserio type.
     *
     * @throws ZserioError If the zserio type is not enumeration or bitmask type.
     */
    TypeInfo getUnderlyingType();

    /**
     * Gets the reference to type information of underlying zserio type arguments.
     *
     * @return Underlying zserio type arguments.
     *
     * @throws ZserioError If the zserio type is not enumeration or bitmask type.
     */
    List<Supplier<Object>> getUnderlyingTypeArguments();

    /**
     * Gets the type information for enumeration type items.
     *
     * @return Sequence of type informations for enumeration type items.
     *
     * @throws ZserioError If the zserio type is not enumeration type.
     */
    List<ItemInfo> getEnumItems();

    /**
     * Gets the type information for bitmask type values.
     *
     * @return Sequence of type informations for bitmask type values.
     *
     * @throws ZserioError If the zserio type is not bitmask type.
     */
    List<ItemInfo> getBitmaskValues();

    // methods for SQL table types

    /**
     * Gets the type information for SQL table columns.
     *
     * @return Sequence of type informations for SQL table columns.
     *
     * @throws ZserioError If the zserio type is not SQL table type.
     */
    List<ColumnInfo> getColumns();

    /**
     * Gets the SQL table constraint.
     *
     * @return The SQL table constraint.
     *
     * @throws ZserioError If the zserio type is not SQL table type.
     */
    String getSqlConstraint();

    /**
     * Gets the SQL table using specification.
     *
     * @return The SQL table using specification.
     *
     * @throws ZserioError If the zserio type is not SQL table type.
     */
    String getVirtualTableUsing();

    /**
     * Checks if SQL table is without row id table.
     *
     * @return true if SQL table is without row id table, otherwise false.
     *
     * @throws ZserioError If the zserio type is not SQL table type.
     */
    boolean isWithoutRowId();

    // method for SQL database type

    /**
     * Gets the type information for SQL database tables.
     *
     * @return Sequence of type informations for SQL database tables.
     *
     * @throws ZserioError If the zserio type is not SQL database type.
     */
    List<TableInfo> getTables();

    // methods for templatable types

    /**
     * Gets the full schema template name.
     *
     * @return The full schema template name.
     *
     * @throws ZserioError If the zserio type is not templatable.
     */
    String getTemplateName();

    /**
     * Gets the type information for template arguments.
     *
     * @return Sequence of type informations for template arguments.
     *
     * @throws ZserioError If the zserio type is not templatable.
     */
    List<TypeInfo> getTemplateArguments();

    // method for pubsub type

    /**
     * Gets the type information for pubsub messages.
     *
     * @return Sequence of type informations for pubsub messages.
     *
     * @throws ZserioError If the zserio type is not pubsub type.
     */
    List<MessageInfo> getMessages();

    // method for service type

    /**
     * Gets the type information for service methods.
     *
     * @return Sequence of type informations for service methods.
     *
     * @throws ZserioError If the zserio type is not service type.
     */
    List<MethodInfo> getMethods();

    /**
     * Type information abstract base class.
     *
     * This base class implements fully the methods getSchemaName(), getSchemaName() and getJavaType().
     * All other interface methods just throw an exception.
     */
    public static abstract class TypeInfoBase implements TypeInfo
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param schemaType The schema type to be stored in type information.
         * @param javaType The Java type to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         */
        public TypeInfoBase(String schemaName, SchemaType schemaType, JavaType javaType, Class<?> javaClass)
        {
            this.schemaName = schemaName;
            this.schemaType = schemaType;
            this.javaType = javaType;
            this.javaClass = javaClass;
        }

        @Override
        public String getSchemaName()
        {
            return schemaName;
        }

        @Override
        public SchemaType getSchemaType()
        {
            return schemaType;
        }

        @Override
        public JavaType getJavaType()
        {
            return javaType;
        }

        @Override
        public Class<?> getJavaClass()
        {
            return javaClass;
        }

        @Override
        public byte getBitSize()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a fixed size type!");
        }

        @Override
        public List<FieldInfo> getFields()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a compound type!");
        }

        @Override
        public List<ParameterInfo> getParameters()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a compound type!");
        }

        @Override
        public List<FunctionInfo> getFunctions()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a compound type!");
        }

        @Override
        public String getSelector()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a choice type!");
        }

        @Override
        public List<CaseInfo> getCases()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a choice type!");
        }

        @Override
        public TypeInfo getUnderlyingType()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' does not have underlying type!");
        }

        @Override
        public List<Supplier<Object>> getUnderlyingTypeArguments()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' does not have underlying type!");
        }

        @Override
        public List<ItemInfo> getEnumItems()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not an enum type!");
        }

        @Override
        public List<ItemInfo> getBitmaskValues()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a bitmask type!");
        }

        @Override
        public List<ColumnInfo> getColumns()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a SQL table type!");
        }

        @Override
        public String getSqlConstraint()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a SQL table type!");
        }

        @Override
        public String getVirtualTableUsing()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a SQL table type!");
        }

        @Override
        public boolean isWithoutRowId()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a SQL table type!");
        }

        @Override
        public List<TableInfo> getTables()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a SQL database type!");
        }

        @Override
        public String getTemplateName()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a templatable type!");
        }

        @Override
        public List<TypeInfo> getTemplateArguments()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a templatable type!");
        }

        @Override
        public List<MessageInfo> getMessages()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a pubsub type!");
        }

        @Override
        public List<MethodInfo> getMethods()
        {
            throw new ZserioError("Type '" + getSchemaName() + "' is not a service type!");
        }

        private final String schemaName;
        private final SchemaType schemaType;
        private final JavaType javaType;
        private final Class<?> javaClass;
    }

    /**
     * Type information abstract base class for builtin types.
     */
    public static class BuiltinTypeInfo extends TypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param schemaType The schema type to be stored in type information.
         * @param javaType The Java type to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         */
        public BuiltinTypeInfo(String schemaName, SchemaType schemaType, JavaType javaType, Class<?> javaClass)
        {
            super(schemaName, schemaType, javaType, javaClass);
        }

        /**
         * Gets the type information of bool schema type.
         *
         * @return Type information of bool schema type.
         */
        public static BuiltinTypeInfo getBool()
        {
            return FixedSizeBuiltinTypeInfo.getBool();
        }

        /**
         * Gets the type information of int8 schema type.
         *
         * @return Type information of int8 schema type.
         */
        public static BuiltinTypeInfo getInt8()
        {
            return FixedSizeBuiltinTypeInfo.getInt8();
        }

        /**
         * Gets the type information of int16 schema type.
         *
         * @return Type information of int16 schema type.
         */
        public static BuiltinTypeInfo getInt16()
        {
            return FixedSizeBuiltinTypeInfo.getInt16();
        }

        /**
         * Gets the type information of int32 schema type.
         *
         * @return Type information of int32 schema type.
         */
        public static BuiltinTypeInfo getInt32()
        {
            return FixedSizeBuiltinTypeInfo.getInt32();
        }

        /**
         * Gets the type information of int64 schema type.
         *
         * @return Type information of int64 schema type.
         */
        public static BuiltinTypeInfo getInt64()
        {
            return FixedSizeBuiltinTypeInfo.getInt64();
        }

        /**
         * Gets the type information of uint8 schema type.
         *
         * @return Type information of uint8 schema type.
         */
        public static BuiltinTypeInfo getUInt8()
        {
            return FixedSizeBuiltinTypeInfo.getUInt8();
        }

        /**
         * Gets the type information of uint16 schema type.
         *
         * @return Type information of uint16 schema type.
         */
        public static BuiltinTypeInfo getUInt16()
        {
            return FixedSizeBuiltinTypeInfo.getUInt16();
        }

        /**
         * Gets the type information of uint32 schema type.
         *
         * @return Type information of uint32 schema type.
         */
        public static BuiltinTypeInfo getUInt32()
        {
            return FixedSizeBuiltinTypeInfo.getUInt32();
        }

        /**
         * Gets the type information of uint64 schema type.
         *
         * @return Type information of uint64 schema type.
         */
        public static BuiltinTypeInfo getUInt64()
        {
            return FixedSizeBuiltinTypeInfo.getUInt64();
        }

        /**
         * Gets the type information of varint16 schema type.
         *
         * @return Type information of varint16 schema type.
         */
        public static BuiltinTypeInfo getVarInt16()
        {
            return VARINT16;
        }

        /**
         * Gets the type information of varint32 schema type.
         *
         * @return Type information of varint32 schema type.
         */
        public static BuiltinTypeInfo getVarInt32()
        {
            return VARINT32;
        }

        /**
         * Gets the type information of varint64 schema type.
         *
         * @return Type information of varint64 schema type.
         */
        public static BuiltinTypeInfo getVarInt64()
        {
            return VARINT64;
        }

        /**
         * Gets the type information of varint schema type.
         *
         * @return Type information of varint schema type.
         */
        public static BuiltinTypeInfo getVarInt()
        {
            return VARINT;
        }

        /**
         * Gets the type information of varuint16 schema type.
         *
         * @return Type information of varuint16 schema type.
         */
        public static BuiltinTypeInfo getVarUInt16()
        {
            return VARUINT16;
        }

        /**
         * Gets the type information of varuint32 schema type.
         *
         * @return Type information of varuint32 schema type.
         */
        public static BuiltinTypeInfo getVarUInt32()
        {
            return VARUINT32;
        }

        /**
         * Gets the type information of varuint64 schema type.
         *
         * @return Type information of varuint64 schema type.
         */
        public static BuiltinTypeInfo getVarUInt64()
        {
            return VARUINT64;
        }

        /**
         * Gets the type information of varuint schema type.
         *
         * @return Type information of varuint schema type.
         */
        public static BuiltinTypeInfo getVarUInt()
        {
            return VARUINT;
        }

        /**
         * Gets the type information of varsize schema type.
         *
         * @return Type information of varsize schema type.
         */
        public static BuiltinTypeInfo getVarSize()
        {
            return VARSIZE;
        }

        /**
         * Gets the type information of float16 schema type.
         *
         * @return Type information of float16 schema type.
         */
        public static BuiltinTypeInfo getFloat16()
        {
            return FixedSizeBuiltinTypeInfo.getFloat16();
        }

        /**
         * Gets the type information of float32 schema type.
         *
         * @return Type information of float32 schema type.
         */
        public static BuiltinTypeInfo getFloat32()
        {
            return FixedSizeBuiltinTypeInfo.getFloat32();
        }

        /**
         * Gets the type information of float64 schema type.
         *
         * @return Type information of float64 schema type.
         */
        public static BuiltinTypeInfo getFloat64()
        {
            return FixedSizeBuiltinTypeInfo.getFloat64();
        }

        /**
         * Gets the type information of bytes schema type.
         *
         * @return Type information of bytes schema type.
         */
        public static BuiltinTypeInfo getBytes()
        {
            return BYTES;
        }

        /**
         * Gets the type information of string schema type.
         *
         * @return Type information of string schema type.
         */
        public static BuiltinTypeInfo getString()
        {
            return STRING;
        }

        /**
         * Gets the type information of extern schema type.
         *
         * @return Type information of extern schema type.
         */
        public static BuiltinTypeInfo getBitBuffer()
        {
            return BIT_BUFFER;
        }

        /**
         * Gets the type information of fixed signed bit field schema type.
         *
         * @param bitSize The bit size of the bit field.
         *
         * @return Type information of fixed signed bit field schema type.
         */
        public static BuiltinTypeInfo getFixedSignedBitField(byte bitSize)
        {
            return FixedSizeBuiltinTypeInfo.getFixedSignedBitField(bitSize);
        }

        /**
         * Gets the type information of fixed unsigned bit field schema type.
         *
         * @param bitSize The bit size of the bit field.
         *
         * @return Type information of fixed unsigned bit field schema type.
         */
        public static BuiltinTypeInfo getFixedUnsignedBitField(byte bitSize)
        {
            return FixedSizeBuiltinTypeInfo.getFixedUnsignedBitField(bitSize);
        }

        /**
         * Gets the type information of dynamic signed bit field schema type.
         *
         * @param maxBitSize The maximum bit size of the dynamic bit field.
         *
         * @return Type information of dynamic signed bit field schema type.
         */
        public static BuiltinTypeInfo getDynamicSignedBitField(byte maxBitSize)
        {
            if (maxBitSize <= 0 || maxBitSize > 64)
            {
                throw new ZserioError(
                        "BuiltinTypeInfo.getDynamicSignedBitField: Invalid max bit size '" + maxBitSize + "'!");
            }

            if (maxBitSize <= 8)
            {
                return new BuiltinTypeInfo(
                        "int<>", SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.BYTE, byte.class);
            }
            else if (maxBitSize <= 16)
            {
                return new BuiltinTypeInfo(
                        "int<>", SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.SHORT, short.class);
            }
            else if (maxBitSize <= 32)
            {
                return new BuiltinTypeInfo(
                        "int<>", SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.INT, int.class);
            }
            else
            {
                return new BuiltinTypeInfo(
                        "int<>", SchemaType.DYNAMIC_SIGNED_BITFIELD, JavaType.LONG, long.class);
            }
        }

        /**
         * Gets the type information of dynamic unsigned bit field schema type.
         *
         * @param maxBitSize The maximum bit size of the dynamic bit field.
         *
         * @return Type information of dynamic unsigned bit field schema type.
         */
        public static BuiltinTypeInfo getDynamicUnsignedBitField(byte maxBitSize)
        {
            if (maxBitSize <= 0 || maxBitSize > 64)
            {
                throw new ZserioError("BuiltinTypeInfo.getDynamicUnsignedBitField: Invalid max bit size '" +
                        maxBitSize + "'!");
            }

            if (maxBitSize < 8)
            {
                return new BuiltinTypeInfo(
                        "bit<>", SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.BYTE, byte.class);
            }
            else if (maxBitSize < 16)
            {
                return new BuiltinTypeInfo(
                        "bit<>", SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.SHORT, short.class);
            }
            else if (maxBitSize < 32)
            {
                return new BuiltinTypeInfo(
                        "bit<>", SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.INT, int.class);
            }
            else if (maxBitSize < 64)
            {
                return new BuiltinTypeInfo(
                        "bit<>", SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.LONG, long.class);
            }
            else
            {
                return new BuiltinTypeInfo(
                        "bit<>", SchemaType.DYNAMIC_UNSIGNED_BITFIELD, JavaType.BIG_INTEGER, BigInteger.class);
            }
        }

        private static final BuiltinTypeInfo VARINT16 =
                new BuiltinTypeInfo("varint16", SchemaType.VARINT16, JavaType.SHORT, short.class);
        private static final BuiltinTypeInfo VARINT32 =
                new BuiltinTypeInfo("varint32", SchemaType.VARINT32, JavaType.INT, int.class);
        private static final BuiltinTypeInfo VARINT64 =
                new BuiltinTypeInfo("varint64", SchemaType.VARINT64, JavaType.LONG, long.class);
        private static final BuiltinTypeInfo VARINT =
                new BuiltinTypeInfo("varint", SchemaType.VARINT, JavaType.LONG, long.class);

        private static final BuiltinTypeInfo VARUINT16 =
                new BuiltinTypeInfo("varuint16", SchemaType.VARUINT16, JavaType.SHORT, short.class);
        private static final BuiltinTypeInfo VARUINT32 =
                new BuiltinTypeInfo("varuint32", SchemaType.VARUINT32, JavaType.INT, int.class);
        private static final BuiltinTypeInfo VARUINT64 =
                new BuiltinTypeInfo("varuint64", SchemaType.VARUINT64, JavaType.LONG, long.class);
        private static final BuiltinTypeInfo VARUINT =
                new BuiltinTypeInfo("varuint", SchemaType.VARUINT, JavaType.BIG_INTEGER, BigInteger.class);

        private static final BuiltinTypeInfo VARSIZE =
                new BuiltinTypeInfo("varsize", SchemaType.VARSIZE, JavaType.INT, int.class);

        private static final BuiltinTypeInfo BYTES =
                new BuiltinTypeInfo("bytes", SchemaType.BYTES, JavaType.BYTES, byte[].class);
        private static final BuiltinTypeInfo STRING =
                new BuiltinTypeInfo("string", SchemaType.STRING, JavaType.STRING, String.class);
        private static final BuiltinTypeInfo BIT_BUFFER =
                new BuiltinTypeInfo("extern", SchemaType.EXTERN, JavaType.BIT_BUFFER, BitBuffer.class);
    }

    /**
     * Type information abstract base class for fixed size builtin types.
     */
    public static final class FixedSizeBuiltinTypeInfo extends BuiltinTypeInfo
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param schemaType The schema type to be stored in type information.
         * @param javaType The Java type to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param bitSize The bit size of the fixed size integral schema type.
         */
        public FixedSizeBuiltinTypeInfo(
                String schemaName, SchemaType schemaType, JavaType javaType, Class<?> javaClass, byte bitSize)
        {
            super(schemaName, schemaType, javaType, javaClass);

            this.bitSize = bitSize;
        }

        @Override
        public byte getBitSize()
        {
            return bitSize;
        }

        /**
         * Gets the type information of bool schema type.
         *
         * @return Type information of bool schema type.
         */
        public static FixedSizeBuiltinTypeInfo getBool()
        {
            return BOOL;
        }

        /**
         * Gets the type information of int8 schema type.
         *
         * @return Type information of int8 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getInt8()
        {
            return INT8;
        }

        /**
         * Gets the type information of int16 schema type.
         *
         * @return Type information of int16 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getInt16()
        {
            return INT16;
        }

        /**
         * Gets the type information of int32 schema type.
         *
         * @return Type information of int32 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getInt32()
        {
            return INT32;
        }

        /**
         * Gets the type information of int64 schema type.
         *
         * @return Type information of int64 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getInt64()
        {
            return INT64;
        }

        /**
         * Gets the type information of uint8 schema type.
         *
         * @return Type information of uint8 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getUInt8()
        {
            return UINT8;
        }

        /**
         * Gets the type information of uint16 schema type.
         *
         * @return Type information of uint16 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getUInt16()
        {
            return UINT16;
        }

        /**
         * Gets the type information of uint32 schema type.
         *
         * @return Type information of uint32 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getUInt32()
        {
            return UINT32;
        }

        /**
         * Gets the type information of uint64 schema type.
         *
         * @return Type information of uint64 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getUInt64()
        {
            return UINT64;
        }

        /**
         * Gets the type information of float16 schema type.
         *
         * @return Type information of float16 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getFloat16()
        {
            return FLOAT16;
        }

        /**
         * Gets the type information of float32 schema type.
         *
         * @return Type information of float32 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getFloat32()
        {
            return FLOAT32;
        }

        /**
         * Gets the type information of float64 schema type.
         *
         * @return Type information of float64 schema type.
         */
        public static FixedSizeBuiltinTypeInfo getFloat64()
        {
            return FLOAT64;
        }

        /**
         * Gets the type information of fixed signed bit field schema type.
         *
         * @param bitSize The bit size of the bit field.
         *
         * @return Type information of fixed signed bit field schema type.
         */
        public static FixedSizeBuiltinTypeInfo getFixedSignedBitField(byte bitSize)
        {
            if (bitSize <= 0 || bitSize > 64)
            {
                throw new ZserioError(
                        "FixedSizeBuiltinTypeInfo.getFixedSignedBitField: Invalid bit size '" + bitSize + "'!");
            }

            final String schemaName = "int:" + bitSize;
            if (bitSize <= 8)
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_SIGNED_BITFIELD, JavaType.BYTE, byte.class, bitSize);
            }
            else if (bitSize <= 16)
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_SIGNED_BITFIELD, JavaType.SHORT, short.class, bitSize);
            }
            else if (bitSize <= 32)
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_SIGNED_BITFIELD, JavaType.INT, int.class, bitSize);
            }
            else // bitSize <= 64
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_SIGNED_BITFIELD, JavaType.LONG, long.class, bitSize);
            }
        }

        /**
         * Gets the type information of fixed unsigned bit field schema type.
         *
         * @param bitSize The bit size of the bit field.
         *
         * @return Type information of fixed unsigned bit field schema type.
         */
        public static FixedSizeBuiltinTypeInfo getFixedUnsignedBitField(byte bitSize)
        {
            if (bitSize <= 0 || bitSize > 64)
            {
                throw new ZserioError(
                        "FixedSizeBuiltinTypeInfo.getFixedSignedBitField: Invalid bit size '" + bitSize + "'!");
            }

            final String schemaName = "bit:" + bitSize;
            if (bitSize < 8)
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.BYTE, byte.class, bitSize);
            }
            else if (bitSize < 16)
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.SHORT, short.class, bitSize);
            }
            else if (bitSize < 32)
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.INT, int.class, bitSize);
            }
            else if (bitSize < 64)
            {
                return new FixedSizeBuiltinTypeInfo(
                        schemaName, SchemaType.FIXED_UNSIGNED_BITFIELD, JavaType.LONG, long.class, bitSize);
            }
            else // bitSize == 64
            {
                return new FixedSizeBuiltinTypeInfo(schemaName, SchemaType.FIXED_UNSIGNED_BITFIELD,
                        JavaType.BIG_INTEGER, BigInteger.class, bitSize);
            }
        }

        private static final FixedSizeBuiltinTypeInfo BOOL =
                new FixedSizeBuiltinTypeInfo("bool", SchemaType.BOOL, JavaType.BOOLEAN, boolean.class, (byte)1);

        private static final FixedSizeBuiltinTypeInfo INT8 =
                new FixedSizeBuiltinTypeInfo("int8", SchemaType.INT8, JavaType.BYTE, byte.class, (byte)8);
        private static final FixedSizeBuiltinTypeInfo INT16 =
                new FixedSizeBuiltinTypeInfo("int16", SchemaType.INT16, JavaType.SHORT, short.class, (byte)16);
        private static final FixedSizeBuiltinTypeInfo INT32 =
                new FixedSizeBuiltinTypeInfo("int32", SchemaType.INT32, JavaType.INT, int.class, (byte)32);
        private static final FixedSizeBuiltinTypeInfo INT64 =
                new FixedSizeBuiltinTypeInfo("int64", SchemaType.INT64, JavaType.LONG, long.class, (byte)64);

        private static final FixedSizeBuiltinTypeInfo UINT8 =
                new FixedSizeBuiltinTypeInfo("uint8", SchemaType.UINT8, JavaType.SHORT, short.class, (byte)8);
        private static final FixedSizeBuiltinTypeInfo UINT16 =
                new FixedSizeBuiltinTypeInfo("uint16", SchemaType.UINT16, JavaType.INT, int.class, (byte)16);
        private static final FixedSizeBuiltinTypeInfo UINT32 =
                new FixedSizeBuiltinTypeInfo("uint32", SchemaType.UINT32, JavaType.LONG, long.class, (byte)32);
        private static final FixedSizeBuiltinTypeInfo UINT64 = new FixedSizeBuiltinTypeInfo(
                "uint64", SchemaType.UINT64, JavaType.BIG_INTEGER, BigInteger.class, (byte)64);

        private static final FixedSizeBuiltinTypeInfo FLOAT16 = new FixedSizeBuiltinTypeInfo(
                "float16", SchemaType.FLOAT16, JavaType.FLOAT, float.class, (byte)16);
        private static final FixedSizeBuiltinTypeInfo FLOAT32 = new FixedSizeBuiltinTypeInfo(
                "float32", SchemaType.FLOAT32, JavaType.FLOAT, float.class, (byte)32);
        private static final FixedSizeBuiltinTypeInfo FLOAT64 = new FixedSizeBuiltinTypeInfo(
                "float64", SchemaType.FLOAT64, JavaType.DOUBLE, double.class, (byte)64);

        private final byte bitSize;
    }

    /**
     * Type information abstract base class for templatable types.
     */
    public static class TemplatableTypeInfoBase extends TypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param schemaType The schema type to be stored in type information.
         * @param javaType The Java type to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param templateName The full schema template name.
         * @param templateArguments The sequence of type informations for template arguments.
         */
        public TemplatableTypeInfoBase(String schemaName, SchemaType schemaType, JavaType javaType,
                Class<?> javaClass, String templateName, List<TypeInfo> templateArguments)
        {
            super(schemaName, schemaType, javaType, javaClass);

            this.templateName = templateName;
            this.templateArguments = templateArguments;
        }

        @Override
        public String getTemplateName()
        {
            return templateName;
        }

        @Override
        public List<TypeInfo> getTemplateArguments()
        {
            return Collections.unmodifiableList(templateArguments);
        }

        private final String templateName;
        private final List<TypeInfo> templateArguments;
    }

    /**
     * Type information abstract base class for compound types.
     */
    public static abstract class CompoundTypeInfoBase extends TemplatableTypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param schemaType The schema type to be stored in type information.
         * @param javaType The Java type to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param templateName The full schema template name.
         * @param templateArguments The sequence of type informations for template arguments.
         * @param fields The sequence of type informations for fields.
         * @param parameters The sequence of type informations for parameters.
         * @param functions The sequence of type informations for functions.
         */
        public CompoundTypeInfoBase(String schemaName, SchemaType schemaType, JavaType javaType,
                Class<?> javaClass, String templateName, List<TypeInfo> templateArguments,
                List<FieldInfo> fields, List<ParameterInfo> parameters, List<FunctionInfo> functions)
        {
            super(schemaName, schemaType, javaType, javaClass, templateName, templateArguments);

            this.fields = fields;
            this.parameters = parameters;
            this.functions = functions;
        }

        @Override
        public List<FieldInfo> getFields()
        {
            return Collections.unmodifiableList(fields);
        }

        @Override
        public List<ParameterInfo> getParameters()
        {
            return Collections.unmodifiableList(parameters);
        }

        @Override
        public List<FunctionInfo> getFunctions()
        {
            return Collections.unmodifiableList(functions);
        }

        private final List<FieldInfo> fields;
        private final List<ParameterInfo> parameters;
        private final List<FunctionInfo> functions;
    }

    /**
     * Type information class for structure types.
     */
    public static final class StructTypeInfo extends CompoundTypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param templateName The full schema template name.
         * @param templateArguments The sequence of type informations for template arguments.
         * @param fields The sequence of type informations for fields.
         * @param parameters The sequence of type informations for parameters.
         * @param functions The sequence of type informations for functions.
         */
        public StructTypeInfo(String schemaName, Class<?> javaClass, String templateName,
                List<TypeInfo> templateArguments, List<FieldInfo> fields, List<ParameterInfo> parameters,
                List<FunctionInfo> functions)
        {
            super(schemaName, SchemaType.STRUCT, JavaType.STRUCT, javaClass, templateName, templateArguments,
                    fields, parameters, functions);
        }
    }

    /**
     * Type information class for union types.
     */
    public static final class UnionTypeInfo extends CompoundTypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param templateName The full schema template name.
         * @param templateArguments The sequence of type informations for template arguments.
         * @param fields The sequence of type informations for fields.
         * @param parameters The sequence of type informations for parameters.
         * @param functions The sequence of type informations for functions.
         */
        public UnionTypeInfo(String schemaName, Class<?> javaClass, String templateName,
                List<TypeInfo> templateArguments, List<FieldInfo> fields, List<ParameterInfo> parameters,
                List<FunctionInfo> functions)
        {
            super(schemaName, SchemaType.UNION, JavaType.UNION, javaClass, templateName, templateArguments,
                    fields, parameters, functions);
        }
    }

    /**
     * Type information class for choice types.
     */
    public static final class ChoiceTypeInfo extends CompoundTypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param templateName The full schema template name.
         * @param templateArguments The sequence of type informations for template arguments.
         * @param fields The sequence of type informations for fields.
         * @param parameters The sequence of type informations for parameters.
         * @param functions The sequence of type informations for functions.
         * @param selector The selector expression.
         * @param cases The sequence of type informations for cases.
         */
        public ChoiceTypeInfo(String schemaName, Class<?> javaClass, String templateName,
                List<TypeInfo> templateArguments, List<FieldInfo> fields, List<ParameterInfo> parameters,
                List<FunctionInfo> functions, String selector, List<CaseInfo> cases)
        {
            super(schemaName, SchemaType.CHOICE, JavaType.CHOICE, javaClass, templateName, templateArguments,
                    fields, parameters, functions);

            this.selector = selector;
            this.cases = cases;
        }

        @Override
        public String getSelector()
        {
            return selector;
        }

        @Override
        public List<CaseInfo> getCases()
        {
            return Collections.unmodifiableList(cases);
        }

        private final String selector;
        private final List<CaseInfo> cases;
    }

    /**
     * Type information abstract base class for enumeration and bitmask types.
     */
    public static abstract class TypeInfoWithUnderlyingTypeBase extends TypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param schemaType The schema type to be stored in type information.
         * @param javaType The Java type to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param underlyingType The reference to type information of underlying zserio type.
         * @param underlyingTypeArguments The underlying zserio type arguments.
         */
        public TypeInfoWithUnderlyingTypeBase(String schemaName, SchemaType schemaType, JavaType javaType,
                Class<?> javaClass, TypeInfo underlyingType, List<Supplier<Object>> underlyingTypeArguments)
        {
            super(schemaName, schemaType, javaType, javaClass);

            this.underlyingType = underlyingType;
            this.underlyingTypeArguments = underlyingTypeArguments;
        }

        @Override
        public TypeInfo getUnderlyingType()
        {
            return underlyingType;
        }

        @Override
        public List<Supplier<Object>> getUnderlyingTypeArguments()
        {
            return Collections.unmodifiableList(underlyingTypeArguments);
        }

        private final TypeInfo underlyingType;
        private final List<Supplier<Object>> underlyingTypeArguments;
    }

    /**
     * Type information class for enumeration types.
     */
    public static final class EnumTypeInfo extends TypeInfoWithUnderlyingTypeBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param underlyingType The reference to type information of underlying zserio type.
         * @param underlyingTypeArguments The underlying zserio type arguments.
         * @param enumItems The sequence of type informations for enumeration items.
         */
        public EnumTypeInfo(String schemaName, Class<?> javaClass, TypeInfo underlyingType,
                List<Supplier<Object>> underlyingTypeArguments, List<ItemInfo> enumItems)
        {
            super(schemaName, SchemaType.ENUM, JavaType.ENUM, javaClass, underlyingType,
                    underlyingTypeArguments);

            this.enumItems = enumItems;
        }

        @Override
        public List<ItemInfo> getEnumItems()
        {
            return Collections.unmodifiableList(enumItems);
        }

        private final List<ItemInfo> enumItems;
    }

    /**
     * Type information class for bitmask types.
     */
    public static final class BitmaskTypeInfo extends TypeInfoWithUnderlyingTypeBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param underlyingType The reference to type information of underlying zserio type.
         * @param underlyingTypeArguments The underlying zserio type arguments.
         * @param bitmaskValues The sequence of type informations for bitmask values.
         */
        public BitmaskTypeInfo(String schemaName, Class<?> javaClass, TypeInfo underlyingType,
                List<Supplier<Object>> underlyingTypeArguments, List<ItemInfo> bitmaskValues)
        {
            super(schemaName, SchemaType.BITMASK, JavaType.BITMASK, javaClass, underlyingType,
                    underlyingTypeArguments);

            this.bitmaskValues = bitmaskValues;
        }

        @Override
        public List<ItemInfo> getBitmaskValues()
        {
            return Collections.unmodifiableList(bitmaskValues);
        }

        private final List<ItemInfo> bitmaskValues;
    }

    /**
     * Type information class for SQL table types.
     */
    public static final class SqlTableTypeInfo extends TemplatableTypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param templateName The full schema template name.
         * @param templateArguments The sequence of type informations for template arguments.
         * @param columns The sequence of type informations for columns.
         * @param sqlConstraint The SQL table constraint.
         * @param virtualTableUsing The SQL virtual table using specification.
         * @param isWithoutRowId True if SQL table is without row id table, otherwise false.
         */
        public SqlTableTypeInfo(String schemaName, Class<?> javaClass, String templateName,
                List<TypeInfo> templateArguments, List<ColumnInfo> columns, String sqlConstraint,
                String virtualTableUsing, boolean isWithoutRowId)
        {
            super(schemaName, SchemaType.SQL_TABLE, JavaType.SQL_TABLE, javaClass, templateName,
                    templateArguments);

            this.columns = columns;
            this.sqlConstraint = sqlConstraint;
            this.virtualTableUsing = virtualTableUsing;
            this.isWithoutRowId = isWithoutRowId;
        }

        @Override
        public List<ColumnInfo> getColumns()
        {
            return Collections.unmodifiableList(columns);
        }

        @Override
        public String getSqlConstraint()
        {
            return sqlConstraint;
        }

        @Override
        public String getVirtualTableUsing()
        {
            return virtualTableUsing;
        }

        @Override
        public boolean isWithoutRowId()
        {
            return isWithoutRowId;
        }

        private final List<ColumnInfo> columns;
        private final String sqlConstraint;
        private final String virtualTableUsing;
        private final boolean isWithoutRowId;
    }

    /**
     * Type information class for SQL database types.
     */
    public static final class SqlDatabaseTypeInfo extends TypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param tables The sequence of type informations for tables.
         */
        public SqlDatabaseTypeInfo(String schemaName, Class<?> javaClass, List<TableInfo> tables)
        {
            super(schemaName, SchemaType.SQL_DATABASE, JavaType.SQL_DATABASE, javaClass);

            this.tables = tables;
        }

        @Override
        public List<TableInfo> getTables()
        {
            return Collections.unmodifiableList(tables);
        }

        private final List<TableInfo> tables;
    }

    /**
     * Type information class for pubsub types.
     */
    public static final class PubsubTypeInfo extends TypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param messages The sequence of type informations for pubsub messages.
         */
        public PubsubTypeInfo(String schemaName, Class<?> javaClass, List<MessageInfo> messages)
        {
            super(schemaName, SchemaType.PUBSUB, JavaType.PUBSUB, javaClass);

            this.messages = messages;
        }

        @Override
        public List<MessageInfo> getMessages()
        {
            return Collections.unmodifiableList(messages);
        }

        private final List<MessageInfo> messages;
    }

    /**
     * Type information class for service types.
     */
    public static final class ServiceTypeInfo extends TypeInfoBase
    {
        /**
         * Constructor.
         *
         * @param schemaName The schema name to be stored in type information.
         * @param javaClass The Java class object to be stored in type information.
         * @param methods The sequence of type informations for service methods.
         */
        public ServiceTypeInfo(String schemaName, Class<?> javaClass, List<MethodInfo> methods)
        {
            super(schemaName, SchemaType.SERVICE, JavaType.SERVICE, javaClass);

            this.methods = methods;
        }

        @Override
        public List<MethodInfo> getMethods()
        {
            return Collections.unmodifiableList(methods);
        }

        private final List<MethodInfo> methods;
    }

    /**
     * Type info for recursive types used as a wrapper around generated static typeInfo method to prevent
     * infinite recursion in type info definition.
     */
    public static final class RecursiveTypeInfo implements TypeInfo
    {
        /**
         * Type info getter.
         */
        public static interface TypeInfoGetter {
            /**
             * Gets the type info.
             *
             * @return Type info.
             */
            public TypeInfo get();
        }

        /**
         * Constructor.
         *
         * @param typeInfoGetter Implementation of TypeInfoGetter interface.
         */
        public RecursiveTypeInfo(TypeInfoGetter typeInfoGetter)
        {
            this.typeInfoGetter = typeInfoGetter;
        }

        @Override
        public String getSchemaName()
        {
            return getTypeInfo().getSchemaName();
        }

        @Override
        public SchemaType getSchemaType()
        {
            return getTypeInfo().getSchemaType();
        }

        @Override
        public JavaType getJavaType()
        {
            return getTypeInfo().getJavaType();
        }

        @Override
        public Class<?> getJavaClass()
        {
            return getTypeInfo().getJavaClass();
        }

        @Override
        public byte getBitSize()
        {
            return getTypeInfo().getBitSize();
        }

        @Override
        public List<FieldInfo> getFields()
        {
            return getTypeInfo().getFields();
        }

        @Override
        public List<ParameterInfo> getParameters()
        {
            return getTypeInfo().getParameters();
        }

        @Override
        public List<FunctionInfo> getFunctions()
        {
            return getTypeInfo().getFunctions();
        }

        @Override
        public String getSelector()
        {
            return getTypeInfo().getSelector();
        }

        @Override
        public List<CaseInfo> getCases()
        {
            return getTypeInfo().getCases();
        }

        @Override
        public TypeInfo getUnderlyingType()
        {
            return getTypeInfo().getUnderlyingType();
        }

        @Override
        public List<Supplier<Object>> getUnderlyingTypeArguments()
        {
            return getTypeInfo().getUnderlyingTypeArguments();
        }

        @Override
        public List<ItemInfo> getEnumItems()
        {
            return getTypeInfo().getEnumItems();
        }

        @Override
        public List<ItemInfo> getBitmaskValues()
        {
            return getTypeInfo().getBitmaskValues();
        }

        @Override
        public List<ColumnInfo> getColumns()
        {
            return getTypeInfo().getColumns();
        }

        @Override
        public String getSqlConstraint()
        {
            return getTypeInfo().getSqlConstraint();
        }

        @Override
        public String getVirtualTableUsing()
        {
            return getTypeInfo().getVirtualTableUsing();
        }

        @Override
        public boolean isWithoutRowId()
        {
            return getTypeInfo().isWithoutRowId();
        }

        @Override
        public List<TableInfo> getTables()
        {
            return getTypeInfo().getTables();
        }

        @Override
        public String getTemplateName()
        {
            return getTypeInfo().getTemplateName();
        }

        @Override
        public List<TypeInfo> getTemplateArguments()
        {
            return getTypeInfo().getTemplateArguments();
        }

        @Override
        public List<MessageInfo> getMessages()
        {
            return getTypeInfo().getMessages();
        }

        @Override
        public List<MethodInfo> getMethods()
        {
            return getTypeInfo().getMethods();
        }

        private TypeInfo getTypeInfo()
        {
            if (typeInfo == null)
                typeInfo = typeInfoGetter.get();
            return typeInfo;
        }

        private final TypeInfoGetter typeInfoGetter;
        private TypeInfo typeInfo = null;
    }
}
