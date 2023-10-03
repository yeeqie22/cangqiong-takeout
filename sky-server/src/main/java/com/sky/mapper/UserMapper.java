package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openId查询用户
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 获取用户数据
     * @param map
     * @return
     */
    Integer getUserNumberByMap(Map<String,LocalDateTime> map);
}
