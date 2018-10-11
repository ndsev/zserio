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

import zserio.ast.Root;
import zserio.emit.common.FileUtil;
import zserio.emit.common.ZserioEmitException;

public class SyntaxTreeEmitter
{
    public SyntaxTreeEmitter(String outputDir)
    {
        this.outputDir = outputDir;
    }

    public void emit(Root rootNode) throws ZserioEmitException
    {
        final File outputFile = new File(outputDir, "syntax_tree.xml");
        FileUtil.createOutputDirectory(outputFile);

        final SyntaxTreeXmlFilter filter = new SyntaxTreeXmlFilter(rootNode);
        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(outputFile);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try
            {
                transformerFactory.setAttribute("indent-number", Integer.valueOf(2));
            }
            catch (IllegalArgumentException exception)
            {
                // unsupported attribute - silently ignore
            }

            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            final Source source = new SAXSource(filter, new InputSource());
            final Result result = new StreamResult(new OutputStreamWriter(outputStream, "UTF-8"));
            transformer.transform(source, result);
        }
        catch (TransformerConfigurationException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TransformerException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (UnsupportedEncodingException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (FileNotFoundException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (IOException exception)
                {
                }
            }
        }
    }

    private final String outputDir;
}
