package com.xxl.job.core.enums;

/**
 * Created by xuxueli on 17/5/9.
 * 调度策略，仅适用于同一台执行器服务，多次执行相同任务
 */
public enum ExecutorBlockStrategyEnum {
    // 单机串行
    SERIAL_EXECUTION("Serial execution"),
    /*CONCURRENT_EXECUTION("并行"),*/
    // 丢弃后续调度
    DISCARD_LATER("Discard Later"),
    // 覆盖之前调度
    COVER_EARLY("Cover Early");

    private String title;
    private ExecutorBlockStrategyEnum (String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
