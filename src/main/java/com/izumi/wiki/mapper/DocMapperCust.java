package com.izumi.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface DocMapperCust {
    /**
     * 更新浏览量
     * @param id
     */
    void increaseViewCount(@Param("id") Long id);

    /**
     * 更新点赞量
     * @param id
     */
    void increaseVoteCount(@Param("id") Long id);
}
