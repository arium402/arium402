const calendarBody = document.getElementById('calendar-body');
const calendarTitle = document.getElementById('calendar-title');
let currentDate = new Date(2025, 5, 1); // 2025년 6월

function getMonthDays(year, month) {
	return new Date(year, month + 1, 0).getDate();
}

function getFirstDay(year, month) {
	return new Date(year, month, 1).getDay();
}

function renderMonthly() {
	calendarBody.innerHTML = '';
	const year = currentDate.getFullYear();
	const month = currentDate.getMonth();
	calendarTitle.textContent = `${year}년 ${month + 1}월`;

	const days = getMonthDays(year, month);
	const firstDay = getFirstDay(year, month);

	let html = '';
	let day = 1 - firstDay;
	for (let i = 0; i < 6; i++) {
		html += '<tr>';
		for (let j = 0; j < 7; j++, day++) {
			if (day < 1 || day > days) {
				html += '<td></td>';
			} else {
				html += `<td data-date="${year}-${String(month+1).padStart(2,'0')}-${String(day).padStart(2,'0')}">${day}<br>
				<div class="event-box" style="background-color: skyblue; cursor:pointer;" data-bs-toggle="modal" data-bs-target="#eventModal">
				<span>10:00</span><span> 홍길동</span>
				</div></td>`;
			}
		}
		html += '</tr>';
		if (day > days) break;
	}
	calendarBody.innerHTML = html;
}

document.getElementById('prev-month').onclick = function() {
	currentDate.setMonth(currentDate.getMonth() - 1);
	renderMonthly();
};
document.getElementById('next-month').onclick = function() {
	currentDate.setMonth(currentDate.getMonth() + 1);
	renderMonthly();
};
renderMonthly();

function pages(no){
	if(no == 1){
		location.href = './monthlyCalendar.html';
	}
	else{
		location.href = './weeklyCalendar.html';
	}
}