// 비교과 상세 페이지 JavaScript

// 목록으로 이동 함수
function goToList() {
    window.location.href = '/admin/noncurr_list';
}

// 수정 페이지로 이동 함수
function editProgram() {
    // URL에서 프로그램 ID 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id') || getDefaultProgramId();
    
    // 수정 페이지로 이동 (프로그램 ID와 함께)
    window.location.href = '/admin/noncurr_edit';
    
    console.log(`프로그램 ID ${programId} 수정 페이지로 이동`);
}

// 기본 프로그램 ID 반환 (임시용)
function getDefaultProgramId() {
    // 실제로는 현재 페이지의 프로그램 데이터에서 가져와야 함
    return '1';
}

// URL 파라미터에서 프로그램 정보 가져오기
function loadProgramInfo() {
    // URL에서 프로그램 ID를 가져와서 해당 정보를 로드
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id');
    
    if (programId) {
        console.log('프로그램 ID:', programId);
        // 실제로는 서버에서 프로그램 정보를 가져올 것
        // loadProgramData(programId);
    } else {
        console.warn('프로그램 ID가 제공되지 않았습니다.');
    }
}

// 프로그램 데이터 로드 (실제 구현시)
async function loadProgramData(programId) {
    try {
        // 실제로는 서버 API 호출
        // const response = await fetch(`/api/admin/noncurr/${programId}`);
        // const data = await response.json();
        // updateProgramDisplay(data);
        
        console.log(`프로그램 ID ${programId}의 데이터를 로드했습니다.`);
    } catch (error) {
        console.error('프로그램 데이터 로드 중 오류 발생:', error);
    }
}

// 프로그램 화면 표시 업데이트
function updateProgramDisplay(data) {
    // 프로그램 기본 정보 업데이트
    const programNameEl = document.getElementById('programName');
    const recruitPeriodEl = document.getElementById('recruitPeriod');
    const recruitCountEl = document.getElementById('recruitCount');
    const operationPeriodEl = document.getElementById('operationPeriod');
    const programStatusEl = document.getElementById('programStatus');
    const departmentEl = document.getElementById('department');
    const contactEl = document.getElementById('contact');
    const mileageEl = document.getElementById('mileage');
    const competenciesEl = document.getElementById('competencies');

    if (programNameEl) programNameEl.textContent = data.programName || '';
    if (recruitPeriodEl) recruitPeriodEl.textContent = `${data.recruitStartDate} ~ ${data.recruitEndDate}` || '';
    if (recruitCountEl) recruitCountEl.textContent = `${data.applicantCount}/${data.capacity}명` || '';
    if (operationPeriodEl) operationPeriodEl.textContent = `${data.operationStartDate} ~ ${data.operationEndDate}` || '';
    if (programStatusEl) {
        programStatusEl.textContent = data.statusText || '';
        programStatusEl.className = `status ${data.status || ''}`;
    }
    if (departmentEl) departmentEl.textContent = data.department || '';
    if (contactEl) contactEl.textContent = data.contact || '';
    if (mileageEl) mileageEl.textContent = `${data.mileagePoints}M` || '';
    if (competenciesEl) competenciesEl.textContent = data.competencies || '';

    // 프로그램 이미지 업데이트
    updateProgramImage(data.imagePath);
}

// 프로그램 이미지 업데이트
function updateProgramImage(imagePath) {
    const imageContainer = document.getElementById('programImage');
    if (!imageContainer) return;

    if (imagePath) {
        imageContainer.innerHTML = `<img src="${imagePath}" alt="프로그램 이미지" style="width: 100%; height: 100%; object-fit: cover;">`;
    } else {
        imageContainer.innerHTML = '프로그램 이미지';
    }
}

// 신청자 목록 로드
async function loadApplicants(page = 1) {
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const programId = urlParams.get('id');
        
        if (!programId) {
            console.error('프로그램 ID가 없습니다.');
            return;
        }

        // 실제로는 서버 API 호출
        // const response = await fetch(`/api/admin/noncurr/${programId}/applicants?page=${page}`);
        // const data = await response.json();
        // updateApplicantTable(data.applicants);
        // updatePagination(data.pagination);
        
        console.log(`프로그램 ID ${programId}의 ${page}페이지 신청자 목록을 로드했습니다.`);
    } catch (error) {
        console.error('신청자 목록 로드 중 오류 발생:', error);
    }
}

// 신청자 테이블 업데이트
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

    tableBody.innerHTML = applicants.map((applicant, index) => `
        <tr>
            <td>${index + 1}</td>
            <td>${applicant.studentId}</td>
            <td>${applicant.name}</td>
            <td>${applicant.department}</td>
            <td><span class="yn-badge ${applicant.completed ? 'yes' : 'no'}">${applicant.completed ? 'Y' : 'N'}</span></td>
            <td><span class="yn-badge ${applicant.surveyCompleted ? 'yes' : 'no'}">${applicant.surveyCompleted ? 'Y' : 'N'}</span></td>
        </tr>
    `).join('');
}

// 페이지네이션 업데이트
function updatePagination(paginationData) {
    const paginationEl = document.getElementById('pagination');
    const paginationInfoEl = document.getElementById('paginationInfo');
    
    if (!paginationEl || !paginationData) return;

    const { currentPage, totalPages, totalCount, startRecord, endRecord } = paginationData;
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
        paginationInfoEl.innerHTML = `총 ${totalCount}건 중 ${startRecord}-${endRecord}건 표시`;
    }
}

// 페이지네이션 클릭 이벤트 처리 (정적 HTML용)
function setupPaginationEvents() {
    document.addEventListener('click', function(e) {
        // 페이지네이션 링크 클릭 처리
        if (e.target.closest('.pagination .page-link')) {
            const link = e.target.closest('.pagination .page-link');
            const listItem = link.parentElement;
            
            // 비활성화된 링크는 무시
            if (listItem.classList.contains('disabled')) {
                e.preventDefault();
                return;
            }
            
            // 화살표가 아닌 경우 (페이지 번호)
            if (!link.classList.contains('page-arrow')) {
                const pageText = link.textContent.trim();
                console.log(`페이지 ${pageText}로 이동`);
                
                // 활성 상태 업데이트 (임시)
                document.querySelectorAll('.pagination .page-item').forEach(item => {
                    item.classList.remove('active');
                });
                listItem.classList.add('active');
            } else {
                // 화살표 클릭 처리
                const isNext = link.querySelector('.fa-angle-right');
                console.log(isNext ? '다음 페이지' : '이전 페이지');
            }
        }
    });
}

// 이수여부/만족도조사 토글 (관리자가 직접 수정할 수 있는 경우)
function toggleCompletionStatus(studentId, type) {
    // type: 'completion' 또는 'survey'
    console.log(`학생 ID ${studentId}의 ${type} 상태를 토글합니다.`);
    
    // 실제로는 서버에 업데이트 요청
    // updateStudentStatus(studentId, type);
}

// 학생 상태 업데이트 (실제 구현시)
async function updateStudentStatus(studentId, type) {
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const programId = urlParams.get('id');
        
        // const response = await fetch(`/api/admin/noncurr/${programId}/student/${studentId}/${type}`, {
        //     method: 'PUT',
        //     headers: { 'Content-Type': 'application/json' }
        // });
        
        // if (response.ok) {
        //     // 테이블 새로고침
        //     loadApplicants();
        // }
        
        console.log(`학생 상태가 업데이트되었습니다.`);
    } catch (error) {
        console.error('학생 상태 업데이트 중 오류 발생:', error);
    }
}

// 페이지 로드시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 프로그램 정보 로드
    loadProgramInfo();
    
    // 페이지네이션 이벤트 설정 (정적 HTML용)
    setupPaginationEvents();
    
    // URL 파라미터 변경 감지 (뒤로가기/앞으로가기 대응)
    window.addEventListener('popstate', function() {
        loadProgramInfo();
    });
});

// 공통 JavaScript 로드 후 실행
if (typeof activateMenuByCurrentUrl === 'function') {
    activateMenuByCurrentUrl();
}