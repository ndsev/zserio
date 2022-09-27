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
import zserio.ast.Rule;
import zserio.ast.RuleGroup;
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
        return createData(context, node, AstNodeNameMapper.getName(node));
    }

    public static SymbolTemplateData createData(TemplateDataContext context, AstNode node, String alias)
    {
        final String name = AstNodeNameMapper.getName(node);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final Package pkg = AstNodePackageMapper.getPackage(node);
        final List<SymbolTemplateData> templateArguments = createTemplateArguments(context, node);

        if (pkg == null)
        {
            final String htmlTitle = typeName;
            return new SymbolTemplateData(alias, typeName, htmlTitle, templateArguments);
        }
        else
        {
            final String htmlTitle = createHtmlTitle(typeName, pkg);
            final String htmlLinkPage = createHtmlLinkPage(context, pkg);
            final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

            return new SymbolTemplateData(alias, typeName, htmlTitle, htmlLinkPage, htmlLinkAnchor,
                    templateArguments);
        }
    }

    public static SymbolTemplateData createData(TemplateDataContext context, ZserioType zserioType,
            AstNode member)
    {
        return createData(context, zserioType, member, AstNodeNameMapper.getName(member));
    }

    public static SymbolTemplateData createData(TemplateDataContext context, ZserioType zserioType,
            AstNode member, String memberName)
    {
        return createData(context, zserioType, member, memberName, memberName);
    }

    public static SymbolTemplateData createData(TemplateDataContext context, ZserioType zserioType,
            AstNode member, String memberName, String alias)
    {
        final String memberTypeName = AstNodeTypeNameMapper.getTypeName(member);

        final Package zserioPackage = AstNodePackageMapper.getPackage(zserioType);
        final String htmlTitle = createHtmlTitle(memberTypeName, zserioPackage);

        final String htmlLinkPage = createHtmlLinkPage(context, zserioPackage);

        final String zserioTypeName = AstNodeTypeNameMapper.getTypeName(zserioType);
        final String zserioName = zserioType.getName();
        final String htmlLinkAnchor = createHtmlAnchor(zserioTypeName, zserioName) + ANCHOR_SEPARATOR +
                createHtmlAnchor(memberTypeName, memberName);

        return new SymbolTemplateData(alias, memberTypeName, htmlTitle, htmlLinkPage, htmlLinkAnchor);
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
        final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

        return new SymbolTemplateData(name, typeName, htmlTitle, htmlLinkPage, htmlLinkAnchor);
    }

    public static SymbolTemplateData createData(TemplateDataContext context, RuleGroup ruleGroup, Rule rule)
    {
        final String name = AstNodeNameMapper.getName(rule);
        final String typeName = AstNodeTypeNameMapper.getTypeName(rule);

        final Package zserioPackage = AstNodePackageMapper.getPackage(ruleGroup);
        final String htmlTitle = createHtmlTitle(typeName, zserioPackage);
        final String htmlLinkPage = createHtmlLinkPage(context, zserioPackage);
        final String htmlLinkAnchor = name;

        return new SymbolTemplateData(name, typeName, htmlTitle, htmlLinkPage, htmlLinkAnchor);
    }

    public static SymbolTemplateData createData(TemplateDataContext context, Expression expression)
            throws ZserioExtensionException
    {
        final ZserioType expressionZserioType = expression.getExprZserioType();
        final AstNode expressionSymbolObject = expression.getExprSymbolObject();
        final String name = context.getExpressionFormatter().formatGetter(expression);
        if (expressionZserioType != null && expressionSymbolObject != null)
        {
            return createData(context, expressionZserioType, expressionSymbolObject,
                    AstNodeNameMapper.getName(expressionSymbolObject), name);
        }

        final String typeName = AstNodeTypeNameMapper.getTypeName(expression);
        final String htmlTitle = (expressionZserioType == null) ? typeName :
            typeName + " of type " + AstNodeNameMapper.getName(expressionZserioType);

        return new SymbolTemplateData(name, typeName, htmlTitle);
    }

    public static SymbolTemplateData createData(TemplateDataContext context, DocTagSee docTagSee)
    {
        final String alias = docTagSee.getLinkAlias();
        final SymbolReference linkSymbolReference = docTagSee.getLinkSymbolReference();
        final Package referencedPackage = linkSymbolReference.getReferencedPackage();
        final PackageSymbol referencedPackageSymbol = linkSymbolReference.getReferencedPackageSymbol();
        final ScopeSymbol referencedScopeSymbol = linkSymbolReference.getReferencedScopeSymbol();
        if (referencedPackage == null)
        {
            // this can happen if see tag link is invalid
            return new SymbolTemplateData(alias, "UnknownTypeName", "Unknown link");
        }
        else if (referencedPackageSymbol == null)
        {
            // link to package
            return SymbolTemplateDataCreator.createData(context, referencedPackage, alias);
        }
        else if (referencedScopeSymbol == null)
        {
            // link to package symbol
            return SymbolTemplateDataCreator.createData(context, referencedPackageSymbol, alias);
        }
        else
        {
            // link to scope symbol
            return SymbolTemplateDataCreator.createData(context, (ZserioType)referencedPackageSymbol,
                    referencedScopeSymbol, AstNodeNameMapper.getName(referencedScopeSymbol), alias);
        }
    }

    private static String createHtmlTitle(String typeName, Package pkg)
    {
        final String packageNameString = AstNodeNameMapper.getName(pkg);

        return typeName + " defined in " + packageNameString;
    }

    private static String createHtmlLinkPage(TemplateDataContext context, Package pkg)
    {
        return PackageEmitter.getPackageHtmlLink(pkg, context.getPackagesDirectory());
    }

    private static String createHtmlAnchor(String typeName, String name)
    {
        // space can be in name due to case expressions, dot can be in a package name
        return typeName + ANCHOR_SEPARATOR +
                name.replaceAll("\\s", ANCHOR_SEPARATOR).replaceAll("\\.", ANCHOR_SEPARATOR);
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

    private static final String ANCHOR_SEPARATOR = "-";
}
