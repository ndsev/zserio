package zserio.extension.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.DocTagSee;
import zserio.ast.Package;
import zserio.ast.PackageSymbol;
import zserio.ast.ScopeSymbol;
import zserio.ast.SymbolReference;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioType;

class SymbolTemplateDataCreator
{
    public static SymbolTemplateData createData(TemplateDataContext context, AstNode node)
    {
        final String name = AstNodeNameMapper.getName(node);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final Package pkg = AstNodePackageMapper.getPackage(node);
        final List<SymbolTemplateData> templateArguments =
                AstNodeTemplateArgumentsMapper.getTemplateArguments(node, context);

        if (pkg == null)
        {
            final String htmlTitle = typeName;
            return new SymbolTemplateData(name, htmlTitle, templateArguments);
        }
        else
        {
            final String htmlTitle = createHtmlTitle(typeName, pkg);
            final String htmlLinkPage = createHtmlLinkPage(context, pkg);
            final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

            return new SymbolTemplateData(name, htmlTitle, htmlLinkPage, htmlLinkAnchor, templateArguments);
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

        return new SymbolTemplateData(name, htmlTitle, htmlLinkPage, htmlLinkAnchor,
                new ArrayList<SymbolTemplateData>());
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
            return new SymbolTemplateData(alias, "Unknown link", new ArrayList<SymbolTemplateData>());
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

        return new SymbolTemplateData(memberName, htmlTitle, htmlLinkPage, htmlLinkAnchor,
                new ArrayList<SymbolTemplateData>());
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
}
