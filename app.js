'use strict';

// Shared mutable state (var → window, accessible across all scripts)
var mode = 'portfolio';
var cvData = null;

async function init() {
  let jsonOk = false;
  try {
    const resp = await fetch('cv-data.json');
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    cvData = await resp.json();
    document.getElementById('h-name').textContent =
      `${cvData.name.first} ${cvData.name.last}`;
    document.getElementById('h-tagline').textContent = cvData.tagline;
    buildSidebar();
    showSection(0);
    jsonOk = true;
  } catch (e) {
    console.warn('Portfolio data unavailable:', e.message);
    const btn = document.querySelector('[data-mode="portfolio"]');
    btn.disabled = true;
    btn.title = 'Portfolio data not found (run generator: ./gradlew run)';
  }

  setMode(jsonOk ? 'portfolio' : 'pdf');
  initPDF();
}

window.setMode = function(m) {
  mode = m;
  document.getElementById('pdf-view').classList.toggle('hidden', m !== 'pdf');
  document.getElementById('portfolio-view').classList.toggle('hidden', m !== 'portfolio');
  const zc = document.getElementById('zoom-controls');
  zc.style.display = m === 'pdf' ? 'flex' : 'none';
  document.querySelectorAll('[data-mode]').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.mode === m);
  });
  if (m === 'pdf' && pdfDoc && !pdfRendered) renderPDFNow();
};

function esc(s) {
  return String(s)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function plainTitle(t) {
  const d = document.createElement('div');
  d.innerHTML = t;
  return d.textContent;
}
