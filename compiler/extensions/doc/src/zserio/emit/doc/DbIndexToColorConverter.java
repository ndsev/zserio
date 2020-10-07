package zserio.emit.doc;

/**
 * The class converts database index to HTML color.
 */
class DbIndexToColorConverter
{
    /**
     * Converts database index to HTML color.
     *
     * This is tricky and hard-coded. Should be done properly by external configuration.
     *
     * @param databaseIndex Index of the database calculated from its occurrence in the zserio.
     *
     * @return The string which represents the database color.
     */
    public static String convert(int databaseIndex)
    {
        return databaseColorList[databaseIndex % databaseColorList.length];
    }

    private static final String[] databaseColorList = new String[]
    {
        "#E60003",
        "#00679C",
        "#AE080F",
        "#779A14",
        "#450B3F",
        "#537374",
        "#E74294",
        "#E8A900",
        "#0068B4",
        "#905A4A",
        "#1C95D4",
        "#AC7900",
        "#EA4F00",
        "#84715F",
        "#3E3837",
        "#6A1385",
        "#083788"
    };
}
