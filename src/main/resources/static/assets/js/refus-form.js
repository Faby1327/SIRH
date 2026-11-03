document.querySelectorAll('[data-open-refus]').forEach((button) => {
  button.addEventListener('click', () => {
    const form = document.querySelector(button.dataset.openRefus);
    if (!form) return;
    form.hidden = false;
    form.querySelector('textarea')?.focus();
    button.closest('.actions')?.setAttribute('hidden', '');
  });
});

document.querySelectorAll('[data-close-refus]').forEach((button) => {
  button.addEventListener('click', () => {
    const form = button.closest('[data-refus-form]');
    if (!form) return;
    form.hidden = true;
    form.closest('.bloc')?.querySelector('.actions')?.removeAttribute('hidden');
  });
});
