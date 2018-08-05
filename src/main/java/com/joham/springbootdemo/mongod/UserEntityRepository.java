package com.joham.springbootdemo.mongod;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * mongo查询
 *
 * @author joham
 */
public interface UserEntityRepository extends MongoRepository<UserEntity, String>,
        QueryDslPredicateExecutor<UserEntity> {

    @Query("{'userName' : ?0}")
    List<UserEntity> findByUserName(String userName);

    List<UserEntity> findByUserNameStartingWith(String a);
}
