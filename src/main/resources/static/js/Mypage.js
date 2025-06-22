document.addEventListener('DOMContentLoaded', () => {
    const modals = document.querySelectorAll('.modal');
    const openModalButtons = document.querySelectorAll('.open-modal');
    const closeButtons = document.querySelectorAll('.close');

    openModalButtons.forEach(button => {
        button.addEventListener('click', async (event) => {
            event.preventDefault();
            const target = document.querySelector(button.getAttribute('data-target'));
            target.style.display = 'block';
        });
    });

    closeButtons.forEach(button => {
        button.addEventListener('click', () => {
            button.closest('.modal').style.display = 'none';
        });
    });

    window.addEventListener('click', (event) => {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = 'none';
        }
    });

    const detailCloseButton = detailModal.querySelector('.close');
    detailCloseButton.addEventListener('click', () => {
        detailModal.style.display = 'none';
        myActivitiesModal.style.display = 'block';
    });
});
