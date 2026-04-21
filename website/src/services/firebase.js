import { initializeApp } from 'firebase/app';
import { getAnalytics } from "firebase/analytics";
import {
  getAuth,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  GoogleAuthProvider,
  signInWithPopup,
  signInWithRedirect,
  getRedirectResult,
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

// Firebase config — using environment variables from .env
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "AIzaSyCWoAYg_1WQPABOS8WzFxoQCcgDY5Rgyzc",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "govai-7ee5b.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "govai-7ee5b",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "govai-7ee5b.firebasestorage.app",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "868025142353",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:868025142353:web:d7687cdd6c8bd19c32fc70",
  measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID || "G-H4LJTCNN2V",
};

const app = initializeApp(firebaseConfig);
export const analytics = typeof window !== 'undefined' ? getAnalytics(app) : null;
console.log("Firebase initialized with project:", firebaseConfig.projectId);
export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);

const googleProvider = new GoogleAuthProvider();
googleProvider.setCustomParameters({ prompt: 'select_account' });

// ===== AUTH =====
export const loginWithEmail = (email, password) =>
  signInWithEmailAndPassword(auth, email, password);

export const registerWithEmail = async (email, password) => {
  const userCredential = await createUserWithEmailAndPassword(auth, email, password);
  const user = userCredential.user;
  // Initialize default profile
  await saveUserProfile(user.uid, {
    email: user.email,
    displayName: user.email.split('@')[0],
    createdAt: new Date().toISOString()
  });
  return userCredential;
};

export const loginWithGoogle = async () => {
  const result = await signInWithPopup(auth, googleProvider);
  const user = result.user;
  
  // Create profile if it doesn't exist
  const existingProfile = await getUserProfile(user.uid);
  if (!existingProfile) {
    await saveUserProfile(user.uid, {
      displayName: user.displayName || 'Student',
      email: user.email,
      photoURL: user.photoURL,
      createdAt: new Date().toISOString()
    });
  }
  return result;
};

// Google Sign-in via Redirect (for WebView / mobile browsers where popup is blocked)
export const loginWithGoogleRedirect = async () => {
  await signInWithRedirect(auth, googleProvider);
  // After redirect returns to the page, getRedirectResult handles it automatically
};

export const handleGoogleRedirectResult = async () => {
  try {
    const result = await getRedirectResult(auth);
    if (result) {
      const user = result.user;
      const existingProfile = await getUserProfile(user.uid);
      if (!existingProfile) {
        await saveUserProfile(user.uid, {
          displayName: user.displayName || 'Student',
          email: user.email,
          photoURL: user.photoURL,
          createdAt: new Date().toISOString()
        });
      }
      return result;
    }
  } catch (e) {
    console.error('Redirect result error:', e);
  }
  return null;
};

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
