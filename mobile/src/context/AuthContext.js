import React, { createContext, useContext, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { auth, user as userApi, setOnAuthError } from '../api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser]                     = useState(null);
  const [loading, setLoading]               = useState(true);
  const [needsQuestionnaire, setNeedsQ]     = useState(false);

  useEffect(() => { restoreSession(); }, []);

  useEffect(() => {
    setOnAuthError(async () => {
      await AsyncStorage.multiRemove(['accessToken', 'refreshToken']);
      setUser(null);
      setNeedsQ(false);
    });
    return () => setOnAuthError(null);
  }, []);

  async function checkQuestionnaire() {
    try {
      await userApi.getQuestionnaire();
      setNeedsQ(false);
    } catch (e) {
      setNeedsQ(e.status === 404);
    }
  }

  async function restoreSession() {
    try {
      const token = await AsyncStorage.getItem('accessToken');
      if (token) {
        const me = await auth.me();
        setUser(me);
        await checkQuestionnaire();
      }
    } catch {
      await AsyncStorage.multiRemove(['accessToken', 'refreshToken']);
    } finally {
      setLoading(false);
    }
  }

  async function login(email, password) {
    const data = await auth.login(email, password);
    await AsyncStorage.setItem('accessToken', data.accessToken);
    await AsyncStorage.setItem('refreshToken', data.refreshToken);
    const me = await auth.me();
    setUser(me);
    await checkQuestionnaire();
  }

  async function register(email, password) {
    const data = await auth.register(email, password);
    await AsyncStorage.setItem('accessToken', data.accessToken);
    await AsyncStorage.setItem('refreshToken', data.refreshToken);
    const me = await auth.me();
    setUser(me);
    setNeedsQ(true);
  }

  async function completeQuestionnaire() {
    setNeedsQ(false);
  }

  async function logout() {
    try {
      const refreshToken = await AsyncStorage.getItem('refreshToken');
      if (refreshToken) await auth.logout(refreshToken);
    } finally {
      await AsyncStorage.multiRemove(['accessToken', 'refreshToken']);
      setUser(null);
      setNeedsQ(false);
    }
  }

  return (
    <AuthContext.Provider value={{ user, loading, needsQuestionnaire, login, register, logout, completeQuestionnaire }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
