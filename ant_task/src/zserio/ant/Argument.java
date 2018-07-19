package zserio.ant;

/**
 * POJO Argument used like
 *
 * <code>
 * <zserio>
 *    <arg name="foo" value="1"/>
 *    <arg name="bar"/>
 * </zserio>
 * </code>
 *
 * Where foo is an argument with a value (here 1), and bar is a value-less
 * argument.
 *
 * Arguments will be given to Zserio as -name value, i.e.
 * args = { "-foo", "1", "-bar" }
 */
public class Argument
{
    /**
     *
     * @return argument value, might be null
     */
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * @return true if a value is set
     */
    public boolean hasValue()
    {
        return getValue() != null;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String name;

    /**
     * argument value, might be null
     */
    private String value;
}
