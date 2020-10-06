package zserio.emit.doc;

import java.util.Locale;

import zserio.ast.AstNode;
import zserio.ast.ChoiceCase;
import zserio.ast.ChoiceType;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.PackageMapper;
import zserio.tools.StringJoinUtil;

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
            // TODO[mikir] html extension
            final String htmlContentDirectory = context.getHtmlContentDirectory();
            final String htmlLinkPage = StringJoinUtil.joinStrings(htmlContentDirectory, packageName.toString(),
                    "/") + ".html";
            final String htmlLinkAnchor = typeName + "_" + name;

            return new SymbolTemplateData(name, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor);
        }
    }

    public static SymbolTemplateData createData(TemplateDataContext context, ChoiceType choiceType,
            ChoiceCase choiceCase, String choiceCaseExpr)
    {
        final String choiceCaseTypeName  = AstNodeTypeNameMapper.getTypeName(choiceCase);
        final String htmlClassPrefix = choiceCaseTypeName.substring(0, 1).toLowerCase(Locale.ENGLISH) +
                choiceCaseTypeName.substring(1);
        final String htmlClass = htmlClassPrefix + "Link";

        final String choiceName = choiceType.getName();
        final PackageMapper packageMapper = context.getPackageMapper();
        final String choicePackageName = packageMapper.getPackageName(choiceType.getPackage()).toString();
        final String htmlTitle = choiceCaseTypeName + " in " + choiceName + " defined in " + choicePackageName;

        // TODO[mikir] html extension
        // TODO[mikir] choiceCaseExpr can be have operators!!!
        final String choiceTypeName  = AstNodeTypeNameMapper.getTypeName(choiceType);
        final String htmlContentDirectory = context.getHtmlContentDirectory();
        final String htmlLinkPage = StringJoinUtil.joinStrings(htmlContentDirectory, choicePackageName, "/") +
                ".html";
        final String htmlLinkAnchor = choiceTypeName + "_" + choiceName + "_" + choiceCaseTypeName + "_" +
                choiceCaseExpr;

        return new SymbolTemplateData(choiceCaseExpr, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor);
    }

    public static SymbolTemplateData createDataWithTypeName(TemplateDataContext context, ChoiceType choiceType,
            ChoiceCase choiceCase, String choiceCaseExpr)
    {
        final SymbolTemplateData symbolData = createData(context, choiceType, choiceCase, choiceCaseExpr);
        final String fullName = symbolData.getName() + " (" + choiceType.getName() + ")";

        return new SymbolTemplateData(fullName, symbolData.getHtmlClass(), symbolData.getHtmlTitle(),
                symbolData.getHtmlLink());
    }

    public static SymbolTemplateData createData(TemplateDataContext context, ZserioType zserioType,
            AstNode member)
    {
        final String memberTypeName  = AstNodeTypeNameMapper.getTypeName(member);
        final String htmlClassPrefix = memberTypeName.substring(0, 1).toLowerCase(Locale.ENGLISH) +
                memberTypeName.substring(1);
        final String htmlClass = htmlClassPrefix + "Link";

        final String zserioName = zserioType.getName();
        final PackageMapper packageMapper = context.getPackageMapper();
        final String zserioPackageName = packageMapper.getPackageName(zserioType.getPackage()).toString();
        final String htmlTitle = memberTypeName + " in " + zserioName + " defined in " + zserioPackageName;

        // TODO[mikir] html extension
        // TODO[mikir] memberExpr can be have operators!!!
        final String memberName  = AstNodeNameMapper.getName(member);
        final String zserioTypeName  = AstNodeTypeNameMapper.getTypeName(zserioType);
        final String htmlContentDirectory = context.getHtmlContentDirectory();
        final String htmlLinkPage = StringJoinUtil.joinStrings(htmlContentDirectory, zserioPackageName, "/") +
                ".html";
        final String htmlLinkAnchor = zserioTypeName + "_" + zserioName + "_" + memberTypeName + "_" +
                memberName;

        return new SymbolTemplateData(memberName, htmlClass, htmlTitle, htmlLinkPage, htmlLinkAnchor);
    }

    public static SymbolTemplateData createDataWithTypeName(TemplateDataContext context, ZserioType zserioType,
            AstNode member)
    {
        final SymbolTemplateData symbolData = createData(context, zserioType, member);
        final String fullName = zserioType.getName() + "." + symbolData.getName();

        return new SymbolTemplateData(fullName, symbolData.getHtmlClass(), symbolData.getHtmlTitle(),
                symbolData.getHtmlLink());
    }
}
