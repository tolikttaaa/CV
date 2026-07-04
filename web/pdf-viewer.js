'use strict';

// PDF state (var → window, read by app.js setMode)
var pdfDoc = null, pdfRendered = false, zoomIndex = 2, fitScale = 1;

const ZOOM_STEPS  = [0.5, 0.75, 1.0, 1.25, 1.5, 2.0];
const ZOOM_LABELS = ['50%', '75%', '100%', '125%', '150%', '200%'];

const pdfjsLib = window.pdfjsLib;
pdfjsLib.GlobalWorkerOptions.workerSrc =
  'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js';

async function initPDF() {
  const container = document.getElementById('viewer-container');
  try {
    pdfDoc = await pdfjsLib.getDocument('cv.pdf').promise;
    if (mode === 'pdf') renderPDFNow();
  } catch (err) {
    container.innerHTML = `<p style="color:#c00;padding:2rem">
      Failed to load PDF: ${esc(err.message)}</p>`;
  }
}

async function renderPDFNow() {
  const container = document.getElementById('viewer-container');
  const first = await pdfDoc.getPage(1);
  fitScale = (container.clientWidth - 48) / first.getViewport({ scale: 1 }).width;
  await renderAllPages();
  pdfRendered = true;
}

async function renderAllPages() {
  const container = document.getElementById('viewer-container');
  const scale = fitScale * ZOOM_STEPS[zoomIndex];
  container.innerHTML = '';
  for (let i = 1; i <= pdfDoc.numPages; i++) {
    const page = await pdfDoc.getPage(i);
    container.appendChild(await renderPDFPage(page, scale));
  }
  document.getElementById('zoom-label').textContent = ZOOM_LABELS[zoomIndex];
  document.getElementById('zoom-in').disabled  = zoomIndex >= ZOOM_STEPS.length - 1;
  document.getElementById('zoom-out').disabled = zoomIndex <= 0;
}

async function renderPDFPage(page, scale) {
  const dpr   = window.devicePixelRatio || 1;
  const cssVp = page.getViewport({ scale });
  const hqVp  = page.getViewport({ scale: scale * dpr });

  const wrapper = document.createElement('div');
  wrapper.className = 'pdf-page';
  wrapper.style.width  = `${cssVp.width}px`;
  wrapper.style.height = `${cssVp.height}px`;

  const canvas = document.createElement('canvas');
  canvas.width  = hqVp.width;
  canvas.height = hqVp.height;
  canvas.style.width  = `${cssVp.width}px`;
  canvas.style.height = `${cssVp.height}px`;
  await page.render({ canvasContext: canvas.getContext('2d'), viewport: hqVp }).promise;
  wrapper.appendChild(canvas);

  const annotations = await page.getAnnotations();
  const linkAnns = annotations.filter(a => a.subtype === 'Link' && a.url);
  if (linkAnns.length) {
    const layer = document.createElement('div');
    layer.className = 'links';
    for (const ann of linkAnns) {
      const [x1, y1, x2, y2] = cssVp.convertToViewportRectangle(ann.rect);
      const a = document.createElement('a');
      a.href = ann.url; a.target = '_blank'; a.rel = 'noopener noreferrer';
      a.style.left   = `${Math.min(x1, x2)}px`;
      a.style.top    = `${Math.min(y1, y2)}px`;
      a.style.width  = `${Math.abs(x2 - x1)}px`;
      a.style.height = `${Math.abs(y2 - y1)}px`;
      layer.appendChild(a);
    }
    wrapper.appendChild(layer);
  }
  return wrapper;
}

window.zoomChange = function(dir) {
  const n = zoomIndex + dir;
  if (n < 0 || n >= ZOOM_STEPS.length) return;
  zoomIndex = n;
  renderAllPages();
};
