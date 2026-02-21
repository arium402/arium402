//상태 관리 변수
let currentStatus = 'all';  // 현재 탭 (all/waiting/completed)
let currentPage = 0;        // 현재 페이지 (0부터 시작!)
const pageSize = 5;         // 페이지 크기

//마일리지 전환 신청 목록 조회(API호출)
function loadConversions(status, page){
	console.log(`목록 조회: status=${status}, page=${page}`);
	//API URL생성
	const url = `/api/admin/mileage_conversions?status=${status}&page=${page}&size=${pageSize}`;
	
	fetch(url)
		.then(response => {
			if(!response.ok){
				throw new Error('서버 응답 오류');
			}
			return response.json();
		}).then(data => {
			console.log('API 응답:', data);
			
			if(data.success){
				//성공: 테이블 업데이트
				updateTable(data.conversions, data.pagination);
			} else {
				//실패:에러 메세지 표시
				alert('목록 조회 실패: '+ data.error);
			}
		}).catch(error => {
			console.error('API 호출 실패:', error);
			alert('서버 연결 오류가 발생했습니다.');
		});

}



// 테이블 업데이트 (API 데이터 사용)
function updateTable(conversions, pagination){
	console.log('테이블 업데이트:', conversions);
	
	const tbody = document.getElementById('conversionTableBody');
	
	//데이터 없으면
	if(!conversions || conversions.length === 0) {
		tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 40px;">데이터가 없습니다.</td></tr>';
		updatePagination(pagination);
		return;
	}
	
	//테이블 생성
	tbody.innerHTML = '';
	conversions.forEach((item, index) => {
		const row = document.createElement('tr');
		
		//순번 계산 (페이지 고려)
		const no = pagination.currentPage * pagination.size + index + 1;
		
		row.innerHTML = `
		    <td>${no}</td>
		    <td class="student-id">${item.stdNo}</td>
		    <td class="student-name">${item.stdNm}</td>
	        <td class="mileage-amount">${item.appliedMile.toLocaleString()}점</td>
		    <td class="application-date">${item.applyDateFmt}</td>
		    <td><span class="status-badge ${item.statusClass}">${item.statusNm}</span></td>
		`;
		
		//행 클릭 이벤트 (모달)
		row.style.cursor = 'pointer';
		row.addEventListener('click', () => openModal(item.mlgUseId));
		
		tbody.appendChild(row);
	});
	//페이지네이션 업데이트
	updatePagination(pagination);
}

// 페이지네이션 업데이트(API 데이터 활용))
function updatePagination(pagination) {
    const paginationDiv = document.querySelector('.pagination');
	const paginationInfo = document.getElementById('paginationInfo');
	//페이지 버튼 생성
	paginationDiv.innerHTML = '';
	//이전 버튼
	const prevBtn = document.createElement('button');
	prevBtn.className = 'pagination-btn';
	prevBtn.textContent = '‹';
	prevBtn.disabled = !pagination.hasPrevious;
	prevBtn.onclick = () => changePage(-1);
	paginationDiv.appendChild(prevBtn);
	
	//페이지 번호 버튼들
	const startPage = Math.max(0, pagination.currentPage - 2);
	const endPage = Math.min(pagination.totalPages -1, pagination.currentPage + 2);
	
	for(let i = startPage; i <= endPage; i++){
		const pageBtn = document.createElement('button');
		pageBtn.className = 'pagination-btn' + (i === pagination.currentPage ? ' active' : '');
		pageBtn.textContent = i + 1;
		pageBtn.onclick = () => goToPage(i);
		paginationDiv.appendChild(pageBtn);
	}
	
	// 다음 버튼
	const nextBtn = document.createElement('button');
	nextBtn.className = 'pagination-btn';
	nextBtn.textContent = '›';
	nextBtn.disabled = !pagination.hasNext;
	nextBtn.onclick = () => changePage(1);
	paginationDiv.appendChild(nextBtn);
	
	//페이지 정보
	const startItem = pagination.currentPage * pagination.size + 1;
	const endItem = Math.min((pagination.currentPage + 1) * pagination.size, pagination.totalElements);
	
	paginationInfo.textContent = pagination.totalElements > 0
		? `총 ${pagination.totalElements}건 중 ${startItem}-${endItem}건 표시`
	    : '데이터가 없습니다';
	
}

//탭 클릭 이벤트
document.querySelectorAll('.tab-btn').forEach(btn => {
	btn.addEventListener('click', function(){
		//탭 활성화 상태 변경
		document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
		this.classList.add('active');
		
		//상태 및 페이지 초기화
		currentStatus = this.dataset.tab;  // 'all', 'waiting', 'completed'
		currentPage = 0;  // 첫 페이지로
		        
		// API 호출
		loadConversions(currentStatus, currentPage);
	});
});


// 페이지 변경 (이전/다음)
function changePage(direction) {
    const newPage = currentPage + direction;
	//페이지 범위 체크는 API에서 처리됨
	//버튼 disabled로 이미 막혀있음
	currentPage = newPage;
	loadConversions(currentStatus, currentPage);
}

// 특정 페이지로 이동
function goToPage(page) {
    currentPage = page;
    loadConversions(currentStatus, currentPage);
}

// 모달 열기(API로 상세 조회)
function openModal(mlgUseId) {
	console.log('모달 열기: mlgUseId=', mlgUseId);
	//API호출
	fetch(`/api/admin/mileage_conversions/${mlgUseId}`)
	.then(response => {
		if (!response.ok){
			throw new Error('상세 조회 실패');
		}
		return response.json();
		
	}).then(data => {
		console.log('모달 데이터:', data);
		if(data.success){
			//모달에 데이터 표시
			displayModalData(data.conversion);
			//모달 열기
			document.getElementById('conversionModal').style.display = 'flex';
			//현재 데이터 저장 (승인처리용)
			window.currentConversion = data.conversion;
		}else{
			alert('상세 조회 실패: '+ data.error);
		}
	}).catch(error => {
		console.error('모달 API 호출 실패:', error);
		alert('상세정보를 불러오는 중 오류발생!');
	});
}

//모달에 데이터 표시
function displayModalData(data){
	//학생정보
	document.getElementById('modalStudentId').textContent = data.stdNo;
	document.getElementById('modalDepartment').textContent = data.deptNm;
	document.getElementById('modalStudentName').textContent = data.stdNm;
	//신청정보
	document.getElementById('modalMileage').textContent = `${data.appliedMile.toLocaleString()}점`;
	//계좌정보
	document.getElementById('modalBank').textContent = data.bankNm || '정보없음';
	document.getElementById('modalAccount').textContent = data.accountNo || '정보없음';
	document.getElementById('modalAccountHolder').textContent = data.depositor || '정보없음';
	//전환내역(고정)
	const conversionText = `마일리지 ${data.appliedMile.toLocaleString()}점 <span class="conversion-arrow">→</span> 장학금 ${data.convertMoneyFmt}원`;
	    document.getElementById('conversionText').innerHTML = conversionText;
}


// 전환 표시 업데이트
function updateConversionDisplay() {
    const rate = conversionRates[currentRateIndex];
    document.getElementById('conversionText').innerHTML = 
        `마일리지 ${rate.mileage.toLocaleString()}점 <span class="conversion-arrow">→</span> 장학금 ${rate.scholarship.toLocaleString()}원`;
}

/**
 * 전환 승인 처리 (나중에 구현)
 */
function processConversion() {
    const data = window.currentConversion;
    
    if (!data) {
        alert('데이터를 불러오는 중입니다.');
        return;
    }
    
    if (confirm(`${data.stdNm} 학생의 마일리지 ${data.appliedMile}점을 ${data.convertMoneyFmt}원으로 전환 승인하시겠습니까?`)) {
        const today = new Date().toISOString().split('T')[0];
		
		// 승인 API 호출
        fetch(`/api/admin/mileage_conversions/${data.mlgUseId}/approve`,{
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({
				money: data.convertMoney,
				payDate: today
			}) 
		}).then(response => response.json())
		.then(result => {
			if(result.success){
				alert('마일리지 전환이 승인되었습니다.');
				closeModal();
				loadConversions(currentStatus, currentPage);
			}else{
				alert('승인 실패: ' + result.error);
			}
		}).catch(error =>{
			console.error('승인 API 호출 실패:', error);
			alert('승인 처리 중 오류가 발생했습니다.');
		});
    }
}

// 모달 닫기
function closeModal() {
    document.getElementById('conversionModal').style.display = 'none';
}

// 모달 외부 클릭 시 닫기
document.getElementById('conversionModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});


//페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('페이지 로드 완료 - 초기 데이터 로딩');
    // API로 초기 데이터 로드 (전체, 첫 페이지)
    loadConversions('all', 0);
});