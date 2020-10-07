package zserio.emit.doc;

import java.util.Locale;

import zserio.ast.AstNode;
import zserio.ast.BuiltInType;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.PackageMapper;
import zserio.tools.StringJoinUtil;

class SymbolTemplateDataCreator
{
    public static SymbolTemplateData createData(TemplateDataContext context, AstNode node)
    {
        final String name = AstNodeNameMapper.getName(node);
        final PackageMapper packageMapper = context.getPackageMapper();
        final PackageName packageName = AstNodePackageNameMapper.getPackageName(node, packageMapper);
        if (packageName.isEmpty())
        {
            final String htmlClass = "withoutLink";
            final String htmlTitle = "Built-in type";

            return new SymbolTemplateData(name, htmlClass, htmlTitle);
        }
        else
        {
            final String typeName = AstNodeTypeNameMapper.getTypeName(node);
            final String packageNameString = packageName.toString();
            final String htmlClass = createHtmlClass(typeName);
            final String htmlTitle = createHtmlTitle(typeName, packageNameString);
            final String htmlLinkPage = createHtmlLinkPage(context, packageNameString);
            final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

            return new SymbolTemplateData(name, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor);
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
        final String memberTypeName = AstNodeTypeNameMapper.getTypeName(member);
        final String htmlClass = createHtmlClass(memberTypeName);

        final PackageMapper packageMapper = context.getPackageMapper();
        final String zserioPackageName = packageMapper.getPackageName(zserioType.getPackage()).toString();
        final String htmlTitle = createHtmlTitle(memberTypeName, zserioPackageName);

        final String htmlLinkPage = createHtmlLinkPage(context, zserioPackageName);

        final String zserioTypeName = AstNodeTypeNameMapper.getTypeName(zserioType);
        final String zserioName = zserioType.getName();
        final String htmlLinkAnchor = createHtmlAnchor(zserioTypeName, zserioName) + "_" +
                createHtmlAnchor(memberTypeName, memberName);

        return new SymbolTemplateData(memberName, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor);
    }

    private static String createHtmlClass(String typeName)
    {
        final String htmlClassPrefix = typeName.substring(0, 1).toLowerCase(Locale.ENGLISH) +
                typeName.substring(1);

        return htmlClassPrefix + "Link";
    }

    private static String createHtmlTitle(String typeName, String packageName)
    {
        return typeName + " defined in " + packageName;
    }

    private static String createHtmlLinkPage(TemplateDataContext context, String packageName)
    {
        // TODO[mikir] html extension
        final String htmlContentDirectory = context.getHtmlContentDirectory();

        return StringJoinUtil.joinStrings(htmlContentDirectory, packageName, "/") + ".html";
    }

    private static String createHtmlAnchor(String typeName, String name)
    {
        return typeName + "_" + name;
    }
}
