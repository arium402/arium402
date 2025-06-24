// 비교과 등록 페이지 JavaScript

// 대표 사진 선택 처리
function handleImageSelect(input) {
    const file = input.files[0];
    const uploadArea = input.parentElement;
    const uploadContent = uploadArea.querySelector('.image-upload-content');
    
    if (file) {
        // 기존 미리보기 이미지가 있다면 제거
        const existingPreview = uploadArea.querySelector('.image-preview');
        if (existingPreview) {
            existingPreview.remove();
        }
        
        const reader = new FileReader();
        reader.onload = function(e) {
            // 새로운 이미지 요소 생성
            const img = document.createElement('img');
            img.src = e.target.result;
            img.alt = '대표 사진';
            img.className = 'image-preview';
            
            // 업로드 콘텐츠 숨기고 이미지 표시
            uploadContent.style.display = 'none';
            uploadArea.appendChild(img);
        };
        reader.readAsDataURL(file);
    } else {
        // 파일이 없으면 미리보기 제거하고 원래 상태로
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
    const preview = document.getElementById('filePreview');
    
    if (file) {
        preview.innerHTML = `<i class="fas fa-check-circle"></i> 선택된 파일: ${file.name}`;
        preview.style.display = 'block';
    } else {
        preview.style.display = 'none';
        preview.innerHTML = '';
    }
}

// 미리보기 표시
function showPreview() {
    const programName = document.getElementById('programName').value || '프로그램명';
    const recruitStart = document.getElementById('recruitStart').value;
    const recruitEnd = document.getElementById('recruitEnd').value;
    const department = document.getElementById('department').value || '-';
    const contact = document.getElementById('contact').value || '-';
    const mileagePoints = document.getElementById('mileagePoints').value || '0';
    const description = document.getElementById('description').value || '프로그램 설명이 없습니다.';
    const programImageInput = document.getElementById('programImageInput');

    // 미리보기 데이터 설정
    document.getElementById('previewTitle').textContent = programName;
    document.getElementById('previewRecruitPeriod').textContent = 
        recruitStart && recruitEnd ? `${recruitStart} ~ ${recruitEnd}` : '-';
    document.getElementById('previewDepartment').textContent = department;
    document.getElementById('previewContact').textContent = contact;
    document.getElementById('previewMileage').textContent = `${parseInt(mileagePoints).toLocaleString()} 포인트`;
    document.getElementById('previewDescription').textContent = description;

    // 대표 사진 미리보기
    const previewImage = document.getElementById('previewImage');
    if (programImageInput.files && programImageInput.files[0]) {
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
        document.getElementById('filePreview').style.display = 'none';
        document.getElementById('filePreview').innerHTML = '';
        
        // 대표 사진 영역 초기화
        const imageUploadArea = document.querySelector('.image-upload-area');
        const existingPreview = imageUploadArea.querySelector('.image-preview');
        const uploadContent = imageUploadArea.querySelector('.image-upload-content');
        
        if (existingPreview) {
            existingPreview.remove();
        }
        if (uploadContent) {
            uploadContent.style.display = 'flex';
        }
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

// 폼 유효성 검사
function validateForm() {
    // 핵심역량 체크 검증
    const competencies = document.querySelectorAll('input[name="competencies"]:checked');
    if (competencies.length === 0) {
        alert('핵심역량을 하나 이상 선택해주세요.');
        return false;
    }

    // 날짜 유효성 검사
    const recruitStart = new Date(document.getElementById('recruitStart').value);
    const recruitEnd = new Date(document.getElementById('recruitEnd').value);
    const operationStart = new Date(document.getElementById('operationStart').value);
    const operationEnd = new Date(document.getElementById('operationEnd').value);

    if (recruitStart >= recruitEnd) {
        alert('모집 시작일은 모집 마감일보다 이전이어야 합니다.');
        return false;
    }

    if (operationStart >= operationEnd) {
        alert('운영 시작일은 운영 종료일보다 이전이어야 합니다.');
        return false;
    }

    if (recruitEnd > operationStart) {
        alert('모집 마감일은 운영 시작일 이전이어야 합니다.');
        return false;
    }

    return true;
}

// 페이지 로드 시 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
    // 폼 제출 처리
    const programForm = document.getElementById('programForm');
    if (programForm) {
        programForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // 유효성 검사
            if (!validateForm()) {
                return;
            }

            // 등록 확인
            if (confirm('비교과 프로그램을 등록하시겠습니까?')) {
                // 실제로는 폼을 서버로 전송
                this.submit();
            }
        });
    }

    // 모집인원 최소값 설정
    const capacityInput = document.getElementById('capacity');
    if (capacityInput) {
        capacityInput.addEventListener('input', function() {
            if (this.value < 1) {
                this.value = 1;
            }
        });
    }

    // 마일리지 점수 최소값 설정
    const mileageInput = document.getElementById('mileagePoints');
    if (mileageInput) {
        mileageInput.addEventListener('input', function() {
            if (this.value < 0) {
                this.value = 0;
            }
        });
    }

    // 오늘 날짜보다 이전 날짜 선택 방지
    const today = new Date().toISOString().split('T')[0];
    const dateInputs = ['recruitStart', 'recruitEnd', 'operationStart', 'operationEnd', 'surveyDeadline'];
    
    dateInputs.forEach(inputId => {
        const input = document.getElementById(inputId);
        if (input) {
            input.setAttribute('min', today);
        }
    });
});

// 공통 JavaScript 로드 후 실행
if (typeof activateMenuByCurrentUrl === 'function') {
    activateMenuByCurrentUrl();
}