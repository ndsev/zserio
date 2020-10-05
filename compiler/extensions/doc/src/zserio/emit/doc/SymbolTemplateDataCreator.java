package zserio.emit.doc;

import java.util.Locale;

import zserio.ast.AstNode;
import zserio.ast.PackageName;
import zserio.emit.common.PackageMapper;

class SymbolTemplateDataCreator
{
    public static SymbolTemplateData createData(TemplateDataContext context, AstNode node)
    {
        final PackageMapper packageMapper = context.getPackageMapper();
        final PackageName packageName = AstNodePackageNameMapper.getPackageName(node, packageMapper);
        final String name = AstNodeNameMapper.getName(node);
        // TODO[mikir] not correct, what about bitmask value => packageName is empty as well
        if (packageName.isEmpty())
        {
            final String htmlClass = "builtInType";
            final String htmlTitle = "Built-in type";

            return new SymbolTemplateData(name, htmlClass, htmlTitle);
        }
        else
        {
            final String typeName = AstNodeTypeNameMapper.getTypeName(node);
            final String htmlClassPrefix = typeName.substring(0, 1).toLowerCase(Locale.ENGLISH) +
                    typeName.substring(1);
            final String htmlClass = htmlClassPrefix + "Link";
            final String htmlTitle = typeName + " defined in " + packageName.toString();
            // TODO[mikir] Use joiner to fix empty string in htmlContentDirectory or typeCollaborationDirectory
            // TODO[mikir] html extension
            final String htmlContentDirectory = context.getHtmlContentDirectory();
            final String htmlLinkPage = htmlContentDirectory + "/" + packageName.toString() + ".html";
            final String htmlLinkAnchor = typeName + "_" + name;

            return new SymbolTemplateData(name, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor);
        }
    }
}
