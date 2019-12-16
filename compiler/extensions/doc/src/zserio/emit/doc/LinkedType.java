package zserio.emit.doc;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BuiltInType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.ParameterizedTypeInstantiation;
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
    private AstNode astNode;
    private final boolean isDoubleDefinedType;
    private String style;
    private String category = "";

    public LinkedType(AstNode node) throws ZserioEmitException
    {
        this.isDoubleDefinedType = false;
        init(node);
    }

    public LinkedType(AstNode node, boolean isDoubleDefinedType) throws ZserioEmitException
    {
        this.isDoubleDefinedType = isDoubleDefinedType;
        init(node);
    }

    private void init(AstNode node) throws ZserioEmitException
    {
        if (node instanceof ZserioType)
            this.astNode = (ZserioType)node;
        else if (node instanceof TypeReference)
            this.astNode = ((TypeReference)node).getType();
        else
            this.astNode = node;

        if (node instanceof TypeInstantiation)
        {
            if (node instanceof ArrayInstantiation)
            {
                node  = ((ArrayInstantiation)node).getElementTypeInstantiation();
                style = "arrayLink";
                category += "array of ";
            }

            if ((!(node instanceof ParameterizedTypeInstantiation)))
                node = ((TypeInstantiation)node).getType();
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
            else if (node instanceof BitmaskType)
            {
                style = "bitmaskLink";
                category += createTitle("Bitmask");
            }
            else if (node instanceof Subtype)
            {
                style = "subtypeLink";
                category += createTitle("Subtype");
            }
            else if (node instanceof Constant)
            {
                style = "constantLink";
                category += createTitle("Constant");
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
            else if (node instanceof TypeInstantiation) // only when it's a parameterized type
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

    private String createTitle(String cat) throws ZserioEmitException
    {
        String packageName = "";
        if (isDoubleDefinedType)
        {
            packageName = ", defined in: " + getPackageName();
        }
        return cat + packageName;
    }

    public String getName() throws ZserioEmitException
    {
        String typeName = TypeNameEmitter.getTypeName(astNode);
        return typeName;
    }

    public String getHyperlinkName() throws ZserioEmitException
    {
        String hyperlinkName = TypeNameEmitter.getTypeName(astNode) + "_";

        HtmlModuleNameSuffixVisitor suffixVisitor = new HtmlModuleNameSuffixVisitor();
        astNode.accept(suffixVisitor);
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
        AstNode node = astNode;
        if (node instanceof TypeInstantiation)
        {
            if (node instanceof ArrayInstantiation)
                node = ((ArrayInstantiation) node).getElementTypeInstantiation();
            node = ((TypeInstantiation)node).getType();
        }
        return (node instanceof BuiltInType);
    }

    public String getPackageName() throws ZserioEmitException
    {
        return DocEmitterTools.getZserioPackageName(astNode).toString();
    }

    public String getPackageNameAsID() throws ZserioEmitException
    {
        return getPackageName().replace('.', '_');
    }
}
