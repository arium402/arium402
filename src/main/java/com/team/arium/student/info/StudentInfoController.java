package com.team.arium.student.info;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class StudentInfoController {
	
	private final StudentService studentservice;
	
	public StudentInfoController(StudentService studentService) {
        this.studentservice = studentService;
    }

    @PostMapping("/find_id")
    public ResponseEntity<Map<String, Object>> findStudentId(@RequestBody StudentInfoDto dto) {
    	 Map<String, Object> res = new HashMap<>();
    	
    	String studentId = studentservice.findStudentId(dto.getStudentName(), dto.getStudentPhone(), dto.getStudentEmail());
        
        if (studentId != null) {
            res.put("success", true);
            res.put("stdNo", studentId);
            return ResponseEntity.ok(res);
        } else {
            res.put("success", false);
            res.put("message", "일치하는 학생 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }
}
