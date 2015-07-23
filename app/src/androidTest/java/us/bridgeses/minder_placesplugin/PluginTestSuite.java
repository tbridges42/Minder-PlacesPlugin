package us.bridgeses.minder_placesplugin;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

/**
 * A suite for testing the entire application
 */
public class PluginTestSuite {
    public static Test suite () {
        return new TestSuiteBuilder(PluginTestSuite.class)
                .includeAllPackagesUnderHere()
                .build();

    }
}
