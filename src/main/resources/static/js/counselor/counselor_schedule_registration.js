// 내 일정 등록 페이지 JavaScript

// 상담 시간대 체크박스 상호 배타적 처리
function setupConsultationTimeHandlers() {
    var allTimeCheckbox = document.getElementById('allTime');
    var specificTimeCheckboxes = ['morning', 'lunch', 'afternoon'];
    
    // 전체 시간 체크박스 클릭 시
    allTimeCheckbox.addEventListener('change', function() {
        if (this.checked) {
            // 전체 시간이 체크되면 다른 모든 시간대 체크 해제
            specificTimeCheckboxes.forEach(function(id) {
                document.getElementById(id).checked = false;
            });
        }
    });
    
    // 개별 시간대 체크박스 클릭 시
    specificTimeCheckboxes.forEach(function(id) {
        document.getElementById(id).addEventListener('change', function() {
            if (this.checked) {
                // 개별 시간대가 체크되면 전체 시간 체크 해제
                allTimeCheckbox.checked = false;
            }
        });
    });
}

// 폼 초기화
function resetForm() {
    if (confirm('모든 설정을 초기화하시겠습니까?')) {
        // 기본 정보 초기화
        document.getElementById('scheduleYear').value = '2025';
        document.getElementById('scheduleMonth').value = '6';
        
        // 근무 시간 초기화
        document.getElementById('startTime').value = '09:00';
        document.getElementById('endTime').value = '18:00';
        
        // 근무 요일 초기화 (모두 체크)
        var dayCheckboxes = document.querySelectorAll('.day-checkbox');
        dayCheckboxes.forEach(function(checkbox) {
            checkbox.checked = true;
        });
        
        // 상담 시간 초기화 (전체 시간만 체크)
        document.getElementById('allTime').checked = true;
        document.getElementById('morning').checked = false;
        document.getElementById('lunch').checked = false;
        document.getElementById('afternoon').checked = false;
    }
}

// 취소
function cancelForm() {
    if (confirm('작성 중인 내용을 취소하고 창을 닫으시겠습니까?')) {
        window.close();
    }
}

// 일정 저장
function saveSchedule() {
    var year = document.getElementById('scheduleYear').value;
    var month = document.getElementById('scheduleMonth').value;
    var startTime = document.getElementById('startTime').value;
    var endTime = document.getElementById('endTime').value;
    
    // 근무 요일 확인
    var selectedDays = [];
    var dayCheckboxes = document.querySelectorAll('.day-checkbox:checked');
    dayCheckboxes.forEach(function(checkbox) {
        selectedDays.push(checkbox.value);
    });
    
    if (selectedDays.length === 0) {
        alert('최소 하나의 근무 요일을 선택해주세요.');
        return;
    }
    
    // 상담 시간대 확인
    var selectedConsultationTimes = [];
    var consultationCheckboxes = document.querySelectorAll('.consultation-checkbox:checked');
    consultationCheckboxes.forEach(function(checkbox) {
        selectedConsultationTimes.push(checkbox.value);
    });
    
    if (selectedConsultationTimes.length === 0) {
        alert('상담 가능 시간대를 선택해주세요.');
        return;
    }
    
    // 시간 유효성 검사
    if (startTime >= endTime) {
        alert('종료 시간은 시작 시간보다 늦어야 합니다.');
        return;
    }
    
    // 실제로는 서버에 데이터 전송
    var scheduleData = {
        year: year,
        month: month,
        startTime: startTime,
        endTime: endTime,
        workDays: selectedDays,
        consultationTimes: selectedConsultationTimes
    };
    
    console.log('저장할 데이터:', scheduleData);
    
    alert(year + '년 ' + month + '월 일정이 성공적으로 저장되었습니다!');
    
    // 부모 창의 달력 새로고침
    if (window.opener && window.opener.generateCalendar) {
        window.opener.location.reload();
    }
    
    window.close();
}

// 마이페이지로 이동
function goToMyPage() {
    window.open('mypage.html', '_blank');
}

// 페이지 로드 시 초기화
window.onload = function() {
    setupConsultationTimeHandlers();
    
    // 현재 날짜 기준으로 기본값 설정
    var currentDate = new Date();
    document.getElementById('scheduleYear').value = currentDate.getFullYear();
    document.getElementById('scheduleMonth').value = currentDate.getMonth() + 1;
};

// 사이드바 메뉴 클릭 핸들링
document.addEventListener('DOMContentLoaded', function() {
    var menuLinks = document.querySelectorAll('.sidebar-menu a:not(.main-category)');
    
    menuLinks.forEach(function(link) {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // 모든 메뉴에서 active 클래스 제거
            menuLinks.forEach(function(l) {
                l.classList.remove('active');
            });
            
            // 클릭된 메뉴에 active 클래스 추가
            this.classList.add('active');
        });
    });
});