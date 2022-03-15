package com.varane.controllers;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.impl.predicates.SqlPredicate;
import com.hazelcast.sql.SqlService;
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


/**
 * Controllers for student related services
 * @author Sreeram Maram
 */

@RestController
public class StudentController {
    private static final Log LOG = LogFactory.getLog(StudentController.class);

    @Autowired
    StudentDAO studentDAO;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    /**
     * For given ID,
     *  -   Checks if Student with ID exists in cache. If yes, return.
     *  -   Else, get from database. If exists, return.
     *  -   Else, return  I am a teapot exception.
     *
     * @param id id of the student
     * @return Student with id=id
     * @throws InterruptedException yes it does
     */
    @GetMapping("/student/get")
    Student getStudent(Integer id) throws InterruptedException {
        LOG.info("Student data requested for id: " + id);
        Student student = studentDAO.findByIdCustom(id);
        if(student == null)
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, String.format("Student with id:%d - Not Found ", id));
        return student;
    }

    /**
     * Adds a Student entry into database.
     * This method also adds the entry into the cache.
     *
     * @param id id of the student
     * @param name name of the student
     * @param contact contact of the student
     * @return student created
     * @throws InterruptedException yes it does
     */
    @PostMapping("/student/add")
    Student addStudent(Integer id, String name, String contact) throws InterruptedException {
        return studentDAO.insertingStudent(id, name, contact);
    }

    /**
     * endpoint to demonstrate sql predicate
     * Values in Imap here are listed and queried in an SQL fashion.
     *
     * Returns the students who have substring name in their name (cached students are only queried)
     *
     * @param substring substring that is required to be present in student's name
     * @return list of students whose name has letter s
     */
    @GetMapping("/student/cache-name-contains")
    List<Student> getAllStudentsInCacheSql(String substring){
        String sqlPredicateString = "name LIKE %" + substring + "%";
        IMap<Integer, Student> hazelcastStudentsMap = studentDAO.getHazelcastStudentsMap();
        List<Student> students = null;

      students = new ArrayList(hazelcastStudentsMap.values(new SqlPredicate(sqlPredicateString)));
        return students;
    }

    @GetMapping("/sql-test")
    void sqlTest(){
//        HazelcastInstance hazelcastInstance = studentDAO.getHazelcastInstance();
        SqlService sqlService = hazelcastInstance.getSql();

        sqlService.execute(" ");

//        sqlService.execute("CREATE MAPPING studentMap ( "
//                + "__key INT, "
//                + "id INT ) "
//                + "name VARCHAR ,"
//                + "contact VARCHAR,"
//                + "TYPE IMap "
//                + "OPTIONS ("
//                + "    'keyFormat'='int', "
//                + "    'valueFormat'='compact', "
//                + "    'valueCompactTypeName'='" + Student.class.getName() + "' ) ");

//        sqlService.execute("INSERT INTO studentMap (__key, name, surname, id) VALUES (202020, ?, ?, 202020)", "Jack", "Sparrowlord");

//        Query map with sql
//        SqlResult sqlRows = sqlService.execute("SELECT * FROM studentMap WHERE id = 202020");
//        for (SqlRow sqlRow : sqlRows) {
//            System.out.println(sqlRow);
//        }
//        try (SqlResult result = hazelcastInstance.getSql().execute("SELECT __key FROM student")) {
//            for (SqlRow row : result) {
//                String student_name = row.getObject(0);
//
//                System.out.println(student_name);
//            }
//        }
    }


    /**
     * Return list of all students available in the database.
     * @return list of all students in database
     * @throws InterruptedException yes it does
     */
    @GetMapping("/student/all")
    List<Student> getAllStudents() throws InterruptedException {
        List<Student> students = studentDAO.findAll();
        LOG.info("All students data requested");
        return students;
    }

    /**
     * Return list of students in currently held in cache.
     * @return list of all students in cache
     */
    @GetMapping("/student/all-in-cache")
    List<Student> getAllStudentsInCache(){
        IMap<Integer, Student> hazelcastStudentsMap = studentDAO.getHazelcastStudentsMap();
        List<Student> students = new ArrayList(hazelcastStudentsMap.values());
        return students;
    }

    /**
     * Return students with ID less than queries id.
     * @param id id of the student
     * @return list of all students with ID less than queried id
     * @throws InterruptedException yes it does
     */
    @GetMapping("/students/id-less-than/")
    List<Student> getStudentsIdLessThan(Integer id) throws InterruptedException {
        List<Student> students = studentDAO.getStudentsLessThanId(id);
        LOG.info("Students data with id < " + id + " requested");
        return students;
    }

}
