package com.examly.plantcare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlantCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlantCareApplication.class, args);
		System.out.println("Hello World");
        connect();
	}
	public static void connect() {
		String url="jdbc:mysql://localhost:3306/hello";
		String user="root";
		String password="root";
		
		try (Connection conn=DriverManager.getConnection(url, user, password)){
			if(conn!=null) {
				System.out.println("Connection is Successfull!");
			}		
		}
		catch(SQLException e) {
			System.out.println("Connection is Unsuccessfull!" + e.getMessage());
		}
    }
}
