package zserio.extension.cpp;

public class BitSizeTemplateData
{
    public BitSizeTemplateData(String value, boolean isDynamicBitField)
    {
        this.value = value;
        this.isDynamicBitField = isDynamicBitField;
    }

    public String getValue()
    {
        return value;
    }

    public boolean getIsDynamicBitField()
    {
        return isDynamicBitField;
    }

    private final String value;
    private final boolean isDynamicBitField;
}
