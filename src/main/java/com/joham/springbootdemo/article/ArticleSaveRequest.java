package com.joham.springbootdemo.article;

import lombok.Data;

/**
 * 添加文章类
 *
 * @author joham
 */
@Data
public class ArticleSaveRequest {

    /**
     * 发布者
     */
    private String poster;

    /**
     * 标题
     */
    private String title;

    /**
     * 链接
     */
    private String link;
}
