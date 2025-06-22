let severToday;
let tomorrow;
let currentDate = new Date();
let currentWeek = 0;
const studentColors = {};
const newDate = document.querySelector("#new_date");
const newTime=document.querySelector("#new_time");

function getInitialWeek() {
	const firstDayOfCurrentMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
	const dayOfWeek = firstDayOfCurrentMonth.getDay();
	const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
	const firstMondayOfMonth = new Date(firstDayOfCurrentMonth);
	firstMondayOfMonth.setDate(firstDayOfCurrentMonth.getDate() + diff);
	const today = new Date();
	const daysFromFirstMonday = Math.floor((today - firstMondayOfMonth) / (24 * 60 * 60 * 1000));
	currentWeek = Math.floor(daysFromFirstMonday / 7);
}

function updateCalendar() {
	const firstDayOfWeek = getFirstDayOfWeek(currentDate, currentWeek);
	const dayHeaders = document.querySelector('.calendar-table thead tr');
	const appointmentsBody = document.getElementById('appointments');
	const weekTitle = document.getElementById('week-title');

	dayHeaders.innerHTML = '<th class="time-column">시간</th>'; // 시간 열

	//시간 일정 출력하는 부분
	appointmentsBody.innerHTML = ``;

	// 시간별 상담 내용 생성
		for (let hour = 9; hour <= 17; hour++) {
			let rowHtml = `<tr><td class="time-column" style="padding-top:10px;">${hour}:00</td>`;
			for (let i = 0; i < 5; i++) {
				const date = new Date(firstDayOfWeek);
				date.setDate(date.getDate() + i);
				const dayOfWeek = date.getDay();
				if (dayOfWeek === 0 || dayOfWeek === 6) {
					rowHtml += '<td></td>'; // 토요일과 일요일은 빈 칸
					continue;
				}
				const month = date.getMonth() + 1; // 월 (0부터 시작하므로 +1)
				const day = date.getDate(); // 일
				const dateStr = date.toISOString().split('T')[0]; // YYYY-MM-DD 형식
			
				// 상담 내용이 있는 경우
				if (appointments[dateStr] && appointments[dateStr][hour]) {
					const appointment = appointments[dateStr][hour];
					rowHtml += `<td class="appointment" data-date="${dateStr}" data-hour="${hour}" style="background-color: ${studentColors[appointment.studentNo] || 'skyblue'};" onclick="showEventDetails(${JSON.stringify(appointment)})">
						<span>${appointment.name}</span>
						<span>${appointment.studentNo}</span>
					</td>`;
				} else {
					rowHtml += `<td class="empty" data-date="${dateStr}" data-hour="${hour}"></td>`;
				}
			}
			rowHtml += '</tr>';
			appointmentsBody.innerHTML += rowHtml;
	}
	
	const daysOfWeek = ['일', '월', '화', '수', '목', '금', '토'];
	const today = new Date().toISOString().split('T')[0]; // 오늘 날짜를 YYYY-MM-DD 형식으로
	let todayIndex = null;

	let dateArray = [];
	// 날짜 헤더 생성
	for (let i = 0; i < 7; i++) {
		const date = new Date(firstDayOfWeek);
		date.setDate(date.getDate() + i);
		const dayOfWeek = date.getDay();
		if (dayOfWeek === 0 || dayOfWeek === 6) {
			continue; // 토요일과 일요일은 제외
		}
		const month = date.getMonth() + 1; // 월 (0부터 시작하므로 +1)
		const day = date.getDate(); // 일
		const dateStr = date.toISOString().split('T')[0]; // YYYY-MM-DD 형식

		
		if (dateStr === today) {
			todayIndex = i; // 오늘 날짜의 인덱스를 저장
		}
		dayHeaders.innerHTML += `<th>${month}/${day}(${daysOfWeek[dayOfWeek]})</th>`;
		dateArray.push(date.getFullYear() + "-" + month + "-" + day);
	}
	
	let startdate = formatDate(dateArray[0]);
	let enddate = formatDate(dateArray[dateArray.length - 1]);
	
	
	const monthName = firstDayOfWeek.toLocaleString('default', { month: 'long' });
	const weekNumber = getWeekNumber(firstDayOfWeek);
	weekTitle.textContent = `${monthName} ${weekNumber}째 주`;
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
	const firstDayOfWeek = new Date(firstMonday.setDate(firstMonday.getDate() + (weekOffset * 7)));

	// UTC+9로 변환 (한국 표준시 예시)
	const utcOffset = firstDayOfWeek.getTimezoneOffset() * 60000; // 분 단위를 밀리초로 변환
	return new Date(firstDayOfWeek.getTime() + utcOffset); // 로컬 시간대에 맞춰 조정
}

function getWeekNumber(date) {
	const firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 1);
	const dayOfWeek = firstDayOfMonth.getDay();
	const daysFromStart = Math.floor((date - firstDayOfMonth) / (24 * 60 * 60 * 1000));
	return Math.ceil((daysFromStart + firstDayOfMonth.getDay() + 1) / 7);
}

function moveWeek(offset) {
	currentWeek += offset;
	updateCalendar();
}



getInitialWeek();
updateCalendar();

function pages(no){
	if(no == 1){
		location.href = './monthlyCalendar.html';
	}
	else{
		location.href = './weeklyCalendar.html';
	}
}