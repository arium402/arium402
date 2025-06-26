// 비교과 등록 JavaScript

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
});

function getTodayLocalIsoDate() {
    const now = new Date();
    // 현재 시각에 timezoneOffset(분)을 더해주면 UTC→Local 보정이 됩니다
    const offsetMs = now.getTimezoneOffset() * 60 * 1000;
    const localTime = new Date(now.getTime() - offsetMs);
    return localTime.toISOString().split('T')[0];
}


// 페이지 초기화
function initializePage() {
    // 날짜 필드 기본값 설정 (오늘 날짜)
    const today = getTodayLocalIsoDate();
    
	// 2) recruitStDt (모집 시작일)
	const recruitStartInput = document.querySelector('input[name="recruitStDt"]');
	if (recruitStartInput) {
	    recruitStartInput.min = today;
	}

	
    // 폼 검증 이벤트 추가
    addFormValidation();
    
    // 이미지 드래그 앤 드롭 추가
    addImageDragDrop();
    
    // 전화번호 자동 하이픈 추가
    addPhoneNumberFormatter();
}

// 목록으로 이동
function goToList() {
    if (confirm('작성 중인 내용이 사라집니다. 목록으로 이동하시겠습니까?')) {
        window.location.href = '/admin/noncurr_list';
    }
}

// 이미지 파일 선택 핸들링
function handleImageSelect(input) {
    const file = input.files[0];
    if (!file) return;
    
    // 파일 타입 검증
    if (!file.type.startsWith('image/')) {
        alert('이미지 파일만 업로드 가능합니다.');
        input.value = '';
        return;
    }
    
    // 파일 크기 검증 (5MB)
    if (file.size > 5 * 1024 * 1024) {
        alert('파일 크기는 5MB 이하로 업로드해주세요.');
        input.value = '';
        return;
    }
    
    // 이미지 미리보기
    const reader = new FileReader();
    reader.onload = function(e) {
        const imageArea = document.querySelector('.image-upload-area');
        imageArea.style.backgroundImage = `url(${e.target.result})`;
        imageArea.style.backgroundSize = 'cover';
        imageArea.style.backgroundPosition = 'center';
        
        // 업로드 텍스트 숨기기
        const uploadContent = imageArea.querySelector('.image-upload-content');
        uploadContent.style.opacity = '0.7';
        uploadContent.style.backgroundColor = 'rgba(0,0,0,0.5)';
        uploadContent.style.color = 'white';
        uploadContent.innerHTML = '<i class="fas fa-check"></i><div>이미지 선택됨</div><small>다시 클릭하여 변경</small>';
    };
    reader.readAsDataURL(file);
}

// 이미지 드래그 앤 드롭 기능
function addImageDragDrop() {
    const imageArea = document.querySelector('.image-upload-area');
    const fileInput = document.getElementById('imageFile');
    
    imageArea.addEventListener('dragover', function(e) {
        e.preventDefault();
        imageArea.classList.add('drag-over');
    });
    
    imageArea.addEventListener('dragleave', function(e) {
        e.preventDefault();
        imageArea.classList.remove('drag-over');
    });
    
    imageArea.addEventListener('drop', function(e) {
        e.preventDefault();
        imageArea.classList.remove('drag-over');
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            fileInput.files = files;
            handleImageSelect(fileInput);
        }
    });
}

// 미리보기 함수
function showPreview() {
    // 폼 데이터 수집
    const formData = {
        programName: document.querySelector('input[name="prgNm"]')?.value || '',
        recruitStart: document.querySelector('input[name="recruitStDt"]')?.value || '',
        recruitEnd: document.querySelector('input[name="recruitEndDt"]')?.value || '',
        operationStart: document.querySelector('input[name="prgStDt"]')?.value || '',
        operationEnd: document.querySelector('input[name="prgEndDt"]')?.value || '',
        department: document.querySelector('select[name="prgDept"]')?.value || '',
        contact: document.querySelector('input[name="prgTel"]')?.value || '',
        mileage: document.querySelector('input[name="mlgDefScore"]')?.value || '0',
        description: document.querySelector('textarea[name="prgDesc"]')?.value || ''
    };

    // 선택된 핵심역량 수집
    const selectedCompetencies = [];
    const competencyCheckboxes = document.querySelectorAll('input[name="competencyIds"]:checked');
    competencyCheckboxes.forEach(checkbox => {
        const label = document.querySelector(`label[for="${checkbox.id}"]`);
        if (label) {
            selectedCompetencies.push(label.textContent);
        }
    });

    // 미리보기 데이터 설정
    document.getElementById('previewTitle').textContent = formData.programName || '프로그램명';
    
    // 모집기간 설정
    const recruitPeriod = formData.recruitStart && formData.recruitEnd 
        ? `${formatDate(formData.recruitStart)} ~ ${formatDate(formData.recruitEnd)}`
        : '-';
    document.getElementById('previewRecruitPeriod').textContent = recruitPeriod;
    
    // 운영기간 설정 (필요시)
    const operationPeriod = formData.operationStart && formData.operationEnd
        ? `${formatDate(formData.operationStart)} ~ ${formatDate(formData.operationEnd)}`
        : '-';
    
    document.getElementById('previewDepartment').textContent = formData.department || '-';
    document.getElementById('previewContact').textContent = formData.contact || '-';
    document.getElementById('previewMileage').textContent = `${formData.mileage} 포인트`;
    document.getElementById('previewDescription').textContent = formData.description || '프로그램 설명이 여기에 표시됩니다.';

    // 선택된 이미지가 있으면 미리보기에 표시
	const fileInput = document.getElementById('imageFile');
	const imageArea = document.querySelector('.image-upload-area');
	const previewImage = document.getElementById('previewImage');
	const file = fileInput.files[0];
	
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImage.style.backgroundImage = `url(${e.target.result})`;
            previewImage.style.backgroundSize = 'cover';
            previewImage.style.backgroundPosition = 'center';
            previewImage.textContent = '';
        };
        reader.readAsDataURL(file);
		
	// 2) 파일 객체가 없더라도, 만약 업로드 영역에 백그라운드가 셋팅돼 있다면
	} else if (imageArea.style.backgroundImage && imageArea.style.backgroundImage !== 'none') {
		previewImage.style.backgroundImage = imageArea.style.backgroundImage;
		previewImage.style.backgroundSize = 'cover';
		previewImage.style.backgroundPosition = 'center';
		previewImage.textContent = '';

	// 3) 그 외에는 기본 placeholder
	} else {
        previewImage.style.backgroundImage = 'none';
        previewImage.textContent = '대표 사진 미리보기';
    }

    // 모달 표시
    document.getElementById('previewModal').style.display = 'block';
}

// 미리보기 닫기
function closePreview() {
    document.getElementById('previewModal').style.display = 'none';
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// 폼 리셋
function resetForm() {
    if (confirm('모든 입력 내용이 초기화됩니다. 계속하시겠습니까?')) {
        document.getElementById('programForm').reset();
        
        // 이미지 미리보기 초기화
        const imageArea = document.querySelector('.image-upload-area');
        imageArea.style.backgroundImage = 'none';
        
        const uploadContent = imageArea.querySelector('.image-upload-content');
        uploadContent.style.opacity = '1';
        uploadContent.style.backgroundColor = 'transparent';
        uploadContent.style.color = 'inherit';
        uploadContent.innerHTML = '<i class="fas fa-camera"></i><div>대표 사진 업로드</div><small>클릭하여 선택</small>';
        
        // 첫 번째 입력 필드에 포커스
        const firstInput = document.querySelector('input[name="prgNm"]');
        if (firstInput) firstInput.focus();
    }
}

// 폼 취소
function cancelForm() {
    if (confirm('작성 중인 내용이 사라집니다. 취소하시겠습니까?')) {
        window.location.href = '/admin/noncurr_list';
    }
}

// 폼 검증 추가
function addFormValidation() {
    const form = document.getElementById('programForm');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // 기본 검증
        if (!validateForm()) {
            return false;
        }
        
        // 핵심역량 선택 검증
        const selectedCompetencies = document.querySelectorAll('input[name="competencyIds"]:checked');
        if (selectedCompetencies.length === 0) {
            alert('핵심역량을 하나 이상 선택해주세요.');
            return false;
        }
        
        // 날짜 검증
        if (!validateDates()) {
            return false;
        }
        
        // Ajax 전송
        submitForm();
    });
}

// 폼 검증
function validateForm() {
    const requiredFields = [
        { name: 'prgNm', label: '비교과명' },
        { name: 'recruitStDt', label: '모집 시작일' },
        { name: 'recruitEndDt', label: '모집 마감일' },
        { name: 'maxCnt', label: '모집인원' },
        { name: 'prgDept', label: '운영부서' },
        { name: 'prgStDt', label: '운영 시작일' },
        { name: 'prgEndDt', label: '운영 종료일' },
        { name: 'prgTel', label: '문의 전화번호' },
        { name: 'surveyDt', label: '만족도조사 마감일' },
        { name: 'mlgDefScore', label: '마일리지 점수' },
        { name: 'prgDesc', label: '프로그램 설명' }
    ];
    
    for (const field of requiredFields) {
        const element = document.querySelector(`[name="${field.name}"]`);
        if (!element || !element.value.trim()) {
            alert(`${field.label}을(를) 입력해주세요.`);
            if (element) element.focus();
            return false;
        }
    }
    
    return true;
}

// 날짜 검증
function validateDates() {
    const recruitStart = new Date(document.querySelector('input[name="recruitStDt"]').value);
    const recruitEnd = new Date(document.querySelector('input[name="recruitEndDt"]').value);
    const operationStart = new Date(document.querySelector('input[name="prgStDt"]').value);
    const operationEnd = new Date(document.querySelector('input[name="prgEndDt"]').value);
    const surveyDate = new Date(document.querySelector('input[name="surveyDt"]').value);
    
    // 모집 기간 검증
    if (recruitStart >= recruitEnd) {
        alert('모집 마감일은 모집 시작일보다 늦어야 합니다.');
        return false;
    }
    
    // 운영 기간 검증
    if (operationStart >= operationEnd) {
        alert('운영 종료일은 운영 시작일보다 늦어야 합니다.');
        return false;
    }
    
    // 모집 마감일은 운영 시작일보다 이전이어야 함
    if (recruitEnd > operationStart) {
        alert('모집 마감일은 운영 시작일보다 이전이어야 합니다.');
        return false;
    }
    
    // 만족도조사 마감일은 운영 종료일 이후여야 함
    if (surveyDate <= operationEnd) {
        alert('만족도조사 마감일은 운영 종료일 이후여야 합니다.');
        return false;
    }
    
    return true;
}

// Ajax 폼 전송
function submitForm() {
    const form = document.getElementById('programForm');
    const formData = new FormData(form);
    
    // 로딩 표시
    const submitButton = document.querySelector('button[type="submit"]');
    const originalText = submitButton.innerHTML;
    submitButton.innerHTML = '<span class="material-symbols-outlined">hourglass_empty</span> 등록 중...';
    submitButton.disabled = true;
    
    fetch('/api/admin/noncurr_add', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('비교과 프로그램이 성공적으로 등록되었습니다.');
            window.location.href = '/admin/noncurr_list';
        } else {
            alert(data.error || '등록 중 오류가 발생했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('네트워크 오류가 발생했습니다.');
    })
    .finally(() => {
        // 로딩 해제
        submitButton.innerHTML = originalText;
        submitButton.disabled = false;
    });
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const modal = document.getElementById('previewModal');
    if (event.target === modal) {
        closePreview();
    }
}

// 전화번호 자동 하이픈 기능
function addPhoneNumberFormatter() {
    const phoneInput = document.querySelector('input[name="prgTel"]');
    if (!phoneInput) return;
    
    phoneInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/[^0-9]/g, ''); // 숫자만 추출
        let formattedValue = '';
        
        if (value.length <= 3) {
            // 3자리 이하
            formattedValue = value;
        } else if (value.length <= 7) {
            // 4~7자리: 02-1234 또는 031-123
            if (value.startsWith('02')) {
                formattedValue = value.substring(0, 2) + '-' + value.substring(2);
            } else {
                formattedValue = value.substring(0, 3) + '-' + value.substring(3);
            }
        } else if (value.length <= 11) {
            // 8~11자리: 전체 번호
            if (value.startsWith('02')) {
                // 서울: 02-1234-5678
                formattedValue = value.substring(0, 2) + '-' + value.substring(2, 6) + '-' + value.substring(6);
            } else if (value.startsWith('01')) {
                // 휴대폰: 010-1234-5678
                formattedValue = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7);
            } else {
                // 기타 지역번호: 031-123-4567
                formattedValue = value.substring(0, 3) + '-' + value.substring(3, 6) + '-' + value.substring(6);
            }
        } else {
            // 11자리 초과 시 마지막 자리 제거
            value = value.substring(0, 11);
            if (value.startsWith('02')) {
                formattedValue = value.substring(0, 2) + '-' + value.substring(2, 6) + '-' + value.substring(6);
            } else {
                formattedValue = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7);
            }
        }
        
        e.target.value = formattedValue;
    });
    
    // 붙여넣기 이벤트 처리
    phoneInput.addEventListener('paste', function(e) {
        setTimeout(() => {
            phoneInput.dispatchEvent(new Event('input'));
        }, 10);
    });
}

// 키보드 이벤트 (ESC로 모달 닫기)
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modal = document.getElementById('previewModal');
        if (modal.style.display === 'block') {
            closePreview();
        }
    }
});