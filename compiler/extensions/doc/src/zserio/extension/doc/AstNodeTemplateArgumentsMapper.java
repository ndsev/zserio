package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.TemplateArgument;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;

class AstNodeTemplateArgumentsMapper
{
    public static List<SymbolTemplateData> getTemplateArguments(AstNode node, TemplateDataContext context)
    {
        final ArrayList<SymbolTemplateData> templateArguments = new ArrayList<SymbolTemplateData>();

        TypeReference typeReference = null;
        if (node instanceof ArrayInstantiation)
            typeReference = ((ArrayInstantiation)node).getElementTypeInstantiation().getTypeReference();
        else if (node instanceof TypeInstantiation)
            typeReference = ((TypeInstantiation)node).getTypeReference();
        else if (node instanceof TypeReference)
            typeReference = (TypeReference)node;
        if (typeReference != null)
        {
            for (TemplateArgument templateArgument : typeReference.getTemplateArguments())
            {
                templateArguments.add(
                        SymbolTemplateDataCreator.createData(context, templateArgument.getTypeReference()));
            }
        }

        return templateArguments;
    }
}
