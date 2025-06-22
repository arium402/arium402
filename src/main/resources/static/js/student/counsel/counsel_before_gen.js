// 사이드바 메뉴 토글
function toggleMenu(menuId) {
	const submenu = document.getElementById(menuId);
	const menuTitle = submenu.previousElementSibling;
    
	// 모든 메뉴 닫기
	document.querySelectorAll('.submenu').forEach(menu => {
		if (menu !== submenu) {
			menu.classList.remove('show');
 			menu.previousElementSibling.classList.remove('active');
		}
	});

	// 클릭된 메뉴 토글
	submenu.classList.toggle('show');
	menuTitle.classList.toggle('active');
}

// 옵션 선택 토글
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
		relation: [],
		emotion: [],
		habit: [],
		gender: [],
		other: [],
		currentDifficulty: document.getElementById('currentDifficulty').value
	};

	// 각 카테고리별 선택된 항목 수집
	document.querySelectorAll('input[name="relation"]:checked').forEach(item => {
		selectedItems.relation.push(item.value);
	});
	document.querySelectorAll('input[name="emotion"]:checked').forEach(item => {
		selectedItems.emotion.push(item.value);
	});
	document.querySelectorAll('input[name="habit"]:checked').forEach(item => {
		selectedItems.habit.push(item.value);
	});
	document.querySelectorAll('input[name="gender"]:checked').forEach(item => {
		selectedItems.gender.push(item.value);
	});
	document.querySelectorAll('input[name="other"]:checked').forEach(item => {
		selectedItems.other.push(item.value);
	});

	// 결과 출력 (실제 구현에서는 다음 페이지로 이동)
	console.log('선택된 항목들:', selectedItems);
	alert('사전 검사가 완료되었습니다. 상담사 선택 페이지로 이동합니다.');
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
});