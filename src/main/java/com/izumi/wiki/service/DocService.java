package com.izumi.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.izumi.wiki.domain.Content;
import com.izumi.wiki.domain.Doc;
import com.izumi.wiki.domain.DocExample;
import com.izumi.wiki.exception.BusinessException;
import com.izumi.wiki.exception.BusinessExceptionCode;
import com.izumi.wiki.mapper.ContentMapper;
import com.izumi.wiki.mapper.DocMapper;
import com.izumi.wiki.mapper.DocMapperCust;
import com.izumi.wiki.req.DocQueryReq;
import com.izumi.wiki.req.DocSaveReq;
import com.izumi.wiki.resp.DocQueryResp;
import com.izumi.wiki.resp.PageResp;
import com.izumi.wiki.util.CopyUtil;
import com.izumi.wiki.util.RedisUtil;
import com.izumi.wiki.util.RequestContext;
import com.izumi.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WsService wsService;

    // @Resource
    // private RocketMQTemplate rocketMQTemplate;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        // 如果 ebookId = null 那就搜不到文档
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc"); // 排序
        List<Doc> docList = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();

        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }

        PageHelper.startPage(req.getPage(), req.getSize()); // 只对第一次遇到的sql查询有效
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());


        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     *
     * @param req
     */
    @Transactional
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content); // BLOB是带大字段的用法
            if(count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    /**
     * 删除
     *
     * @param ids
     */
    public void delete(List<Long> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    /**
     * 根据id查找文档
     * @param id
     * @return
     */
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 更新文档阅读数
        docMapperCust.increaseViewCount(id);
        if (!ObjectUtils.isEmpty(content)) {
            return content.getContent();
        } else {
            return "";
        }
    }

    /**
     * 点赞
     * @param id
     */
    public void vote(Long id) {
        // 更新文档点赞数
        // docMapperCust.increaseVoteCount(id);

        // 远程IP+doc.id作为key，24小时内不能重复点击
        String key = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + key, 5000)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        // 取出日志流水号
        String log_id = MDC.get("LOG_ID");
        wsService.sendInfo("【" + docDb.getName() + "】被点赞!", log_id);

        // 一个业务逻辑一个 topic 发送方
        // rocketMQTemplate.convertAndSend("VOTE_TOPIC", "【" + docDb.getName() + "】被点赞!");
    }


    // 定时任务更新电子书信息
    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
