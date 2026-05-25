package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findById(@Param("id") Long id);

    Optional<User> findByEmail(@Param("email") String email);

    int insert(User user);

    int update(User user);
}
