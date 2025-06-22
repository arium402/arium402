   // 전역 변수
   let qaCounter = 1;
   let currentEditingIndex = 0;
   let draggedElement = null;
   let orderCounter = 1;

   // 소분류 데이터
   const subCategories = {
       'extracurricular': [
           { value: 'program-apply', text: '프로그램 신청' },
           { value: 'certificate', text: '자격증 취득' },
           { value: 'language', text: '어학 프로그램' },
           { value: 'completion', text: '수료증 발급' }
       ],
       'counseling': [
           { value: 'psychology', text: '심리상담' },
           { value: 'career', text: '진로상담' },
           { value: 'anonymous', text: '익명상담' },
           { value: 'reservation', text: '예약 안내' }
       ],
       'etc': [
           { value: 'facility', text: '시설이용' },
           { value: 'mileage', text: '마일리지' },
           { value: 'website', text: '홈페이지 이용' },
           { value: 'inquiry', text: '기타 문의' }
       ]
   };

   // 샘플 F&Q 데이터
   const sampleFAQData = [
       { id: 1, question: "비교과 프로그램 신청은 어떻게 하나요?", answer: "비교과 프로그램 신청은 학교 홈페이지 로그인 후 비교과 메뉴에서 신청하실 수 있습니다.", category: "extracurricular", subCategory: "program-apply" },
       { id: 2, question: "토익 특강 일정이 언제인가요?", answer: "토익 특강은 매월 첫째, 셋째 주 토요일에 진행됩니다. 자세한 일정은 공지사항을 확인해주세요.", category: "extracurricular", subCategory: "language" },
       { id: 3, question: "심리상담은 어떻게 신청하나요?", answer: "심리상담은 온라인 상담신청 또는 전화로 신청하실 수 있습니다. 상담은 무료로 제공됩니다.", category: "counseling", subCategory: "psychology" },
       { id: 4, question: "진로상담 예약 방법이 궁금합니다", answer: "진로상담은 학생상담센터 홈페이지에서 예약하거나 직접 방문하여 신청하실 수 있습니다.", category: "counseling", subCategory: "career" },
       { id: 5, question: "마일리지는 어떻게 적립되나요?", answer: "마일리지는 비교과 프로그램 참여, 상담 이용, 봉사활동 등을 통해 적립됩니다.", category: "etc", subCategory: "mileage" },
       { id: 6, question: "도서관 이용시간을 알고 싶습니다", answer: "중앙도서관은 평일 09:00-22:00, 주말 09:00-18:00에 이용 가능합니다.", category: "etc", subCategory: "facility" }
   ];


   // 대분류 선택 시 소분류 업데이트
   document.getElementById('mainCategory').addEventListener('change', function() {
       const subCategorySelect = document.getElementById('subCategory');
       const selectedCategory = this.value;
       
       subCategorySelect.innerHTML = '<option value="">선택하세요</option>';
       
       if (selectedCategory && subCategories[selectedCategory]) {
           subCategories[selectedCategory].forEach(sub => {
               const option = document.createElement('option');
               option.value = sub.value;
               option.textContent = sub.text;
               subCategorySelect.appendChild(option);
           });
       }
   });

   // 순서 번호 업데이트
   function updateOrderNumbers() {
       const qaItems = document.querySelectorAll('.qa-item');
       qaItems.forEach((item, index) => {
           const orderNumber = item.querySelector('.qa-order-number');
           if (orderNumber) {
               orderNumber.textContent = index + 1;
           }
       });
   }

   // Q&A 아이템 추가
   function addQAItem() {
       const container = document.getElementById('qaListContainer');
       const newItem = document.createElement('div');
       orderCounter++;
       
       newItem.className = 'qa-item empty';
       newItem.setAttribute('data-index', qaCounter);
       newItem.setAttribute('draggable', 'true');
       
       newItem.innerHTML = `
           <div class="drag-handle">
               <div class="drag-dot"></div>
               <div class="drag-dot"></div>
               <div class="drag-dot"></div>
               <div class="drag-dot"></div>
               <div class="drag-dot"></div>
           </div>
           <div class="qa-order-number">${orderCounter}</div>
           <div class="qa-content">
               <div class="qa-question">
                   <span class="qa-label">Q:</span>
                   <div class="qa-question-content empty">불러오기 버튼을 클릭하여 질문을 선택하세요</div>
                   <div class="qa-controls">
                       <button class="btn-load" onclick="openFAQSearchModal(${qaCounter})">불러오기</button>
                       <button class="btn-remove" onclick="removeQAItem(${qaCounter})">
                           <i class="fas fa-trash"></i>
                           삭제
                       </button>
                   </div>
               </div>
               <div class="qa-answer">
                   <span class="qa-answer-label">ㄴ A:</span>
                   <div class="qa-answer-content empty">질문을 선택하면 답변이 자동으로 입력됩니다</div>
               </div>
           </div>
       `;
       
       container.appendChild(newItem);
       
       // 드래그 이벤트 리스너 추가
       setupDragEvents(newItem);
       
       qaCounter++;
       updateOrderNumbers();
       
       // 스크롤을 새로 추가된 아이템으로 이동
       newItem.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
   }

   // Q&A 아이템 제거
   function removeQAItem(index) {
       const item = document.querySelector(`[data-index="${index}"]`);
       if (item) {
           // 첫 번째 아이템인 경우 삭제하지 않고 초기화
           if (index === 0) {
               const questionContent = item.querySelector('.qa-question-content');
               const answerContent = item.querySelector('.qa-answer-content');
               const qaControls = item.querySelector('.qa-controls');
               
               questionContent.textContent = '불러오기 버튼을 클릭하여 질문을 선택하세요';
               questionContent.classList.add('empty');
               
               answerContent.textContent = '질문을 선택하면 답변이 자동으로 입력됩니다';
               answerContent.classList.add('empty');
               
               item.classList.add('empty');
               
               qaControls.innerHTML = `
                   <button class="btn-load" onclick="openFAQSearchModal(0)">불러오기</button>
               `;
           } else {
               // 다른 아이템들은 완전히 제거
               item.remove();
               updateOrderNumbers();
           }
       }
   }

   // 드래그 앤 드롭 설정
   function setupDragEvents(element) {
       element.addEventListener('dragstart', handleDragStart);
       element.addEventListener('dragend', handleDragEnd);
       element.addEventListener('dragover', handleDragOver);
       element.addEventListener('drop', handleDrop);
       element.addEventListener('dragenter', handleDragEnter);
       element.addEventListener('dragleave', handleDragLeave);
   }

   function handleDragStart(e) {
       draggedElement = this;
       this.classList.add('dragging');
       e.dataTransfer.effectAllowed = 'move';
       e.dataTransfer.setData('text/html', this.outerHTML);
   }

   function handleDragEnd(e) {
       this.classList.remove('dragging');
       draggedElement = null;
   }

   function handleDragOver(e) {
       if (e.preventDefault) {
           e.preventDefault();
       }
       
       if (this !== draggedElement) {
           const container = document.getElementById('qaListContainer');
           const allItems = Array.from(container.children);
           const draggedIndex = allItems.indexOf(draggedElement);
           const targetIndex = allItems.indexOf(this);

           if (draggedIndex !== targetIndex) {
               if (draggedIndex < targetIndex) {
                   // 아래로 이동
                   container.insertBefore(draggedElement, this.nextSibling);
               } else {
                   // 위로 이동
                   container.insertBefore(draggedElement, this);
               }
               // 실시간으로 순서 번호 업데이트
               updateOrderNumbers();
           }
       }
       
       e.dataTransfer.dropEffect = 'move';
       return false;
   }

   function handleDragEnter(e) {
       // 실시간 이동으로 인해 더 이상 필요 없음
   }

   function handleDragLeave(e) {
       // 실시간 이동으로 인해 더 이상 필요 없음
   }

   function handleDrop(e) {
       if (e.stopPropagation) {
           e.stopPropagation();
       }
       
       // 실시간 이동으로 인해 여기서는 별도 처리 불필요
       // 순서 번호는 이미 dragOver에서 업데이트됨
       
       return false;
   }

   // F&Q 검색 모달 열기
   function openFAQSearchModal(index) {
       currentEditingIndex = index;
       const modal = document.getElementById('faqSearchModal');
       modal.style.display = 'block';
       
       // 현재 선택된 카테고리에 따라 필터링된 데이터 표시
       displayFAQSearchResults();
   }

   // F&Q 검색 모달 닫기
   function closeFAQSearchModal() {
       const modal = document.getElementById('faqSearchModal');
       modal.style.display = 'none';
       document.getElementById('faqSearchInput').value = '';
   }

   // F&Q 검색 결과 표시
   function displayFAQSearchResults(searchTerm = '') {
       const selectedMainCategory = document.getElementById('mainCategory').value;
       const selectedSubCategory = document.getElementById('subCategory').value;
       const resultsContainer = document.getElementById('faqSearchResults');
       
       let filteredData = sampleFAQData;
       
       // 카테고리 필터링
       if (selectedMainCategory) {
           filteredData = filteredData.filter(item => item.category === selectedMainCategory);
       }
       
       if (selectedSubCategory) {
           filteredData = filteredData.filter(item => item.subCategory === selectedSubCategory);
       }
       
       // 검색어 필터링
       if (searchTerm) {
           filteredData = filteredData.filter(item => 
               item.question.toLowerCase().includes(searchTerm.toLowerCase()) ||
               item.answer.toLowerCase().includes(searchTerm.toLowerCase())
           );
       }
       
       resultsContainer.innerHTML = '';
       
       if (filteredData.length === 0) {
           resultsContainer.innerHTML = '<p style="text-align: center; color: #6c757d; padding: 20px;">검색 결과가 없습니다.</p>';
           return;
       }
       
       filteredData.forEach(item => {
           const faqItem = document.createElement('div');
           faqItem.className = 'faq-item';
           faqItem.innerHTML = `
               <div class="faq-question-text">${item.question}</div>
               <button class="btn-select" onclick="selectFAQItem(${item.id})">선택</button>
           `;
           resultsContainer.appendChild(faqItem);
       });
   }

   // F&Q 검색
   function searchFAQData() {
       const searchTerm = document.getElementById('faqSearchInput').value;
       displayFAQSearchResults(searchTerm);
   }

   // F&Q 아이템 선택
   function selectFAQItem(faqId) {
       const selectedFAQ = sampleFAQData.find(item => item.id === faqId);
       if (selectedFAQ) {
           const qaItem = document.querySelector(`[data-index="${currentEditingIndex}"]`);
           const questionContent = qaItem.querySelector('.qa-question-content');
           const answerContent = qaItem.querySelector('.qa-answer-content');
           const qaControls = qaItem.querySelector('.qa-controls');
           
           questionContent.textContent = selectedFAQ.question;
           questionContent.classList.remove('empty');
           
           answerContent.textContent = selectedFAQ.answer;
           answerContent.classList.remove('empty');
           
           qaItem.classList.remove('empty');
           
           // 불러오기 버튼 제거하고 삭제 버튼만 표시
           qaControls.innerHTML = `
               <button class="btn-remove" onclick="removeQAItem(${currentEditingIndex})">
                   <i class="fas fa-trash"></i>
                   삭제
               </button>
           `;
           
           closeFAQSearchModal();
       }
   }

   // 미리보기 모달 열기
   function openPreviewModal() {
       const modal = document.getElementById('previewModal');
       const previewContent = document.getElementById('previewContent');
       
       // 현재 입력된 Q&A 데이터 수집
       const qaItems = document.querySelectorAll('.qa-item:not(.empty)');
       let previewHTML = '<h2 class="preview-page-title">자주 묻는 질문(F&Q)</h2>';
       
       qaItems.forEach((item, index) => {
           const question = item.querySelector('.qa-question-content').textContent;
           const answer = item.querySelector('.qa-answer-content').textContent;
           
           if (question.trim() && answer.trim() && !item.classList.contains('empty')) {
               previewHTML += `
                   <div class="preview-qa-item">
                       <div class="preview-question" onclick="togglePreviewAnswer(${index})">
                           <div>
                               <i class="fas fa-question-circle"></i>
                               ${question}
                           </div>
                           <i class="fas fa-chevron-down preview-toggle-icon"></i>
                       </div>
                       <div class="preview-answer">
                           <div class="preview-answer-content">
                               ${answer.replace(/\n/g, '<br>')}
                           </div>
                       </div>
                   </div>
               `;
           }
       });
       
       if (previewHTML === '<h2 class="preview-page-title">자주 묻는 질문(F&Q)</h2>') {
           previewHTML += '<p style="text-align: center; color: #6c757d; padding: 40px;">등록된 Q&A가 없습니다.</p>';
       }
       
       previewContent.innerHTML = previewHTML;
       modal.style.display = 'block';
   }

   // 미리보기에서 답변 토글
   function togglePreviewAnswer(index) {
       const allQuestions = document.querySelectorAll('.preview-question');
       const allAnswers = document.querySelectorAll('.preview-answer');
       const currentQuestion = allQuestions[index];
       const currentAnswer = allAnswers[index];
       
       // 모든 다른 항목들을 닫기
       allQuestions.forEach((q, i) => {
           if (i !== index) {
               q.classList.remove('active');
               allAnswers[i].classList.remove('active');
           }
       });
       
       // 현재 항목 토글
       currentQuestion.classList.toggle('active');
       currentAnswer.classList.toggle('active');
   }

   // 미리보기 모달 닫기
   function closePreviewModal() {
       const modal = document.getElementById('previewModal');
       modal.style.display = 'none';
   }

   // 페이지 등록
   function registerPage() {
       const mainCategory = document.getElementById('mainCategory').value;
       const subCategory = document.getElementById('subCategory').value;
       
       if (!mainCategory || !subCategory) {
           alert('대분류와 소분류를 모두 선택해주세요.');
           return;
       }
       
       const qaItems = document.querySelectorAll('.qa-item:not(.empty)');
       
       if (qaItems.length === 0) {
           alert('최소 하나 이상의 Q&A를 선택해주세요.');
           return;
       }
       
       if (confirm('페이지를 등록하시겠습니까?')) {
           alert('페이지가 성공적으로 등록되었습니다.');
           // 여기서 실제 등록 로직 구현
           window.location.href = 'faqManagement.html'; // 원래 페이지로 돌아가기
       }
   }

   // 등록 취소
   function cancelRegister() {
       if (confirm('등록을 취소하시겠습니까? 입력된 내용이 모두 사라집니다.')) {
           window.location.href = 'faqManagement.html'; // 원래 페이지로 돌아가기
       }
   }

   // 모달 외부 클릭 시 닫기
   window.onclick = function(event) {
       const faqSearchModal = document.getElementById('faqSearchModal');
       const previewModal = document.getElementById('previewModal');
       
       if (event.target === faqSearchModal) {
           closeFAQSearchModal();
       }
       
       if (event.target === previewModal) {
           closePreviewModal();
       }
   }

   // 엔터키로 검색 실행
   document.getElementById('faqSearchInput').addEventListener('keypress', function(e) {
       if (e.key === 'Enter') {
           searchFAQData();
       }
   });

   // 페이지 로드 시 초기화
   document.addEventListener('DOMContentLoaded', function() {
       // 게시판 관리 > F&Q 관리 메뉴 활성화
       const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
       const faqSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
       
       boardManagementMenu.classList.add('active');
       faqSubmenu.style.maxHeight = '200px';
       
       // 첫 번째 아이템에 드래그 이벤트 리스너 추가
       const firstItem = document.querySelector('.qa-item');
       if (firstItem) {
           setupDragEvents(firstItem);
       }
   });
