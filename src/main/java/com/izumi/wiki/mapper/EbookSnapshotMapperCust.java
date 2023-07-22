package com.izumi.wiki.mapper;

import com.izumi.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {

    public void genSnapshot();

    public List<StatisticResp> getStatistic();

    public List<StatisticResp> get30Statistic();
}
