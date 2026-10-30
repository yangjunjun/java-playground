import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';

export const here = path.dirname(fileURLToPath(import.meta.url));
const repoURL = 'https://github.com/yangjunjun/java-playground';
export const escape = s => String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const plain = s => s.replace(/[`*_]/g, '');
const slugify = s => plain(s).toLowerCase().replace(/[^\p{L}\p{N}\s_-]/gu, '').replace(/\s/g, '-');
const md = new MarkdownIt({html:false, linkify:true, highlight(code, lang) {
  return lang && hljs.getLanguage(lang) ? hljs.highlight(code, {language:lang}).value : escape(code);
}});
const originalFence = md.renderer.rules.fence;
md.renderer.rules.fence = (tokens, i, options, env, self) => `<div class="code-block"><div class="code-bar"><span>${escape(tokens[i].info.split(/\s/)[0] || 'text')}</span><button class="copy" type="button">${env.lang === 'en' ? 'Copy' : '复制'}</button></div>${originalFence(tokens,i,options,env,self)}</div>`;
const originalTable = md.renderer.rules.table_open;
md.renderer.rules.table_open = (...args) => '<div class="table-scroll" tabindex="0">' + (originalTable ? originalTable(...args) : '<table>');
md.renderer.rules.table_close = () => '</table></div>';

export async function discover(root) {
  const dirs = (await fs.readdir(root, {withFileTypes:true})).filter(d=>d.isDirectory() && /^\d+-/.test(d.name)).map(d=>d.name).sort((a,b)=>a.localeCompare(b, 'en', {numeric:true}));
  const docs=[];
  for (const dir of ['', ...dirs]) {
    const entries=await fs.readdir(path.join(root,dir));
    const zh=entries.find(n=>/^readme\.md$/i.test(n));
    if (!zh) continue;
    const en=entries.find(n=>/^readme\.en\.md$/i.test(n));
    const files={zh: path.posix.join(dir,zh), en:en ? path.posix.join(dir,en) : null};
    const source={zh:await fs.readFile(path.join(root,files.zh),'utf8'), en:en ? await fs.readFile(path.join(root,files.en),'utf8') : null};
    const title=Object.fromEntries(['zh','en'].map(l=>[l,plain(source[l]?.match(/^#\s+(.+)$/m)?.[1] || (l==='en' ? dir || 'Overview' : dir))]));
    docs.push({id:dir || 'index', dir,files,source,title});
  }
  return docs;
}
export function parse(source, lang) {
  const tokens=md.parse(source || '',{});
  const headings=[]; const slugs=new Map(); const duplicates=new Map();
  for(let i=0;i<tokens.length;i++) if(tokens[i].type==='heading_open') {
    const text=plain(tokens[i+1].content); const base=slugify(text); const count=duplicates.get(base)||0; duplicates.set(base,count+1);
    const id=`${lang}-h-${headings.length}`;
    const oldSlug=base+(count ? `-${count}` : ''); slugs.set(oldSlug,id);
    tokens[i].attrSet('id',id); tokens[i].attrSet('data-heading',String(headings.length));
    headings.push({id,text,level:Number(tokens[i].tag.slice(1)),oldSlug});
  }
  const sections=[]; let current=[];
  for(const token of tokens) {
    if(token.type==='heading_open' && Number(token.tag.slice(1))<=2 && current.length) {sections.push(current);current=[];}
    current.push(token);
  }
  if(current.length) sections.push(current);
  return {tokens,headings,slugs,sections};
}
function rewrite(tokens, doc, docs, lang, mode, parsed) {
  for(const token of tokens) {
    if(token.type==='link_open' || token.type==='image') {
      const attr=token.type==='image'?'src':'href'; const value=token.attrGet(attr);
      if(value && !/^(?:[a-z][a-z\d+.-]*:|\/\/)/i.test(value)) {
        const [raw,fragment]=value.split('#'); let target;
        const resolved=path.posix.normalize(path.posix.join(doc.dir,decodeURIComponent(raw || ''))).replace(/\/$/,'');
        if(!raw) target=doc;
        else target=docs.find(d=>resolved===d.dir || resolved===d.files.zh || resolved===d.files.en || (resolved==='.' && d.id==='index'));
        if(target) {
          let hash='';
          if(fragment) {
            const p=target.id===doc.id ? parsed : parse(target.source[lang] || target.source.zh,lang);
            hash='#'+(p.slugs.get(decodeURIComponent(fragment)) || `${lang}-h-0`);
          }
          token.attrSet(attr, `${target.id}.html${hash}`);
        } else token.attrSet(attr,`${repoURL}/${attr==='src'?'raw':'blob'}/master/${resolved}${fragment?'#'+fragment:''}`);
      }
    }
    if(token.children) rewrite(token.children,doc,docs,lang,mode,parsed);
  }
}
const ui={zh:{chapters:'教程目录',search:'搜索章节',toc:'本页内容',prev:'上一章',next:'下一章',source:'查看原文',overview:'阅读指南',empty:'没有匹配的章节',label:'Java 学习笔记',fallback:'本章暂无英文译文，以下显示中文原文。'},en:{chapters:'CHAPTERS',search:'Find a chapter',toc:'ON THIS PAGE',prev:'Previous',next:'Next',source:'View source',overview:'Reading guide',empty:'No matching chapters',label:'Java learning notes',fallback:'English translation is not available. The Chinese source is shown below.'}};
function title(d,lang) {return d.id==='index' ? ui[lang].overview : d.title[lang].replace(/（[^）]*）/g,'').trim();}
export function render(doc,docs,mode) {
  const lang=mode==='en'?'en':'zh'; const t=ui[lang];
  const zh=parse(doc.source.zh,'zh'); const en=parse(doc.source.en || doc.source.zh,doc.source.en?'en':'zh');
  rewrite(zh.tokens,doc,docs,'zh',mode,zh);rewrite(en.tokens,doc,docs,doc.source.en?'en':'zh',mode,en);
  const html=(tokens,l)=>md.renderer.render(tokens,md.options,{lang:l});
  let body='';
  if(mode==='both' && doc.source.en) {
    const aligned=zh.sections.length===en.sections.length && zh.headings.map(h=>h.level).join()===en.headings.map(h=>h.level).join();
    body=aligned ? zh.sections.map((s,i)=>`<section class="parallel-section"><div class="prose" lang="zh-CN">${html(s,'zh')}</div><div class="prose" lang="en">${html(en.sections[i],'en')}</div></section>`).join('') : `<p class="notice">两种语言的章节结构不同，以下保留两份完整原文。</p><div class="parallel-section"><div class="prose" lang="zh-CN">${html(zh.tokens,'zh')}</div><div class="prose" lang="en">${html(en.tokens,'en')}</div></div>`;
  } else body=`${mode!=='zh'&&!doc.source.en?`<p class="notice">${t.fallback}</p>`:''}<div class="prose" lang="${lang==='en'&&doc.source.en?'en':'zh-CN'}">${html(lang==='en'?en.tokens:zh.tokens,lang)}</div>`;
  const headings=(lang==='en'?en:zh).headings.filter(h=>h.level===2 || h.level===3);
  const toc=headings.map(h=>`<a class="toc-link depth-${h.level}" href="#${h.id}">${escape(h.text)}</a>`).join('');
  const nav=docs.map(d=>`<a class="chapter${d.id===doc.id?' active':''}" ${d.id===doc.id?'aria-current="page"':''} data-search="${escape(d.id+' '+d.title.zh+' '+d.title.en)}" href="${d.id}.html"><span class="chapter-number">${d.id==='index'?'—':d.id.split('-')[0]}</span><span>${escape(title(d,lang))}</span></a>`).join('');
  const idx=docs.indexOf(doc);
  const pager=[[-1,t.prev],[1,t.next]].map(([offset,label])=>{const d=docs[idx+offset];return d?`<a class="page-${offset===-1?'prev':'next'}" href="${d.id}.html"><small>${label} ${offset===-1?'↑':'↓'}</small><span>${escape(title(d,lang))}</span></a>`:'<span></span>';}).join('');
  return `<!doctype html><html lang="${lang==='en'?'en':'zh-CN'}" data-mode="${mode}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><meta name="color-scheme" content="light"><meta name="description" content="${escape(title(doc,lang))} · Java Playground"><title>${escape(title(doc,lang))} · Java Playground</title><link rel="icon" href="../assets/favicon.svg" type="image/svg+xml"><link rel="stylesheet" href="../assets/style.css"><script src="../assets/app.js" defer></script></head><body><a class="skip" href="#content">${lang==='en'?'Skip to content':'跳转到正文'}</a><header class="topbar"><a class="brand" href="index.html">Java<span class="brand-rest"> Playground</span><span class="brand-tag">DOCS</span></a><span class="site-note">${t.label}</span><nav class="language-switch" aria-label="Language / 阅读语言">${[['zh','中文'],['en','English'],['both','中英对照']].map(([m,l])=>`<a href="../${m}/${doc.id}.html" data-language="${m}" data-prefix="${m==='en'&&doc.source.en?'en':'zh'}" data-last-heading="${(m==='en'?en:zh).headings.length-1}" ${m===mode?'aria-current="true"':''}>${l}</a>`).join('')}</nav><a class="repo-link" href="${repoURL}">GitHub ↗</a></header><div class="layout"><aside class="sidebar"><details class="nav-panel" open><summary>${t.chapters}</summary><div class="nav-body"><label class="search-box"><span class="sr-only">${t.search}</span><input id="chapter-search" type="search" placeholder="${t.search}" autocomplete="off"><span aria-hidden="true">⌕</span></label><nav aria-label="${t.chapters}">${nav}</nav><p id="no-results" role="status" hidden>${t.empty}</p><div class="sidebar-foot">Java 8 → 25<span>${docs.length-1} ${lang==='en'?'chapters':'个章节'}</span></div></div></details></aside><main id="content" tabindex="-1"><div class="article-meta"><span>JAVA PLAYGROUND <b>/</b> ${doc.id==='index'?(lang==='en'?'GUIDE':'阅读指南'):doc.id.split('-')[0]}</span><a href="${repoURL}/blob/master/${doc.files[lang]||doc.files.zh}">${t.source} ↗</a></div>${mode==='both'?'<div class="parallel-labels"><span>中文</span><span>ENGLISH</span></div>':''}<article>${body}</article><nav class="pagination" aria-label="${lang==='en'?'Chapter navigation':'章节翻页'}">${pager}</nav><footer>Java Playground <span>·</span> ${lang==='en'?'Learn a little, every day.':'循序渐进，学有所获。'}</footer></main><aside class="outline"><details class="toc-panel" open><summary>${t.toc}</summary><nav aria-label="${t.toc}">${toc}</nav></details></aside></div></body></html>`;
}
export async function build(root=path.resolve(here,'..'),out=path.join(here,'dist')) {
  const docs=await discover(root);
  if(!docs.length) throw new Error('No tutorials found');
  await fs.rm(out,{recursive:true,force:true});await fs.mkdir(path.join(out,'assets'),{recursive:true});
  await fs.cp(path.join(here,'assets'),path.join(out,'assets'),{recursive:true});
  for(const mode of ['zh','en','both']) {await fs.mkdir(path.join(out,mode),{recursive:true});for(const doc of docs) await fs.writeFile(path.join(out,mode,`${doc.id}.html`),render(doc,docs,mode));}
  await fs.writeFile(path.join(out,'index.html'),'<!doctype html><html lang="zh-CN"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><meta http-equiv="refresh" content="0;url=zh/index.html"><title>Java Playground</title><script src="assets/entry.js"></script></head><body><a href="zh/index.html">进入教程 / Read the documentation</a></body></html>');
  await fs.writeFile(path.join(out,'.nojekyll'),'');
  console.log(`Built ${docs.length} tutorials × 3 modes = ${docs.length*3} static pages.`);return docs;
}
if(process.argv[1] && path.resolve(process.argv[1])===fileURLToPath(import.meta.url)) await build();
