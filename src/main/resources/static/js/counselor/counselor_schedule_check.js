// 상담 일정 확인 페이지 JavaScript

// 하드코딩된 주간 시작 날짜 (2025-06-15 일요일)
const weekStartDate = new Date('2025-06-15');

// 날짜 포맷팅 함수 (원래 코드와 동일)
function formatDate(date) {
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${month}.${day}`;
}

function formatDateFull(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 실시간 오늘 날짜에 today 클래스 적용 (원래 코드 방식)
function applyTodayStyle() {
    const today = new Date(); // 원래 코드와 동일
    console.log('오늘 날짜:', formatDateFull(today));
    
    // 하드코딩된 주간의 각 날짜와 비교
    for (let dayIndex = 0; dayIndex < 7; dayIndex++) {
        const currentDate = new Date(weekStartDate);
        currentDate.setDate(currentDate.getDate() + dayIndex);
        
        console.log(`${dayIndex}번째 날:`, formatDateFull(currentDate));
        
        // 원래 코드와 동일한 비교 방식
        if (formatDateFull(currentDate) === formatDateFull(today)) {
            console.log('오늘 날짜 발견! dayIndex:', dayIndex);
            
            // 헤더에 today 클래스 적용 (시간 헤더는 0번째이므로 +1)
            const headerRow = document.getElementById('tableHeader');
            if (headerRow && headerRow.children[dayIndex + 1]) {
                headerRow.children[dayIndex + 1].classList.add('today');
                console.log('헤더에 today 클래스 적용:', headerRow.children[dayIndex + 1].textContent);
            }
            
            // 모든 시간대 행의 해당 날짜 셀에 today 클래스 적용
            const tableBody = document.getElementById('scheduleBody');
            if (tableBody) {
                const rows = tableBody.getElementsByTagName('tr');
                for (let row of rows) {
                    if (row.children[dayIndex + 1]) {
                        row.children[dayIndex + 1].classList.add('today');
                    }
                }
                console.log(`총 ${rows.length}개 행의 ${dayIndex + 1}번째 컬럼에 today 클래스 적용`);
            }
            
            break; // 찾았으므로 루프 종료
        }
    }
}

// 내담자 상세 정보로 이동
function goToStudentDetail(studentId, studentName) {
    const confirmView = confirm(`${studentName} 학생의 상세 정보를 확인하시겠습니까?\n\n학번: ${studentId}`);
    
    if (confirmView) {
        console.log(`내담자 상세 정보로 이동: ${studentName} (${studentId})`);
        alert(`${studentName} 학생의 상세 정보 페이지로 이동합니다.`);
        // 실제로는 window.location.href = `/counselor/client/detail/${studentId}` 등으로 이동
    }
}

// 이전 주 (실제 기능은 나중에 구현)
function previousWeek() {
    console.log('이전 주로 이동');
    alert('이전 주 기능은 추후 구현 예정입니다.');
}

// 다음 주 (실제 기능은 나중에 구현)
function nextWeek() {
    console.log('다음 주로 이동');
    alert('다음 주 기능은 추후 구현 예정입니다.');
}

// 초기화 및 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
    // 실시간 오늘 날짜 스타일 적용 (원래 코드 방식)
    applyTodayStyle();
    
    // 이벤트 리스너 등록
    const prevBtn = document.getElementById('prevWeek');
    const nextBtn = document.getElementById('nextWeek');
    
    if (prevBtn) {
        prevBtn.addEventListener('click', previousWeek);
    }
    
    if (nextBtn) {
        nextBtn.addEventListener('click', nextWeek);
    }
    
    console.log('상담 일정 확인 페이지 초기화 완료');
});