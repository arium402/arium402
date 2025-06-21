// 학습 컨설팅 내담자 상세 정보 페이지 JavaScript

// URL에서 클라이언트 ID 가져오기
function getClientIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id') || '1';
}

// 학습 컨설팅 클라이언트 데이터 (실제로는 서버에서 가져올 데이터)
const learningClientData = {
    '1': {
        name: '최수영',
        gender: '여',
        birth: '2003-05-12',
        studentId: '202312345',
        department: '심리학과',
        phone: '010-4567-8901',
        email: 'suyoung.choi@university.ac.kr',
        address: '서울시 마포구 홍대로 456',
        applicationType: '학습 컨설팅',
        previousCounseling: '없음',
        currentGrade: '2학년',
        applicationPurposes: ['대학공부 전략(노트필기, 예복습, 과제 등)', '시험 및 성적관리', '전공학습'],
        subjectCount: '5~6개',
        studyTime: '1~3시간',
        achievement: '40~60%',
        studyMethods: ['암기', '요약 정리'],
        distractions: ['스마트폰', '친구'],
        studyPlaces: ['기숙사', '도서관'],
        applicationReason: '2학년이 되면서 전공 수업의 난이도가 높아져 공부 방법에 대한 고민이 많습니다. 고등학교 때의 암기 위주 공부법으로는 대학 수업을 따라가기 어려워서 효과적인 학습 전략을 배우고 싶습니다. 특히 심리학과 전공 과목들은 이론과 실습이 함께 있어서 어떻게 공부해야 할지 막막합니다. 성적 관리도 체계적으로 하고 싶고, 시험 준비를 더 효율적으로 하는 방법을 알고 싶어서 학습 컨설팅을 신청하게 되었습니다.',
        counselingSessions: [
            { date: '2025-06-15', session: 1, status: 'progress', recordWritten: false }
        ]
    },
    '2': {
        name: '박지민',
        gender: '남',
        birth: '2002-09-18',
        studentId: '202212346',
        department: '경영학과',
        phone: '010-5678-9012',
        email: 'jimin.park@university.ac.kr',
        address: '서울시 강서구 화곡로 789',
        applicationType: '학습 컨설팅',
        previousCounseling: '있음 (2024년 1회)',
        currentGrade: '3학년',
        applicationPurposes: ['시간 관리', '자격증 공부', '학습 습관 형성'],
        subjectCount: '3~4개',
        studyTime: '3~6시간',
        achievement: '60~80%',
        studyMethods: ['문제풀이', '스터디'],
        distractions: ['컴퓨터', '만화책'],
        studyPlaces: ['스터디 카페', '집'],
        applicationReason: '3학년이 되면서 전공 공부와 취업 준비를 병행해야 하는데 시간 관리가 어려워서 도움을 받고 싶습니다.',
        counselingSessions: [
            { date: '2025-06-10', session: 2, status: 'progress', recordWritten: false },
            { date: '2025-06-03', session: 1, status: 'complete', recordWritten: true }
        ]
    }
};

// 목록으로 돌아가기
function goBack() {
    alert("내담자 관리 목록으로 돌아갑니다");
    console.log('목록으로 돌아가기');
    // TODO: 실제 목록 페이지로 이동 로직 구현 필요
    // window.location.href = '/counselor/clients';
}

// 마이페이지로 이동
function goToMyPage() {
    alert("마이페이지로 이동합니다");
    console.log('마이페이지로 이동');
    // TODO: 실제 마이페이지 이동 로직 구현 필요
    // window.open('mypage.html', '_blank');
}

// 상담일지 작성
function writeCounselingRecord() {
    const selectedSession = document.querySelector('input[name="selectedSession"]:checked');
    
    if (!selectedSession) {
        alert('상담일지를 작성할 세션을 선택해주세요.');
        return;
    }
    
    const sessionValue = selectedSession.value;
    alert(`선택한 세션(${sessionValue})의 상담일지 작성 페이지로 이동합니다`);
    console.log('상담일지 작성:', sessionValue);
    // TODO: 실제 상담일지 작성 페이지로 이동 로직 구현 필요
    // window.location.href = `/counseling-record?session=${sessionValue}`;
}

// 상담 완료
function completeCounseling() {
    const clientId = getClientIdFromUrl();
    const clientInfo = learningClientData[clientId];
    
    if (!clientInfo) {
        alert('클라이언트 정보를 찾을 수 없습니다.');
        return;
    }
    
    if (confirm(`정말로 ${clientInfo.name} 학생의 학습 컨설팅을 완료하시겠습니까?`)) {
        alert('상담이 완료되었습니다.');
        console.log('상담 완료:', clientId);
        // TODO: 실제 상담 완료 처리 로직 구현 필요
        // 완료 처리 후 목록으로 이동하거나 상태 업데이트
    }
}

// 페이지 로드 시 데이터 초기화
function initializeStudyClientData() {
    const clientId = getClientIdFromUrl();
    const clientInfo = learningClientData[clientId];
    
    if (clientInfo) {
        // 학생 정보 표시
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
        
        // 설문 조사 데이터 표시 (체크박스, 라디오 버튼 상태 설정)
        updateSurveyData(clientInfo);
        
        console.log('학습 컨설팅 클라이언트 데이터 로드 완료:', clientInfo);
    } else {
        console.warn('해당 클라이언트 데이터를 찾을 수 없습니다:', clientId);
    }
}

// 설문 조사 데이터 업데이트
function updateSurveyData(clientInfo) {
    // 현재 학년 설정
    const gradeRadios = document.querySelectorAll('input[name="grade"]');
    gradeRadios.forEach(radio => {
        if (radio.value === clientInfo.currentGrade) {
            radio.checked = true;
        }
    });
    
    // 수강 과목 수 설정
    const subjectRadios = document.querySelectorAll('input[name="subjects"]');
    subjectRadios.forEach(radio => {
        if (radio.value === clientInfo.subjectCount) {
            radio.checked = true;
        }
    });
    
    // 학습 시간 설정
    const studyTimeRadios = document.querySelectorAll('input[name="studyHours"]');
    studyTimeRadios.forEach(radio => {
        if (radio.value === clientInfo.studyTime) {
            radio.checked = true;
        }
    });
    
    // 학습 성취도 설정
    const achievementRadios = document.querySelectorAll('input[name="achievement"]');
    achievementRadios.forEach(radio => {
        if (radio.value === clientInfo.achievement) {
            radio.checked = true;
        }
    });
}

