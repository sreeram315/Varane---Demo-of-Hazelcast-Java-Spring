package com.varane.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Model class for students
 */
@Entity
public class Student implements Serializable {
    @Id
    Integer id;
    String name;
    String contact;

    public Student(){}

    public Student(Integer id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
