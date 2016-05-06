package com.philemonworks.selfdiagnose.check.vendor;

import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpringProfileUtil {

    /**
     * Checks if the supplied profile is active in the current spring profile.
     * The following rules apply:
     * <ul>
     *     <li>The supplied profile can be comma separated (Without spaces).</li>
     *     <li>If one of the supplied profile values is active it will return true.</li>
     *     <li>If the supplied profile is empty it will return true. (Keep default behaviour)</li>
     * </ul>
     *
     * @param ctx Spring application context.
     * @param profile Value as supplied in the attribute "profile". Profiles can be comma separated.
     * @return
     */
    public static boolean profileIsActive(ApplicationContext ctx, String profile) {
        if (profile == null || profile.trim().isEmpty()) {
            return true;
        }

        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        String[] profiles = profile.split(",");

        return intersect(activeProfiles, profiles);
    }

    /**
     * Checks if the two supplied lists intersect.
     *
     * @param list1 Array of strings 1
     * @param list2 Array of strings 2
     * @return
     */
    private static boolean intersect(String[] list1, String[] list2) {

        Set<String> s1 = new HashSet<String>(Arrays.asList(list1));
        Set<String> s2 = new HashSet<String>(Arrays.asList(list2));
        s1.retainAll(s2);

        String[] result = s1.toArray(new String[s1.size()]);

        return result.length > 0;
    }

}
