// 프로그램 데이터
const programData = [
	{
		id: 1,
		name: "진로탐색 및 자기계발 워크샵",
		period: "2024-06-20 ~ 2024-06-22",
		status: "applied",
		satisfaction: "none"
	},
	{
		id: 2,
		name: "리더십 개발 프로그램",
		period: "2024-05-15 ~ 2024-05-17",
		status: "ongoing",
		satisfaction: "none"
	},
	{
		id: 3,
		name: "창업 아이디어 경진대회",
		period: "2024-04-10 ~ 2024-04-12",
		status: "completed",
		satisfaction: "pending"
	},
	{
		id: 4,
		name: "글로벌 역량 강화 세미나",
		period: "2024-03-20 ~ 2024-03-22",
		status: "completed",
		satisfaction: "completed"
	},
	{
		id: 5,
		name: "소통 스킬 향상 워크샵",
		period: "2024-02-25 ~ 2024-02-27",
		status: "completed",
		satisfaction: "completed"
	},
	{
		id: 6,
		name: "디지털 리터러시 교육",
		period: "2024-01-15 ~ 2024-01-17",
		status: "completed",
		satisfaction: "pending"
	},
	{
		id: 7,
		name: "팀워크 향상 프로그램",
		period: "2023-12-10 ~ 2023-12-12",
		status: "completed",
		satisfaction: "completed"
	},
	{
		id: 8,
		name: "취업 역량 강화 세미나",
		period: "2023-11-20 ~ 2023-11-22",
		status: "completed",
		satisfaction: "completed"
	}
];

let currentFilter = 'all';
let filteredData = [...programData];

// 상태별 스타일 반환
function getStatusBadge(status) {
	const statusMap = {
		'applied': { class: 'status-applied', text: '신청' },
		'ongoing': { class: 'status-ongoing', text: '진행' },
		'completed': { class: 'status-completed', text: '완료' }
	};

	const statusInfo = statusMap[status];
	return `<span class="status-badge ${statusInfo.class}">${statusInfo.text}</span>`;
}

// 만족도 조사 상태 반환
function getSatisfactionElement(program) {
	if (program.satisfaction === 'none') {
		return '<span class="satisfaction-none">-</span>';
	}
	else if (program.satisfaction === 'pending') {
		return `<button class="satisfaction-btn" onclick="openSatisfactionSurvey(${program.id})">실시</button>`;
	}
	else if (program.satisfaction === 'completed') {
		return '<span class="satisfaction-completed">완료</span>';
	}
}

// 테이블 렌더링
function renderTable(data) {
	const tbody = document.getElementById('programTableBody');

	if (data.length === 0) {
		tbody.innerHTML = `
			<tr>
				<td colspan="5" class="no-results">검색 결과가 없습니다.</td>
			</tr>
		`;
		return;
	}

	tbody.innerHTML = data.map((program, index) => `
		<tr>
			<td>${index + 1}</td>
			<td class="program-name">${program.name}</td>
			<td>${program.period}</td>
			<td>${getStatusBadge(program.status)}</td>
			<td>${getSatisfactionElement(program)}</td>
		</tr>
	`).join('');
}

// 필터 설정
function setFilter(filterType) {
	currentFilter = filterType;

	// 모든 탭에서 active 클래스 제거
	document.querySelectorAll('.filter-tab').forEach(tab => {
		tab.classList.remove('active');
	});

	// 클릭된 탭에 active 클래스 추가
	event.target.classList.add('active');

	// 필터링 적용
	filterPrograms();
}

// 검색 버튼 클릭 처리
function performSearch() {
	filterPrograms();
}

// 프로그램 필터링
function filterPrograms() {
	const searchTerm = document.getElementById('searchInput').value.toLowerCase().trim();

	let filtered = [...programData];

	// 상태별 필터링
	if (currentFilter !== 'all') {
		filtered = filtered.filter(program => program.status === currentFilter);
	}

	// 검색어 필터링
	if (searchTerm) {
		filtered = filtered.filter(program => 
			program.name.toLowerCase().includes(searchTerm)
		);
	}

	filteredData = filtered;
	renderTable(filteredData);
	updatePagination();
}

// 만족도 조사 함수
function openSatisfactionSurvey(programId) {
	if (confirm('만족도 조사를 실시하시겠습니까?')) {
		alert('만족도 조사 페이지로 이동합니다.');
		location.href = /*[[@{/student/noncurr/survey}]]*/ '/student/noncurr/survey';
	}
}

// 페이지네이션 함수
let currentPage = 1;

function changePage(page) {
	const buttons = document.querySelectorAll('.pagination button');

	if (page === 'prev') {
		if (currentPage > 1) {
			currentPage--;
		}
	} 
	else if (page === 'next') {
		if (currentPage < 3) {
			currentPage++;
		}
	}
	else {
		currentPage = page;
	}

	// 모든 버튼에서 active 클래스 제거
	buttons.forEach(btn => btn.classList.remove('active'));

	// 현재 페이지 버튼에 active 클래스 추가
	buttons.forEach(btn => {
		if (btn.textContent == currentPage) {
			btn.classList.add('active');
		}
	});

	// 이전/다음 버튼 상태 업데이트
	buttons[0].disabled = currentPage === 1; // 이전 버튼
	buttons[buttons.length - 1].disabled = currentPage === 3; // 다음 버튼

	console.log('페이지 변경:', currentPage);
}

function updatePagination() {
	// 실제로는 데이터 길이에 따라 페이지네이션을 동적으로 업데이트
	// 지금은 기본 구현 유지
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
	// 비교과 메뉴 열기
	const extracurricularMenu = document.querySelector('.sidebar ul li:nth-child(4)');
	if (extracurricularMenu) {
		extracurricularMenu.classList.add('active');
	}

	// 초기 테이블 렌더링
	renderTable(programData);
});