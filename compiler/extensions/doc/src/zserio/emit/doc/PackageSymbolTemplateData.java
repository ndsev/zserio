package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.TemplateParameter;
import zserio.ast.ZserioTemplatableType;

public class PackageSymbolTemplateData implements Comparable<PackageSymbolTemplateData>
{
    public PackageSymbolTemplateData(TemplateDataContext context, AstNode packageSymbol)
    {
        if (packageSymbol instanceof ZserioTemplatableType)
        {
            for (TemplateParameter templateParameter :
                    ((ZserioTemplatableType)packageSymbol).getTemplateParameters())
            {
                templateParameters.add(templateParameter.getName());
            }
        }
        this.symbol = SymbolTemplateDataCreator.createData(context, packageSymbol);
    }

    @Override
    public int compareTo(PackageSymbolTemplateData other)
    {
        int result = symbol.getName().compareTo(other.symbol.getName());
        return result;
    }

    @Override
    public boolean equals(Object other)
    {
        if ( !(other instanceof PackageSymbolTemplateData) )
            return false;

        return (this == other) || compareTo((PackageSymbolTemplateData)other) == 0;
    }

    @Override
    public int hashCode()
    {
        return symbol.getName().hashCode();
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public List<String> getTemplateParameters()
    {
        return templateParameters;
    }

    private final SymbolTemplateData symbol;
    private final List<String> templateParameters = new ArrayList<String>();
}
