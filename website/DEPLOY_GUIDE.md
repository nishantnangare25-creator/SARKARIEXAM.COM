# 🚀 Deploy Karne ka Step-by-Step Guide

## Part 1: Cloudflare Worker Deploy karo (Backend - FREE)

### Step 1: Accounts banao (agar nahi hai)
- **Cloudflare:** https://cloudflare.com → Free account
- **Gemini API key:** https://aistudio.google.com → "Get API Key" → FREE milegi

### Step 2: Wrangler CLI install karo
```bash
npm install -g wrangler
```

### Step 3: Cloudflare mein login karo
```bash
wrangler login
```
(Browser khulega → Allow karo)

### Step 4: Worker folder mein jao
```bash
cd cloudflare-worker
```

### Step 5: API Keys secret ke roop mein add karo
```bash
wrangler secret put GROQ_API_KEY
# Paste: YOUR_GROQ_API_KEY_HERE

wrangler secret put GEMINI_API_KEY
# Paste: YOUR_GEMINI_API_KEY_HERE

wrangler secret put OPENROUTER_API_KEY
# Paste: YOUR_OPENROUTER_API_KEY_HERE
```

### Step 6: Worker deploy karo
```bash
wrangler deploy
```
Output mein URL aayega jaise:
`https://sarkari-exam-ai.YOUR_NAME.workers.dev`

---

## Part 2: Frontend ko Worker se connect karo

### Step 7: `.env` file update karo
`.env` file mein yeh line uncomment karo aur URL daalo:
```
VITE_WORKER_URL=https://sarkari-exam-ai.YOUR_NAME.workers.dev
```

---

## Part 3: Firebase Hosting pe deploy karo (FREE)

### Step 8: Firebase CLI install karo
```bash
npm install -g firebase-tools
```

### Step 9: Firebase login karo
```bash
firebase login
```

### Step 10: Project build karo
```bash
npm run build
```

### Step 11: Firebase initialize karo (sirf pehli baar)
```bash
firebase init hosting
```
- Select existing project: `govai-7ee5b`
- Public directory: `dist`
- Single-page app: `Yes`
- Overwrite index.html: `No`

### Step 12: Deploy karo!
```bash
firebase deploy --only hosting
```
Output mein live URL milega jaise:
`https://govai-7ee5b.web.app` ✅

---

## Part 4: MilesWeb (Shared Hosting) pe deploy karo (PH)

Agar aap MilesWeb ya kisi bhi Shared Hosting (cPanel) ka use kar rahe hain:

### Step 13: Project ko build karo
```bash
npm run build
```
Yeh command `dist` naam ka ek folder banayegi.

### Step 14: Files upload karo
1.  **cPanel mein login karo** (Usually: yourdomain.com/cpanel).
2.  **File Manager** mein jao.
3.  **public_html** (root) folder ko open karo.
4.  Apne computer se `dist` folder ke andar ki **SARI FILES** (index.html, assets, .htaccess, etc.) ko `public_html` mein upload kar do.
    - *Note:* `dist` folder ko mat upload karna, uske ANDAR ka content upload karna.

### Step 15: .htaccess check karo (Zaroori!)
React router ke liye `.htaccess` file ka hona zaroori hai taaki "Page Not Found" error na aaye refresh karne par.
Agar `public/.htaccess` file pehle se hai, toh check karo ki woh `public_html` mein ho.

### Error Solutions:
- **White Screen:** Check karo ki `index.html` mein assets ke paths sahi hain (Usually relative paths `/assets/` kaam karte hain).
- **404 on Refresh:** `.htaccess` file ko confirm karo root folder mein.

✅ **Done! Aapki website ab MilesWeb pe live hai.**
