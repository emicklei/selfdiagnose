package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.report.ReportPropertiesFile;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;

public class ReportPropertiesFileTest extends TestCase {

    public void testFilteringAndFormatting() {
        final String organizationName = "acme";
        final String projectName = "coolproject";
        final String tagUrl = String.format("<a href=\"https://github.com/%s/%s/commits/{value}\">{value}</a>", organizationName, projectName);
        final String commitUrl = String.format("<a href=\"https://github.com/%s/%s/commit/{value}\">{value}</a>", organizationName, projectName);
        ReportPropertiesFile gitInfo = new ReportPropertiesFile();
        gitInfo.setName("example/git.properties");
        gitInfo.setKeysToReport(new HashSet<String>(Arrays.asList("git.closest.tag.name", "git.commit.id", "git.branch")));
        gitInfo.addFormatForKey("git.closest.tag.name", "tag", tagUrl);
        gitInfo.addFormatForKey("git.commit.id", "commit", commitUrl);
        gitInfo.addFormatForKey("git.branch", "branch", null);

        final String expected =
                "branch: dragons-be-here, " +
                "tag: <a href=\"https://github.com/acme/coolproject/commits/v1.2.3\">v1.2.3</a>, " +
                "commit: <a href=\"https://github.com/acme/coolproject/commit/781040cdcbc1fbd8X3e9d5X54f92Xbada508326b\">781040cdcbc1fbd8X3e9d5X54f92Xbada508326b</a>";

        final String actual = gitInfo.run().getMessage();
        assertEquals(expected, actual);
    }
}
