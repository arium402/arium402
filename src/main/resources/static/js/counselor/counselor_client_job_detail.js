// 취업/진로 상담 클라이언트 데이터
const careerClientData = {
    '1': {
        name: '박진호',
        gender: '남',
        birth: '2001-09-28',
        studentId: '202112345',
        department: '경영학과',
        phone: '010-3456-7890',
        email: 'jinho.park@university.ac.kr',
        address: '서울시 종로구 세종로 789',
        applicationType: '취업/진로 상담',
        previousCounseling: '없음',
        currentGrade: '3학년',
        careerStatus: '진로 정하지 못함',
        jobFields: ['정보통신', '공공·기업 고위직'],
        preferredCompanies: ['중견기업', '대기업'],
        preferredLocations: ['서울', '경기'],
        workType: '정규직',
        expectedSalary: '3000~3100',
        applicationReason: '3학년이 되면서 취업에 대한 걱정이 많아졌습니다. 경영학과 전공으로 무엇을 할 수 있는지, 어떤 분야로 진로를 정해야 할지 혼란스럽습니다. 막연하게 대기업에 들어가고 싶다는 생각은 있지만, 구체적으로 어떤 준비를 해야 하는지 모르겠고, 자신의 적성과 능력에 대해서도 확신이 서지 않습니다. 전문적인 진로 상담을 통해 명확한 방향을 찾고 싶어 신청하게 되었습니다.',
        counselingSessions: [
            { date: '2025-06-14', session: 1, status: 'progress', recordWritten: false }
        ]
    }
};

// URL에서 클라이언트 ID 가져오기
function getClientIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id') || '1';
}

// 페이지 초기화 함수
function initializeJobDetailPage() {
    const clientId = getClientIdFromUrl();
    const clientInfo = careerClientData[clientId];

    if (clientInfo) {
        // 학생 정보 업데이트
        updateStudentInfo(clientInfo);
    }
}

// 학생 정보 업데이트 함수
function updateStudentInfo(clientInfo) {
    document.getElementById('studentName').textContent = clientInfo.name;
    document.getElementById('studentGender').textContent = clientInfo.gender;
    document.getElementById('studentBirth').textContent = clientInfo.birth;
    document.getElementById('studentId').textContent = clientInfo.studentId;
    document.getElementById('studentDepartment').textContent = clientInfo.department;
    document.getElementById('studentPhone').textContent = clientInfo.phone;
    document.getElementById('studentEmail').textContent = clientInfo.email;
    document.getElementById('studentAddress').textContent = clientInfo.address;
    document.getElementById('applicationType').textContent = clientInfo.applicationType;
    document.getElementById('previousCounseling').textContent = clientInfo.previousCounseling;
    document.getElementById('applicationReason').textContent = clientInfo.applicationReason;
}

// 목록으로 돌아가기
function goBack() {
    history.back();
}

// 마이페이지로 이동
function goToMyPage() {
    window.open('mypage.html', '_blank');
}

// 상담일지 작성
function writeCounselingRecord() {
    const selectedSession = document.querySelector('input[name="selectedSession"]:checked');
    if (!selectedSession) {
        alert('상담일지를 작성할 세션을 선택해주세요.');
        return;
    }
    alert('상담일지 작성 페이지로 이동합니다.');
    // window.location.href = `/counseling-record?session=${selectedSession.value}`;
}

// 상담 완료
function completeCounseling() {
    if (confirm('정말로 상담을 완료하시겠습니까?')) {
        alert('상담이 완료되었습니다.');
        // 완료 처리 로직
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바는 counselor_sidebar.js에서 처리하므로 여기서는 제거
    // (사이드바 이벤트 핸들러 코드 삭제됨)
    
    initializeJobDetailPage();
});