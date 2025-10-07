package com.springboot.tutorial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.tutorial.entity.*;

public interface StudentRepository extends JpaRepository<Student, Long>{

}