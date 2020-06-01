package com.sample.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sample.model.Students;
import com.sample.parser.StudentParser;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

@RestController
@Validated
@RequestMapping("/services/api/")
public class StudentController {

	@Autowired
	private Executor executor;
	@Value("${api.timeout}")
	private int apiTimeout;
	@Autowired
	private StudentParser parser;

	// As DB layer is not available used for storing data
	private final static Map<Integer, Students.StudentModel> studentDetails = new ConcurrentHashMap<>();
	private AtomicInteger id = new AtomicInteger(0);

	/**
	 * This method is responsible for creation of student
	 * @param studentData
	 * @return
	 */
	@PostMapping(value = {
			"/students/create" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CompletableFuture<ResponseEntity<String>> create(@NotNull @RequestBody final String studentData) {

		Students.StudentModel students = parser.getObjectsfromJson(studentData, Students.StudentModel.class);
		
		if(StringUtils.isEmpty(students.getName())) {
			throw new ConstraintViolationException("Failed to provide student name", new HashSet<ConstraintViolation<?>>());
		}
		students.setId(id.incrementAndGet());

		return CompletableFuture.supplyAsync(() -> {
			studentDetails.put(Integer.valueOf(students.getId()), students);
			return new ResponseEntity<String>("Student created Successfully", HttpStatus.OK);

		}, executor).orTimeout(apiTimeout, TimeUnit.MILLISECONDS);

	}

	/**
	 * This method returns all the students
	 * @return
	 */
   @GetMapping(value = {"/students" },consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public Students getAllStudents(){
		
	    Students students = new Students();
	    List<Students.StudentModel> model = new ArrayList<>();
	    
	    studentDetails.forEach((key, value) -> {
	    	model.add(value);
	    });
	    
	    model.sort( (s1, s2) -> s1.getName().compareTo(s2.getName()));
	    students.setStudents(model);
		
		return students;
	}
   
   @GetMapping(value = {"/student/{id:[\\d]+}" },consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public Students.StudentModel getStudentById(@NotNull @PathVariable("id") final Integer id){
	    return studentDetails.get(id);
   }
   
	@GetMapping(value = {
			"/student/remove/{id:[\\d]+}" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> removeStudentById(@NotNull @PathVariable("id") final Integer id) {

		studentDetails.remove(id);
		return new ResponseEntity<String>("Student removed successfully", HttpStatus.OK);
	}
	
	@GetMapping(value = {
			"/student/{name:[a-zA-Z]+}" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Students.StudentModel getStudentByName(@NotNull @PathVariable("name") final String name) {
		return studentDetails.values().stream().filter(s -> s.getName().equalsIgnoreCase(name)).findAny().orElseThrow();
	}
	
}