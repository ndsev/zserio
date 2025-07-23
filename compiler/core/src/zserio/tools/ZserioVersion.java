package zserio.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which represents the zserio version number and provides parsing and comparison.
 */
public final class ZserioVersion implements Comparable<ZserioVersion>
{
    /**
     * Constructor overload for convenience.
     *
     * @param major Major version number.
     * @param minor Minor version number.
     * @param revision Revision number.
     */
    public ZserioVersion(int major, int minor, int revision)
    {
        this(major, minor, revision, null);
    }

    /**
     * Constructor.
     *
     * @param major Major version number.
     * @param minor Minor version number.
     * @param revision Revision number.
     * @param preRelease Optional pre-release number.
     */
    public ZserioVersion(int major, int minor, int revision, Integer preRelease)
    {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.preRelease = preRelease;
    }

    @Override
    public String toString()
    {
        return major + "." + minor + "." + revision + (preRelease != null ? "-pre" + preRelease : "");
    }

    @Override
    public int compareTo(ZserioVersion other)
    {
        if (major != other.major)
            return major - other.major;
        if (minor != other.minor)
            return minor - other.minor;
        if (revision != other.revision)
            return revision - other.revision;

        return comparePreRelease(other.preRelease);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ZserioVersion))
            return false;

        final ZserioVersion otherVersion = (ZserioVersion)other;
        return compareTo(otherVersion) == 0;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, major);
        hash = HashUtil.hash(hash, minor);
        hash = HashUtil.hash(hash, revision);
        hash = HashUtil.hash(hash, preRelease);

        return hash;
    }

    /**
     * Gets major version number.
     *
     * @return Major version number.
     */
    public int getMajor()
    {
        return major;
    }

    /**
     * Gets minor version number.
     *
     * @return Minor version number.
     */
    public int getMinor()
    {
        return minor;
    }

    /**
     * Gets reversion number.
     *
     * @return Reversion number.
     */
    public int getRevision()
    {
        return revision;
    }

    /**
     * Gets pre-release number. Can be null.
     *
     * @return Pre-release number.
     */
    public Integer getPreRelease()
    {
        return preRelease;
    }

    /**
     * Parses versions string according to zserio versioning rules.
     *
     * @param versionString Version string to parse.
     *
     * @return Parsed Version object.
     * @throws RuntimeException On a parsing error.
     */
    public static ZserioVersion parseVersion(String versionString)
    {
        final Pattern pattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(?:-pre(.*))?$");
        final Matcher matcher = pattern.matcher(versionString);
        if (matcher.find())
        {
            final Integer preRelease = matcher.group(4) == null ? null : Integer.parseInt(matcher.group(4));
            return new ZserioVersion(Integer.parseInt(matcher.group(1)), // major
                    Integer.parseInt(matcher.group(2)), // minor
                    Integer.parseInt(matcher.group(3)), // revision
                    preRelease); // pre-release
        }
        else
        {
            throw new IllegalArgumentException(
                    "Failed to parse version string: '" + versionString + "' as a version!");
        }
    }

    private int comparePreRelease(Integer otherPreRelease)
    {
        // note that pre-release is always "younger" than final release
        if (preRelease == null)
            return otherPreRelease == null ? 0 : 1;
        else
            return otherPreRelease == null ? -1 : preRelease.compareTo(otherPreRelease);
    }

    /** Zserio core version string. */
    public static final String VERSION_STRING = "2.17.0";

    private final int major;
    private final int minor;
    private final int revision;
    private final Integer preRelease;
};
