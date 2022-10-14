package com.jozz.venus.domain;

import com.jozz.venus.annotation.Document;

@Document(indexName = "_user")
public class User {
//    @Id
    public Long id;
    public String name;
    public int age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
