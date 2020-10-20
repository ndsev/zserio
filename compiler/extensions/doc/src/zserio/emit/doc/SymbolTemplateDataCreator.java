package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zserio.ast.AstNode;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;

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
            final String htmlClass = "withoutLink";
            final String htmlTitle = typeName;

            return new SymbolTemplateData(name, htmlClass, htmlTitle, templateArguments);
        }
        else
        {
            final String htmlClass = createHtmlClass(typeName);
            final String htmlTitle = createHtmlTitle(typeName, packageName);
            final String htmlLinkPage = createHtmlLinkPage(context, packageName);
            final String htmlLinkAnchor = createHtmlAnchor(typeName, name);

            return new SymbolTemplateData(name, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor,
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
        final String memberTypeName = AstNodeTypeNameMapper.getTypeName(member);
        final String htmlClass = createHtmlClass(memberTypeName);

        final PackageName zserioPackageName = zserioType.getPackage().getPackageName();
        final String htmlTitle = createHtmlTitle(memberTypeName, zserioPackageName);

        final String htmlLinkPage = createHtmlLinkPage(context, zserioPackageName);

        final String zserioTypeName = AstNodeTypeNameMapper.getTypeName(zserioType);
        final String zserioName = zserioType.getName();
        final String htmlLinkAnchor = createHtmlAnchor(zserioTypeName, zserioName) + "_" +
                createHtmlAnchor(memberTypeName, memberName);

        return new SymbolTemplateData(memberName, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor,
                new ArrayList<SymbolTemplateData>());
    }

    private static String createHtmlClass(String typeName)
    {
        final String htmlClassPrefix = typeName.substring(0, 1).toLowerCase(Locale.ENGLISH) +
                typeName.substring(1);

        return htmlClassPrefix + "Link";
    }

    private static String createHtmlTitle(String typeName, PackageName packageName)
    {
        return (packageName.isEmpty()) ? typeName : typeName + " defined in " + packageName.toString();
    }

    private static String createHtmlLinkPage(TemplateDataContext context, PackageName packageName)
    {
        final String htmlContentDirectory = context.getHtmlContentDirectory();

        return PackageEmitter.getPackageHtmlLink(packageName, htmlContentDirectory);
    }

    private static String createHtmlAnchor(String typeName, String name)
    {
        return typeName + "_" + name;
    }
}
