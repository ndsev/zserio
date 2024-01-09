package zserio.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;

/**
 * Class which keeps configuration of warnings subsystem.
 */
public final class WarningsConfig
{
    /**
     * Default constructor which construct default configuration for warnings subsystem.
     */
    public WarningsConfig()
    {
        addWarningGroup(ALL, "Controls all warnings at once.", 0);
        addWarning(CHOICE_UNHANDLED_ENUM_ITEM, true,
                "Warn when a choice with enumeration selector does not handle some of the enumeration items.");
        addWarning(DEFAULT_INSTANTIATION, false,
                "Warn about template instantiations which are not instantiated using instantiate keyword.");
        addWarning(DOC_COMMENT_FORMAT, true, "Warn when a documentation comment has invalid format.");
        addWarning(DOC_COMMENT_MISSING, false,
                "Warn when a documentable schema element has no documentation comment assigned.");
        addWarning(DOC_COMMENT_LINK, true,
                "Warn when a documentation link contains invalid symbol reference "
                        + "(e.g. see tag or markdown link).");
        addWarning(DOC_COMMENT_UNUSED, true,
                "Warn when a documentation comment is not used - i.e. cannot be assigned to any documentable "
                        + "element.");
        addWarning(ENCODING, true,
                "Warn when a source file is not in UTF-8 encoding or "
                        + "when it contains non-printable characters.");
        addWarning(IMPORT, true, "Warn when some imports are duplicated or overrides other imports.");
        addWarning(OPTIONAL_FIELD_REFERENCE, true,
                "Warn when an expression contains a reference to an optional field, while the owner of the "
                        + "expression has no or inconsistent optional condition.");
        addWarning(TIMESTAMP, true,
                "Warn when timestamp of a source or a resource (on a class path) cannot be retrieved.");
        addWarning(SQL_PRIMARY_KEY, true,
                "Warn when a problem with primary key is detected in a SQL table. "
                        + "This can happen in various cases - no primary key, duplicated primary key, etc.");
        addWarning(UNPACKABLE_ARRAY, true,
                "Warn when a packed array is used on arrays of unpackable elements. Can be fired either for "
                        + "arrays of unpackable simple types (e.g. string) or for arrays of compounds which "
                        +
                        "contain only unpackable types. The packed keyword has no effect when this warnings is fired.");
        addWarning(UNPACKABLE_UNION, true,
                "Warn when a packed array of unions doesn't contain any packable fields and thus only selector "
                        + "is packed, which may unintentionally break alignment of the union fields.");
        addWarning(UNUSED, false, "Warn about defined, but unused types.");
    }

    /**
     * Constructor which takes list of explicitly enabled/disabled warnings.
     *
     * @param enabledWarnings Explicitly enabled warnings.
     * @param disabledWarnings Explicitly disabled warnings.
     *
     * @throws ParseException In case of invalid warnings options.
     */
    public WarningsConfig(List<String> enabledWarnings, List<String> disabledWarnings) throws ParseException
    {
        this();

        validate(enabledWarnings, disabledWarnings);
        setWarnings(enabledWarnings, true);
        setWarnings(disabledWarnings, false);
    }

    /**
     * Gets whether the given warning is enabled.
     *
     * @param warningOption Warning option to check.
     *
     * @return True when the specified warning is enabled, false otherwise.
     */
    public boolean isEnabled(String warningOption)
    {
        final WarningConfig config = warningsConfigMap.get(warningOption);
        return config != null ? config.isEnabled() : false;
    }

    /**
     * Gets definition of all warnings.
     *
     * @return Map of all defined warnings options.
     */
    public Map<String, WarningDefinition> getWarningsDefinition()
    {
        return Collections.unmodifiableMap(warningsDefinitionMap);
    }

    private void validate(List<String> enabledWarnings, List<String> disabledWarnings) throws ParseException
    {
        for (String option : enabledWarnings)
            checkIfExists(option);

        for (String option : disabledWarnings)
            checkIfExists(option);

        final List<String> conflictingValues = new ArrayList<String>(enabledWarnings);
        conflictingValues.retainAll(disabledWarnings);
        if (!conflictingValues.isEmpty())
        {
            throw new ParseException("The specified warnings options are in conflict: " +
                    String.join(String.valueOf(WARNINGS_OPTIONS_SEPARATOR), conflictingValues) + "!");
        }
    }

    private void setWarnings(List<String> warningsOptions, boolean value)
    {
        for (String option : warningsOptions)
        {
            if (ALL.equals(option))
                setAll(value);
            else
                setWarning(option, value, warningsDefinitionMap.get(option).getPriority());
        }
    }

    private void checkIfExists(String option) throws ParseException
    {
        if (warningsDefinitionMap.get(option) == null)
        {
            throw new ParseException("The specified warnings option is not known: '" + option + "'!");
        }
    }

    private void setAll(boolean value)
    {
        for (String option : warningsConfigMap.keySet())
        {
            setWarning(option, value, warningsDefinitionMap.get(ALL).getPriority());
        }
    }

    private void setWarning(String warningOption, boolean enabled, int priority)
    {
        final WarningConfig prevConfig = warningsConfigMap.get(warningOption);
        if (priority > prevConfig.getPriority())
            warningsConfigMap.put(warningOption, new WarningConfig(enabled, priority));
    }

    private void addWarningGroup(String warningOption, String desription, int priority)
    {
        warningsDefinitionMap.put(warningOption, new WarningDefinition(desription, priority));
    }

    private void addWarning(String warningOption, boolean enabled, String description)
    {
        warningsDefinitionMap.put(warningOption, new WarningDefinition(description, Integer.MAX_VALUE));
        warningsConfigMap.put(warningOption, new WarningConfig(enabled, Integer.MIN_VALUE));
    }

    /**
     * Definition of a single warning or a warning group.
     */
    public static final class WarningDefinition
    {
        /**
         * Constructor.
         *
         * @param description Description of the warning option.
         * @param priority Priority of the warning option.
         */
        public WarningDefinition(String description, int priority)
        {
            this.description = description;
            this.priority = priority;
        }

        /**
         * Gets description of the warnings option.
         *
         * @return String description.
         */
        public String getDescription()
        {
            return description;
        }

        /**
         * Gets priority of the warnings option.
         *
         * Single warning options have highest priority (Integer.MAX_VALUE) and are always applied,
         * while warnings options groups are applied only when no option with higher priority was applied.
         * Option ALL_OPTION has the lowest possible priority (Integer.MIN_VALUE).
         *
         * @return The option priority.
         */
        public int getPriority()
        {
            return priority;
        }

        private final String description;
        private final int priority;
    }

    private static final class WarningConfig
    {
        public WarningConfig(boolean enabled, int priority)
        {
            this.enabled = enabled;
            this.priority = priority;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public int getPriority()
        {
            return priority;
        }

        private final boolean enabled;
        private final int priority;
    }

    /** Option name for choice unhandled enumeration item warnings. */
    public static final String CHOICE_UNHANDLED_ENUM_ITEM = "choice-unhandled-enum-item";
    /** Option name for default template instantiation warnings. */
    public static final String DEFAULT_INSTANTIATION = "default-instantiation";
    /** Option name for documentation comment format warnings. */
    public static final String DOC_COMMENT_FORMAT = "doc-comment-format";
    /** Option name for missing documentation comment warnings. */
    public static final String DOC_COMMENT_MISSING = "doc-comment-missing";
    /** Option name for documentation comment warnings related to links (e.g. see tag or markdown link). */
    public static final String DOC_COMMENT_LINK = "doc-comment-link";
    /** Option name for unused documentation comments warnings. */
    public static final String DOC_COMMENT_UNUSED = "doc-comment-unused";
    /** Option name for encoding warnings. */
    public static final String ENCODING = "encoding";
    /** Option name for import warnings. */
    public static final String IMPORT = "import";
    /** Option name for optional field references warnings. */
    public static final String OPTIONAL_FIELD_REFERENCE = "optional-field-reference";
    /** Option name for timestamp warnings. */
    public static final String TIMESTAMP = "timestamp";
    /** Option name for SQL primary key warnings. */
    public static final String SQL_PRIMARY_KEY = "sql-primary-key";
    /** Option name for unpackable array warnings. */
    public static final String UNPACKABLE_ARRAY = "unpackable-array";
    /** Option name for arrays of unpackable unions warnings. */
    public static final String UNPACKABLE_UNION = "unpackable-union";
    /** Option name for unused type warnings. */
    public static final String UNUSED = "unused";

    // special option group covering all warnings options
    static final String ALL = "all";

    static final Character WARNINGS_OPTIONS_SEPARATOR = ',';
    // description map contains all warning options including group options (e.g. 'all')
    private final Map<String, WarningDefinition> warningsDefinitionMap =
            new LinkedHashMap<String, WarningDefinition>();
    // config map contains only particular warning options - i.e. groups are not included
    private final Map<String, WarningConfig> warningsConfigMap = new HashMap<String, WarningConfig>();
}
