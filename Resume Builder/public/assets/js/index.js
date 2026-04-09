// ==========================================
// CV CRAFT - MAIN JAVASCRIPT
// ==========================================

// Loader
window.addEventListener('load', () => {
    setTimeout(() => {
        document.getElementById('loader').classList.add('hidden');
    }, 1600);
});

// Rotating text in hero
const words = ['Faster', 'Smarter', 'Easier', 'Beautifully'];
let wordIdx = 0;
const rotatingEl = document.getElementById('rotating-text');
setInterval(() => {
    wordIdx = (wordIdx + 1) % words.length;
    rotatingEl.style.opacity = '0';
    rotatingEl.style.transform = 'translateY(10px)';
    setTimeout(() => {
        rotatingEl.textContent = words[wordIdx];
        rotatingEl.style.transition = 'all 0.5s ease';
        rotatingEl.style.opacity = '1';
        rotatingEl.style.transform = 'translateY(0)';
    }, 300);
}, 2500);

// Scroll to builder
function scrollToBuilder() {
    document.getElementById('builder').scrollIntoView({ behavior: 'smooth' });
}
function scrollToPreview() {
    document.getElementById('scrollPreview').scrollIntoView({ behavior: 'smooth' });
}

// ==========================================
// STEP NAVIGATION
// ==========================================
let currentStep = 1;

function goToStep(step) {
    // Hide current step
    document.getElementById(`step${currentStep}`).classList.remove('active');
    document.getElementById(`step-dot-${currentStep}`).classList.remove('active');
    if (step > currentStep) {
        document.getElementById(`step-dot-${currentStep}`).classList.add('done');
    } else {
        document.getElementById(`step-dot-${currentStep}`).classList.remove('done');
    }

    // Show new step
    currentStep = step;
    document.getElementById(`step${currentStep}`).classList.add('active');
    document.getElementById(`step-dot-${currentStep}`).classList.add('active');

    // Scroll to form panel
    document.querySelector('.form-panel').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// ==========================================
// LIVE PREVIEW UPDATES
// ==========================================
const fields = [
    { input: 'myname',        preview: 'rv-name',          fallback: 'Your Name' },
    { input: 'jobtitle',      preview: 'rv-title',         fallback: 'Job Title' },
    { input: 'email',         preview: 'rv-email',         fallback: '<i class="uil uil-envelope"></i> email@example.com', icon: '📧 ' },
    { input: 'tel',           preview: 'rv-tel',           fallback: '<i class="uil uil-phone"></i> 9876543210', icon: '📞 ' },
    { input: 'location',      preview: 'rv-location',      fallback: '<i class="uil uil-map-marker"></i> Location', icon: '📍 ' },
    { input: 'profileText',   preview: 'rv-profile',       fallback: 'Your professional summary will appear here...' },
    { input: 'degree',        preview: 'rv-degree',        fallback: 'Degree Name' },
    { input: 'clgname',       preview: 'rv-clgname',       fallback: 'College Name' },
    { input: 'degreeyear',    preview: 'rv-degreeyear',    fallback: 'Year' },
    { input: 'degreepercent', preview: 'rv-degreepercent', fallback: '' },
    { input: 'hsc',           preview: 'rv-hsc',           fallback: '12th / HSC' },
    { input: 'sclname',       preview: 'rv-sclname',       fallback: 'School Name' },
    { input: 'hscyear',       preview: 'rv-hscyear',       fallback: '' },
    { input: 'projectTitle',  preview: 'rv-projectTitle',  fallback: 'Project Title' },
    { input: 'projectInfo',   preview: 'rv-projectInfo',   fallback: 'Project description...' },
    { input: 'li1',           preview: 'rv-li1',           fallback: 'Strength 1' },
    { input: 'li2',           preview: 'rv-li2',           fallback: 'Strength 2' },
    { input: 'li3',           preview: 'rv-li3',           fallback: 'Strength 3' },
    { input: 'achievements',  preview: 'rv-achievements',  fallback: '' },
];

fields.forEach(({ input, preview, fallback, icon }) => {
    const el = document.getElementById(input);
    const pv = document.getElementById(preview);
    if (!el || !pv) return;

    el.addEventListener('input', () => {
        const val = el.value.trim();
        if (input === 'email') {
            pv.innerHTML = val
                ? `<i class="uil uil-envelope"></i> ${val}`
                : '<i class="uil uil-envelope"></i> email@example.com';
        } else if (input === 'tel') {
            pv.innerHTML = val
                ? `<i class="uil uil-phone"></i> ${val}`
                : '<i class="uil uil-phone"></i> 9876543210';
        } else if (input === 'location') {
            pv.innerHTML = val
                ? `<i class="uil uil-map-marker"></i> ${val}`
                : '<i class="uil uil-map-marker"></i> Location';
        } else if (input === 'myname') {
            pv.textContent = val || fallback;
            // Update mini resume card too
            document.getElementById('miniName').textContent = val || 'Your Name';
        } else {
            pv.textContent = val || fallback;
        }
    });
});

// Project URL live update
document.getElementById('projectURL').addEventListener('input', function () {
    const link = document.getElementById('rv-projectURL');
    link.href = this.value || '#';
    link.style.display = this.value ? 'flex' : 'none';
});

// Skills live update
document.getElementById('skills').addEventListener('input', function () {
    const wrap = document.getElementById('rv-skills');
    const skills = this.value.split('|').map(s => s.trim()).filter(Boolean);
    if (skills.length === 0) {
        wrap.innerHTML = '<span class="rv-skill-tag">HTML</span><span class="rv-skill-tag">CSS</span>';
        return;
    }
    wrap.innerHTML = skills.map(s => `<span class="rv-skill-tag">${s}</span>`).join('');
});

// Languages live update
document.getElementById('languages').addEventListener('input', function () {
    const wrap = document.getElementById('rv-languages');
    const langs = this.value.split('|').map(l => l.trim()).filter(Boolean);
    wrap.innerHTML = langs.map(l => `<span class="rv-lang-tag">${l}</span>`).join('');
});

// Hobbies live update
['h1', 'h2'].forEach(id => {
    document.getElementById(id).addEventListener('input', updateHobbies);
});

function updateHobbies() {
    const h1 = document.getElementById('h1').value.trim();
    const h2 = document.getElementById('h2').value.trim();
    const wrap = document.getElementById('rv-hobbies');
    const hobbies = [h1, h2].filter(Boolean);
    wrap.innerHTML = hobbies.length
        ? hobbies.map(h => `<span>${h}</span>`).join('')
        : '<span>Reading</span><span>Coding</span>';
}

// LinkedIn field (no preview needed, but stored)
// Photo upload
function loadFile(event) {
    const imgBox = document.getElementById('imgBox');
    const file = event.target.files[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    imgBox.style.background = `url(${url}) center/cover`;
    document.getElementById('fileUpload').style.opacity = '0';
}

// ==========================================
// DOWNLOAD PDF
// ==========================================
function downloadResume() {
    saveToStorage();
    const invoice = document.getElementById('invoice');

    // Temporarily hide upload label for clean PDF
    document.getElementById('fileUpload').style.display = 'none';

    const name = document.getElementById('myname').value || 'Resume';

    const opt = {
        margin: 0.3,
        filename: `${name}-Resume-CVCraft.pdf`,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, useCORS: true },
        jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
    };

    html2pdf().from(invoice).set(opt).save().then(() => {
        document.getElementById('fileUpload').style.display = 'flex';
    });
}

// ==========================================
// SAVE / RESTORE FROM LOCALSTORAGE
// ==========================================
function saveToStorage() {
    const ids = ['myname', 'jobtitle', 'email', 'tel', 'location', 'linkedin',
        'profileText', 'degree', 'clgname', 'degreeyear', 'degreepercent',
        'hsc', 'sclname', 'hscyear', 'skills', 'languages',
        'projectTitle', 'projectInfo', 'projectURL',
        'li1', 'li2', 'li3', 'h1', 'h2', 'achievements'];
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) localStorage.setItem('cv_' + id, el.value);
    });
}

function restoreFromStorage() {
    const ids = ['myname', 'jobtitle', 'email', 'tel', 'location', 'linkedin',
        'profileText', 'degree', 'clgname', 'degreeyear', 'degreepercent',
        'hsc', 'sclname', 'hscyear', 'skills', 'languages',
        'projectTitle', 'projectInfo', 'projectURL',
        'li1', 'li2', 'li3', 'h1', 'h2', 'achievements'];
    ids.forEach(id => {
        const val = localStorage.getItem('cv_' + id);
        const el = document.getElementById(id);
        if (el && val) {
            el.value = val;
            el.dispatchEvent(new Event('input'));
        }
    });
}

// Auto-save every 30 seconds
setInterval(saveToStorage, 30000);

// Restore on page load
window.addEventListener('DOMContentLoaded', restoreFromStorage);

// Navbar scroll effect
window.addEventListener('scroll', () => {
    const nav = document.getElementById('navbar');
    if (window.scrollY > 50) {
        nav.style.background = 'rgba(10,10,15,0.95)';
    } else {
        nav.style.background = 'rgba(10,10,15,0.8)';
    }
});

console.log('🚀 CV Craft - Premium Resume Builder loaded!');