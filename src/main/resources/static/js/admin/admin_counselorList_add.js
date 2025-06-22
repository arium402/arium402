// 사이드바 토글 기능
document.getElementById('sidebarToggle').addEventListener('click', function() {
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');
    
    if (window.innerWidth <= 768) {
        sidebar.classList.toggle('show');
    } else {
        sidebar.classList.toggle('collapsed');
        content.classList.toggle('expanded');
    }
});

// 윈도우 리사이즈 시 클래스 정리
window.addEventListener('resize', function() {
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');
    
    if (window.innerWidth > 768) {
        sidebar.classList.remove('show');
    } else {
        sidebar.classList.remove('collapsed');
        content.classList.remove('expanded');
    }
});

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
    // 모든 메시지 숨기기
    document.getElementById('successMessage').style.display = 'none';
    document.getElementById('errorMessage').style.display = 'none';
    
    // 해당 메시지 표시
    const messageElement = document.getElementById(type + 'Message');
    if (message) {
        messageElement.innerHTML = messageElement.innerHTML.replace(/>.+<\//, '>' + message + '</');
    }
    messageElement.style.display = 'block';
    
    // 3초 후 자동 숨김
    setTimeout(() => {
        messageElement.style.display = 'none';
    }, 3000);
    
    // 메시지로 스크롤
    messageElement.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// 폼 제출 처리
document.getElementById('counselorForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    // 폼 데이터 수집
    const formData = {
        name: document.getElementById('counselorName').value.trim(),
        empNo: document.getElementById('counselorEmpNo').value.trim(),
        field: document.getElementById('counselorField').value,
        phone: document.getElementById('counselorPhone').value.trim(),
        email: document.getElementById('counselorEmail').value.trim(),
        status: '재직',
        regDate: new Date().toISOString().split('T')[0]
    };
    
    // 유효성 검사
    if (!formData.name) {
        showMessage('error', '이름을 입력해주세요.');
        document.getElementById('counselorName').focus();
        return;
    }
    
    if (!formData.empNo) {
        showMessage('error', '사번을 입력해주세요.');
        document.getElementById('counselorEmpNo').focus();
        return;
    }
    
    if (!formData.field) {
        showMessage('error', '분야를 선택해주세요.');
        document.getElementById('counselorField').focus();
        return;
    }
    
    if (!formData.phone) {
        showMessage('error', '전화번호를 입력해주세요.');
        document.getElementById('counselorPhone').focus();
        return;
    }
    
    if (!formData.email) {
        showMessage('error', '이메일을 입력해주세요.');
        document.getElementById('counselorEmail').focus();
        return;
    }
    
    // 이메일 형식 검사
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
        showMessage('error', '올바른 이메일 형식을 입력해주세요.');
        document.getElementById('counselorEmail').focus();
        return;
    }
    
    // 전화번호 형식 검사
    const phoneRegex = /^010-\d{4}-\d{4}$/;
    if (!phoneRegex.test(formData.phone)) {
        showMessage('error', '올바른 전화번호 형식을 입력해주세요. (010-0000-0000)');
        document.getElementById('counselorPhone').focus();
        return;
    }
    
    // 실제로는 서버에 데이터를 전송
    console.log('등록할 데이터:', formData);
    
    // 성공 메시지 표시
    showMessage('success');
    
    // 폼 초기화
    document.getElementById('counselorForm').reset();
    
    // 5초 후 목록 페이지로 이동 확인
    setTimeout(() => {
        if (confirm('상담사 목록 페이지로 이동하시겠습니까?')) {
            window.location.href = 'counselor_list.html';
        }
    }, 2000);
});

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
        window.location.href = 'counselor_list.html';
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