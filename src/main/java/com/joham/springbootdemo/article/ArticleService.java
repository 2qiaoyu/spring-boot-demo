package com.joham.springbootdemo.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文章服务类
 *
 * @author joham
 */

@Service
public class ArticleService {

    private static final int VOTE_SCORE = 432;
    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    private static final int ARTICLES_PER_PAGE = 25;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增文章
     *
     * @param poster
     * @param title
     * @param link
     * @return
     */
    public String postArticle(String poster, String title, String link) {
        //简单地获取文章自增ID
        Long incrementId = redisTemplate.opsForValue().increment("article:", 1);
        Long now = System.currentTimeMillis() / 1000;
        String articleId = "article:" + incrementId;
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("link", link);
        map.put("poster", poster);
        map.put("time", String.valueOf(now));
        map.put("votes", "1");
        redisTemplate.opsForHash().putAll(articleId, map);

        //新增文章投票记录，自动清除一周以前的投票记录
        String voted = "voted:" + incrementId;
        redisTemplate.opsForSet().add(voted, poster);
        redisTemplate.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);

        //新增文章分数、发布时间排行榜
        redisTemplate.opsForZSet().add("score:", articleId, now + VOTE_SCORE);
        redisTemplate.opsForZSet().add("time:", articleId, now);
        return articleId;
    }

    /**
     * 文章投票
     *
     * @param user
     * @param articleId
     */
    public void voteArticle(String user, String articleId) {
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        //若文章超过一周则不再投票
        if (redisTemplate.opsForZSet().score("time:", articleId) < cutoff) {
            return;
        }


        String id = articleId.substring(articleId.indexOf(':') + 1);
        if (redisTemplate.opsForSet().add("voted:" + id, user) == 1) {
            /**
             * sadd返回1，表示投票成功
             * 分数+VOTE_SCORE，投票数+1
             */
            redisTemplate.opsForZSet().incrementScore("score:", articleId, VOTE_SCORE);
            redisTemplate.opsForHash().increment(articleId, "votes", 1);
        }
        //上述opsForSet().add、opsForZSet().incrementScore、opsForHash().increment应该在同一个事物中，后面再讲，暂时忽略
    }

    /**
     * 按排名获取文章
     *
     * @param page
     * @param key
     * @return
     */
    public List<Map<String, String>> getArticles(int page, String key) {
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;

        Set<Object> ids = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return getArticlesByIds(ids);
    }

    private List<Map<String, String>> getArticlesByIds(Set<Object> ids) {
        List<Map<String, String>> articles = new ArrayList<>();
        ids.forEach(id -> {
            Map<Object, Object> tmpMap = redisTemplate.opsForHash().entries(id.toString());
            Map<String, String> article = new HashMap<>();
            tmpMap.forEach((k, v) -> {
                article.put("id", id.toString());
                article.put("title", tmpMap.get("title").toString());
                article.put("link", tmpMap.get("link").toString());
                article.put("poster", tmpMap.get("poster").toString());
                article.put("time", tmpMap.get("time").toString());
                article.put("votes", tmpMap.get("votes").toString());
            });
            articles.add(article);
        });
        return articles;
    }

    /**
     * 按组获取文章
     *
     * @param groupId
     * @return
     */
    public List<Map<String, String>> getGroupArticles(String groupId) {
        Set<Object> ids = redisTemplate.opsForSet().members(groupId);
        return getArticlesByIds(ids);
    }


    /**
     * 添加组
     *
     * @param groupId
     * @param articleId
     */
    public void addGroups(String groupId, String articleId) {
        String article = "article:" + articleId;
        redisTemplate.opsForSet().add("group:" + groupId, article);
    }
}
