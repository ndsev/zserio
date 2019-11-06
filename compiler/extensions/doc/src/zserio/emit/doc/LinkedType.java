package zserio.emit.doc;

import zserio.ast.ArrayType;
import zserio.ast.AstNode;
import zserio.ast.BuiltInType;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.ServiceType;
import zserio.ast.EnumType;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;

public class LinkedType
{
    private ZserioType type;
    private final boolean isDoubleDefinedType;
    private String style;
    private String category = "";


    public LinkedType(AstNode node)
    {
        this.isDoubleDefinedType = false;
        init(node);
    }

    public LinkedType(AstNode node, boolean isDoubleDefinedType)
    {
        this.isDoubleDefinedType = isDoubleDefinedType;
        init(node);
    }

    private void init(AstNode node)
    {
        if (node instanceof ZserioType)
            this.type = (ZserioType)node;
        else if (node instanceof TypeInstantiation)
            this.type = ((TypeInstantiation)node).getTypeReference().getType();
        else if (node instanceof TypeReference)
            this.type = ((TypeReference)node).getType();
        else
            this.type = null;

        if (node instanceof TypeInstantiation)
        {
            final TypeInstantiation inst = (TypeInstantiation)node;
            node = (inst.getInstantiatedParameters().isEmpty()) ? inst.getTypeReference().getType() : inst;
            if (node instanceof ArrayType)
            {
                final TypeInstantiation arrayElementInst = ((ArrayType)node).getElementTypeInstantiation();
                node = (arrayElementInst.getInstantiatedParameters().isEmpty()) ?
                        arrayElementInst.getTypeReference().getType() : arrayElementInst;
            }
        }

        while (node instanceof ArrayType)
        {
            final TypeReference elementBaseTypeReference =
                    ((ArrayType)node).getElementTypeInstantiation().getTypeReference().getBaseTypeReference();
            node = elementBaseTypeReference.getType();
            style = "arrayLink";
            category += "array of ";
        }

        if (node instanceof BuiltInType)
        {
            style = "noStyle";
        }
        else
        {
            // generate styles depending on the ast node

            if (node instanceof StructureType)
            {
                style = "structureLink";
                category += createTitle("Structure");
            }
            else if (node instanceof ChoiceType)
            {
                style = "choiceLink";
                category += createTitle("Choice");
            }
            else if (node instanceof UnionType)
            {
                style = "unionLink";
                category += createTitle("Union");
            }
            else if (node instanceof EnumType)
            {
                style = "enumLink";
                category += createTitle("Enum");
            }
            else if (node instanceof Subtype)
            {
                style = "subtypeLink";
                category += createTitle("Subtype");
            }
            else if (node instanceof ConstType)
            {
                style = "consttypeLink";
                category += createTitle("Consttype");
            }
            else if (node instanceof SqlTableType)
            {
                style = "sqlTableLink";
                category += createTitle("SQL Table");
            }
            else if (node instanceof SqlDatabaseType)
            {
                style = "sqlDBLink";
                category += createTitle("SQL Database");
            }
            else if (node instanceof ServiceType)
            {
                style = "serviceLink";
                category += createTitle("Service");
            }
            else if (node instanceof TypeInstantiation)
            {
                style = "instantLink";
                category += createTitle("TypeInstantiation");
            }
            else if (node instanceof TypeReference)
            {
                style = "referenceLink";
                category += createTitle("TypeReference");
            }
            else
            {
                style = "noStyle";
            }
        }
    }

    private String createTitle(String cat)
    {
        String packageName = "";
        if (isDoubleDefinedType)
        {
            packageName = ", defined in: " + type.getPackage().getPackageName();
        }
        return cat + packageName;
    }

    public String getName() throws ZserioEmitException
    {
        String typeName = TypeNameEmitter.getTypeName(type);
        return typeName;
    }

    public String getHyperlinkName() throws ZserioEmitException
    {
        String hyperlinkName = TypeNameEmitter.getTypeName(type) + "_";

        ZserioType ti  = type;
        HtmlModuleNameSuffixVisitor suffixVisitor = new HtmlModuleNameSuffixVisitor();
        ti.accept(suffixVisitor);
        hyperlinkName += suffixVisitor.getSuffix();

        return hyperlinkName;
    }

    public String getStyle()
    {
        return style;
    }

    public String getCategory()
    {
        return category;
    }

    public boolean getIsBuiltIn()
    {
        return (type instanceof BuiltInType);
    }

    public String getPackageName()
    {
        String result = "";
        if( type.getPackage()!=null )
        {
            result = type.getPackage().getPackageName().toString();
        }
        return result;
    }

    public String getPackageNameAsID()
    {
        return getPackageName().replace('.', '_');
    }
}
