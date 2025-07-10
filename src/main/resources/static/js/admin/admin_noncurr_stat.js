// ============================================
// admin_noncurr_stat.js - 404 오류 해결 및 UI 개선 버전
// ============================================

// 테이블 초기화할 때 응답률 색상 적용
function applyResponseRateColors() {
    document.querySelectorAll('.response-rate').forEach(span => {
        const rateText = span.textContent.trim();
        const rate = parseFloat(rateText.replace('%', ''));
        
        if (rate >= 80) {
            span.classList.add('high');
        } else if (rate >= 70) {
            span.classList.add('medium');
        } else {
            span.classList.add('low');
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    console.log('비교과 통계 페이지 초기화 시작');
    
    // 기존 테이블 데이터 확인
    const existingRows = document.querySelectorAll('#statisticsTableBody tr[data-program-id]');
    
    if (existingRows.length > 0) {
        console.log('기존 서버 데이터 존재:', existingRows.length + '개 프로그램');
        initializeTableClickEvents();
    } else {
        console.log('서버 데이터 없음 - 빈 상태 표시');
        showEmptyDataState();
    }
	// 페이지네이션 초기화 추가
	initializePagination();
	
	applyResponseRateColors();
    
    initializeBasicEventListeners();
    //initializeSidebar();
    
    console.log('페이지 초기화 완료');
});

/**
 * 만족도 조사 통계 데이터 로드
 */
async function loadSatisfactionStatistics(prgId, container) {
    console.log(`만족도 통계 로드 시작: 프로그램ID=${prgId}`);
    
    try {
        // 로딩 상태 표시
        showModalLoading(container);
        
        // API 호출
        console.log('API 호출 시작:', `/api/admin/satisfaction-stats/${prgId}`);
        const response = await fetch(`/api/admin/satisfaction-stats/${prgId}`);
        
        console.log('API 응답 상태:', response.status, response.statusText);
        
        if (!response.ok) {
            throw new Error(`서버 오류 (${response.status}): ${response.statusText}`);
        }
        
        const data = await response.json();
        console.log('API 응답 데이터:', JSON.stringify(data, null, 2));
        
        // 에러 응답 체크
        if (data.error) {
            console.log('서버에서 에러 응답:', data.message);
            showModalSimpleMessage(data.message || '통계 데이터가 없습니다.', container);
            return;
        }
        
        // 데이터 구조 검증
        if (!data.sections || !Array.isArray(data.sections) || data.sections.length === 0) {
            console.log('섹션 데이터가 없거나 빈 배열:', data.sections);
            showModalSimpleMessage('통계 데이터 없음', container);
            return;
        }
        
        console.log('정상적인 데이터 수신, 모달 업데이트 시작');
        updateModalWithStatistics(data, container);
        
    } catch (error) {
        console.error('만족도 통계 로드 실패:', error);
        console.error('에러 스택:', error.stack);
        showModalSimpleMessage('통계 데이터 없음', container);
    }
}


// ============================================
// 페이지네이션 관련 함수들 추가
// ============================================

/**
 * 페이지네이션 초기화 및 생성
 */
function initializePagination() {
	// DOM에서 페이지 정보 읽어오기
	const paginationContainer = document.querySelector('.pagination-container');

	if (!paginationContainer) {
	    console.log('페이지네이션 컨테이너를 찾을 수 없음');
	    return;
	}
	
	    // HTML data 속성에서 페이지 정보 추출
	    const currentPage = parseInt(paginationContainer.dataset.currentPage || '0');
	    const totalPages = parseInt(paginationContainer.dataset.totalPages || '1');
	    const totalElements = parseInt(paginationContainer.dataset.totalElements || '0');
	    const size = parseInt(paginationContainer.dataset.size || '10');
	    const hasNext = paginationContainer.dataset.hasNext === 'true';
	    const hasPrevious = paginationContainer.dataset.hasPrevious === 'true';
	    
	    console.log('페이지네이션 초기화:', { 
	        currentPage, totalPages, totalElements, size, hasNext, hasPrevious 
	    });
	    
	    generatePaginationHTML(currentPage, totalPages, totalElements, size, hasNext, hasPrevious);
	    updatePaginationInfo(currentPage, totalPages, totalElements, size);
	}

	/**
	 * 페이지네이션 HTML 생성 (파라미터 추가)
	 */
	function generatePaginationHTML(currentPage, totalPages, totalElements, size, hasNext, hasPrevious) {
	    const paginationContainer = document.getElementById('pagination');
	    
	    if (!paginationContainer) {
	        console.log('페이지네이션 컨테이너를 찾을 수 없음');
	        return;
	    }
	    
	    let paginationHTML = '';
	    
	    // 이전 버튼
	    paginationHTML += `
	        <li class="page-item ${!hasPrevious ? 'disabled' : ''}">
	            <a class="page-link page-arrow" href="#" ${hasPrevious ? `onclick="navigateToPage(${currentPage - 1})"` : ''} aria-label="Previous">
	                <i class="fas fa-angle-left"></i>
	            </a>
	        </li>
	    `;
	    
	    // 페이지 번호들 (최대 5개 표시)
	    const startPage = Math.max(0, currentPage - 2);
	    const endPage = Math.min(totalPages - 1, currentPage + 2);
	    
	    for (let i = startPage; i <= endPage; i++) {
	        paginationHTML += `
	            <li class="page-item ${i === currentPage ? 'active' : ''}">
	                <a class="page-link" href="#" onclick="navigateToPage(${i})">${i + 1}</a>
	            </li>
	        `;
	    }
	    
	    // 다음 버튼
	    paginationHTML += `
	        <li class="page-item ${!hasNext ? 'disabled' : ''}">
	            <a class="page-link page-arrow" href="#" ${hasNext ? `onclick="navigateToPage(${currentPage + 1})"` : ''} aria-label="Next">
	                <i class="fas fa-angle-right"></i>
	            </a>
	        </li>
	    `;
	    
	    paginationContainer.innerHTML = paginationHTML;
	    console.log('페이지네이션 HTML 생성 완료');
	}
	/**
	 * 페이지네이션 정보 업데이트
	 */
	function updatePaginationInfo(currentPage, totalPages, totalElements, size) {
	    const paginationInfo = document.getElementById('paginationInfo');
	    
	    if (!paginationInfo) {
	        console.log('페이지네이션 정보 컨테이너를 찾을 수 없음');
	        return;
	    }
	    
	    if (totalElements === 0) {
	        paginationInfo.textContent = '총 0건 (완료된 프로그램 없음)';
	    } else {
	        const startItem = currentPage * size + 1;
	        const endItem = Math.min((currentPage + 1) * size, totalElements);
	        paginationInfo.textContent = `총 ${totalElements}건 중 ${startItem}-${endItem}건 표시 (완료된 프로그램만)`;
	    }
	    
	    console.log('페이지네이션 정보 업데이트 완료');
	}

	/**
	 * 페이지 이동
	 */
	function navigateToPage(page) {
	    console.log(`페이지 ${page + 1}로 이동 요청`);
	    
	    const params = new URLSearchParams(window.location.search);
	    params.set('page', page);
	    
	    const newUrl = `${window.location.pathname}?${params}`;
	    console.log('페이지 이동:', newUrl);
	    window.location.href = newUrl;
	}




/**
 * 모달 로딩 상태 표시
 */
function showModalLoading(container) {
    clearModalContent(container);
    
    const loadingHTML = `
        <div class="stats-section">
            <div style="text-align: center; padding: 40px; color: #6c757d;">
                <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p style="margin-top: 15px; font-size: 16px;">만족도 조사 통계를 불러오는 중...</p>
            </div>
        </div>
    `;
    
    container.insertAdjacentHTML('beforeend', loadingHTML);
}

/**
 * 심플한 메시지 표시 (노란 박스 제거)
 */
function showModalSimpleMessage(message, container) {
    clearModalContent(container);
    
    const messageHTML = `
        <div class="stats-section">
            <div style="text-align: center; padding: 60px 20px; color: #6c757d;">
                <i class="fas fa-chart-bar" style="font-size: 3rem; margin-bottom: 20px; color: #dee2e6;"></i>
                <h4 style="color: #495057; margin-bottom: 10px; font-weight: 500;">${message}</h4>
                <p style="margin: 0; font-size: 14px;">
                    해당 프로그램의 만족도 조사가 아직 완료되지 않았습니다.
                </p>
            </div>
        </div>
    `;
    
    container.insertAdjacentHTML('beforeend', messageHTML);
}

/**
 * 모달 컨텐츠 초기화
 */
function clearModalContent(container) {
    const existingSections = container.querySelectorAll('.stats-section');
    existingSections.forEach(section => section.remove());
}

/**
 * 모달에 실제 통계 데이터 반영
 */
function updateModalWithStatistics(data, container) {
    console.log('모달 통계 데이터 업데이트 시작:', data);
    
    clearModalContent(container);
    
    // 1. 기본 정보 업데이트
    const modalResponders = document.getElementById('modalResponders');
    if (modalResponders) {
        modalResponders.textContent = `${data.totalResponders}명 (${data.responseRate})`;
        console.log('응답자 정보 업데이트:', `${data.totalResponders}명 (${data.responseRate})`);
    }
    
    // 2. 섹션별 통계 생성
    if (!data.sections || !Array.isArray(data.sections) || data.sections.length === 0) {
        console.log('섹션 데이터 문제:', data.sections);
        showModalSimpleMessage('통계 데이터 없음', container);
        return;
    }
    
    console.log(`총 ${data.sections.length}개 섹션 처리 시작`);
    
    // 각 섹션 HTML 생성
    data.sections.forEach((section, index) => {
        console.log(`섹션 ${index + 1} 처리:`, section.sectionName, '문항수:', section.questions?.length || 0);
        
        if (!section.questions || !Array.isArray(section.questions)) {
            console.log('섹션의 문항 데이터가 없음:', section);
            return;
        }
        
        const sectionHTML = createSectionHTML(section);
        container.insertAdjacentHTML('beforeend', sectionHTML);
        console.log(`섹션 ${index + 1} HTML 생성 완료`);
    });
    
    console.log('모달 통계 데이터 업데이트 완료');
}

/**
 * 섹션 HTML 생성
 */
function createSectionHTML(section) {
    const questionsHTML = section.questions.map((question, index) => 
        createQuestionHTML(question, index + 1)
    ).join('');
    
    const sectionAverageHTML = createSectionAverageHTML(section);
    
    return `
        <div class="stats-section">
            <h4 class="stats-section-title">${section.sectionName}</h4>
            <table class="stats-table">
                <thead>
                    <tr>
                        <th style="width: 5%;">번호</th>
                        <th style="width: 40%;">설문내용</th>
                        <th style="width: 30%;">응답분포 (1~5점)</th>
                        <th style="width: 15%;">응답수</th>
                        <th style="width: 10%;">평균</th>
                    </tr>
                </thead>
                <tbody>
                    ${questionsHTML}
                    ${sectionAverageHTML}
                </tbody>
            </table>
        </div>
    `;
}

/**
 * 문항 HTML 생성
 */
function createQuestionHTML(question, questionNumber) {
    const responseBarsHTML = [1, 2, 3, 4, 5].map(score => {
        const scoreData = question.scoreStats[score] || { count: 0 };
        return `<div class="response-bar score-${score}">${scoreData.count}</div>`;
    }).join('');
    
    const responsePercentagesHTML = [1, 2, 3, 4, 5].map(score => {
        const scoreData = question.scoreStats[score] || { percentage: '0.0' };
        return `<div class="response-percentage">${scoreData.percentage}%</div>`;
    }).join('');
    
    const averageClass = getAverageScoreClass(question.averageScore);
    
    return `
        <tr>
            <td>${questionNumber}</td>
            <td class="question-text">${question.surContent}</td>
            <td>
                <div class="response-distribution">
                    <div class="response-bars">
                        ${responseBarsHTML}
                    </div>
                    <div class="response-percentages">
                        ${responsePercentagesHTML}
                    </div>
                </div>
            </td>
            <td>${question.totalResponses}명</td>
            <td><span class="average-score ${averageClass}">${question.averageScore}</span></td>
        </tr>
    `;
}

/**
 * 섹션 평균 HTML 생성
 */
function createSectionAverageHTML(section) {
    const averageClass = getAverageScoreClass(parseFloat(section.sectionAverage));
    
    return `
        <tr class="section-average-row">
            <td colspan="2"><strong>${section.sectionName} 평균</strong></td>
            <td>-</td>
            <td><strong>${section.sectionResponders}명</strong></td>
            <td><span class="average-score ${averageClass}"><strong>${section.sectionAverage}점</strong></span></td>
        </tr>
    `;
}

/**
 * 평균 점수에 따른 CSS 클래스 결정
 */
function getAverageScoreClass(score) {
    if (score >= 4.5) return 'excellent';
    if (score >= 4.0) return 'excellent';
    if (score >= 3.5) return 'good';
    if (score >= 3.0) return 'average';
    return 'poor';
}

/**
 * 상세 모달 열기
 */
function openDetailModal(prgId, programName, department, period, participants, responseRate) {
    console.log('=== 상세 모달 열기 ===');
    console.log('프로그램ID:', prgId);
    console.log('프로그램명:', programName);
    console.log('운영부서:', department);
    console.log('운영기간:', period);
    console.log('참여인원:', participants);
    console.log('응답률:', responseRate);
    
    // 모달 기본 정보 설정
    const modalTitle = document.getElementById('modalProgramTitle');
    const modalDepartment = document.getElementById('modalDepartment');
    const modalPeriod = document.getElementById('modalPeriod');
    const modalParticipants = document.getElementById('modalParticipants');
    const modalResponders = document.getElementById('modalResponders');
    
    if (modalTitle) modalTitle.textContent = programName;
    if (modalDepartment) modalDepartment.textContent = department;
    if (modalPeriod) modalPeriod.textContent = period;
    if (modalParticipants) modalParticipants.textContent = participants;
    if (modalResponders) modalResponders.textContent = '조회 중...';
    
    console.log('모달 기본 정보 설정 완료');
    
    // 모달 표시
    const modal = document.getElementById('detailModal');
    if (modal) {
        modal.style.display = 'block';
        console.log('모달 표시 완료');
    } else {
        console.error('모달 엘리먼트를 찾을 수 없음');
        return;
    }
    
    // 통계 컨테이너 가져오기
    const statsContainer = document.getElementById('modalStatsContainer') || 
                          document.querySelector('.detail-modal-body');
    
    if (!statsContainer) {
        console.error('통계 컨테이너를 찾을 수 없음');
        return;
    }
    
    console.log('통계 컨테이너 찾음:', statsContainer);
    
    // 만족도 통계 데이터 로드
    console.log('만족도 통계 로드 시작...');
    loadSatisfactionStatistics(prgId, statsContainer);
}

/**
 * 모달 닫기
 */
function closeDetailModal() {
    document.getElementById('detailModal').style.display = 'none';
}

// ============================================
// 기존 코드들 (검색, 사이드바 등)
// ============================================

function showEmptyDataState() {
    const tbody = document.getElementById('statisticsTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="7" style="text-align: center; padding: 50px; color: #6c757d;">
                완료된 프로그램이 없습니다
            </td>
        </tr>
    `;
    
    const paginationInfo = document.getElementById('paginationInfo');
    if (paginationInfo) {
        paginationInfo.textContent = '1/1 페이지';
    }
}

function initializeBasicEventListeners() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }
    
    const periodFilter = document.getElementById('periodFilter');
    if (periodFilter) {
        periodFilter.addEventListener('change', function() {
            performSearch();
        });
    }
    
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            const modal = document.getElementById('detailModal');
            if (modal && modal.style.display === 'block') {
                closeDetailModal();
            }
        }
    });
    
    window.onclick = function(event) {
        const modal = document.getElementById('detailModal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    }
}

function initializeTableClickEvents() {
    const rows = document.querySelectorAll('#statisticsTableBody tr[data-program-id]');
    
    rows.forEach(row => {
        row.style.cursor = 'pointer';
        
        row.addEventListener('click', function() {
            const programId = this.getAttribute('data-program-id');
            const programName = this.cells[1].textContent.trim();
            const department = this.cells[2].textContent.trim();
            const period = this.cells[3].textContent.trim();
            const participants = this.cells[4].textContent.trim();
            const responseRate = this.cells[5].querySelector('.response-rate')?.textContent.trim() || this.cells[5].textContent.trim();
            
            console.log(`프로그램 클릭: ID=${programId}, 이름=${programName}`);
            openDetailModal(programId, programName, department, period, participants, responseRate);
        });
        
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f8f9fa';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
    
    console.log(`테이블 클릭 이벤트 등록 완료: ${rows.length}개 행`);
}

function performSearch() {
    const searchType = document.getElementById('searchType')?.value || 'name';
    const searchInput = document.getElementById('searchInput')?.value?.trim() || '';
    const periodFilter = document.getElementById('periodFilter')?.value || '';
    
    console.log(`검색 실행: 유형=${searchType}, 검색어="${searchInput}", 기간=${periodFilter}`);
    
    const params = new URLSearchParams();
    if (searchInput) {
        params.append('search', searchInput);
        params.append('searchType', searchType);
    }
    if (periodFilter) {
        params.append('period', periodFilter);
    }
    
    const currentUrl = window.location.pathname;
    const newUrl = params.toString() ? `${currentUrl}?${params}` : currentUrl;
    
    console.log('페이지 이동:', newUrl);
    window.location.href = newUrl;
}

function searchStatistics() {
    performSearch();
}

function initializeSidebar() {
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            const sidebar = document.getElementById('layoutSidenav_nav');
            const content = document.getElementById('layoutSidenav_content');
            
            if (sidebar && content) {
                if (window.innerWidth <= 768) {
                    sidebar.classList.toggle('show');
                } else {
                    sidebar.classList.toggle('collapsed');
                    content.classList.toggle('expanded');
                }
            }
        });
    }
    
    window.addEventListener('resize', function() {
        const sidebar = document.getElementById('layoutSidenav_nav');
        const content = document.getElementById('layoutSidenav_content');
        
        if (sidebar && content) {
            if (window.innerWidth > 768) {
                sidebar.classList.remove('show');
            } else {
                sidebar.classList.remove('collapsed');
                content.classList.remove('expanded');
            }
        }
    });
}

function toggleSubMenu(element) {
    const navItem = element.closest('.nav-item');
    
    document.querySelectorAll('.nav-item').forEach(item => {
        if (item !== navItem) {
            item.classList.remove('menu-open');
        }
    });
    
    navItem.classList.toggle('menu-open');
    
    if (event) {
        event.preventDefault();
    }
    return false;
}

console.log('admin_noncurr_stat.js 로드 완료 - 404 오류 해결 및 UI 개선 버전');