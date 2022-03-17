package com.varane.dao;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.varane.controllers.student.StudentConstants;
import com.varane.models.Student;
import com.varane.repositories.StudentRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * This DAO is to insert/update student entries in databse
 * @author Sreeram Maram
 */
@Component
public class StudentDAO {
    private static final Log LOG = LogFactory.getLog(StudentDAO.class);

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    public IMap<Integer, Student> getHazelcastStudentsMap() {
        return hazelcastInstance.getMap(StudentConstants.CACHE_MAP);
    }

    public Student findByIdCustom(@Param("id") Integer id) throws InterruptedException {
        Student student = getHazelcastStudentsMap().get(id);
        if(student != null){
            LOG.info(String.format("Fetched student from cache for id: %d", id));
            return student;
        }
        Thread.sleep(1000);
        student = studentRepo.findById2(id).orElse(null);
        if(student != null) {
            LOG.info(String.format("Inserting student into cache for id: %d", id));
            getHazelcastStudentsMap().put(id, student);
        } else{
            LOG.info(String.format("Student not found for id: %d", id));
        }
        return student;
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

    public Student insertingStudent(Integer id, String name, String contact) throws InterruptedException {
        LOG.info("New Student ADD request with id: " + id + " Name: " + name);
        Student student = this.findById(id);
        if(student != null) {
            LOG.info(String.format("Student already exists with id: %d", student.getId()));
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Student with id:%d - Already Exists ", student.getId()));
        }
        Thread.sleep(1000);
        // more on locks: https://docs.hazelcast.com/imdg/4.2/data-structures/fencedlock
        Lock lock = hazelcastInstance.getCPSubsystem().getLock(StudentConstants.STUDENT_TABLE_LOCK);
        lock.lock();
        studentRepo.insertingStudent(id, name, contact);
        lock.unlock();
        student = this.findById(id);
        LOG.info(String.format("Adding student to cache with id: %d", student.getId()));
        getHazelcastStudentsMap().put(student.getId(), student);
        return student;
    }

    public List<Student> findAll() throws InterruptedException {
        Thread.sleep(1000);
        return (List<Student>) studentRepo.findAll();
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }
}
