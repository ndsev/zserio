package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import zserio.ast.ZserioException;
import zserio.emit.common.DefaultEmitter;

abstract public class DefaultDocEmitter extends DefaultEmitter
{
    protected void openOutputFile(File directory, String fileName) throws ZserioException
    {
        openOutputFile(new File(directory, fileName));
    }

    protected void openOutputFile(File file) throws ZserioException
    {
        File parentDir = file.getParentFile();
        if (!parentDir.exists())
        {
            if (!parentDir.mkdirs())
            {
                throw new ZserioException( "Can't create directory: " + parentDir.toString() );
            }
        }

        try
        {
            writer = new PrintWriter(file, "UTF-8");
        }
        catch (IOException exc)
        {
            throw new ZserioException( exc );
        }
    }

    protected PrintWriter writer = null;
}
