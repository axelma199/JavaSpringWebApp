package com.springboot.tutorial.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import jakarta.validation.Valid;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.tutorial.entity.Student;
import com.springboot.tutorial.service.StudentService;

@Controller
public class StudentController {
	
	private StudentService studentService;

	public StudentController(StudentService studentService) {
		super();
		this.studentService = studentService;
	}
	
	@GetMapping("/")
	public String redirectToStudents() {
	    return "redirect:/students";
	}
	
	// handler method to handle list students and return mode and view
	@GetMapping("/students")
	public String listStudents(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		return "students";
	}
	
	@GetMapping("/students/create_students")
	public String createStudentForm(Model model) {
		
		// create student object to hold student form data
		Student student = new Student();
		model.addAttribute("student", student);
		return "create_students";
		
	}
	
 
	@PostMapping("/students")
	public String saveStudent(@Valid @ModelAttribute("student") Student student,
	                          BindingResult result,
	                          @RequestParam("resume") MultipartFile resumeFile) {
	    if (result.hasErrors()) {
	    	result.getAllErrors().forEach(error -> System.out.println(error.toString()));
	        return "student-form"; // return to your form view
	    }

	    try {
	        if (!resumeFile.isEmpty()) {
	            student.setResume(resumeFile.getBytes());
	        }
	        studentService.saveStudent(student);
	        return "redirect:/students";
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "error";
	    }
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.setDisallowedFields("resume");
	}
	
	@GetMapping("/students/edit/{id}")
	public String editStudentForm(@PathVariable Long id, Model model) {
		Student s=studentService.getStudentById(id);
		
		byte[] resumeBytes = s.getResume();
		try (PDDocument document = PDDocument.load(new ByteArrayInputStream(resumeBytes))) {
	        PDFTextStripper stripper = new PDFTextStripper();
	        String text = stripper.getText(document);

	        // Split text into words using whitespace
	        String[] words = text.split("\\s+");

	        for (String word : words)  {
	            System.out.println(word);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		model.addAttribute("student", s);
		return "edit_student";
	}

	@PostMapping("/students/{id}")
	public String updateStudent(@PathVariable Long id,
			@ModelAttribute("student") Student student,
			Model model) {
		
		// get student from database by id
		Student existingStudent = studentService.getStudentById(id);
		existingStudent.setId(id);
		existingStudent.setFirstName(student.getFirstName());
		existingStudent.setLastName(student.getLastName());
		existingStudent.setEmail(student.getEmail());
		
		// save updated student object
		studentService.updateStudent(existingStudent);
		return "redirect:/students";		
	}
	
	// handler method to handle delete student request
	
	@GetMapping("/students/{id}")
	public String deleteStudent(@PathVariable Long id) {
		studentService.deleteStudentById(id);
		return "redirect:/students";
	}	
	
}	