package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.ast.Package;

class SymbolTemplateDataCreator
{
    public static SymbolTemplateData createData(TemplateDataContext context, AstNode node)
    {
        final String name = AstNodeNameMapper.getName(node);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final PackageName packageName = AstNodePackageNameMapper.getPackageName(node);
        final List<SymbolTemplateData> templateArguments =
                AstNodeTemplateArgumentsMapper.getTemplateArguments(node, context);

        if (packageName == null)
        {
            final String htmlTitle = typeName;
            return new SymbolTemplateData(name, htmlTitle, templateArguments);
        }
        else
        {
            final String htmlTitle = createHtmlTitle(typeName, packageName);
            final String htmlLinkPage = createHtmlLinkPage(context, packageName);
            final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

            return new SymbolTemplateData(name, htmlTitle, htmlLinkPage, htmlLinkAnchor, templateArguments);
        }
    }

    public static SymbolTemplateData createData(TemplateDataContext context, Package pkg)
    {
        final String name = AstNodeNameMapper.getName(pkg);
        final String typeName = AstNodeTypeNameMapper.getTypeName(pkg);
        final String htmlTitle = typeName + " " + name;
        final PackageName packageName = AstNodePackageNameMapper.getPackageName(pkg);
        final String htmlLinkPage = createHtmlLinkPage(context, packageName);
        final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

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

        final PackageName zserioPackageName = AstNodePackageNameMapper.getPackageName(zserioType);
        final String htmlTitle = createHtmlTitle(memberTypeName, zserioPackageName);

        final String htmlLinkPage = createHtmlLinkPage(context, zserioPackageName);

        final String zserioTypeName = AstNodeTypeNameMapper.getTypeName(zserioType);
        final String zserioName = zserioType.getName();
        final String htmlLinkAnchor = createHtmlAnchor(zserioTypeName, zserioName) + "_" +
                createHtmlAnchor(memberTypeName, memberName);

        return new SymbolTemplateData(memberName, htmlTitle, htmlLinkPage, htmlLinkAnchor,
                new ArrayList<SymbolTemplateData>());
    }

    private static String createHtmlTitle(String typeName, PackageName packageName)
    {
        return (packageName.isEmpty()) ? typeName : typeName + " defined in " + packageName.toString();
    }

    private static String createHtmlLinkPage(TemplateDataContext context, PackageName packageName)
    {
        final String contentDirectory = context.getContentDirectory();

        return PackageEmitter.getPackageHtmlLink(packageName, contentDirectory);
    }

    private static String createHtmlAnchor(String typeName, String name)
    {
        // replace needed due to multi-level packages
        return typeName + "_" + name.replace('.', '_');
    }
}
