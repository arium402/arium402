let currentPage = 0;
const pageSize = 8;
let currentFilters = {};

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    initializeFilters();
    initializeSearch();
    loadPrograms(); // 초기 데이터 로드
});

// 프로그램 로드 (페이징 포함)
function loadPrograms(page = 0) {
    currentPage = page;
    
    const params = new URLSearchParams({
        page: page,
        size: pageSize,
        ...currentFilters
    });
    
	fetch(`/api/student/noncurr/search?${params}`)
	    .then(response => response.json())
	    .then(data => {
	        console.log('🔍 전체 API 응답:', data);
	        
	        if (data.success) {
	            console.log('🔍 프로그램 데이터:', data.data.programs);
	            
	            // 각 프로그램별 상세 확인
	            data.data.programs.forEach((program, index) => {
	                console.log(`프로그램 ${index + 1}:`, program.prgNm);
	                console.log('  - dDayText:', program.dDayText);
	                console.log('  - dDay:', program.dDay);
	                console.log('  - 전체 키들:', Object.keys(program));
	                console.log('  - 전체 값들:', program);
	                console.log('---');
	            });
	            
	            updateProgramGrid(data.data.programs);
	            updatePagination(data.data);
	        } else {
	            alert('프로그램 로드 중 오류가 발생했습니다: ' + data.message);
	        }
	    })
	    .catch(error => {
	        console.error('Error:', error);
	        alert('서버 오류가 발생했습니다.');
	    });
}

// 검색/필터링 (null 방지)
function searchPrograms() {
    currentFilters = {
        keyword: document.getElementById('searchKeyword').value || '',          // || '' 추가
        dept: document.getElementById('deptFilter').value || '',               // || '' 추가
        mileageFilter: document.getElementById('mileageFilter').value || '',   // || '' 추가
        statusFilter: document.getElementById('statusFilter').value || '',     // || '' 추가
        sortBy: document.getElementById('sortBy').value || ''                  // || '' 추가
    };
    
    loadPrograms(0);
}

// 페이지네이션 업데이트
function updatePagination(pageData) {
    const container = document.getElementById('paginationContainer');
    const { currentPage, totalPages, hasPrevious, hasNext, isFirst, isLast } = pageData;
    
    let paginationHtml = '';
    
    // 이전 버튼
    paginationHtml += `
        <li class="page-item ${!hasPrevious ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="${hasPrevious ? `changePage(${currentPage - 1})` : 'return false'}" 
               ${!hasPrevious ? 'tabindex="-1" aria-disabled="true"' : ''}>이전</a>
        </li>
    `;
    
    // 페이지 번호들
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHtml += `
            <li class="page-item ${i === currentPage ? 'active' : ''}" ${i === currentPage ? 'aria-current="page"' : ''}>
                <a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>
            </li>
        `;
    }
    
    // 다음 버튼
    paginationHtml += `
        <li class="page-item ${!hasNext ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="${hasNext ? `changePage(${currentPage + 1})` : 'return false'}">다음</a>
        </li>
    `;
    
    container.innerHTML = paginationHtml;
}

// 페이지 변경
function changePage(page) {
    loadPrograms(page);
    
    // 페이지 상단으로 스크롤
    document.querySelector('.page-header').scrollIntoView({ 
        behavior: 'smooth' 
    });
    
    return false; // 링크 기본 동작 방지
}

// 필터 초기화 (deptFilter 추가)
function initializeFilters() {
    const filters = ['deptFilter', 'mileageFilter', 'statusFilter', 'sortBy'];  // ← categoryFilter에서 deptFilter로 변경
    
    filters.forEach(filterId => {
        document.getElementById(filterId).addEventListener('change', function() {
            searchPrograms();
        });
    });
}

// 검색 초기화
function initializeSearch() {
    const searchInput = document.getElementById('searchKeyword');
    let searchTimeout;
    
    searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            searchPrograms(); // 검색시 첫 페이지부터
        }, 500);
    });
}

// 프로그램 그리드 업데이트
function updateProgramGrid(programs) {
    const grid = document.getElementById('programGrid');
    
    if (programs.length === 0) {
        grid.innerHTML = '<div class="no-results">조건에 맞는 프로그램이 없습니다.</div>';
        return;
    }
    
    grid.innerHTML = programs.map(program => createProgramCard(program)).join('');
}


// 프로그램 카드 HTML 생성
function createProgramCard(program) {
	    // 🔍 undefined 방지 처리
	    const dDayText = program.dDayText || 'D-?';
	    const programStatus = program.programStatus || 'available';
	    const applicationStatus = program.applicationStatus || 'NOT_APPLIED';
	    
	    const dDayClass = programStatus === 'closed' ? 'urgent' : 
	                     (program.dDay <= 3 ? 'urgent' : 
	                      (program.dDay <= 7 ? 'warning' : ''));
	    
	    const buttonClass = applicationStatus === 'APPLIED' ? 'completed' : 
	                       (programStatus === 'closed' ? 'disabled' : '');
	    
	    const buttonText = applicationStatus === 'APPLIED' ? '신청완료' : 
	                      (programStatus === 'closed' ? '마감' : '신청하기');
	    
	    const capacityClass = program.currentApplicants >= program.maxCnt ? 'full' : '';
	    
	    // 🔍 디버그 로그 추가
	    console.log('Program:', program.prgNm, 'D-Day Text:', dDayText);
	    
		
		// 🔍 이미지 URL 디버깅
		console.log('Program:', program.prgNm);
		console.log('imageUrl:', program.imageUrl);
		console.log('orgFileName:', program.orgFileName);

		
	    return `
	        <div class="program-card">
	            <div class="program-image">
	                <div class="dday-badge ${dDayClass}">${dDayText}</div>
	                <div class="status-badge status-${programStatus}"></div>
	                ${program.imageUrl ? 
	                    `<img src="${program.imageUrl}" alt="프로그램 이미지">` : 
	                    `<i class="fas fa-graduation-cap placeholder-icon"></i>`
	                }
	            </div>
	            <div class="program-content">
	                <h3 class="program-title">${program.prgNm || '프로그램명'}</h3>
	                <div class="program-details">
	                    <div class="program-dates">
	                        <div class="date-item">
	                            <span class="date-label">신청기간:</span>
	                            <span class="date-value">${program.recruitStDt || '미정'} ~ ${program.recruitEndDt || '미정'}</span>
	                        </div>
	                        <div class="date-item">
	                            <span class="date-label">운영기간:</span>
	                            <span class="date-value">${program.prgStDt || '미정'} ~ ${program.prgEndDt || '미정'}</span>
	                        </div>
	                    </div>
	                    <div class="program-info">
	                        <span class="program-points">${program.mlgDefScore || 0} P</span>
	                        <span class="program-capacity ${capacityClass}">${program.currentApplicants || 0}/${program.maxCnt || 0}</span>
	                    </div>
	                    <button class="apply-btn ${buttonClass}" 
	                            ${programStatus === 'closed' ? 'disabled' : ''}
	                            onclick="applyProgram(${program.prgId})">
	                        ${buttonText}
	                    </button>
	                </div>
	            </div>
	        </div>
	    `;
	}

// 프로그램 신청 (경로 오류 수정)
function applyProgram(prgId) {
    if (confirm('이 프로그램에 신청하시겠습니까?')) {
        fetch('/api/student/noncurr/apply', {  // ← 경로 수정
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `prgId=${prgId}`
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) {
                searchPrograms();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('신청 처리 중 오류가 발생했습니다.');
        });
    }
}