package zserio.extension.java;

import java.util.List;
import java.util.ListIterator;

import zserio.ast.DocComment;
import zserio.ast.DocumentableAstNode;
import zserio.extension.common.ZserioExtensionException;

/**
 * Create DocCommentsTemplateData needed for documentation comments.
 */
class DocCommentsDataCreator
{
    public static DocCommentsTemplateData createData(TemplateDataContext context,
            DocumentableAstNode documentableNode) throws ZserioExtensionException
    {
        return createData(context, documentableNode.getDocComments());
    }

    public static DocCommentsTemplateData createData(TemplateDataContext context,
            List<DocComment> docComments) throws ZserioExtensionException
    {
        final int numComments = docComments.size();
        int firstStickyCommentIndex = numComments;
        final ListIterator<DocComment> iterator = docComments.listIterator(numComments);
        while (iterator.hasPrevious() && iterator.previous().isSticky())
            --firstStickyCommentIndex;

        final List<DocComment> stickyDocComments = docComments.subList(firstStickyCommentIndex, numComments);

        return (stickyDocComments.isEmpty()) ? null : new DocCommentsTemplateData(context, stickyDocComments);
    }
}
