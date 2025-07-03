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

// 상담사 선택 이벤트 (select 태그용)
function handleCounselorSelection(selectElement) {
	if (selectElement.value === '') return; // 빈 값이면 무시
	
	// 선택된 상담사 정보 수집
	const row = selectElement.closest('tr');
	const timeSlot = row.querySelector('.time-slot').textContent;
	
	const cell = selectElement.closest('td');
	const cellIndex = Array.from(row.children).indexOf(cell) - 1; // 첫 번째는 시간 셀이므로 -1
	const days = ['월', '화', '수', '목', '금'];
	const selectedDay = days[cellIndex];
	
	const selectedCounselorId = selectElement.value;
	const selectedCounselorName = selectElement.options[selectElement.selectedIndex].text;
	
	// 날짜 계산
	const currentStartDate = getCurrentStartDate();
	const selectedDate = new Date(currentStartDate);
	selectedDate.setDate(selectedDate.getDate() + cellIndex);
	
	selectedBooking = {
		date: formatDate(selectedDate),
		day: selectedDay,
		time: timeSlot,
		counselorId: selectedCounselorId,
		counselor: selectedCounselorName
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
	
	// 선택된 select 초기화
	document.querySelectorAll('.counselor-select').forEach(select => {
		select.value = '';
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

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
	const modal = document.getElementById('confirmModal');
	
	if (event.target === modal) {
		closeModal();
	}
}

// 페이지 로드 완료 후 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
	// 모든 select 태그에 이벤트 리스너 추가
	document.querySelectorAll('.counselor-select').forEach(select => {
		select.addEventListener('change', function() {
			handleCounselorSelection(this);
		});
	});
});