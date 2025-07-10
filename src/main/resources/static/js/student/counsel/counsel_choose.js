let selectedBooking = null;

// 주간 변경 함수
function changeWeek(direction) {
	// 현재 날짜 가져오기
	const currentStartDate = getCurrentStartDate();
	
	// 새로운 날짜 계산
	const newDate = new Date(currentStartDate);
	newDate.setDate(newDate.getDate() + (direction * 7));
	
	// 새로운 날짜로 페이지 이동
	const newStartDate = formatDateForUrl(newDate);
	window.location.href = `/student/counsel/add/choose?startDate=${newStartDate}`;
}

// 현재 시작 날짜 가져오기 (HTML에서)
function getCurrentStartDate() {
	const weekDisplay = document.getElementById('week-display').textContent;
	const startDateStr = weekDisplay.split(' ~ ')[0]; // "2025.06.16"
	
	// "2025.06.16" → "2025-06-16" 형태로 변환
	const [year, month, day] = startDateStr.split('.');
	
	return new Date(year, month - 1, day); // month는 0부터 시작
}

// URL용 날짜 포맷팅
function formatDateForUrl(date) {
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	
	return `${year}-${month}-${day}`;
}

// 상담사 선택 이벤트 (드롭다운용)
function handleCounselorSelection(counselor, dayIndex, time) {

	// 날짜 계산
	const currentStartDate = getCurrentStartDate();
	const selectedDate = new Date(currentStartDate);
	selectedDate.setDate(selectedDate.getDate() + dayIndex);
	
	selectedBooking = {
		date: formatDate(selectedDate),
		day: days[dayIndex],
		time: time,
		counselorId: counselor.emplId,
		counselor: counselor.emplName
	};
	
	showConfirmModal();
}

// 날짜 포맷팅 (화면 표시용)
function formatDate(date) {
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	
	return `${year}.${month}.${day}`;
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
	
	// 커스텀 드롭다운 초기화
	document.querySelectorAll('.dropdown-button').forEach(button => {
		button.innerHTML = '<span class="dropdown-placeholder">상담사 선택</span>';
	});
}

// 예약 확인
function confirmBooking() {
	closeModal();

	if (confirm('상담 신청 내역 페이지로 이동하시겠습니까?')) {
		location.href = '/student/counsel/add/addcheck';
	}
	else {
		alert('상담 메인 페이지로 이동합니다.');
	}
}

// 커스텀 드롭다운 생성 함수
function createCustomDropdown(dayIndex, time) {
	// 해당 시간대/요일의 상담사들 필터링
	const availableCounselors = getAvailableCounselors(time, days[dayIndex]);
	
	// 상담사가 없으면 null 반환 (드롭다운 생성 안함)
	if (availableCounselors.length === 0) {
		return null;
	}
	
	const dropdown = document.createElement('div');
	dropdown.className = 'custom-dropdown';
	
	const button = document.createElement('button');
	button.className = 'dropdown-button';
	button.innerHTML = '<span class="dropdown-placeholder">상담사 선택</span>';
	
	const list = document.createElement('div');
	list.className = 'dropdown-list';

	availableCounselors.forEach(counselor => {
		const item = document.createElement('div');
		item.className = 'dropdown-item';
		item.textContent = counselor.emplName;
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

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
	const modal = document.getElementById('confirmModal');
	
	if (event.target === modal) {
		closeModal();
	}
}

// 상담사 선택
function selectCounselor(button, counselor, dayIndex, time) {
	button.innerHTML = counselor.emplName;
	button.classList.remove('open');
	
	handleCounselorSelection(counselor, dayIndex, time);
}

// 동적으로 시간 슬롯 테이블 생성 (커스텀 드롭다운 사용)
function createTimeSlotTable() {
	const tbody = document.getElementById('timetable-body');
	if (!tbody) return;
	
	tbody.innerHTML = ''; // 기존 내용 클리어
	
	// 서버에서 받은 timeSlots 그대로 사용 (공백 유지)
	timeSlots.forEach((timeSlot, index) => {
		const row = document.createElement('tr');
		
		// 시간 셀 생성
		const timeCell = document.createElement('td');
		timeCell.className = 'time-slot';
		timeCell.textContent = timeSlot;  // "9:00 ~ 10:00" 그대로
		row.appendChild(timeCell);
		
		// 서버에서 받은 days 사용
		days.forEach((day, dayIndex) => {
			const cell = document.createElement('td');
			cell.className = 'counselor-cell';
			
			const dropdown = createCustomDropdown(dayIndex, timeSlot);
			
			// 드롭다운이 있을 때만 추가
			if (dropdown) {
				cell.appendChild(dropdown);
			}
			
			row.appendChild(cell);
		});
		
		tbody.appendChild(row);
	});
}

// 서버 데이터와 매칭: 해당 시간대/요일의 상담사 필터링 함수
function getAvailableCounselors(serverTimeSlot, day) {
	if (!counselors) return [];
	
	return counselors.filter(counselor => {
		// 해당 요일에 근무하는 상담사인지 확인
		if (counselor.code != day) return false;
		
		// 서버 데이터끼리 직접 비교
		const counselorTimeSlot = counselor.startTime + ' ~ ' + counselor.endTime;
		if (counselorTimeSlot != serverTimeSlot) return false;
		
		// 예약 가능한 상담사인지 확인 (AVAILABLE 상태)
		return counselor.status == 'AVAILABLE';
	});
}

// 전역 클릭 이벤트로 드롭다운 닫기
document.addEventListener('click', () => {
	closeAllDropdowns();
});

// 요일 헤더 동적 생성 함수 추가
function createTableHeaders() {
	const headerRow = document.getElementById('table-header');
	if (!headerRow) return;
	
	// 기존 요일 헤더들 제거 (시간/요일 헤더는 유지)
	const diagonalHeader = headerRow.querySelector('.diagonal-header');
	headerRow.innerHTML = '';
	headerRow.appendChild(diagonalHeader);
	
	// 서버 데이터의 요일들로 헤더 생성
	days.forEach(day => {
		const th = document.createElement('th');
		th.textContent = day;
		headerRow.appendChild(th);
	});
}

// 페이지 로드 완료 후 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
	createTableHeaders();
	createTimeSlotTable();
});