package java_version8;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;

public class JavaVersion8Test
{
    @Test
    public void checkJava8GeneratedAnnotation() throws IOException
    {
        final String generatedFileName = "../gen/java_version8/MainStruct.java";
        final InputStream inputStream = new FileInputStream(generatedFileName);
        final Reader fileReader = new InputStreamReader(inputStream, "UTF-8");
        final BufferedReader reader = new BufferedReader(fileReader);
        try
        {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null)
            {
               // process the line.
               if (line.startsWith("import javax.annotation."))
               {
                   assertEquals("import javax.annotation.Generated;", line);
                   found = true;
                   break;
               }
            }

            if (!found)
                fail("Can't find 'import javax.annotation.Generated' in generated Java source!");
        }
        finally
        {
            reader.close();
        }
    }
}
