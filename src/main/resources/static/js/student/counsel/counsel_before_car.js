// 라디오 버튼 토글 (단일 선택)
function toggleRadio(optionElement, groupName) {
	const radio = optionElement.querySelector('input[type="radio"]');

	// 같은 그룹의 다른 옵션들 선택 해제
	document.querySelectorAll(`input[name="${groupName}"]`).forEach(input => {
		const parentOption = input.closest('.option-item');
		parentOption.classList.remove('selected');
		input.checked = false;
	});

	// 클릭된 옵션 선택
	radio.checked = true;
	optionElement.classList.add('selected');
}

// 옵션 선택 토글 (복수 선택)
function toggleOption(optionElement) {
	const checkbox = optionElement.querySelector('input[type="checkbox"]');
	checkbox.checked = !checkbox.checked;

	if (checkbox.checked) {
		optionElement.classList.add('selected');
	}
	else {
		optionElement.classList.remove('selected');
    }
}

// 글자 수 카운트 업데이트
function updateCharCount(textarea) {
	const charCount = document.getElementById('charCount');
	const currentLength = textarea.value.length;
	charCount.textContent = currentLength;

	if (currentLength > 1000) {
		textarea.value = textarea.value.substring(0, 1000);
		charCount.textContent = 1000;
	}
}

// 상담사 선택 버튼 클릭
function selectCounselor() {
	// 선택된 항목들 수집
	const selectedItems = {
		grade: document.querySelector('input[name="grade"]:checked')?.value || '',
		career_status: document.querySelector('input[name="career_status"]:checked')?.value || '',
		job_field: [],
		region: [],
		work_type: document.querySelector('input[name="work_type"]:checked')?.value || '',
		salary: document.querySelector('input[name="salary"]:checked')?.value || '',
		applicationReason: document.getElementById('applicationReason').value
	};

	// 복수 선택 항목들 수집
	document.querySelectorAll('input[name="job_field"]:checked').forEach(item => {
		selectedItems.job_field.push(item.value);
	});
	document.querySelectorAll('input[name="region"]:checked').forEach(item => {
		selectedItems.region.push(item.value);
	});

	// 결과 출력 (실제 구현에서는 다음 페이지로 이동)
	console.log('선택된 항목들:', selectedItems);
	alert('취업/진로 사전 검사가 완료되었습니다. 상담사 선택 페이지로 이동합니다.');
	location.href = /*[[@{/student/counsel/add/choose}]]*/ '/student/counsel/add/choose';
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
	// 체크박스 클릭 이벤트 처리
	document.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
		checkbox.addEventListener('change', function(e) {
			e.stopPropagation();
			const optionItem = this.closest('.option-item');
			if (this.checked) {
				optionItem.classList.add('selected');
			}
			else {
				optionItem.classList.remove('selected');
			}
		});
	});

	// 라디오 버튼 클릭 이벤트 처리
	document.querySelectorAll('input[type="radio"]').forEach(radio => {
		radio.addEventListener('change', function(e) {
			e.stopPropagation();
			const groupName = this.name;

			// 같은 그룹의 다른 옵션들 선택 해제
			document.querySelectorAll(`input[name="${groupName}"]`).forEach(input => {
				const parentOption = input.closest('.option-item');
				parentOption.classList.remove('selected');
			});

			// 선택된 옵션 표시
			const optionItem = this.closest('.option-item');
			optionItem.classList.add('selected');
		});
	});
});