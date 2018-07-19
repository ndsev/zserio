package zserio.ast;

@SuppressWarnings("serial")
public class ZserioException extends RuntimeException
{
    public ZserioException()
    {

    }


    public ZserioException(String text)
    {
        super(text);
    }


    public ZserioException(Throwable exc)
    {
        super(exc);
    }
}
