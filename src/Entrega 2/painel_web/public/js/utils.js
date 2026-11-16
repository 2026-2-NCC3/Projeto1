export const esc = value=>String(value??'').replace(/[&<>"']/g,ch=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[ch]));
export const dateBR = (date,withTime=false) => {if(!date)return 'Não informado';const d=new Date(date);return Number.isNaN(d.getTime())?'Data inválida':new Intl.DateTimeFormat('pt-BR',{day:'2-digit',month:'short',year:'numeric',...(withTime?{hour:'2-digit',minute:'2-digit'}:{})}).format(d);};
export const dateInput = date => {if(!date)return '';const d=new Date(date);const shift=new Date(d.getTime()-d.getTimezoneOffset()*60000);return shift.toISOString().slice(0,16);};
export const short = (text,n=90) => String(text??'').length>n?String(text).slice(0,n-1)+'…':String(text??'');
export const initials = name => String(name||'PE').trim().split(/\s+/).slice(0,2).map(x=>x[0]).join('').toUpperCase();
const paths={
  grid:'<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
  book:'<path d="M4 19V5a2 2 0 0 1 2-2h13a1 1 0 0 1 1 1v17H6a2 2 0 0 1-2-2z"/><path d="M4 19a2 2 0 0 1 2-2h14"/><path d="M8 7h7"/>',
  calendar:'<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18"/>',
  users:'<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>',
  megaphone:'<path d="m3 11 18-6v14L3 13z"/><path d="M7 14.4 9 21h4l-2-6"/>',
  message:'<path d="M21 11.5a8.3 8.3 0 0 1-.9 3.8A8.5 8.5 0 0 1 12.5 20a8.3 8.3 0 0 1-3.8-.9L3 21l1.9-5.7a8.3 8.3 0 0 1-.9-3.8A8.5 8.5 0 0 1 12.5 3h.5a8.5 8.5 0 0 1 8 8v.5z"/>',
  qr:'<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><path d="M16 14v3h3m2-3v7h-7m0-3h3"/>',
  plus:'<path d="M12 5v14M5 12h14"/>',
  edit:'<path d="M12 20h9M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4z"/>',
  trash:'<path d="M3 6h18M8 6V4h8v2M5 6l1 15h12l1-15M10 10v7M14 10v7"/>',
  arrow:'<path d="M5 12h14m-6-6 6 6-6 6"/>',
  close:'<path d="M18 6 6 18M6 6l12 12"/>',
  search:'<circle cx="10.5" cy="10.5" r="6.5"/><path d="m16 16 5 5"/>',
  clock:'<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  map:'<path d="m3 6 6-3 6 3 6-3v15l-6 3-6-3-6 3zM9 3v15M15 6v15"/>',
  sync:'<path d="M20 7v5h-5M4 17v-5h5"/><path d="M5 9a8 8 0 0 1 14-3l1 1M19 15a8 8 0 0 1-14 3l-1-1"/>',
  check:'<path d="m5 12 4 4L19 6"/>',
  shield:'<path d="M12 2 4 6v6c0 5 3.5 8.5 8 10 4.5-1.5 8-5 8-10V6z"/><path d="m9 12 2 2 4-4"/>',
  info:'<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7h.01"/>',
  send:'<path d="m22 2-7 20-4-9-9-4zM22 2 11 13"/>',
  filter:'<path d="M4 7h16M7 12h10M10 17h4"/>',
  up:'<path d="m7 14 5-5 5 5"/>',
  chevron:'<path d="m9 18 6-6-6-6"/>',
  eye:'<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/>',
  download:'<path d="M12 3v12m-5-5 5 5 5-5M5 17v4h14v-4"/>'
};
export const icon = (name,cls='')=>`<svg class="ico ${cls}" viewBox="0 0 24 24" aria-hidden="true">${paths[name]||paths.info}</svg>`;
export const badge=(text,color='sage')=>`<span class="tag tag-${color}">${esc(text)}</span>`;
