// 내담자 상세 정보 페이지 JavaScript

// URL에서 클라이언트 ID 가져오기
function getClientIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id') || '1';
}



// 목록으로 돌아가기
function goBack() {
    alert("내담자 관리 목록으로 돌아갑니다");
    console.log('목록으로 돌아가기');
    // TODO: 실제 목록 페이지로 이동 로직 구현 필요
    // window.location.href = '/counselor/clients';
}

// 다음 상담 예약
function scheduleNextCounseling() {
    alert("다음 상담 예약 페이지로 이동합니다");
    console.log('다음 상담 예약');
    // TODO: 실제 상담 예약 페이지로 이동 로직 구현 필요
}

// 상담일지 작성
function writeCounselingRecord() {
    const selectedSession = document.querySelector('input[name="selectedSession"]:checked');
    
    if (!selectedSession) {
        alert("상담일지를 작성할 세션을 선택해주세요.");
        return;
    }
    
    const sessionValue = selectedSession.value;
    alert(`선택한 세션(${sessionValue})의 상담일지 작성 페이지로 이동합니다`);
    console.log('상담일지 작성:', sessionValue);
    // TODO: 실제 상담일지 작성 페이지로 이동 로직 구현 필요
}

// 마이페이지로 이동
function goToMyPage() {
    alert("마이페이지로 이동합니다");
    console.log('마이페이지로 이동');
    // TODO: 실제 마이페이지 이동 로직 구현 필요
    // window.open('mypage.html', '_blank');
}

// 페이지 로드 시 데이터 초기화
function initializeClientData() {
    const clientId = getClientIdFromUrl();
    const client = clientData[clientId];
    
    if (client) {
        // 학생 정보 표시
        document.getElementById('studentName').textContent = client.name;
        document.getElementById('studentGender').textContent = client.gender;
        document.getElementById('studentBirth').textContent = client.birth;
        document.getElementById('studentId').textContent = client.studentId;
        document.getElementById('studentDepartment').textContent = client.department;
        document.getElementById('studentPhone').textContent = client.phone;
        document.getElementById('studentEmail').textContent = client.email;
        document.getElementById('studentAddress').textContent = client.address;
        document.getElementById('applicationType').textContent = client.applicationType;
        document.getElementById('previousCounseling').textContent = client.previousCounseling;
        document.getElementById('currentConcern').textContent = client.currentConcern;
        
        console.log('클라이언트 데이터 로드 완료:', client);
    } else {
        console.warn('해당 클라이언트 데이터를 찾을 수 없습니다:', clientId);
    }
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    initializeClientData();
    
    // 사이드바 메뉴 클릭 핸들링
    const menuLinks = document.querySelectorAll('.sidebar-menu a:not(.main-category)');
    
    menuLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // 모든 메뉴에서 active 클래스 제거
            menuLinks.forEach(l => l.classList.remove('active'));
            
            // 클릭된 메뉴에 active 클래스 추가
            this.classList.add('active');
        });
    });
});