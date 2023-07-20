package com.izumi.wiki.controller;

import com.izumi.wiki.req.DocQueryReq;
import com.izumi.wiki.req.DocSaveReq;
import com.izumi.wiki.resp.DocQueryResp;
import com.izumi.wiki.resp.CommonResp;
import com.izumi.wiki.resp.PageResp;
import com.izumi.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/doc")
public class DocController {


    @Resource
    private DocService docService;

    @GetMapping("/all/{ebookId}")
    public CommonResp<List<DocQueryResp>> all(@PathVariable Long ebookId) {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        CommonResp resp = new CommonResp<>();
        List<String> strList = Arrays.asList(idsStr.split(","));
        List<Long> longList = new ArrayList<>();
        for (String str : strList) {
            longList.add(Long.valueOf(str));
        }
        docService.delete(longList);
        return resp;
    }

    // 点赞
    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        docService.vote(id);
        return resp;
    }
}
