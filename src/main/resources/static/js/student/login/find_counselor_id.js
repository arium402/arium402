document.getElementById('findCounselorIdForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    // 입력값 검증
    const name = document.getElementById('counselorName').value.trim();
    const phone = document.getElementById('counselorPhone').value.trim();
    const email = document.getElementById('counselorEmail').value.trim();
    
    // 에러 메시지 초기화
    clearErrorMessages();
    
    let hasError = false;
    
    // 이름 검증
    if (!name) {
        showError('nameError', '이름을 입력해주세요.');
        hasError = true;
    }
    
    // 전화번호 검증
    if (!phone) {
        showError('phoneError', '전화번호를 입력해주세요.');
        hasError = true;
    } else if (!validatePhone(phone)) {
        showError('phoneError', '올바른 전화번호 형식이 아닙니다.');
        hasError = true;
    }
    
    // 이메일 검증
    if (!email) {
        showError('emailError', '이메일을 입력해주세요.');
        hasError = true;
    } else if (!validateEmail(email)) {
        showError('emailError', '올바른 이메일 형식이 아닙니다.');
        hasError = true;
    }
    
    if (hasError) return;
    
    // 서버로 데이터 전송
    findCounselorId(name, phone, email);
});

function findCounselorId(name, phone, email) {
    console.log("=== 사번 찾기 시작 ===");
    console.log("이름:", name);
    console.log("전화번호:", phone);
    console.log("이메일:", email);
    
    const data = {
        counselorName: name,
        counselorPhone: phone,
        counselorEmail: email
    };
    
    console.log("전송 데이터:", data);

    fetch("/counselor/find_id", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        console.log("응답 상태:", response.status);
        console.log("응답 OK:", response.ok);  // ✅ ] 제거
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response.json();
    })
    .then(result => {
        console.log("응답 데이터:", result);
        
        if (result.success) {
            showResult(result.emplNo);
        } else {
            alert(result.message || '사번을 찾을 수 없습니다.');
        }
    })
    .catch(err => {
        console.error("=== 에러 발생 ===");
        console.error("에러 타입:", err.name);
        console.error("에러 메시지:", err.message);
        console.error("전체 에러:", err);
        
        alert("서버 요청 중 오류가 발생했습니다: " + err.message);
    });
}

function showResult(counselorId) {
    document.getElementById('findCounselorIdForm').style.display = 'none';
    document.getElementById('resultSection').style.display = 'block';
    document.getElementById('resultContent').textContent = counselorId;
}

function copyToParent() {
    const counselorId = document.getElementById('resultContent').textContent;
    
    // 부모 창의 아이디 입력 필드에 값 설정
    if (window.opener && !window.opener.closed) {
        const parentCounselorIdField = window.opener.document.getElementById('counselorId');
        if (parentCounselorIdField) {
            parentCounselorIdField.value = counselorId;
            parentCounselorIdField.focus();
        }
    }
    
    window.close();
}

function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    errorElement.textContent = message;
    errorElement.style.display = 'block';
}

function clearErrorMessages() {
    const errorElements = document.querySelectorAll('.error-message');
    errorElements.forEach(element => {
        element.style.display = 'none';
        element.textContent = '';
    });
}

function validatePhone(phone) {
    const phoneRegex = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/;
    return phoneRegex.test(phone.replace(/\s/g, ''));
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// 전화번호 자동 포맷팅
document.getElementById('counselorPhone').addEventListener('input', function(e) {
    let value = e.target.value.replace(/[^0-9]/g, '');
    if (value.length >= 3 && value.length <= 7) {
        value = value.substring(0, 3) + '-' + value.substring(3);
    } else if (value.length > 7) {
        value = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7, 11);
    }
    e.target.value = value;
});