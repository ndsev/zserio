package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.Package;
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
        return typeName + "_" + name;
    }
}
