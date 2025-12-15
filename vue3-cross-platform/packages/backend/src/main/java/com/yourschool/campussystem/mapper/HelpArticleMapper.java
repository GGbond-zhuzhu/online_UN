package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.HelpArticle;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帮助文档Mapper接口
 */
@Mapper
public interface HelpArticleMapper extends BaseMapper<HelpArticle> {
}
