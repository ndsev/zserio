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
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;

class MarkdownToHtmlConverter
{
    public static String markdownToHtml(String markdown)
    {
        List<Extension> extensions = Arrays.asList(
                AutolinkExtension.create(),
                HeadingAnchorExtension.create());

        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        Node document = parser.parse(markdown);

        Set<String> linkedFiles = new HashSet<String>();

        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .nodeRendererFactory(
                        new HtmlNodeRendererFactory()
                        {
                            public NodeRenderer create(HtmlNodeRendererContext context)
                            {
                                return new ResourcesRenderer(context, linkedFiles);
                            }
                        }
                )
                .build();
        final String html = renderer.render(document);
        return html;
    }

    private static class ResourcesRenderer extends CoreHtmlNodeRenderer
    {
        ResourcesRenderer(HtmlNodeRendererContext context, Set<String> linkedFiles)
        {
            super(context);
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return new HashSet<Class<? extends Node>>(Arrays.asList(
                    Link.class,
                    Image.class));
        }

        @Override
        public void visit(Link link) {
            final String mappedResource = ResourceManager.getInstance().addResource(link.getDestination());
            link.setDestination(mappedResource);

            super.visit(link);
        }

        @Override
        public void visit(Image image) {
            final String mappedResource = ResourceManager.getInstance().addResource(image.getDestination());
            image.setDestination(mappedResource);

            super.visit(image);
        }
    }
}
