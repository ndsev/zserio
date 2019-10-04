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
        root.visitChildren(this);
        if (checkUnusedTypes)
        {
            for (ZserioType definedType : definedTypes)
            {
                final String definedTypeName = ZserioTypeUtil.getFullName(definedType);
                if (!usedTypeNames.contains(definedTypeName))
                    ZserioToolPrinter.printWarning(definedType, "Type '" + definedTypeName + "' is not used.");
            }
        }
    }

    @Override
    public void visitPackage(Package pkg)
    {
        currentPackage = pkg;
        pkg.visitChildren(this);
        currentPackage = null;
    }

    @Override
    public void visitConstType(ConstType constType)
    {
        constType.visitChildren(this);
        constType.check();
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        subtype.visitChildren(this);
        definedTypes.add(subtype);
        addUsedType(subtype.getTargetType());
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        if (structureType.getTemplateParameters().isEmpty())
        {
            structureType.visitChildren(this);
            definedTypes.add(structureType);
            structureType.check();
        }
        else
        {
            visitInstantiations(structureType);
        }
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        if (choiceType.getTemplateParameters().isEmpty())
        {
            choiceType.visitChildren(this);
            definedTypes.add(choiceType);
            choiceType.check();
        }
        else
        {
            visitInstantiations(choiceType);
        }
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        if (unionType.getTemplateParameters().isEmpty())
        {
            unionType.visitChildren(this);
            definedTypes.add(unionType);
            unionType.check();
        }
        else
        {
            visitInstantiations(unionType);
        }
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        enumType.visitChildren(this);
        definedTypes.add(enumType);
        enumType.check();
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        if (sqlTableType.getTemplateParameters().isEmpty())
        {
            sqlTableType.visitChildren(this);
            definedTypes.add(sqlTableType);
            sqlTableType.check();
        }
        else
        {
            visitInstantiations(sqlTableType);
        }
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        sqlDatabaseType.visitChildren(this);
        sqlDatabaseType.check();
    }

    @Override
    public void visitField(Field field)
    {
        field.visitChildren(this);
        field.check(currentPackage);
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        rpc.visitChildren(this);
        rpc.check();
    }

    @Override
    public void visitFunctionType(FunctionType functionType)
    {
        functionType.visitChildren(this);
        functionType.check();
    }

    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        arrayType.visitChildren(this);
        arrayType.check();
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        typeInstantiation.visitChildren(this);
        typeInstantiation.check();
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        typeReference.visitChildren(this);
        addUsedType(typeReference.getReferencedType());
        typeReference.check();
    }

    private void visitInstantiations(ZserioTemplatableType template)
    {
        for (ZserioTemplatableType instantiation : template.getInstantiations())
        {
            try
            {
                instantiation.accept(this);
            }
            catch (ParserException e)
            {
                final ParserStackedException stackedException = new ParserStackedException(e);
                stackedException.pushMessage(instantiation.getInstantiationLocation(),
                        "In instantiation of '" + template.getName() + "' required from here");
                throw stackedException;
            }
        }
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

    private Package currentPackage = null;
};
