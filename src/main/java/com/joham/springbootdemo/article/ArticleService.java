package com.joham.springbootdemo.article;

import com.joham.springbootdemo.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RedisUtil redisUtil;

    /**
     * 新增文章
     *
     * @param poster 发布者
     * @param title  标题
     * @param link   链接
     * @return
     */
    String postArticle(String poster, String title, String link) {
        //简单地获取文章自增ID
        Long incrementId = redisUtil.incrBy("article:", 1);
        Long now = System.currentTimeMillis() / 1000;
        String articleId = "article:" + incrementId;
        Map<String, String> map = new HashMap<>(16);
        map.put("title", title);
        map.put("link", link);
        map.put("poster", poster);
        map.put("time", String.valueOf(now));
        map.put("votes", "1");
        redisUtil.hPutAll(articleId, map);

        //新增文章投票记录，自动清除一周以前的投票记录
        String voted = "voted:" + incrementId;
        redisUtil.sAdd(voted, poster);
        redisUtil.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);

        //新增文章分数、发布时间排行榜
        redisUtil.zAdd("score:", articleId, now + VOTE_SCORE);
        redisUtil.zAdd("time:", articleId, now);
        return articleId;
    }

    /**
     * 文章投票
     *
     * @param user      用户
     * @param articleId 文章id
     */
    void voteArticle(String user, String articleId) {
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        //若文章超过一周则不再投票
        if (redisUtil.zScore("time:", articleId) < cutoff) {
            return;
        }

        String id = articleId.substring(articleId.indexOf(':') + 1);

        if (redisUtil.sAdd("voted:" + id, user) == 1) {
            //返回1，表示投票成功,分数+VOTE_SCORE，投票数+1
            redisUtil.zIncrementScore("score:", articleId, VOTE_SCORE);
            redisUtil.hIncrByFloat(articleId, "votes", 1);
        }
        //上述opsForSet().add、opsForZSet().incrementScore、opsForHash().increment应该在同一个事物中，后面再讲，暂时忽略
    }

    /**
     * 按排名获取文章
     *
     * @param page 页码
     * @param key  key名称
     * @return
     */
    public List<Map<String, String>> getArticles(int page, String key) {
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;

        Set<String> ids = redisUtil.zReverseRange(key, start, end);
        return getArticlesByIds(ids);
    }

    /**
     * 根据id集合查询文章
     *
     * @param ids ids集合
     * @return
     */
    private List<Map<String, String>> getArticlesByIds(Set<String> ids) {
        List<Map<String, String>> articles = new ArrayList<>();
        ids.forEach(id -> {
            Map<Object, Object> tmpMap = redisUtil.hGetAll(id);
            Map<String, String> article = new HashMap<>(16);
            tmpMap.forEach((k, v) -> {
                article.put("id", id);
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
     * @param groupId 组id
     * @return
     */
    public List<Map<String, String>> getGroupArticles(String groupId) {
        Set<String> ids = redisUtil.setMembers(groupId);
        return getArticlesByIds(ids);
    }


    /**
     * 添加组
     *
     * @param groupId   组id
     * @param articleId 文章id
     */
    public void addGroups(String groupId, String articleId) {
        String article = "article:" + articleId;
        redisUtil.sAdd("group:" + groupId, article);
    }
}
