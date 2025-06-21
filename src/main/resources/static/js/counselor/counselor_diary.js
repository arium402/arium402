// 상담일지 작성 페이지 JavaScript

// URL 파라미터에서 정보 가져오기
function getUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    return {
        clientId: urlParams.get('clientId') || '1',
        sessionDate: urlParams.get('sessionDate') || '',
        sessionNumber: urlParams.get('sessionNumber') || ''
    };
}

// 클라이언트 데이터 (실제로는 서버에서 가져올 데이터)
const clientData = {
    '1': {
        name: '김민수',
        gender: '남',
        birth: '2002-03-15',
        studentId: '202012345',
        department: '컴퓨터공학과',
        phone: '010-1234-5678',
        email: 'minsu.kim@university.ac.kr',
        address: '서울시 강남구 테헤란로 123',
        applicationType: '개인상담',
        previousCounseling: '없음'
    },
    '2': {
        name: '이영희',
        gender: '여',
        birth: '2001-08-22',
        studentId: '202012346',
        department: '경영학과',
        phone: '010-2345-6789',
        email: 'younghee.lee@university.ac.kr',
        address: '서울시 서초구 강남대로 456',
        applicationType: '집단상담',
        previousCounseling: '있음 (2024년 1회)'
    },
    '3': {
        name: '박철수',
        gender: '남',
        birth: '2003-01-10',
        studentId: '202012347',
        department: '심리학과',
        phone: '010-3456-7890',
        email: 'chulsoo.park@university.ac.kr',
        address: '서울시 마포구 월드컵로 789',
        applicationType: '개인상담',
        previousCounseling: '없음'
    }
};

// 페이지 데이터 로드
function loadPageData() {
    const params = getUrlParams();
    const client = clientData[params.clientId];
    
    if (client) {
        // 학생 정보 로드
        document.getElementById('studentName').textContent = client.name;
        document.getElementById('studentGender').textContent = client.gender;
        document.getElementById('studentBirth').textContent = client.birth;
        document.getElementById('studentId').textContent = client.studentId;
        document.getElementById('studentDepartment').textContent = client.department;
        document.getElementById('studentPhone').textContent = client.phone;
        document.getElementById('studentEmail').textContent = client.email;
        document.getElementById('studentAddress').textContent = client.address;
        
        // 상담 세션 정보 로드
        document.getElementById('sessionDate').textContent = params.sessionDate;
        document.getElementById('sessionNumber').textContent = params.sessionNumber + '회차';
        document.getElementById('counselingType').textContent = client.applicationType;
    }
}

// 필드 유효성 검사
function validateField(fieldId, errorId) {
    const field = document.getElementById(fieldId);
    const error = document.getElementById(errorId);
    
    if (!field.value.trim()) {
        field.classList.add('error');
        error.style.display = 'block';
        return false;
    } else {
        field.classList.remove('error');
        error.style.display = 'none';
        return true;
    }
}

// 폼 유효성 검사
function validateForm() {
    const requiredFields = [
        { field: 'counselingGoal', error: 'counselingGoalError' },
        { field: 'counselingProcess', error: 'counselingProcessError' },
        { field: 'clientResponse', error: 'clientResponseError' }
    ];
    
    let isValid = true;
    
    requiredFields.forEach(item => {
        if (!validateField(item.field, item.error)) {
            isValid = false;
        }
    });
    
    return isValid;
}

// 상담일지 저장
function saveRecord() {
    if (!validateForm()) {
        alert('필수 입력 항목을 모두 작성해주세요.');
        return;
    }
    
    // 폼 데이터 수집
    const formData = new FormData(document.getElementById('counselingRecordForm'));
    const recordData = Object.fromEntries(formData);
    const params = getUrlParams();
    
    // 추가 정보 포함
    recordData.clientId = params.clientId;
    recordData.sessionDate = params.sessionDate;
    recordData.sessionNumber = params.sessionNumber;
    recordData.createdDate = new Date().toISOString();
    
    // 실제로는 서버에 저장하는 API 호출
    console.log('저장할 상담일지 데이터:', recordData);
    
    // 저장 성공 시
    alert('상담일지가 성공적으로 등록되었습니다.');
    
    // 부모 창으로 돌아가기
    window.close();
}

// 취소
function cancelRecord() {
    if (confirm('작성 중인 내용이 있습니다. 정말 취소하시겠습니까?')) {
        window.close();
    }
}

// 뒤로 가기
function goBack() {
    cancelRecord();
}

// 마이페이지 이동
function goToMyPage() {
    window.open('mypage.html', '_blank');
}

// 실시간 유효성 검사
document.addEventListener('DOMContentLoaded', function() {
    loadPageData();
    
    // 필수 필드에 대한 실시간 유효성 검사
    const requiredFields = ['counselingGoal', 'counselingProcess', 'clientResponse'];
    
    requiredFields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        field.addEventListener('blur', function() {
            validateField(fieldId, fieldId + 'Error');
        });
        
        field.addEventListener('input', function() {
            if (field.classList.contains('error') && field.value.trim()) {
                field.classList.remove('error');
                document.getElementById(fieldId + 'Error').style.display = 'none';
            }
        });
    });
});