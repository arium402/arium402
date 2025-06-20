// JavaScript를 사용하여 모달을 열고 닫는 기능을 구현
document.addEventListener('DOMContentLoaded', function () {
    var modal = document.getElementById('modal');
    var overlay = document.getElementById('overlay');
    var openModalBtn = document.getElementById('openModalBtn');
    var closeModalBtn = document.querySelector('.modal .close');

    // 모달 열기
    openModalBtn.addEventListener('click', function () {
        modal.style.display = 'block';
        overlay.style.display = 'block';
    });

    // 모달 닫기
    closeModalBtn.addEventListener('click', function () {
        modal.style.display = 'none';
        overlay.style.display = 'none';
    });

    // 오버레이 클릭 시 모달 닫기
    overlay.addEventListener('click', function () {
        modal.style.display = 'none';
        overlay.style.display = 'none';
    });
});
