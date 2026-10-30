const mode = document.documentElement.dataset.mode;
try { localStorage.setItem('java-docs-language', mode); } catch {}
const search = document.querySelector('#chapter-search');
search.addEventListener('input', () => {
  const query = search.value.trim().toLocaleLowerCase(); let count = 0;
  document.querySelectorAll('.chapter').forEach(link => {link.hidden = !link.dataset.search.toLocaleLowerCase().includes(query); if (!link.hidden) count++;});
  document.querySelector('#no-results').hidden = count > 0;
});
const visibleHeadings = [...document.querySelectorAll('article [data-heading]')].filter(h => mode !== 'both' || h.id.startsWith('zh-'));
const tocLinks = [...document.querySelectorAll('.toc-link')];
let scheduled = false;
function updatePosition() {
  scheduled = false;
  let current = visibleHeadings[0];
  for (const h of visibleHeadings) { if (h.getBoundingClientRect().top <= 130) current = h; else break; }
  if (!current) return;
  tocLinks.forEach(a => {if(a.hash === '#' + current.id) a.setAttribute('aria-current','location');else a.removeAttribute('aria-current');});
  document.querySelectorAll('[data-language]').forEach(a => {
    const prefix = a.dataset.prefix;
    a.hash = `${prefix}-h-${Math.min(Number(current.dataset.heading), Number(a.dataset.lastHeading))}`;
  });
}
window.addEventListener('scroll', () => {if(!scheduled){scheduled=true;requestAnimationFrame(updatePosition);}}, {passive:true});
requestAnimationFrame(updatePosition);
// If a translation is absent or has a different heading count, recover to the closest available heading.
if(location.hash && !document.getElementById(location.hash.slice(1))) {
  const match=location.hash.match(/-(h-\d+)$/);const fallback=match && document.getElementById('zh-'+match[1]);
  if(fallback) fallback.scrollIntoView();
}
document.querySelectorAll('.copy').forEach(button => button.addEventListener('click', async () => {
  const text = button.closest('.code-block').querySelector('code').textContent;
  const initial = button.textContent;
  try {await navigator.clipboard.writeText(text);button.textContent = mode === 'en' ? 'Copied' : '已复制';}
  catch {button.textContent = mode === 'en' ? 'Select to copy' : '请选中复制';const range=document.createRange();range.selectNodeContents(button.closest('.code-block').querySelector('code'));const selection=getSelection();selection.removeAllRanges();selection.addRange(range);}
  setTimeout(() => button.textContent = initial, 1600);
}));
const mobile = matchMedia('(max-width: 760px)');
const medium = matchMedia('(max-width: 1199px)');
function panels() {document.querySelector('.nav-panel').open = !mobile.matches;document.querySelector('.toc-panel').open = !medium.matches;}
panels();mobile.addEventListener('change',panels);medium.addEventListener('change',panels);
// A directory-selected anchor is the stable language-switch target, even when
// a browser scrolls a sticky link into view just before dispatching its click.
document.querySelectorAll('[data-language]').forEach(link => link.addEventListener('click', () => {
  const selected = location.hash.match(/-(h-)(\d+)$/);
  if (selected) link.hash = `${link.dataset.prefix}-h-${Math.min(Number(selected[2]), Number(link.dataset.lastHeading))}`;
}));
