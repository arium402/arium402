// 페이지 이동 함수
function goToPage(url) {
	window.location.href = url;
}

// 마일리지 내역보기 함수
function showMileageHistory() {
	goToPage('/student/mileage/history');
}

// 상세 정보 보기 함수 (상태별 필터링)
function showDetail(type, status) {
	if (type === 'noncurricular') {
		// 비교과 프로그램 상태별 페이지로 이동
		goToPage(`/student/noncurricular/mylist?status=${status}`);
	}
	else if (type === 'counseling') {
		// 상담 상태별 페이지로 이동
		goToPage(`/student/counseling/mylist?status=${status}`);
	}
}

// 모달 관련 함수들
function openEditModal() {
	const modal = document.getElementById('editModal');
	modal.classList.add('show');
	document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
}

function closeEditModal() {
	const modal = document.getElementById('editModal');
	modal.classList.remove('show');
	document.body.style.overflow = 'auto'; // 배경 스크롤 복원
}

// 모달 배경 클릭 시 닫기
document.getElementById('editModal').addEventListener('click', function(e) {
	if (e.target === this) {
		closeEditModal();
	}
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
	if (e.key === 'Escape') {
		closeEditModal();
	}
});

// 주소 검색 함수 (실제로는 다음 주소 API 등을 사용)
function searchAddress() {
	// 실제 구현에서는 다음 주소 검색 API나 기타 주소 검색 서비스를 사용
	alert('주소 검색 기능을 구현해주세요.\n(다음 주소 API 등을 활용)');
	
	// 예시 데이터
	document.getElementById('postalCode').value = '06234';
	document.getElementById('address').value = '서울특별시 강남구 테헤란로 123';
}

// 학생 정보 저장 함수
function saveStudentInfo() {
	// 폼 데이터 수집
	const formData = {
		admissionDate: document.getElementById('admissionDate').value,
		studentStatus: document.getElementById('studentStatus').value,
		studentCode: document.getElementById('studentCode').value,
		postalCode: document.getElementById('postalCode').value,
		address: document.getElementById('address').value,
		detailAddress: document.getElementById('detailAddress').value,
		phoneNumber: document.getElementById('phoneNumber').value,
		email: document.getElementById('email').value
	};

	// 유효성 검사
	if (!formData.email || !formData.phoneNumber) {
		alert('필수 항목을 모두 입력해주세요.');
		return;
	}

	// 이메일 형식 검사
	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	if (!emailRegex.test(formData.email)) {
		alert('올바른 이메일 형식을 입력해주세요.');
		return;
	}
	
	// 전화번호 형식 검사
	const phoneRegex = /^010-\d{4}-\d{4}$/;
	if (!phoneRegex.test(formData.phoneNumber)) {
		alert('올바른 전화번호 형식을 입력해주세요. (010-0000-0000)');
		return;
	}
	
	// 실제로는 서버에 데이터를 전송
	console.log('저장할 데이터:', formData);
	
	// 저장 성공 알림
	alert('학생 정보가 성공적으로 저장되었습니다.');
	closeEditModal();
	
	// 페이지 새로고침 또는 데이터 업데이트
	// location.reload(); // 실제 구현에서는 필요에 따라 사용
}

// 모든 알림을 읽음으로 표시
function markAllAsRead() {
	const alertItems = document.querySelectorAll('.alert-item');
	alertItems.forEach(item => {
		item.classList.add('read');
	});
	alert('모든 알림이 읽음으로 표시되었습니다.');
}

// 알림 설정 팝업 열기
function openNotificationSettings() {
	const popup = document.getElementById('notificationSettingsPopup');
	popup.classList.add('show');
	document.body.style.overflow = 'hidden';
}

// 알림 설정 팝업 닫기
function closeNotificationSettings() {
	const popup = document.getElementById('notificationSettingsPopup');
	popup.classList.remove('show');
	document.body.style.overflow = 'auto';
}

// 알림 설정 저장
function saveNotificationSettings() {
	const settings = {
		urgent: document.getElementById('urgentNotifications').checked,
		program: document.getElementById('programNotifications').checked,
		counseling: document.getElementById('counselingNotifications').checked,
		grade: document.getElementById('gradeNotifications').checked,
		event: document.getElementById('eventNotifications').checked,
		system: document.getElementById('systemNotifications').checked
	};
	
	console.log('알림 설정 저장:', settings);
	alert('알림 설정이 저장되었습니다.');
	closeNotificationSettings();
}

// 모든 알림 보기 팝업 열기
function showAllAlerts() {
	const allAlerts = [
		{
		    type: 'urgent',
		    title: '만족도 조사 기간이 3일 남았습니다.',
		    description: '만족도 조사를 진행하세요.',
		    time: '방금 전',
		    link: '/student/diagnosis/satisfaction/125'
		},
		{
		    type: 'success',
		    title: 'AI 프로그래밍 워크샵이 이수 처리되었습니다.',
		    description: '웹 개발 기초 과정이 이수 완료되었습니다.',
		    time: '2시간 전',
		    link: '/student/noncurricular/detail/123'
		},
		{
		    type: 'info',
		    title: '창업 아이디어 경진대회가 승인 처리되었습니다.',
		    description: '비교과 프로그램 신청이 완료되었습니다.',
		    time: '1일 전',
		    link: '/student/noncurricular/detail/124'
		},
		{
		    type: 'info',
		    title: '진로 상담 신청이 접수되었습니다.',
		    description: '상담 일정을 확인해주세요.',
		    time: '2일 전',
		    link: '/student/counseling/detail/126'
		},
		{
		    type: 'urgent',
		    title: '핵심역량 진단 기간이 5일 남았습니다.',
		    description: '핵심역량 진단을 완료해주세요.',
		    time: '3일 전',
		    link: '/student/diagnosis/competency'
		},
		{
		    type: 'success',
		    title: '웹 개발 기초 과정이 이수 처리되었습니다.',
		    description: '비교과 프로그램이 성공적으로 완료되었습니다.',
		    time: '4일 전',
		    link: '/student/noncurricular/detail/127'
		},
		{
		    type: 'info',
		    title: '학습 컨설팅 신청이 승인되었습니다.',
		    description: '예정된 일정을 확인하세요.',
		    time: '5일 전',
		    link: '/student/career/consulting'
		},
		{
		    type: 'success',
		    title: '심리 상담이 완료되었습니다.',
		    description: '상담 결과 보고서를 확인하세요.',
		    time: '1주 전',
		    link: '/student/counseling/psychology'
		}
	];

	let content = '';

	allAlerts.forEach(alert => {
		const badgeClass = alert.type;
		const badgeText = alert.type === 'urgent' ? '긴급' : alert.type === 'success' ? '완료' : '신청';

		content += `
			<div style="display: flex; align-items: flex-start; gap: 12px; padding: 15px; margin-bottom: 12px; background: white; border-radius: 8px; border: 1px solid #e9ecef; cursor: pointer; transition: all 0.2s ease;" onclick="goToPageAndClosePopup('${alert.link}')" onmouseover="this.style.boxShadow='0 2px 8px rgba(0,0,0,0.1)'; this.style.transform='translateY(-1px)'" onmouseout="this.style.boxShadow='none'; this.style.transform='none'">
				<div style="padding: 4px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 600; white-space: nowrap; min-width: 50px; text-align: center; background: ${badgeClass === 'urgent' ? '#f8d7da' : badgeClass === 'success' ? '#d4edda' : '#d1ecf1'}; color: ${badgeClass === 'urgent' ? '#721c24' : badgeClass === 'success' ? '#155724' : '#0c5460'};">
					${badgeText}
				</div>
				<div style="flex: 1; font-size: 0.9rem; line-height: 1.4;">
					<strong style="color: #495057; font-weight: 600;">${alert.title}</strong><br>
					${alert.description}
				</div>
				<div style="font-size: 0.8rem; color: #6c757d; white-space: nowrap;">
					${alert.time}
				</div>
			</div>
		`;
	});

	document.getElementById('allAlertsContent').innerHTML = content;
	document.getElementById('allAlertsPopup').classList.add('show');
	document.body.style.overflow = 'hidden';
}

// 모든 알림 보기 팝업 닫기
function closeAllAlertsPopup() {
	const popup = document.getElementById('allAlertsPopup');
	popup.classList.remove('show');
	document.body.style.overflow = 'auto';
}

// 페이지 이동 후 팝업 닫기
function goToPageAndClosePopup(url) {
	closeAllAlertsPopup();
	setTimeout(() => {
		goToPage(url);
	}, 300);
}

// 팝업 외부 클릭 시 닫기
document.addEventListener('click', function(e) {
	const settingsPopup = document.getElementById('notificationSettingsPopup');
	const alertsPopup = document.getElementById('allAlertsPopup');
	
	if (e.target === settingsPopup) {
		closeNotificationSettings();
	}
	if (e.target === alertsPopup) {
		closeAllAlertsPopup();
	}
});