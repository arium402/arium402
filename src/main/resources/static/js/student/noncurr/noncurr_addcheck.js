// 서버 데이터 사용 (하드코딩 제거)
let programData = [];
let currentFilter = 'all';
let filteredData = [];
let serverCurrentDate = null;  // 서버 날짜 저장용

// HTML에서 서버 데이터 읽기 (만족도 조사 상태 포함)
function loadServerData() {
    const dataContainer = document.getElementById('applicationsData');
    
    serverCurrentDate = dataContainer.getAttribute('data-server-date');
    console.log('서버 현재 날짜 (아시아/서울):', serverCurrentDate);
    
    const appDataItems = dataContainer.querySelectorAll('.app-data-item');
    const serverApplications = [];
    
    appDataItems.forEach(item => {
        serverApplications.push({
            prgId: parseInt(item.getAttribute('data-app-id')),
            prgNm: item.getAttribute('data-app-name'),
            prgStDt: item.getAttribute('data-start-date'),
            prgEndDt: item.getAttribute('data-end-date'),
			surveyDt: item.getAttribute('data-survey-dt'),
			applicationStatus: item.getAttribute('data-application-status'),
            satisfactionStatus: item.getAttribute('data-satisfaction-status') //추가
        });
    });
    
    console.log('HTML에서 읽어온 서버 데이터:', serverApplications);
    return serverApplications;
}
//서버 데이터를 화면용 형태로 변환
function convertServerDataToDisplayFormat(serverApplications) {
    return serverApplications.map((app, index) => ({
        id: app.prgId,
        name: app.prgNm,
        period: `${app.prgStDt} ~ ${app.prgEndDt}`,
		surveyDt: app.surveyDt,
		status: getProgramStatus(app),           // 상태 계산
		satisfaction: app.satisfactionStatus === 'completed' 
		            ? 'completed' 
		            : getSatisfactionStatus(app)
    }));
}

//프로그램 상태 계산 (서버 날짜 기준)
function getProgramStatus(app) {
    //서버 날짜 사용 (아시아/서울 시간)
    const today = new Date(serverCurrentDate);
    const startDate = new Date(app.prgStDt);
    const endDate = new Date(app.prgEndDt);
    
    console.log(`프로그램 ${app.prgNm} 상태 계산:`);
    console.log(`  - 서버 기준 오늘: ${serverCurrentDate}`);
    console.log(`  - 운영 시작일: ${app.prgStDt}`);
    console.log(`  - 운영 종료일: ${app.prgEndDt}`);
    
    if (today < startDate) {
        console.log(`  → 결과: applied (운영 시작 전)`);
        return 'applied';  // 신청 (운영 시작 전)
    } else if (today >= startDate && today <= endDate) {
        console.log(`  → 결과: ongoing (운영 중)`);
        return 'ongoing';  // 진행 (운영 중)
    } else {
        console.log(`  → 결과: completed (운영 종료)`);
        return 'completed'; // 완료 (운영 종료)
    }
}

// 만족도 조사 상태 계산
function getSatisfactionStatus(app) {
    const status = getProgramStatus(app);
    
    if (status !== 'completed') {
        return 'none';
    }
    
	if (!app.surveyDt) {
	    return 'pending';  // 마감일 없으면 계속 가능
	}
	
	// 마감일 비교
	const today = new Date(serverCurrentDate);
	const surveyDeadline = new Date(app.surveyDt);
	    
	if (today > surveyDeadline) {
	     return 'expired';  // 마감
	} else {
	     return 'pending';  // 실시 가능
	}
}
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

// 만족도 조사 상태 반환(마감일 포함)
function getSatisfactionElement(program) {
	
	//마감일 포맷팅 (yyyy-MM-dd -> 03/01까지)
	let deadlineText = '';
	if (program.surveyDt) {
	    const parts = program.surveyDt.split('-');
	    if (parts.length === 3) {
	        const year = parts[0].substring(2);
			const month = parts[1];
			const day = parts[2];
			deadlineText = `<span class="survey-deadline">${year}/${month}/${day}까지</span>`;
	    }
	}
	
	//상태별 처리
    if (program.satisfaction === 'none') {
        return '<span class="satisfaction-none">-</span>';
    }
	
	else if (program.satisfaction === 'pending') {
		     return `
		        <div class="satisfaction-container">
		            ${deadlineText}
		            <button class="satisfaction-btn" onclick="openSatisfactionSurvey(${program.id})">실시</button>
		        </div>
		     `;
	}
    else if (program.satisfaction === 'completed') {
        return '<span class="satisfaction-completed">완료</span>';
    }
	
	else if (program.satisfaction === 'expired') {
	    return `
	    <div class="satisfaction-container">
	       ${deadlineText}
	       <span class="satisfaction-expired">마감</span>
	    </div>
	    `;
	}
	
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
    updatePagination(); // 필터링 후 페이지네이션 업데이트
}

// 만족도 조사 함수
function openSatisfactionSurvey(programId) {
    if (confirm('만족도 조사를 실시하시겠습니까?')) {
        // 실제 프로그램 ID를 파라미터로 전달
        window.location.href = `/student/noncurr/survey?prgId=${programId}`;
    }
}

// 페이지네이션 함수
let currentPage = 1;
const itemsPerPage = 10; // 페이지당 아이템 수

function changePage(page) {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    
    if (page === 'prev') {
        if (currentPage > 1) {
            currentPage--;
        }
    } 
    else if (page === 'next') {
        if (currentPage < totalPages) {
            currentPage++;
        }
    }
    else {
        currentPage = page;
    }

    updatePaginationButtons();
    renderCurrentPage();
    
    console.log('페이지 변경:', currentPage);
}

// 현재 페이지 데이터만 렌더링
function renderCurrentPage() {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentPageData = filteredData.slice(startIndex, endIndex);
    
    renderTable(currentPageData, startIndex); // startIndex를 넘겨서 번호 계산
}

// 테이블 렌더링
function renderTable(data, startIndex = 0) {
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
        <tr class="clickable-row" data-prg-id="${program.id}">
            <td>${startIndex + index + 1}</td>
            <td class="program-name">${program.name}</td>
            <td>${program.period}</td>
            <td>${getStatusBadge(program.status)}</td>
            <td>${getSatisfactionElement(program)}</td>
        </tr>
    `).join('');
    
    //클릭 이벤트 등록
    initTableClickEvent();
}

// 페이지네이션 버튼 업데이트 (동적 생성) - 수정된 버전
function updatePaginationButtons() {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const paginationContainer = document.querySelector('.pagination');
    
    // 데이터가 아예 없을 때만 페이지네이션 숨김
    if (filteredData.length === 0) {
        paginationContainer.style.display = 'none';
        return;
    } else {
        paginationContainer.style.display = 'flex';
    }
    
    let paginationHtml = '';
    
    // 이전 버튼
    paginationHtml += `
        <button onclick="changePage('prev')" ${currentPage === 1 ? 'disabled' : ''}>◀</button>
    `;
    
    // 페이지 번호들 (최대 5개만 표시)
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, startPage + 4);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHtml += `
            <button onclick="changePage(${i})" ${i === currentPage ? 'class="active"' : ''}>${i}</button>
        `;
    }
    
    // 다음 버튼
    paginationHtml += `
        <button onclick="changePage('next')" ${currentPage === totalPages ? 'disabled' : ''}>▶</button>
    `;
    
    paginationContainer.innerHTML = paginationHtml;
}

function updatePagination() {
    currentPage = 1; // 검색/필터링시 첫 페이지로
    updatePaginationButtons();
    renderCurrentPage();
}

/**
 * 테이블 행 클릭 이벤트 (상세 페이지 이동)
 */
function initTableClickEvent() {
    const rows = document.querySelectorAll('#programTableBody .clickable-row');
    
    rows.forEach(row => {
        // 마우스 커서 변경
        row.style.cursor = 'pointer';
        
        // 클릭 이벤트
        row.addEventListener('click', function() {
			//만족도 버튼 클릭 시 이벤트 전파 방지
			if (e.target.classList.contains('satisfaction-btn')) {
			    return;  // 버튼 클릭이면 여기서 중단
			}
			
			const prgId = this.dataset.prgId; 
            if (prgId) {
                // 상세 페이지로 이동
                window.location.href = `/student/noncurr/detail?prgId=${prgId}`;
            }
        });
    });
}

//페이지 로드 시 실행 (HTML data 속성에서 데이터 로드)
document.addEventListener('DOMContentLoaded', function() {
    
    //HTML의 data 속성에서 서버 데이터 읽기
    const serverApplications = loadServerData();
    
    if (serverApplications && serverApplications.length > 0) {
        programData = convertServerDataToDisplayFormat(serverApplications);
		
    } else {
        programData = [];
    }

    // 초기 테이블 렌더링
    filteredData = [...programData];
    updatePagination();
	//테이블 클릭 이벤트 등록
	initTableClickEvent();
	console.log('신청 내역 페이지 로드 완료');
});