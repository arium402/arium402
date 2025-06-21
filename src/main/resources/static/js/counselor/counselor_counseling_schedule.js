// 현재 날짜 정보
let currentDate = new Date();
let currentYear = currentDate.getFullYear();
let currentMonth = currentDate.getMonth();
let today = new Date();

// 샘플 데이터 (현충일을 6월 6일로 올바르게 설정)
const scheduleData = {
    '2025-06-02': { counseling: 3},
    '2025-06-03': { counseling: 5},
    '2025-06-04': { counseling: 2},
    '2025-06-05': { counseling: 4},
    '2025-06-06': { counseling: 0, holiday: '현충일' },
    '2025-06-09': { counseling: 6},
    '2025-06-10': { counseling: 3},
    '2025-06-11': { counseling: 4},
    '2025-06-12': { counseling: 2},
    '2025-06-13': { counseling: 5},
    '2025-06-16': { counseling: 7},
    '2025-06-17': { counseling: 3},
    '2025-06-18': { counseling: 4},
    '2025-06-19': { counseling: 2},
    '2025-06-20': { counseling: 6},
    '2025-06-23': { counseling: 0},
    '2025-06-24': { counseling: 0},
    '2025-06-25': { counseling: 4},
    '2025-06-26': { counseling: 3},
    '2025-06-27': { counseling: 5},
    '2025-06-30': { counseling: 4}
};

// 달력 생성 (5주로 제한)
function generateCalendar(year, month) {
    const calendarGrid = document.getElementById('calendarGrid');
    
    // 기존 달력 셀들 제거 (헤더는 유지)
    const existingCells = calendarGrid.querySelectorAll('.calendar-cell');
    existingCells.forEach(cell => cell.remove());

    // 해당 월의 첫 번째 날과 마지막 날
    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);
    
    // 첫 번째 주의 시작 날짜 (일요일부터 시작)
    const startDate = new Date(firstDayOfMonth);
    const dayOfWeek = firstDayOfMonth.getDay(); // 0=일요일, 1=월요일, ..., 6=토요일
    startDate.setDate(startDate.getDate() - dayOfWeek);
    
    // 5주간 표시 (35일)
    for (let i = 0; i < 35; i++) {
        const currentDate = new Date(startDate);
        currentDate.setDate(startDate.getDate() + i);
        
        const dayElement = createDayElement(currentDate, year, month);
        calendarGrid.appendChild(dayElement);
    }

    // 월 표시 업데이트
    document.getElementById('currentMonth').textContent = `${year}년 ${month + 1}월`;
}

// 일자 요소 생성
function createDayElement(date, currentYear, currentMonth) {
    const dayElement = document.createElement('div');
    dayElement.className = 'calendar-cell';
    
    const isCurrentMonth = date.getMonth() === currentMonth;
    const isToday = date.toDateString() === today.toDateString();
    
    // 날짜를 YYYY-MM-DD 형식으로 변환 (시간대 문제 방지)
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const dateString = `${year}-${month}-${day}`;
    
    if (!isCurrentMonth) {
        dayElement.classList.add('other-month');
    }
    
    if (isToday) {
        dayElement.classList.add('today');
    }

    // 날짜 번호
    const dateNumber = document.createElement('div');
    dateNumber.className = 'date-number';
    dateNumber.textContent = date.getDate();
    dayElement.appendChild(dateNumber);

    // 스케줄 정보
    const scheduleInfo = document.createElement('div');
    scheduleInfo.className = 'schedule-info';
    
    const schedule = scheduleData[dateString];
    if (schedule && isCurrentMonth) {
        if (schedule.holiday) {
            dayElement.classList.add('holiday');
            const holidayMark = document.createElement('div');
            holidayMark.className = 'holiday-mark';
            holidayMark.textContent = schedule.holiday;
            scheduleInfo.appendChild(holidayMark);
        } else if (schedule.counseling > 0) {
            const counselingCount = document.createElement('div');
            counselingCount.className = 'counseling-count';
            counselingCount.textContent = `상담 ${schedule.counseling}건`;
            counselingCount.addEventListener('click', function() {
                // 상담 일정 확인 페이지로 이동 (해당 날짜 정보와 함께)
                window.open(`counseling_schedule.html?date=${dateString}`, '_blank');
            });
            scheduleInfo.appendChild(counselingCount);
        }
    }
    
    dayElement.appendChild(scheduleInfo);
    return dayElement;
}

// 이전 달
function previousMonth() {
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    generateCalendar(currentYear, currentMonth);
}

// 다음 달
function nextMonth() {
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    generateCalendar(currentYear, currentMonth);
}

// 일정 등록 페이지 열기
function openScheduleRegistration() {
    // 새 창에서 일정 등록 페이지 열기
    window.open('/counselor/schedule_registration', '_blank', 'width=800,height=600');
}

// 페이지 로드 시 달력 생성
window.onload = function() {
    generateCalendar(currentYear, currentMonth);
};

// ❌ 사이드바 메뉴 클릭 핸들링 제거됨 - counselor_sidebar.js에서 처리