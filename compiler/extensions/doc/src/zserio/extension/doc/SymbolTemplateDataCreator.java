package zserio.extension.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.DocTagSee;
import zserio.ast.Expression;
import zserio.ast.Package;
import zserio.ast.PackageSymbol;
import zserio.ast.ScopeSymbol;
import zserio.ast.SymbolReference;
import zserio.ast.TemplateArgument;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;

/**
 * Creator for FreeMarker template data for symbol used by Package emitter.
 *
 * This creator creates FreeMarker template data for symbol from AST node. It covers all necessary alternatives.
 */
class SymbolTemplateDataCreator
{
    public static SymbolTemplateData createData(TemplateDataContext context, AstNode node)
    {
        final String name = AstNodeNameMapper.getName(node);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final Package pkg = AstNodePackageMapper.getPackage(node);
        final List<SymbolTemplateData> templateArguments = createTemplateArguments(context, node);

        if (pkg == null)
        {
            final String htmlTitle = typeName;
            return new SymbolTemplateData(name, typeName, htmlTitle, templateArguments);
        }
        else
        {
            final String htmlTitle = createHtmlTitle(typeName, pkg);
            final String htmlLinkPage = createHtmlLinkPage(context, pkg);
            final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

            return new SymbolTemplateData(name, typeName, htmlTitle, htmlLinkPage, htmlLinkAnchor,
                    templateArguments);
        }
    }

    // in case of a template instantiation, use instantiation reference instead
    public static SymbolTemplateData createTemplateInstantiationReferenceData(
            TemplateDataContext context, AstNode node)
    {
        if (node instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType instance = (ZserioTemplatableType)node;
            final ZserioTemplatableType template = instance.getTemplate();
            if (template != null)
            {
                final Iterator<TypeReference> instantiationReferenceIterator =
                        instance.getInstantiationReferenceStack().iterator();
                if (instantiationReferenceIterator.hasNext())
                    return createData(context, instantiationReferenceIterator.next());
            }
        }

        return createData(context, node);
    }

    public static SymbolTemplateData createData(TemplateDataContext context, Package pkg)
    {
        final String name = AstNodeNameMapper.getName(pkg);
        final String typeName = AstNodeTypeNameMapper.getTypeName(pkg);
        final String htmlTitle = typeName + " " + name;
        final String htmlLinkPage = createHtmlLinkPage(context, pkg);
        final String htmlLinkAnchor = typeName;

        return new SymbolTemplateData(name, typeName, htmlTitle, htmlLinkPage, htmlLinkAnchor);
    }

    public static SymbolTemplateData createData(TemplateDataContext context, Expression expression)
            throws ZserioExtensionException
    {
        if (expression.getExprZserioType() != null && expression.getExprSymbolObject() != null)
        {
            return createData(context, expression.getExprZserioType(), expression.getExprSymbolObject());
        }

        final String name = context.getExpressionFormatter().formatGetter(expression);
        String typeName = AstNodeTypeNameMapper.getTypeName(expression);
        if (expression.getExprZserioType() != null)
        {
            typeName += " of type " + AstNodeNameMapper.getName(expression.getExprZserioType());
        }

        return new SymbolTemplateData(name, typeName, typeName);
    }

    public static SymbolTemplateData createData(TemplateDataContext context, ZserioType zserioType,
            AstNode member)
    {
        return createData(context, zserioType, member, AstNodeNameMapper.getName(member));
    }

    public static SymbolTemplateData createData(TemplateDataContext context, DocTagSee docTagSee)
    {
        final String alias = docTagSee.getLinkAlias();
        final SymbolReference linkSymbolReference = docTagSee.getLinkSymbolReference();
        final PackageSymbol referencedPackageSymbol = linkSymbolReference.getReferencedPackageSymbol();
        final ScopeSymbol referencedScopeSymbol = linkSymbolReference.getReferencedScopeSymbol();
        if (referencedPackageSymbol == null)
        {
            // this can happen if see tag link is invalid
            return new SymbolTemplateData(alias, "UnknownTypeName", "Unknown link");
        }
        else if (referencedScopeSymbol == null)
        {
            return new SymbolTemplateData(alias, SymbolTemplateDataCreator.createData(context,
                    referencedPackageSymbol));
        }
        else
        {
            return new SymbolTemplateData(alias, SymbolTemplateDataCreator.createData(context,
                    (ZserioType)referencedPackageSymbol, referencedScopeSymbol));
        }
    }

    public static SymbolTemplateData createData(TemplateDataContext context, ZserioType zserioType,
            AstNode member, String memberName)
    {
        final String memberTypeName = AstNodeTypeNameMapper.getTypeName(member);

        final Package zserioPackage = AstNodePackageMapper.getPackage(zserioType);
        final String htmlTitle = createHtmlTitle(memberTypeName, zserioPackage);

        final String htmlLinkPage = createHtmlLinkPage(context, zserioPackage);

        final String zserioTypeName = AstNodeTypeNameMapper.getTypeName(zserioType);
        final String zserioName = zserioType.getName();
        final String htmlLinkAnchor = createHtmlAnchor(zserioTypeName, zserioName) + "_" +
                createHtmlAnchor(memberTypeName, memberName);

        return new SymbolTemplateData(memberName, memberTypeName, htmlTitle, htmlLinkPage, htmlLinkAnchor);
    }

    private static String createHtmlTitle(String typeName, Package pkg)
    {
        final String packageNameString = AstNodeNameMapper.getName(pkg);

        return typeName + " defined in " + packageNameString;
    }

    private static String createHtmlLinkPage(TemplateDataContext context, Package pkg)
    {
        final String contentDirectory = context.getContentDirectory();

        return PackageEmitter.getPackageHtmlLink(pkg, contentDirectory);
    }

    private static String createHtmlAnchor(String typeName, String name)
    {
        return typeName + "_" + name.replaceAll("\\s", "_");
    }

    private static List<SymbolTemplateData> createTemplateArguments(TemplateDataContext context, AstNode node)
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
