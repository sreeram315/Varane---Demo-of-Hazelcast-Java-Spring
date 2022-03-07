package com.varane.dao;


import com.varane.models.Student;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentDAO extends CrudRepository<Student, Integer> {
    @Cacheable(value = "students", key = "#id")
    @Query("SELECT u FROM Student u WHERE u.id = :id")
    Optional<Student> findById2(@Param("id") Integer id);

    @Cacheable(value = "studentsLessThan", key = "#id")
    @Query("FROM Student WHERE id < :id")
    List<Student> getStudentsLessThanId(@Param("id") Integer id);

}
