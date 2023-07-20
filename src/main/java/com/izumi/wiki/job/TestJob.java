// package com.izumi.wiki.job;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.text.SimpleDateFormat;
// import java.util.Date;
//
// @Component
// public class TestJob {
//
//     private static final Logger LOG = LoggerFactory.getLogger(TestJob.class);
//
//     /**
//      * 固定时间间隔 单位毫秒
//      * @throws InterruptedException
//      */
//     @Scheduled(fixedRate = 1000)
//     public void simple() throws InterruptedException {
//         SimpleDateFormat format = new SimpleDateFormat("mm:ss");
//         String dateString = format.format(new Date());
//         Thread.sleep(2000);
//         LOG.info("每隔5秒执种执行一次: {}", dateString);
//     }
//
//     /**
//      * 自定义cron表达式
//      * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
//      * @throws InterruptedException
//      */
//     @Scheduled(cron = "*/1 * * * * ?")
//     public void cron() throws InterruptedException {
//         SimpleDateFormat format = new SimpleDateFormat("mm:ss");
//         String dateString = format.format(new Date());
//         Thread.sleep(1500);
//         LOG.info("每隔1.5秒执种执行一次: {}", dateString);
//     }
// }
