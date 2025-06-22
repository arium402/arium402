document.addEventListener('DOMContentLoaded', function () {
    const calendarDays = document.getElementById('calendarDays');
    const monthYearDisplay = document.getElementById('monthYear');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const searchBtn = document.getElementById('searchBtn');
    const counselorNameInput = document.getElementById('counselorName');
    const resultMessage = document.getElementById('resultMessage');

    let currentDate = new Date();
    let currentEvents = {}; // 상담 일정 데이터를 저장하는 객체
    


    // 캘린더를 렌더링하는 함수
    function renderCalendar(date) {
        const year = date.getFullYear();
        const month = date.getMonth();
        const firstDay = new Date(year, month, 1).getDay();
        const lastDate = new Date(year, month + 1, 0).getDate();

        calendarDays.innerHTML = ''; // 캘린더 초기화
        monthYearDisplay.textContent = `${year}년 ${month + 1}월`;

        // 이전 달의 빈 칸 채우기
        for (let i = 0; i < firstDay; i++) {
            const emptyCell = document.createElement('td');
            calendarDays.appendChild(emptyCell);
        }

        // 현재 월의 날짜 그리기
        for (let day = 1; day <= lastDate; day++) {
            const dayCell = document.createElement('td');
            dayCell.textContent = day;
            if(day == 3 || day == 6 || day == 13 || day == 23) {   // 특정 날짜에 상담 일정이 있다고 가정
            dayCell.innerHTML += `<br><div style="display:inline-block;padding:2px 8px;background-color:#007bff;color:#fff;border-radius:12px;font-size:12px;">홍길동</div>`; // 상담사 이름 표시 (파란색 버튼 스타일)
            }
            calendarDays.appendChild(dayCell);
            if ((firstDay + day) % 7 === 0) {
                const row = document.createElement('tr');
                calendarDays.appendChild(row);
            }
        }
    }

    // 이전 달로 이동
    prevMonthBtn.addEventListener('click', function () {
        currentDate.setMonth(currentDate.getMonth() - 1);
        currentEvents = {}; // 이전 일정을 초기화
        renderCalendar(currentDate);
        loadSchedules();
    });

    // 다음 달로 이동
    nextMonthBtn.addEventListener('click', function () {
        currentDate.setMonth(currentDate.getMonth() + 1);
        currentEvents = {}; // 이전 일정을 초기화
        renderCalendar(currentDate);
        loadSchedules();
    });

    // 처음 페이지가 로드될 때 실행
    renderCalendar(currentDate);
});
