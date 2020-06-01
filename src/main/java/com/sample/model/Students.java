package com.sample.model;

import java.util.List;

import org.springframework.lang.NonNull;

import lombok.Data;

@Data
public class Students {

	private List<StudentModel> students;
	@Data
	public static class StudentModel {
		
		@NonNull
		private String name;
		private int id;
		private int age;
	}
}
