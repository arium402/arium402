let severToday;
let tomorrow;
let currentDate = new Date();
let currentWeek = 0;
const studentColors = {};

// 예시 상담 데이터 - 실제 데이터로 교체해야 합니다
const appointments = {
    "2025-06-16": {
        9: { name: "김학생", studentNo: "2021001", studentMajor: "컴퓨터과학과", studentTelNo: "010-1234-5678", consultationCategory: "학업상담", consultationWay: "대면상담" },
        14: { name: "이학생", studentNo: "2021002", studentMajor: "경영학과", studentTelNo: "010-2345-6789", consultationCategory: "진로상담", consultationWay: "온라인상담" },
        16: { name: "최학생", studentNo: "2021004", studentMajor: "영문학과", studentTelNo: "010-4567-8901", consultationCategory: "심리상담", consultationWay: "대면상담" }
    },
    "2025-06-17": {
        10: { name: "박학생", studentNo: "2021003", studentMajor: "심리학과", studentTelNo: "010-3456-7890", consultationCategory: "심리상담", consultationWay: "대면상담" },
        15: { name: "정학생", studentNo: "2021005", studentMajor: "수학과", studentTelNo: "010-5678-9012", consultationCategory: "학업상담", consultationWay: "온라인상담" }
    },
    "2025-06-18": {
        11: { name: "송학생", studentNo: "2021006", studentMajor: "물리학과", studentTelNo: "010-6789-0123", consultationCategory: "진로상담", consultationWay: "대면상담" }
    }
};

function getInitialWeek() {
    const today = new Date();
    const firstDayOfCurrentMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const dayOfWeek = firstDayOfCurrentMonth.getDay();
    const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    const firstMondayOfMonth = new Date(firstDayOfCurrentMonth);
    firstMondayOfMonth.setDate(firstDayOfCurrentMonth.getDate() + diff);
    
    const daysFromFirstMonday = Math.floor((today - firstMondayOfMonth) / (24 * 60 * 60 * 1000));
    currentWeek = Math.floor(daysFromFirstMonday / 7);
    
    // 현재 주로 설정
    currentDate = new Date(today);
}

function updateCalendar() {
    const firstDayOfWeek = getFirstDayOfWeek(currentDate, currentWeek);
    const dayHeaders = document.querySelector('.calendar-table thead tr');
    const appointmentsBody = document.getElementById('appointments');
    const weekTitle = document.getElementById('week-title');

    if (!dayHeaders || !appointmentsBody || !weekTitle) {
        console.error('Required elements not found');
        return;
    }

    dayHeaders.innerHTML = '<th class="time-column">시간</th>';
    appointmentsBody.innerHTML = '';

    const daysOfWeek = ['일', '월', '화', '수', '목', '금', '토'];
    const today = new Date().toISOString().split('T')[0];
    let dateArray = [];

    // 날짜 헤더 생성 (월요일부터 금요일까지)
    for (let i = 0; i < 7; i++) {
        const date = new Date(firstDayOfWeek);
        date.setDate(date.getDate() + i);
        const dayOfWeek = date.getDay();
        
        if (dayOfWeek === 0 || dayOfWeek === 6) {
            continue; // 토요일과 일요일은 제외
        }
        
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const dateStr = date.toISOString().split('T')[0];
        
        const isToday = dateStr === today;
        const headerClass = isToday ? 'style="background-color: #e3f2fd; color: #1976d2; font-weight: bold;"' : '';
        
        dayHeaders.innerHTML += `<th ${headerClass}>${month}/${day}(${daysOfWeek[dayOfWeek]})</th>`;
        dateArray.push(dateStr);
    }

    // 시간별 상담 내용 생성 (9시부터 17시까지)
    for (let hour = 9; hour <= 17; hour++) {
        let rowHtml = `<tr><td class="time-column">${hour}:00</td>`;
        
        dateArray.forEach(dateStr => {
            if (hour === 13) {
                // 점심시간
                rowHtml += '<td class="lunch-time"></td>';
            } else if (appointments[dateStr] && appointments[dateStr][hour]) {
                // 상담이 있는 경우
                const appointment = appointments[dateStr][hour];
                const appointmentJson = JSON.stringify(appointment).replace(/"/g, '&quot;');
                const backgroundColor = getStudentColor(appointment.studentNo);
                
                rowHtml += `<td class="appointment" data-date="${dateStr}" data-hour="${hour}" 
                    style="background-color: ${backgroundColor} !important;" 
                    onclick="showEventDetails(${appointmentJson})">
                    <span class="student-name">${appointment.name}</span>
                    <span class="student-no">${appointment.studentNo}</span>
                </td>`;
            } else {
                // 빈 시간
                rowHtml += `<td class="empty" data-date="${dateStr}" data-hour="${hour}" 
                    onclick="addNewAppointment('${dateStr}', ${hour})"></td>`;
            }
        });
        
        rowHtml += '</tr>';
        appointmentsBody.innerHTML += rowHtml;
    }

    // 주 제목 업데이트
    const monthName = firstDayOfWeek.toLocaleDateString('ko-KR', { month: 'long' });
    const weekNumber = getWeekNumber(firstDayOfWeek);
    weekTitle.textContent = `${monthName} ${weekNumber}째 주`;
}

function getStudentColor(studentNo) {
    if (!studentColors[studentNo]) {
        const colors = ['#3498db', '#e74c3c', '#2ecc71', '#f39c12', '#9b59b6', '#1abc9c', '#34495e'];
        studentColors[studentNo] = colors[Object.keys(studentColors).length % colors.length];
    }
    return studentColors[studentNo];
}

function formatDate(dateString) {
    const [year, month, day] = dateString.split('-');
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
}

function getFirstDayOfWeek(date, weekOffset) {
    const firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 1);
    const dayOfWeek = firstDayOfMonth.getDay();
    const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    const firstMonday = new Date(firstDayOfMonth);
    firstMonday.setDate(firstDayOfMonth.getDate() + diff);

    // 주 오프셋을 적용하여 첫 번째 월요일을 계산
    const targetWeek = new Date(firstMonday);
    targetWeek.setDate(firstMonday.getDate() + (weekOffset * 7));

    return targetWeek;
}

function getWeekNumber(date) {
    const firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 1);
    const daysFromStart = Math.floor((date - firstDayOfMonth) / (24 * 60 * 60 * 1000));
    return Math.ceil((daysFromStart + firstDayOfMonth.getDay() + 1) / 7);
}

function moveWeek(offset) {
    currentWeek += offset;
    updateCalendar();
}

function pages(no) {
    if (no == 1) {
        location.href = './monthlyCalendar.html';
    } else {
        location.href = './weeklyCalendar.html';
    }
}

function showEventDetails(appointment) {
    try {
        // 모달에 상담 정보 표시
        document.getElementById('studentName').value = appointment.name || '';
        document.getElementById('studentNo').value = appointment.studentNo || '';
        document.getElementById('studentMajor').value = appointment.studentMajor || '';
        document.getElementById('studentTelNo').value = appointment.studentTelNo || '';
        document.getElementById('consultationCategory').value = appointment.consultationCategory || '';
        document.getElementById('consultationWay').value = appointment.consultationWay || '';
        
        // 상담 일시 설정 (예시)
        document.getElementById('consultationDate').value = '상담 일시 정보';
        
        // 모달 열기 (Bootstrap 5 방식)
        const modalElement = document.getElementById('eventModal');
        if (modalElement && typeof bootstrap !== 'undefined') {
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        } else {
            console.error('Modal element or Bootstrap not found');
        }
    } catch (error) {
        console.error('Error showing event details:', error);
    }
}

function addNewAppointment(dateStr, hour) {
    console.log(`새 상담 추가: ${dateStr} ${hour}:00`);
    // 여기에 새 상담 추가 로직을 구현할 수 있습니다
    alert(`${dateStr} ${hour}:00에 새 상담을 추가하시겠습니까?`);
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing calendar...');
    getInitialWeek();
    updateCalendar();
    
    // 변경하기 버튼 이벤트 리스너
    const changeDateBtn = document.getElementById('changeDate');
    if (changeDateBtn) {
        changeDateBtn.addEventListener('click', function() {
            const newDateDiv = document.getElementById('newDate');
            if (newDateDiv) {
                newDateDiv.style.display = newDateDiv.style.display === 'none' ? 'block' : 'none';
            }
        });
    }
});

// 초기화 (이전 방식과의 호환성을 위해)
getInitialWeek();
updateCalendar();loor((date - firstDayOfMonth) / (24 * 60 * 60 * 1000));
    return Math.ceil((daysFromStart + firstDayOfMonth.getDay() + 1) / 7);
}

function moveWeek(offset) {
    currentWeek += offset;
    updateCalendar();
}

function pages(no) {
    if (no == 1) {
        location.href = './monthlyCalendar.html';
    } else {
        location.href = './weeklyCalendar.html';
    }
}

function showEventDetails(appointment) {
    // 모달에 상담 정보 표시
    document.getElementById('studentName').value = appointment.name;
    document.getElementById('studentNo').value = appointment.studentNo;
    document.getElementById('studentMajor').value = appointment.studentMajor;
    document.getElementById('studentTelNo').value = appointment.studentTelNo;
    document.getElementById('consultationCategory').value = appointment.consultationCategory;
    document.getElementById('consultationWay').value = appointment.consultationWay;
    
    // 모달 열기 (Bootstrap 5 방식)
    const modal = new bootstrap.Modal(document.getElementById('eventModal'));
    modal.show();
}

// 초기화
getInitialWeek();
updateCalendar();