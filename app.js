'use strict';

// Shared state (var → window, also read by pdf-viewer.js).
var mode = 'portfolio';
var currentSection = 0;

function init() {
  document.querySelectorAll('.nav-item').forEach(item => {
    const open = () => showSection(Number(item.dataset.index));
    item.addEventListener('click', open);
    item.addEventListener('keydown', event => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        open();
      }
    });
  });

  document.getElementById('menu-btn').addEventListener('click', toggleSidebar);
  document.getElementById('sidebar-overlay').addEventListener('click', closeSidebar);
  document.getElementById('prev-btn').addEventListener('click', () => navigate(-1));
  document.getElementById('next-btn').addEventListener('click', () => navigate(1));
  document.getElementById('zoom-out').addEventListener('click', () => zoomChange(-1));
  document.getElementById('zoom-in').addEventListener('click', () => zoomChange(1));
  document.querySelectorAll('[data-mode]').forEach(button =>
    button.addEventListener('click', () => setMode(button.dataset.mode)));

  showSection(0);
  setMode('portfolio');
  initPDF();
}

window.showSection = function(index) {
  const sections = document.querySelectorAll('.portfolio-section');
  if (index < 0 || index >= sections.length) return;

  currentSection = index;
  closeSidebar();
  document.querySelectorAll('.nav-item').forEach((item, i) =>
    item.classList.toggle('active', i === index));
  sections.forEach((section, i) => { section.hidden = i !== index; });

  const display = document.getElementById('section-display');
  display.scrollTop = 0;
  document.getElementById('prev-btn').disabled = index === 0;
  document.getElementById('next-btn').disabled = index === sections.length - 1;
  document.getElementById('nav-indicator').textContent =
    `${index + 1} / ${sections.length}  ·  ${sections[index].dataset.title}`;
};

window.navigate = function(direction) {
  showSection(currentSection + direction);
};

window.setMode = function(nextMode) {
  mode = nextMode;
  document.getElementById('pdf-view').classList.toggle('hidden', mode !== 'pdf');
  document.getElementById('portfolio-view').classList.toggle('hidden', mode !== 'portfolio');
  document.getElementById('zoom-controls').style.display = mode === 'pdf' ? 'flex' : 'none';
  document.querySelectorAll('[data-mode]').forEach(button =>
    button.classList.toggle('active', button.dataset.mode === mode));
  if (mode === 'pdf' && pdfDoc && !pdfRendered) renderPDFNow();
};

window.toggleSidebar = function() {
  const sidebar = document.getElementById('sidebar');
  const open = sidebar.classList.toggle('open');
  document.getElementById('sidebar-overlay').classList.toggle('visible', open);
};

window.closeSidebar = function() {
  document.getElementById('sidebar').classList.remove('open');
  document.getElementById('sidebar-overlay').classList.remove('visible');
};

document.addEventListener('keydown', event => {
  if (mode !== 'portfolio') return;
  if (['INPUT', 'TEXTAREA', 'SELECT'].includes(document.activeElement.tagName)) return;
  if (event.key === 'ArrowLeft' || event.key === 'ArrowUp') navigate(-1);
  if (event.key === 'ArrowRight' || event.key === 'ArrowDown') navigate(1);
});

function esc(value) {
  return String(value)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
