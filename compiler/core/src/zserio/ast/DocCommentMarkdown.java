package zserio.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a single documentation comment in markdown style.
 */
public final class DocCommentMarkdown extends DocComment
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param markdown Markdown documentation.
     * @param isSticky True if the Markdown documentation comment is not followed by blank line.
     * @param isOneLiner True if the documentation comment is on one line in the source.
     * @param isIndented True if all lines are indented at least as the begin-comment delimiter.
     */
    public DocCommentMarkdown(AstLocation location, String markdown, boolean isSticky, boolean isOneLiner,
            boolean isIndented)
    {
        super(location, isSticky, isOneLiner);

        this.markdown = markdown;
        // when isIndented is true, the beginning indent is already stripped out of all lines
        this.isIndented = isIndented;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocCommentMarkdown(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
    }

    @Override
    public DocComment findParamDoc(String paramName)
    {
        return null;
    }

    /**
     * Converts this markdown documentation comment to the classic documentation comment.
     *
     * The following actions during conversion are performed:
     *
     * 1. Markdown documentation comments is split up to the paragraphs according to the empty lines.
     * 2. Each markdown link in format [alias](../path/schema_file.zs#zserio_type) is converted to the see tag.
     *
     * @return Converted classic documentation comment.
     */
    @Override
    public DocCommentClassic toClassic()
    {
        return classic;
    }

    /**
     * Gets markdown documentation.
     *
     * @return Markdown documentation.
     */
    public String getMarkdown()
    {
        return markdown;
    }

    void resolve(Package ownerPackage, ZserioAstSymbolResolver currentResolver)
    {
        final String[] markdownLines = markdown.split("\r?\n|\r");

        final ArrayList<ArrayList<MarkdownLine>> markdownParagraphs = new ArrayList<ArrayList<MarkdownLine>>();
        ArrayList<MarkdownLine> markdownParagraphLines = new ArrayList<MarkdownLine>();
        final String fileName = getLocation().getFileName();
        int line = getLocation().getLine();
        for (String markdownLine : markdownLines)
        {
            // swallow empty lines
            if (!markdownLine.trim().isEmpty())
            {
                int column = line == getLocation().getLine()
                        ? getLocation().getColumn() + 3 /* first line: +3 for begin-comment delimiter '/*!' */
                        : (isIndented ? getLocation().getColumn() : 1);
                // add leading white spaces which will be trimmed below
                column += markdownLine.length() - markdownLine.replaceFirst("^\\s+", "").length();

                final AstLocation location = new AstLocation(fileName, line, column - 1);
                markdownParagraphLines.add(new MarkdownLine(location, markdownLine.trim()));
            }
            else
            {
                 if (!markdownParagraphLines.isEmpty())
                 {
                    markdownParagraphs.add(markdownParagraphLines);
                    markdownParagraphLines = new ArrayList<MarkdownLine>();
                 }
            }

            line += 1;
        }

        if (!markdownParagraphLines.isEmpty())
            markdownParagraphs.add(markdownParagraphLines);

        final List<DocParagraph> docParagraphs = new ArrayList<DocParagraph>();
        for (ArrayList<MarkdownLine> markdownParagraph : markdownParagraphs)
            docParagraphs.add(markdownToClassicParagraph(ownerPackage, markdownParagraph));

        classic = new DocCommentClassic(getLocation(), docParagraphs, isSticky(), isOneLiner());

        classic.accept(currentResolver);
    }

    private static final class MarkdownLine
    {
        public MarkdownLine(AstLocation location, String text)
        {
            this.location = location;
            this.text = text;
        }

        public AstLocation getLocation()
        {
            return location;
        }

        public String getText()
        {
            return text;
        }

        private AstLocation location;
        private String text;
    }

    private static DocParagraph markdownToClassicParagraph(Package pkg, List<MarkdownLine> markdownLines)
    {
        final MarkdownLine firstMarkdownLine = markdownLines.get(0);
        final AstLocation firstLineLocation = firstMarkdownLine.getLocation();
        final DocLine firstDocLine = markdownToClassicLine(pkg, firstMarkdownLine);
        final DocMultiline docMultiline = new DocMultiline(firstLineLocation, firstDocLine);
        for (int i = 1; i < markdownLines.size(); ++i)
        {
            final DocLine docLine = markdownToClassicLine(pkg, markdownLines.get(i));
            docMultiline.addLine(docLine);
        }

        final DocParagraph docParagraph = new DocParagraph(firstLineLocation);
        final DocElement docElement = new DocElement(firstLineLocation, docMultiline);
        docParagraph.addDocElement(docElement);

        return docParagraph;
    }

    private static DocLine markdownToClassicLine(Package pkg, MarkdownLine markdownLine)
    {
        final List<DocLineElement> docLineElements = new ArrayList<DocLineElement>();
        addMarkdownToDocLineElements(pkg, docLineElements, markdownLine);

        return new DocLine(markdownLine.getLocation(), docLineElements);
    }

    private static void addMarkdownToDocLineElements(Package pkg, List<DocLineElement> docLineElements,
            MarkdownLine markdownLine)
    {
        final AstLocation lineLocation = markdownLine.getLocation();
        final Pattern linkRegex =
                Pattern.compile("([^\\[]*)\\[([^\\[\\]]*)\\]\\(([^)]+)\\)(.*)");
        final Matcher linkMatcher = linkRegex.matcher(markdownLine.getText());

        if (!linkMatcher.matches())
        {
            final DocText docText = new DocText(lineLocation, markdownLine.getText());
            docLineElements.add(new DocLineElement(lineLocation, docText));
        }
        else
        {
            final Pattern zsFileRegex =
                    Pattern.compile("^([a-zA-Z_0-9\\.\\/]+)\\.zs(?:#([a-zA-Z_0-9]+))?");
            final String linkName = linkMatcher.group(3);
            final Matcher zsFileMatcher = zsFileRegex.matcher(linkName);

            final String seeTagLink = zsFileMatcher.matches()
                    ? createSeeTagLink(pkg, zsFileMatcher.group(1), zsFileMatcher.group(2))
                    : null;

            if (seeTagLink == null)
            {
                final String linePrefix = markdownLine.getText().substring(0, linkMatcher.start(4)).trim();
                final DocText docText = new DocText(lineLocation, linePrefix);
                docLineElements.add(new DocLineElement(lineLocation, docText));
            }
            else
            {
                final String linePrefix = linkMatcher.group(1).trim();
                if (!linePrefix.isEmpty())
                {
                    final DocText docText = new DocText(lineLocation, linePrefix);
                    docLineElements.add(new DocLineElement(lineLocation, docText));
                }

                final String seeTagAlias = linkMatcher.group(2);
                final AstLocation seeTagLocation = new AstLocation(
                        lineLocation.getFileName(), lineLocation.getLine(),
                        lineLocation.getColumn() - 1 + linkMatcher.end(1)); // '[' is not in any group
                final DocTagSee docTagSee = new DocTagSee(seeTagLocation, seeTagAlias, seeTagLink);
                docLineElements.add(new DocLineElement(seeTagLocation, docTagSee));
            }

            final String lineSuffix = linkMatcher.group(4).trim();
            if (!lineSuffix.isEmpty())
            {
                final AstLocation suffixLocation = new AstLocation(
                        lineLocation.getFileName(), lineLocation.getLine(),
                        lineLocation.getColumn() - 1 + linkMatcher.start(4));
                addMarkdownToDocLineElements(pkg, docLineElements,
                        new MarkdownLine(suffixLocation, lineSuffix));
            }
        }
    }

    private static String createSeeTagLink(Package pkg, String zsFile, String zsTypeName)
    {
        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        packageNameBuilder.append(pkg.getPackageName());
        // do not include top level package name (it will be handled in classic DocTagSee)
        for (int i = 0; i < pkg.getTopLevelPackageName().getIdList().size(); ++i)
            packageNameBuilder.removeFirstId();
        packageNameBuilder.removeLastId(); // go to parent directory (from markdown point of view)
        final String[] packageIds = zsFile.split("/");
        for (String id : packageIds)
        {
            if (id.equals("."))
            {
                continue;
            }
            else if (id.equals(".."))
            {
                if (packageNameBuilder.removeLastId() == null)
                    return null;
            }
            else
            {
                packageNameBuilder.addId(id);
            }
        }

        final String seeTagLink = (zsTypeName == null) ? packageNameBuilder.get().toString() :
                ZserioTypeUtil.getFullName(packageNameBuilder.get(), zsTypeName);

        return seeTagLink;
    }

    private final String markdown;
    private final boolean isIndented;
    private DocCommentClassic classic = null;
}