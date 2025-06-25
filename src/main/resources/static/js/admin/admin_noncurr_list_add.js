// 비교과 등록 페이지 JavaScript (임시 버전 - 대표사진만)

// 전역 설정
const MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
const MAX_FILES_PER_REQUEST = 1; // 파일 1개만 (대표사진)

// 대표 사진 선택 처리
function handleImageSelect(input) {
    const file = input.files[0];
    const uploadArea = input.parentElement;
    const uploadContent = uploadArea.querySelector('.image-upload-content');
    
    if (file) {
        // 파일 크기 검증
        if (file.size > MAX_IMAGE_SIZE) {
            alert(`대표사진 크기는 ${Math.round(MAX_IMAGE_SIZE / 1024 / 1024)}MB 이하여야 합니다.\n현재 파일 크기: ${Math.round(file.size / 1024 / 1024)}MB`);
            input.value = '';
            return;
        }
        
        // 파일 형식 검증
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            alert('허용되지 않는 이미지 형식입니다.\n허용 형식: JPG, PNG, GIF, BMP, WEBP');
            input.value = '';
            return;
        }
        
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
        
        reader.onerror = function() {
            alert('이미지 파일을 읽는 중 오류가 발생했습니다.');
            input.value = '';
        };
        
        reader.readAsDataURL(file);
        
        console.log('대표사진 선택:', file.name, `(${Math.round(file.size / 1024)}KB)`);
    } else {
        // 파일이 없으면 미리보기 제거하고 원래 상태로
        const existingPreview = uploadArea.querySelector('.image-preview');
        if (existingPreview) {
            existingPreview.remove();
        }
        uploadContent.style.display = 'flex';
    }
}

// 첨부파일 관련 함수 제거됨 (임시)
// function handleFileSelect(input) { ... } // 제거됨

// 미리보기 표시
function showPreview() {
    const prgNm = document.getElementById('prgNm').value || '프로그램명';
    const recruitStart = document.getElementById('recruitStart').value;
    const recruitEnd = document.getElementById('recruitEnd').value;
    const department = document.getElementById('department').value || '-';
    const contact = document.getElementById('contact').value || '-';
    const mlgDefScore = document.getElementById('mlgDefScore').value || '0';
    const prgDesc = document.getElementById('prgDesc').value || '프로그램 설명이 없습니다.';
    const fileIdInput = document.getElementById('fileId');

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
    if (fileIdInput.files && fileIdInput.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImage.innerHTML = `<img src="${e.target.result}" alt="프로그램 대표 사진" style="width: 100%; height: 100%; object-fit: cover;">`;
        };
        reader.readAsDataURL(fileIdInput.files[0]);
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
        
        console.log('폼 초기화 완료');
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

// 폼 유효성 검사 (간소화 버전)
function validateForm() {
    console.log('=== 폼 유효성 검사 시작 ===');
    
    // 필수 필드 검증
    const requiredFields = [
        { id: 'prgNm', name: '프로그램명' },
        { id: 'recruitStart', name: '모집 시작일' },
        { id: 'recruitEnd', name: '모집 마감일' },
        { id: 'maxCnt', name: '모집인원' },
        { id: 'department', name: '운영부서' },
        { id: 'prgStDt', name: '운영 시작일' },
        { id: 'prgEndDt', name: '운영 종료일' },
        { id: 'contact', name: '문의 전화번호' },
        { id: 'surveyDt', name: '만족도조사 마감일' },
        { id: 'mlgDefScore', name: '마일리지 점수' },
        { id: 'prgDesc', name: '프로그램 설명' }
    ];
    
    for (const field of requiredFields) {
        const element = document.getElementById(field.id);
        if (!element.value.trim()) {
            alert(`${field.name}을(를) 입력해주세요.`);
            element.focus();
            return false;
        }
    }
    
    // 핵심역량 체크 검증
    const competencies = document.querySelectorAll('input[name="competencies"]:checked');
    if (competencies.length === 0) {
        alert('핵심역량을 하나 이상 선택해주세요.');
        return false;
    }
    
    // 대표사진 필수 체크 (선택사항으로 변경 가능)
    const imageFile = document.getElementById('fileId').files[0];
    if (!imageFile) {
        if (!confirm('대표사진이 없습니다. 그래도 등록하시겠습니까?')) {
            return false;
        }
    }
    
    // 날짜 유효성 검사
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
        alert('모집 마감일은 운영 시작일 이전이어야 합니다.');
        return false;
    }
    
    if (surveyDt < prgEndDt) {
        alert('만족도조사 마감일은 운영 종료일 이후여야 합니다.');
        return false;
    }
    
    // 숫자 필드 검증
    const maxCnt = parseInt(document.getElementById('maxCnt').value);
    const mlgDefScore = parseInt(document.getElementById('mlgDefScore').value);
    
    if (maxCnt < 1) {
        alert('모집인원은 1명 이상이어야 합니다.');
        return false;
    }
    
    if (mlgDefScore < 0) {
        alert('마일리지 점수는 0점 이상이어야 합니다.');
        return false;
    }
    
    console.log('폼 유효성 검사 통과');
    return true;
}

// 폼 제출 함수 (간소화 버전)
function submitForm() {
    console.log('=== 폼 제출 시작 (대표사진만) ===');
    
    const formData = new FormData();
    
    try {
        // 기본 정보 추가
        formData.append('prgNm', document.getElementById('prgNm').value);
        formData.append('prgDesc', document.getElementById('prgDesc').value);
        formData.append('recruitStart', document.getElementById('recruitStart').value);
        formData.append('recruitEnd', document.getElementById('recruitEnd').value);
        formData.append('prgStDt', document.getElementById('prgStDt').value);
        formData.append('prgEndDt', document.getElementById('prgEndDt').value);
        formData.append('maxCnt', document.getElementById('maxCnt').value);
        formData.append('department', document.getElementById('department').value);
        formData.append('contact', document.getElementById('contact').value);
        formData.append('surveyDt', document.getElementById('surveyDt').value);
        formData.append('mlgDefScore', document.getElementById('mlgDefScore').value);
        
        // 대표사진만 추가
        const imageFile = document.getElementById('fileId').files[0];
        if (imageFile) {
            formData.append('imageFile', imageFile);
            console.log('대표사진 추가:', imageFile.name, `(${Math.round(imageFile.size / 1024)}KB)`);
        }
        
        // 첨부파일 처리 제거됨 (임시)
        // const attachmentFile = document.getElementById('attachmentFile').files[0];
        // if (attachmentFile) {
        //     formData.append('attachmentFile', attachmentFile);
        // }
        
        // 핵심역량 추가
        const selectedCompetencies = [];
        document.querySelectorAll('input[name="competencies"]:checked').forEach(checkbox => {
            selectedCompetencies.push(checkbox.value);
        });
        formData.append('competencies', JSON.stringify(selectedCompetencies));
        
        console.log('선택된 핵심역량:', selectedCompetencies);
        console.log('총 1개 파일 첨부 (대표사진만)');
        
        // 버튼 비활성화
        const submitBtn = document.querySelector('.btn-register');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="material-symbols-outlined">hourglass_empty</span> 등록 중...';
        
        // 서버로 전송
        console.log('서버로 전송 시작...');
        fetch('/api/admin/noncurr', {
            method: 'POST',
            body: formData // Content-Type 헤더는 브라우저가 자동으로 설정
        })
        .then(response => {
            console.log('Response status:', response.status);
            console.log('Response headers:', response.headers.get('content-type'));
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            return response.json();
        })
        .then(data => {
            console.log('응답 데이터:', data);
            
            if (data.success) {
                alert('비교과 프로그램이 성공적으로 등록되었습니다.\n(첨부파일은 추후 별도로 추가할 수 있습니다.)');
                console.log('등록 완료 - 프로그램 ID:', data.prgId);
                goToList();
            } else {
                throw new Error(data.message || '알 수 없는 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('등록 오류:', error);
            
            let errorMessage = '등록 중 오류가 발생했습니다.';
            
            if (error.message.includes('413')) {
                errorMessage = '파일 크기가 너무 큽니다. 파일 크기를 줄여주세요.';
            } else if (error.message.includes('400')) {
                errorMessage = '요청 데이터에 문제가 있습니다. 입력 내용을 확인해주세요.';
            } else if (error.message.includes('500')) {
                errorMessage = '서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
            } else if (error.message) {
                errorMessage = error.message;
            }
            
            alert(errorMessage);
        })
        .finally(() => {
            // 버튼 복원
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
            console.log('폼 제출 처리 완료');
        });
        
    } catch (error) {
        console.error('폼 데이터 구성 오류:', error);
        alert('폼 데이터 구성 중 오류가 발생했습니다: ' + error.message);
    }
}

// 페이지 로드 시 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
    console.log('=== 비교과 등록 페이지 초기화 (임시 버전) ===');
    
    // 폼 제출 처리
    const programForm = document.getElementById('programForm');
    if (programForm) {
        programForm.addEventListener('submit', function(e) {
            e.preventDefault(); // 기본 폼 제출 방지
            
            console.log('폼 제출 이벤트 발생');
            
            // 유효성 검사
            if (!validateForm()) {
                console.log('유효성 검사 실패');
                return;
            }

            // 등록 확인
            if (confirm('비교과 프로그램을 등록하시겠습니까?\n(첨부파일은 추후 별도 추가 가능)')) {
                submitForm();
            } else {
                console.log('사용자가 등록을 취소함');
            }
        });
    }

    // 모집인원 최소값 설정
    const maxCntInput = document.getElementById('maxCnt');
    if (maxCntInput) {
        maxCntInput.addEventListener('input', function() {
            if (this.value < 1) {
                this.value = 1;
            }
        });
    }

    // 마일리지 점수 최소값 설정
    const mlgDefScoreInput = document.getElementById('mlgDefScore');
    if (mlgDefScoreInput) {
        mlgDefScoreInput.addEventListener('input', function() {
            if (this.value < 0) {
                this.value = 0;
            }
        });
    }

    // 오늘 날짜보다 이전 날짜 선택 방지
    const today = new Date().toISOString().split('T')[0];
    const dateInputs = ['recruitStart', 'recruitEnd', 'prgStDt', 'prgEndDt', 'surveyDt'];
    
    dateInputs.forEach(inputId => {
        const input = document.getElementById(inputId);
        if (input) {
            input.setAttribute('min', today);
        }
    });
    
    console.log('페이지 초기화 완료');
    console.log('파일 업로드 제한: 최대 1개 (대표사진만)');
    console.log('이미지 크기 제한:', Math.round(MAX_IMAGE_SIZE / 1024 / 1024) + 'MB');
    console.log('⚠️ 임시 버전: 첨부파일 기능 비활성화');
});

// 공통 JavaScript 로드 후 실행
if (typeof activateMenuByCurrentUrl === 'function') {
    activateMenuByCurrentUrl();
}