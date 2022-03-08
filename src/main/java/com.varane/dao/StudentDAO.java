package com.varane.dao;


import com.varane.models.Student;
import com.varane.repositories.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * This DAO is to insert/update student entries in databse
 * @author Sreeram Maram
 */
@Component
public class StudentDAO {
    @Autowired
    StudentRepo studentRepo;

    public Optional<Student> findById2(@Param("id") Integer id) throws InterruptedException {
        Thread.sleep(1000);
        return studentRepo.findById2(id);
    }

    public List<Student> getStudentsLessThanId(@Param("id") Integer id) throws InterruptedException {
        Thread.sleep(1000);
        return studentRepo.getStudentsLessThanId(id);
    }

    public Student findById(Integer id) throws InterruptedException {
        Thread.sleep(1000);
        Student student = studentRepo.findById(id).orElse(null);
        return student;
    }

    public void insertingStudent(Integer id, String name, String contact) throws InterruptedException {
        Thread.sleep(1000);
        studentRepo.insertingStudent(id, name, contact);
    }

    public List<Student> findAll() throws InterruptedException {
        Thread.sleep(1000);
        return (List<Student>) studentRepo.findAll();
    }
}
