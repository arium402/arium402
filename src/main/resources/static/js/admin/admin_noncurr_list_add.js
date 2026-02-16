// 비교과 등록 페이지 JavaScript

// 대표 사진 선택 처리
function handleImageSelect(input) {
    const file = input.files[0];
    const uploadArea = input.parentElement;
    const uploadContent = uploadArea.querySelector('.image-upload-content');
    
    if (file) {
		
		// ✅ 파일 크기 검증 추가
		console.log('선택된 파일:', file.name, '크기:', file.size, '타입:', file.type);
        
		if (file.size === 0) {
		    alert('선택된 파일이 비어있습니다. 다른 파일을 선택해주세요.');
		    input.value = ''; // 파일 입력 초기화
		    return;
		}
		
		if (file.size > 5 * 1024 * 1024) { // 5MB
		    alert('파일 크기가 5MB를 초과합니다.');
		    input.value = ''; // 파일 입력 초기화
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

// ✅ 전화번호 포맷팅 함수 추가
function formatPhoneNumber(input) {
    // 숫자만 추출
    let value = input.value.replace(/[^0-9]/g, '');
    
    // 길이에 따라 다른 포맷 적용
    if (value.length <= 3) {
        input.value = value;
    } else if (value.length <= 7) {
        // 02-1234, 031-123, 010-123 등
        if (value.startsWith('02')) {
            input.value = value.replace(/(\d{2})(\d+)/, '$1-$2');
        } else {
            input.value = value.replace(/(\d{3})(\d+)/, '$1-$2');
        }
    } else {
        // 완전한 전화번호
        if (value.startsWith('02')) {
            // 서울: 02-1234-5678 or 02-123-4567
            if (value.length === 9) {
                input.value = value.replace(/(\d{2})(\d{3})(\d{4})/, '$1-$2-$3');
            } else {
                input.value = value.replace(/(\d{2})(\d{4})(\d{4})/, '$1-$2-$3');
            }
        } else if (value.startsWith('01')) {
            // 휴대폰: 010-1234-5678
            input.value = value.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        } else {
            // 지역번호: 031-123-4567 or 031-1234-5678
            if (value.length === 10) {
                input.value = value.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
            } else {
                input.value = value.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
            }
        }
    }
}

// 체크박스 상태에 따라 점수 선택 보이기/숨기기
function toggleScoreSelect(checkbox) {
    const competencyId = checkbox.value;
    const scoreWrapper = document.getElementById('score_wrapper_' + competencyId);
    
    if (scoreWrapper) {
        if (checkbox.checked) {
            scoreWrapper.style.display = 'block';
        } else {
            scoreWrapper.style.display = 'none';
        }
    }
    
    // 체크박스 상태 변경시 점수 정보도 업데이트
    updateCompetencyScores();
}

// ✅ 새로 추가: 핵심역량 점수 정보 수집
function updateCompetencyScores() {
    const checkboxes = document.querySelectorAll('input[name="competencyCheckbox"]:checked');
    const scoresData = [];
    
    checkboxes.forEach(checkbox => {
        const competencyId = checkbox.value;
        const scoreSelect = document.getElementById('score_' + competencyId);
        
        if (scoreSelect && scoreSelect.value && scoreSelect.value !== '') {
            const score = scoreSelect.value;
            scoresData.push(competencyId + ':' + score);
        }
    });
    
    // 히든 필드에 점수 정보 저장 (형식: "1:100,2:95,3:80")
    const scoresField = document.getElementById('competencyScoresStr');
    if (scoresField) {
        scoresField.value = scoresData.join(',');
    }
    
    console.log('핵심역량 점수 정보:', scoresData.join(','));
}

// ✅ 수정: 체크박스 값들과 점수를 함께 수집
function updateCompetencyIds() {
    const checkboxes = document.querySelectorAll('input[name="competencyCheckbox"]:checked');
    const competencyIds = Array.from(checkboxes).map(cb => cb.value);
    
    // 기존 히든 필드에 ID 저장 (하위 호환성)
    const competencyIdsField = document.getElementById('competencyIdsStr');
    if (competencyIdsField) {
        competencyIdsField.value = competencyIds.join(',');
    }
    
    console.log('선택된 핵심역량 ID:', competencyIds);
}

// 미리보기 표시
function showPreview() {
    // ✅ 수정: 모든 ID를 새로운 필드명에 맞게 변경
    const prgNm = document.getElementById('prgNm').value || '프로그램명';
    const recruitStDt = document.getElementById('recruitStDt').value;
    const recruitEndDt = document.getElementById('recruitEndDt').value;
    const prgDept = document.getElementById('prgDept').value || '-';
    const prgTel = document.getElementById('prgTel').value || '-';
    const mlgDefScore = document.getElementById('mlgDefScore').value || '0';
    const prgDesc = document.getElementById('prgDesc').value || '프로그램 설명이 없습니다.';
    const imageFileInput = document.getElementById('imageFile');

    // 미리보기 데이터 설정
    document.getElementById('previewTitle').textContent = prgNm;
    document.getElementById('previewRecruitPeriod').textContent = 
        recruitStDt && recruitEndDt ? `${recruitStDt} ~ ${recruitEndDt}` : '-';
    document.getElementById('previewDepartment').textContent = prgDept;
    document.getElementById('previewContact').textContent = prgTel;
    document.getElementById('previewMileage').textContent = `${parseInt(mlgDefScore).toLocaleString()} 포인트`;
    document.getElementById('previewDescription').textContent = prgDesc;

    // 대표 사진 미리보기
    const previewImage = document.getElementById('previewImage');
    if (imageFileInput.files && imageFileInput.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImage.innerHTML = `<img src="${e.target.result}" alt="프로그램 대표 사진" style="width: 100%; height: 100%; object-fit: cover;">`;
        };
        reader.readAsDataURL(imageFileInput.files[0]);
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
        
        // 히든 필드도 초기화
        document.getElementById('competencyIdsStr').value = '';
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
    const competencies = document.querySelectorAll('input[name="competencyCheckbox"]:checked');
    if (competencies.length === 0) {
        alert('핵심역량을 하나 이상 선택해주세요.');
        return false;
    }

    // ✅ 수정: 새로운 필드 ID로 변경
    const recruitStDt = new Date(document.getElementById('recruitStDt').value);
    const recruitEndDt = new Date(document.getElementById('recruitEndDt').value);
    const prgStDt = new Date(document.getElementById('prgStDt').value);
    const prgEndDt = new Date(document.getElementById('prgEndDt').value);

    if (recruitStDt >= recruitEndDt) {
        alert('모집 시작일은 모집 마감일보다 이전이어야 합니다.');
        return false;
    }

    if (prgStDt >= prgEndDt) {
        alert('운영 시작일은 운영 종료일보다 이전이어야 합니다.');
        return false;
    }

    if (recruitEndDt > prgStDt) {
        alert('모집 마감일은 운영 시작일 이전이어야 합니다.');
        return false;
    }
	
	// ✅ 파일 검증 추가
	const fileInput = document.getElementById('imageFile');
	if (fileInput.files && fileInput.files[0]) {
	    const file = fileInput.files[0];
	    if (file.size === 0) {
	        alert('선택된 파일이 비어있습니다. 다른 파일을 선택해주세요.');
	        return false;
	    }
	    console.log('검증 통과 - 파일:', file.name, '크기:', file.size);
	}
	

    return true;
}

// 페이지 로드 시 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
    // 폼 제출 처리
    const programForm = document.getElementById('programForm');
    if (programForm) {
        programForm.addEventListener('submit', function(e) {
            
            
            // 유효성 검사
            if (!validateForm()) {
				e.preventDefault();
                return;
            }
            
            // 체크박스 값들을 히든 필드에 설정
            updateCompetencyIds();
			updateCompetencyScores(); // ✅ 이 줄 추가

			// 등록 확인
			if (!confirm('비교과 프로그램을 등록하시겠습니까?')) {
			    e.preventDefault(); // 사용자가 취소 시에만 제출 방지
			    return false;
			}
			
			// ✅ 파일 정보 최종 확인 (디버깅용)
			const fileInput = document.getElementById('imageFile');
			if (fileInput.files && fileInput.files[0]) {
			    console.log('제출 전 파일 확인:', fileInput.files[0].name, '크기:', fileInput.files[0].size);
			}
			
			// 모든 검증 통과 시 정상 제출 (preventDefault 없음)
			console.log('폼 제출 진행...');
			return true;
			
        });
    }

    // 체크박스 변경 시 히든 필드 업데이트
    const competencyCheckboxes = document.querySelectorAll('input[name="competencyCheckbox"]');
    competencyCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateCompetencyIds);
		checkbox.addEventListener('change', updateCompetencyScores); // ✅ 이 줄 추가
    });

    // ✅ 수정: 새로운 필드 ID로 변경
    const maxCntInput = document.getElementById('maxCnt');
    if (maxCntInput) {
        maxCntInput.addEventListener('input', function() {
            if (this.value < 1) {
                this.value = 1;
            }
        });
    }

    // ✅ 수정: 새로운 필드 ID로 변경
    const mlgDefScoreInput = document.getElementById('mlgDefScore');
    if (mlgDefScoreInput) {
        mlgDefScoreInput.addEventListener('input', function() {
            if (this.value < 0) {
                this.value = 0;
            }
        });
    }
	
	// ✅ 전화번호 자동 포맷팅 이벤트 추가
	const prgTelInput = document.getElementById('prgTel');
	if (prgTelInput) {
	    prgTelInput.addEventListener('input', function() {
	        formatPhoneNumber(this);
	    });
	    
	    // 붙여넣기 시에도 포맷팅 적용
	    prgTelInput.addEventListener('paste', function() {
	        setTimeout(() => {
	            formatPhoneNumber(this);
	        }, 10);
	    });
	}
	

    // 오늘 날짜보다 이전 날짜 선택 방지
    const today = new Date().toISOString().split('T')[0];
    // ✅ 수정: 새로운 필드 ID로 변경
    const dateInputs = ['recruitStDt', 'recruitEndDt', 'prgStDt', 'prgEndDt', 'surveyDt'];
    
	// ISO 포맷(YYYY-MM-DD)으로 나오는 로케일(캐나다) + timeZone 옵션 사용
	const todayKST = new Date().toLocaleDateString('en-CA', { timeZone: 'Asia/Seoul' });
	
    dateInputs.forEach(inputId => {
        const input = document.getElementById(inputId);
		if (input) input.setAttribute('min', todayKST);

    });
    
	// ✅ 새로 추가: 초기 로드시 체크된 항목 처리
	const initialCheckedBoxes = document.querySelectorAll('input[name="competencyCheckbox"]:checked');
	initialCheckedBoxes.forEach(checkbox => {
	    toggleScoreSelect(checkbox);
	});
	
	// ✅ 점수 선택 변경시 이벤트 추가
	document.addEventListener('change', function(e) {
	    if (e.target.classList.contains('score-select')) {
	        updateCompetencyScores();
	    }
	});
	
    // 페이지 로드 시 체크박스 상태 반영
    updateCompetencyIds();
	
	// 초기 점수 정보 설정
	updateCompetencyScores();
	
});





// 공통 JavaScript 로드 후 실행
if (typeof activateMenuByCurrentUrl === 'function') {
    activateMenuByCurrentUrl();
}