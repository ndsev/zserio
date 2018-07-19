package zserio.ast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.tools.PackageManager;

/**
 * Resolves {@literal @}entities in strings.
 *
 * This is currently used only inside SQL constraints.
 */
class StringEntityResolver
{
    /**
     * Replace all {@literal @}entities in a string.
     *
     * @param s Input string.
     * @return String with all the entities resolved.
     */
    public static String resolve(String s) throws StringEntityResolverException
    {
        int atIndex = s.indexOf(ENTITY_ESCAPE);
        if (atIndex < 0)
            return s; // shortcut when there are no entities

        StringBuilder buffer = new StringBuilder(s);
        while (atIndex >= 0)
        {
            final int endIndex = seekToEndOfEntity(buffer, atIndex + 1);

            final String reference = buffer.substring(atIndex + 1, endIndex);
            final String resolved = resolveReference(reference);

            buffer.replace(atIndex, endIndex, resolved);
            atIndex = buffer.indexOf(ENTITY_ESCAPE);
        }

        return buffer.toString();
    }

    // seek till the end OR a character other than (digit || letter || '.' || '_') is encountered
    private static int seekToEndOfEntity(StringBuilder buffer, int startIndex)
    {
        int endIndex = startIndex;
        while (endIndex < buffer.length())
        {
            final char c = buffer.charAt(endIndex);
            if (c != '.' && c != '_' && !Character.isLetterOrDigit(buffer.charAt(endIndex)))
                break;

            endIndex++;
        }

        return endIndex;
    }

    private static String resolveReference(String reference) throws StringEntityResolverException
    {
        if (reference.endsWith("."))
        {
            throw new StringEntityResolverException("Invalid reference '" + ENTITY_ESCAPE + reference + "'.");
        }

        // look up the enum type -> value
        List<ZserioType> typeList = new ArrayList<ZserioType>();
        List<String> fieldList = new ArrayList<String>();
        PackageManager.get().getTypeFieldList(reference, typeList, fieldList);

        if (typeList.isEmpty())
        {
            throw new StringEntityResolverException("Reference '" + ENTITY_ESCAPE + reference +
                    "' can't be resolved to a type.");
        }
        else if (typeList.size() > 1)
        {
            throw new StringEntityResolverException("Reference '" + ENTITY_ESCAPE + reference +
                    "' is ambiguous.");
        }

        if (fieldList.size() != 1)
        {
            throw new InternalError("File list size != type list size.");
        }

        final ZserioType referencedType = typeList.get(0);
        final String fieldPart = fieldList.get(0);

        if (referencedType instanceof EnumType)
        {
            return resolveEnumReference(reference, (EnumType)referencedType, fieldPart);
        }

        if (referencedType instanceof ConstType)
        {
            return resolveConstReference(reference, (ConstType)referencedType, fieldPart);
        }

        throw new StringEntityResolverException("Reference '" + ENTITY_ESCAPE + reference +
                "' does refer to neither enumeration type nor contant.");
    }

    private static String resolveEnumReference(String reference, EnumType enumType, String fieldPart)
            throws StringEntityResolverException
    {
        if (fieldPart.isEmpty())
        {
            throw new StringEntityResolverException("Reference '" + ENTITY_ESCAPE + reference +
                    "' refers to an enumeration type, but an enumeration value was expected.");
        }

        final Scope enumScope = enumType.getScope();
        Object obj = enumScope.getSymbol(fieldPart);

        if (obj == null)
        {
            throw new StringEntityResolverException("Enum " + enumType.getName() +
                    " does not contain the value " + fieldPart + " (required in '" + ENTITY_ESCAPE +
                    reference + "')!");
        }

        if (!(obj instanceof EnumItem))
        {
            throw new InternalError("EnumType returned non-EnumItem as its item.");
        }
        EnumItem item = (EnumItem)obj;

        return item.getValue().toString();
    }

    private static String resolveConstReference(String reference, ConstType constType, String fieldPart)
            throws StringEntityResolverException
    {
        if (!fieldPart.isEmpty())
        {
            throw new StringEntityResolverException("Reference '" + ENTITY_ESCAPE + reference +
                    "' refers to a field inside a scalar constant.");
        }

        final BigInteger value = constType.getValueExpression().getIntegerValue();
        if (value == null)
            throw new StringEntityResolverException(" Reference '" + ENTITY_ESCAPE + reference + "' refers " +
                    "to non-integer constant.");

        return value.toString();
    }

    /**
     * String used to denote an entity to be resolved.
     */
    private static String ENTITY_ESCAPE = "@";
}
