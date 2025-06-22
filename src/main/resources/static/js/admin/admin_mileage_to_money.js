   // 샘플 전환 신청 데이터
   const conversionData = [
       { 
           id: 1, 
           studentId: "20240001", 
           name: "김학생", 
           department: "컴퓨터과학과",
           mileage: 1500, 
           applicationDate: "2025.06.15", 
           status: "waiting",
           bank: "국민은행",
           account: "123-456-789012",
           accountHolder: "김학생"
       },
       { 
           id: 2, 
           studentId: "20240002", 
           name: "이학생", 
           department: "경영학과",
           mileage: 2000, 
           applicationDate: "2025.06.14", 
           status: "completed",
           bank: "신한은행",
           account: "234-567-890123",
           accountHolder: "이학생"
       },
       { 
           id: 3, 
           studentId: "20240003", 
           name: "박학생", 
           department: "영어영문학과",
           mileage: 1200, 
           applicationDate: "2025.06.13", 
           status: "waiting",
           bank: "우리은행",
           account: "345-678-901234",
           accountHolder: "박학생"
       },
       { 
           id: 4, 
           studentId: "20240004", 
           name: "최학생", 
           department: "행정학과",
           mileage: 1800, 
           applicationDate: "2025.06.12", 
           status: "completed",
           bank: "하나은행",
           account: "456-789-012345",
           accountHolder: "최학생"
       },
       { 
           id: 5, 
           studentId: "20240005", 
           name: "정학생", 
           department: "법학과",
           mileage: 2500, 
           applicationDate: "2025.06.11", 
           status: "waiting",
           bank: "농협은행",
           account: "567-890-123456",
           accountHolder: "정학생"
       },
       { 
           id: 6, 
           studentId: "20240006", 
           name: "강학생", 
           department: "교육학과",
           mileage: 1000, 
           applicationDate: "2025.06.10", 
           status: "completed",
           bank: "기업은행",
           account: "678-901-234567",
           accountHolder: "강학생"
       }
   ];

   let currentTab = 'all';
   let currentPage = 1;
   const itemsPerPage = 5;
   let conversionRates = [
       { mileage: 1500, scholarship: 150000 },
       { mileage: 1500, scholarship: 130000 },
       { mileage: 1500, scholarship: 120000 }
   ];
   let currentRateIndex = 0;

   // 사이드바 토글 기능
   document.getElementById('sidebarToggle').addEventListener('click', function() {
       const sidebar = document.getElementById('layoutSidenav_nav');
       const content = document.getElementById('layoutSidenav_content');
       
       if (window.innerWidth <= 768) {
           sidebar.classList.toggle('show');
       } else {
           sidebar.classList.toggle('collapsed');
           content.classList.toggle('expanded');
       }
   });


   // 탭 전환 기능
   document.querySelectorAll('.tab-btn').forEach(btn => {
       btn.addEventListener('click', function() {
           // 탭 버튼 활성화 상태 변경
           document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
           this.classList.add('active');
           
           // 현재 탭 설정 및 페이지 리셋
           currentTab = this.dataset.tab;
           currentPage = 1;
           
           // 테이블 업데이트
           updateTable();
       });
   });

   // 테이블 업데이트 함수
   function updateTable() {
       const tbody = document.getElementById('conversionTableBody');
       let filteredData = conversionData;

       // 탭에 따른 데이터 필터링
       if (currentTab === 'waiting') {
           filteredData = conversionData.filter(item => item.status === 'waiting');
       } else if (currentTab === 'completed') {
           filteredData = conversionData.filter(item => item.status === 'completed');
       }

       // 페이지네이션 적용
       const startIndex = (currentPage - 1) * itemsPerPage;
       const endIndex = startIndex + itemsPerPage;
       const paginatedData = filteredData.slice(startIndex, endIndex);

       // 테이블 생성
       tbody.innerHTML = '';
       paginatedData.forEach(item => {
           const row = document.createElement('tr');
           
           const statusClass = item.status === 'waiting' ? 'status-waiting' : 'status-completed';
           const statusText = item.status === 'waiting' ? '대기' : '완료';
           
           row.innerHTML = `
               <td>${item.id}</td>
               <td class="student-id">${item.studentId}</td>
               <td class="student-name">${item.name}</td>
               <td class="mileage-amount">${item.mileage.toLocaleString()}점</td>
               <td class="application-date">${item.applicationDate}</td>
               <td><span class="status-badge ${statusClass}">${statusText}</span></td>
           `;
           
           // 행 클릭 이벤트 추가
           row.addEventListener('click', () => openModal(item));
           
           tbody.appendChild(row);
       });

       // 페이지네이션 업데이트
       updatePagination(filteredData.length);
   }

   // 페이지네이션 업데이트 함수
   function updatePagination(totalItems) {
       const totalPages = Math.ceil(totalItems / itemsPerPage);
       const pagination = document.querySelector('.pagination');
       const startItem = (currentPage - 1) * itemsPerPage + 1;
       const endItem = Math.min(currentPage * itemsPerPage, totalItems);
       
       // 페이지네이션 버튼 재생성
       pagination.innerHTML = `
           <button class="pagination-btn" id="prevBtn" onclick="changePage(-1)" ${currentPage === 1 ? 'disabled' : ''}>‹</button>
       `;

       // 페이지 번호 버튼들
       const startPage = Math.max(1, currentPage - 2);
       const endPage = Math.min(totalPages, currentPage + 2);
       
       for (let i = startPage; i <= endPage; i++) {
           pagination.innerHTML += `
               <button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="goToPage(${i})">${i}</button>
           `;
       }

       // 다음 버튼
       pagination.innerHTML += `
           <button class="pagination-btn" id="nextBtn" onclick="changePage(1)" ${currentPage === totalPages ? 'disabled' : ''}>›</button>
       `;

       // 페이지 정보 업데이트
       document.getElementById('paginationInfo').textContent = 
           `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
   }

   // 페이지 변경 함수
   function changePage(direction) {
       let filteredData = conversionData;
       if (currentTab === 'waiting') {
           filteredData = conversionData.filter(item => item.status === 'waiting');
       } else if (currentTab === 'completed') {
           filteredData = conversionData.filter(item => item.status === 'completed');
       }

       const totalPages = Math.ceil(filteredData.length / itemsPerPage);
       const newPage = currentPage + direction;

       if (newPage >= 1 && newPage <= totalPages) {
           currentPage = newPage;
           updateTable();
       }
   }


   // 모달 열기
   function openModal(data) {
       document.getElementById('modalStudentId').textContent = data.studentId;
       document.getElementById('modalDepartment').textContent = data.department;
       document.getElementById('modalStudentName').textContent = data.name;
       document.getElementById('modalMileage').textContent = `${data.mileage.toLocaleString()}점`;
       document.getElementById('modalBank').textContent = data.bank;
       document.getElementById('modalAccount').textContent = data.account;
       document.getElementById('modalAccountHolder').textContent = data.accountHolder;
       
       // 전환율 초기화
       currentRateIndex = 0;
       conversionRates = [
           { mileage: data.mileage, scholarship: data.mileage * 100 },
           { mileage: data.mileage, scholarship: data.mileage * 90 },
           { mileage: data.mileage, scholarship: data.mileage * 80 }
       ];
       updateConversionDisplay();
       
       // 모달 표시
       document.getElementById('conversionModal').style.display = 'flex';
       
       // 현재 데이터 저장
       window.currentConversionData = data;
   }

   // 모달 닫기
   function closeModal() {
       document.getElementById('conversionModal').style.display = 'none';
   }

   // 전환율 토글
   function toggleConversion() {
       currentRateIndex = (currentRateIndex + 1) % conversionRates.length;
       updateConversionDisplay();
   }

   // 전환 표시 업데이트
   function updateConversionDisplay() {
       const rate = conversionRates[currentRateIndex];
       document.getElementById('conversionText').innerHTML = 
           `마일리지 ${rate.mileage.toLocaleString()}점 <span class="conversion-arrow">→</span> 장학금 ${rate.scholarship.toLocaleString()}원`;
   }

   // 전환 처리
   function processConversion() {
       if (confirm('마일리지를 장학금으로 전환하시겠습니까?')) {
           // 데이터 상태 업데이트
           const data = window.currentConversionData;
           const index = conversionData.findIndex(item => item.id === data.id);
           if (index !== -1) {
               conversionData[index].status = 'completed';
           }
           
           alert('마일리지 전환이 완료되었습니다.');
           closeModal();
           updateTable();
       }
   }

   // 모달 외부 클릭 시 닫기
   document.getElementById('conversionModal').addEventListener('click', function(e) {
       if (e.target === this) {
           closeModal();
       }
   });

