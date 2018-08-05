package com.joham.springbootdemo.mongod;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 会员
 *
 * @author joham
 */
@Component
public class UserEntityService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserEntityRepository userEntityRepository;

    /**
     * 创建对象
     *
     * @param user
     */
    public void saveUser(UserEntity user) {
        mongoTemplate.save(user);
    }

    /**
     * 根据用户名查询对象
     *
     * @param userName
     * @return
     */
    public UserEntity findUserByUserName(String userName) {
        Query query = new Query(Criteria.where("userName").is(userName));
        UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
        return user;
    }

    /**
     * 正则表达式查询
     *
     * @return
     */
    public List<UserEntity> find() {
        Pageable pageableRequest = new PageRequest(0, 2);
        //模糊查询
        Query query = new Query(Criteria.where("userName").regex("t"));
        //排序，分页
        query.with(new Sort(Sort.Direction.ASC, "age")).with(pageableRequest);
        List<UserEntity> userEntityList = mongoTemplate.find(query, UserEntity.class);
        return userEntityList;
    }

    public List<UserEntity> find1() {
        return userEntityRepository.findByUserName("test");
//        return userEntityRepository.findByUserNameStartingWith("t");
    }

    public List<UserEntity> find2() {
        QUserEntity qUserEntity = new QUserEntity("user");
        Predicate predicate = qUserEntity.userName.eq("test");
        return (List<UserEntity>)userEntityRepository.findAll(predicate);
//        return userEntityRepository.findByUserNameStartingWith("t");
    }

    /**
     * 更新对象
     *
     * @param user
     */
    public void updateUser(UserEntity user) {
        Query query = new Query(Criteria.where("id").is(user.getId()));
        Update update = new Update().set("userName", user.getUserName()).set("passWord", user.getPassWord());
        //更新查询返回结果集的第一条
        mongoTemplate.updateFirst(query, update, UserEntity.class);
        //更新查询返回结果集的所有
        // mongoTemplate.updateMulti(query,update,UserEntity.class);
    }

    /**
     * 删除对象
     *
     * @param id
     */
    public void deleteUserById(Long id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, UserEntity.class);
    }
}
