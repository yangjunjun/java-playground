try { const mode=localStorage.getItem('java-docs-language'); if(['en','both'].includes(mode)) location.replace(mode+'/index.html'); } catch {}
