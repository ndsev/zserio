package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zserio.ast.BitmaskValue;
import zserio.ast.Constant;
import zserio.ast.DocComment;
import zserio.ast.DocCommentClassic;
import zserio.ast.DocElement;
import zserio.ast.DocLine;
import zserio.ast.DocLineElement;
import zserio.ast.DocMultiline;
import zserio.ast.DocParagraph;
import zserio.ast.DocTagDeprecated;
import zserio.ast.DocTagParam;
import zserio.ast.DocTagSee;
import zserio.ast.DocTagTodo;
import zserio.ast.DocText;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Package;
import zserio.ast.PackageSymbol;
import zserio.ast.Parameter;
import zserio.ast.PubsubMessage;
import zserio.ast.ScopeSymbol;
import zserio.ast.ServiceMethod;
import zserio.ast.SqlTableType;
import zserio.ast.SymbolReference;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for documentation comments.
 */
public class DocCommentsTemplateData
{
    public DocCommentsTemplateData(TemplateDataContext context, List<DocComment> docComments)
            throws ZserioExtensionException
    {
        docParagraphs = new ArrayList<DocParagraphData>();
        for (int i = 0; i < docComments.size(); ++i)
        {
            final DocCommentClassic docCommentClassic = docComments.get(i).toClassic();
            for (DocParagraph docParagraph : docCommentClassic.getParagraphs())
                docParagraphs.add(new DocParagraphData(context, docParagraph));
        }
    }

    public Iterable<DocParagraphData> getParagraphs()
    {
        return docParagraphs;
    }

    public static class DocParagraphData
    {
        public DocParagraphData(TemplateDataContext context, DocParagraph docParagraph)
                throws ZserioExtensionException
        {
            for (DocElement docElement : docParagraph.getDocElements())
                docElements.add(new DocElementData(context, docElement));
        }

        public Iterable<DocElementData> getElements()
        {
            return docElements;
        }

        public static class DocElementData
        {
            public DocElementData(TemplateDataContext context, DocElement docElement)
                    throws ZserioExtensionException
            {
                final DocMultiline multiline = docElement.getDocMultiline();
                this.multiline = (multiline != null) ? new DocMultilineData(context, multiline) : null;

                final DocTagSee seeTag = docElement.getSeeTag();
                this.seeTag = (seeTag != null) ? new DocTagSeeData(context, seeTag) : null;

                final DocTagTodo todoTag = docElement.getTodoTag();
                this.todoTag = (todoTag != null) ? new DocMultilineData(context, todoTag) : null;

                final DocTagParam paramTag = docElement.getParamTag();
                this.paramTag = (paramTag != null) ? new DocTagParamData(context, paramTag) : null;

                final DocTagDeprecated deprecatedTag = docElement.getDeprecatedTag();
                this.isDeprecated = deprecatedTag != null;
            }

            public DocMultilineData getMultiline()
            {
                return multiline;
            }

            public DocTagSeeData getSeeTag()
            {
                return seeTag;
            }

            public DocMultilineData getTodoTag()
            {
                return todoTag;
            }

            public DocTagParamData getParamTag()
            {
                return paramTag;
            }

            public boolean getIsDeprecated()
            {
                return isDeprecated;
            }

            private final DocMultilineData multiline;
            private final DocTagSeeData seeTag;
            private final DocMultilineData todoTag;
            private final DocTagParamData paramTag;
            private final boolean isDeprecated;
        }

        private final List<DocElementData> docElements = new ArrayList<DocElementData>();
    }

    public static class DocMultilineData
    {
        public DocMultilineData(TemplateDataContext context, DocMultiline docMultiline)
                throws ZserioExtensionException
        {
            for (DocLine docLine : docMultiline.getLines())
                lines.add(new DocLineData(context, docLine));
        }

        public Iterable<DocLineData> getLines()
        {
            return lines;
        }

        public static class DocLineData
        {
            public DocLineData(TemplateDataContext context, DocLine docLine) throws ZserioExtensionException
            {
                for (DocLineElement docLineElement : docLine.getLineElements())
                {
                    lineElements.add(new DocLineElementData(context, docLineElement));
                }
            }

            public Iterable<DocLineElementData> getLineElements()
            {
                return lineElements;
            }

            private final List<DocLineElementData> lineElements = new ArrayList<DocLineElementData>();
        }

        public static class DocLineElementData
        {
            public DocLineElementData(TemplateDataContext context, DocLineElement docLineElement)
                    throws ZserioExtensionException
            {
                final DocText docText = docLineElement.getDocText();
                docString = (docText != null) ? docText.getText() : null;

                final DocTagSee docTagSee = docLineElement.getSeeTag();
                seeTag = (docTagSee != null) ? new DocTagSeeData(context, docTagSee) : null;
            }

            public String getDocString()
            {
                return docString;
            }

            public DocTagSeeData getSeeTag()
            {
                return seeTag;
            }

            private final String docString;
            private final DocTagSeeData seeTag;
        }

        private final List<DocLineData> lines = new ArrayList<DocLineData>();
    }

    public static class DocTagSeeData
    {
        public DocTagSeeData(TemplateDataContext context, DocTagSee docTagSee) throws ZserioExtensionException
        {
            alias = docTagSee.getLinkAlias();

            final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
            final SymbolReference symbolReference = docTagSee.getLinkSymbolReference();
            final Package referencedPackage = symbolReference.getReferencedPackage();
            final PackageSymbol referencedPackageSymbol = symbolReference.getReferencedPackageSymbol();
            final ScopeSymbol referencedScopeSymbol = symbolReference.getReferencedScopeSymbol();

            if (referencedPackage == null)
            {
                // link cannot be resolved
                link = docTagSee.getLinkName();
            }
            else if (referencedPackageSymbol == null)
            {
                // link is a package
                link = CppFullNameFormatter.getFullName(referencedPackage.getPackageName());
            }
            else if (referencedScopeSymbol == null)
            {
                // link is a package symbol
                link = getPackageSymbolFullName(cppNativeMapper, referencedPackageSymbol);
            }
            else
            {
                // link is a scope symbol - full name without leading "::" which doxygen doesn't like
                link = getPackageSymbolFullName(cppNativeMapper, referencedPackageSymbol).substring(2) +
                        getScopeSymbolSuffix(referencedPackageSymbol, referencedScopeSymbol);
            }
        }

        public String getAlias()
        {
            return alias;
        }

        public String getLink()
        {
            return link;
        }

        private static String getPackageSymbolFullName(CppNativeMapper cppNativeMapper,
                PackageSymbol packageSymbol) throws ZserioExtensionException
        {
            if (packageSymbol instanceof ZserioType)
            {
                return cppNativeMapper.getCppType((ZserioType)packageSymbol).getFullName();
            }
            else if (packageSymbol instanceof Constant)
            {
                return cppNativeMapper.getCppSymbol((Constant)packageSymbol).getFullName();
            }
            else
            {
                throw new ZserioExtensionException("Unhandled symbol '" + packageSymbol.getClass().getName() +
                        "' in DocCommentsTemplateData!");
            }
        }

        private static String getScopeSymbolSuffix(PackageSymbol packageSymbol, ScopeSymbol scopeSymbol)
        {
            if (scopeSymbol instanceof BitmaskValue)
            {
                return "::Values::" + scopeSymbol.getName();
            }
            else if (scopeSymbol instanceof Field) // including SQL tables and columns
            {
                final String getterName = AccessorNameFormatter.getGetterName((Field)scopeSymbol);
                if (packageSymbol instanceof SqlTableType)
                    return "::Row::" + getterName; // getter for column in SQL table
                else
                    return "::" + getterName;
            }
            else if (scopeSymbol instanceof Parameter)
            {
                return "::" + AccessorNameFormatter.getGetterName((Parameter)scopeSymbol);
            }
            else if (scopeSymbol instanceof Function)
            {
                return "::" + AccessorNameFormatter.getFunctionName((Function)scopeSymbol);
            }
            else if (scopeSymbol instanceof ServiceMethod)
            {
                return "::Client::" + scopeSymbol.getName() + "Method";
            }
            else if (scopeSymbol instanceof PubsubMessage)
            {
                final String messageName = scopeSymbol.getName();
                return "::publish" + messageName.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                        messageName.substring(1);
            }
            else // no special handling for other symbols
            {
                return "::" + scopeSymbol.getName();
            }
        }

        private final String alias;
        private final String link;
    }

    public static class DocTagParamData
    {
        public DocTagParamData(TemplateDataContext context, DocTagParam docTagParam)
                throws ZserioExtensionException
        {
            name = docTagParam.getParamName();
            description = new DocMultilineData(context, docTagParam);
        }

        public String getName()
        {
            return name;
        }

        public DocMultilineData getDescription()
        {
            return description;
        }

        private final String name;
        private final DocMultilineData description;
    }

    private final List<DocParagraphData> docParagraphs;
}
