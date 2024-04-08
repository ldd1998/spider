package org.example.spider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName(value ="content")
@Data
public class Content {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String url;
    private String title;
    private String content;
    private Integer parentId;
    private Integer status;
}