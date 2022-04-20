package zserio.runtime.typeinfo;

/**
 * The type information helper utilities to check zserio schema.
 */
public final class TypeInfoUtil
{
    /**
     * Checks if zserio type is a compound type.
     *
     * @param schemaType Schema type to check.
     *
     * @return true if zserio type is a compound type, otherwise false.
     */
    public static boolean isCompound(SchemaType schemaType)
    {
        switch (schemaType)
        {
        case STRUCT:
        case CHOICE:
        case UNION:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if zserio type is a choice or union type.
     *
     * @param schemaType Schema type to check.
     *
     * @return true if zserio type is a choice or union type, otherwise false.
     */
    public static boolean hasChoice(SchemaType schemaType)
    {
        switch (schemaType)
        {
        case CHOICE:
        case UNION:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if zserio type is a fixed sized type.
     *
     * @param schemaType Schema type to check.
     *
     * @return true if zserio type is a fixed sized type, otherwise false.
     */
    public static boolean isFixedSize(SchemaType schemaType)
    {
        switch (schemaType)
        {
        case BOOL:
        case INT8:
        case INT16:
        case INT32:
        case INT64:
        case UINT8:
        case UINT16:
        case UINT32:
        case UINT64:
        case FIXED_SIGNED_BITFIELD:
        case FIXED_UNSIGNED_BITFIELD:
        case FLOAT16:
        case FLOAT32:
        case FLOAT64:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if zserio type is a integral type.
     *
     * @param schemaType Schema type to check.
     *
     * @return true if zserio type is a integral type, otherwise false.
     */
    public static boolean isIntegral(SchemaType schemaType)
    {
        switch (schemaType)
        {
        case BOOL:
        case INT8:
        case INT16:
        case INT32:
        case INT64:
        case UINT8:
        case UINT16:
        case UINT32:
        case UINT64:
        case VARINT16:
        case VARINT32:
        case VARINT64:
        case VARINT:
        case VARUINT16:
        case VARUINT32:
        case VARUINT64:
        case VARUINT:
        case VARSIZE:
        case FIXED_SIGNED_BITFIELD:
        case FIXED_UNSIGNED_BITFIELD:
        case DYNAMIC_SIGNED_BITFIELD:
        case DYNAMIC_UNSIGNED_BITFIELD:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if zserio type is a signed type.
     *
     * @param schemaType Schema type to check.
     *
     * @return true if zserio type is a signed type, otherwise false.
     */
    public static boolean isSigned(SchemaType schemaType)
    {
        switch (schemaType)
        {
        case INT8:
        case INT16:
        case INT32:
        case INT64:
        case VARINT16:
        case VARINT32:
        case VARINT64:
        case VARINT:
        case FIXED_SIGNED_BITFIELD:
        case DYNAMIC_SIGNED_BITFIELD:
        case FLOAT16:
        case FLOAT32:
        case FLOAT64:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if zserio type is a floating point type.
     *
     * @param schemaType Schema type to check.
     *
     * @return true if zserio type is a floating point type, otherwise false.
     */
    public static boolean isFloatingPoint(SchemaType schemaType)
    {
        switch (schemaType)
        {
        case FLOAT16:
        case FLOAT32:
        case FLOAT64:
            return true;
        default:
            return false;
        }
    }
}
