# sarkariexamai.com UI Style Guide & Analysis

This document provides a comprehensive breakdown of the user interface for **sarkariexamai.com**. It includes technical details on color palettes, typography, spacing, and structural components.

---

## 🎨 Color Palette

The website uses a modern, high-contrast palette with deep blues, vibrant gradients, and clean neutrals to convey trust and intelligence (AI focus).

### Brand Colors
| Role | Color / Gradient | Hex Code |
| :--- | :--- | :--- |
| **Primary Blue** | Standard Brand Blue | `#1a56db` / `#3b82f6` |
| **Primary Text** | Slate Dark Navy | `#111827` |
| **Secondary Text** | Slate Grey | `#475569` |
| **Background** | Soft Off-White | `#f8fafd` |
| **Accent (AI)** | Blue-to-Purple Gradient | `linear-gradient(135deg, #6366f1, #a855f7)` |
| **Accent (Exam)** | Orange/Gold | `#ea580c` |

### Gradients
- **Primary Buttons:** `linear-gradient(135deg, #1a56db 0%, #3b82f6 100%)`
- **Hero CTA:** `linear-gradient(135deg, #2563eb, #4f46e5)`
- **Hero Background:** Features a subtle `1px` grid pattern over a light grey background (`#f1f5f9`).

---

## 🔡 Typography

A dual-font system is employed to balance readability and modern aesthetics.

- **Headings (H1, H2, H3):** **Outfit** (Sans-serif)
    - **H1 (Hero):** `36px` (`2.25rem`), Weight: `800` (ExtraBold)
    - **H2 (Section Titles):** `28px` (`1.75rem`), Weight: `700` (Bold)
- **Body & Navigation:** **Inter** (Sans-serif)
    - **Standard Body:** `16px`, Line-height: `1.6`
    - **Footer Links:** `14.4px` (`0.9rem`)

---

## 📐 Layout & Dimensions

### Section Breakdown
1. **Header (Sticky):**
    - **Height:** `72px`
    - **Elements:** Logo (Left), Nav Links (Center), Login/Signup (Right).
    - **Padding:** `0 24px`
2. **Hero Section:**
    - **Vertical Padding:** `64px` (Top/Bottom)
    - **Structural Pattern:** Square grid background overlay.
    - **Floating Badges:** "AI Analyzing...", "Focus Area: Indian Polity" with `box-shadow` and `border-radius: 9999px` (Pill).
3. **Features Grid (Master Every Subject):**
    - **Layout:** 3-column responsive grid.
    - **Card Padding:** `32px`
    - **Card Border-Radius:** `20px`
    - **Card Border:** `1px solid #e5e7eb`
4. **Exams Library Section:**
    - **Layout:** 6-column icon grid.
    - **Element:** Icons in colored squares with rounded corners.
5. **CTA Blue Block:**
    - **Background:** Solid Royal Blue to Indigo Gradient.
    - **Padding:** `80px 24px`

---

## 🦶 Footer Analysis

The footer is designed for high information density with a dark navy background.

- **Background Color:** `#0b1120` (Dark Navy)
- **Top Padding:** `64px`
- **Bottom Padding:** `32px`
- **Link Hierarchy:**
    - **Column Headers:** White, Uppercase, Medium Weight.
    - **Links:** `#94a3b8` (Slate-400), Hover: White.
- **Columns:** 4-column layout (Platform, Exams, Company, Logo/Intro).
- **Copyright Bar:** Separated by a thin border-top (`#1e293b`).

---

## 🧩 Specific UI Elements

### Brackets & Structural Text
The site uses parentheses/brackets to clarify exam exam types in the "Exams We Cover" section:
- `Banking (IBPS/SBI)`
- `Railway (RRB)`
- `SSC CGL/CHSL`
- This consistency helps users find specific sub-exams quickly.

### Interactive Elements
- **Pill Badges:** Used for highlights like "AI-POWERED EXAM SUCCESS".
- **Buttons:** 
    - **Hero:** Pill-shaped (`border-radius: 9999px`) with large shadows.
    - **Standard:** Rounded-rectangle (`border-radius: 12px`).
- **Icons:** Minimalist line icons housed within light-colored background containers (Soft Purple, Teal, Orange).

---

## 🖼️ Full Page Capture

See the full visual breakdown below:

![Full Page UI Breakdown](file:///C:/Users/nishant/.gemini/antigravity/brain/302a2745-038f-4c3e-bedb-1c13036f443e/full_page_view_1776314967099.png)

---
