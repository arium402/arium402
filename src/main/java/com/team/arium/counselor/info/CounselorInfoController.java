package com.team.arium.counselor.info;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/counselor")
public class CounselorInfoController {

	private final CounselorService counselorService;
	
	public CounselorInfoController(CounselorService counselorService) {
		this.counselorService = counselorService;
	}
	
	@PostMapping("/find_id")
    public ResponseEntity<Map<String, Object>> findCounselorId(@RequestBody CounselorInfoDto dto) {
    	 Map<String, Object> res = new HashMap<>();
    	
    	String counselorId = counselorService.findCounselorId(dto.getCounselorName(), dto.getCounselorPhone(), dto.getCounselorEmail());
        
        if (counselorId != null) {
            res.put("success", true);
            res.put("emplNo", counselorId);
            return ResponseEntity.ok(res);
        } else {
            res.put("success", false);
            res.put("message", "일치하는 상담사 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }
}
