package com.varane.repositories;

import com.varane.models.Student;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Dear CrudRepository to ease our database access.
 * For Student database.
 *
 * @author Sreeram Maram
 */
public interface StudentRepo extends CrudRepository<Student, Integer> {
    @Query("SELECT u FROM Student u WHERE u.id = :id")
    Optional<Student> findById2(@Param("id") Integer id);

    @Query("FROM Student WHERE id < :id")
    List<Student> getStudentsLessThanId(@Param("id") Integer id);

    @Modifying
    @Query(value = "INSERT INTO student(id, name, contact) VALUES(:id, :name, :contact)", nativeQuery = true)
    @Transactional
    void insertingStudent(@Param("id") Integer id, @Param("name") String name, @Param("contact") String contact);

}
