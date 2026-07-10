'use strict';

var currentSection = 0;

const SECTION_ICONS = {
  summary:    'user',
  experience: 'briefcase',
  skills:     'bolt',
  projects:   'laptop-code',
  teaching:   'graduation-cap',
  education:  'book-open',
  references: 'quote-left',
};

const SOCIAL_ICONS = {
  phone:    ['fa-solid',  'phone'],
  telegram: ['fa-brands', 'telegram'],
  email:    ['fa-solid',  'envelope'],
  linkedin: ['fa-brands', 'linkedin'],
  leetcode: ['fa-solid',  'code'],
  github:   ['fa-brands', 'github'],
  address:  ['fa-solid',  'location-dot'],
};

// ── Sidebar ──────────────────────────────────────────────────────────────────

function buildSidebar() {
  const navItems = cvData.sections.map((sec, i) => {
    const ico = SECTION_ICONS[sec.id] || 'circle';
    return `<div class="nav-item" data-index="${i}" onclick="showSection(${i})">
      <i class="fa-solid fa-${ico}"></i>${sec.title}
    </div>`;
  }).join('');

  document.getElementById('sidebar').innerHTML = `
    <nav class="sb-nav">
      <div class="sb-nav-label">Sections</div>
      ${navItems}
    </nav>`;
}

// ── Section display ──────────────────────────────────────────────────────────

window.showSection = function(index) {
  currentSection = index;
  closeSidebar();
  document.querySelectorAll('.nav-item').forEach((el, i) =>
    el.classList.toggle('active', i === index));

  const sec = cvData.sections[index];
  const display = document.getElementById('section-display');
  display.innerHTML = renderSection(sec);
  display.scrollTop = 0;

  document.getElementById('prev-btn').disabled = index === 0;
  document.getElementById('next-btn').disabled = index === cvData.sections.length - 1;
  document.getElementById('nav-indicator').textContent =
    `${index + 1} / ${cvData.sections.length}  ·  ${plainTitle(sec.title)}`;
};

window.navigate = function(dir) {
  const n = currentSection + dir;
  if (n >= 0 && n < cvData.sections.length) showSection(n);
};

// ── Section renderers ────────────────────────────────────────────────────────

function sectionTitle(id, text) {
  const ico = SECTION_ICONS[id] || 'circle';
  return `<div class="sec-title"><i class="fa-solid fa-${ico}"></i>${text}</div>`;
}

function tags(arr) {
  if (!arr.length) return '';
  return `<div class="tags">${arr.map(t => `<span class="tag">${esc(t)}</span>`).join('')}</div>`;
}

function renderSection(sec) {
  switch (sec.type) {
    case 'summary':    return renderSummary(sec);
    case 'works':      return renderWorks(sec);
    case 'skills':     return renderSkills(sec);
    case 'projects':   return renderProjects(sec);
    case 'education':  return renderEducation(sec);
    case 'references': return renderReferences(sec);
    default: return `<p>Unknown section type: ${esc(sec.type)}</p>`;
  }
}

function renderSummary(sec) {
  const contactItems = cvData.social.map(s => {
    const [cls, ico] = SOCIAL_ICONS[s.type] || ['fa-solid', 'circle'];
    const icon = `<i class="${cls} fa-${ico}"></i>`;
    const label = `<span>${esc(s.text)}</span>`;
    if (s.url) {
      return `<a class="contact-item" href="${esc(s.url)}" target="_blank" rel="noopener">${icon}${label}</a>`;
    }
    return `<div class="contact-item">${icon}${label}</div>`;
  }).join('');

  return `<div class="summary-header">
      <div class="summary-header-name">${esc(cvData.name.first)} ${esc(cvData.name.last)}</div>
      <div class="summary-header-tagline">${esc(cvData.tagline)}</div>
    </div>
    <div class="summary-grid">
      <div class="contact-list">${contactItems}</div>
      <div class="summary-box">${sec.html}</div>
      ${cvData.photo ? `<img class="summary-photo" src="${esc(cvData.photo)}" alt="Photo"
           onerror="this.style.display='none'">` : ''}
    </div>`;
}

function renderWorks(sec) {
  const cards = sec.items.map(item => `
    <div class="work-card">
      <div class="work-header">
        <div>
          <div class="work-role">${esc(item.role)}</div>
          <div class="work-sub">${item.company_html} &middot; ${esc(item.location)}</div>
        </div>
        <div class="work-dates">${esc(item.dates)}</div>
      </div>
      <div class="work-desc">${item.description_html}</div>
      ${tags(item.tags)}
    </div>`).join('');
  return sectionTitle(sec.id, sec.title) + `<div class="work-entries">${cards}</div>`;
}

function renderSkills(sec) {
  const rows = sec.items.map(item =>
    `<div class="skill-row">
      <div class="skill-cat">${esc(item.category)}</div>
      <div class="skill-val">${esc(item.values)}</div>
    </div>`).join('');
  return sectionTitle(sec.id, sec.title) + `<div class="skills-table">${rows}</div>`;
}

function renderProjects(sec) {
  const cards = sec.items.map(item => `
    <div class="project-card">
      <div class="project-header">
        <div class="project-name">${esc(item.name)}</div>
        <div class="work-dates">${esc(item.dates)}</div>
      </div>
      <div class="project-sub">${item.company_html}</div>
      <div class="work-desc">${item.description_html}</div>
      ${tags(item.tags)}
    </div>`).join('');
  return sectionTitle(sec.id, sec.title) + `<div class="project-entries">${cards}</div>`;
}

function renderEducation(sec) {
  const entries = sec.items.map(item =>
    `<div class="edu-entry">
      <div class="edu-years">${esc(item.years)}</div>
      <div class="edu-desc">${item.description_html}</div>
    </div>`).join('');
  return sectionTitle(sec.id, sec.title) + `<div class="edu-entries">${entries}</div>`;
}

function renderReferences(sec) {
  const cards = sec.items.map(item => `
    <div class="ref-card">
      <div class="ref-name">${esc(item.name)}</div>
      <div class="ref-detail">
        <div>${esc(item.role)}</div>
        <div>${item.company_html} · ${esc(item.period)}</div>
        <div><a href="mailto:${esc(item.email)}">${esc(item.email)}</a></div>
      </div>
    </div>`).join('');
  return sectionTitle(sec.id, sec.title) + `<div class="ref-grid">${cards}</div>`;
}

// ── Sidebar toggle (mobile) ──────────────────────────────────────────────────

window.toggleSidebar = function() {
  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('sidebar-overlay');
  const open = sidebar.classList.toggle('open');
  overlay.classList.toggle('visible', open);
};

window.closeSidebar = function() {
  document.getElementById('sidebar').classList.remove('open');
  document.getElementById('sidebar-overlay').classList.remove('visible');
};

// ── Keyboard navigation ──────────────────────────────────────────────────────

document.addEventListener('keydown', e => {
  if (mode !== 'portfolio' || !cvData) return;
  if (['INPUT', 'TEXTAREA', 'SELECT'].includes(document.activeElement.tagName)) return;
  if (e.key === 'ArrowLeft'  || e.key === 'ArrowUp')   navigate(-1);
  if (e.key === 'ArrowRight' || e.key === 'ArrowDown') navigate(1);
});
