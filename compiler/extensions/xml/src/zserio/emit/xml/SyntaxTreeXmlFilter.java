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

    /*
     * 'main' for XML output
     * @see org.xml.sax.helpers.XMLFilterImpl#parse(org.xml.sax.InputSource)
     */
    @Override
    public void parse(InputSource inputSource) throws SAXException
    {
        ContentHandler handler = getContentHandler();
        if (handler == null)
            handler = new DefaultHandler();

        handler.startDocument();
        final char []  newLine = {'\n'};
        handler.characters(newLine, 0, newLine.length);
        fireSaxEvents(handler, rootNode);
        handler.endDocument();
    }

    private void fireSaxEvents(ContentHandler handler, TokenAST ast) throws SAXException
    {
        // print out this node and all siblings
        for (TokenAST node = ast; node != null; node = (TokenAST)node.getNextSibling())
        {
            handleDataNode(handler, node);
        }
    }

    private void handleDataNode(ContentHandler handler, TokenAST node) throws SAXException
    {
        final String tokenName = node.getTokenTypeName();
        if (node.getFirstChild() == null)
        {
            if (node.isKeyword())
            {
                if (!tokenName.equals("CASE")) //Filter on empty token statements
                {
                    startElement(handler, tokenName);
                    endElement(handler, tokenName);
                }
            }
            else
            {
                startElement(handler, tokenName);
                text(handler, node.getText());
                endElement(handler, tokenName);
            }
        }
        else
        {
            startElement(handler, tokenName);
            // print children
            fireSaxEvents(handler, (TokenAST)node.getFirstChild());
            endElement(handler, tokenName);
        }
    }

    private void startElement(ContentHandler handler, String tag) throws SAXException
    {
        handler.startElement("", "", tag, NO_ATTRIBUTES);
    }

    private void endElement(ContentHandler handler, String tag) throws SAXException
    {
        handler.endElement("", "", tag);
    }

    private void text(ContentHandler handler, String s) throws SAXException
    {
        handler.characters(s.toCharArray(), 0, s.length());
    }

    private TokenAST rootNode;

    private static AttributesImpl NO_ATTRIBUTES = new AttributesImpl();
}
