package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import zserio.emit.common.DefaultEmitter;
import zserio.emit.common.ZserioEmitException;

abstract public class DefaultDocEmitter extends DefaultEmitter
{
    protected void openOutputFile(File directory, String fileName) throws ZserioEmitException
    {
        openOutputFile(new File(directory, fileName));
    }

    protected void openOutputFile(File file) throws ZserioEmitException
    {
        File parentDir = file.getParentFile();
        if (!parentDir.exists())
        {
            if (!parentDir.mkdirs())
            {
                throw new ZserioEmitException( "Can't create directory: " + parentDir.toString() );
            }
        }

        try
        {
            writer = new PrintWriter(file, "UTF-8");
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
    }

    protected PrintWriter writer = null;
}
