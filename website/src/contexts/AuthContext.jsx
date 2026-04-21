import { createContext, useContext, useState, useEffect } from 'react';
import { onAuthChange, getUserProfile, handleGoogleRedirectResult } from '../services/firebase';
import i18n from '../i18n';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Attempt to handle Google Redirect Sign-In (for Flutter WebViews)
    handleGoogleRedirectResult().catch(err => console.error('Redirect handler error:', err));

    const unsubscribe = onAuthChange(async (firebaseUser) => {
      setUser(firebaseUser);
      if (firebaseUser) {
        try {
          const p = await getUserProfile(firebaseUser.uid);
          setProfile(p);
          if (p?.language) {
            i18n.changeLanguage(p.language);
          }
        } catch (e) {
          console.error('Error loading profile:', e);
        }
      } else {
        setProfile(null);
      }
      setLoading(false);
    });
    return () => unsubscribe();
  }, []);

  return (
    <AuthContext.Provider value={{ user, profile, setProfile, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
