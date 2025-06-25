// 비교과 등록 페이지 JavaScript - API 연동 버전 (DB 컬럼명 통일)

// 전역 변수
let isSubmitting = false;

// 페이지 로드시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 이벤트 리스너 설정
    initializeEvents();
    
    // 날짜 입력 필드 최소값 설정
    setDateInputMinValues();
    
    // 텍스트 에디터 자동 높이 조절
    initializeTextareaAutoResize();
});

// 텍스트 에디터 자동 높이 조절 (원본 기능 추가)
function initializeTextareaAutoResize() {
    const contentEditor = document.getElementById('prgDesc');
    if (contentEditor) {
        contentEditor.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    }
}

// 이벤트 리스너 초기화
function initializeEvents() {
    // 폼 제출 이벤트
    const programForm = document.getElementById('programForm');
    if (programForm) {
        programForm.addEventListener('submit', handleFormSubmit);
    }
    
    // 대표 사진 업로드 이벤트
    const programImageInput = document.getElementById('programImage');
    if (programImageInput) {
        programImageInput.addEventListener('change', function() {
            handleImageSelect(this);
        });
    }
    
    // 숫자 입력 필드 최소값 검증
    initializeNumericInputs();
}

// 숫자 입력 필드 초기화
function initializeNumericInputs() {
    const capacityInput = document.getElementById('maxCnt');
    if (capacityInput) {
        capacityInput.addEventListener('input', function() {
            if (this.value < 1) this.value = 1;
        });
    }
    
    const mileageInput = document.getElementById('mlgDefScore');
    if (mileageInput) {
        mileageInput.addEventListener('input', function() {
            if (this.value < 0) this.value = 0;
        });
    }
}

// 폼 제출 처리 (원본 스타일 참고)
async function handleFormSubmit(event) {
    event.preventDefault();
    
    if (isSubmitting) {
        console.log('이미 제출 중입니다.');
        return;
    }
    
    try {
        // 폼 유효성 검사
        if (!validateFormData()) {
            return;
        }
        
        // 제출 확인
        if (!confirm('비교과 프로그램을 등록하시겠습니까?')) {
            return;
        }
        
        isSubmitting = true;
        showSubmitLoading();
        
        // 폼 데이터 수집
        const formData = collectFormData();
        console.log('등록할 데이터:', formData);
        
        // 첨부파일 정보 수집
        const attachedFiles = getAttachedFilesList();
        console.log('첨부파일 목록:', attachedFiles);
        
        // API 호출을 위한 FormData 생성
        const apiFormData = createApiFormData(formData, attachedFiles);
        
        // API 호출
        const response = await fetch('/api/admin/noncurr/add', {
            method: 'POST',
            body: apiFormData
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('비교과 프로그램이 성공적으로 등록되었습니다.');
            console.log('등록 완료 - 프로그램 ID:', result.programId);
            
            // 목록 페이지로 이동
            window.location.href = '/admin/noncurr_list';
        } else {
            throw new Error(result.message || '등록에 실패했습니다.');
        }
        
    } catch (error) {
        console.error('프로그램 등록 실패:', error);
        alert('프로그램 등록 중 오류가 발생했습니다: ' + error.message);
    } finally {
        isSubmitting = false;
        hideSubmitLoading();
    }
}

// 폼 데이터 수집 (DB 컬럼명 기준)
function collectFormData() {
    const prgNm = document.getElementById('prgNm').value.trim();
    const prgDesc = document.getElementById('prgDesc').value.trim();
    const recruitStart = document.getElementById('recruitStart').value;
    const recruitEnd = document.getElementById('recruitEnd').value;
    const prgStDt = document.getElementById('prgStDt').value;
    const prgEndDt = document.getElementById('prgEndDt').value;
    const maxCnt = document.getElementById('maxCnt').value;
    const department = document.getElementById('department').value;
    const contact = document.getElementById('contact').value.trim();
    const surveyDt = document.getElementById('surveyDt').value;
    const mlgDefScore = document.getElementById('mlgDefScore').value;
    
    return {
        prgNm: prgNm,
        prgDesc: prgDesc,
        recruitStart: recruitStart,
        recruitEnd: recruitEnd,
        prgStDt: prgStDt,
        prgEndDt: prgEndDt,
        maxCnt: maxCnt,
        department: department,
        contact: contact,
        surveyDt: surveyDt,
        mlgDefScore: mlgDefScore
    };
}

// 첨부파일 목록 수집
function getAttachedFilesList() {
    const attachmentFileInput = document.getElementById('attachmentFile');
    if (attachmentFileInput && attachmentFileInput.files.length > 0) {
        return [attachmentFileInput.files[0].name];
    }
    return [];
}

// 선택된 핵심역량 가져오기
function getSelectedCompetencies() {
    const selectedCheckboxes = document.querySelectorAll('input[name="competencies"]:checked');
    return Array.from(selectedCheckboxes).map(checkbox => checkbox.value);
}

// API 호출용 FormData 생성
function createApiFormData(formData, attachedFiles) {
    const apiFormData = new FormData();
    
    // 기본 정보 추가 (DB 컬럼명 기준)
    Object.keys(formData).forEach(key => {
        apiFormData.append(key, formData[key]);
    });
    
    // 선택된 핵심역량 처리
    const selectedCompetencies = getSelectedCompetencies();
    if (selectedCompetencies.length > 0) {
        apiFormData.append('selectedCompetencies', selectedCompetencies.join(','));
    }
    
    // 프로그램 이미지 파일 추가
    const programImageInput = document.getElementById('programImage');
    if (programImageInput && programImageInput.files.length > 0) {
        apiFormData.append('programImage', programImageInput.files[0]);
    }
    
    // 첨부파일 추가
    const attachmentFileInput = document.getElementById('attachmentFile');
    if (attachmentFileInput && attachmentFileInput.files.length > 0) {
        apiFormData.append('attachmentFile', attachmentFileInput.files[0]);
    }
    
    return apiFormData;
}

// 폼 유효성 검사 (DB 컬럼명 기준)
function validateFormData() {
    const prgNm = document.getElementById('prgNm').value.trim();
    const prgDesc = document.getElementById('prgDesc').value.trim();
    const recruitStart = document.getElementById('recruitStart').value;
    const recruitEnd = document.getElementById('recruitEnd').value;
    const prgStDt = document.getElementById('prgStDt').value;
    const prgEndDt = document.getElementById('prgEndDt').value;
    const maxCnt = document.getElementById('maxCnt').value;
    const department = document.getElementById('department').value;
    const contact = document.getElementById('contact').value.trim();
    const surveyDt = document.getElementById('surveyDt').value;
    const mlgDefScore = document.getElementById('mlgDefScore').value;
    
    if (!prgNm) {
        alert('프로그램명을 입력하세요.');
        document.getElementById('prgNm').focus();
        return false;
    }
    
    if (!prgDesc) {
        alert('프로그램 설명을 입력하세요.');
        document.getElementById('prgDesc').focus();
        return false;
    }
    
    if (!recruitStart) {
        alert('모집 시작일을 입력하세요.');
        document.getElementById('recruitStart').focus();
        return false;
    }
    
    if (!recruitEnd) {
        alert('모집 마감일을 입력하세요.');
        document.getElementById('recruitEnd').focus();
        return false;
    }
    
    if (!prgStDt) {
        alert('운영 시작일을 입력하세요.');
        document.getElementById('prgStDt').focus();
        return false;
    }
    
    if (!prgEndDt) {
        alert('운영 종료일을 입력하세요.');
        document.getElementById('prgEndDt').focus();
        return false;
    }
    
    if (!maxCnt) {
        alert('모집인원을 입력하세요.');
        document.getElementById('maxCnt').focus();
        return false;
    }
    
    if (!department) {
        alert('운영부서를 선택하세요.');
        document.getElementById('department').focus();
        return false;
    }
    
    if (!contact) {
        alert('문의 전화번호를 입력하세요.');
        document.getElementById('contact').focus();
        return false;
    }
    
    if (!surveyDt) {
        alert('만족도조사 마감일을 입력하세요.');
        document.getElementById('surveyDt').focus();
        return false;
    }
    
    if (!mlgDefScore) {
        alert('마일리지 점수를 입력하세요.');
        document.getElementById('mlgDefScore').focus();
        return false;
    }
    
    // 핵심역량 선택 검사
    const selectedCompetencies = getSelectedCompetencies();
    if (selectedCompetencies.length === 0) {
        alert('핵심역량을 하나 이상 선택해주세요.');
        return false;
    }
    
    // 날짜 유효성 검사
    if (!validateDates()) {
        return false;
    }
    
    // 숫자 필드 검사
    const capacityNum = parseInt(maxCnt);
    if (capacityNum < 1) {
        alert('모집인원은 1명 이상이어야 합니다.');
        document.getElementById('maxCnt').focus();
        return false;
    }
    
    const mileageNum = parseInt(mlgDefScore);
    if (mileageNum < 0) {
        alert('마일리지 점수는 0 이상이어야 합니다.');
        document.getElementById('mlgDefScore').focus();
        return false;
    }
    
    return true;
}

// 날짜 유효성 검사 (DB 컬럼명 기준)
function validateDates() {
    const recruitStart = new Date(document.getElementById('recruitStart').value);
    const recruitEnd = new Date(document.getElementById('recruitEnd').value);
    const prgStDt = new Date(document.getElementById('prgStDt').value);
    const prgEndDt = new Date(document.getElementById('prgEndDt').value);
    const surveyDt = new Date(document.getElementById('surveyDt').value);
    
    if (recruitStart >= recruitEnd) {
        alert('모집 시작일은 모집 마감일보다 이전이어야 합니다.');
        return false;
    }
    
    if (prgStDt >= prgEndDt) {
        alert('운영 시작일은 운영 종료일보다 이전이어야 합니다.');
        return false;
    }
    
    if (recruitEnd > prgStDt) {
        alert('모집 마감일은 운영 시작일과 같거나 이전이어야 합니다.');
        return false;
    }
    
    if (surveyDt < prgEndDt) {
        alert('만족도조사 마감일은 운영 종료일과 같거나 이후여야 합니다.');
        return false;
    }
    
    return true;
}

// 대표 사진 선택 처리 (기존 로직 유지)
function handleImageSelect(input) {
    const file = input.files[0];
    const uploadArea = input.parentElement;
    const uploadContent = uploadArea.querySelector('.image-upload-content');
    
    if (file) {
        // 파일 크기 검사 (5MB)
        if (file.size > 5 * 1024 * 1024) {
            alert('파일 크기는 5MB 이하여야 합니다.');
            input.value = '';
            return;
        }
        
        // 파일 형식 검사
        if (!file.type.startsWith('image/')) {
            alert('이미지 파일만 업로드 가능합니다.');
            input.value = '';
            return;
        }
        
        // 기존 미리보기 제거
        const existingPreview = uploadArea.querySelector('.image-preview');
        if (existingPreview) {
            existingPreview.remove();
        }
        
        // 새 미리보기 생성
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.alt = '대표 사진';
            img.className = 'image-preview';
            img.style.cssText = 'width: 100%; height: 100%; object-fit: cover; border-radius: 8px;';
            
            uploadContent.style.display = 'none';
            uploadArea.appendChild(img);
        };
        reader.readAsDataURL(file);
        
    } else {
        // 파일 제거시 원래 상태로
        const existingPreview = uploadArea.querySelector('.image-preview');
        if (existingPreview) {
            existingPreview.remove();
        }
        uploadContent.style.display = 'flex';
    }
}

// 첨부파일 선택 처리
function handleFileSelect(input) {
    const file = input.files[0];
    const filePreview = document.getElementById('filePreview');
    
    if (file) {
        // 파일 유효성 검사
        if (!validateAttachmentFile(file)) {
            input.value = '';
            return;
        }
        
        // 파일 미리보기 표시
        filePreview.innerHTML = `
            <div class="file-item" style="display: flex; justify-content: space-between; align-items: center; padding: 8px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 4px; background: #f8f9fa;">
                <span class="file-name" style="flex: 1; margin-right: 10px;">${escapeHtml(file.name)}</span>
                <button type="button" class="file-delete" onclick="deleteAttachmentFile(this)" style="padding: 4px 8px; background: #dc3545; color: white; border: none; border-radius: 4px; cursor: pointer;">삭제</button>
            </div>
        `;
        filePreview.style.display = 'block';
        
    } else {
        // 파일이 없으면 미리보기 숨김
        filePreview.style.display = 'none';
        filePreview.innerHTML = '';
    }
}

// 첨부파일 유효성 검사
function validateAttachmentFile(file) {
    // 파일 크기 검사 (10MB)
    if (file.size > 10 * 1024 * 1024) {
        alert(`파일 "${file.name}"의 크기가 10MB를 초과합니다.`);
        return false;
    }
    
    // 허용된 파일 형식 검사
    const allowedExtensions = ['.pdf', '.doc', '.docx', '.hwp', '.jpg', '.jpeg', '.png'];
    const fileExtension = getFileExtension(file.name).toLowerCase();
    
    if (!allowedExtensions.includes(fileExtension)) {
        alert(`파일 "${file.name}"은 허용되지 않는 형식입니다.\n허용 형식: PDF, DOC, DOCX, HWP, JPG, PNG`);
        return false;
    }
    
    return true;
}

// 첨부파일 삭제 함수
function deleteAttachmentFile(button) {
    if (confirm('이 파일을 삭제하시겠습니까?')) {
        const filePreview = document.getElementById('filePreview');
        const attachmentFileInput = document.getElementById('attachmentFile');
        
        // 파일 입력 필드 초기화
        attachmentFileInput.value = '';
        
        // 미리보기 숨김
        filePreview.style.display = 'none';
        filePreview.innerHTML = '';
    }
}

// 파일 확장자 추출 함수
function getFileExtension(filename) {
    if (!filename || filename.lastIndexOf('.') === -1) {
        return '';
    }
    return filename.substring(filename.lastIndexOf('.'));
}

// 미리보기 표시 (DB 컬럼명 기준)
function showPreview() {
    try {
        // 폼 데이터 수집
        const prgNm = document.getElementById('prgNm').value.trim() || '프로그램명';
        const recruitStart = document.getElementById('recruitStart').value;
        const recruitEnd = document.getElementById('recruitEnd').value;
        const department = document.getElementById('department').value || '-';
        const contact = document.getElementById('contact').value.trim() || '-';
        const mlgDefScore = document.getElementById('mlgDefScore').value || '0';
        const prgDesc = document.getElementById('prgDesc').value.trim() || '프로그램 설명이 없습니다.';
        
        // 미리보기 데이터 설정
        document.getElementById('previewTitle').textContent = prgNm;
        document.getElementById('previewRecruitPeriod').textContent = 
            recruitStart && recruitEnd ? `${recruitStart} ~ ${recruitEnd}` : '-';
        document.getElementById('previewDepartment').textContent = department;
        document.getElementById('previewContact').textContent = contact;
        document.getElementById('previewMileage').textContent = `${parseInt(mlgDefScore).toLocaleString()} 포인트`;
        document.getElementById('previewDescription').textContent = prgDesc;
        
        // 대표 사진 미리보기
        const previewImage = document.getElementById('previewImage');
        const programImageInput = document.getElementById('programImage');
        
        if (programImageInput && programImageInput.files && programImageInput.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                previewImage.innerHTML = `<img src="${e.target.result}" alt="프로그램 대표 사진" style="width: 100%; height: 100%; object-fit: cover;">`;
            };
            reader.readAsDataURL(programImageInput.files[0]);
        } else {
            previewImage.innerHTML = '대표 사진 미리보기';
        }
        
        // 모달 표시
        document.getElementById('previewModal').style.display = 'block';
        
    } catch (error) {
        console.error('미리보기 생성 실패:', error);
        alert('미리보기를 생성할 수 없습니다.');
    }
}

// 미리보기 닫기
function closePreview() {
    document.getElementById('previewModal').style.display = 'none';
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const modal = document.getElementById('previewModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}

// 폼 초기화
function resetForm() {
    if (confirm('모든 입력 내용이 초기화됩니다. 계속하시겠습니까?')) {
        document.getElementById('programForm').reset();
        
        // 파일 미리보기 초기화
        const filePreview = document.getElementById('filePreview');
        filePreview.style.display = 'none';
        filePreview.innerHTML = '';
        
        // 대표 사진 영역 초기화
        const imageUploadArea = document.querySelector('.image-upload-area');
        const existingPreview = imageUploadArea?.querySelector('.image-preview');
        const uploadContent = imageUploadArea?.querySelector('.image-upload-content');
        
        if (existingPreview) {
            existingPreview.remove();
        }
        if (uploadContent) {
            uploadContent.style.display = 'flex';
        }
        
        console.log('폼이 초기화되었습니다.');
    }
}

// 취소
function cancelForm() {
    if (confirm('작성 중인 내용이 모두 사라집니다. 취소하시겠습니까?')) {
        goToList();
    }
}

// 목록으로 이동
function goToList() {
    window.location.href = '/admin/noncurr_list';
}

// 날짜 입력 필드 최소값 설정
function setDateInputMinValues() {
    const today = new Date().toISOString().split('T')[0];
    const dateInputs = ['recruitStart', 'recruitEnd', 'prgStDt', 'prgEndDt', 'surveyDt'];
    
    dateInputs.forEach(inputId => {
        const input = document.getElementById(inputId);
        if (input) {
            input.setAttribute('min', today);
        }
    });
}

// 제출 로딩 상태 표시/숨김
function showSubmitLoading() {
    const submitBtn = document.querySelector('.btn-register, button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 등록 중...';
    }
}

function hideSubmitLoading() {
    const submitBtn = document.querySelector('.btn-register, button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<span class="material-symbols-outlined">save</span> 등록';
    }
}

// 유틸리티 함수
function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

function showError(message) {
    alert(message);
}

// 공통 JavaScript 로드 후 실행
if (typeof activateMenuByCurrentUrl === 'function') {
    activateMenuByCurrentUrl();
}