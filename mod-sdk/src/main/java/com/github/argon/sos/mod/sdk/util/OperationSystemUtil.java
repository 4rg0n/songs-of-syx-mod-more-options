package com.github.argon.sos.mod.sdk.util;

import com.sun.management.OperatingSystemMXBean;
import lombok.experimental.UtilityClass;

import java.lang.management.ManagementFactory;

@UtilityClass
public class OperationSystemUtil {

    public static final OperatingSystemMXBean OS_BEAN = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     * @see OperatingSystemMXBean#getTotalMemorySize()
     */
    public static long getTotalMemorySize() {
        return OS_BEAN.getTotalMemorySize();
    }

    /**
     * @see OperatingSystemMXBean#getTotalSwapSpaceSize()
     */
    public static long getTotalSwapSpaceSize() {
        return OS_BEAN.getTotalSwapSpaceSize();
    }

    /**
     * @see OperatingSystemMXBean#getFreeSwapSpaceSize()
     */
    public static long getFreeSwapSpaceSize() {
        return OS_BEAN.getFreeSwapSpaceSize();
    }

    /**
     * @see OperatingSystemMXBean#getFreeMemorySize()
     */
    public static long getFreeMemorySize() {
        return OS_BEAN.getFreeMemorySize();
    }

    /**
     * @see OperatingSystemMXBean#getCpuLoad()
     */
    public static double getCpuLoad() {
        return OS_BEAN.getCpuLoad();
    }

    /**
     * @see OperatingSystemMXBean#getProcessCpuLoad()
     */
    public static double getProcessCpuLoad() {
        return OS_BEAN.getProcessCpuLoad();
    }

    /**
     * @see OperatingSystemMXBean#getCommittedVirtualMemorySize()
     */
    public static long getCommittedVirtualMemorySize() {
        return OS_BEAN.getCommittedVirtualMemorySize();
    }

    /**
     * @see OperatingSystemMXBean#getProcessCpuTime()
     */
    public static long getProcessCpuTime() {
        return OS_BEAN.getProcessCpuTime();
    }
}
