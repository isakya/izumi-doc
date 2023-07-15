package com.izumi.wiki.service;

import com.izumi.wiki.domain.Ebook;
import com.izumi.wiki.domain.EbookExample;
import com.izumi.wiki.mapper.EbookMapper;
import com.izumi.wiki.req.EbookReq;
import com.izumi.wiki.resp.EbookResp;
import com.izumi.wiki.util.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        // 列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);

        return list;
    }
}
