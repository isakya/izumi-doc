package com.izumi.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.izumi.wiki.domain.Ebook;
import com.izumi.wiki.domain.EbookExample;
import com.izumi.wiki.mapper.EbookMapper;
import com.izumi.wiki.req.EbookReq;
import com.izumi.wiki.resp.EbookResp;
import com.izumi.wiki.resp.PageResp;
import com.izumi.wiki.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();

        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize()); // 只对第一次遇到的sql查询有效
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());


        // 列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);
        PageResp<EbookResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
}
