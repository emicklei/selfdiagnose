package com.philemonworks.selfdiagnose.output;

import java.lang.management.ManagementFactory;
import java.util.Date;

public class Startup {
    public static final Date TIMESTAMP = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
}
