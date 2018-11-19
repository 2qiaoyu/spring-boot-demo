package com.joham.springbootdemo.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 文章控制类
 *
 * @author joham
 */
@RestController
@RequestMapping("article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 添加文章
     *
     * @param articleSaveRequest 添加请求
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String saveArticle(@RequestBody ArticleSaveRequest articleSaveRequest) {
        return articleService.postArticle(articleSaveRequest.getPoster(), articleSaveRequest.getTitle(),
                articleSaveRequest.getLink());
    }

    /**
     * 文章投票
     *
     * @param user      会员
     * @param articleId 文章Id
     * @return
     */
    @RequestMapping(value = "voteArticle", method = RequestMethod.POST)
    public String vote(String user, String articleId) {
        articleService.voteArticle(user, "article:" + articleId);
        return "1";
    }

    /**
     * 按排名获取文章
     *
     * @return
     */
    @RequestMapping(value = "getArticles", method = RequestMethod.GET)
    public List<Map<String, String>> getArticles(int page) {
        return articleService.getArticles(page, "score:");
    }

    /**
     * 添加文章到分组
     *
     * @param groupId   组Id
     * @param articleId 文章Id
     * @return
     */
    @RequestMapping(value = "addGroups", method = RequestMethod.POST)
    public String addGroups(String groupId, String articleId) {
        articleService.addGroups(groupId, articleId);
        return "1";
    }

    /**
     * 根据分组Id获取分组文章
     *
     * @param groupId 组Id
     * @return
     */
    @RequestMapping(value = "getGroupArticles", method = RequestMethod.GET)
    public List<Map<String, String>> getGroupArticles(String groupId) {
        return articleService.getGroupArticles("group:" +groupId);
    }
}
