package com.example.demo.similar;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lijunjun
 * @title 描述
 * @date 2023/8/15 17:24
 */
public class Similarity {

    //public static final String content1 = "今天小小和爸爸一起去摘草莓，小小说今天的草莓特别的酸，而且特别的小，关键价格还贵";
    public static final String content1 = "生物质制氢技术";

    //public static final String content2 = "今天小小和妈妈一起去草原里采草莓，今天的草莓味道特别好，而且价格还挺实惠的";
    public static final String content2 = "生物质废弃物高浓度厌氧消化制生物天然气技术与工程";

    public static void main(String[] args) throws InterruptedException {

        // 计算处理时间差
        long start = System.currentTimeMillis();
        List<String> data = DemoData.getData();
        List<FilterEntity> filterEntities = new CopyOnWriteArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(data.size());
        new Thread(() -> {
            for (String s : data) {
                count.getAndIncrement();
                countDownLatch.countDown();
                double similarity = CosineSimilarity.getSimilarity(content1, s);
                if (similarity > 0.5) {
                    filterEntities.add(new FilterEntity(s, similarity));
                }
            }
        }).start();/*

        new Thread(() -> {
            for (String s : data) {
                count.getAndIncrement();
                countDownLatch.countDown();
                double similarity = CosineSimilarity.getSimilarity(content1, s);
                if (similarity > 0.5) {
                    filterEntities.add(new FilterEntity(s, similarity));
                }
            }
            filterEntities.sort((o1, o2) -> {
                if (o1.getScore() > o2.getScore()) {
                    return -1;
                } else if (o1.getScore() < o2.getScore()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }).start();
        */
        countDownLatch.await();

        filterEntities.sort((o1, o2) -> {
            if (o1.getScore() > o2.getScore()) {
                return -1;
            } else if (o1.getScore() < o2.getScore()) {
                return 1;
            } else {
                return 0;
            }
        });
        filterEntities.forEach(e -> System.out.println("匹配内容：" + e.getName() + " 匹配相似度：" + e.getScore()));
        System.out.println("总数：" + filterEntities.size());
        System.out.println(count.get());
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
        //double score = CosineSimilarity.getSimilarity(content1, content2);
        //
        //System.out.println("相似度：" + score);
        //
        //score = CosineSimilarity.getSimilarity(content1, content1);
        //
        //System.out.println("相似度：" + score);
    }

    @Data
    @AllArgsConstructor
    public static class FilterEntity {
        private String name;
        private double score;
    }
}
