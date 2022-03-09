package com.varane.controllers;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.query.impl.predicates.SqlPredicate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


/**
 * Controllers for student related services
 * @author Sreeram Maram
 */

@RestController
public class StudentController {
    private static final Log LOG = LogFactory.getLog(StudentController.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    StudentDAO studentDAO;

    private ConcurrentMap<Integer, Student> hazelcastMap() {
        return hazelcastInstance.getMap("map");
    }

    /**
     * For given ID,
     *  -   Checks if Student with ID exists in cache. If yes, return.
     *  -   Else, get from database. If exists, return.
     *  -   Else, return  I am a teapot exception.
     *
     * @param id
     * @return Student with id=id
     * @throws InterruptedException
     */
    @GetMapping("/student/get")
    Student getStudent(Integer id) throws InterruptedException {
        LOG.info("Student data requested for id: " + id);
        Student student;
        student = hazelcastMap().get(id);
        if(student != null){
            LOG.info(String.format("Fetched student from cache for id: %d", id));
            return student;
        }
        student = studentDAO.findById2(id).orElse(null);
        if(student == null) {
            LOG.info(String.format("Student not found for id: %d", id));
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, String.format("Student with id:%d - Not Found ", id));
        }
        LOG.info(String.format("Inserting student into cache for id: %d", id));
        hazelcastMap().put(id, student);
        return student;
    }

    /**
     * Adds a Student entry into database.
     * This method also adds the entry into the cache.
     *
     * @param id
     * @param name
     * @param contact
     * @return
     * @throws InterruptedException
     */
    @PostMapping("/student/add")
    Student addStudent(Integer id, String name, String contact) throws InterruptedException {
        LOG.info("New Student ADD request with id: " + id + " Name: " + name);
        Student student = studentDAO.findById(id);
        if(student != null) {
            LOG.info(String.format("Student already exists with id: %d", student.getId()));
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Student with id:%d - Already Exists ", student.getId()));
        }
        studentDAO.insertingStudent(id, name, contact);
        student = studentDAO.findById(id);
        LOG.info(String.format("Adding student to cache with id:%d", student.getId()));
        hazelcastMap().put(student.getId(), student);
        return student;
    }

    /**
     * endpoint to demonstrate sql predicate
     * Values in Imap here are listed and queried in an SQL fashion.
     *
     * @return list of students whose name has letter s
     */
    @GetMapping("/student/sql-example")
    List<Student> getAllStudentsInCacheSql(Character ch){
        List<Student> students = new ArrayList(hazelcastInstance.getMap("map").values(new SqlPredicate("name LIKE '%s%' ")));
        return students;
    }


    /**
     * Return list of all students available in the database.
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/student/all")
    List<Student> getAllStudents() throws InterruptedException {
        List<Student> students = studentDAO.findAll();
        LOG.info("All students data requested");
        return students;
    }

    /**
     * Return list of students in currently held in cache.
     * @return
     */
    @GetMapping("/student/all-in-cache")
    List<Student> getAllStudentsInCache(){
        List<Student> students = new ArrayList(hazelcastMap().values());
        return students;
    }

    /**
     * Return students with ID less than queries id.
     * @param id
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/students/id-less-than/")
    List<Student> getStudentsIdLessThan(Integer id) throws InterruptedException {
        List<Student> students = studentDAO.getStudentsLessThanId(id);
        LOG.info("Students data with id < " + id + " requested");
        return students;
    }

}
