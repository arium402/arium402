let currentWeekStart = new Date(2025, 5, 16); // 2025년 6월 16일 (월요일)
let selectedBooking = null;

// 상담사 목록
const counselors = [
	'김상담 선생님',
	'박상담 선생님',
	'이상담 선생님',
	'정상담 선생님',
	'최상담 선생님'
];

// 주간 변경 함수
function changeWeek(direction) {
	currentWeekStart.setDate(currentWeekStart.getDate() + (direction * 7));
	updateWeekDisplay();
	generateTimetable();
}

// 주간 표시 업데이트
function updateWeekDisplay() {
	const weekEnd = new Date(currentWeekStart);
	weekEnd.setDate(weekEnd.getDate() + 4);
	
	const startStr = formatDate(currentWeekStart);
	const endStr = formatDate(weekEnd);
	
	document.getElementById('week-display').textContent = `${startStr} ~ ${endStr}`;
}

// 날짜 포맷팅
function formatDate(date) {
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	return `${year}.${month}.${day}`;
}

// 커스텀 드롭다운 생성
function createCustomDropdown(dayIndex, time) {
	const dropdown = document.createElement('div');
	dropdown.className = 'custom-dropdown';
	
	const button = document.createElement('button');
	button.className = 'dropdown-button';
	button.innerHTML = '<span class="dropdown-placeholder">상담사 선택</span>';
	
	const list = document.createElement('div');
	list.className = 'dropdown-list';
	
	counselors.forEach(counselor => {
		const item = document.createElement('div');
		item.className = 'dropdown-item';
		item.textContent = counselor;
		item.addEventListener('click', () => {
			selectCounselor(button, counselor, dayIndex, time);
			closeAllDropdowns();
		});
		list.appendChild(item);
	});
    
	button.addEventListener('click', (e) => {
		e.stopPropagation();
		closeAllDropdowns();
		toggleDropdown(button, list);
	});

	dropdown.appendChild(button);
	dropdown.appendChild(list);

	return dropdown;
}

// 드롭다운 토글
function toggleDropdown(button, list) {
	const isOpen = list.classList.contains('show');
	if (!isOpen) {
		button.classList.add('open');
		list.classList.add('show');
	}
}

// 모든 드롭다운 닫기
function closeAllDropdowns() {
	document.querySelectorAll('.dropdown-list').forEach(list => {
		list.classList.remove('show');
	});
	document.querySelectorAll('.dropdown-button').forEach(button => {
		button.classList.remove('open');
	});
}

// 상담사 선택
function selectCounselor(button, counselor, dayIndex, time) {
	button.innerHTML = counselor;
	button.classList.remove('open');
	
	const currentDate = new Date(currentWeekStart);
	currentDate.setDate(currentDate.getDate() + dayIndex);
	
	const days = ['월', '화', '수', '목', '금'];
	
	selectedBooking = {
		date: formatDate(currentDate),
		day: days[dayIndex],
		time: time,
		counselor: counselor
	};
	
	showConfirmModal();
}

// 타임테이블 생성
function generateTimetable() {
	const tbody = document.getElementById('timetable-body');
	tbody.innerHTML = '';

	const times = [
		'9:00~10:00',
		'10:00~11:00',
		'11:00~12:00',
		'12:00~13:00',
		'13:00~14:00',
		'14:00~15:00',
		'15:00~16:00'
	];
	
	const days = ['월', '화', '수', '목', '금'];
	
	times.forEach(time => {
		const row = document.createElement('tr');
		
		// 시간 셀
		const timeCell = document.createElement('td');
		timeCell.className = 'time-slot';
		timeCell.textContent = time;
		row.appendChild(timeCell);
		
		// 요일별 셀
		days.forEach((day, dayIndex) => {
			const cell = document.createElement('td');
			cell.className = 'counselor-cell';
			
			const dropdown = createCustomDropdown(dayIndex, time);
			cell.appendChild(dropdown);
			row.appendChild(cell);
		});
		
		tbody.appendChild(row);
	});
}

// 확인 모달 표시
function showConfirmModal() {
	const modal = document.getElementById('confirmModal');
	const message = document.getElementById('modal-message');
	
	message.innerHTML = `
		상담 신청 내용을 확인해주세요.<br><br>
		• 일자: ${selectedBooking.date} (${selectedBooking.day})<br>
		• 시간: ${selectedBooking.time}<br>
		• 상담사: ${selectedBooking.counselor}<br><br>
		이 일정으로 상담을 신청하시겠습니까?
	`;
	
	modal.style.display = 'block';
}

// 모달 닫기
function closeModal() {
	document.getElementById('confirmModal').style.display = 'none';
	// 선택된 드롭다운 초기화
	document.querySelectorAll('.dropdown-button').forEach(button => {
		button.innerHTML = '<span class="dropdown-placeholder">상담사 선택</span>';
	});
}

// 예약 확인
function confirmBooking() {
	closeModal();
	
	if (confirm('상담 신청 내역 페이지로 이동하시겠습니까?')) {
	    location.href = /*[[@{/student/counsel/add/addcheck}]]*/ '/student/counsel/add/addcheck';
	}
	else {
		// 상담 메인 페이지로 이동
		alert('상담 메인 페이지로 이동합니다.');
	}
}

// 전역 클릭 이벤트로 드롭다운 닫기
document.addEventListener('click', () => {
	closeAllDropdowns();
});

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
	const modal = document.getElementById('confirmModal');
	if (event.target === modal) {
		closeModal();
	}
}

// 초기 로드
document.addEventListener('DOMContentLoaded', function() {
	updateWeekDisplay();
	generateTimetable();
});