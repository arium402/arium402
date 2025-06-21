// 공지사항 데이터 (실제로는 서버에서 가져올 데이터)
const noticeData = {
	all: [
		{ no: 156, title: "2025년 하계 비교과 프로그램 신청 안내", date: "2025.06.18", author: "비교과팀", views: 234, category: "extracurricular" },
		{ no: 155, title: "마일리지 적립 기준 변경 안내", date: "2025.06.17", author: "학생지원팀", views: 156, category: "etc" },
		{ no: 154, title: "온라인 진로상담 예약 시스템 오픈", date: "2025.06.16", author: "상담팀", views: 189, category: "counseling" },
		{ no: 153, title: "핵심역량 진단 결과 공개 안내", date: "2025.06.15", author: "역량개발팀", views: 298, category: "etc" },
		{ no: 152, title: "학습컨설팅 신청 방법 변경 안내", date: "2025.06.14", author: "상담팀", views: 167, category: "counseling" },
		{ no: 151, title: "2025년 하계방학 센터 운영시간 안내", date: "2025.06.13", author: "관리자", views: 123, category: "etc" },
		{ no: 150, title: "익명상담 서비스 개선 안내", date: "2025.06.12", author: "상담팀", views: 245, category: "counseling" },
		{ no: 149, title: "비교과 프로그램 만족도 조사 결과", date: "2025.06.11", author: "비교과팀", views: 178, category: "extracurricular" }
	]
};

let currentTab = 'all';
let currentPage = 16;

function openChatModal() {
	alert('채팅 기능은 준비 중입니다.');
}

function changeTab(tabName) {
	// 탭 버튼 활성화 상태 변경
	document.querySelectorAll('.tab-btn').forEach(btn => {
		btn.classList.remove('active');
	});
	event.target.classList.add('active');

	currentTab = tabName;

	// 공지사항 목록 필터링 및 업데이트
	updateNoticeList();
}

function updateNoticeList() {
	const tableBody = document.getElementById('noticeTableBody');
	let filteredData = [];

	if (currentTab === 'all') {
		filteredData = noticeData.all;
	}
	else {
		filteredData = noticeData.all.filter(notice => notice.category === currentTab);
	}

	// 고정 공지사항
	let html = `
		<tr class="pinned">
			<td class="no-col"><i class="fas fa-thumbtack pin-icon"></i>공지</td>
			<td class="title-col">
				<a href="#" class="notice-title-link">[중요] 2025학년도 1학기 학사일정 안내</a>
			</td>
			<td class="date-col">2025.02.01</td>
			<td class="author-col">관리자</td>
			<td class="views-col">1,523</td>
		</tr>
		<tr class="pinned">
			<td class="no-col"><i class="fas fa-thumbtack pin-icon"></i>공지</td>
			<td class="title-col">
				<a href="#" class="notice-title-link">[필독] 학생상담센터 이용 안내</a>
			</td>
			<td class="date-col">2025.01.15</td>
			<td class="author-col">관리자</td>
			<td class="views-col">987</td>
		</tr>
	`;

	// 일반 공지사항
	filteredData.forEach(notice => {
		html += `
			<tr>
				<td class="no-col">${notice.no}</td>
				<td class="title-col">
					<a href="#" class="notice-title-link">${notice.title}</a>
				</td>
				<td class="date-col">${notice.date}</td>
				<td class="author-col">${notice.author}</td>
				<td class="views-col">${notice.views}</td>
			</tr>
		`;
	});

	tableBody.innerHTML = html;
}

function searchNotices() {
	const searchType = document.getElementById('searchType').value;
	const searchInput = document.getElementById('searchInput').value;

	if (searchInput.trim() === '') {
		alert('검색어를 입력해주세요.');
		return;
	}

	alert(`${searchType} 기준으로 "${searchInput}" 검색을 실행합니다.`);
	// 실제 검색 로직 구현
}

function changePage(page) {
	if (page === currentPage) return;

	currentPage = page;

	// 페이지 버튼 활성화 상태 업데이트
	document.querySelectorAll('.page-btn').forEach(btn => {
		btn.classList.remove('active');
	});

	// 새로운 페이지의 공지사항 데이터 로드 (실제로는 서버에서 가져올 데이터)
	alert(`${page}페이지로 이동합니다.`);

	// 실제로는 서버에서 해당 페이지의 데이터를 가져와서 테이블을 업데이트
}