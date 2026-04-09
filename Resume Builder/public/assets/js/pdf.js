// PDF Page - Load data from localStorage and render resume

window.addEventListener('load', () => {
    setTimeout(() => {
        document.getElementById('loader').classList.add('hidden');
    }, 1300);
    loadResumeData();
});

function loadResumeData() {
    const get = (key) => localStorage.getItem('cv_' + key) || '';

    // Basic Info
    setText('myname',     get('myname'));
    setText('jobtitle',   get('jobtitle'));
    setText('email',      get('email'));
    setText('tel',        get('tel'));
    setText('locationTxt', get('location'));
    setText('profileText', get('profileText'));

    // LinkedIn
    const linkedinVal = get('linkedin');
    if (linkedinVal) {
        const linkEl = document.getElementById('linkedin');
        linkEl.href = linkedinVal.startsWith('http') ? linkedinVal : 'https://' + linkedinVal;
        linkEl.textContent = linkedinVal;
    } else {
        document.getElementById('linkedinRow').style.display = 'none';
    }

    // Education
    setText('degree',        get('degree'));
    setText('degreeyear',    get('degreeyear'));
    setText('clgname',       get('clgname'));
    setText('degreepercent', get('degreepercent'));
    setText('hsc',           get('hsc'));
    setText('hscyear',       get('hscyear'));
    setText('sclname',       get('sclname'));

    // Skills
    const skillsRaw = get('skills');
    const langRaw   = get('languages');
    const skillsWrap = document.getElementById('skillsWrap');
    const langWrap   = document.getElementById('languagesWrap');

    if (skillsRaw) {
        const skills = skillsRaw.split('|').map(s => s.trim()).filter(Boolean);
        skillsWrap.innerHTML = skills.map(s => `<span class="rv-skill-tag">${s}</span>`).join('');
    }
    if (langRaw) {
        const langs = langRaw.split('|').map(l => l.trim()).filter(Boolean);
        langWrap.innerHTML = langs.map(l => `<span class="rv-lang-tag">${l}</span>`).join('');
    }
    if (!skillsRaw && !langRaw) {
        document.getElementById('skillsSec').style.display = 'none';
    }

    // Project
    const ptitle = get('projectTitle');
    const pinfo  = get('projectInfo');
    const purl   = get('projectURL');
    if (ptitle || pinfo) {
        setText('projectTitle', ptitle);
        setText('projectInfo',  pinfo);
        if (purl) {
            const pLink = document.getElementById('projectURL');
            pLink.href = purl;
            pLink.style.display = 'flex';
        } else {
            document.getElementById('projectURL').style.display = 'none';
        }
    } else {
        document.getElementById('projectSec').style.display = 'none';
    }

    // Strengths
    const s1 = get('li1'), s2 = get('li2'), s3 = get('li3');
    if (s1 || s2 || s3) {
        setText('li1', s1); setText('li2', s2); setText('li3', s3);
        // Hide empty items
        ['li1','li2','li3'].forEach(id => {
            if (!get(id)) document.getElementById(id).style.display = 'none';
        });
    } else {
        document.getElementById('strengthSec').style.display = 'none';
    }

    // Hobbies
    const h1 = get('h1'), h2 = get('h2');
    if (h1 || h2) {
        const hobbiesWrap = document.getElementById('hobbiesWrap');
        hobbiesWrap.innerHTML = [h1, h2].filter(Boolean).map(h => `<span>${h}</span>`).join('');
    } else {
        document.getElementById('hobbySec').style.display = 'none';
    }

    // Achievements
    const ach = get('achievements');
    if (ach) {
        setText('achievements', ach);
    } else {
        document.getElementById('achieveSec').style.display = 'none';
    }

    // Profile
    if (!get('profileText')) {
        document.getElementById('profileSec').style.display = 'none';
    }
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el && value) el.textContent = value;
}

// Photo upload
function loadFile(event) {
    const imgBox = document.getElementById('imgBox');
    const file = event.target.files[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    imgBox.style.background = `url(${url}) center/cover`;
    imgBox.style.backgroundSize = 'cover';
    document.getElementById('fileUpload').style.opacity = '0';
}

// Download PDF
function downloadPDF() {
    const invoice = document.getElementById('invoice');
    document.getElementById('fileUpload').style.display = 'none';

    const name = localStorage.getItem('cv_myname') || 'Resume';

    const opt = {
        margin: 0.3,
        filename: `${name}-Resume-CVCraft.pdf`,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, useCORS: true, letterRendering: true },
        jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
    };

    html2pdf().from(invoice).set(opt).save().then(() => {
        document.getElementById('fileUpload').style.display = 'flex';
    });
}