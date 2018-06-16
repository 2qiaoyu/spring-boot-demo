package com.joham.springbootdemo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiaoyu
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);

    List<User> findByEmailLike(String email);
}
