package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zserio.tools.ZserioToolPrinter;

/**
 * Implementation of ZserioAstVisitor which manages checking phase.
 */
public class ZserioAstChecker extends ZserioAstWalker
{
    /**
     * Constructor.
     *
     * @param checkUnusedTypes Whether to check for unused types.
     */
    public ZserioAstChecker(boolean checkUnusedTypes)
    {
        this.checkUnusedTypes = checkUnusedTypes;
    }

    @Override
    public void visitRoot(Root root)
    {
        if (checkUnusedTypes)
        {
            super.visitRoot(root);

            for (ZserioType definedType : definedTypes)
            {
                final String definedTypeName = ZserioTypeUtil.getFullName(definedType);
                if (!usedTypeNames.contains(definedTypeName))
                    ZserioToolPrinter.printWarning(definedType, "Type '" + definedTypeName + "' is not used.");
            }
        }
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        definedTypes.add(subtype);
        addUsedType(subtype.getTargetType());
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        definedTypes.add(structureType);
        structureType.visitChildren(this);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        definedTypes.add(choiceType);
        choiceType.visitChildren(this);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        definedTypes.add(unionType);
        unionType.visitChildren(this);
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        definedTypes.add(enumType);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        definedTypes.add(sqlTableType);
        sqlTableType.visitChildren(this);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        addUsedType(typeReference.getReferencedType());
    }

    private void addUsedType(ZserioType usedType)
    {
        final ZserioType referencedType = TypeReference.resolveType(usedType);
        if (!ZserioTypeUtil.isBuiltIn(referencedType))
            usedTypeNames.add(ZserioTypeUtil.getFullName(referencedType));
    }

    private final boolean checkUnusedTypes;

    private final Set<String> usedTypeNames = new HashSet<String>();
    private final List<ZserioType> definedTypes = new ArrayList<ZserioType>();
};
