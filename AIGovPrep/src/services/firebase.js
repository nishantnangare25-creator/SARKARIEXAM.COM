/**
 * MOCK FIREBASE BACKEND (Local Storage)
 * 
 * Yeh file Firebase ki local mock copy hai taaki aap bina Firebase setup kiye
 * login, dashboard aur tests chala sakein. Original code "firebase-real.js" me saved hai.
 */

const delay = (ms) => new Promise((res) => setTimeout(res, ms));
const generateId = () => Math.random().toString(36).substr(2, 9);

export const auth = { currentUser: null };
export const db = {};
export const storage = {};

const getLocal = (key, defaultVal) => JSON.parse(localStorage.getItem(key)) || defaultVal;
const setLocal = (key, val) => localStorage.setItem(key, JSON.stringify(val));

// ===== AUTH =====
let authChangeCallback = null;

const notifyAuth = (user) => {
  auth.currentUser = user;
  if (authChangeCallback) authChangeCallback(user);
};

export const loginWithEmail = async (email, password) => {
  await delay(800);
  if (!email || !password) throw new Error('Email or Password missing.');
  const user = { uid: email, email, displayName: email.split('@')[0] };
  setLocal('currentUser', user);
  notifyAuth(user);
  return { user };
};

export const loginWithGoogle = async () => {
  await delay(800);
  const user = { uid: 'google_user', email: 'student@google.com', displayName: 'Google Student' };
  setLocal('currentUser', user);
  notifyAuth(user);
  return { user };
};

export const logout = async () => {
  await delay(400);
  localStorage.removeItem('currentUser');
  notifyAuth(null);
};

export const onAuthChange = (callback) => {
  authChangeCallback = callback;
  const user = getLocal('currentUser', null);
  auth.currentUser = user;
  setTimeout(() => callback(user), 100);
  return () => { authChangeCallback = null; };
};

// ===== USER PROFILE =====
export const saveUserProfile = async (uid, data) => {
  await delay(500);
  const profiles = getLocal('mockProfiles', {});
  profiles[uid] = { ...profiles[uid], ...data, updatedAt: new Date().toISOString() };
  setLocal('mockProfiles', profiles);
};

export const getUserProfile = async (uid) => {
  await delay(300);
  const profiles = getLocal('mockProfiles', {});
  return profiles[uid] || null;
};

// ===== STUDY PLANS =====
export const saveStudyPlan = async (uid, plan) => {
  await delay(500);
  const plans = getLocal('mockStudyPlans', {});
  plans[uid] = { plan, updatedAt: new Date().toISOString() };
  setLocal('mockStudyPlans', plans);
};

export const getStudyPlan = async (uid) => {
  await delay(300);
  const plans = getLocal('mockStudyPlans', {});
  return plans[uid] || null;
};

// ===== TEST HISTORY =====
export const saveTestResult = async (uid, result) => {
  await delay(500);
  const results = getLocal('mockTestResults', []);
  const newResult = { id: generateId(), uid, ...result, createdAt: new Date().toISOString() };
  results.push(newResult);
  setLocal('mockTestResults', results);
  return newResult;
};

export const getTestHistory = async (uid) => {
  await delay(400);
  const results = getLocal('mockTestResults', []);
  return results.filter(r => r.uid === uid).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
};

// ===== FORUM =====
export const createForumPost = async (data) => {
  await delay(500);
  const posts = getLocal('mockForumPosts', []);
  const newPost = { id: generateId(), ...data, createdAt: new Date().toISOString(), replies: [] };
  posts.push(newPost);
  setLocal('mockForumPosts', posts);
  return newPost;
};

export const getForumPosts = async (category = null) => {
  await delay(400);
  let posts = getLocal('mockForumPosts', []);
  if (category) posts = posts.filter(p => p.category === category);
  return posts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
};

export const addReplyToPost = async (postId, reply) => {
  await delay(500);
  const posts = getLocal('mockForumPosts', []);
  const post = posts.find(p => p.id === postId);
  if (post) {
    post.replies = post.replies || [];
    post.replies.push({ ...reply, createdAt: new Date().toISOString() });
    setLocal('mockForumPosts', posts);
  }
};

// ===== STORAGE (PDF Upload) =====
export const uploadFile = async (path, file) => {
  await delay(1000);
  return `https://mock-storage.local/${path.replace(/[^a-z0-9]/gi, '_')}`;
};

// ===== PEER MATCHING =====
export const getAllUsers = async () => {
  await delay(400);
  const profiles = getLocal('mockProfiles', {});
  return Object.entries(profiles).map(([id, data]) => ({ id, ...data }));
};

export default { app: 'mock-app' };
