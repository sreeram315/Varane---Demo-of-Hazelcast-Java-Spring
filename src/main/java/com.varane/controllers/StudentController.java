package com.varane.controllers;

import com.hazelcast.core.HazelcastInstance;
import com.varane.dao.StudentDAO;
import com.varane.models.Student;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

@RestController
public class StudentController {
    private static final Log LOG = LogFactory.getLog(StudentController.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    StudentDAO studentDAO;

    @GetMapping("/hello")
    String hello(){
        return "hello";
    }

    private ConcurrentMap<Integer, Student> hazelcastMap() {
        return hazelcastInstance.getMap("map");
    }

    @GetMapping("/student/get")
    Student getStudent(Integer id){
        LOG.info("Student data requested for id: " + id);
        Student student;
        student = hazelcastMap().get(id);
        if(student != null){
            LOG.info(String.format("Fetched student from cache for id: %d", id));
            return student;
        }
        student = studentDAO.findById2(id).orElse(null);
        if(student == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Student with id:%d - Not Found ", id));
        LOG.info(String.format("Inserting student into cache for id: %d", id));
        hazelcastMap().put(id, student);
        return student;
    }

    @PostMapping("/student/add")
    Student addStudent(Student student){
        Student studentObj = studentDAO.findById(student.getId()).orElse(null);
        if(studentObj != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Student with id:%d - Already Exists ", studentObj.getId()));
        studentDAO.save(student);
        LOG.info("New Student added with id: " + student.getId() + " Name: " + student.getName());
        return student;
    }

    @GetMapping("/student/all")
    List<Student> getAllStudents(){
        List<Student> students = (List<Student>) studentDAO.findAll();
        LOG.info("All students data requested");
        return students;
    }

    @GetMapping("/students/id-less-than/")
    List<Student> getStudentsIdLessThan(Integer id){
        List<Student> students = studentDAO.getStudentsLessThanId(id);
        LOG.info("Students data wiht id < " + id + " requested");
        return students;
    }

}
