package zserio.emit.doc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.commonmark.node.Node;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlRenderer;

import zserio.ast.AstLocation;
import zserio.tools.ZserioToolPrinter;

import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;

class MarkdownToHtmlConverter
{
    static String markdownToHtml(ResourceManager resourceManager, AstLocation location, String markdown)
    {
        final List<Extension> extensions = Arrays.asList(AutolinkExtension.create(),
                HeadingAnchorExtension.create(), TablesExtension.create());

        final Parser parser = Parser.builder().extensions(extensions).build();
        final Node document = parser.parse(markdown);

        final HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .nodeRendererFactory(
                        new HtmlNodeRendererFactory()
                        {
                            @Override
                            public NodeRenderer create(HtmlNodeRendererContext context)
                            {
                                return new ResourcesRenderer(context, resourceManager, location);
                            }
                        }
                )
                .build();

        final String html = renderer.render(document);

        return html.trim(); // strip white-spaces around generated HTML added by renderer
    }

    private static class ResourcesRenderer extends CoreHtmlNodeRenderer
    {
        ResourcesRenderer(HtmlNodeRendererContext context,
                ResourceManager resourceManager, AstLocation location)
        {
            super(context);
            this.resourceManager = resourceManager;
            this.location = location;
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes()
        {
            return new HashSet<Class<? extends Node>>(Arrays.asList(Link.class, Image.class));
        }

        @Override
        public void visit(Link link)
        {
            try
            {
                final String mappedResource = resourceManager.addResource(link.getDestination());
                link.setDestination(mappedResource);
            }
            catch (Exception e)
            {
                ZserioToolPrinter.printWarning(location, e.getMessage());
            }

            super.visit(link);
        }

        @Override
        public void visit(Image image)
        {
            try
            {
                final String mappedResource = resourceManager.addResource(image.getDestination());
                image.setDestination(mappedResource);
            }
            catch (Exception e)
            {
                ZserioToolPrinter.printWarning(location, e.getMessage());
            }

            super.visit(image);
        }

        private final ResourceManager resourceManager;
        private final AstLocation location;
    }
}
