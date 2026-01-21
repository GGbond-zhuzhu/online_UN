package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.ChatGroupMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatGroupMessageMapper extends BaseMapper<ChatGroupMessage> {
}

