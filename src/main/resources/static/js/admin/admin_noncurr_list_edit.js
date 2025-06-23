// 초기 데이터 저장 변수
let originalData = {};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 원본 데이터 저장
    saveOriginalData();
    
    // 이벤트 리스너 등록
    initializeEventListeners();
});

// 원본 데이터 저장
function saveOriginalData() {
    originalData = {
        programName: document.getElementById('programName').value,
        recruitStart: document.getElementById('recruitStart').value,
        recruitEnd: document.getElementById('recruitEnd').value,
        capacity: document.getElementById('capacity').value,
        department: document.getElementById('department').value,
        operationStart: document.getElementById('operationStart').value,
        operationEnd: document.getElementById('operationEnd').value,
        contact: document.getElementById('contact').value,
        surveyDeadline: document.getElementById('surveyDeadline').value,
        mileagePoints: document.getElementById('mileagePoints').value,
        description: document.getElementById('description').value,
        competencies: getSelectedCompetencies(),
        fileName: getAttachedFileName()
    };
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 사이드바 토글
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            const sidebar = document.getElementById('layoutSidenav_nav');
            const content = document.getElementById('layoutSidenav_content');
            
            if (window.innerWidth <= 768) {
                sidebar.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
                content.classList.toggle('expanded');
            }
        });
    }

    // 윈도우 리사이즈 처리
    window.addEventListener('resize', function() {
        const sidebar = document.getElementById('layoutSidenav_nav');
        const content = document.getElementById('layoutSidenav_content');
        
        if (window.innerWidth > 768) {
            sidebar.classList.remove('show');
        } else {
            sidebar.classList.remove('collapsed');
            content.classList.remove('expanded');
        }
    });

    // 폼 제출 처리
    const programForm = document.getElementById('programForm');
    if (programForm) {
        programForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleFormSubmit();
        });
    }

    // 모달 외부 클릭 시 닫기
    window.onclick = function(event) {
        const modal = document.getElementById('previewModal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    }
}

// 선택된 핵심역량 가져오기
function getSelectedCompetencies() {
    const competencies = [];
    document.querySelectorAll('input[name="competencies"]:checked').forEach(checkbox => {
        competencies.push(checkbox.value);
    });
    return competencies;
}

// 첨부파일명 가져오기
function getAttachedFileName() {
    const filePreview = document.getElementById('filePreview');
    if (filePreview && filePreview.textContent) {
        const match = filePreview.textContent.match(/현재 파일: (.+)/);
        return match ? match[1] : '';
    }
    return '';
}

// 대표 사진 선택 처리
function handleImageSelect(input) {
    const file = input.files[0];
    const uploadArea = input.parentElement;
    
    if (file) {
        // 파일 크기 검증 (5MB)
        if (file.size > 5 * 1024 * 1024) {
            alert('파일 크기는 5MB 이하여야 합니다.');
            input.value = '';
            return;
        }

        // 파일 형식 검증
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
        if (!allowedTypes.includes(file.type)) {
            alert('JPG, PNG 파일만 업로드 가능합니다.');
            input.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            uploadArea.innerHTML = `
                <input type="file" id="programImageInput" accept="image/*" onchange="handleImageSelect(this)">
                <img src="${e.target.result}" alt="대표 사진" class="image-preview">
            `;
        };
        reader.readAsDataURL(file);
    } else {
        // 원래 이미지로 복원
        resetImageUploadArea(uploadArea);
    }
}

// 이미지 업로드 영역 초기화
function resetImageUploadArea(uploadArea) {
    uploadArea.innerHTML = `
        <input type="file" id="programImageInput" accept="image/*" onchange="handleImageSelect(this)">
        <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'%3E%3Crect width='400' height='300' fill='%23e9ecef'/%3E%3Ctext x='50%25' y='50%25' font-size='18' text-anchor='middle' dy='.3em' fill='%23495057'%3E프로그램 대표 사진%3C/text%3E%3C/svg%3E" alt="프로그램 대표 사진" class="image-preview">
    `;
}

// 첨부파일 선택 처리
function handleFileSelect(input) {
    const file = input.files[0];
    const preview = document.getElementById('filePreview');
    
    if (file) {
        // 파일 크기 검증 (10MB)
        if (file.size > 10 * 1024 * 1024) {
            alert('파일 크기는 10MB 이하여야 합니다.');
            input.value = '';
            return;
        }

        // 파일 형식 검증
        const allowedExtensions = ['.pdf', '.doc', '.docx', '.hwp', '.jpg', '.jpeg', '.png'];
        const fileName = file.name.toLowerCase();
        const isValidFile = allowedExtensions.some(ext => fileName.endsWith(ext));
        
        if (!isValidFile) {
            alert('PDF, DOC, DOCX, HWP, JPG, PNG 파일만 업로드 가능합니다.');
            input.value = '';
            return;
        }

        preview.innerHTML = `<i class="fas fa-check-circle"></i> 선택된 파일: ${file.name}`;
        preview.style.display = 'block';
    } else {
        if (originalData.fileName) {
            preview.innerHTML = `<i class="fas fa-file-pdf"></i> 현재 파일: ${originalData.fileName}`;
        } else {
            preview.style.display = 'none';
        }
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
    document.getElementById('previewDescription').innerHTML = description.replace(/\n/g, '<br>');

    // 대표 사진 미리보기
    const previewImage = document.getElementById('previewImage');
    if (programImageInput.files && programImageInput.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImage.innerHTML = `<img src="${e.target.result}" alt="프로그램 대표 사진" style="width: 100%; height: 100%; object-fit: cover;">`;
        };
        reader.readAsDataURL(programImageInput.files[0]);
    } else {
        previewImage.innerHTML = `<img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='150' viewBox='0 0 200 150'%3E%3Crect width='200' height='150' fill='%23e9ecef'/%3E%3Ctext x='50%25' y='50%25' font-size='14' text-anchor='middle' dy='.3em' fill='%23495057'%3E프로그램 사진%3C/text%3E%3C/svg%3E" style="width: 100%; height: 100%; object-fit: cover;">`;
    }

    // 모달 표시
    document.getElementById('previewModal').style.display = 'block';
}

// 미리보기 닫기
function closePreview() {
    document.getElementById('previewModal').style.display = 'none';
}

// 폼 원래대로 복원
function resetForm() {
    if (confirm('원래 데이터로 복원하시겠습니까?')) {
        loadOriginalData();
    }
}

// 원본 데이터 로드
function loadOriginalData() {
    document.getElementById('programName').value = originalData.programName || '';
    document.getElementById('recruitStart').value = originalData.recruitStart || '';
    document.getElementById('recruitEnd').value = originalData.recruitEnd || '';
    document.getElementById('capacity').value = originalData.capacity || '';
    document.getElementById('department').value = originalData.department || '';
    document.getElementById('operationStart').value = originalData.operationStart || '';
    document.getElementById('operationEnd').value = originalData.operationEnd || '';
    document.getElementById('contact').value = originalData.contact || '';
    document.getElementById('surveyDeadline').value = originalData.surveyDeadline || '';
    document.getElementById('mileagePoints').value = originalData.mileagePoints || '';
    document.getElementById('description').value = originalData.description || '';
    
    // 핵심역량 체크박스 설정
    const checkboxes = document.querySelectorAll('input[name="competencies"]');
    checkboxes.forEach(checkbox => {
        checkbox.checked = originalData.competencies && originalData.competencies.includes(checkbox.value);
    });

    // 첨부파일 정보 복원
    const filePreview = document.getElementById('filePreview');
    if (originalData.fileName) {
        filePreview.innerHTML = `<i class="fas fa-file-pdf"></i> 현재 파일: ${originalData.fileName}`;
        filePreview.style.display = 'block';
    } else {
        filePreview.style.display = 'none';
    }
    
    // 파일 입력 초기화
    document.getElementById('fileInput').value = '';
    
    // 대표 사진 복원
    const imageUploadArea = document.querySelector('.image-upload-area');
    resetImageUploadArea(imageUploadArea);
    
    // 프로그램 이미지 입력 초기화
    document.getElementById('programImageInput').value = '';
}

// 취소
function cancelForm() {
    if (confirm('수정을 취소하시겠습니까? 변경사항이 저장되지 않습니다.')) {
        goToList();
    }
}

// 목록으로 이동
function goToList() {
    window.location.href = '/admin/noncurr_list';
}

// 폼 제출 처리
function handleFormSubmit() {
    // 핵심역량 체크 검증
    const competencies = document.querySelectorAll('input[name="competencies"]:checked');
    if (competencies.length === 0) {
        alert('핵심역량을 하나 이상 선택해주세요.');
        return;
    }

    // 날짜 검증
    const recruitStart = new Date(document.getElementById('recruitStart').value);
    const recruitEnd = new Date(document.getElementById('recruitEnd').value);
    const operationStart = new Date(document.getElementById('operationStart').value);
    const operationEnd = new Date(document.getElementById('operationEnd').value);
    const surveyDeadline = new Date(document.getElementById('surveyDeadline').value);

    if (recruitStart >= recruitEnd) {
        alert('모집 마감일은 모집 시작일보다 늦어야 합니다.');
        return;
    }

    if (operationStart >= operationEnd) {
        alert('운영 종료일은 운영 시작일보다 늦어야 합니다.');
        return;
    }

    if (recruitEnd > operationStart) {
        alert('운영 시작일은 모집 마감일 이후여야 합니다.');
        return;
    }

    if (surveyDeadline <= operationEnd) {
        alert('만족도조사 마감일은 운영 종료일 이후여야 합니다.');
        return;
    }

    // 수정 확인
    if (confirm('비교과 프로그램을 수정하시겠습니까?')) {
        // FormData 생성하여 파일과 함께 전송
        const formData = new FormData(document.getElementById('programForm'));
        
        // 이미지 파일 추가
        const imageFile = document.getElementById('programImageInput').files[0];
        if (imageFile) {
            formData.append('programImage', imageFile);
        }

        // 서버로 전송
        submitFormData(formData);
    }
}

// 폼 데이터 서버 전송
function submitFormData(formData) {
    const programId = document.getElementById('programId').value;
    
    fetch('/admin/noncurr_edit', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            alert('비교과 프로그램이 성공적으로 수정되었습니다.');
            goToList();
        } else {
            throw new Error('서버 오류가 발생했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('수정 중 오류가 발생했습니다. 다시 시도해주세요.');
    });
}

// 유틸리티 함수들
function formatNumber(num) {
    return parseInt(num).toLocaleString();
}

function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePhoneNumber(phone) {
    const re = /^[0-9-]+$/;
    return re.test(phone);
}