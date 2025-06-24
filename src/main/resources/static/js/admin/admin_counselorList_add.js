// 전화번호 자동 포맷팅
document.getElementById('counselorPhone').addEventListener('input', function(e) {
    let value = e.target.value.replace(/[^0-9]/g, '');
    
    if (value.length <= 3) {
        e.target.value = value;
    } else if (value.length <= 7) {
        e.target.value = value.slice(0, 3) + '-' + value.slice(3);
    } else {
        e.target.value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
    }
});

// 메시지 표시 함수
function showMessage(type, message) {
    document.querySelector("#successMessage").style.display = 'none';
    document.querySelector("#errorMessage").style.display = 'none';

    const messageElement = document.getElementById(type + 'Message');
    const textElement = messageElement.querySelector('.message-text');
    
    if (textElement) {
        textElement.textContent = message;
    }

    messageElement.style.display = 'block';

    setTimeout(() => {
        messageElement.style.display = 'none';
    }, 3000);

    messageElement.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

//등록 버튼 클릭시 
document.querySelector("#counselorAddBtn").addEventListener('click', function() {
   	const cns_name = document.querySelector("#counselorName");
	const cns_field = document.querySelector("#counselorField");
	const cns_phone = document.querySelector("#counselorPhone");
	const cns_mail = document.querySelector("#counselorEmail");
	
	//형식검사
	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	const phoneRegex = /^010-\d{4}-\d{4}$/;
    
	const cns_info = {
		emplName : cns_name.value.trim(),
        cnslCd: cns_field.value,
		emplTellno: cns_phone.value.trim(),
		emplEmlAddr: cns_mail.value.trim(),
		emplStatCd:"31"
  	};
	
	console.log(cns_info)
	// 유효성 검사
    if (cns_name.value=="") {
        showMessage('error', '상담사의 이름을 입력해주세요.');
        cns_name.focus();
    }
	else if (cns_field.value=="") {
        showMessage('error', '상담분야를 선택해주세요.');
        cns_field.focus();
    }
	else if (cns_phone.value=="") {
        showMessage('error', '전화번호를 입력해주세요.');
        cns_phone.focus();
    }
	else if (!phoneRegex.test(cns_phone.value)) {
       showMessage('error', '올바른 전화번호 형식을 입력해주세요. (010-0000-0000)');
       cns_phone.focus();
   }
    else if (cns_mail.value=="") {
        showMessage('error', '이메일을 입력해주세요.');
       	cns_mail.focus();
    }
    else if (!emailRegex.test(cns_mail.value)) {
        showMessage('error', '올바른 이메일 형식을 입력해주세요.');
        cns_mail.focus();
    }
	else {
		//데이터 전송
		addCounselor(cns_info)
	}
});


//상담사 등록 ajax
function addCounselor(cns_info){
	console.log(JSON.stringify(cns_info))
	fetch("/admin/admin_counselorList_addOk", {
		method: "PUT",
		headers: {'content-type': 'application/json'},
		body : JSON.stringify(cns_info),
		//credentials: "include" 
		
	}).then(function(data) {
		return data.text();

	}).then(function(result) {
		console.log("result : " + result)
		if(result=="ok"){
			
			// 성공 메시지 표시
		   showMessage('success');
		   
		   // 폼 초기화
		   document.querySelector("#counselorForm").reset();
		   
		   // 5초 후 목록 페이지로 이동 확인
		   setTimeout(() => {
		       if (confirm('상담사 목록 페이지로 이동하시겠습니까?')) {
		       		location.href = "./admin/admin_counselorList";
		       }
		   }, 2000);
		   
		}else if(result=="fail"){
			alert("BOM 등록에 실패했습니다.");
		}

	}).catch(function(error) {
		console.log("통신오류발생" + error);
	}); 
	
}

// 취소 버튼 처리
function cancelRegister() {
    // 입력된 내용이 있는지 확인
    const hasContent = document.getElementById('counselorName').value ||
                     document.getElementById('counselorEmpNo').value ||
                     document.getElementById('counselorField').value ||
                     document.getElementById('counselorPhone').value ||
                     document.getElementById('counselorEmail').value;
    
    if (hasContent) {
        if (confirm('작성 중인 내용이 사라집니다. 정말 취소하시겠습니까?')) {
            window.location.href = 'counselor_list.html';
        }
    } else {
       location.href = 'counselor_list.html';
    }
}

// 입력 필드 실시간 검증
document.getElementById('counselorEmail').addEventListener('blur', function() {
    const email = this.value.trim();
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        this.style.borderColor = '#e74c3c';
    } else {
        this.style.borderColor = '#e9ecef';
    }
});

document.getElementById('counselorPhone').addEventListener('blur', function() {
    const phone = this.value.trim();
    if (phone && !/^010-\d{4}-\d{4}$/.test(phone)) {
        this.style.borderColor = '#e74c3c';
    } else {
        this.style.borderColor = '#e9ecef';
    }
});