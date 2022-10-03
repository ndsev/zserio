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
public class WarningsConfig
{
    /**
     * Default constructor which construct default configuration for warnings subsystem.
     */
    public WarningsConfig()
    {
        addWarningGroup(ALL_OPTION, "Controls all warnings at once.", 0);
        addWarning(UNUSED_OPTION, false, "Warn about defined, but unused types.");
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
            if (ALL_OPTION.equals(option))
                setAll(value);
            else
                setWarning(option, value, warningsDefinitionMap.get(option).getPriority());
        }
    }

    private void checkIfExists(String option) throws ParseException
    {
        if (warningsDefinitionMap.get(option) == null)
        {
            throw new ParseException("The specified warnings option in not known: '" + option + "'!");
        }
    }

    private void setAll(boolean value)
    {
        for (String option : warningsConfigMap.keySet())
        {
            setWarning(option, value, warningsDefinitionMap.get(ALL_OPTION).getPriority());
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
    public static class WarningDefinition
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
         * Single warning options have highest priority and are always applied, while warnings options groups
         * are applied only when no option with higher priority was applied.
         * Option ALL_OPTION has the lowest possible priority.
         *
         * @return
         */
        public int getPriority()
        {
            return priority;
        }

        private final String description;
        private final int priority;
    }

    private static class WarningConfig
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

    /** Option name for unused type warnings. */
    public static final String UNUSED_OPTION = "unused";

    // special option group covering all warnings options
    private static final String ALL_OPTION = "all";

    static final Character WARNINGS_OPTIONS_SEPARATOR = ',';
    // description map contains all warning options including group options (e.g. 'all')
    private final Map<String, WarningDefinition> warningsDefinitionMap =
            new LinkedHashMap<String, WarningDefinition>();
    // config map contains only particular warning options - i.e. groups are not included
    private final Map<String, WarningConfig> warningsConfigMap = new HashMap<String, WarningConfig>();
}
