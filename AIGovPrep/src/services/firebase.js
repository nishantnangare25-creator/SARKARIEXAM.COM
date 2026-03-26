import { initializeApp } from 'firebase/app';
import {
  getAuth,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  GoogleAuthProvider,
  signInWithPopup,
  onAuthStateChanged,
  updateProfile,
} from 'firebase/auth';
import {
  getFirestore,
  doc,
  setDoc,
  getDoc,
  getDocs,
  updateDoc,
  deleteDoc,
  collection,
  query,
  where,
  orderBy,
  limit,
  addDoc,
  serverTimestamp,
} from 'firebase/firestore';
import { getStorage, ref, uploadBytes, getDownloadURL } from 'firebase/storage';

// Firebase config — replace with your project credentials
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "YOUR_API_KEY",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "YOUR_PROJECT.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "YOUR_PROJECT_ID",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "YOUR_PROJECT.appspot.com",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "000000000000",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:000000000000:web:0000000000000000000000",
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);

const googleProvider = new GoogleAuthProvider();

// ===== AUTH =====
export const loginWithEmail = (email, password) =>
  signInWithEmailAndPassword(auth, email, password);

export const loginWithGoogle = () => signInWithPopup(auth, googleProvider);

export const logout = () => signOut(auth);

export const onAuthChange = (callback) => onAuthStateChanged(auth, callback);

// ===== USER PROFILE =====
export const saveUserProfile = async (uid, data) => {
  await setDoc(doc(db, 'users', uid), { ...data, updatedAt: serverTimestamp() }, { merge: true });
};

export const getUserProfile = async (uid) => {
  const snap = await getDoc(doc(db, 'users', uid));
  return snap.exists() ? snap.data() : null;
};

// ===== STUDY PLANS =====
export const saveStudyPlan = async (uid, plan) => {
  await setDoc(doc(db, 'studyPlans', uid), { plan, updatedAt: serverTimestamp() }, { merge: true });
};

export const getStudyPlan = async (uid) => {
  const snap = await getDoc(doc(db, 'studyPlans', uid));
  return snap.exists() ? snap.data() : null;
};

// ===== TEST HISTORY =====
export const saveTestResult = async (uid, result) => {
  return addDoc(collection(db, 'testResults'), {
    uid,
    ...result,
    createdAt: serverTimestamp(),
  });
};

export const getTestHistory = async (uid) => {
  const q = query(
    collection(db, 'testResults'),
    where('uid', '==', uid),
    orderBy('createdAt', 'desc'),
    limit(50)
  );
  const snap = await getDocs(q);
  return snap.docs.map(d => ({ id: d.id, ...d.data() }));
};

// ===== FORUM =====
export const createForumPost = async (data) => {
  return addDoc(collection(db, 'forumPosts'), {
    ...data,
    createdAt: serverTimestamp(),
    replies: [],
  });
};

export const getForumPosts = async (category = null) => {
  let q;
  if (category) {
    q = query(collection(db, 'forumPosts'), where('category', '==', category), orderBy('createdAt', 'desc'), limit(50));
  } else {
    q = query(collection(db, 'forumPosts'), orderBy('createdAt', 'desc'), limit(50));
  }
  const snap = await getDocs(q);
  return snap.docs.map(d => ({ id: d.id, ...d.data() }));
};

export const addReplyToPost = async (postId, reply) => {
  const postRef = doc(db, 'forumPosts', postId);
  const postSnap = await getDoc(postRef);
  if (postSnap.exists()) {
    const replies = postSnap.data().replies || [];
    replies.push({ ...reply, createdAt: new Date().toISOString() });
    await updateDoc(postRef, { replies });
  }
};

// ===== STORAGE (PDF Upload) =====
export const uploadFile = async (path, file) => {
  const storageRef = ref(storage, path);
  await uploadBytes(storageRef, file);
  return getDownloadURL(storageRef);
};

// ===== PEER MATCHING =====
export const getAllUsers = async () => {
  const snap = await getDocs(collection(db, 'users'));
  return snap.docs.map(d => ({ id: d.id, ...d.data() }));
};

export default app;
