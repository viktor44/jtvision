package org.viktor44.jtvision.util;

public class SystemUtils {

    /**
     * A constant for the System Property {@code os.name}. Operating system name.
     */
    public static final String OS_NAME = System.getProperty("os.name");

    /**
     * A constant for the System Property {@code os.arch}. Operating system architecture.
     */
    public static final String OS_ARCH = System.getProperty("os.arch");

    //vk: property order is important here
    
    /**
     * The constant {@code true} if this is Mac.
     */
    public static final boolean IS_OS_MAC = isOsNameMatch(OS_NAME, "Mac");

    /**
     * The constant {@code true} if this is Windows.
     */
    public static final boolean IS_OS_WINDOWS = isOsNameMatch(OS_NAME, "Windows");

    /**
     * Tests whether the operating system matches with a case-insensitive comparison.
     * <p>
     * This method is package private instead of private to support unit test invocation.
     * </p>
     *
     * @param osName       the actual OS name.
     * @param osNamePrefix the prefix for the expected OS name.
     * @return true for a case-insensitive match, or false if not.
     */
    private static boolean isOsNameMatch(final String osName, final String osNamePrefix) {
        if (osName == null || osNamePrefix == null) {
            return false;
        }
        return osName.toLowerCase().startsWith(osNamePrefix.toLowerCase());
    }
}
