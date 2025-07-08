// ============================================
// admin_noncurr_stat.js - 404 오류 해결 버전
// ============================================

// ============================================
// 1. 페이지 초기화 및 기존 코드 유지
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('비교과 통계 페이지 초기화 시작');
    
    // 기존 테이블 데이터 확인
    const existingRows = document.querySelectorAll('#statisticsTableBody tr[data-program-id]');
    
    if (existingRows.length > 0) {
        console.log('기존 서버 데이터 존재:', existingRows.length + '개 프로그램');
        // 기존 데이터가 있으면 클릭 이벤트만 추가
        initializeTableClickEvents();
    } else {
        console.log('서버 데이터 없음 - 빈 상태 표시');
        showEmptyDataState();
    }
    
    // 기본 이벤트 리스너 등록
    initializeBasicEventListeners();
    
    // 사이드바 기능 초기화
    initializeSidebar();
    
    console.log('페이지 초기화 완료');
});

/**
 * 데이터가 없을 때 상태 표시 (404 아님)
 */
function showEmptyDataState() {
    const tbody = document.getElementById('statisticsTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="7" style="text-align: center; padding: 50px; color: #6c757d;">
                완료된 프로그램이 없습니다
            </td>
        </tr>
    `;
    
    // 페이징 정보는 기본 1페이지로 표시
    const paginationInfo = document.getElementById('paginationInfo');
    if (paginationInfo) {
        paginationInfo.textContent = '1/1 페이지';
    }
    
    // 페이징 버튼은 기본 상태로 유지 (숨기지 않음)
}

/**
 * 기본 이벤트 리스너 초기화 (검색, 필터 등)
 */
function initializeBasicEventListeners() {
    // 검색 기능
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }
    
    // 필터 변경
    const periodFilter = document.getElementById('periodFilter');
    if (periodFilter) {
        periodFilter.addEventListener('change', function() {
            performSearch(); // 필터 변경 시 검색 실행
        });
    }
    
    // 모달 이벤트
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

/**
 * 테이블 행 클릭 이벤트 초기화
 */
function initializeTableClickEvents() {
    const rows = document.querySelectorAll('#statisticsTableBody tr[data-program-id]');
    
    rows.forEach(row => {
        row.style.cursor = 'pointer'; // 클릭 가능하다는 시각적 표시
        
        row.addEventListener('click', function() {
            const programId = this.getAttribute('data-program-id');
            const programName = this.cells[1].textContent.trim();
            const department = this.cells[2].textContent.trim();
            const period = this.cells[3].textContent.trim();
            const participants = this.cells[4].textContent.trim();
            const responseRate = this.cells[5].querySelector('.response-rate')?.textContent.trim() || this.cells[5].textContent.trim();
            
            console.log(`프로그램 클릭: ID=${programId}, 이름=${programName}`);
            
            // 모달 열기
            openDetailModal(programId, programName, department, period, participants, responseRate);
        });
        
        // 호버 효과 추가
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f8f9fa';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
    
    console.log(`테이블 클릭 이벤트 등록 완료: ${rows.length}개 행`);
}

// ============================================
// 2. 검색 및 필터링 (서버 요청)
// ============================================

/**
 * 검색 실행 - 서버에 페이지 요청
 */
function performSearch() {
    const searchType = document.getElementById('searchType')?.value || 'name';
    const searchInput = document.getElementById('searchInput')?.value?.trim() || '';
    const periodFilter = document.getElementById('periodFilter')?.value || '';
    
    console.log(`검색 실행: 유형=${searchType}, 검색어="${searchInput}", 기간=${periodFilter}`);
    
    // URL 파라미터 구성
    const params = new URLSearchParams();
    if (searchInput) {
        params.append('search', searchInput);
        params.append('searchType', searchType);
    }
    if (periodFilter) {
        params.append('period', periodFilter);
    }
    
    // 서버에 페이지 요청 (새로고침)
    const currentUrl = window.location.pathname;
    const newUrl = params.toString() ? `${currentUrl}?${params}` : currentUrl;
    
    console.log('페이지 이동:', newUrl);
    window.location.href = newUrl;
}

/**
 * 기존 검색 함수 (호환성 유지)
 */
function searchStatistics() {
    performSearch();
}

// ============================================
// 3. 상세 모달 처리 (만족도 통계)
// ============================================

/**
 * 상세 모달 열기
 */
function openDetailModal(prgId, programName, department, period, participants, responseRate) {
    console.log(`상세 모달 열기: 프로그램ID=${prgId}`);
    
    // 모달 기본 정보 설정
    document.getElementById('modalProgramTitle').textContent = programName;
    document.getElementById('modalDepartment').textContent = department;
    document.getElementById('modalPeriod').textContent = period;
    document.getElementById('modalParticipants').textContent = participants;
    document.getElementById('modalResponders').textContent = '조회 중...';
    
    // 모달 표시
    document.getElementById('detailModal').style.display = 'block';
    
    // 통계 컨테이너 초기화
    const statsContainer = document.getElementById('modalStatsContainer') || 
                          document.querySelector('.detail-modal-body');
    
    // 기존 stats-section 제거
    const existingSections = statsContainer.querySelectorAll('.stats-section');
    existingSections.forEach(section => section.remove());
    
    // 만족도 통계 데이터 로드
    loadSatisfactionStatistics(prgId, statsContainer);
}

/**
 * 만족도 조사 통계 데이터 로드
 */
async function loadSatisfactionStatistics(prgId, container) {
    console.log(`만족도 통계 로드 시작: 프로그램ID=${prgId}`);
    
    try {
        // 로딩 상태 표시
        showModalLoading(container);
        
        // API 호출 (이 API는 이미 구현되어 있어야 함)
        const response = await fetch(`/admin/api/noncurr/satisfaction-stats/${prgId}`);
        
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('해당 프로그램의 만족도 조사 데이터가 없습니다.');
            }
            throw new Error(`서버 오류 (${response.status}): ${response.statusText}`);
        }
        
        const data = await response.json();
        
        // 에러 응답 체크
        if (data.error) {
            throw new Error(data.message || '데이터 조회 실패');
        }
        
        console.log('만족도 통계 데이터 수신:', data);
        
        // 모달 데이터 업데이트
        updateModalWithStatistics(data, container);
        
    } catch (error) {
        console.error('만족도 통계 로드 실패:', error);
        showModalError(error.message, container);
    }
}

/**
 * 모달 로딩 상태 표시
 */
function showModalLoading(container) {
    const loadingHTML = `
        <div class="stats-section">
            <div class="loading-container" style="text-align: center; padding: 50px;">
                <div class="spinner-border" role="status" style="width: 3rem; height: 3rem;">
                    <span class="sr-only">Loading...</span>
                </div>
                <p style="margin-top: 15px; font-size: 16px;">만족도 조사 통계를 불러오는 중...</p>
            </div>
        </div>
    `;
    
    container.insertAdjacentHTML('beforeend', loadingHTML);
}

/**
 * 모달 에러 상태 표시
 */
function showModalError(message, container) {
    // 기존 로딩 제거
    const existingSections = container.querySelectorAll('.stats-section');
    existingSections.forEach(section => section.remove());
    
    const errorHTML = `
        <div class="stats-section">
            <div class="error-container" style="text-align: center; padding: 50px;">
                <div class="alert alert-warning" role="alert">
                    <h4 class="alert-heading">통계 데이터 없음</h4>
                    <p style="margin: 15px 0;">${message}</p>
                    <hr>
                    <p class="mb-0">
                        해당 프로그램의 만족도 조사가 아직 완료되지 않았거나<br>
                        조사 데이터가 존재하지 않습니다.
                    </p>
                </div>
            </div>
        </div>
    `;
    
    container.insertAdjacentHTML('beforeend', errorHTML);
}

/**
 * 모달에 실제 통계 데이터 반영
 */
function updateModalWithStatistics(data, container) {
    console.log('모달 통계 데이터 업데이트 시작:', data);
    
    // 기존 로딩 제거
    const existingSections = container.querySelectorAll('.stats-section');
    existingSections.forEach(section => section.remove());
    
    // 1. 기본 정보 업데이트
    const modalResponders = document.getElementById('modalResponders');
    if (modalResponders) {
        modalResponders.textContent = `${data.totalResponders}명 (${data.responseRate})`;
    }
    
    // 2. 섹션별 통계 생성
    if (!data.sections || data.sections.length === 0) {
        showModalError('통계 데이터가 없습니다.', container);
        return;
    }
    
    // 각 섹션 HTML 생성
    data.sections.forEach((section) => {
        const sectionHTML = createSectionHTML(section);
        container.insertAdjacentHTML('beforeend', sectionHTML);
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
    // 점수별 응답 분포 생성 (1~5점)
    const responseBarsHTML = [1, 2, 3, 4, 5].map(score => {
        const scoreData = question.scoreStats[score] || { count: 0 };
        return `<div class="response-bar score-${score}">${scoreData.count}</div>`;
    }).join('');
    
    const responsePercentagesHTML = [1, 2, 3, 4, 5].map(score => {
        const scoreData = question.scoreStats[score] || { percentage: 0.0 };
        return `<div class="response-percentage">${scoreData.percentage}%</div>`;
    }).join('');
    
    // 평균 점수에 따른 CSS 클래스 결정
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
    const averageClass = getAverageScoreClass(section.sectionAverage);
    
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
    if (score >= 3.0) return 'fair';
    return 'poor';
}

/**
 * 모달 닫기
 */
function closeDetailModal() {
    document.getElementById('detailModal').style.display = 'none';
}

// ============================================
// 4. 사이드바 및 기타 기능 (기존 코드 유지)
// ============================================

/**
 * 사이드바 초기화
 */
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

/**
 * 메뉴 토글 함수 (기존 함수 유지)
 */
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

// ============================================
// 5. 기존 페이징 이벤트 처리 (호환성 유지)
// ============================================

// 기존 페이징 이벤트 처리
document.querySelectorAll('.pagination .page-link').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        
        if (this.parentElement.classList.contains('disabled')) {
            return;
        }
        
        // 기존 활성 페이지 제거
        document.querySelectorAll('.pagination .page-item').forEach(item => {
            item.classList.remove('active');
        });
        
        // 새로운 활성 페이지 설정 (화살표가 아닌 경우)
        if (!this.classList.contains('page-arrow')) {
            this.parentElement.classList.add('active');
            const pageText = this.textContent.trim();
            console.log(`페이지 ${pageText}로 이동`);
        } else {
            // 화살표 클릭 처리
            const isNext = this.querySelector('.fa-angle-right');
            console.log(isNext ? '다음 페이지' : '이전 페이지');
        }
        
        // 실제 페이지 이동은 서버사이드에서 처리
    });
});

console.log('admin_noncurr_stat.js 로드 완료 - 404 오류 해결 버전');