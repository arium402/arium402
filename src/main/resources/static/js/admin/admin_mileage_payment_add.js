// 마일리지 지급 상세 페이지 JavaScript

// 전역 변수
let programId = null;
let programInfo = null;
let applicantData = [];
let isLoading = false;

// API 엔드포인트
const API_ENDPOINTS = {
    participants: '/api/admin/mileage_participants',
    payment: '/api/admin/mileage_payment',
    validate: '/api/admin/mileage_validate'
};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('마일리지 지급 상세 페이지 초기화');
    
    // URL에서 프로그램 ID 추출
    const urlParams = new URLSearchParams(window.location.search);
    programId = parseInt(urlParams.get('prgId'));
    
    if (!programId) {
        alert('프로그램 ID가 유효하지 않습니다.');
        goToList();
        return;
    }
    
    // 데이터 로드
    loadProgramData();
});

// 프로그램 데이터 로드
async function loadProgramData() {
    if (isLoading) return;
    
    try {
        isLoading = true;
        
        console.log('프로그램 데이터 요청:', `${API_ENDPOINTS.participants}/${programId}`);
        
        const response = await fetch(`${API_ENDPOINTS.participants}/${programId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const data = await response.json();
        console.log('프로그램 데이터 응답:', data);
        
        if (data.success) {
            programInfo = data.program;
            applicantData = data.participants || [];
            
            // 화면 업데이트
            updateProgramInfo(programInfo);
            renderTable();
            
        } else {
            throw new Error(data.error || '데이터 로드 실패');
        }
        
    } catch (error) {
        console.error('프로그램 데이터 로드 실패:', error);
        alert(`데이터를 불러오는데 실패했습니다: ${error.message}`);
        
        // 에러 시 빈 데이터로 설정
        applicantData = [];
        renderTable();
        
    } finally {
        isLoading = false;
    }
}

// 프로그램 정보 업데이트
function updateProgramInfo(program) {
    if (!program) return;
    
    // 프로그램명
    const titleElement = document.getElementById('programTitle');
    if (titleElement) {
        titleElement.textContent = program.prgNm || '프로그램명 없음';
    }
    
    // 운영기간
    const periodElement = document.getElementById('programPeriod');
    if (periodElement) {
        const formattedPeriod = formatProgramPeriod(program.prgStDt, program.prgEndDt);
        periodElement.textContent = formattedPeriod;
    }
    
    // 인원 정보
    const participantsElement = document.getElementById('programParticipants');
    if (participantsElement) {
        const completedCount = applicantData.filter(a => a.completed && a.surveyCompleted).length;
        participantsElement.textContent = `인원 ${completedCount}명`;
    }
    
    // 마일리지 점수
    const mileageElement = document.getElementById('programMileage');
    if (mileageElement) {
        mileageElement.textContent = `마일리지 ${program.mlgDefScore || 0}점`;
    }
    
    // 제목 업데이트
    const listTitleElement = document.getElementById('applicantListTitle');
    if (listTitleElement) {
        const eligibleCount = applicantData.filter(a => 
            a.completed && a.surveyCompleted && a.statusBadgeClass !== 'success'
        ).length;
        listTitleElement.textContent = `신청자 리스트 (지급대상: ${eligibleCount}명)`;
    }
}

// 기간 포맷팅
function formatProgramPeriod(startDt, endDt) {
    if (!startDt || !endDt) return '기간 정보 없음';
    
    let start = startDt.length >= 10 ? startDt.substring(0, 10) : startDt;
    let end = endDt.length >= 10 ? endDt.substring(0, 10) : endDt;
    
    // 하이픈을 점으로 변경
    start = start.replace(/-/g, '.');
    end = end.replace(/-/g, '.');
    
    return `${start} ~ ${end}`;
}

// Y/N 배지 렌더링
function getYNBadge(value) {
    if (value === true || value === 'Y') {
        return '<span class="yn-badge yes">Y</span>';
    } else {
        return '<span class="yn-badge no">N</span>';
    }
}

// 지급 버튼 렌더링
function getPaymentButton(applicant) {
    const isCompleted = applicant.completed;
    const isSurveyCompleted = applicant.surveyCompleted;
    const isAlreadyPaid = applicant.statusBadgeClass === 'success';
    
    if (isCompleted && isSurveyCompleted) {
        if (isAlreadyPaid) {
            return '<button class="payment-btn completed">완료</button>';
        } else {
            return `<button class="payment-btn waiting" onclick="changePaymentStatus(${applicant.cmpId})">지급대기</button>`;
        }
    }
    return '<button class="payment-btn disabled">-</button>';
}

// 테이블 렌더링
function renderTable() {
    const tableBody = document.getElementById('applicantTableBody');
    if (!tableBody) return;
    
    tableBody.innerHTML = '';
    
    if (!applicantData || applicantData.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 20px;">
                    <i class="fas fa-user-slash"></i> 지급 대상자가 없습니다.
                </td>
            </tr>
        `;
        return;
    }
    
    applicantData.forEach((applicant, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="col-no">${index + 1}</td>
            <td class="col-student-id">${applicant.studentId || ''}</td>
            <td class="col-name">${applicant.name || '이름없음'}</td>
            <td class="col-department">${applicant.department || '학과정보없음'}</td>
            <td class="col-completion">${getYNBadge(applicant.completed)}</td>
            <td class="col-satisfaction">${getYNBadge(applicant.surveyCompleted)}</td>
            <td class="col-payment">${getPaymentButton(applicant)}</td>
        `;
        tableBody.appendChild(row);
    });
    
    // 전체 지급 버튼 상태 업데이트
    updateBulkPaymentButton();
}

// 전체 지급 버튼 상태 업데이트
function updateBulkPaymentButton() {
    const bulkBtn = document.getElementById('bulkPaymentBtn');
    if (!bulkBtn) return;
    
    const eligibleCount = applicantData.filter(a => 
        a.completed && a.surveyCompleted && a.statusBadgeClass !== 'success'
    ).length;
    
    if (eligibleCount === 0) {
        bulkBtn.disabled = true;
        bulkBtn.innerHTML = '<i class="fas fa-coins"></i> 지급대상 없음';
    } else {
        bulkBtn.disabled = false;
        bulkBtn.innerHTML = `<i class="fas fa-coins"></i> 전체 지급 (${eligibleCount}명)`;
    }
}

// 개별 지급 상태 변경
async function changePaymentStatus(cmpId) {
    const applicant = applicantData.find(a => a.cmpId === cmpId);
    if (!applicant) return;
    
    if (!confirm(`${applicant.name} 학생에게 마일리지를 지급하시겠습니까?`)) {
        return;
    }
    
    try {
        const result = await processPayment([cmpId]);
        if (result.success) {
            alert('마일리지가 성공적으로 지급되었습니다.');
            // 데이터 새로고침
            await loadProgramData();
        } else {
            throw new Error(result.error);
        }
    } catch (error) {
        console.error('개별 지급 실패:', error);
        alert(`마일리지 지급에 실패했습니다: ${error.message}`);
    }
}

// 전체 지급
async function bulkPayment() {
    const eligibleApplicants = applicantData.filter(a => 
        a.completed && a.surveyCompleted && a.statusBadgeClass !== 'success'
    );
    
    if (eligibleApplicants.length === 0) {
        alert('지급 대기 중인 대상자가 없습니다.');
        return;
    }
    
    const confirmMessage = `총 ${eligibleApplicants.length}명에게 마일리지를 지급하시겠습니까?\n\n` +
                          `대상자: ${eligibleApplicants.map(a => a.name).join(', ')}`;
    
    if (!confirm(confirmMessage)) {
        return;
    }
    
    try {
        const participantIds = eligibleApplicants.map(a => a.cmpId);
        const result = await processPayment(participantIds);
        
        if (result.success) {
            const successCount = result.result.successCount || 0;
            const failCount = result.result.failCount || 0;
            alert(`마일리지 지급이 완료되었습니다.\n성공: ${successCount}명, 실패: ${failCount}명`);
            
            // 데이터 새로고침
            await loadProgramData();
        } else {
            throw new Error(result.error);
        }
    } catch (error) {
        console.error('전체 지급 실패:', error);
        alert(`마일리지 지급에 실패했습니다: ${error.message}`);
    }
}

// 마일리지 지급 처리
async function processPayment(participantIds) {
    const response = await fetch(API_ENDPOINTS.payment, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({
            prgId: programId,
            participantIds: participantIds,
            paymentDate: new Date().toISOString().split('T')[0]
        })
    });
    
    if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    
    return await response.json();
}

// 목록으로 돌아가기
function goToList() {
    
        window.location.href = '/admin/admin_mileage_payment';
    
}