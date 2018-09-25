package zserio.emit.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import zserio.ast.TokenAST;
import zserio.emit.common.FileUtil;
import zserio.emit.common.ZserioEmitException;

public class SyntaxTreeEmitter
{
    public SyntaxTreeEmitter(String outputDir)
    {
        this.outputDir = outputDir;
    }

    public void emit(TokenAST rootNode)
    {
        final File outputFile = new File(outputDir, "syntax_tree.xml");
        FileUtil.createOutputDirectory(outputFile);

        final SyntaxTreeXmlFilter filter = new SyntaxTreeXmlFilter(rootNode);
        FileOutputStream os = null;
        try
        {
            os = new FileOutputStream(outputFile);

            TransformerFactory tf = TransformerFactory.newInstance();
            try
            {
                tf.setAttribute("indent-number", Integer.valueOf(2));
            }
            catch (IllegalArgumentException e)
            {
                // unsupported attribute - silently ignore
            }

            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            final Source source = new SAXSource(filter, new InputSource());
            final Result result = new StreamResult(new OutputStreamWriter(os, "UTF-8"));
            transformer.transform(source, result);
        }
        catch (TransformerConfigurationException exc)
        {
            throw new ZserioEmitException(exc);
        }
        catch (TransformerException exc)
        {
            throw new ZserioEmitException(exc);
        }
        catch (UnsupportedEncodingException exc)
        {
            try
            {
                os.close();
            }
            catch (IOException e)
            {
            }
            throw new ZserioEmitException(exc);
        }
        catch (FileNotFoundException exc)
        {
            throw new ZserioEmitException(exc);
        }
    }

    private String outputDir;
}
