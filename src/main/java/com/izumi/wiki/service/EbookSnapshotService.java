package com.izumi.wiki.service;

import com.izumi.wiki.mapper.EbookSnapshotMapperCust;
import com.izumi.wiki.resp.StatisticResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }

    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }

    public List<StatisticResp> get30Statistic() {
        return ebookSnapshotMapperCust.get30Statistic();
    }
}
