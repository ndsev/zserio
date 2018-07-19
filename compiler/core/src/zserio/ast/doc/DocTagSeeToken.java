package zserio.ast.doc;

import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.Package;
import zserio.tools.StringSplitUtil;
import zserio.tools.StringSplitUtil.TypeAndFieldName;

/**
 * Implements AST token for type DOC_TAG_SEE.
 */
public class DocTagSeeToken extends DocTokenAST
{
    @Override
    public void evaluate() throws ParserException
    {
        final String tokenText = getText();
        final String[] parameterList = tokenText.split("\"");
        for (String parameter : parameterList)
        {
            if (!parameter.isEmpty())
            {
                if (linkAlias == null)
                    linkAlias = parameter;
                typeName = parameter;
            }
        }
    }

    /**
     * Gets string which represent tag see alias name.
     *
     * @return String which represent tag see alias name.
     */
    public String getLinkAlias()
    {
        return linkAlias;
    }

    /**
     * Gets the type to which tag see points.
     *
     * @return Ttype to which tag see points.
     */
    public ZserioType getLinkType()
    {
        return linkType;
    }

    /**
     * Gets the field name to which tag see points.
     *
     * @return The field name to which tag see points or null if it points to the whole type.
     */
    public String getLinkTypeFieldName()
    {
        return fieldName;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        // no children
        return false;
    }

    @Override
    protected void check(ZserioType owner) throws ParserException
    {
        checkLinkType(owner.getPackage());
    }

    private ZserioType checkLinkType(Package ownerPackage) throws ParserException
    {
        try
        {
            linkType = resolveZserioType(ownerPackage, typeName, null);
        }
        catch (ParserException outerException)
        {
            // try if the last link component is a field name
            final TypeAndFieldName typeAndFieldName = StringSplitUtil.splitLinkToTypeAndFieldName(typeName);
            try
            {
                linkType = resolveZserioType(ownerPackage, typeAndFieldName.getTypeName(),
                        typeAndFieldName.getFieldName());
            }
            catch (ParserException innerException)
            {
                throw new ParserException(this, outerException.getMessage());
            }

            typeName = typeAndFieldName.getTypeName();
            fieldName = typeAndFieldName.getFieldName();
        }

        return linkType;
    }

    public ZserioType resolveZserioType(Package ownerPackage, String typeName, String fieldName)
            throws ParserException
    {
        final ZserioType foundType = ownerPackage.getType(typeName);
        if (foundType == null)
            throw new ParserException(this, "Type " + typeName + " not found!");

        ZserioType resolveType;
        if (foundType instanceof EnumType)
        {
            final EnumType foundEnumType = (EnumType)foundType;
            if (fieldName != null)
                checkEnumValue(foundEnumType, fieldName);
            resolveType = foundEnumType;
        }
        else if (foundType instanceof CompoundType)
        {
            final CompoundType foundCompoundType = (CompoundType)foundType;
            if (fieldName != null)
                checkCompoundField(foundCompoundType, fieldName);
            resolveType = foundCompoundType;
        }
        else if (fieldName == null)
        {
            resolveType = foundType;
        }
        else
        {
            throw new ParserException(this, "Type " + foundType.getName() +
                    " is not a valid type for a link reference!");
        }

        return resolveType;
    }

    private Field checkCompoundField(CompoundType compoundType, String compoundFieldName)
            throws ParserException
    {
        Field foundField = null;
        for (Field field : compoundType.getFields())
        {
            if (field.getName().equals(compoundFieldName))
            {
                foundField = field;
                break;
            }
        }
        if (foundField == null)
            throw new ParserException(this, "Type " + compoundType.getName() + " has no field " +
                    compoundFieldName + "!");

        return foundField;
    }

    private EnumItem checkEnumValue(EnumType enumType, String enumValueName) throws ParserException
    {
        EnumItem foundItem = null;
        for (EnumItem item : enumType.getItems())
        {
            if (item.getName().equals(enumValueName))
            {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null)
            throw new ParserException(this, "Enumeration value " + enumValueName + " not found!");

        return foundItem;
    }

    private static final long serialVersionUID = 1L;

    private String          linkAlias;
    private String          typeName;
    private String          fieldName;
    private ZserioType  linkType;
}
