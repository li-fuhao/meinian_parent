package com.atguigu;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.util.Date;

public class HelloQuartz implements Job {
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDetail detail = jobExecutionContext.getJobDetail();
        String name = detail.getJobDataMap().getString("name");
        System.out.println("my job name is  " + name + " at " + new Date());
    }
}
