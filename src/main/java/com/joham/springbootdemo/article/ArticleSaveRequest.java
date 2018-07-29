package com.joham.springbootdemo.article;

import lombok.Data;

/**
 * 添加文章类
 *
 * @author joham
 */
@Data
public class ArticleSaveRequest {

    private String poster;

    private String title;

    private String link;
}
