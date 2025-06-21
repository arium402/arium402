// 샘플 학생 데이터
const studentData = [
	{
		name: "홍길동",
		studentId: "2023001001",
		department: "컴퓨터과학과",
		phone: "010-1234-5678",
		email: "hong@knou.ac.kr"
	},
	{
		name: "김철수",
		studentId: "2023001002", 
		department: "경영학과",
		phone: "010-2345-6789",
		email: "kim@knou.ac.kr"
	},
	{
		name: "이영희",
		studentId: "2023001003",
		department: "간호학과", 
		phone: "010-3456-7890",
		email: "lee@knou.ac.kr"
	},
	{
		name: "박민수",
		studentId: "2023001004",
		department: "법학과",
		phone: "010-4567-8901", 
		email: "park@knou.ac.kr"
	},
	{
		name: "최지영",
		studentId: "2023001005",
		department: "교육학과",
		phone: "010-5678-9012",
		email: "choi@knou.ac.kr"
	}
];

// 모달 관련 함수들
function openSearchModal() {
	const modal = document.getElementById('searchModal');
	modal.style.display = 'flex';
	document.getElementById('searchInput').focus();
}

function closeSearchModal() {
	document.getElementById('searchModal').style.display = 'none';
	document.getElementById('searchInput').value = '';
	displaySearchResults([]);
}

// 학생 검색 함수
function searchStudents() {
	const searchTerm = document.getElementById('searchInput').value.toLowerCase().trim();

	if (searchTerm === '') {
		displaySearchResults([]);
		return;
	}

	const filteredStudents = studentData.filter(student => 
		student.name.toLowerCase().includes(searchTerm)
	);

	displaySearchResults(filteredStudents);
}

// 검색 버튼 클릭 또는 엔터키 처리
function performSearch() {
	searchStudents();
}

// 엔터키 처리
function handleEnterKey(event) {
	if (event.key === 'Enter') {
		performSearch();
	}
}

// 검색 결과 표시 함수
function displaySearchResults(results) {
	const tbody = document.getElementById('searchResults');

	if (results.length === 0) {
		const searchTerm = document.getElementById('searchInput').value.trim();
		tbody.innerHTML = `
			<tr>
				<td colspan="7" class="no-results">
					${searchTerm === '' ? '이름을 입력하여 검색해주세요.' : '검색 결과가 없습니다.'}
				</td>
			</tr>
		`;
		return;
	}

	tbody.innerHTML = results.map((student, index) => `
		<tr>
			<td>${index + 1}</td>
			<td>${student.name}</td>
			<td>${student.studentId}</td>
			<td>${student.department}</td>
			<td>${student.phone}</td>
			<td>${student.email}</td>
			<td>
				<button class="select-btn" onclick="selectStudent(${index}, '${student.name}', '${student.studentId}', '${student.department}', '${student.phone}', '${student.email}')">
					선택
				</button>
			</td>
		</tr>
	`).join('');
}

// 학생 선택 함수
function selectStudent(index, name, studentId, department, phone, email) {
	// 폼에 데이터 입력
	document.getElementById('studentName').value = name;
	document.getElementById('studentId').value = studentId;
	document.getElementById('department').value = department;
	document.getElementById('phone').value = phone;
	document.getElementById('email').value = email;

	// 모달 닫기
	closeSearchModal();

	alert(`${name}님의 정보가 입력되었습니다.`);
}

// 신청 완료 함수
function submitApplication() {
	const studentName = document.getElementById('studentName').value;

	if (!studentName) {
		alert('학생 정보를 먼저 검색하여 입력해주세요.');
		return;
	}

	if (confirm('비교과 프로그램 신청을 완료하시겠습니까?')) {
		alert('신청이 완료되었습니다!');

		// 신청 완료 후 페이지 이동 확인
		if (confirm('신청내역 페이지로 이동하시겠습니까?')) {
			// 확인 클릭 시 - 신청내역 페이지로 이동
			alert('신청내역 페이지로 이동합니다.');
			location.href = /*[[@{/student/noncurr/addcheck}]]*/ '/student/noncurr/addcheck';
		}
		else {
			// 아니오 클릭 시 - 메인 페이지로 이동
			alert('메인 페이지로 이동합니다.');
			location.href = /*[[@{/student/noncurr/list}]]*/ '/student/noncurr/list';
		}
	}
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
	const modal = document.getElementById('searchModal');
	if (event.target === modal) {
		closeSearchModal();
	}
}

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
	if (event.key === 'Escape') {
		const modal = document.getElementById('searchModal');
		if (modal.style.display === 'flex') {
			closeSearchModal();
		}
	}
});