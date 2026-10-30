import { test } from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs/promises';
import os from 'node:os';
import path from 'node:path';
import { build, discover, parse, render, here } from '../build.mjs';
const root=path.resolve(here,'..');
const decode=s=>s.replace(/&amp;/g,'&').replace(/&#(?:39|x27);/g,"'").replace(/&quot;/g,'"').replace(/&lt;/g,'<').replace(/&gt;/g,'>');
test('all source tutorials generate readable pages, valid links and unique anchors',async()=>{
 const temp=await fs.mkdtemp(path.join(os.tmpdir(),'java-docs-'));
 try {
  const docs=await build(root,temp);assert.ok(docs.length>=26);assert.ok(docs.every(d=>d.source.zh));
  const pages=new Map();
  for(const mode of ['zh','en','both']) for(const doc of docs) pages.set(path.join(temp,mode,doc.id+'.html'),await fs.readFile(path.join(temp,mode,doc.id+'.html'),'utf8'));
  for(const [file,html] of pages) {
   const ids=[...html.matchAll(/\bid="([^"]+)"/g)].map(m=>m[1]);assert.equal(ids.length,new Set(ids).size,`duplicate ids: ${file}`);
   for(const [,raw] of html.matchAll(/(?:href|src)="([^"]+)"/g)) {
    const url=decode(raw);if(/^[a-z]+:|^\/\//i.test(url)) continue;
    const [target,anchor]=url.split('#');const dest=target?path.resolve(path.dirname(file),target):file;
    await assert.doesNotReject(fs.access(dest),`${file}: missing ${url}`);
    if(anchor) assert.ok(pages.get(dest)?.includes(`id="${anchor}"`),`${file}: missing anchor ${url}`);
   }
   const doc=docs.find(d=>path.basename(file)===d.id+'.html');const mode=path.basename(path.dirname(file));
   for(const lang of (mode==='both'?['zh','en']:[mode])) {
    const parsed=parse(doc.source[lang]||doc.source.zh,lang);
    assert.ok(html.includes('class="prose"'));assert.ok(html.includes('id="content"'));
    if(mode==='both' || mode===lang) for(const h of parsed.headings) assert.ok(html.includes(`id="${h.id}"`),`${file}: dropped source heading ${h.id}`);
   }
  }
  // Compare code text after removing highlighting, including translated comments and escaped generics.
  for(const doc of docs) for(const mode of ['zh','en','both']) {
   const html=pages.get(path.join(temp,mode,doc.id+'.html'));
   const actual=[...html.matchAll(/<pre><code[^>]*>([\s\S]*?)<\/code><\/pre>/g)].map(m=>decode(m[1].replace(/<[^>]*>/g,'')));
   const expected=(mode==='both'?['zh','en']:[mode]).flatMap(l=>parse(doc.source[l]||doc.source.zh,l).tokens.filter(t=>t.type==='fence').map(t=>t.content));
   assert.deepEqual([...actual].sort(),[...expected].sort(),`${mode}/${doc.id}: code content changed`);
  }
 } finally {await fs.rm(temp,{recursive:true,force:true});}
});
test('duplicate headings remain unique and raw HTML is escaped',()=>{
 const p=parse('# Example\n\n## Same\n\n## Same\n\n<script>alert(1)</script>','zh');
 assert.equal(p.slugs.get('same'),'zh-h-1');assert.equal(p.slugs.get('same-1'),'zh-h-2');
 const d={id:'index',dir:'',files:{zh:'README.md'},title:{zh:'Example',en:'Example'},source:{zh:'# Example\n\n<script>alert(1)</script>',en:null}};
 const html=render(d,[d],'en');assert.ok(!html.includes('<script>alert'));assert.ok(html.includes('English translation is not available'));assert.ok(html.includes('&lt;script&gt;'));
});
test('mismatched translation structure keeps both complete documents',()=>{
 const d={id:'index',dir:'',files:{zh:'README.md',en:'readme.en.md'},title:{zh:'测试',en:'Test'},source:{zh:'# 测试\n\n## 一\n\n中文最后一句',en:'# Test\n\n## One\n\nEnglish part\n\n## Two\n\nLast English sentence'}};
 const html=render(d,[d],'both');assert.ok(html.includes('中文最后一句'));assert.ok(html.includes('Last English sentence'));assert.ok(html.includes('id="en-h-2"'));
});
test('new numbered tutorials are discovered automatically and unrelated docs excluded',async()=>{
 const temp=await fs.mkdtemp(path.join(os.tmpdir(),'java-docs-discovery-'));
 try {await fs.mkdir(path.join(temp,'26-new'));await fs.mkdir(path.join(temp,'doc-web'));await fs.writeFile(path.join(temp,'README.md'),'# Guide');await fs.writeFile(path.join(temp,'26-new','README.md'),'# New');await fs.writeFile(path.join(temp,'doc-web','README.md'),'# Not a tutorial');const docs=await discover(temp);assert.deepEqual(docs.map(d=>d.id),['index','26-new']);assert.equal(docs[1].source.en,null);}finally{await fs.rm(temp,{recursive:true,force:true});}
});
