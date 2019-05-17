package zserio.emit.doc;

import zserio.ast.ArrayType;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.ServiceType;
import zserio.ast.ZserioTypeUtil;
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


    public LinkedType(ZserioType type)
    {
        this.type = type;
        this.isDoubleDefinedType = false;
        init();
    }

    public LinkedType(ZserioType type, boolean isDoubleDefinedType)
    {
        this.type = type;
        this.isDoubleDefinedType = isDoubleDefinedType;
        init();
    }

    private void init()
    {
        while (type instanceof ArrayType)
        {
            type = TypeReference.resolveBaseType(((ArrayType) type).getElementType());
            style = "arrayLink";
            category += "array of ";
        }

        if (ZserioTypeUtil.isBuiltIn(type))
        {
            style = "noStyle";
        }
        else
        {
            // generate styles depending on the field type

            if (type instanceof StructureType)
            {
                style = "structureLink";
                category += createTitle("Structure");
            }
            else if (type instanceof ChoiceType)
            {
                style = "choiceLink";
                category += createTitle("Choice");
            }
            else if (type instanceof UnionType)
            {
                style = "unionLink";
                category += createTitle("Union");
            }
            else if (type instanceof EnumType)
            {
                style = "enumLink";
                category += createTitle("Enum");
            }
            else if (type instanceof Subtype)
            {
                style = "subtypeLink";
                category += createTitle("Subtype");
            }
            else if (type instanceof ConstType)
            {
                style = "consttypeLink";
                category += createTitle("Consttype");
            }
            else if (type instanceof SqlTableType)
            {
                style = "sqlTableLink";
                category += createTitle("SQL Table");
            }
            else if (type instanceof SqlDatabaseType)
            {
                style = "sqlDBLink";
                category += createTitle("SQL Database");
            }
            else if (type instanceof ServiceType)
            {
                style = "serviceLink";
                category += createTitle("Service");
            }
            else if (type instanceof TypeInstantiation)
            {
                style = "instantLink";
                category += createTitle("TypeInstantiation");
            }
            else if (type instanceof TypeReference)
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
        if (type instanceof TypeReference)
        {
            ti = TypeReference.resolveType(type);
        }
        else if (ti instanceof TypeInstantiation)
        {
            ti = ((TypeInstantiation) ti).getBaseType();
        }

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
        return ZserioTypeUtil.isBuiltIn(type);
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
