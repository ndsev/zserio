package zserio.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExtensionVersionMatcher
{
    static boolean matchExtensionVersion(String zserioVersionString, String extensionVersionString)
    {
        final List<String> zserioVersionParts = splitVersionString(zserioVersionString);
        final List<String> extensionVersionParts = splitVersionString(extensionVersionString);

        // invalid version format, try to match exactly
        if (zserioVersionParts.isEmpty() || extensionVersionParts.isEmpty())
            return matchExactly(zserioVersionString, extensionVersionString);

        // pre-released extensions must match exactly!
        if (extensionVersionParts.get(3) != null) // if is a pre-release
            return matchExactly(zserioVersionString, extensionVersionString);

        final int zserioRevision = Integer.parseInt(zserioVersionParts.get(2));
        final int extensionRevision = Integer.parseInt(extensionVersionParts.get(2));

        return zserioVersionParts.get(0).equals(extensionVersionParts.get(0)) && // major version
                zserioVersionParts.get(1).equals(extensionVersionParts.get(1)) && // minor version
                (zserioVersionParts.get(3) != null // if is zserio pre-release
                        ? zserioRevision > extensionRevision
                        : zserioRevision >= extensionRevision);
    }

    static List<String> splitVersionString(String version)
    {
        final List<String> parts = new ArrayList<String>();
        if (version == null)
            return parts;

        final Pattern pattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(-.*)?$");
        final Matcher matcher = pattern.matcher(version);
        if (matcher.find())
        {
            parts.add(matcher.group(1)); // major version
            parts.add(matcher.group(2)); // minor version
            parts.add(matcher.group(3)); // revision
            parts.add(matcher.group(4)); // extra suffix (e.g. pre-release)
        }

        return parts;
    }

    private static boolean matchExactly(String zserioVersionString, String extensionVersionString)
    {
        if (zserioVersionString == null)
            return false;

        return zserioVersionString.equals(extensionVersionString);
    }
};
