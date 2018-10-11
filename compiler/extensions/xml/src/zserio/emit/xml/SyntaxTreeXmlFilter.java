package zserio.emit.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import zserio.ast.TokenAST;

public class SyntaxTreeXmlFilter extends XMLFilterImpl
{
    public SyntaxTreeXmlFilter(TokenAST rootNode)
    {
        this.rootNode = rootNode;
    }

    @Override
    public void parse(InputSource inputSource) throws SAXException
    {
        ContentHandler handler = getContentHandler();
        if (handler == null)
            handler = new DefaultHandler();

        handler.startDocument();
        final char [] newLine = {'\n'};
        handler.characters(newLine, 0, newLine.length);
        fireSaxEvents(handler, rootNode);
        handler.endDocument();
    }

    private void fireSaxEvents(ContentHandler handler, TokenAST ast) throws SAXException
    {
        // print out this node and all siblings
        for (TokenAST node = ast; node != null; node = (TokenAST)node.getNextSibling())
            handleDataNode(handler, node);
    }

    private void handleDataNode(ContentHandler handler, TokenAST node) throws SAXException
    {
        final String tokenName = node.getTokenTypeName();
        handler.startElement("", "", tokenName, NO_ATTRIBUTES);

        if (node.getFirstChild() == null)
        {
            if (!node.isKeyword())
                handler.characters(node.getText().toCharArray(), 0, node.getText().length());
        }
        else
        {
            // print children
            fireSaxEvents(handler, (TokenAST)node.getFirstChild());
        }

        handler.endElement("", "", tokenName);
    }

    private static AttributesImpl NO_ATTRIBUTES = new AttributesImpl();

    private final TokenAST rootNode;
}
