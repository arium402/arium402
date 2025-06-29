// 비교과 상세 페이지 JavaScript - 읽기 전용 조회 버전

// 전역 변수
let currentProgramId = null;
let isLoading = false;

// 목록으로 이동 함수
function goToList() {
    window.location.href = '/admin/noncurr_list';
}

// 수정 페이지로 이동 함수
function editProgram() {
    // URL에서 프로그램 ID 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id') || currentProgramId;
    
    if (programId) {
        // 수정 페이지로 이동 (프로그램 ID와 함께)
        window.location.href = `/admin/noncurr_edit?id=${programId}`;
        console.log(`프로그램 ID ${programId} 수정 페이지로 이동`);
    } else {
        console.warn('프로그램 ID를 찾을 수 없습니다.');
        alert('프로그램 ID를 찾을 수 없습니다.');
    }
}

// ✅ 프로그램 상세 정보 새로고침 (조회만)
async function refreshProgramInfo() {
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id');
    
    if (!programId) {
        console.warn('프로그램 ID가 없습니다.');
        return;
    }
    
    if (isLoading) return;
    
    try {
        isLoading = true;
        console.log('프로그램 정보 새로고침 시작...');
        
        // ✅ 실제 API 호출로 프로그램 정보 조회 (GET만)
        const response = await fetch(`/api/admin/noncurr_detail_full?id=${programId}&page=0&size=10`);
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            updateProgramDisplay(result.program);
            updateApplicantTable(result.applicants);
            updatePagination(result.pagination);
            
            console.log('프로그램 정보 새로고침 완료');
        } else {
            console.error('프로그램 정보 조회 실패:', result.error);
        }
        
    } catch (error) {
        console.error('프로그램 정보 새로고침 오류:', error);
    } finally {
        isLoading = false;
    }
}

// ✅ 프로그램 화면 표시 업데이트 (읽기 전용)
function updateProgramDisplay(program) {
    if (!program) return;
    
    // 프로그램 기본 정보 업데이트
    const elements = {
        programName: program.prgNm,
        recruitPeriod: `${program.recruitStDt} ~ ${program.recruitEndDt}`,
        recruitCount: `${program.currentCnt || 0}/${program.maxCnt || 0}명`,
        operationPeriod: `${program.prgStDt} ~ ${program.prgEndDt}`,
        department: program.prgDept,
        contact: program.prgTel,
        mileage: `${program.mlgDefScore || 0}M`,
        competencies: program.competencyNames || '핵심역량 정보 없음'
    };
    
    // DOM 업데이트
    Object.entries(elements).forEach(([id, value]) => {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value || '';
        }
    });
    
    // 프로그램 상태 업데이트 (CSS 클래스 포함)
    const statusElement = document.getElementById('programStatus');
    if (statusElement && program.prgStatNm) {
        statusElement.textContent = program.prgStatNm;
        statusElement.className = `status ${program.prgStatNm.toLowerCase().replace(/\s+/g, '-')}`;
    }
    
    // 프로그램 이미지 업데이트
    updateProgramImage(program.imageUrl);
}

// 프로그램 이미지 업데이트
function updateProgramImage(imageUrl) {
    const imageContainer = document.getElementById('programImage');
    if (!imageContainer) return;

    if (imageUrl) {
        imageContainer.innerHTML = `<img src="${imageUrl}" alt="프로그램 이미지" style="width: 100%; height: 100%; object-fit: cover;">`;
    } else {
        imageContainer.innerHTML = '<span class="no-image">프로그램 이미지</span>';
    }
}

// ✅ 신청자 테이블 업데이트 (읽기 전용)
function updateApplicantTable(applicants) {
    const tableBody = document.getElementById('applicantTableBody');
    if (!tableBody) return;

    if (!applicants || applicants.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 40px; color: #6c757d;">
                    신청자가 없습니다.
                </td>
            </tr>
        `;
        return;
    }

    // ✅ 클릭 이벤트 없는 단순 표시용 테이블
    tableBody.innerHTML = applicants.map((applicant, index) => `
        <tr>
            <td>${index + 1}</td>
            <td>${applicant.studentId || '학번없음'}</td>
            <td>${applicant.name || '이름없음'}</td>
            <td>${applicant.department || '학과없음'}</td>
            <td>
                <span class="yn-badge ${applicant.completed ? 'yes' : 'no'}">
                    ${applicant.completed ? 'Y' : 'N'}
                </span>
            </td>
            <td>
                <span class="yn-badge ${applicant.surveyCompleted ? 'yes' : 'no'}">
                    ${applicant.surveyCompleted ? 'Y' : 'N'}
                </span>
            </td>
        </tr>
    `).join('');
}

// ✅ 페이지네이션 업데이트
function updatePagination(paginationData) {
    if (!paginationData) return;
    
    const paginationEl = document.getElementById('pagination');
    const paginationInfoEl = document.getElementById('paginationInfo');
    
    if (!paginationEl) return;

    const { currentPage, totalPages, totalElements, startRecord, endRecord } = paginationData;
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id');

    // 페이지네이션 버튼 생성
    let paginationHTML = '';
    
    // 이전 페이지
    paginationHTML += `
        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link page-arrow" href="${currentPage > 1 ? `/admin/noncurr_detail?id=${programId}&page=${currentPage - 1}` : '#'}" aria-label="Previous">
                <i class="fas fa-angle-left"></i>
            </a>
        </li>
    `;

    // 페이지 번호들
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="/admin/noncurr_detail?id=${programId}&page=${i}">${i}</a>
            </li>
        `;
    }

    // 다음 페이지
    paginationHTML += `
        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link page-arrow" href="${currentPage < totalPages ? `/admin/noncurr_detail?id=${programId}&page=${currentPage + 1}` : '#'}" aria-label="Next">
                <i class="fas fa-angle-right"></i>
            </a>
        </li>
    `;

    paginationEl.innerHTML = paginationHTML;

    // 페이지네이션 정보 업데이트
    if (paginationInfoEl) {
        paginationInfoEl.innerHTML = `총 ${totalElements}건 중 ${startRecord}-${endRecord}건 표시`;
    }
}

// ✅ 신청자 통계 조회 (조회만)
async function loadApplicantStatistics() {
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id');
    
    if (!programId) return;
    
    try {
        const response = await fetch(`/api/admin/noncurr_applicant_stats?prgId=${programId}`);
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            console.log('신청자 통계:', result.data);
            // 필요시 통계 정보 UI에 표시
            updateStatisticsDisplay(result.data);
        } else {
            console.warn('통계 조회 실패:', result.error);
        }
        
    } catch (error) {
        console.error('통계 조회 오류:', error);
    }
}

// ✅ 통계 정보 표시 (필요시 구현)
function updateStatisticsDisplay(stats) {
    // 통계 정보를 UI에 표시하는 로직 (추후 확장 가능)
    console.log('통계 정보:', stats);
}

// ✅ URL 파라미터에서 프로그램 정보 추출
function loadProgramInfo() {
    // URL에서 프로그램 ID를 가져와서 전역 변수에 저장
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id');
    
    if (programId) {
        currentProgramId = programId;
        console.log('프로그램 ID 확인:', programId);
        
        // 필요시 추가 정보 로드
        // refreshProgramInfo(); // 서버렌더링으로 이미 데이터가 있으니 불필요
    } else {
        console.warn('프로그램 ID가 제공되지 않았습니다.');
    }
}

// ✅ 페이지 로드시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // URL에서 프로그램 ID 추출
    loadProgramInfo();
    
    // 초기 통계 로드 (선택사항)
    loadApplicantStatistics();
    
    console.log('읽기 전용 비교과 상세 페이지 로드 완료');
    
    // URL 파라미터 변경 감지 (뒤로가기/앞으로가기 대응)
    window.addEventListener('popstate', function() {
        loadProgramInfo();
    });
});

// ✅ 공통 JavaScript 로드 후 실행
if (typeof activateMenuByCurrentUrl === 'function') {
    activateMenuByCurrentUrl();
}

// ✅ 에러 처리
window.addEventListener('unhandledrejection', function(event) {
    console.error('처리되지 않은 Promise 오류:', event.reason);
});

window.addEventListener('error', function(event) {
    console.error('JavaScript 오류:', event.error);
});

// ✅ 페이지 새로고침 함수 (필요시 사용)
function reloadPage() {
    window.location.reload();
}

// ✅ 특정 페이지로 이동하는 함수 (페이지네이션에서 사용 가능)
function goToPage(pageNumber) {
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id');
    
    if (programId && pageNumber > 0) {
        window.location.href = `/admin/noncurr_detail?id=${programId}&page=${pageNumber}`;
    }
}